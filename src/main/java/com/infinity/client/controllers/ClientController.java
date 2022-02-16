package com.infinity.client.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.client.models.ConnectMessModel;
import com.infinity.client.models.ReceivedFileModel;
import com.infinity.client.models.UpdateMessModel;
import com.infinity.client.models.SharedFileModel;

public class ClientController {
	/**
	 * The ip address of the server.
	 */
	private String ipAddress;
	
	/**
	 * The nick name of the user.
	 */
	private String username;
	
	/**
	 * The socket used for communicating with server.
	 */
	private Socket socket;
	
	/**
	 * The reader used for reading input stream. 
	 */
	private BufferedReader inputStreamReader;
	
	/**
	 * The writer used for writing output stream.
	 */
	private PrintWriter outputStreamWriter;
	
	/**
	 * The port of server.
	 */
	private static final int PORT = 7777;
	
	/**
	 * The unique instance of client.
	 */
	public static final ClientController INSTANCE = new ClientController();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ClientController.class);
	
	private ClientController() { }
	
	public static ClientController getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Connect to server.
	 * @param ipAddress the IP address of the server.
	 * @param username  the nick name of the user
	 * @throws Exception 
	 */
	public void connect(String ipAddress, String username, int commandPort, String sharedFolderName) 
			throws Exception {
		this.ipAddress = ipAddress;
		this.username = username;
		this.socket = new Socket(ipAddress, PORT);
		this.inputStreamReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
		this.outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
		
		socket.setSoTimeout(5000);
		
		ConnectMessModel connectMess = new ConnectMessModel();
        connectMess.setStatus("CONNECT");
        connectMess.setUsername(username);
        connectMess.setCommandPort(commandPort);
        
        FileServerController.clearOldData();
        
        List<SharedFileModel> currentFilesInSharedFolder = FileServerController.getFilesInSharedFolder(sharedFolderName);
        for (SharedFileModel element : currentFilesInSharedFolder) {
        	// Create request message
            connectMess.addSharedFile(element);
            
            // Initial list shared files
            FileServerController.shareNewFile(element);
        }
        
        String reqMess = JSON.toJSONString(connectMess);
        
		// Send connect message to server
		outputStreamWriter.println(reqMess);
		LOGGER.debug("Send connection request to server: \n\t" + reqMess);
		
		
		// Receive ACK from server
		String ackMessage = inputStreamReader.readLine();
		
		JSONObject ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
		LOGGER.debug("[ConnectionReq] Received message from server: \n\t" + ackMessJsonObject.toJSONString());
		
		if ( ackMessJsonObject.getString("message").equals("ACCEPT") ) {
			LOGGER.info("Connected to server.");
		} else {
			LOGGER.warn("Server closed socket for unknown reason.");
			throw new Exception("Server closed socket for unknown reason.");
		}
	}
	
	/**
	 * Search from shared files right now from server.
	 * @return the list of shared files
	 */
	public List<ReceivedFileModel> searchFiles(String fileName) {
		
		// Send Search command to server for querying shared files
		
		JSONObject searchObj = new JSONObject();
		searchObj.put("status", "SEARCH");
		searchObj.put("payload", fileName);
		
		String searchMess = searchObj.toJSONString();
		
		outputStreamWriter.println(searchMess);
		LOGGER.debug("Send search file request to server: \n\t" + searchMess);

		// Receive response from server
		List<ReceivedFileModel> receivedFiles = new ArrayList<>();
		
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[SearchFile] Received message from server: \n\t" + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));

			if ( !responseInJson.getString("message").equals("ERROR") ) {
				receivedFiles = JSON.parseArray(responseInJson.getString("payload"), ReceivedFileModel.class);
			}
			
		} catch ( IOException e ) {
			LOGGER.catching(e);
		}
		
		return receivedFiles;
	}
	
	/**
	 * Share new file to server.
	 * @param sharedFiles list of file to share
	 * @return whether the share operation is successful
	 */
	public boolean shareFile(List<SharedFileModel> sharedFiles) {
		
		UpdateMessModel publishMessObj = new UpdateMessModel();
		publishMessObj.setStatus("PUBLISH");
		publishMessObj.setPayload(sharedFiles);
		
		String publishMess = JSON.toJSONString(publishMessObj);
		
		// Send publish command to server
		outputStreamWriter.println(publishMess);
		LOGGER.debug("Send share file request to server: \n\t" + publishMess);

		// Receive response from server
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[PublishFile] Received message from server: \n\t" + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));

			if ( responseInJson.getString("message").equals("SUCCESS") ) {
				return true;
			}
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		}
		return false;
	}
	
	/**
	 * Unpublish file from server.
	 * @param fileName the file name of the file
	 * @param checksum the checksum of the file
	 * @return whether the unpublish operation is successful
	 */
	public boolean unshareFile(List<SharedFileModel> unshareFiles) {
		
		// Send unpublish command to server
		
		UpdateMessModel unpublishMessObj = new UpdateMessModel();
		unpublishMessObj.setStatus("UNPUBLISH");
		unpublishMessObj.setPayload(unshareFiles);
		
		String unpublishMess = JSON.toJSONString(unpublishMessObj);
		
		outputStreamWriter.println(unpublishMess);
		LOGGER.debug("Send unshare file request to server: \n\t" + unpublishMess);

		// Receive response from server
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[UnPublishFile] Received message from server: \n\t" + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));

			if ( responseInJson.getString("message").equals("SUCCESS") ) {
				return true;
			}
		} catch ( IOException e) {
			LOGGER.catching(e);
		}
		return false;
	}
	
	/**
	 * Report to server when cannot download a file.
	 */
	public void reportDownloadErr(String id) {
		
		JSONObject reportErrDownObj = new JSONObject();
		reportErrDownObj.put("status", "ERRDOWNLOAD");
		reportErrDownObj.put("payload", id);
		
		String reportErrDownMess = JSON.toJSONString(reportErrDownObj);
		
		outputStreamWriter.println(reportErrDownMess);
		LOGGER.debug("Send error report to server: \n\t" + reportErrDownMess);
	}
	
	/**
	 * Close socket for client.
	 */
	public void disconnect() {
		
		// Say goodbye to server
		
		JSONObject quitObj = new JSONObject();
		quitObj.put("status", "QUIT");
		
		String quitMess = quitObj.toJSONString();
		
		outputStreamWriter.println(quitMess);
		LOGGER.debug("Send quit request to server: \n\t" + quitMess);
		
		// CLose Socket
		try {
			inputStreamReader.close();
			outputStreamWriter.close();
			socket.close();
			
			LOGGER.info("Disconnected from server.");
		} catch (IOException e) {
			LOGGER.catching(e);
		}
	}
	
	
}

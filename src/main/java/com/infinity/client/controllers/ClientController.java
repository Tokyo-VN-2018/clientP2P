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
import com.infinity.client.models.ErrorReportModel;
import com.infinity.client.models.GetSharerInfoMessModel;
import com.infinity.client.models.ShareFileMessModel;
import com.infinity.client.models.SharedFileModel;
import com.infinity.client.models.UnShareFileMessModel;

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
        
        List<SharedFileModel> currentFilesInSharedFolder = FileServerController.getFilesInSharedFolder(sharedFolderName);
        for (SharedFileModel element : currentFilesInSharedFolder) {
        	// Create request message
            element.setSharer(username);
            connectMess.addSharedFile(element);
            
            // Initial list shared files
            FileServerController.shareNewFile(element.getChecksum(), element);
        }
        
        String reqMess = JSON.toJSONString(connectMess);
        
		// Send connect message to server
		outputStreamWriter.println(reqMess);
		LOGGER.info("Send connection request to server: " + reqMess);
		
		
		// Receive ACK from server
		String ackMessage = inputStreamReader.readLine();
		
		JSONObject ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
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
	public List<SharedFileModel> searchFiles(String fileName) {
		
		// Send Search command to server for querying shared files
		
		JSONObject searchObj = new JSONObject();
		searchObj.put("status", "SEARCH");
		searchObj.put("payload", fileName);
		
		String searchMess = searchObj.toJSONString();
		
		outputStreamWriter.println(searchMess);
		LOGGER.info("Send search file request to server: " + searchMess);

		// Receive response from server
		List<SharedFileModel> sharedFiles = new ArrayList<>();
		
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[SearchFile] Received message from server: " + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));

			if ( !responseInJson.getString("message").equals("ERROR") ) {
				sharedFiles = JSON.parseArray(responseInJson.getString("payload"), SharedFileModel.class);
			}
			
		} catch ( IOException e ) {
			LOGGER.catching(e);
		}
		
		return sharedFiles;
	}
	
	/**
	 * Share new file to server.
	 * @param sharedFiles list of file to share
	 * @return whether the share operation is successful
	 */
	public boolean shareFile(List<SharedFileModel> sharedFiles) {
		
		ShareFileMessModel publishMessObj = new ShareFileMessModel();
		publishMessObj.setStatus("PUBLISH");
		publishMessObj.setPayload(sharedFiles);
		
		String publishMess = JSON.toJSONString(publishMessObj);
		
		// Send publish command to server
		outputStreamWriter.println(publishMess);
		LOGGER.info("Send share file request to server: " + publishMess);

		// Receive response from server
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[PublishFile] Received message from server: " + response);
			
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
		
		UnShareFileMessModel unpublishMessObj = new UnShareFileMessModel();
		unpublishMessObj.setStatus("UNPUBLISH");
		unpublishMessObj.setPayload(unshareFiles);
		
		String unpublishMess = JSON.toJSONString(unpublishMessObj);
		
		outputStreamWriter.println(unpublishMess);
		LOGGER.info("Send unshare file request to server: " + unpublishMess);

		// Receive response from server
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[UnPublishFile] Received message from server: " + response);
			
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
	 * Get the IP of the sharer who share a specific file
	 * @param checksum the checksum of the file
	 * @return the IP of the sharer or N/a if the file is not available
	 */
	public Object[] getFileSharerInfo(SharedFileModel sharedFile) {
		
		// Send share command to server
		
		GetSharerInfoMessModel requestIPMessObj = new GetSharerInfoMessModel();
		requestIPMessObj.setStatus("INFOREQUEST");
		requestIPMessObj.setPayload(sharedFile);
		
		String requestIPMess = JSON.toJSONString(requestIPMessObj);
		
		outputStreamWriter.println(requestIPMess);
		LOGGER.info("Send get file sharer info request to server: " + requestIPMess);

		// Receive response from server
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[RequestSharerInfo] Received message from server: " + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));
			JSONObject payloadObject = responseInJson.getJSONObject("payload");
			
			String IP = payloadObject.getString("ip");
			Integer commandPort = payloadObject.getInteger("commandPort");

			if ( !IP.equals("N/a") && commandPort != -1) {
				return new Object[] {IP, commandPort};
			}
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		}
		return null;
	}
	
	/**
	 * Report to server when cannot download a file.
	 */
	public void reportDownloadErr(SharedFileModel sharedFile) {
		ErrorReportModel errReportMessObj = new ErrorReportModel();
		errReportMessObj.setStatus("ERRDOWNLOAD");
		errReportMessObj.setPayload(sharedFile);
		
		String errReportMess = JSON.toJSONString(errReportMessObj);
		
		outputStreamWriter.println(errReportMess);
		LOGGER.info("Send error report to server: " + errReportMess);
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

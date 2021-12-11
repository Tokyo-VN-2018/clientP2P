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
import com.infinity.client.models.GetSharerIPMessModel;
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
	public void connect(String ipAddress, String username) 
			throws Exception {
		this.ipAddress = ipAddress;
		this.username = username;
		this.socket = new Socket(ipAddress, PORT);
		this.inputStreamReader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
		this.outputStreamWriter = new PrintWriter(socket.getOutputStream(), true);
		
		ConnectMessModel connectMess = new ConnectMessModel();
        connectMess.setStatus("CONNECT");
        connectMess.setUsername(username);
        
        List<SharedFileModel> currentFilesInSharedFolder = FileServerController.getFilesInSharedFolder();
        for (SharedFileModel element : currentFilesInSharedFolder) {
            element.setSharer(username);
            connectMess.addSharedFile(element);
        }
        
        String reqMess = JSON.toJSONString(connectMess);
        
		// Send connect message to server
		outputStreamWriter.println(reqMess);
		
		// Receive ACK from server
		String ackMessage = inputStreamReader.readLine();
		JSONObject ackMessJsonObject = (JSONObject) JSON.parse(ackMessage);
		JSONObject messObject = ackMessJsonObject.getJSONObject("payload");
		if ( messObject.getString("message").equals("ACCEPT") ) {
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
	public String getFileSharerIp(SharedFileModel sharedFile) {
		
		// Send share command to server
		
		GetSharerIPMessModel requestIPMessObj = new GetSharerIPMessModel();
		requestIPMessObj.setStatus("IPREQUEST");
		requestIPMessObj.setPayload(sharedFile);
		
		String requestIPMess = JSON.toJSONString(requestIPMessObj, true);
		
		outputStreamWriter.println(requestIPMess);

		// Receive response from server
		try {
			String response = inputStreamReader.readLine();
			LOGGER.debug("[RequestSharerIP] Received message from server: " + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));
			String IP = responseInJson.getString("message");

			if ( !IP.equals("N/a") ) {
				return IP;
			}
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		}
		return "N/a";
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

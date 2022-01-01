package com.infinity.client.controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.client.models.CheckFileMessModel;
import com.infinity.client.models.SharedFileModel;

public class FileReceiverController {

	/**
	 * The port used for receiving file stream.
	 */
	private static int FILE_STREAM_PORT;
	
	public static int getFILE_STREAM_PORT() {
		return FILE_STREAM_PORT;
	}

	public static void setFILE_STREAM_PORT(int fILE_STREAM_PORT) {
		FILE_STREAM_PORT = fILE_STREAM_PORT;
	}

	/**
	 * The buffer size of the file stream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * The unique instance of FileReceiver.
	 */
	private static final FileReceiverController INSTANCE = new FileReceiverController();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileReceiverController.class);
	
	private FileReceiverController() { }

	public static FileReceiverController getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Receive file stream.
	 * @param file	The file want to download
	 * @param filePath  the file path to save the file
	 * @param ipAddress the IP address of the sender
	 * @throws Exception
	 */
	public void receiveFile(SharedFileModel file, String filePath, String ipAddress, int HOST_COMMAND_PORT) throws Exception {
		DatagramSocket commandSocket = null;
		ServerSocket fileStreamListener = null;
		Socket fileStreamSocket = null;
		DataInputStream fileInputStream = null;
		DataOutputStream fileOutputStream = null;

		try {
			
//			JSONObject checkMessObj = new JSONObject();
//			checkMessObj.put("status", "CHECK");
//			checkMessObj.put("listeningPort", FILE_STREAM_PORT);
//			checkMessObj.put("checksum", checksum);
			
			CheckFileMessModel checkMessObj = new CheckFileMessModel();
			checkMessObj.setStatus("CHECK");
			checkMessObj.setListeningPort(FILE_STREAM_PORT);
			checkMessObj.setPayload(file);
			
			String checkMess = JSON.toJSONString(checkMessObj);
			
			// Send command for requesting files
			commandSocket = new DatagramSocket();
			byte[] inputDataBuffer = new byte[BUFFER_SIZE];
			byte[] outputDataBuffer = checkMess.getBytes();
			
			DatagramPacket outputPacket = new DatagramPacket(outputDataBuffer,
					outputDataBuffer.length, InetAddress.getByName(ipAddress), HOST_COMMAND_PORT);
			commandSocket.send(outputPacket);

			DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
			commandSocket.receive(inputPacket);
			String response = new String(inputPacket.getData());
			
			LOGGER.debug("[Check] Received message from file owner: " + response);
			
			JSONObject responseInJson = (JSONObject)(JSON.parse(response));

			if ( !responseInJson.getString("message").equals("ACCEPT") ) {
				throw new Exception("The sharer refused to send this file.");
			}

			// Opening port for receiving file stream
			fileStreamListener = new ServerSocket(FILE_STREAM_PORT);
			fileStreamSocket = fileStreamListener.accept();

			// Receiving Data Stream
			fileInputStream = new DataInputStream(new BufferedInputStream(fileStreamSocket.getInputStream()));
			fileOutputStream = new DataOutputStream(new BufferedOutputStream(new BufferedOutputStream(new FileOutputStream(filePath))));
			byte[] fileBuffer = new byte[BUFFER_SIZE];
			
			while ( true ) {
				if ( fileInputStream == null ) {
					return;
				}
				int bytesRead = fileInputStream.read(fileBuffer);

				if ( bytesRead == -1 ) {
					break;
				}
				fileOutputStream.write(fileBuffer, 0, bytesRead);
			}
			fileOutputStream.flush();
		} finally {
			try {
				if ( commandSocket != null ) {
					commandSocket.close();
				}
				if ( fileInputStream != null ) {
					fileInputStream.close();
				}
				if ( fileOutputStream != null ) {
					fileOutputStream.close();
				}
				if ( fileStreamSocket != null ) {
					fileStreamSocket.close();
				}
				if ( fileStreamListener != null ) {
					fileStreamListener.close();
				}
			} catch ( IOException e ) {
				LOGGER.catching(e);
			}
		}
	}
}

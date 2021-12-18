package com.infinity.client.controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.client.models.SharedFileModel;

public class FileServerController {
	/**
	 * The map is used for storing shared files.
	 *
	 * The key stands for the checksum of the file.
	 * The value stands for the absolute path of the file.
	 */
	private static Map<Long, SharedFileModel> sharedFiles = new Hashtable<Long, SharedFileModel>();

	public static Map<Long, SharedFileModel> getSharedFiles() {
		return sharedFiles;
	}

	/**
	 * The server socket used for receiving commands.
	 */
	private ServerSocket commandListener;

	/**
	 * The port used for receiving commands.
	 */
	private static int COMMAND_PORT;

	public static int getCOMMAND_PORT() {
		return COMMAND_PORT;
	}

	public static void setCOMMAND_PORT(int cOMMAND_PORT) {
		COMMAND_PORT = cOMMAND_PORT;
	}

	/**
	 * The buffer size of the file stream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * The unique instance of client.
	 */
	public static final FileServerController INSTANCE = new FileServerController();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileServerController.class);
	
	private FileServerController() { }

	public static FileServerController getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Receive the message from client.
	 * @throws IOException
	 */
	public void accept() throws IOException {
		Runnable commandListenerTask = () -> {
			DatagramSocket commandSocket = null;
			try {
				commandSocket = new DatagramSocket(COMMAND_PORT);
				byte[] inputDataBuffer = new byte[BUFFER_SIZE];
				byte[] outputDataBuffer = new byte[BUFFER_SIZE];

				while ( true ) {
					DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
					commandSocket.receive(inputPacket);
					String request = new String(inputPacket.getData());
					
					LOGGER.debug("Received new message: " + request);
					JSONObject requestInJson = (JSONObject)(JSON.parse(request));

					if ( requestInJson.getString("status").equals("CHECK") ) {
						
						Long checksum = requestInJson.getLong("checksum");
						int requesterListeningPort = requestInJson.getInteger("listeningPort");
						
						String ipAddress = inputPacket.getAddress().toString().substring(1);
						int port = inputPacket.getPort();
						
						JSONObject checkResObj = new JSONObject();
						checkResObj.put("status", "CHECK");
						

						if ( sharedFiles.containsKey(checksum) ) {
							checkResObj.put("message", "ACCEPT");
							String checkRes = checkResObj.toJSONString();
							
							outputDataBuffer = checkRes.getBytes();
							sendDatagramPacket(commandSocket, outputDataBuffer, ipAddress, port);
							Thread.sleep(2000); // Wait for open socket for receiving files
							sendFileStream(checksum, ipAddress, requesterListeningPort);
							
						} else {
							checkResObj.put("message", "ERROR");
							String checkRes = checkResObj.toJSONString();
							
							outputDataBuffer = checkRes.getBytes();
							sendDatagramPacket(commandSocket, outputDataBuffer, ipAddress, port);
						}
					}
				}
			} catch ( Exception ex ) {
				LOGGER.catching(ex);
			} finally {
				if ( commandSocket != null ) {
					commandSocket.close();
				}
			}
		};
		Thread commandListenerThread = new Thread(commandListenerTask);
		commandListenerThread.setDaemon(true);
		commandListenerThread.start();
	}

	private void sendDatagramPacket(DatagramSocket socket, byte[] outputDataBuffer, String ipAddress, int port)
			throws IOException {
		InetAddress inetAddress = InetAddress.getByName(ipAddress);

		DatagramPacket outputPacket = new DatagramPacket(outputDataBuffer, outputDataBuffer.length, inetAddress, port);
		socket.send(outputPacket);
	}
	
	/**
	 * Stop receiving file stream.
	 */
	public void close() {
		closeSocket(commandListener);
	}
	
	/**
	 * Send file stream to the receiver.
	 * @param checksum  the checksum of the file
	 * @param ipAddress the IP address of the receiver
	 */
	private void sendFileStream(Long checksum, String ipAddress, int REQUESTER_PORT) {
		SharedFileModel fileToSend = sharedFiles.get(checksum);
		String filePath = fileToSend.getFilePath();
		Socket socket = null;
		DataInputStream fileInputStream = null;
		DataOutputStream fileOutputStream = null;

		try {
			socket = new Socket(ipAddress, REQUESTER_PORT);
			fileInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			fileOutputStream = new DataOutputStream(socket.getOutputStream());

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
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		} finally {
			// Close Socket and DataStream
			try {
				if ( fileInputStream != null ) {
					fileInputStream.close();
				}
				if ( fileOutputStream != null ) {
					fileOutputStream.close();
				}
				if ( socket != null ) {
					socket.close();
				}
			} catch ( IOException ex ) {
				LOGGER.catching(ex);
			}
		}
	}


	
	/**
	 * Register new file to the file server for sharing.
	 *
	 * @param checksum the checksum of the file
	 * @param filePath the absolute path of the file
	 */
	public static void shareNewFile(Long checksum, SharedFileModel sharedFile) {
		sharedFiles.put(checksum, sharedFile);
	}
	
	/**
	 * Remove a shared file from the file server because it is no longer shared.
	 * @param checksum the checksum of the file
	 */
	public void unshareFile(Long checksum) {
		sharedFiles.remove(checksum);
	}
	
	/**
	 * Check if the shared file requested is available.
	 * @param checksum - the checksum of the file
	 * @return whether the shared file is available
	 */
	public boolean contains(Long checksum) {
		return sharedFiles.containsKey(checksum);
	}
	
	/**
	 * Get current files in Shared Folder
	 */
	public static List<SharedFileModel> getFilesInSharedFolder(String sharedFolderName) {
		List<SharedFileModel> listOfFile = new ArrayList<SharedFileModel>();
		
		File dirPath = new File(System.getProperty("user.dir") + File.separator + sharedFolderName);
		List<File> files = (List<File>) FileUtils.listFiles(dirPath, null, true);
        for (File file : files) {
			try {
				long checksum = FileUtils.checksum(file, new CRC32()).getValue();
				SharedFileModel tempObj = new SharedFileModel(file.getName(), file.getCanonicalPath(), "", checksum, file.length());
				listOfFile.add(tempObj);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return listOfFile;
		
	}
	
	/**
	 * Close socket for the server.
	 * @param socket the server socket to close
	 */
	private void closeSocket(ServerSocket socket) {
		try {
			if ( socket != null ) {
				socket.close();
			}
		} catch ( IOException ex ) {
			LOGGER.catching(ex);
		}
	}
}

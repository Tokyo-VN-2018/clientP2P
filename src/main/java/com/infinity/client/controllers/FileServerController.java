package com.infinity.client.controllers;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
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
	private static Map<String, SharedFileModel> sharedFiles = new Hashtable<String, SharedFileModel>();

	public static Map<String, SharedFileModel> getSharedFiles() {
		return sharedFiles;
	}

	/**
	 * The server socket used for receiving commands.
	 */
	private DatagramSocket commandSocket;

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
	
	private final AtomicBoolean running = new AtomicBoolean(false);
	
	public void stop() {
        running.set(false);
    }
	
	/**
	 * Receive the message from client.
	 * @throws IOException
	 */
	public void accept() throws IOException {
		Runnable commandListenerTask = () -> {
			commandSocket = null;
			try {
				commandSocket = new DatagramSocket(COMMAND_PORT);
				byte[] inputDataBuffer = new byte[BUFFER_SIZE];
				byte[] outputDataBuffer = new byte[BUFFER_SIZE];
				
				running.set(true);

				while ( running.get() ) {
					DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
					commandSocket.receive(inputPacket);
					String request = new String(inputPacket.getData());
					
					LOGGER.debug("Received new message: \n\t" + request);
					JSONObject requestInJson = (JSONObject)(JSON.parse(request));

					if ( requestInJson.getString("status").equals("CHECK") ) {
						
						SharedFileModel requestedFile = JSON.toJavaObject(requestInJson.getJSONObject("payload"), SharedFileModel.class);
						int requesterListeningPort = requestInJson.getInteger("listeningPort");
						
						String ipAddress = inputPacket.getAddress().toString().substring(1);
						int port = inputPacket.getPort();
						
						JSONObject checkResObj = new JSONObject();
						checkResObj.put("status", "CHECK");
						
						String hashOfRequestedFile = getMd5(requestedFile);
						if ( sharedFiles.containsKey(hashOfRequestedFile) ) {
							checkResObj.put("message", "ACCEPT");
							String checkRes = checkResObj.toJSONString();
							
							outputDataBuffer = checkRes.getBytes();
							sendDatagramPacket(commandSocket, outputDataBuffer, ipAddress, port);
							Thread.sleep(2000); // Wait for open socket for receiving files
							sendFileStream(hashOfRequestedFile, ipAddress, requesterListeningPort);
							
						} else {
							checkResObj.put("message", "ERROR");
							String checkRes = checkResObj.toJSONString();
							
							outputDataBuffer = checkRes.getBytes();
							sendDatagramPacket(commandSocket, outputDataBuffer, ipAddress, port);
						}
					}
				}
			} catch ( Exception ex ) {
//				LOGGER.catching(ex);
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
		closeSocket(commandSocket);
	}
	
	/**
	 * Send file stream to the receiver.
	 * @param hash				hash of file information
	 * @param ipAddress			the IP address of the receiver
	 * @param REQUESTER_PORT	the port of requester who want to download file
	 */
	private void sendFileStream(String hash, String ipAddress, int REQUESTER_PORT) {
		SharedFileModel fileToSend = sharedFiles.get(hash);
		String filePath = fileToSend.getFilePath();
		Socket socket = null;
		DataInputStream fileInputStream = null;
		DataOutputStream fileOutputStream = null;

		try {
			socket = new Socket(ipAddress, REQUESTER_PORT);
			fileInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filePath)));
			fileOutputStream = new DataOutputStream(socket.getOutputStream());
			
			socket.setSoTimeout(5000);

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
	public static void shareNewFile(SharedFileModel file) {
		sharedFiles.put(getMd5(file), file);
	}
	
	/**
	 * Remove a shared file from the file server because it is no longer shared.
	 * @param checksum the checksum of the file
	 */
	public void unshareFile(SharedFileModel file) {
		sharedFiles.remove(getMd5(file));
	}
	
	/**
	 * Check if the shared file requested is available.
	 * @param checksum - the checksum of the file
	 * @return whether the shared file is available
	 */
	public boolean contains(SharedFileModel file) {
		return sharedFiles.containsKey(getMd5(file));
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
				SharedFileModel tempObj = new SharedFileModel(file.getName(), file.getCanonicalPath(), checksum, file.length());
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
	private void closeSocket(DatagramSocket socket) {
		if ( socket != null ) {
			socket.close();
		}
	}
	
	public static String getMd5(SharedFileModel file)
    {
        try {
  
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
  
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(file.toString().getBytes());
  
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
  
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
}

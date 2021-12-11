package com.infinity.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileServer {
	/**
	 * The map is used for storing shared files.
	 *
	 * The key stands for the checksum of the file.
	 * The value stands for the absolute path of the file.
	 */
	private static Map<String, String> sharedFiles = new Hashtable<String, String>();

	/**
	 * The server socket used for receiving commands.
	 */
	private ServerSocket commandListener;

	/**
	 * The port used for receiving commands.
	 */
	private static final int COMMAND_PORT = 7701;

	/**
	 * The port used for sending file stream.
	 */
	private static final int FILE_STREAM_PORT = 7702;

	/**
	 * The buffer size of the file stream.
	 */
	private static final int BUFFER_SIZE = 4096;

	/**
	 * The unique instance of client.
	 */
	public static final FileServer INSTANCE = new FileServer();

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(FileServer.class);
	
	private FileServer() { }

	public static FileServer getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Register new file to the file server for sharing.
	 *
	 * @param checksum the checksum of the file
	 * @param filePath the absolute path of the file
	 */
	public void shareNewFile(String checksum, String filePath) {
		sharedFiles.put(checksum, filePath);
	}
	
	/**
	 * Remove a shared file from the file server because it is no longer shared.
	 * @param checksum the checksum of the file
	 */
	public void unshareFile(String checksum) {
		sharedFiles.remove(checksum);
	}
	
	/**
	 * Check if the shared file requested is available.
	 * @param checksum - the checksum of the file
	 * @return whether the shared file is available
	 */
	public boolean contains(String checksum) {
		return sharedFiles.containsKey(checksum);
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

package com.infinity.client.controllers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.CRC32;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.infinity.client.models.CheckFileMessModel;
import com.infinity.client.models.ReceivedFileModel;
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
	 * @param file	The file will be download
	 * @param selectedFile	The file is selected
	 * @param filePath  the file path to save the file
	 * @param progressBar download progress bar
	 * @param btnDownload download button
	 * @param clientController 
	 * @throws Exception
	 */
	public void receiveFile(File file, ReceivedFileModel selectedFile, String filePath,
			JProgressBar progressBar, JButton btnDownload, ClientController clientController) throws Exception {
		
		SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
            	progressBar.setVisible(true);
            	
            	DatagramSocket commandSocket = null;
        		ServerSocket fileStreamListener = null;
        		Socket fileStreamSocket = null;
        		DataInputStream fileInputStream = null;
        		DataOutputStream fileOutputStream = null;
        		
        		try {
        			CheckFileMessModel checkMessObj = new CheckFileMessModel();
            		checkMessObj.setStatus("CHECK");
            		checkMessObj.setListeningPort(FILE_STREAM_PORT);
            		
					SharedFileModel fileToDownload = new SharedFileModel(selectedFile.getFileName(), selectedFile.getFilePath(), 
							selectedFile.getChecksum(), selectedFile.getSize());
            		checkMessObj.setPayload(fileToDownload);
            		
            		String checkMess = JSON.toJSONString(checkMessObj);
            		
            		// Send command for requesting files
            		commandSocket = new DatagramSocket();
            		byte[] inputDataBuffer = new byte[BUFFER_SIZE];
            		byte[] outputDataBuffer = checkMess.getBytes();
            		
            		commandSocket.setSoTimeout(5000);
            		
            		DatagramPacket outputPacket = new DatagramPacket(outputDataBuffer,
            				outputDataBuffer.length, InetAddress.getByName(selectedFile.getIp()), selectedFile.getCommandPort());
            		commandSocket.send(outputPacket);

            		DatagramPacket inputPacket = new DatagramPacket(inputDataBuffer, inputDataBuffer.length);
            		commandSocket.receive(inputPacket);
            		String response = new String(inputPacket.getData());
            		
            		LOGGER.debug("[Check] Received message from file owner: \n\t" + response);
            		
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
                	
                	double progress = 0;
                	
                    while (true) {
                    	if ( fileInputStream == null ) {
        					return null;
        				}
        				int bytesRead = fileInputStream.read(fileBuffer);

        				if ( bytesRead == -1 ) {
        					break;
        				}
        				fileOutputStream.write(fileBuffer, 0, bytesRead);
        				
        				double increaseProgress = calculateProgress(bytesRead, selectedFile.getSize());
        				
        				for (int i = (int)progress; i < (int) (progress + increaseProgress); i++) {
        					setProgress(i);
						}
                        progress += increaseProgress;
                        
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
                
                return null;
            }
            
            @Override
            protected void done() {
            	long receivedChecksum = 0;
            	
				try {
					receivedChecksum = FileUtils.checksum(file, new CRC32()).getValue();
					
					if (selectedFile.getChecksum() == receivedChecksum) {
						progressBar.setValue(100);
						JOptionPane.showMessageDialog(null, "File Downloaded Successfully !!!");
						progressBar.setVisible(false);
						progressBar.setValue(0);
						LOGGER.info("File saved to: " + file.getAbsolutePath());
					} else {
						throw new Exception("File download failed, please try again. !!!");
					}
				} catch (Exception e) {
					
					try {
						progressBar.setValue(3);
					    FileUtils.forceDelete(FileUtils.getFile(file.getAbsolutePath()));
					} catch (Exception e2) {
						LOGGER.debug(e2.getMessage());
					}
					
					clientController.reportDownloadErr(selectedFile.getId());
					
					JOptionPane.showMessageDialog(null,
							"Failed to download file from sharer.\n",
							"Receive File Failed", JOptionPane.ERROR_MESSAGE);
					progressBar.setVisible(false);
					progressBar.setValue(0);
				}

				btnDownload.setText("Download");
				btnDownload.setEnabled(true);
            }
        };
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if ("progress".equals(name)) {
                    SwingWorker worker = (SwingWorker) evt.getSource();
                    progressBar.setValue(worker.getProgress());
                }
            }
        });
        worker.execute();
	}
	
	public double calculateProgress(double volume, double overrall) {
		return volume * 100 / overrall;
	}
}

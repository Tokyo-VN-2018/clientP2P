package com.infinity.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infinity.client.controllers.ClientController;
import com.infinity.client.controllers.FileReceiverController;
import com.infinity.client.controllers.FileServerController;
import com.infinity.client.models.ReceivedFileModel;
import javax.swing.ListSelectionModel;
import javax.swing.JProgressBar;

public class Application {

	private JFrame frmFileSharingSystem;
	private JTextField serverIPInput;
	private JTextField usernameInput;
	private JTextField commandPortInput;
	private JTextField streamPortInput;
	private JTextField searchInput;
	private JTable table;
	private JTextField sharedFolderInput;

	/**
	 * Client used for communicating with server.
	 */
	private static final ClientController clientController = ClientController.getInstance();

	/**
	 * FileServer used for receiving commands for sending files.
	 */
	private static final FileServerController fileServerController = FileServerController.getInstance();

	/**
	 * FileReceiver used for receiving file stream.
	 */
	private static final FileReceiverController fileReceiverController = FileReceiverController.getInstance();

	/**
	 * A variable stores whether the client is connected to server.
	 */
	private boolean isConnected = false;

	/**
	 * A variable stores name of shared folder
	 */
	private String sharedFolder;

	/**
	 * A variable stores username of user
	 */
	private String username;

	/**
	 * A variable stores results search
	 */
	List<ReceivedFileModel> results = new ArrayList<>();
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Application window = new Application();
					window.frmFileSharingSystem.setLocationRelativeTo(null);
					window.frmFileSharingSystem.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Application() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmFileSharingSystem = new JFrame();
		frmFileSharingSystem.setTitle("File Sharing System");
		frmFileSharingSystem.setBounds(100, 100, 970, 681);
		frmFileSharingSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFileSharingSystem.getContentPane().setLayout(null);

		JLabel lblServerIP = new JLabel("Server IP");
		lblServerIP.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblServerIP.setBounds(29, 54, 86, 36);
		frmFileSharingSystem.getContentPane().add(lblServerIP);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblUsername.setBounds(379, 54, 86, 36);
		frmFileSharingSystem.getContentPane().add(lblUsername);

		serverIPInput = new JTextField();
		lblServerIP.setLabelFor(serverIPInput);
		serverIPInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		serverIPInput.setBounds(131, 59, 180, 24);
		frmFileSharingSystem.getContentPane().add(serverIPInput);
		serverIPInput.setColumns(10);

		usernameInput = new JTextField();
		lblUsername.setLabelFor(usernameInput);
		usernameInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		usernameInput.setColumns(10);
		usernameInput.setBounds(481, 60, 194, 24);
		frmFileSharingSystem.getContentPane().add(usernameInput);

		JButton btnConnect = new JButton("Connect");
		btnConnect.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));

		btnConnect.setBounds(783, 100, 138, 37);
		frmFileSharingSystem.getContentPane().add(btnConnect);

		JLabel lblTeam = new JLabel("TEAM 5");
		lblTeam.setForeground(new Color(220, 20, 60));
		lblTeam.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeam.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 28));
		lblTeam.setBounds(396, 10, 184, 36);
		frmFileSharingSystem.getContentPane().add(lblTeam);

		JLabel lblPortNo = new JLabel("Port No.");
		lblPortNo.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblPortNo.setBounds(29, 100, 86, 36);
		frmFileSharingSystem.getContentPane().add(lblPortNo);

		commandPortInput = new JTextField();
		lblPortNo.setLabelFor(commandPortInput);
		commandPortInput.setHorizontalAlignment(SwingConstants.CENTER);
		commandPortInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		commandPortInput.setColumns(10);
		commandPortInput.setBounds(131, 105, 76, 24);
		frmFileSharingSystem.getContentPane().add(commandPortInput);

		streamPortInput = new JTextField();
		streamPortInput.setHorizontalAlignment(SwingConstants.CENTER);
		streamPortInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		streamPortInput.setColumns(10);
		streamPortInput.setBounds(235, 105, 76, 24);
		frmFileSharingSystem.getContentPane().add(streamPortInput);

		JButton btnSearch = new JButton("Search");

		btnSearch.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnSearch.setBounds(327, 225, 138, 37);
		frmFileSharingSystem.getContentPane().add(btnSearch);

		JButton btnMyFiles = new JButton("My Files");

		btnMyFiles.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnMyFiles.setBounds(783, 154, 138, 37);
		frmFileSharingSystem.getContentPane().add(btnMyFiles);

		JButton btnAboutTeam = new JButton("About team");

		btnAboutTeam.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnAboutTeam.setBounds(783, 42, 138, 37);
		frmFileSharingSystem.getContentPane().add(btnAboutTeam);

		searchInput = new JTextField();
		searchInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		searchInput.setColumns(10);
		searchInput.setBounds(180, 179, 431, 30);
		frmFileSharingSystem.getContentPane().add(searchInput);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(89, 287, 610, 310);
		frmFileSharingSystem.getContentPane().add(scrollPane);

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 14));
		table.setModel(
				new DefaultTableModel(
						new Object[][] { { null, null, null }, { null, null, null }, { null, null, null },
								{ null, null, null }, { null, null, null }, { null, null, null }, { null, null, null },
								{ null, null, null }, { null, null, null }, { null, null, null }, },
						new String[] { "File name", "Sharer", "Size" }) {
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
					boolean[] columnEditables = new boolean[] { false, false, false };

					public boolean isCellEditable(int row, int column) {
						return columnEditables[column];
					}
				});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
//		table.setAutoCreateRowSorter(true);
		scrollPane.setViewportView(table);

		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setOpaque(false);
		tableHeader.setBackground(new Color(210, 210, 210));
		tableHeader.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		table.setRowHeight(28);

		JButton btnDownload = new JButton("Download");

		btnDownload.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnDownload.setBounds(783, 399, 138, 37);
		frmFileSharingSystem.getContentPane().add(btnDownload);

		JButton btnExit = new JButton("Exit");

		btnExit.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnExit.setBounds(783, 578, 138, 37);
		frmFileSharingSystem.getContentPane().add(btnExit);

		JLabel lblSharedFolder = new JLabel("Shared Folder");
		lblSharedFolder.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblSharedFolder.setBounds(348, 100, 117, 36);
		frmFileSharingSystem.getContentPane().add(lblSharedFolder);

		sharedFolderInput = new JTextField();
		lblSharedFolder.setLabelFor(sharedFolderInput);
		sharedFolderInput.setFont(new Font("Arial", Font.BOLD, 14));
		sharedFolderInput.setColumns(10);
		sharedFolderInput.setBounds(481, 106, 160, 24);
		frmFileSharingSystem.getContentPane().add(sharedFolderInput);

		JFileChooser fileChooser = new JFileChooser();
		
//		serverIPInput.setText("127.0.0.1");
		usernameInput.setText("Anonymous");
		commandPortInput.setText("7701");
		streamPortInput.setText("7702");
//		sharedFolderInput.setText("D:\\Code\\eclipse_workspace_2\\clientP2P\\sharedFolder");
		
		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 10));
		progressBar.setBounds(770, 449, 160, 15);
		frmFileSharingSystem.getContentPane().add(progressBar);
		
		JButton btnChooseFolder = new JButton("...");
		btnChooseFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser f = new JFileChooser();
		        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		        int chooserState = f.showDialog(null, "Choose");
		        if (chooserState == JFileChooser.APPROVE_OPTION) {
		        	sharedFolderInput.setText(f.getSelectedFile().toString());
		        }
			}
		});
		btnChooseFolder.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnChooseFolder.setBounds(643, 106, 32, 24);
		frmFileSharingSystem.getContentPane().add(btnChooseFolder);
		progressBar.setVisible(false);
		
		// Initialize UI
		setupUiComponentAvailability(serverIPInput, usernameInput, commandPortInput, streamPortInput, sharedFolderInput,
						btnConnect, btnMyFiles, btnSearch, btnDownload, table, isConnected, btnChooseFolder);
		
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				btnConnect.setEnabled(false);

				if (!isConnected) {
					String ipAddress = serverIPInput.getText();
					username = usernameInput.getText();
					sharedFolder = sharedFolderInput.getText();

					String commandPortStr = commandPortInput.getText();
					String streamPortStr = streamPortInput.getText();

					boolean check = true;

					int commandPort = -1;
					int streamPort = -1;

					if (ipAddress.length() <= 0 || username.length() <= 0 || sharedFolder.length() <= 0
							|| commandPortStr.length() <= 0 || streamPortStr.length() <= 0) {
						JOptionPane.showMessageDialog(null, "You have not filled in all the required information !!!");
						check = false;
					} else {
						try {
							commandPort = Integer.parseInt(commandPortStr);
							streamPort = Integer.parseInt(streamPortStr);

							if (commandPort < 1025 || commandPort > 65535 || streamPort < 1025 || streamPort > 65535
									|| commandPort == streamPort) {
								JOptionPane.showMessageDialog(null, "Please choose another port number !!!");
								check = false;
							}

						} catch (Exception ex) {
							JOptionPane.showMessageDialog(null, "Ports must be Integer number !!!");
						}

//						File f = new File(System.getProperty("user.dir") + File.separator + sharedFolder);
						File f = new File(sharedFolder);
						if (!f.exists() || !f.isDirectory()) {
							JOptionPane.showMessageDialog(null, "Shared Folder does not exist !!!");
							check = false;
						}

					}

					if (check) {
						FileServerController.setCOMMAND_PORT(commandPort);
						FileReceiverController.setFILE_STREAM_PORT(streamPort);

						try {
							fileServerController.accept();
							clientController.connect(ipAddress, username, commandPort, sharedFolder);
//							fileTableView.setItems(getSharedFiles());

							isConnected = true;
						} catch (Exception ex) {
							LOGGER.catching(ex);
							JOptionPane.showMessageDialog(null,
									"Failed to connect to central server.\n" + ex.getMessage(), "Connection Refused",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					clientController.disconnect();
					fileServerController.stop();
					fileServerController.close();
					fileReceiverController.close();
					isConnected = false;
					searchInput.setText("");
				}
				setupUiComponentAvailability(serverIPInput, usernameInput, commandPortInput, streamPortInput,
						sharedFolderInput, btnConnect, btnMyFiles, btnSearch, btnDownload, table, isConnected, btnChooseFolder);

				btnConnect.setEnabled(true);

			}
		});

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String searchText = searchInput.getText();
				
				clearDataTable(table);
				
				if (searchText.length() <= 0) {
					JOptionPane.showMessageDialog(null, "Enter file name to search !!!");
				} else {
					results.clear();
					List<ReceivedFileModel> searchResults = clientController.searchFiles(searchText);
					results.addAll(searchResults);

//					if (results.size() > 10) {
//						results.subList(10, results.size()).clear();
//					} else if (results.size() == 0) {
//						JOptionPane.showMessageDialog(null, "File not found !!!");
//					}
					
					if (results.size() == 0) {
						JOptionPane.showMessageDialog(null, "File not found !!!");
					}

					int count = 0;
//					for (ReceivedFileModel file : results) {
//						table.getModel().setValueAt(file.getFileName(), count, 0);
//						table.getModel().setValueAt(file.getSharer(), count, 1);
////						table.getModel().setValueAt(file.getSize(), count, 2);
//						table.getModel().setValueAt(Application.humanReadableByteCountBin(file.getSize()), count, 2);
//						count++;
//					}
					
					
					String[] columnNames = { "File Name", "Checksum", "Size" };
					
					int numOfRows = results.size();
					if (numOfRows < 10) {
						numOfRows = 10;
					}
					TableModel myData = new DefaultTableModel(new String[numOfRows][3], columnNames);
					
					table.setModel(myData);
					
					for (ReceivedFileModel file : results) {
						table.getModel().setValueAt(file.getFileName(), count, 0);
						table.getModel().setValueAt(file.getSharer(), count, 1);
//						table.getModel().setValueAt(file.getSize(), count, 2);
						table.getModel().setValueAt(Application.humanReadableByteCountBin(file.getSize()), count, 2);
						count++;
					}

				}
				
				btnDownload.setEnabled(false);
			}
		});

		btnAboutTeam.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AboutUsFrame aboutUsFrame = new AboutUsFrame();
				aboutUsFrame.setLocationRelativeTo(frmFileSharingSystem);
				aboutUsFrame.setVisible(true);
			}
		});

		btnMyFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UpdateDBFrame updateDBFrame = new UpdateDBFrame(clientController, fileServerController,
						fileReceiverController, username, sharedFolder, LOGGER);
				updateDBFrame.setLocationRelativeTo(frmFileSharingSystem);
				updateDBFrame.setVisible(true);
			}
		});
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	int _row = table.getSelectedRow();
	        	if (_row >=0 && results.size() > 0 && 0 <= _row && _row < results.size() ) {
	        		btnDownload.setEnabled(true);
				} else {
					btnDownload.setEnabled(false);
				}
	        }
	    });

		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				int row = table.getSelectedRow();

				btnDownload.setText("Please wait ...");
				btnDownload.setEnabled(false);

//					String fileName = table.getModel().getValueAt(row, 0).toString();
				ReceivedFileModel selectedFile = results.get(row);

				fileChooser.setSelectedFile(new File(selectedFile.getFileName()));
				int chooserState = fileChooser.showSaveDialog(null);
				
				if (chooserState == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();

					if (file != null) {
						try {
							// Receive files and check if checksum is the same
							fileReceiverController.receiveFile(file, selectedFile, file.getAbsolutePath(),
									progressBar, btnDownload, clientController);

						} catch (Exception ex) {
							LOGGER.catching(ex);
							
							try {
							    FileUtils.forceDelete(FileUtils.getFile(file.getAbsolutePath()));
							} catch (Exception e2) {
								LOGGER.debug(e2.getMessage());
							}
							
							clientController.reportDownloadErr(selectedFile.getId());
							
							JOptionPane.showMessageDialog(null,
									"Failed to receive a file from another sharer.\n",
									"Receive File Failed", JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					btnDownload.setText("Download");
					btnDownload.setEnabled(true);
				}
			}
		});

		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isConnected) {
					clientController.disconnect();
					fileServerController.stop();
					fileServerController.close();
				}
				System.exit(0);
			}
		});

	}

	/**
	 * Setup the availability of components in UI.
	 * 
	 * @param serverIpTextField     the text field for server IP
	 * @param usernameTextField     the text field for nick name
	 * @param commandPortTextField  the text field for command port
	 * @param streamPortTextField   the text field for stream file port
	 * @param sharedFolderTextField the text field for name of shared folder
	 * @param connectServerButton   the button for connecting/disconnect to/from
	 *                              server
	 * @param searchButton          the button for search files in Server
	 * @param downloadButton        the button for download the selected file
	 * @param myFilesButton         the button for display all shared files / update
	 *                              DB
	 * @param isConnected           whether is connected to server right now
	 */
	private void setupUiComponentAvailability(JTextField serverIpTextField, JTextField usernameTextField,
			JTextField commandPortTextField, JTextField streamPortTextField, JTextField sharedFolderTextField,
			JButton connectServerButton, JButton myFilesButton, JButton searchButton, JButton downloadButton,
			JTable table, boolean isConnected, JButton btnChooseFolder) {
		if (isConnected) {
			serverIpTextField.setEditable(false);
			usernameTextField.setEditable(false);
			commandPortTextField.setEditable(false);
			streamPortTextField.setEditable(false);
			sharedFolderTextField.setEditable(false);
			connectServerButton.setText("Disconnect");
			myFilesButton.setEnabled(true);
			searchButton.setEnabled(true);
			btnChooseFolder.setEnabled(false);

		} else {
			serverIpTextField.setEditable(true);
			usernameTextField.setEditable(true);
			commandPortTextField.setEditable(true);
			streamPortTextField.setEditable(true);
			sharedFolderTextField.setEditable(true);
			connectServerButton.setText("Connect");
			myFilesButton.setEnabled(false);
			searchButton.setEnabled(false);
			downloadButton.setEnabled(false);
			clearDataTable(table);
			btnChooseFolder.setEnabled(true);
		}
	}

	public static void clearDataTable(JTable table) {
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {
				table.getModel().setValueAt(null, i, j);
			}
		}
	}
	
	public static String humanReadableByteCountBin(long bytes) {
	    long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
	    if (absB < 1024) {
	        return bytes + " B";
	    }
	    long value = absB;
	    CharacterIterator ci = new StringCharacterIterator("KMGTPE");
	    for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
	        value >>= 10;
	        ci.next();
	    }
	    value *= Long.signum(bytes);
	    return String.format("%.1f %cB", value / 1024.0, ci.current());
	}
}

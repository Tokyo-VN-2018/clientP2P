package com.infinity.client;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.logging.log4j.Logger;

import com.infinity.client.controllers.ClientController;
import com.infinity.client.controllers.FileReceiverController;
import com.infinity.client.controllers.FileServerController;
import com.infinity.client.models.SharedFileModel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import java.awt.event.ActionEvent;

public class UpdateDBFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	private JTextField textFilter;
	
	JButton btnFilter = new JButton("Filter");

	/**
	 * Create the frame.
	 */
	public UpdateDBFrame(ClientController clientController, FileServerController fileServerController,
			FileReceiverController fileReceiverController, String username, String sharedFolderName, Logger LOGGER) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 671, 497);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblSharedFiles = new JLabel("Shared Files");
		lblSharedFiles.setHorizontalAlignment(SwingConstants.CENTER);
		lblSharedFiles.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 20));
		lblSharedFiles.setBounds(233, 15, 184, 36);
		contentPane.add(lblSharedFiles);

		JLabel notificationText = new JLabel("");
		notificationText.setForeground(Color.RED);
		notificationText.setHorizontalAlignment(SwingConstants.CENTER);
		notificationText.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 15));
		notificationText.setBounds(130, 420, 400, 25);
		contentPane.add(notificationText);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 94, 610, 310);
		contentPane.add(scrollPane);
		
		Object[][] data = new Object[][] {
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
			{null, null, null},
		};
		
		String[] columnNames = { "File Name", "Checksum", "Size" };
		
		TableModel defaultModel = new DefaultTableModel(data, columnNames);

		table = new JTable();
		table.setModel(defaultModel);
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 14));
		table.setAutoCreateRowSorter(false);
		scrollPane.setViewportView(table);

		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setOpaque(false);
		tableHeader.setBackground(new Color(210, 210, 210));
		tableHeader.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		table.setRowHeight(28);

		displayDataInTable(table, FileServerController.getSharedFiles());

		List<SharedFileModel> actualSharedFilesModel = FileServerController.getFilesInSharedFolder(sharedFolderName);
		List<String> actualSharedFile = new ArrayList<String>();
		for (SharedFileModel file : actualSharedFilesModel) {
			actualSharedFile.add(FileServerController.getMd5(file));
		}

		List<String> currentSharedFiles = new ArrayList<String>();
		for (String key : FileServerController.getSharedFiles().keySet()) {
			currentSharedFiles.add(key);
		}

//		int numActualSharedFiles = actualSharedFile.size();
//		int numCurrentDBSharedFiles = currentSharedFiles.size();

		notificationText.setText("Please update if you have changed the shared file !");
//		if (numActualSharedFiles != numCurrentDBSharedFiles) {
//			
//		}

		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				List<SharedFileModel> payloadPublish = new ArrayList<SharedFileModel>();
				List<SharedFileModel> payloadUnPublish = new ArrayList<SharedFileModel>();

				for (SharedFileModel sharedFile : actualSharedFilesModel) {
					if (!fileServerController.contains(sharedFile)) {
						payloadPublish.add(sharedFile);
					}
				}

				if (payloadPublish.size() > 0) {
					if (clientController.shareFile(payloadPublish)) {
						for (SharedFileModel sharedFile : payloadPublish) {
							FileServerController.shareNewFile(sharedFile);
						}
						notificationText.setText("");
//						LOGGER.info("File shared: " + JSON.toJSONString(payloadPublish));
					} else {
						JOptionPane.showMessageDialog(null,
								"Something wrong with your network\n or there's a file have the same checksum. !!!");
					}
				}

				for (String hashOfFile : currentSharedFiles) {
					if (!actualSharedFile.contains(hashOfFile)) {
						if (FileServerController.getSharedFiles().get(hashOfFile) != null) {
							payloadUnPublish.add(FileServerController.getSharedFiles().get(hashOfFile));
						}
						
					}
				}
				if (payloadUnPublish.size() > 0) {
					if (clientController.unshareFile(payloadUnPublish)) {
						for (SharedFileModel unsharedFile : payloadUnPublish) {
							fileServerController.unshareFile(unsharedFile);
						}
						notificationText.setText("");
//						LOGGER.info("File unshared: " + JSON.toJSONString(payloadUnPublish));
					} else {
						JOptionPane.showMessageDialog(null, "Something wrong with your network !!!");
					}
				}

				if (payloadPublish.size() == 0 && payloadUnPublish.size() == 0) {
					JOptionPane.showMessageDialog(null, "Your files are up to date", "Updater",
							JOptionPane.INFORMATION_MESSAGE);
				}

				displayDataInTable(table, FileServerController.getSharedFiles());
			}
		});
		btnUpdate.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnUpdate.setBounds(23, 22, 130, 30);
		contentPane.add(btnUpdate);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
//				System.exit(0);
			}
		});
		btnCancel.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnCancel.setBounds(498, 22, 130, 30);
		contentPane.add(btnCancel);
		
		textFilter = new JTextField();
		textFilter.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 12));
		textFilter.setBounds(180, 60, 236, 25);
		contentPane.add(textFilter);
		textFilter.setColumns(10);
		
//		final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(defaultModel);
//        table.setRowSorter(sorter);
		
		
		btnFilter.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnFilter.setBounds(430, 60, 80, 25);
		contentPane.add(btnFilter);

	}

	private void displayDataInTable(JTable table, Map<String, SharedFileModel> map) {
		Application.clearDataTable(table);
		int count = 0;
		
		String[] columnNames = { "File Name", "Checksum", "Size" };
		
		int numOfRows = map.values().size();
		if (numOfRows < 10) {
			numOfRows = 10;
		}
//		System.out.println(numOfRows);
		TableModel myData = new DefaultTableModel(new Object[numOfRows+1][3], columnNames);
//		System.out.println(myData.getRowCount());
		
		table.setModel(myData);
		
		for (SharedFileModel file : map.values()) {
			table.getModel().setValueAt(file.getFileName(), count, 0);
			table.getModel().setValueAt(file.getChecksum(), count, 1);
//			table.getModel().setValueAt(file.getSize(), count, 2);
			table.getModel().setValueAt(Application.humanReadableByteCountBin(file.getSize()), count, 2);
//			table.getModel().setValueAt(FileUtils.byteCountToDisplaySize(file.getSize()), count, 2);
			
			count++;
		}
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(myData);
        table.setRowSorter(sorter);
        
        btnFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String text = textFilter.getText();
				System.out.println(text);
              if (text.length() == 0) {
                  sorter.setRowFilter(null);
              } else {
                  try {
                      sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                  } catch (PatternSyntaxException pse) {
                      System.out.println("Bad regex pattern");
                  }
              }
			}
		});
		
	}
}

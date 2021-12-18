package com.infinity.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.infinity.client.controllers.ClientController;
import com.infinity.client.controllers.FileReceiverController;
import com.infinity.client.controllers.FileServerController;
import com.infinity.client.models.SharedFileModel;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.awt.event.ActionEvent;

public class UpdateDBFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					UpdateDBFrame frame = new UpdateDBFrame("sharedFolder2");
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public UpdateDBFrame(ClientController clientController, FileServerController fileServerController, 
			FileReceiverController fileReceiverController, String sharedFolderName, Logger LOGGER) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 671, 497);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblSharedFiles = new JLabel("Shared Files");
		lblSharedFiles.setHorizontalAlignment(SwingConstants.CENTER);
		lblSharedFiles.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 20));
		lblSharedFiles.setBounds(233, 22, 184, 36);
		contentPane.add(lblSharedFiles);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(23, 94, 610, 310);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
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
			},
			new String[] {
				"File Name", "Checksum", "Size (Byte)"
			}
		) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Class[] columnTypes = new Class[] {
				String.class, Long.class, Long.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 14));
		scrollPane.setViewportView(table);
		
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setOpaque(false);
		tableHeader.setBackground(new Color(210, 210, 210));
		tableHeader.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		table.setRowHeight(28);
		
		JButton btnUpdate = new JButton("Update");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				List<SharedFileModel> actualSharedFilesModel = FileServerController.getFilesInSharedFolder(sharedFolderName);
				List<Long> actualSharedFile = new ArrayList<Long>();
				for (SharedFileModel files : actualSharedFilesModel) {
					actualSharedFile.add(files.getChecksum());
				}
				
				List<Long> currentDBSharedFiles = new ArrayList<Long>();
				for (Long key : FileServerController.getSharedFiles().keySet()) {
				    currentDBSharedFiles.add(key);
				}
				
				int numActualSharedFiles = actualSharedFile.size();
				int numCurrentDBSharedFiles = currentDBSharedFiles.size();
				
				List<SharedFileModel> payload = new ArrayList<SharedFileModel>();
				
				if (numActualSharedFiles > numCurrentDBSharedFiles) {
					for (SharedFileModel sharedFile : actualSharedFilesModel) {
						if (!fileServerController.contains(sharedFile.getChecksum())) {
							payload.add(sharedFile);
						}
					}
					
					if ( clientController.shareFile(payload) ) {
						for (SharedFileModel sharedFile : payload) {
							FileServerController.shareNewFile(sharedFile.getChecksum(), sharedFile);
						}
						LOGGER.info("File shared: " + JSON.toJSONString(payload));
					} else {
						JOptionPane.showMessageDialog(null, "Something wrong with your network\n or there's a file have the same checksum. !!!");
					}
					
				} else if (numActualSharedFiles < numCurrentDBSharedFiles) {
					for (Long cs : currentDBSharedFiles) {
						if (!actualSharedFile.contains(cs)) {
							payload.add(FileServerController.getSharedFiles().get(cs));
						}
					}
					
					if ( clientController.unshareFile(payload) ) {
						for (SharedFileModel unsharedFile : payload) {
							fileServerController.unshareFile(unsharedFile.getChecksum());
						}
						LOGGER.info("File unshared: " + JSON.toJSONString(payload));
					} else {
						JOptionPane.showMessageDialog(null, "Something wrong with your network !!!");
					}
				}
			}
		});
		btnUpdate.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnUpdate.setBounds(23, 22, 130, 30);
		contentPane.add(btnUpdate);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
//				dispose();
				System.exit(0);
			}
		});
		btnCancel.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnCancel.setBounds(498, 22, 130, 30);
		contentPane.add(btnCancel);
	}
}

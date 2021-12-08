package com.infinity.client;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.AttributeSet.ColorAttribute;

import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.JScrollPane;

public class Main extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;

	/**
	 * Create the frame.
	 */
	public Main() {
		setTitle("File Sharing System");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 645, 544);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textField = new JTextField();
		textField.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		textField.setBounds(101, 65, 409, 27);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnNewButton = new JButton("Search");
		btnNewButton.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnNewButton.setBounds(250, 110, 113, 37);
		contentPane.add(btnNewButton);
		
		JButton btnSharedFile = new JButton("My Files");
		btnSharedFile.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnSharedFile.setBounds(15, 10, 139, 37);
		contentPane.add(btnSharedFile);
		
		JButton btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnDisconnect.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnDisconnect.setBounds(478, 10, 139, 37);
		contentPane.add(btnDisconnect);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setEnabled(false);
		scrollPane.setBounds(32, 165, 568, 311);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setForeground(new Color(0, 0, 0));
		table.setBackground(new Color(255, 255, 255));
		scrollPane.setViewportView(table);
		table.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		table.setBorder(null);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"File 1", "192.168.1.1", "123456", "454915"},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
				{null, null, null, null},
			},
			new String[] {
				"File Name", "Sharer", "Checksum", "Size (Byte)"
			}
		));
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setOpaque(false);
		tableHeader.setBackground(new Color(210, 210, 210));
		tableHeader.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		table.setRowHeight(28);
	}
}

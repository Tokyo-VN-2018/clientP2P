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
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class AboutUsFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public AboutUsFrame() {
		setTitle("File Sharing System");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 495, 374);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblMembers = new JLabel("Members");
		lblMembers.setForeground(Color.RED);
		lblMembers.setHorizontalAlignment(SwingConstants.CENTER);
		lblMembers.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblMembers.setBounds(190, 105, 86, 36);
		contentPane.add(lblMembers);
		
		JLabel lblNguyenVietHoang = new JLabel("Nguyen Viet Hoang");
		lblNguyenVietHoang.setHorizontalAlignment(SwingConstants.CENTER);
		lblNguyenVietHoang.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 15));
		lblNguyenVietHoang.setBounds(140, 135, 184, 36);
		contentPane.add(lblNguyenVietHoang);
		
		JLabel lblTaDangHuy = new JLabel("Ta Dang Huy");
		lblTaDangHuy.setHorizontalAlignment(SwingConstants.CENTER);
		lblTaDangHuy.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 15));
		lblTaDangHuy.setBounds(140, 160, 184, 36);
		contentPane.add(lblTaDangHuy);
		
		JLabel lblNguyenTienDung = new JLabel("Nguyen Tien Dung");
		lblNguyenTienDung.setHorizontalAlignment(SwingConstants.CENTER);
		lblNguyenTienDung.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 15));
		lblNguyenTienDung.setBounds(140, 188, 184, 36);
		contentPane.add(lblNguyenTienDung);
		
		JLabel lblNetworkProgramming = new JLabel("Network Programming");
		lblNetworkProgramming.setForeground(Color.RED);
		lblNetworkProgramming.setHorizontalAlignment(SwingConstants.CENTER);
		lblNetworkProgramming.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblNetworkProgramming.setBounds(135, 45, 200, 40);
		contentPane.add(lblNetworkProgramming);
		
		JLabel lblLecturer = new JLabel("Lecturer: Assoc. Prof. Truong Dieu Linh");
		lblLecturer.setHorizontalAlignment(SwingConstants.CENTER);
		lblLecturer.setFont(new Font("UD Digi Kyokasho NK-B", Font.PLAIN, 15));
		lblLecturer.setBounds(59, 70, 362, 36);
		contentPane.add(lblLecturer);
		
		JLabel lblDaiCo = new JLabel("1 Dai Co Viet Road, Ha Noi, Viet Nam");
		lblDaiCo.setHorizontalAlignment(SwingConstants.CENTER);
		lblDaiCo.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 16));
		lblDaiCo.setBounds(59, 285, 362, 36);
		contentPane.add(lblDaiCo);
		
		JLabel lblHanoiUniversityOf = new JLabel("HANOI UNIVERSITY OF SCIENCE AND TECHNOLOGY");
		lblHanoiUniversityOf.setHorizontalAlignment(SwingConstants.CENTER);
		lblHanoiUniversityOf.setForeground(new Color(178, 34, 34));
		lblHanoiUniversityOf.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblHanoiUniversityOf.setBounds(27, 10, 437, 40);
		contentPane.add(lblHanoiUniversityOf);
		
		JLabel lblEmail = new JLabel("Contact");
		lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
		lblEmail.setForeground(Color.RED);
		lblEmail.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblEmail.setBounds(190, 224, 86, 36);
		contentPane.add(lblEmail);
		
		JLabel lblTokyoexamplegmailcom = new JLabel("tokyo.example@gmail.com");
		lblTokyoexamplegmailcom.setHorizontalAlignment(SwingConstants.CENTER);
		lblTokyoexamplegmailcom.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblTokyoexamplegmailcom.setBounds(110, 250, 264, 36);
		contentPane.add(lblTokyoexamplegmailcom);
	}
}

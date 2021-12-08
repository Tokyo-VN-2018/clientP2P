package com.infinity.client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class Application {

	private JFrame frmFileSharingSystem;
	private JTextField serverIPInput;
	private JTextField usernameInput;

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
		frmFileSharingSystem.setBounds(100, 100, 625, 419);
		frmFileSharingSystem.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmFileSharingSystem.getContentPane().setLayout(null);
		
		JLabel lblServerIP = new JLabel("Server IP");
		lblServerIP.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblServerIP.setBounds(253, 80, 86, 36);
		frmFileSharingSystem.getContentPane().add(lblServerIP);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblUsername.setBounds(253, 140, 86, 36);
		frmFileSharingSystem.getContentPane().add(lblUsername);
		
		serverIPInput = new JTextField();
		serverIPInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		serverIPInput.setBounds(355, 85, 194, 24);
		frmFileSharingSystem.getContentPane().add(serverIPInput);
		serverIPInput.setColumns(10);
		
		usernameInput = new JTextField();
		usernameInput.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 14));
		usernameInput.setColumns(10);
		usernameInput.setBounds(355, 146, 194, 24);
		frmFileSharingSystem.getContentPane().add(usernameInput);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String serverIP = serverIPInput.getText();
				String username = usernameInput.getText();
				
				Main viewFrame = new Main();
				viewFrame.setLocationRelativeTo(frmFileSharingSystem);
				viewFrame.setVisible(true);
			}
		});
		btnConnect.setBounds(386, 203, 138, 44);
		frmFileSharingSystem.getContentPane().add(btnConnect);
		
		JLabel lblNetworkProgramming = new JLabel("Network Programming");
		lblNetworkProgramming.setHorizontalAlignment(SwingConstants.CENTER);
		lblNetworkProgramming.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblNetworkProgramming.setBounds(202, 284, 200, 40);
		frmFileSharingSystem.getContentPane().add(lblNetworkProgramming);
		
		JLabel lblLecturer = new JLabel("Lecturer: Assoc. Prof. Truong Dieu Linh");
		lblLecturer.setHorizontalAlignment(SwingConstants.CENTER);
		lblLecturer.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblLecturer.setBounds(126, 330, 362, 36);
		frmFileSharingSystem.getContentPane().add(lblLecturer);
		
		JLabel lblMembers = new JLabel("Members");
		lblMembers.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblMembers.setBounds(40, 70, 86, 36);
		frmFileSharingSystem.getContentPane().add(lblMembers);
		
		JLabel lblNguyenVietHoang = new JLabel("Nguyen Viet Hoang");
		lblNguyenVietHoang.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblNguyenVietHoang.setBounds(40, 113, 184, 36);
		frmFileSharingSystem.getContentPane().add(lblNguyenVietHoang);
		
		JLabel lblTaDangHuy = new JLabel("Ta Dang Huy");
		lblTaDangHuy.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblTaDangHuy.setBounds(40, 153, 184, 36);
		frmFileSharingSystem.getContentPane().add(lblTaDangHuy);
		
		JLabel lblNguyenTienDung = new JLabel("Nguyen Tien Dung");
		lblNguyenTienDung.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 15));
		lblNguyenTienDung.setBounds(40, 199, 184, 36);
		frmFileSharingSystem.getContentPane().add(lblNguyenTienDung);
		
		JLabel lblTeam = new JLabel("Team 5");
		lblTeam.setHorizontalAlignment(SwingConstants.CENTER);
		lblTeam.setFont(new Font("UD Digi Kyokasho NK-B", Font.BOLD, 28));
		lblTeam.setBounds(202, 10, 184, 36);
		frmFileSharingSystem.getContentPane().add(lblTeam);
	}
}

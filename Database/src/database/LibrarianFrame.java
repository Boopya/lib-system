package database;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LibrarianFrame {

	private JFrame frame;
	private LoginFrame loginFrame;

	/**
	 * Launch the application.
	 */
	public static void createLibrarianFrame() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LibrarianFrame window = new LibrarianFrame();
					window.frame.setVisible(true);
					window.frame.setResizable(false);
					window.frame.setLocationRelativeTo(null);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LibrarianFrame() {
		initialize();
	}
	
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
	
		JPanel panel = new JPanel();
		panel.setBounds(76, 11, 289, 239);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Librarian Frame");
		lblNewLabel.setBounds(89, 32, 129, 14);
		panel.add(lblNewLabel);
		
		JButton btnNewButton = new JButton("Logout");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane.showConfirmDialog(null, "Do you want to logout?","Logout",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
				
				if(response == JOptionPane.YES_OPTION) {
					LoginFrame loginFrame = new LoginFrame();
					frame.setVisible(false);
					loginFrame.show();
				}
			}
		});
		btnNewButton.setBounds(89, 154, 89, 23);
		panel.add(btnNewButton);
	}
}

package database;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;
import javax.swing.RootPaneContainer;

import org.eclipse.swt.events.DisposeEvent;

import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;

import com.ibm.icu.impl.duration.impl.DataRecord.ESeparatorVariant;
import com.jgoodies.forms.factories.DefaultComponentFactory;



import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Label;

public class LoginFrame {
	
	private LibrarianFrame librarianFrame;
	private PatronFrame patronFrame;
	private JFrame frame;
	private JTextField textField;
	private JPasswordField passwordField;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame();
					window.show();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginFrame() {
		initialize();
	}
	

	public void show() {
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
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
		panel.setBounds(70, 20, 290, 240);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(100, 88, 86, 20);
		panel.add(textField);
		textField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(100, 119, 86, 20);
		panel.add(passwordField);
		
		JButton btnNewButton = new JButton("Login");
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				// if librarian id 
				int choice = JOptionPane.showConfirmDialog(null, "Do you want to login as a Librarian?","Login",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
				if(choice == JOptionPane.YES_OPTION) {
					librarianFrame = new LibrarianFrame();
					librarianFrame.createLibrarianFrame();
					frame.setVisible(false);
					
				
				}else if(choice == JOptionPane.NO_OPTION) {
					// patron
					patronFrame = new PatronFrame();
					patronFrame.createPatronFrame();
					frame.dispose();
				}else if(choice == JOptionPane.CANCEL_OPTION) {
					//cancel .. go back to login page
				}
			}
		});
		btnNewButton.setBounds(100, 162, 86, 23);
		panel.add(btnNewButton);
		
		lblNewLabel = new JLabel("Username:");
		lblNewLabel.setFont(new Font("Serif", Font.PLAIN, 15));
		lblNewLabel.setBounds(33, 90, 67, 14);
		panel.add(lblNewLabel);
		
		lblNewLabel_1 = new JLabel("Password:");
		lblNewLabel_1.setFont(new Font("Serif", Font.PLAIN, 15));
		lblNewLabel_1.setBounds(38, 120, 62, 17);
		panel.add(lblNewLabel_1);
		
		Font font = new Font("Serif", Font.BOLD,30);
		Label label = new Label("Login");
		label.setAlignment(Label.CENTER);
		label.setBounds(90, 27, 100, 35);
		label.setFont(font);
		panel.add(label);
	}
}

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import java.sql.*;

public class LoginFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Connection con;
	private MenuBar menuBar;
	private Menu aboutMenu;
	private MenuItem aboutSys;
	private MenuItem aboutUs;

	private JLabel loginLabel;
	private JLabel passwordLabel;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JButton loginButton;

	public LoginFrame(Connection con){
		this.con = con;

		setTitle("Library System");

		menuBar = new MenuBar();

		aboutMenu = new Menu("About");

		aboutSys = new MenuItem("About System");
		aboutUs = new MenuItem("About Us");

		aboutSys.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				JOptionPane.showMessageDialog(rootPane,"[Message]","About System",JOptionPane.INFORMATION_MESSAGE);
			}
		});
		aboutUs.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				JOptionPane.showMessageDialog(rootPane,"[Message]","About Us",JOptionPane.INFORMATION_MESSAGE);
			}
		});

		aboutMenu.add(aboutSys);
		aboutMenu.add(aboutUs);

		menuBar.add(aboutMenu);

		setMenuBar(menuBar);

		loginLabel = new JLabel("Login ID:");
		passwordLabel = new JLabel("Password:");

		loginField = new JTextField();
		passwordField = new JPasswordField();
		
		loginField.setPreferredSize(new Dimension(150,20));
		passwordField.setPreferredSize(new Dimension(150,20));

		loginField.addKeyListener(new LoginListener());
		passwordField.addKeyListener(new LoginListener());

		loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(65,25));
		loginButton.setFocusable(false);
		loginButton.addActionListener(new LoginListener());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(5,5,5,5);
		setLayout(new GridBagLayout());
		
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.EAST;
		add(loginLabel,constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.WEST;
		add(loginField,constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.EAST;
		add(passwordLabel,constraints);

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.anchor = GridBagConstraints.WEST;
		add(passwordField,constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.gridwidth = 2;
		add(loginButton,constraints);

		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent event){
				int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", 
				"Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION){
					dispose();
				}
			}
		});
	}

	private void login(){
		String loginStr = loginField.getText();
		String passStr = String.valueOf(passwordField.getPassword());

		if (loginStr.isEmpty() || passStr.isEmpty()){
			JOptionPane.showMessageDialog(rootPane, 
			"You must fill all the required fields.", 
			"Null Input", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {
			String query = "SELECT LOGINID FROM PATRON WHERE LOGINID = ? AND PASSWORD = ?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, loginStr);
			ps.setString(2, passStr);

			ResultSet rs = ps.executeQuery();

			if (!rs.next()){
				JOptionPane.showMessageDialog(rootPane, "Invalid login ID/password.", 
				"Login Failed", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			query = "SELECT LOGINID FROM LIBRARIAN WHERE LOGINID = ?";
			ps = con.prepareStatement(query);
			ps.setString(1,loginStr);
			rs = ps.executeQuery();

			if (rs.next()){
				createOptionDialog();
			}
			else {
				createPatronFrame();
			}

			rs.close();
			ps.close();
		}
		catch (SQLException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(rootPane, e.getMessage(), 
			"SQLException", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void createOptionDialog(){
		Object[] options = {"Librarian","Patron"};
		JOptionPane optionPane = new JOptionPane("Select the mode interface:\n", JOptionPane.INFORMATION_MESSAGE,
		JOptionPane.OK_CANCEL_OPTION,null,options);
		JDialog dialog = new JDialog(this,"Librarian Detected",true);
		dialog.setContentPane(optionPane);
		optionPane.addPropertyChangeListener(new PropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())){
					if (optionPane.getValue().equals(options[0])){
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
						createLibrarianFrame();
					}
					else if (optionPane.getValue().equals(options[1])){
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
						createPatronFrame();
					}
				}
			}
		});
		dialog.pack();
		dialog.setLocationRelativeTo(rootPane);
		dialog.setVisible(true);
	}

	private void createPatronFrame(){
		PatronFrame patron = new PatronFrame(con);
		patron.setSize(500,300);
		patron.setResizable(false);
		patron.setLocationRelativeTo(null);
		patron.setVisible(true);
		dispose();
	}

	private void createLibrarianFrame(){
		LibrarianFrame librarian = new LibrarianFrame(con);
		librarian.setSize(500,300);
		librarian.setResizable(false);
		librarian.setLocationRelativeTo(null);
		librarian.setVisible(true);
		dispose();
	}

	private class LoginListener extends KeyAdapter implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			login();
		}

		@Override
		public void keyTyped(KeyEvent event) {
			if ((int)event.getKeyChar() == 10) {
				login();
			}
		}
	}
}
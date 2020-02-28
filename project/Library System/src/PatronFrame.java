import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class PatronFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Connection con;

	public PatronFrame(Connection con){
		this.con = con;
		
		setTitle("Patron");

		pack();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent event){
				int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", 
				"Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (response == JOptionPane.YES_OPTION){
					createLoginFrame();
				}
			}
		});
	}

	private void createLoginFrame() {
		LoginFrame login = new LoginFrame(con);
		login.setSize(300, 200);
        login.setResizable(false);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
        dispose();
	}
}
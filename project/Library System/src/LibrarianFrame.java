import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class LibrarianFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Connection con;

	private JTabbedPane tabbedPane;
	private JPanel transactionPanel;
	private JPanel patronPanel;
	private JPanel bookPanel;
	private JPanel librarianPanel;

	private JPanel[] panels = {transactionPanel,patronPanel,bookPanel,librarianPanel};
	private String[] panelTitles = {"Transaction","Patron","Book","Librarian"};

	private JLabel searchLabel;
	private JTextField searchField;
	private JComboBox<String> searchBox;

	private JTable table;

	private JButton addButton;
	private JButton editButton;
	private JButton deleteButton;
	private JButton finishButton;

	public LibrarianFrame(Connection con){
		this.con = con;

		setTitle("Librarian");

		tabbedPane = new JTabbedPane();

		for (int i = 0; i < panels.length; ++i){
			panels[i] = new JPanel(new GridBagLayout());
			tabbedPane.add(panelTitles[i],panels[i]);
		}
		tabbedPane.setFocusable(false);

		addButton = new JButton("Add");
		editButton = new JButton("Edit");
		deleteButton = new JButton("Delete");
		finishButton = new JButton("Finish");

		table = new JTable();

		add(tabbedPane);

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
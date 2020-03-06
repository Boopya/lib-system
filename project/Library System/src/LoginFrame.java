import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.*;
import java.sql.*;

public class LoginFrame extends JFrame implements SQLStatements{
    private static final long serialVersionUID = 1L;
    private Connection con;
    private MenuBar menuBar;
    private Menu aboutMenu;
    private MenuItem aboutSystemMenuItem;
    private MenuItem aboutUsMenuItem;
    private JLabel loginLabel;
    private JLabel passwordLabel;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private String loginId;

    public LoginFrame(Connection con){
            this.con = con;
            initComponents();
    }

    public void initComponents() {
        // frame settings
        setTitle("Library System");
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // menu objects
        menuBar = new MenuBar();
        aboutMenu = new Menu("About");
        aboutSystemMenuItem = new MenuItem("About System");
        aboutUsMenuItem = new MenuItem("About Us");

        // compile menu objects
        aboutMenu.add(aboutSystemMenuItem);
        aboutMenu.add(aboutUsMenuItem);
        menuBar.add(aboutMenu);
        setMenuBar(menuBar);

        // layout constraints
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        // login label
        loginLabel = new JLabel("Login ID:");
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.EAST;
        add(loginLabel,c);

        // password label
        passwordLabel = new JLabel("Password:");
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        add(passwordLabel,c);

        // login field
        loginField = new JTextField();
        loginField.setPreferredSize(new Dimension(150,20));
        loginField.addKeyListener(new LoginListener());
        c.gridx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        add(loginField,c);

        // password field
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150,20));
        passwordField.addKeyListener(new LoginListener());
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        add(passwordField,c);

        // login button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(65,25));
        loginButton.addActionListener(new LoginListener());
        loginButton.setFocusable(false);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(loginButton,c);

        pack();

        // about system action listener
        aboutSystemMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                JOptionPane.showMessageDialog(rootPane, "[Message]", "About System", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // about us action listener
        aboutUsMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                JOptionPane.showMessageDialog(rootPane, "[Message]", "About Us", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // window listener
        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent event){
                int response = JOptionPane.showConfirmDialog(rootPane, "Do you want to exit?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION){
                    try {
                        con.close();
                        dispose();
                    }
                    catch (SQLException e){
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(rootPane, e.getMessage(), 
                        "SQLException", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private void login(){
        PreparedStatement preparedStatement;
        ResultSet resultSet;
        String loginId, password;
        boolean noUsersFound, isLibrarian;

        loginId = loginField.getText();
        password = String.valueOf(passwordField.getPassword());
        this.loginId = loginId;

        if (loginId.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "You must fill all the required fields.", "Null Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            preparedStatement = con.prepareStatement(PATRON_QUERY);
            preparedStatement.setString(1, loginId);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();

            noUsersFound = !resultSet.next();

            if(noUsersFound){
                    JOptionPane.showMessageDialog(rootPane, "Invalid login ID or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
            }

            preparedStatement = con.prepareStatement(LIBRARIAN_QUERY);
            preparedStatement.setString(1, loginId);
            resultSet = preparedStatement.executeQuery();

            isLibrarian = resultSet.next();

            if(isLibrarian)
                    showSelectModeInterface();
            else
                    showPatronFrame();

            preparedStatement.close();
            resultSet.close();
        }
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, e.getMessage(), 
            "SQLException", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSelectModeInterface(){
        Object[] modeInterfaceOptions;
        String librarian, patron;
        final int AS_LIBRARIAN, AS_PATRON;

        librarian = "Librarian";
        AS_LIBRARIAN = 0;

        patron = "Patron";
        AS_PATRON = 1;

        modeInterfaceOptions = new Object[]{librarian, patron};

        JOptionPane selectModeInterfacePane = new JOptionPane("Select the mode interface:\n", JOptionPane.INFORMATION_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, modeInterfaceOptions);

        JDialog dialog = new JDialog(this, "Librarian Detected", true);
        dialog.setContentPane(selectModeInterfacePane);

        selectModeInterfacePane.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (JOptionPane.VALUE_PROPERTY.equals(event.getPropertyName())){
                    if (selectModeInterfacePane.getValue().equals(modeInterfaceOptions[AS_LIBRARIAN])){
                        selectModeInterfacePane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        showLibrarianFrame();
                    }
                    else if (selectModeInterfacePane.getValue().equals(modeInterfaceOptions[AS_PATRON])){
                        selectModeInterfacePane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        showPatronFrame();
                    }
                }
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(rootPane);
        dialog.setVisible(true);
    }

    private void showPatronFrame(){
        PatronFrame patron = new PatronFrame(con, loginId);
        patron.setSize(900,600);
        patron.setResizable(false);
        patron.setLocationRelativeTo(null);
        patron.setVisible(true);
        dispose();
    }

    private void showLibrarianFrame(){
        LibrarianFrame librarian = new LibrarianFrame(con,loginId);
        librarian.setSize(900,600);
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
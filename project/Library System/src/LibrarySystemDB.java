import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class LibrarySystemDB implements AccessCredentials{
    public static void main(String[] args) {
        LoginFrame loginFrame = null;
		
        try {
            // initialize database driver
            Class.forName("oracle.jdbc.OracleDriver");
            
            // attempt to get connection to the database
            Connection con = DriverManager.getConnection(DATABASE_HOST, DATABASE_USERNAME, DATABASE_PASSWORD);
            
            // connection checker
            System.out.println("Connected successfully.");

            Statement statement = con.createStatement();
            statement.executeQuery("SELECT PATRON_ID_SEQ.NEXTVAL, TRANSACTION_ID_SEQ.NEXTVAL FROM DUAL");

            loginFrame = new LoginFrame(con);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            loginFrame.setSize(300,200);
            loginFrame.setResizable(false);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        }
        catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(loginFrame,"SQLException:\n" 
            + e.getMessage(),"Exception",JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(loginFrame,e.getMessage(),
            "Exception",JOptionPane.ERROR_MESSAGE);
        }
    }
}

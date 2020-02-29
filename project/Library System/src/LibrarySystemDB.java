import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class LibrarySystemDB implements AccessCredentials{
    public static void main(String[] args) {
        LoginFrame login = null;

        try {
            // initialize database driver
            Class.forName("oracle.jdbc.OracleDriver");
            
            // attempt to get connection to the database
            Connection con = DriverManager.getConnection(DATABASE_HOST, DATABASE_USERNAME, DATABASE_PASSWORD);
            
            // create a Statement object for conveying SQL statements
            // Statement statement = con.createStatement();
            
            // connection checker
            System.out.println("Connected successfully.");

            login = new LoginFrame(con);

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            login.setSize(300,200);
            login.setResizable(false);
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        }
        catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(login,"SQLException:\n" 
            + e.getMessage(),"Exception",JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(login,e.getMessage(),
            "Exception",JOptionPane.ERROR_MESSAGE);
        }
    }
}

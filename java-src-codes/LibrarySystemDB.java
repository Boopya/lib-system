import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class LibrarySystemDB implements AccessCredentials{
    public static void main(String[] args) {
        try {
            // initialize database driver
            Class.forName("oracle.jdbc.OracleDriver");
            
            // attempt to get connection to the database
            Connection con = DriverManager.getConnection(DATABASE_HOST, DATABASE_USERNAME, DATABASE_PASSWORD);
            
            // create a Statement object for conveying SQL statements
            Statement statement = con.createStatement();
            
            // connection checker
            System.out.println("Connected successfully.");
        }
        catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

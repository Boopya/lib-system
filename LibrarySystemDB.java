import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LibrarySystemDB implements AccessCredentials{
    public static void main(String[] args) {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection con = DriverManager.getConnection(DATABASE_HOST, DATABASE_USERNAME, DATABASE_PASSWORD);
            System.out.println("Connected successfully.");
        }
        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

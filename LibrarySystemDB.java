import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LibrarySystemDB implements AccessCredentials{
    public static void main(String[] args) {
        try {
            Connection con = DriverManager.getConnection(DATABASE_HOST, DATABASE_USERNAME, DATABASE_PASSWORD);
            System.out.println("Connected successfully.");
        }
        catch (SQLException e) {
                System.out.println(e.getMessage());
        }
    }
}

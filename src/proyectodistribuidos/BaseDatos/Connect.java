package proyectodistribuidos.BaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private static Connection conn = null;

    /**
     * Connect to the database
     */
    public static Connection connect() {
        try {
            // Check if the connection is already established
            if (conn == null || conn.isClosed()) {
                // Database parameters
                String dbUrl = "jdbc:sqlite:BasesDatos.db";
                // Create a connection to the database
                conn = DriverManager.getConnection(dbUrl);
                System.out.println("Connection to SQLite has been established.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    /**
     * Disconnect from the database
     */
    public static void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection to SQLite has been closed.");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Connection connection = connect();
        // Perform database operations here using the 'connection' object

        // Close the connection when done
        disconnect();
    }
}

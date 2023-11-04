package proyecto2.BaseDatos;

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
                String dbUrl = "jdbc:sqlite:./src/main/java/proyecto2/BaseDatos/BaseDatos.db";
                // Create a connection to the database
                conn = DriverManager.getConnection(dbUrl);
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
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

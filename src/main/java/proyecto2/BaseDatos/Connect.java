package proyecto2.BaseDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {
    private static Connection conn = null;

    /**
     * Conectar a la base de datos MySQL usando variables de entorno
     */
    public static Connection connect() {
        try {
            String dbHost = System.getenv("DB_HOST");
            String dbPort = System.getenv("DB_PORT");
            String dbName = System.getenv("DB_NAME");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            System.out.println("variables de entorno:");
            System.out.println("DB_HOST: " + dbHost);
            System.out.println("DB_PORT: " + dbPort);
            System.out.println("DB_NAME: " + dbName);
            System.out.println("DB_USER: " + dbUser);
            System.out.println("DB_PASSWORD: " + dbPassword);

            String url = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;

            conn = DriverManager.getConnection(url, dbUser, dbPassword);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Desconectar de la base de datos
     */
    public static void disconnect() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

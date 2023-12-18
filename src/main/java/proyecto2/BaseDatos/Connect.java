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
            // Cargar el controlador JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Obtener las variables de entorno para la conexión a MySQL
            String dbUrl = System.getenv("DB_URL");
            String usuario = System.getenv("DB_USER");
            String contraseña = System.getenv("DB_PASSWORD");

            // Verificar si la conexión ya está establecida
            if (conn == null || conn.isClosed()) {
                // Crear la conexión a la base de datos MySQL
                conn = DriverManager.getConnection(dbUrl, usuario, contraseña);
            }
        } catch (ClassNotFoundException | SQLException e) {
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

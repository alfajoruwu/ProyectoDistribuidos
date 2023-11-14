package proyecto2.Servidor;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import proyecto2.BaseDatos.Connect;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;

public class MainServidor {
    private Observable observable;
    private java.util.HashMap<String, ConexionServidor> usuarios;

    public static void main(String[] args) {

        new MainServidor();
    }

    public MainServidor() {
        try {

            this.observable = new Observable();
            usuarios = new java.util.HashMap<String, ConexionServidor>();
            System.out.println("Servidor iniciado");
            while (true) {
                ServerSocket socketServidor = new ServerSocket(5000);

                Socket cliente = socketServidor.accept();

                String usuario = Constantes.Nombres.USUARIO_ANONIMO + " " + cliente.getPort();
                System.out.println("Conectado: " + usuario);
                Runnable nuevoCliente = new ConexionServidor(cliente, this, usuario);

                Thread hilo = new Thread(nuevoCliente);
                hilo.start();

                socketServidor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void ReiniciarContraseña(String usuario) {
        Connection connection = Connect.connect();

        try {
            System.out.println("rut si");
            // Obtener el rut del usuario de la base de datos
            String rut = "";

            // Verificar si se encontró el rut
            String sql = "SELECT rut FROM Usuarios WHERE Usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                rut = resultSet.getString("rut");
            }

            // Actualizar la contraseña en la base de datos
            sql = "UPDATE Usuarios SET Contraseña = ? WHERE Usuario = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, rut);
            preparedStatement.setString(2, usuario);
            preparedStatement.executeUpdate();

            System.out.println("Contraseña reiniciada correctamente para el usuario: " + usuario);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public Constantes.Canales validarUsuario(String usuario, String contraseña) {
        if (getUsuario(usuario) != null) {
            return null;
        }
        Connection connection = Connect.connect();
        String rol = null;

        try {
            String sql = "SELECT rol FROM Usuarios WHERE rut = ? AND Contraseña = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, contraseña);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                rol = resultSet.getString("rol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }

        // Verificar si se encontró un rol
        if (rol != null) {
            System.out.println("Usuario válido. Rol: " + rol);
            if (rol.equals("administrativo")) {
                return Constantes.Canales.AUXILIAR;
            }
            Constantes.Canales canal = Constantes.Canales.valueOf(rol.toUpperCase());
            return canal;
        } else {
            System.out.println("Usuario no válido.");
            return null;
        }
    }

    public String getHistorial(String usuario) {
        if (getUsuario(usuario) == null) {
            if (!usuario.startsWith(Constantes.Nombres.USUARIO_ANONIMO.toString())) {
                System.err.println("gethistorial: usuario no conectado: " + usuario);
            }
            return "";
        }
        Connection connection = Connect.connect();
        String historial = "";
        try {
            String sql = "SELECT fecha,hora,emisor,mensaje FROM Mensajes WHERE Usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                historial += resultSet.getString("fecha");
                historial += " ";
                historial += resultSet.getString("hora");
                historial += " ";
                historial += resultSet.getString("emisor");
                historial += " ";
                historial += resultSet.getString("mensaje");
                historial += "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
        return historial;
    }

    public String getEstilos(String usuario) {
        if (getUsuario(usuario) == null) {
            if (!usuario.startsWith(Constantes.Nombres.USUARIO_ANONIMO.toString())) {
                System.err.println("gethistorial: usuario no conectado: " + usuario);
            }
            return "";
        }
        Connection connection = Connect.connect();
        String historial = "";
        try {
            String sql = "SELECT estilo FROM Mensajes WHERE Usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                historial += resultSet.getString("estilo");
                historial += "\n";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
        return historial;

    }

    public void setHistorial(String usuario, String historial, String estilos) {
        if (getUsuario(usuario) == null) {
            if (!usuario.startsWith(Constantes.Nombres.USUARIO_ANONIMO.toString())) {
                System.err.println("setHistorial: usuario no conectado: " + usuario);
            }
            return;
        }

        Connection connection = Connect.connect();

        System.out.println(historial);

        try {
            // borrar historial si existe
            String sql = "DELETE FROM Mensajes WHERE Usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.executeUpdate();

            // agregar historial
            String[] mensajes = historial.split("\n");
            String[] estilosMensajes = estilos.split("\n");
            String fecha;
            String hora;
            String emisor;
            String mensajew;
            if (mensajes.length == 0 || mensajes[0].isEmpty()) {
                return;
            }

            for (int i = 0; i < estilosMensajes.length; i++) {
                String mensaje = mensajes[i];
                String estilo = estilosMensajes[i];
                System.out.println(mensaje);
                String[] partes = mensaje.split(" ", 4);
                fecha = partes[0];
                hora = partes[1];
                emisor = partes[2];
                mensajew = partes[3];
                ingresarMensajeBD(connection, usuario, fecha, hora, emisor, mensajew, estilo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
    }

    // incert
    public void CrearUsuario(String Nombre, String Rut, String Correo, String Rol) {
        String sql = "INSERT INTO Usuarios(Usuario,Contraseña,rol,rut,Correo) VALUES(?,?,?,?,?)";

        try (Connection conn = Connect.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, Nombre);
            pstmt.setString(2, Rut);
            pstmt.setString(3, Rol);
            pstmt.setString(4, Rut);
            pstmt.setString(5, Correo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public void agregarUsuario(String usuario, ConexionServidor conexion) {
        System.out.println("Agregando usuario " + usuario);
        usuarios.put(usuario, conexion);
        actualizarUsuarios(usuario);
    }

    // usuarios conectados
    public ConexionServidor getUsuario(String usuario) {
        return usuarios.get(usuario);
    }

    public void removerUsuario(String usuario) {
        System.out.println("Removiendo usuario " + usuario);
        usuarios.remove(usuario);
        actualizarUsuarios(usuario);
    }

    // mensaje privado
    public void enviarMensaje(Mensaje mensaje, String usuario) {
        ConexionServidor conexion = usuarios.get(usuario);
        if (conexion != null) {
            conexion.recibirMensaje(mensaje);
        }
    }

    // notificaciones
    public void agregarCanalUsuario(Constantes.Canales canal, String usuario) {
        PropertyChangeListener observador = usuarios.get(usuario);
        this.observable.agregarObservador(canal, observador);
    }

    public void removerCanalUsuario(Constantes.Canales canal, String usuario) {
        if (canal == null) {
            return;
        }
        PropertyChangeListener observador = usuarios.get(usuario);
        this.observable.removerObservador(canal, observador);
        System.out.println("Removiendo canal " + canal + " a usuario " + usuario);
    }

    public void notificar(Constantes.Canales tipo, Object valorNuevo) {
        this.observable.notificar(tipo, valorNuevo);
        System.out.println("Notificando a observadores de canal " + tipo);
    }

    public void CambiarContraseña(String nuevaContraseña, String usuario) {
        Connection connection = Connect.connect();

        try {
            // Actualizar la contraseña en la base de datos
            String sql = "UPDATE Usuarios SET Contraseña = ? WHERE Usuario = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, nuevaContraseña);
            preparedStatement.setString(2, usuario);
            preparedStatement.executeUpdate();

            System.out.println("Contraseña cambiada correctamente para el usuario: " + usuario);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }

    }

    public int primerinicio(String usuario) {
        System.out.println("consultar primer inicio");
        String rut = "";
        Connection connection = Connect.connect();
        String contraseña = "";

        try {
            String sql = "select Usuario,rut,Contraseña FROM Usuarios where Usuario == ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                rut = resultSet.getString("rut");
                contraseña = resultSet.getString("Contraseña");
                System.out.println("rut: " + rut + " contraseña: " + contraseña);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }

        if (rut.equals(contraseña)) {
            System.out.println("primera vez");
            return 1;
        } else {
            System.out.println("no primer inicio");
            return 0;
        }

    }

    public ArrayList<String> ObtenerUsuarios() {
        ArrayList<String> usuarios = new ArrayList<String>();

        Connection connection = Connect.connect();

        try {
            String sql = "select Usuario from Usuarios";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                usuarios.add(resultSet.getString("Usuario"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
        return usuarios;
    }

    public String getMedicosConectados(Constantes.Canales canal) {
        if (canal == Constantes.Canales.AUXILIAR) {
            return null;
        }
        String usuariosConectados = "";
        for (String usuario : usuarios.keySet()) {
            if (usuarios.get(usuario).getCanal() == Constantes.Canales.MEDICO) {
                usuariosConectados += usuario + ",";
            }
        }
        return usuariosConectados;
    }

    public void ingresarMensajeBD(Connection connection, String usuario, String fecha, String hora, String emisor,
            String mensaje, String estilo) {

        try {
            // Insertar el mensaje en la base de datos
            String sql = "INSERT INTO Mensajes (usuario, fecha, hora, emisor, mensaje, estilo) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, fecha);
            preparedStatement.setString(3, hora);
            preparedStatement.setString(4, emisor);
            preparedStatement.setString(5, mensaje);
            preparedStatement.setString(6, estilo);
            preparedStatement.executeUpdate();

            System.out.println("Mensaje ingresado en la base de datos correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void actualizarUsuarios(String usuario) {
        for (ConexionServidor conexion : usuarios.values()) {
            if (!conexion.getUsuario().equals(usuario)) {
                conexion.actualizarContactos();
            }
        }
    }
}

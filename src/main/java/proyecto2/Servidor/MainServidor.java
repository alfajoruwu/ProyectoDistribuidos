package proyecto2.Servidor;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            String sql = "SELECT mensaje FROM Mensajes WHERE idUsuarioenvia = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                historial = resultSet.getString("mensaje");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
        return historial;
    }

    public void setHistorial(String usuario, String historial) {
        if (getUsuario(usuario) == null) {
            if (!usuario.startsWith(Constantes.Nombres.USUARIO_ANONIMO.toString())) {
                System.err.println("setHistorial: usuario no conectado: " + usuario);
            }
            return;
        }
        Connection connection = Connect.connect();
        try {
            // borrar historial si existe
            String sql = "DELETE FROM Mensajes WHERE idUsuarioenvia = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.executeUpdate();

            // agregar historial
            sql = "INSERT INTO Mensajes (idUsuarioenvia, mensaje) VALUES (?, ?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, historial);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
    }

    //incert 
    public void CrearUsuario(String Nombre,String Rut,String Correo,String Rol){
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
    }

    public int primerinicio(String usuario){
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
                System.out.println("rut: "+rut+" contraseña: "+contraseña);
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }

        if(rut.equals(contraseña)){
            System.out.println("primera vez");
            return 1;
        }
        else{
            System.out.println("no primer inicio");
            return 0;
        }

        
    } 

    public String getUsuariosConectados(Constantes.Canales canal) {
        String usuariosConectados = "";
        for (String usuario : usuarios.keySet()) {
            if (usuarios.get(usuario).getCanal() == canal) {
                usuariosConectados += usuario + ",";
            }
        }
        return usuariosConectados;
    }

    public void actualizarUsuarios(String usuario) {
        for (ConexionServidor conexion : usuarios.values()) {
            if (!conexion.getUsuario().equals(usuario)) {
                conexion.actualizarContactos();
            }
        }
    }
}

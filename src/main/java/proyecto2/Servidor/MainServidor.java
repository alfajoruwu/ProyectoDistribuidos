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
import proyecto2.Mensajeria.Constantes.Nombres;
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
                System.out.println("Conectado: " + Constantes.Nombres.USUARIO_ANONIMO + " " + cliente.getPort());

                String usuario = Nombres.USUARIO_ANONIMO + " " + cliente.getPort();
                Runnable nuevoCliente = new ConexionServidor(cliente, this, usuario);

                Thread hilo = new Thread(nuevoCliente);
                hilo.start();

                socketServidor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // temporalmente despues morira dlskafjdsf
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

    public void agregarUsuario(String usuario, ConexionServidor conexion) {
        usuarios.put(usuario, conexion);
    }

    // usuarios conectados
    public ConexionServidor getUsuario(String usuario) {
        return usuarios.get(usuario);
    }

    public void removerUsuario(String usuario) {
        System.out.println("Removiendo usuario " + usuario);
        usuarios.remove(usuario);
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

}

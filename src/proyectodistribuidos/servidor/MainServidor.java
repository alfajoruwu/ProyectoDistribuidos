package proyectodistribuidos.servidor;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
                System.out.println("Cliente conectado: " + cliente.getPort());

                String canal = Observable.CANAL_MEDICOS;

                // TODO: Pedir y validar usuario (hay que sincronizarlo con el cliente)
                String usuario = "Usuario Test" + cliente.getPort();
                Runnable nuevoCliente = new ConexionServidor(cliente, this, canal, usuario);

                usuarios.put(usuario, (ConexionServidor) nuevoCliente);
                agregarCanalUsuario(canal, usuario);
                Thread hilo = new Thread(nuevoCliente);
                hilo.start();

                socketServidor.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // usuarios conectados
    public ConexionServidor getUsuario(String usuario) {
        return usuarios.get(usuario);
    }

    public void removerUsuario(String usuario) {
        usuarios.remove(usuario);
    }

    // notificaciones
    public void agregarCanalUsuario(String canal, String usuario) {
        PropertyChangeListener observador = usuarios.get(usuario);
        this.observable.agregarObservador(canal, observador);
    }

    public void removerCanalUsuario(String canal, String usuario) {
        PropertyChangeListener observador = usuarios.get(usuario);
        this.observable.removerObservador(canal, observador);
        System.out.println("Removiendo canal " + canal + " a usuario " + usuario);
    }

    public void notificar(String tipo, Object valorNuevo) {
        this.observable.notificar(tipo, valorNuevo);
    }

}

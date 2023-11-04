package proyecto2.Servidor;

import java.net.Socket;
import java.net.SocketException;

import proyecto2.Mensajeria.Mensaje;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConexionServidor implements Runnable, PropertyChangeListener {
    private String usuario;
    private String canal;
    private Socket socket;
    private MainServidor servidor;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;

    public ConexionServidor(Socket socket, MainServidor servidor, String usuario) {
        this.socket = socket;
        this.servidor = servidor;
        this.usuario = usuario;
        try {
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al crear los flujos de entrada y salida");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            Mensaje mensaje;
            while (true) {
                mensaje = (Mensaje) entrada.readObject();

                if (mensaje.getTipoDestinatario().equals(Mensaje.PREFIJO_CANAL)) {
                    servidor.notificar(mensaje.getDestinatario(), mensaje);

                } else if (mensaje.getTipoDestinatario().equals(Mensaje.PREFIJO_USUARIO)) {
                    System.out.println(mensaje.getEmisor() +
                            "-> Enviando mensaje a usuario " +
                            mensaje.getDestinatario());

                } else if (mensaje.getTipoDestinatario().equals(Mensaje.PREFIJO_LOGIN)) {
                    Mensaje respuesta = new Mensaje();
                    respuesta.setEmisor(Mensaje.SERVIDOR);
                    respuesta.setDestinatario(Mensaje.PREFIJO_USUARIO, mensaje.getEmisor());
                    System.out.println(mensaje.getEmisor() + " -> login ");
                    String canal = servidor.validarUsuario(mensaje.getEmisor(), mensaje.getMensaje());
                    if (canal != null) {
                        this.canal = canal;
                        this.usuario = mensaje.getEmisor();
                        servidor.agregarUsuario(mensaje.getEmisor(), this);
                        servidor.agregarCanalUsuario(canal, mensaje.getEmisor());
                        respuesta.setMensaje(Mensaje.LOGIN_EXITOSO + ":" + canal);
                    } else {
                        respuesta.setMensaje(Mensaje.LOGIN_FALLIDO + ":null");
                    }
                    salida.writeObject(respuesta);

                } else if (mensaje.getTipoDestinatario().equals(Mensaje.PREFIJO_LOGOUT)) {
                    System.out.println(mensaje.getEmisor() + " -> logout ");
                    servidor.removerCanalUsuario(canal, usuario);
                    servidor.removerUsuario(usuario);
                    this.canal = MainServidor.USIARIO_ANONIMO + socket.getPort();
                    this.usuario = MainServidor.USIARIO_ANONIMO + socket.getPort();
                } else {
                    System.err.println(mensaje.getEmisor() +
                            " -> Error al enviar mensaje a " +
                            mensaje.getDestinatarioFull() + " -> Destinatario no reconocido");
                }
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado: " + usuario);
            servidor.removerCanalUsuario(canal, usuario);
            servidor.removerUsuario(usuario);
            if (e.getClass() != SocketException.class) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(this.canal)) {
            try {
                Mensaje mensaje = (Mensaje) evt.getNewValue();
                salida.writeObject(mensaje);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error al notificar: " + evt.getPropertyName());
            System.err.println("Tipo de usuario: " + this.canal);
        }
    }
}

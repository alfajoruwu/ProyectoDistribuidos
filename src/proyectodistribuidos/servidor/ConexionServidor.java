package proyectodistribuidos.servidor;

import java.net.Socket;

import proyectodistribuidos.mensajeria.Mensaje;

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

    public ConexionServidor(Socket socket, MainServidor servidor, String canal, String usuario) {
        this.socket = socket;
        this.servidor = servidor;
        this.usuario = usuario;
        this.canal = canal;
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

                    if (servidor.validarUsuario(mensaje.getEmisor(), mensaje.getMensaje())) {
                        respuesta.setMensaje(Mensaje.LOGIN_EXITOSO);
                    } else {
                        respuesta.setMensaje(Mensaje.LOGIN_FALLIDO);
                    }
                    salida.writeObject(respuesta);

                } else {
                    System.err.println(mensaje.getEmisor() +
                            " -> Error al enviar mensaje a " +
                            mensaje.getDestinatarioFull() + " -> Destinatario no reconocido");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Cliente desconectado: " + socket.getPort());
            servidor.removerCanalUsuario(canal, usuario);
            servidor.removerUsuario(usuario);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Observable.CANAL_MEDICOS) && this.canal.equals("medico")) {
            try {
                Mensaje mensaje = (Mensaje) evt.getNewValue();
                salida.writeObject(mensaje);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (evt.getPropertyName().equals(Observable.CANAL_ADMISION) && this.canal.equals("admision")) {
            System.out.println("Admision: " + evt.getNewValue());
        } else if (evt.getPropertyName().equals(Observable.CANAL_PABELLON) && this.canal.equals("pabellon")) {
            System.out.println("Pabellon: " + evt.getNewValue());
        } else if (evt.getPropertyName().equals(Observable.CANAL_EXAMENES) && this.canal.equals("examenes")) {
            System.out.println("Examenes: " + evt.getNewValue());
        } else if (evt.getPropertyName().equals(Observable.CANAL_AUXILIAR) && this.canal.equals("auxiliar")) {
            System.out.println("Auxiliar: " + evt.getNewValue());
        } else {
            System.err.println("Error al notificar: " + evt.getPropertyName());
            System.err.println("Tipo de usuario: " + this.canal);
        }
    }
}

package proyecto2.cliente;

import java.net.Socket;

import proyecto2.mensajeria.Mensaje;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorCliente implements Runnable, ActionListener {
    private String usuario;
    private String canal;
    private PanelCliente panel;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;

    public ControladorCliente(Socket socket, PanelCliente panel, String usuario, String canal) {
        this.usuario = usuario;
        this.canal = canal;
        this.panel = panel;
        panel.addActionListener(this);
        panel.addMensajePrivadoListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Enviando mensaje privado");
            }
        });
        try {
            System.out.println("creando entrada y salida");
            salida = new ObjectOutputStream(socket.getOutputStream()); // importante que sea primero salida
            System.out.println("salida creada");
            entrada = new ObjectInputStream(socket.getInputStream());
            System.out.println("entrada creada");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        panel.addTexto("Bienvenido al chat\n");
        try {
            Mensaje mensaje;
            while (true) {
                mensaje = (Mensaje) entrada.readObject();
                panel.addTexto(mensaje.getEmisor() + ": " + mensaje.getMensaje());
                panel.addTexto("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(this.usuario);
        mensaje.setMensaje(panel.getTexto());
        mensaje.setDestinatario(Mensaje.PREFIJO_CANAL, this.canal);
        try {
            salida.writeObject(mensaje);
        } catch (Exception excepcion) {
            excepcion.printStackTrace();
        }
    }
}

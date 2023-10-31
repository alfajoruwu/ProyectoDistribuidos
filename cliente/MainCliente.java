package proyecto2.cliente;

import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import proyecto2.servidor.Observable;

public class MainCliente {
    private String usuario = "Usuario Test";
    private String canal = Observable.CANAL_MEDICOS;
    private Socket socket;
    private PanelCliente panel;

    public static void main(String[] args) {
        new MainCliente();
    }

    public MainCliente() {
        try {
            socket = new Socket("localhost", 5000);
            usuario += socket.getLocalPort();

            ventana();
            // TODO: Pedir y validar usuario y canal (hay que sincronizarlo con el servidor)
            new ControladorCliente(socket, panel, usuario, canal);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ventana() {
        JFrame frame = new JFrame("Cliente :" + usuario);
        panel = new PanelCliente(frame);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(600, 300);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}

package proyecto2.Vistas;

import javafx.stage.Stage;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// clase padre de las vistas de los usuarios (medico, administrativo, administrador)
public abstract class VistaPadre implements Runnable {
    protected Stage stage;
    protected Scene scene;
    protected String usuario;

    protected Socket socket;
    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;

    @FXML
    protected TextField messageField;
    @FXML
    protected TextArea chatArea;

    protected Constantes.Canales canal;

    protected Thread hilo;

    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario,
            Constantes.Canales canal, String historial) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;
        this.canal = canal;
        this.setHistorial(historial);
    }

    public void irAVistaLogin(ActionEvent event) throws IOException {
        try {
            hilo.interrupt();
        } catch (Exception e) {
            System.err.println("Error al interrumpir el hilo de escucha de mensajes");
            e.printStackTrace();
        }
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador para poder enviarle la información
        Login login = loader.getController();
        login.setInformacion("localhost", "5000");

        try {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(usuario);
            mensaje.setDestinatario(Constantes.TipoDestino.LOGOUT, Constantes.Nombres.SERVIDOR.toString());

            salida.writeObject(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar el mensaje de logout");
            e.printStackTrace();
        }
        this.entrada.close();
        this.salida.close();
        this.socket.close();

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    protected void borrarHistorial() {
        chatArea.clear();
    }

    private void setHistorial(String historial) {
        chatArea.setText(historial);
    }

    @FXML
    protected void handleSendButtonAction() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(this.usuario);
            mensaje.setMensaje(message);
            mensaje.setDestinatario(Constantes.TipoDestino.CANAL, canal);
            messageField.clear();
            try {
                salida.writeObject(mensaje);
            } catch (Exception excepcion) {
                excepcion.printStackTrace();
            }
        }
    }
}

package proyecto2.Vistas;

import javafx.stage.Stage;
import proyecto2.Mensajeria.Mensaje;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

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

    protected Thread hilo;

    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;
    }

    public void irAVistaLogin(ActionEvent event) throws IOException {
        hilo.interrupt();
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador para poder enviarle la información
        Login login = loader.getController();
        login.setInformacion("localhost", "5000");

        try {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(usuario);
            mensaje.setDestinatario(Mensaje.PREFIJO_LOGOUT, null);
            mensaje.setMensaje(this.getHistorial());

            salida.writeObject(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar el mensaje de logout");
            e.printStackTrace();
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    // el historial lo implementan las vistas hijas
    // (porque usan @FXML y no se si lo podria declarar aqui)
    public abstract String getHistorial();

    public abstract void setHistorial(String historial);

    public abstract void borrarHistorial();
}

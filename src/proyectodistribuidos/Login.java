
package proyectodistribuidos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import proyectodistribuidos.mensajeria.Mensaje;
import javafx.scene.Node;


public class Login implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;

    @FXML
    private Button ingresar;

    @FXML
    private TextField nombreUsuario;

    @FXML
    private PasswordField contraseña;

    @FXML
    public void irAVistaMedico(ActionEvent event) throws IOException {
        String usuario = nombreUsuario.getText();
        String contraseña = this.contraseña.getText();
        if (validarUsuario(usuario, contraseña)) {
            root = FXMLLoader.load(getClass().getResource("Vistas/FXMLVistaMedico.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private Label mensajeError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validarUsuario(String usuario, String contraseña) {
        try {

            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(usuario);
            // el destinatario da igual (solo si tiene el prefijo Login)
            mensaje.setDestinatario(Mensaje.PREFIJO_LOGIN, "Servidor");
            mensaje.setMensaje(contraseña);

            this.salida.writeObject(mensaje);

            Mensaje respuesta = (Mensaje) this.entrada.readObject();

            if (respuesta.getMensaje().equals(Mensaje.LOGIN_EXITOSO)) {
                return true;
            } else if (respuesta.getMensaje().equals(Mensaje.LOGIN_FALLIDO)) {
                mensajeError.setText("Usuario o contraseña incorrectos");
            } else {
                mensajeError.setText("Respuesta inesperada del servidor");
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

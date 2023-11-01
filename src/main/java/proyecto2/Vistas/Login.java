
package proyecto2.Vistas;

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
import proyecto2.Mensajeria.Mensaje;
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
        String canal = validarUsuario(usuario, contraseña);
        if (canal != null) {
            // TODO llamar a la interfaz que corresponda

            // Cargar la interfaz gráfica
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLVistaMedico.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la clase FXMLVistaMedicoController
            FXMLVistaMedicoController medicoController = loader.getController();
            medicoController.enviarInformacion(socket, salida, entrada, usuario);

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

    public void enviarInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
    }

    @FXML
    public void irAVistaAdministracion(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("FXMLVistaAdministrativo.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private String validarUsuario(String usuario, String contraseña) {
        try {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(usuario);
            // el destinatario da igual (solo si tiene el prefijo Login)
            mensaje.setDestinatario(Mensaje.PREFIJO_LOGIN, "Servidor");
            mensaje.setMensaje(contraseña);

            this.salida.writeObject(mensaje);

            Mensaje respuesta = (Mensaje) this.entrada.readObject();
            String canal = respuesta.getMensaje().split(":")[1];

            if (respuesta.getMensaje().startsWith(Mensaje.LOGIN_EXITOSO)) {
                return canal;
            } else if (respuesta.getMensaje().startsWith(Mensaje.LOGIN_FALLIDO)) {
                mensajeError.setText("Usuario o contraseña incorrectos");
            } else {
                mensajeError.setText("Respuesta inesperada del servidor");
            }
            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private Button irAVistaAdministrador;

    @FXML
    public void irAVistaAdministrador(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("FXMLVistaAdministrador.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

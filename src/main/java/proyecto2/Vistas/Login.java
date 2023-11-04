
package proyecto2.Vistas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
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
import proyecto2.Servidor.Observable;
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
    public void irAVistaMedico(ActionEvent event) throws IOException { // TODO: nombre a ingresar o algo asi
        String usuario = nombreUsuario.getText();
        String contrasenna = this.contraseña.getText();
        String canal = validarUsuario(usuario, contrasenna);
        if (canal != null) {
            if (canal.equals(Observable.CANAL_MEDICOS)) {
                irVista(event, "FXMLVistaMedico.fxml", usuario);
            } else if (canal.equals(Observable.CANAL_ADMISION) || canal.equals(Observable.CANAL_AUXILIAR)
                    || canal.equals(Observable.CANAL_EXAMENES) || canal.equals(Observable.CANAL_PABELLON)) {
                irVista(event, "FXMLVistaAdministrativo.fxml", usuario);
            } else if (canal.equals(Observable.CANAL_ADMINISTRADOR)) {
                irVista(event, "FXMLVistaAdministrador.fxml", usuario);
            }
        }
    }

    private void irVista(ActionEvent event, String vista, String usuario) throws IOException {
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
        Parent root = loader.load();

        // Obtener el controlador de la vista
        VistaPadre controladorVista = loader.getController();
        controladorVista.setInformacion(socket, salida, entrada, usuario);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Label mensajeError;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setInformacion(String ip, String puerto) {
        try {
            this.socket = new Socket(ip, Integer.parseInt(puerto));
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            // Antes de leer el objeto, verifica que la conexión de red sigue siendo válida
            if (this.entrada == null) {
                System.err.println("El objeto de entrada no se ha inicializado");
                return null;
            }

            Object object = null;
            try {
                object = this.entrada.readObject();
            } catch (IOException e) {
                System.err.println("Error al leer el objeto: " + e.getMessage());
                return null;
            }

            if (!(object instanceof Mensaje)) {
                System.err.println("El objeto leído es: " + object.getClass().getName());
                throw new ClassCastException("El objeto leído no es de tipo Mensaje");
            }

            Mensaje respuesta = (Mensaje) object;

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

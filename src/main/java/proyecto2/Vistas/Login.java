
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
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import javafx.scene.Node;

public class Login implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String historial;

    @FXML
    private Button ingresar;

    @FXML
    private TextField nombreUsuario;

    @FXML
    private PasswordField contraseña;

    @FXML
    public void irAVistaMedico(ActionEvent event) throws IOException { // TODO: cambiar nombre
        String usuario = nombreUsuario.getText();
        String contrasenna = this.contraseña.getText();
        Constantes.Canales canal = validarUsuario(usuario, contrasenna);
        if (canal != null) {
            if (canal.equals(Constantes.Canales.MEDICO)) {
                PopUp(event);
                irVista(event, "FXMLVistaMedico.fxml", usuario, canal, historial);
            } else if (canal.equals(Constantes.Canales.ADMISION) || canal.equals(Constantes.Canales.AUXILIAR)
                    || canal.equals(Constantes.Canales.EXAMENES) || canal.equals(Constantes.Canales.PABELLON)) {
                irVista(event, "FXMLVistaAdministrativo.fxml", usuario, canal, historial);
            } else if (canal.equals(Constantes.Canales.ADMINISTRADOR)) {
                irVista(event, "FXMLVistaAdministrador.fxml", usuario, canal, historial);
            }
        }
    }

    private void irVista(ActionEvent event, String vista, String usuario, Constantes.Canales canal, String historial)
            throws IOException {
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource(vista));
        Parent root = loader.load();

        // Obtener el controlador de la vista
        VistaPadre controladorVista = loader.getController();
        controladorVista.setInformacion(socket, salida, entrada, usuario, canal, historial);

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
    
    private void PopUp(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLPopUpSetearContraseña.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(root1));  
        stage2.show();
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

    private Constantes.Canales validarUsuario(String usuario, String contraseña) {
        try {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(usuario);
            // el destinatario da igual (solo si tiene el prefijo Login)
            mensaje.setDestinatario(Constantes.TipoDestino.LOGIN, "Servidor");
            mensaje.setMensaje(contraseña);

            this.salida.writeObject(mensaje);

            // Antes de leer el objeto, verifica que la conexión de red sigue siendo válida
            if (this.entrada == null) {
                System.err.println("El objeto de entrada no se ha inicializado");
                return null;
            }
            Mensaje respuesta = (Mensaje) this.entrada.readObject();

            // quitar el prefijo de la respuesta
            String canal = respuesta.getMensaje().split(":")[1];
            if (respuesta.getMensaje().split(":").length > 2) {
                historial = respuesta.getMensaje().split(":", 3)[2];
            } else {
                historial = "";
            }

            if (respuesta.getMensaje().startsWith(Constantes.Respuestas.LOGIN_EXITOSO.toString())) {
                Constantes.Canales aux = Constantes.Canales.valueOf(canal);
                return aux;
            } else if (respuesta.getMensaje().startsWith(Constantes.Respuestas.LOGIN_FALLIDO.toString())) {
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

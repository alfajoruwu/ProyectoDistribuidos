
package proyectodistribuidos.Vistas;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class FXMLVistaAdministradorController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    private Label tituloEncabezadoAdministrador;

    @FXML
    private Button botonCrearUsuario;

    @FXML
    private Button botonMonitorearComunicaciones;

    @FXML
    private Button botonReiniciarContraseña;

    @FXML
    private TextArea chatArea;
    
    @FXML
    private TextField messageField;
    
    @FXML
    private Button sendButton;

    @FXML
    private Button botonSalir;

    @FXML
     public void irAVistaLogin(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/proyectodistribuidos/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    public void initialize() {
        
    }
    
    @FXML
    private void handleSendButtonAction() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText("Tú: " + message + "\n");
            messageField.clear();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}
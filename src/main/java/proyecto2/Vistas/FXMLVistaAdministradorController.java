
package proyecto2.Vistas;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLVistaAdministradorController extends VistaPadre implements Initializable {

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
    private Button sendButton;

    @FXML
    private Button botonSalir;

    @FXML
    public void irAVistaLogin(ActionEvent event) throws IOException {
        super.irAVistaLogin(event);
    }

    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatArea.setEditable(false);
    }

    @Override
    public void run() {

    }

    // historial
    @Override
    public String getHistorial() {
        return chatArea.getText();
    }

    @Override
    public void borrarHistorial() {
        chatArea.clear();
    }

    @Override
    public void setHistorial(String historial) {
        chatArea.setText(historial);
    }
}
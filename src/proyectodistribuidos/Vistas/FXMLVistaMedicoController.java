
package proyectodistribuidos.Vistas;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


public class FXMLVistaMedicoController implements Initializable {

    @FXML
    private Button botonPabellon;

    @FXML
    private Button botonAdmision;

    @FXML
    private Button botonExamenes;

    @FXML
    private Button botonAuxiliar;

    @FXML
    private Button botonMedicos;

    @FXML
    private TextArea chatArea;
    
    @FXML
    private TextField messageField;
    
    @FXML
    private Button sendButton;
    
    public void initialize() {
        // Inicializa tu controlador aquí
    }
    
    @FXML
    private void handleSendButtonAction() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText("Tú: " + message + "\n");
            messageField.clear();
            // Aquí puedes agregar lógica para enviar el mensaje a otros usuarios
        }
    }

    


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

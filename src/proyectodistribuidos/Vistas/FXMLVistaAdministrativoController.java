package proyectodistribuidos.Vistas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLVistaAdministrativoController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;


    @FXML
    private ListView<String> listaContactos;

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
    private Button botonEnviarMensaje;

    @FXML
    private TextField textoBuscarContacto;

    @FXML
    private Label tituloEncabezadoMedico;

    @FXML
    private Label chatGeneral;

    @FXML
    private Button botonSalidar;

    @FXML
     public void irAVistaLogin(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("/proyectodistribuidos/Login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private ObservableList<String> contactList = FXCollections.observableArrayList(
        "Pabellón",
        "Exámenes",
        "Médico 1",
        "Médico 2",
        "Médico 3"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaContactos.setItems(contactList);

        listaContactos.setCellFactory(TextFieldListCell.forListView(new StringConverter<String>() {
            @Override
            public String toString(String contact) {
                return contact;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }));

        textoBuscarContacto.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.toLowerCase();
            ObservableList<String> filteredList = FXCollections.observableArrayList();
            for (String contact : contactList) {
                if (contact.toLowerCase().contains(searchTerm)) {
                    filteredList.add(contact);
                }
            }
            listaContactos.setItems(filteredList);
        });
    }

    @FXML
    private void handleSendButtonAction() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            chatArea.appendText("Tú: " + message + "\n");
            messageField.clear();

        }
    }
}

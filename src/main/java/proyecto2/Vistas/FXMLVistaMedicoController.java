package proyecto2.Vistas;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import proyecto2.Mensajeria.Mensaje;
import proyecto2.Servidor.Observable;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLVistaMedicoController extends VistaPadre implements Initializable, Runnable {

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
        super.irAVistaLogin(event);
    }

    private ObservableList<String> contactList = FXCollections.observableArrayList(
            "Pabellón",
            "Exámenes",
            "Médico 1",
            "Médico 2",
            "Médico 3");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatArea.setEditable(false);
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

        Thread hilo = new Thread(this);
        hilo.start();
    }

    @FXML
    private void handleSendButtonAction() {
        // TODO: hay que borrar el usuario al salir
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(this.usuario);
            mensaje.setMensaje(message);
            mensaje.setDestinatario(Mensaje.PREFIJO_CANAL, Observable.CANAL_MEDICOS);
            try {
                salida.writeObject(mensaje);
            } catch (Exception excepcion) {
                excepcion.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        chatArea.appendText("Bienvenido al chat\n");
        try {
            Mensaje mensaje;
            while (true) {
                mensaje = (Mensaje) entrada.readObject();
                if (mensaje.getEmisor().equals(this.usuario)) {
                    chatArea.appendText("TU: " + mensaje.getMensaje());
                } else {
                    chatArea.appendText(mensaje.getEmisor() + ": " + mensaje.getMensaje());
                }
                chatArea.appendText("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

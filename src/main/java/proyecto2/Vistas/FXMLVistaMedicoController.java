package proyecto2.Vistas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLVistaMedicoController extends VistaPadre implements Initializable {

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
    private Button botonEnviarMensaje;

    @FXML
    private TextField textoBuscarContacto;

    @FXML
    private Label tituloEncabezadoMedico;

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

        hilo = new Thread(this);
        hilo.start();
    }

    @Override
    public void run() {
        try {
            while (!hilo.isInterrupted()) {
                Mensaje mensaje;
                mensaje = (Mensaje) entrada.readObject();
                if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.ACTUALIZAR_CONTACTOS)) {
                    String[] contactos = mensaje.getMensaje().split(",");
                    System.out.println("Contactos recibidos: " + mensaje.getMensaje());
                    Platform.runLater(() -> {
                        listaContactos.setItems(FXCollections.observableArrayList(contactos));
                    });
                    System.out.println("Contactos actualizados");
                } else {
                    if (mensaje.getEmisor().equals(this.usuario)) {
                        Platform.runLater(() -> {
                            chatArea.appendText("TU: " + mensaje.getMensaje());
                        });
                    } else {
                        Platform.runLater(() -> {
                            chatArea.appendText(mensaje.getEmisor() + ": " + mensaje.getMensaje());
                        });
                    }
                    Platform.runLater(() -> {
                        chatArea.appendText("\n");
                    });
                }
            }
        } catch (Exception e) {
            if (!hilo.isInterrupted()) {
                e.printStackTrace();
            }
        }
    }
}

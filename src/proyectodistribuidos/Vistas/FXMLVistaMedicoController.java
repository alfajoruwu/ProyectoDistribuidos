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
import proyectodistribuidos.Login;
import proyectodistribuidos.mensajeria.Mensaje;
import proyectodistribuidos.servidor.Observable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLVistaMedicoController implements Initializable, Runnable {

    private Stage stage;
    private Scene scene;

    private Socket socket;
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    private String usuario;

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
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyectodistribuidos/Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la clase Login
        Login loginController = loader.getController();
        loginController.setSocket(socket, salida, entrada); // Pasar el objeto Socket a la clase Login

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
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

    public void enviarInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;
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

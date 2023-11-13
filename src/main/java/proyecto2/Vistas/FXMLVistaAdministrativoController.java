package proyecto2.Vistas;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class FXMLVistaAdministrativoController extends VistaPadre implements Initializable {
    @FXML
    private ListView<String> listaContactos;

    @FXML
    private ListView<String> listaContactosCanal;

    @FXML
    private Button botonPabellon;

    @FXML 
    private TextField textoMensajePrivado;

    @FXML
    private TextField textoMensajePrivadoCanal;

    @FXML
    private Button botonEnviarMensaje;

    @FXML
    private Button botonEnviarMensajePrivado;

    @FXML
    private Button botonEnviarMensajeCanal;

    @FXML
    private TextField textoBuscarContacto;

    @FXML
    private TextField textoBuscadorCanal;

    @FXML
    private Label tituloEncabezadoMedico;

    @FXML
    private Label chatGeneral;

    @FXML
    private Label tituloBuscadorCanal;

    @FXML
    private Button botonSalidar;

    @FXML
    public void irAVistaLogin(ActionEvent event) throws IOException {
        super.irAVistaLogin(event);
    }

    private ObservableList<String> contactList = FXCollections.observableArrayList(
            "Médico 1",
            "Médico 2",
            "Médico 3");

    private ObservableList<String> contactListCanal = FXCollections.observableArrayList(
            "Auxiliar");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chatArea.setEditable(false);
        listaContactos.setItems(contactList);
        listaContactosCanal.setItems(contactListCanal);

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

        listaContactosCanal.setCellFactory(TextFieldListCell.forListView(new StringConverter<String>() {
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

        textoBuscadorCanal.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchTerm = newValue.toLowerCase();
            ObservableList<String> filteredList = FXCollections.observableArrayList();
        
            for (String contact : contactListCanal) {
                if (contact.toLowerCase().contains(searchTerm)) {
                    filteredList.add(contact);
                }
            }
        
            listaContactosCanal.setItems(filteredList);
        });

    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    @FXML
    public void enviarMensajePrivado(ActionEvent event) {
        String mensaje = textoMensajePrivado.getText();
        
        // Verifica si se ha seleccionado un contacto de la lista
        String usuarioSeleccionado = listaContactos.getSelectionModel().getSelectedItem();
        
        if (usuarioSeleccionado != null && !mensaje.isEmpty()) {
            Mensaje mensajeAEnviar = new Mensaje();
            mensajeAEnviar.setEmisor(usuario);
            mensajeAEnviar.setDestinatario(Constantes.TipoDestino.USUARIO, usuarioSeleccionado);
            mensajeAEnviar.setMensaje(mensaje);

            // Muestra el mensaje en el área de chat
            chatArea.appendText("Privado para " + usuarioSeleccionado + ": " + mensaje + "\n");
            
            try {
                salida.writeObject(mensajeAEnviar);
            } catch (Exception e) {
                System.err.println("Error al enviar el mensaje");
                e.printStackTrace();
            }
            
            textoMensajePrivado.clear();
        }
    }

    public void enviarMensajePrivadoCanal(ActionEvent event) {
        String mensaje = textoMensajePrivadoCanal.getText(); // Utiliza el campo de entrada correcto

        // Verifica si se ha seleccionado un contacto del canal
        String usuarioSeleccionado = listaContactosCanal.getSelectionModel().getSelectedItem(); // Utiliza la lista de canal

        if (usuarioSeleccionado != null && !mensaje.isEmpty()) {
            Mensaje mensajeAEnviar = new Mensaje();
            mensajeAEnviar.setEmisor(usuario);
            mensajeAEnviar.setDestinatario(Constantes.TipoDestino.USUARIO, usuarioSeleccionado); // Asegúrate de que el destino sea un usuario
            mensajeAEnviar.setMensaje(mensaje);

            // Muestra el mensaje en el área de chat del canal
            chatArea.appendText("Mensaje para el canal " + usuarioSeleccionado + ": " + mensaje + "\n");

            try {
                salida.writeObject(mensajeAEnviar);
            } catch (Exception e) {
                System.err.println("Error al enviar el mensaje");
                e.printStackTrace();
            }

            textoMensajePrivadoCanal.clear(); // Utiliza el campo de entrada correcto
        }
    }
}

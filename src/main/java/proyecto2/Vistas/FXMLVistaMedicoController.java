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
    private ListView<String> listaContactosCanal;

    @FXML
    private TextField textoBuscarContacto;

    @FXML
    private TextField textoMensajePrivado;

    @FXML
    private TextField textoMensajePrivadoCanal;

    @FXML
    private Label tituloEncabezadoMedico;

    @FXML
    private Button botonSalidar;

    @FXML
    private Button botonEnviarMensajePrivado;

    @FXML
    private Button botonEnviarMensaje;

    @FXML
    private Button botonEnviarMensajeCanal;

    @FXML
    private TextField textoBuscadorCanal;

    @FXML
    private Label tituloBuscadorCanal;

    @FXML
    public void irAVistaLogin(ActionEvent event) throws IOException {
        super.irAVistaLogin(event);
    }

    private ObservableList<String> contactList = FXCollections.observableArrayList();

    private ObservableList<String> contactListCanal = FXCollections.observableArrayList(
            "Pabellón",
            "Exámenes");

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
        
        }
        
        ));

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
                        contactList.clear();
                        for (String contacto : contactos) {
                            contactList.add(contacto);
                        }
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
    @FXML
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
    


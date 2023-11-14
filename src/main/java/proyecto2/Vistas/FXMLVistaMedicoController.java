package proyecto2.Vistas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.StringConverter;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import proyecto2.Mensajeria.TextoEnriquecido;

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

    @FXML
    public void handleSeleccionAuxiliar(MouseEvent event) {
        String usuarioSeleccionado = listaContactosCanal.getSelectionModel().getSelectedItem();

        if ("Auxiliar".equals(usuarioSeleccionado)) {
            mostrarMensajeEmergente("Mensaje para Auxiliar", "Recuerda añadir un motivo al mensaje");
        }
    }

    private ObservableList<String> contactList = FXCollections.observableArrayList();

    private ObservableList<String> contactListCanal = FXCollections.observableArrayList(
            "Pabellon",
            "Examenes",
            "Auxiliar");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaChatGeneral.setEditable(false);
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

        listaChatGeneral.setCellFactory(lv -> new ListCell<TextoEnriquecido>() {
            @Override
            protected void updateItem(TextoEnriquecido item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Text text = new Text(item.getTexto());
                    text.setStyle(item.getEstilo());
                    TextFlow textFlow = new TextFlow(text);
                    setGraphic(textFlow);
                }
            }
        });

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

    private void mostrarMensajeEmergente(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
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
                            if (!contacto.equals(usuario)) {
                                contactList.add(contacto);
                            }
                        }
                    });
                    System.out.println("Contactos actualizados");
                } else {
                    if (mensaje.getEmisor().equals(this.usuario)) {
                        if (mensaje.getTipoDestinatario() == Constantes.TipoDestino.USUARIO) {
                            Platform.runLater(() -> {
                                TextoEnriquecido textoEnriquecido = new TextoEnriquecido(
                                        mensaje.getFechaHora() + ": (privado) TU:  " + mensaje.getMensaje(),
                                        "-fx-fill: #ffff00; -fx-font-weight: bold;"); // TODO: fijar estilo definitivo
                                                                                      // (esto es cuando yo mando un
                                                                                      // mensaje privado)
                                listaChatGeneral.getItems()
                                        .add(textoEnriquecido);
                            });
                        } else if (mensaje.getTipoDestinatario() == Constantes.TipoDestino.CANAL
                                && mensaje.getDestinatario() != canal.toString()) {
                            Platform.runLater(() -> {
                                TextoEnriquecido textoEnriquecido = new TextoEnriquecido(
                                        mensaje.getFechaHora() + ": (canal) TU:  " + mensaje.getMensaje(),
                                        "-fx-fill: #00ffff; -fx-font-weight: bold;"); // TODO: fijar estilo definitivo
                                                                                      // (esto es cuando yo mando un
                                                                                      // mensaje a un canal externo)
                                listaChatGeneral.getItems()
                                        .add(textoEnriquecido);
                            });
                        }

                        else {
                            Platform.runLater(() -> {
                                TextoEnriquecido textoEnriquecido = new TextoEnriquecido(
                                        mensaje.getFechaHora() + ": TU:  " + mensaje.getMensaje(),
                                        "-fx-fill: #ff0000; -fx-font-weight: bold;"); // TODO: fijar estilo definitivo
                                                                                      // (esto es cuando yo mando un
                                                                                      // mensaje a mi canal)
                                listaChatGeneral.getItems()
                                        .add(textoEnriquecido);
                            });
                        }

                    } else {
                        if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.USUARIO)) {
                            TextoEnriquecido textoEnriquecido = new TextoEnriquecido(
                                    mensaje.getFechaHora() + ": (privado) " + mensaje.getEmisor() + ": "
                                            + mensaje.getMensaje(),
                                    "-fx-fill: #00ff00; -fx-font-weight: bold;"); // TODO: fijar estilo definitivo (esto
                                                                                  // es cuando yo recibo un mensaje
                                                                                  // privado)
                            Platform.runLater(() -> {
                                listaChatGeneral.getItems().add(textoEnriquecido);
                            });
                        } else {
                            Platform.runLater(() -> {
                                TextoEnriquecido textoEnriquecido = new TextoEnriquecido(
                                        mensaje.getFechaHora() + ": " + mensaje.getEmisor() + ": "
                                                + mensaje.getMensaje(),
                                        "-fx-fill: #0000ff; -fx-font-weight: bold;"); // TODO: fijar estilo definitivo
                                                                                      // (esto es cuando yo recibo un
                                                                                      // mensaje de un canal externo)
                                listaChatGeneral.getItems().add(textoEnriquecido);
                            });
                        }
                    }
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
        String mensaje = textoMensajePrivadoCanal.getText();

        // Verifica si se ha seleccionado un contacto de la lista
        String usuarioSeleccionado = listaContactosCanal.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado != null && !mensaje.isEmpty()) {
            Mensaje mensajeAEnviar = new Mensaje();
            mensajeAEnviar.setEmisor(usuario);
            mensajeAEnviar.setDestinatario(Constantes.TipoDestino.CANAL,
                    Constantes.Canales.valueOf(usuarioSeleccionado.toUpperCase()));
            mensajeAEnviar.setMensaje(mensaje);

            try {
                salida.writeObject(mensajeAEnviar);
            } catch (Exception e) {
                System.err.println("Error al enviar el mensaje");
                e.printStackTrace();
            }

            textoMensajePrivadoCanal.clear();
        }
    }

    @Override
    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario,
            Constantes.Canales canal, String historial, String estilos) {
        super.setInformacion(socket, salida, entrada, usuario, canal, historial, estilos);
        tituloEncabezadoMedico.setText("Bienvenido " + usuario);
    }
}

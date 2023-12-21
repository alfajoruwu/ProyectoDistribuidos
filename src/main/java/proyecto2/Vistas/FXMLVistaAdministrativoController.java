package proyecto2.Vistas;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import proyecto2.Mensajeria.TextoEnriquecido;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;

public class FXMLVistaAdministrativoController extends VistaPadre implements Initializable {

    private boolean esImagen(File file) {
        // Implementación del método esImagen
        String nombreArchivo = file.getName().toLowerCase();
        return nombreArchivo.endsWith(".png") || nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg");
    }

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
    private Button botonEnviarMensajeChat;

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
    private Label tituloBuscadorContacto;

    @FXML
    private Button botonSalidar;

    @FXML
    private Label labelNombreArchivo;

    @FXML
    private ImageView imagenVistaPrevia;

    @FXML
    private Button botonEnviarArchivo;

    @FXML
    public void irAVistaLogin(ActionEvent event) throws IOException {
        super.irAVistaLogin(event);
    }

    private ObservableList<String> contactList = FXCollections.observableArrayList();

    private ObservableList<String> contactListCanal = FXCollections.observableArrayList(
            "Auxiliar");

    @FXML
    public void handleDoubleClickListaChatGeneral(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selectedMessage = listaChatGeneral.getSelectionModel().getSelectedItem().getTexto();
            if (selectedMessage != null) {
                System.out.println("Selected Message: " + selectedMessage);
            }
        }
    }

    @FXML
    public void enviarArchivo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Archivo");
        File selectedFile = fileChooser.showOpenDialog(null);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(selectedFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;

        if (fileContent != null) {
            labelNombreArchivo.setText(selectedFile.getName());

            if (esImagen(selectedFile)) {
                try {
                    Image image = new Image(selectedFile.toURI().toString());
                    imagenVistaPrevia.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error al cargar la imagen: " + e.getMessage());
                }
            } else {
                System.out.println("No se puede mostrar vista previa para este tipo de archivo.");
            }

            // Aquí agregar la lógica para enviar el archivo
            String usuarioSeleccionado = listaContactos.getSelectionModel().getSelectedItem();

            if (usuarioSeleccionado != null) {
                Mensaje<Object> mensajeAEnviar = new Mensaje<>();
                mensajeAEnviar.setEmisor(usuario);
                mensajeAEnviar.setDestinatario(Constantes.TipoDestino.ARCHIVO, usuarioSeleccionado);
                Object[] archivo = new Object[2];
                archivo[0] = fileContent;
                archivo[1] = selectedFile.getName();
                mensajeAEnviar.setMensaje(archivo);

                try {
                    salida.writeObject(mensajeAEnviar);
                    System.out.println("Archivo enviado correctamente");
                } catch (IOException e) {
                    this.botonSalidar.fire();

                }
            } else {
                System.out.println("Usuario no seleccionado");
            }
        } else {
            System.out.println("Ningún archivo seleccionado");
        }
    }

    private void recibirArchivo(Mensaje<?> mensaje) {
        Object[] archivo = (Object[]) mensaje.getMensaje();
        byte bytes[] = (byte[]) archivo[0];
        String nombreArchivo = (String) archivo[1];
        String emisor = mensaje.getEmisor();

        // Verificar si el destinatario del mensaje es el usuario actual
        String destinatario = mensaje.getDestinatario();
        if (!destinatario.equals(usuario)) {
            return; // No mostrar el cuadro de diálogo si el destinatario no es el usuario actual
        }

        // Muestra un cuadro de diálogo para que el usuario decida qué hacer con el
        // archivo
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Recepción de Archivo");
            alert.setHeaderText("Has recibido un archivo de " + emisor);
            alert.setContentText("Nombre del archivo: " + bytes);

            ButtonType buttonTypeSave = new ButtonType("Guardar");
            ButtonType buttonTypeCancel = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeSave) {
                // Guarda el archivo (puedes implementar esta lógica)
                try {
                    FileOutputStream fos = new FileOutputStream(nombreArchivo);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void responder() {
        String selectedMessage = listaChatGeneral.getSelectionModel().getSelectedItem().getTexto();
        if (selectedMessage != null) {
            System.out.println("Selected Message: " + selectedMessage);
            String destino = selectedMessage.split(" ", 4)[2].replace(" ", "").replace(":", "");

            String mensaje = messageField.getText();

            // Verifica si se ha seleccionado un contacto de la lista
            String usuarioSeleccionado = destino;

            Mensaje<Object> mensajeAEnviar = new Mensaje<Object>();
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listaChatGeneral.setEditable(false);
        listaContactos.setItems(contactList);
        listaContactosCanal.setItems(contactListCanal);
        listaChatGeneral.setOnMouseClicked(this::handleDoubleClickListaChatGeneral);

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
        try {
            while (!hilo.isInterrupted()) {
                Mensaje<?> mensaje;
                mensaje = (Mensaje<?>) entrada.readObject();
                if (mensaje.getTipoDestinatario() == Constantes.TipoDestino.ARCHIVO) {
                    recibirArchivo(mensaje);
                } else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.ACTUALIZAR_CONTACTOS)) {
                    if (mensaje.getMensaje() == null) {
                        continue;
                    }
                    String[] contactos = mensaje.getMensaje().toString().split(",");
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
            Mensaje<Object> mensajeAEnviar = new Mensaje<Object>();
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
            Mensaje<Object> mensajeAEnviar = new Mensaje<Object>();
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

        if (canal == Constantes.Canales.AUXILIAR) {
            listaContactosCanal.setVisible(false);
            textoBuscadorCanal.setVisible(false);
            textoMensajePrivadoCanal.setVisible(false);
            botonEnviarMensajeCanal.setVisible(false);
            tituloBuscadorCanal.setVisible(false);

            textoBuscarContacto.setVisible(false);
            listaContactos.setVisible(false);
            textoMensajePrivado.setVisible(false);
            botonEnviarMensajePrivado.setVisible(false);
            tituloBuscadorContacto.setVisible(false);
            tituloEncabezadoMedico.setText("Bienvenido Auxiliar " + usuario);
        } else {
            listaContactos.setVisible(true);
            textoBuscadorCanal.setVisible(true);
            textoMensajePrivadoCanal.setVisible(true);
            botonEnviarMensajeCanal.setVisible(true);
            tituloBuscadorCanal.setVisible(true);
            textoBuscarContacto.setVisible(true);
            listaContactos.setVisible(true);
            textoMensajePrivado.setVisible(true);
            botonEnviarMensajePrivado.setVisible(true);
            tituloBuscadorContacto.setVisible(true);
            tituloEncabezadoMedico.setText("Bienvenido " + usuario);
        }
    }
}
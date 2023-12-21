/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto2.Vistas;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import proyecto2.Mensajeria.Usuarios;
import proyecto2.BaseDatos.Connect;

/**
 * FXML Controller class
 *
 * @author alfajor
 */
public class FXMLPopUpMonitorearController implements Initializable {

    private String usuario;
    protected Socket socket;
    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;

    @FXML
    private TextField BuscarUsuario;

    @FXML
    private TextField HoraInicio;

    @FXML
    private TextField HoraTermino;

    @FXML
    private ListView<String> ListaUsuarios;

    @FXML
    private ListView<String> MensajesEnviados;

    @FXML
    private Label tiempoUsoTotal;

    @FXML
    private DatePicker fechaInicio;

    @FXML
    private DatePicker fechaTermino;

    @FXML
    private Button Buscar;

    private ObservableList<String> ListaUsuariosObserbable = FXCollections.observableArrayList();

    private ObservableList<String> MensajesEnviadosObserbable = FXCollections.observableArrayList();

    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;

        // mensaje para llenar lista

        Mensaje<Object> mensaje = new Mensaje<Object>();
        mensaje.setEmisor(usuario);
        mensaje.setDestinatario(Constantes.TipoDestino.OBTENER_USUARIOS, Constantes.Nombres.SERVIDOR.toString());
        mensaje.setMensaje("uwu");

        try {
            salida.writeObject(mensaje);
            Usuarios respuesta;

            respuesta = (Usuarios) entrada.readObject();

            if (respuesta.getTipoDestinatario().equals(Constantes.TipoDestino.OBTENER_USUARIOS)) {
                System.out.println("wuwuuwuw");
                System.out.println("usuarios:" + respuesta.getsMensaje().toString());
                String usuarios = respuesta.getsMensaje().toString();
                System.out.println(usuarios);
                usuarios = usuarios.replaceAll("\\[", "").replaceAll("]", "");
                String[] contactos = usuarios.split(",");

                Platform.runLater(() -> {
                    ListaUsuariosObserbable.clear();
                    for (String contacto : contactos) {

                        ListaUsuariosObserbable.add(contacto.trim());

                    }
                });
            } else {
                System.out.println("jaja funaste");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // filtro de busqueda de usuarios
        ListaUsuarios.setCellFactory(TextFieldListCell.forListView());
        ListaUsuarios.setItems(ListaUsuariosObserbable);
        BuscarUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            ListaUsuarios.setItems(ListaUsuariosObserbable.filtered(s -> s.contains(newValue)));
        });

        // filtro de busqueda de mensajes
        MensajesEnviados.setCellFactory(TextFieldListCell.forListView());
        MensajesEnviados.setItems(MensajesEnviadosObserbable);

        // quitar numero de semana
        fechaInicio.setShowWeekNumbers(false);
        fechaTermino.setShowWeekNumbers(false);

        // evitar que se pueda seleccionar una fecha futura
        fechaInicio.valueProperty().addListener((observable, oldValue, newValue) -> {
            fechaTermino.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(fechaInicio.getValue()) < 0
                            || date.compareTo(LocalDate.now()) > 0);
                }
            });
        });
        fechaTermino.valueProperty().addListener((observable, oldValue, newValue) -> {
            fechaInicio.setDayCellFactory(picker -> new DateCell() {
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.compareTo(fechaTermino.getValue()) > 0
                            || date.compareTo(LocalDate.now()) > 0);
                }
            });
        });

        fechaInicio.setValue(LocalDate.now());
        fechaTermino.setValue(LocalDate.now());
    }

    @FXML
    private void actualizarListaMensajes() {
        String fecha1 = fechaInicio.getValue().toString();
        String fecha2 = fechaTermino.getValue().toString();
        if (HoraInicio.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")
                && HoraTermino.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
            fecha1 += " " + HoraInicio.getText();
            fecha2 += " " + HoraTermino.getText();
        }
        // obtener mensajes de la base de datos
        MensajesEnviadosObserbable.clear();

        Connection connection = Connect.connect();
        try {
            String sql = "SELECT Emisor, count(Emisor) as veces FROM Mensajes WHERE Usuario = ? AND Fecha BETWEEN ? AND ? GROUP BY Emisor";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ListaUsuarios.getSelectionModel().getSelectedItem());
            preparedStatement.setString(2, fecha1);
            preparedStatement.setString(3, fecha2);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Mensajes obtenidos");
            System.out.println("consulta: " + preparedStatement.toString());
            while (resultSet.next()) {

                MensajesEnviadosObserbable.add(resultSet.getString("Emisor").replace("TU:",
                        ListaUsuarios.getSelectionModel().getSelectedItem() + ":") + " "
                        + resultSet.getString("veces"));
            }

        } catch (Exception e) {
            System.out.println("Error al obtener mensajes de la base de datos");
            e.printStackTrace();
        } finally {
            Connect.disconnect();
        }
    }
}

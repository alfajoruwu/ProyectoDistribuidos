/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto2.Vistas;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author alfajor
 */
public class FXMLPopUpMonitorearController implements Initializable {

    @FXML
    private TextField BuscarUsuario;

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

    private ObservableList<String> ListaUsuariosObserbable = FXCollections.observableArrayList(
            "Usuario 1",
            "Usuario 2",
            "Usuario 3");

    private ObservableList<String> MensajesEnviadosObserbable = FXCollections.observableArrayList();

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

        // filtro de busqueda de usuarios

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

        System.out.println("fecha inicio: " + fechaInicio.getValue());
        System.out.println("fecha termino: " + fechaTermino.getValue());
    }

}

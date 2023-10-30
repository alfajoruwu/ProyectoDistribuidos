/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectodistribuidos;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 *
 * @author alfajor
 */
public class Login implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    private Socket socket;

    @FXML
    private Button ingresar;

    @FXML
    private void enviarDatos(ActionEvent event) {
        System.out.println("Hola");
    }

    @FXML
    private TextField nombreUsuario;

    @FXML
    public void leerDatos(ActionEvent event) {
        String usuario = nombreUsuario.getText();
        System.out.println(usuario);
        System.out.println(this.socket.getLocalPort());
    }

    @FXML
    private PasswordField contraseña;

    @FXML
    public void irAVistaMedico(ActionEvent event) throws IOException {
        leerDatos(event);
        root = FXMLLoader.load(getClass().getResource("Vistas/FXMLVistaMedico.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Label label;

    @FXML
    private Label mensajeError;

    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}

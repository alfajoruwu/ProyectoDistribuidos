package proyecto2.Vistas;

import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

// clase padre de las vistas de los usuarios (medico, administrativo, administrador)
public abstract class VistaPadre {
    protected Stage stage;
    protected Scene scene;
    protected String usuario;

    protected Socket socket;
    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;

    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;
    }

    public void irAVistaLogin(ActionEvent event) throws IOException {
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador para poder enviarle la información
        Login login = loader.getController();
        login.setInformacion(socket, salida, entrada);

        // TODO: informar al servidor que el usuario se ha desconectado

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}

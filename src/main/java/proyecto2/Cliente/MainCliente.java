
package proyecto2.Cliente;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import proyecto2.Vistas.Login;

public class MainCliente extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Crear el objeto Socket
        Socket socket = new Socket("127.0.0.1", 5000);

        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyecto2/Vistas/Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la clase Login
        Login loginController = loader.getController();

        try {
            ObjectOutputStream salida = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
            loginController.enviarInformacion(socket, salida, entrada); // Pasar el objeto Socket a la clase Login
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

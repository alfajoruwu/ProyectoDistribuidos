
package proyectodistribuidos;

import java.net.Socket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ProyectoDistribuidos extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Crear el objeto Socket
        Socket socket = new Socket("127.0.0.1", 5000); // Ejemplo de creación de Socket

        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la clase Login
        Login loginController = loader.getController();
        loginController.setSocket(socket); // Pasar el objeto Socket a la clase Login

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}

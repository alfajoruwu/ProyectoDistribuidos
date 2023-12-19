
package proyecto2.Cliente;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import proyecto2.Vistas.Login;

public class MainCliente extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Cargar la interfaz gráfica
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/proyecto2/Vistas/Login.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la clase Login
        Login loginController = loader.getController();

        try {
            while (loginController.conectar() == false) {
                loginController.conectar();
            }
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

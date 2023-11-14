
package proyecto2.Vistas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import proyecto2.Mensajeria.Usuarios;

public class FXMLVistaAdministradorController extends VistaPadre implements Initializable {

    @FXML
    private Label tituloEncabezadoAdministrador;

    @FXML
    private Button botonCrearUsuario;

    @FXML
    private Button botonMonitorearComunicaciones;

    @FXML
    private Button botonReiniciarContraseña;

    @FXML
    private Button sendButton;

    @FXML
    private Button botonSalir;

    @FXML
    public void irAVistaLogin(ActionEvent event) throws IOException {
        super.irAVistaLogin(event);
    }

    @FXML
    public void ReiniciarContraseñaPopup(ActionEvent event) throws IOException{
        FXMLLoader Loader = new FXMLLoader(getClass().getResource("FXMLPopUpReiniciarContraseña.fxml"));
        Parent root1 = Loader.load();

        FXMLPopUpReiniciarContraseñaController controladorVista = Loader.getController();
        controladorVista.setInformacion(socket, salida, entrada, usuario);
        
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(root1));
        stage2.show();
    }

    @FXML
    public void AñadirUsuario(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLPopUpCrearUsuario.fxml"));
        Parent root = loader.load();

        // Obtener el controlador de la vista
        FXMLPopUpCrearUsuarioController controladorVista = loader.getController();
        controladorVista.setInformacion(socket, salida, entrada, usuario);

        Stage stage2 = new Stage();
        stage2.setScene(new Scene(root));
        stage2.show();

    }

    @FXML
    public void Monitoreo(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLPopUpMonitorear.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage2 = new Stage();
        stage2.setScene(new Scene(root1));
        stage2.show();
    }

    @FXML
    public void MensajeUrgente(ActionEvent event) {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(this.usuario);
            mensaje.setMensaje(message);
            mensaje.setDestinatario(Constantes.TipoDestino.TODOS, canal);
            messageField.clear();
            try {
                salida.writeObject(mensaje);
            } catch (Exception excepcion) {
                excepcion.printStackTrace();
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chatArea.setEditable(false);

    }

    @Override
    public void run() {
        try {
            while (!hilo.isInterrupted()) {
                                 
            
            }
        } catch (Exception e) {
            if (!hilo.isInterrupted()) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario,
            Constantes.Canales canal, String historial){
                super.setInformacion(socket, salida, entrada, usuario, canal, historial);

                Mensaje mensaje = new Mensaje();
                mensaje.setEmisor(usuario);
                mensaje.setDestinatario(Constantes.TipoDestino.OBTENER_USUARIOS, Constantes.Nombres.SERVIDOR.toString());
                mensaje.setMensaje("uwu");
                
                try {
                    salida.writeObject(mensaje);
                    Usuarios respuesta;
                    
                     
                    respuesta = (Usuarios) entrada.readObject();
                    
                    if (respuesta.getTipoDestinatario().equals(Constantes.TipoDestino.OBTENER_USUARIOS)) {
                        System.out.println("wuwuuwuw");
                        System.out.println("usuarios:"+respuesta.getsMensaje().toString());
                        
                    }  
                    else{
                        System.out.println("jaja funaste");
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                

            };
}

package proyecto2.Vistas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import proyecto2.Mensajeria.Constantes.Canales;



public class FXMLPopUpCrearUsuarioController implements Initializable {

    @FXML
    private TextField Nombre;
    
    @FXML
    private TextField Rut;
    
    @FXML
    private TextField Correo;
    
    @FXML
    private ChoiceBox<String> rol;

    @FXML
    private Label texto;
    
    private String[] roles; 
    
    private  String usuario;
    protected Socket socket;
    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;

    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;
        
    }


    
    @FXML
    public void enviarCrearUsuario() throws IOException{
        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(usuario);
        mensaje.setDestinatario(Constantes.TipoDestino.AÑADIRUSUARIOS, Constantes.Nombres.SERVIDOR.toString());
        mensaje.setMensaje(""+Nombre.getText()+":"+Rut.getText()+":"+Correo.getText()+":"+rol.getValue());
        
        salida.writeObject(mensaje);

        texto.setText("Se Añadio al usuario");
        
    
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String aux = "";
        
        for (Canales canal : Canales.values()) {
            aux+= canal+",";
        }

        roles = aux.split(",");

        rol.getItems().addAll(roles);

    }



    
    
}

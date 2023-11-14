/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto2.Vistas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;

/**
 * FXML Controller class
 *
 * @author alfajor
 */
public class FXMLPopUpSetearContraseñaController implements Initializable {

    
    
    @FXML
    private TextField nuevaClave;
    
    @FXML
    private TextField ConfirmarClave;
    
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
    public void primeraContraseña() throws IOException{
        if(nuevaClave.getText().equals(ConfirmarClave.getText())){
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(usuario);
            mensaje.setDestinatario(Constantes.TipoDestino.ACTUALIZAR_CONTRASEÑA, Constantes.Nombres.SERVIDOR.toString());
            mensaje.setMensaje(nuevaClave.getText()+":"+usuario);
            salida.writeObject(mensaje);
        }
        else{System.out.println("jaja te equivocaste");}
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

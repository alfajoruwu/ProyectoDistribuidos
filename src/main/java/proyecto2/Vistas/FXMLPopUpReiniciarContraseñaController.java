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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import proyecto2.Mensajeria.Constantes;
import proyecto2.Mensajeria.Mensaje;
import proyecto2.Mensajeria.Usuarios;

/**
 * FXML Controller class
 *
 * @author alfajor
 */
public class FXMLPopUpReiniciarContraseñaController implements Initializable {

    @FXML
    private TextField BuscarUsuario;
    
    @FXML
    private ListView<String> ListarUsuarios;    
    
    private ObservableList<String> ListaUsuariosObserbable = FXCollections.observableArrayList(
            "Usuario 1",
            "Usuario 2",
            "Usuario 3");
    
    private  String usuario;
    protected Socket socket;
    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;



    public void setInformacion(Socket socket, ObjectOutputStream salida, ObjectInputStream entrada, String usuario) {
        this.socket = socket;
        this.salida = salida;
        this.entrada = entrada;
        this.usuario = usuario;

        //mensaje para llenar lista

       
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
    }

    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ListarUsuarios.setCellFactory(TextFieldListCell.forListView());
        ListarUsuarios.setItems(ListaUsuariosObserbable);
        BuscarUsuario.textProperty().addListener((observable, oldValue, newValue) -> {
            ListarUsuarios.setItems(ListaUsuariosObserbable.filtered(s -> s.contains(newValue)));
        });
    }    
    
}

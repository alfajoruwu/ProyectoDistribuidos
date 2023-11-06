/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto2.Vistas;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author alfajor
 */
public class FXMLPopUpMonitorearController implements Initializable {

    
    @FXML
    private TextField BuscarUsuario;
    
    @FXML
    private ListView ListaUsuarios; 
    
    @FXML
    private ListView MensajesEnviados;
    
    @FXML
    private Label tiempoUsoTotal;
        
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

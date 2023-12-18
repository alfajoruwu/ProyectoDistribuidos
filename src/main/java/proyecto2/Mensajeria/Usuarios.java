package proyecto2.Mensajeria;

import java.util.ArrayList;

public class Usuarios extends Mensaje<ArrayList<String>> {

    public ArrayList<String> mensaje;

    public void setMensaje(ArrayList<String> mensaje) {
        this.mensaje = mensaje;
    }

    public ArrayList<String> getsMensaje() {
        return this.mensaje;
    }
}

package proyecto2.Mensajeria;

public class Mensaje implements java.io.Serializable {
    private String mensaje;
    private String emisor;
    // el destinatario es un arreglo de Constantes.TipoDestino y String
    private Object[] destinatario = new Object[2];

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getDestinatario() {
        return destinatario[1].toString();
    }

    public Constantes.TipoDestino getTipoDestinatario() {
        return (Constantes.TipoDestino) destinatario[0];
    }

    public String getDestinatarioFull() {
        return destinatario[0] + ":" + destinatario[1];
    }

    public void setDestinatario(Constantes.TipoDestino tipo, String destinatario) {
        this.destinatario[0] = tipo;
        this.destinatario[1] = destinatario;
    }

    public void setDestinatario(Constantes.TipoDestino tipo, Constantes.Canales destinatario) {
        this.destinatario[0] = tipo;
        this.destinatario[1] = destinatario;
    }

}

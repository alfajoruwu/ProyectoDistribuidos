package proyecto2.Mensajeria;

public class Mensaje<tipo> implements java.io.Serializable {
    private tipo mensaje;
    private String emisor;
    private String fechaHora;
    // el destinatario es un arreglo de Constantes.TipoDestino y String
    private Object[] destinatario = new Object[2];
    private String infoDestinatario;

    public tipo getMensaje() {
        return mensaje;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setMensaje(tipo mensaje) {
        this.mensaje = mensaje;
    }

    public String getInfoDestinatario() {
        return infoDestinatario;
    }

    public void setInfoDestinatario(String infoDestinatario) {
        this.infoDestinatario = infoDestinatario;
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

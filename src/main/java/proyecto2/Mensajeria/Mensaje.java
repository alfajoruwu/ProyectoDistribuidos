package proyecto2.Mensajeria;

public class Mensaje implements java.io.Serializable {

    // prefijos para los tipos de destinatario
    public static final String PREFIJO_CANAL = "Canal";
    public static final String PREFIJO_USUARIO = "Usuario";
    public static final String PREFIJO_LOGIN = "Login";
    public static final String PREFIJO_LOGOUT = "Logout";

    public static final String LOGIN_EXITOSO = "Login exitoso";
    public static final String LOGIN_FALLIDO = "Login fallido";

    public static final String SERVIDOR = "Servidor";

    private String mensaje;
    private String emisor;
    private String[] destinatario = new String[2];

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
        return destinatario[1];
    }

    public String getTipoDestinatario() {
        return destinatario[0];
    }

    public String getDestinatarioFull() {
        return destinatario[0] + ":" + destinatario[1];
    }

    public void setDestinatario(String tipo, String destinatario) {
        this.destinatario[0] = tipo;
        this.destinatario[1] = destinatario;
    }
}

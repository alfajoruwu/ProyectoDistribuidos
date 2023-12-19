package proyecto2.Mensajeria;

public class Constantes {
    // prefijos para los tipos de destinatario al enviar un mensaje
    public static enum TipoDestino {
        CANAL, USUARIO, LOGIN, LOGOUT, ANNADIRUSUARIOS, ACTUALIZAR_CONTACTOS, BORRAR_HISTORIAL, TODOS,
        ACTUALIZAR_CONTRASENNA, OBTENER_USUARIOS, REINICIAR_CONTRASENNA
    }

    public static enum Respuestas {
        LOGIN_EXITOSO, LOGIN_FALLIDO, LOGIN_PRIMERO
    }

    public static enum Canales {
        MEDICO, ADMISION, PABELLON, EXAMENES, AUXILIAR, ADMINISTRADOR, TODOS
    }

    public static enum Nombres {
        SERVIDOR, USUARIO_ANONIMO
    }

    public static int puerto = 80;
    public static String host = "34.72.209.178";

}

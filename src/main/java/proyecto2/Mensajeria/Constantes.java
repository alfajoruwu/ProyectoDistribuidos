package proyecto2.Mensajeria;

public class Constantes {
    // prefijos para los tipos de destinatario al enviar un mensaje
    public static enum TipoDestino {
        CANAL, USUARIO, LOGIN, LOGOUT, AÑADIRUSUARIOS, ACTUALIZAR_CONTACTOS, BORRAR_HISTORIAL,TODOS
    }

    public static enum Respuestas {
        LOGIN_EXITOSO, LOGIN_FALLIDO, LOGIN_PRIMERO
    }

    public static enum Canales {
        MEDICO, ADMISION, PABELLON, EXAMENES, AUXILIAR, ADMINISTRADOR,TODOS
    }

    public static enum Nombres {
        SERVIDOR, USUARIO_ANONIMO
    }

}

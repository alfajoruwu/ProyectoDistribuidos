package proyecto2.Servidor;

import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import proyecto2.Mensajeria.Mensaje;
import proyecto2.Mensajeria.Usuarios;
import proyecto2.Mensajeria.Constantes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ConexionServidor implements Runnable, PropertyChangeListener {
    private String usuario;
    private String historial;
    private Constantes.Canales canal;
    private Socket socket;
    private MainServidor servidor;
    private ObjectInputStream entrada;
    private ObjectOutputStream salida;

    public ConexionServidor(Socket socket, MainServidor servidor, String usuario) {
        this.socket = socket;
        this.servidor = servidor;
        this.usuario = usuario;
        try {
            this.salida = new ObjectOutputStream(socket.getOutputStream());
            this.entrada = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.err.println("Error al crear los flujos de entrada y salida");
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        try {
            Mensaje mensaje;
            while (true) {
                mensaje = (Mensaje) entrada.readObject();

                // mensaje a un canal
                if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.CANAL)) {
                    servidor.notificar(Constantes.Canales.valueOf(mensaje.getDestinatario()), mensaje);
                // ----------------------------------------------------------------------------
                
                
                // mensaje a un usuario
                
                }else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.TODOS))
                {
                    System.out.println("uwu a todos");
                    servidor.notificar(Constantes.Canales.ADMINISTRADOR, mensaje);
                    servidor.notificar(Constantes.Canales.ADMISION, mensaje);
                    servidor.notificar(Constantes.Canales.AUXILIAR, mensaje);
                    servidor.notificar(Constantes.Canales.EXAMENES, mensaje);
                    servidor.notificar(Constantes.Canales.MEDICO, mensaje);
                    servidor.notificar(Constantes.Canales.PABELLON, mensaje);
                }

                 else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.USUARIO)) {
                    servidor.enviarMensaje(mensaje, mensaje.getDestinatario());
                    // ----------------------------------------------------------------------------

                    // mensaje al servidor para login
                } else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.LOGIN)) {
                    Mensaje respuesta = new Mensaje();
                    respuesta.setEmisor(Constantes.Nombres.SERVIDOR.toString());
                    System.out.println(mensaje.getEmisor() + " -> login ");
                    Constantes.Canales canal = servidor.validarUsuario(mensaje.getEmisor(), mensaje.getMensaje());
                    respuesta.setDestinatario(Constantes.TipoDestino.USUARIO, mensaje.getEmisor());
                    if (canal != null) {
                        this.canal = canal;
                        this.usuario = mensaje.getEmisor();
                        servidor.agregarUsuario(mensaje.getEmisor(), this);
                        servidor.agregarCanalUsuario(canal, mensaje.getEmisor());
                        this.historial = servidor.getHistorial(usuario);
                        
                        if(servidor.primerinicio(mensaje.getEmisor()) == 1){
                            respuesta.setMensaje(Constantes.Respuestas.LOGIN_PRIMERO + ":" + canal + ":" + historial);
                        }
                        else{
                        respuesta.setMensaje(Constantes.Respuestas.LOGIN_EXITOSO + ":" + canal + ":" + historial);
                        }

                        salida.writeObject(respuesta);
                        actualizarContactos();
                    }
                    
                    else {
                        respuesta.setMensaje(Constantes.Respuestas.LOGIN_FALLIDO + ":null" + ":null");
                        salida.writeObject(respuesta);
                    }
                    // ----------------------------------------------------------------------------

                    // mensaje al servidor para logout
                } else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.LOGOUT)) {
                    System.out.println(mensaje.getEmisor() + " -> logout ");
                    servidor.setHistorial(usuario, historial);
                    servidor.removerCanalUsuario(canal, usuario);
                    servidor.removerUsuario(usuario);
                    this.canal = null;
                    this.usuario = Constantes.Nombres.USUARIO_ANONIMO + " " + socket.getPort();
                    // ----------------------------------------------------------------------------

                    // mensaje al servidor para registrar usuario
                } else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.AÑADIRUSUARIOS)) {
                    System.out.println("añadir usuarios");
                    System.out.println(mensaje.getMensaje());
                    String[] aux = mensaje.getMensaje().split(":");
                    servidor.CrearUsuario(aux[0], aux[1], aux[2], aux[3]);
                    // ----------------------------------------------------------------------------

                    // mensaje al servidor para borrar usuario
                } else if (mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.BORRAR_HISTORIAL)) {
                    this.historial = "";
                }
                // ----------------------------------------------------------------------------

                else if(mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.ACTUALIZAR_CONTRASEÑA)){
                    System.out.println("actualizar contraseñaaaaa");
                    System.out.println(mensaje.getMensaje());
                    servidor.CambiarContraseña(mensaje.getMensaje().split(":")[0], mensaje.getMensaje().split(":")[1]);
                }

                else if(mensaje.getTipoDestinatario().equals(Constantes.TipoDestino.OBTENER_USUARIOS)){
                    System.out.println("Obtener usuarios");
                    
                    ArrayList<String> usuarios = servidor.ObtenerUsuarios();
                    
                    Usuarios respuesta = new Usuarios();
                    respuesta.setEmisor(Constantes.Nombres.SERVIDOR.toString());
                    respuesta.setDestinatario(Constantes.TipoDestino.OBTENER_USUARIOS, mensaje.getEmisor());
                    
                    respuesta.setMensaje(usuarios);
                    salida.writeObject(respuesta);

                }


                // mensaje a un destinatario no reconocido
                else {
                    System.err.println(mensaje.getEmisor() +
                            " -> Error al enviar mensaje a " +
                            mensaje.getDestinatarioFull() + " -> Destinatario no reconocido");
                }
                // --------------------------------------------------------------------------------
                

            }
        } catch (Exception e) { // si algo falla, se desconecta el usuario
            servidor.setHistorial(usuario, historial);
            servidor.removerCanalUsuario(canal, usuario);
            if (usuario.startsWith(Constantes.Nombres.USUARIO_ANONIMO.toString())) {
                System.out.println("Desconectado : " + usuario);
            } else {
                servidor.removerUsuario(usuario);
            }
            // si es un error de socket, no se imprime
            if (e.getClass() != SocketException.class && e.getClass() != EOFException.class) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) { // notificacion a mi canal
        if (Constantes.Canales.valueOf(evt.getPropertyName()) == this.canal) {
            try {
                Mensaje mensaje = (Mensaje) evt.getNewValue();
                recibirMensaje(mensaje);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Error al notificar: " + evt.getPropertyName());
            System.err.println("Tipo de usuario: " + this.canal);
        }
    }

    public void recibirMensaje(Mensaje mensaje) { // mandar al usuario
        try {
            LocalDateTime fechaYHoraActual = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            String fechaYHoraFormateada = fechaYHoraActual.format(formatter);
            
            historial+= fechaYHoraFormateada+": ";

            if (mensaje.getEmisor().equals(usuario)) {
                historial += "TU: " + mensaje.getMensaje()+  "\n";
            } else {
                historial += mensaje.getEmisor() + ": " + mensaje.getMensaje() + "\n";
            }
            salida.writeObject(mensaje);
        } catch (IOException e) {
            System.err.println(usuario + " -> Error al enviar mensaje privado");
        }
    }

    public void actualizarContactos() {
        try {
            if (canal == null || canal == Constantes.Canales.ADMINISTRADOR ) {
                return;
            }
            Mensaje mensaje = new Mensaje();
            mensaje.setEmisor(Constantes.Nombres.SERVIDOR.toString());
            mensaje.setDestinatario(Constantes.TipoDestino.ACTUALIZAR_CONTACTOS, usuario);
            mensaje.setMensaje(servidor.getMedicosConectados(canal));
            salida.writeObject(mensaje);
        } catch (Exception e) {
            System.err.println("Error al enviar el mensaje de actualizar contactos");
            e.printStackTrace();
        }
    }

    public Constantes.Canales getCanal() {
        return canal;
    }

    public String getUsuario() {
        return usuario;
    }
}

package proyecto2.Servidor;

import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class Observable {
    public static final String CANAL_MEDICOS = "medico";
    public static final String CANAL_ADMISION = "admision";
    public static final String CANAL_PABELLON = "pabellon";
    public static final String CANAL_EXAMENES = "examenes";
    public static final String CANAL_AUXILIAR = "auxiliar";
    public static final String CANAL_ADMINISTRADOR = "administrador";

    private PropertyChangeSupport notificador;

    public Observable() {
        this.notificador = new PropertyChangeSupport(this);
    }

    public void agregarObservador(String tipo, PropertyChangeListener observador) {
        if (tipo.equals(CANAL_MEDICOS)) {
            this.notificador.addPropertyChangeListener(CANAL_MEDICOS, observador);
        } else if (tipo.equals(CANAL_ADMISION)) {
            this.notificador.addPropertyChangeListener(CANAL_ADMISION, observador);
        } else if (tipo.equals(CANAL_PABELLON)) {
            this.notificador.addPropertyChangeListener(CANAL_PABELLON, observador);
        } else if (tipo.equals(CANAL_EXAMENES)) {
            this.notificador.addPropertyChangeListener(CANAL_EXAMENES, observador);
        } else if (tipo.equals(CANAL_AUXILIAR)) {
            this.notificador.addPropertyChangeListener(CANAL_AUXILIAR, observador);
        } else if (tipo.equals(CANAL_ADMINISTRADOR)) {
            this.notificador.addPropertyChangeListener(CANAL_ADMINISTRADOR, observador);
        } else {
            throw new IllegalArgumentException("Tipo de usuario no válido: " + tipo);
        }
    }

    public void removerObservador(String tipo, PropertyChangeListener observador) {
        this.notificador.removePropertyChangeListener(tipo, observador);
    }

    public void notificar(String tipo, Object valorNuevo) {
        this.notificador.firePropertyChange(tipo, null, valorNuevo);
    }
}

package proyecto2.Servidor;

import java.beans.PropertyChangeSupport;

import proyecto2.Mensajeria.Constantes;

import java.beans.PropertyChangeListener;

public class Observable {
    private PropertyChangeSupport notificador;

    public Observable() {
        this.notificador = new PropertyChangeSupport(this);
    }

    public void agregarObservador(Constantes.Canales canal, PropertyChangeListener observador) {
        this.notificador.addPropertyChangeListener(canal.toString(), observador);
    }

    public void removerObservador(Constantes.Canales canal, PropertyChangeListener observador) {
        if (canal == null) {
            return;
        }
        this.notificador.removePropertyChangeListener(canal.toString(), observador);
    }

    public void notificar(Constantes.Canales canal, Object valorNuevo) {
        this.notificador.firePropertyChange(canal.toString(), null, valorNuevo);
    }
}

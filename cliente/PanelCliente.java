package proyecto2.cliente;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PanelCliente {
    private JScrollPane scroll;
    private JTextArea textArea;
    private DefaultListModel<String> usuarios;
    private JList<String> listaUsuarios;
    private JTextField textField;
    private JButton boton;
    private JButton mensajePrivado; // boton para enviar mensaje privado al usuario seleccionado en la lista

    public PanelCliente(Container contenedor) {
        contenedor.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
        scroll = new JScrollPane(textArea);
        usuarios = new DefaultListModel<>();
        listaUsuarios = new JList<>(usuarios);

        JPanel panel = new JPanel(new BorderLayout());
        textField = new JTextField(50);
        boton = new JButton("Enviar");
        mensajePrivado = new JButton("Mensaje privado");

        panel.add(textField, BorderLayout.NORTH);
        panel.add(boton, BorderLayout.SOUTH);

        JPanel panelUsuarios = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Usuarios conectados");

        panelUsuarios.add(label, BorderLayout.NORTH);
        panelUsuarios.add(listaUsuarios, BorderLayout.CENTER);
        panelUsuarios.add(mensajePrivado, BorderLayout.SOUTH);

        contenedor.add(panelUsuarios, BorderLayout.EAST);
        contenedor.add(scroll, BorderLayout.CENTER);
        contenedor.add(panel, BorderLayout.SOUTH);
    }

    public void addActionListener(ActionListener accion) {
        textField.addActionListener(accion);
        boton.addActionListener(accion);
    }

    public void addMensajePrivadoListener(ActionListener accion) {
        mensajePrivado.addActionListener(accion);
    }

    public String getUsuarioSeleccionado() {
        return listaUsuarios.getSelectedValue();
    }

    public String getTexto() {
        String texto = textField.getText();
        textField.setText("");
        return texto;
    }

    public void addTexto(String texto) {
        textArea.append(texto);
    }

    public void setUsuarios(ArrayList<String> usuarios, String usuario) {
        this.usuarios.clear();
        for (String usuarioActual : usuarios) {
            if (!usuarioActual.equals(usuario)) {
                this.usuarios.addElement(usuarioActual);
            }
        }
    }
}
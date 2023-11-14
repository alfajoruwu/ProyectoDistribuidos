package proyecto2.Mensajeria;

public class TextoEnriquecido {
    private String texto;
    private String estilo;

    public TextoEnriquecido(String texto, String estilo) {
        this.texto = texto;
        this.estilo = estilo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getEstilo() {
        return estilo;
    }

    public void setEstilo(String estilo) {
        this.estilo = estilo;
    }

}

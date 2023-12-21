module proyecto2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires transitive java.desktop;

    opens proyecto2.Vistas to javafx.fxml;

    exports proyecto2.Cliente;
    exports proyecto2.Vistas;
    exports proyecto2.Mensajeria;
}

module proyecto2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;
    requires transitive java.desktop;

    exports proyecto2.BaseDatos;
    exports proyecto2.Mensajeria;
    exports proyecto2.Servidor;
}

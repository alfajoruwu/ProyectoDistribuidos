module proyecto2 {
    requires transitive java.sql;
    requires transitive java.desktop;

    exports proyecto2.BaseDatos;
    exports proyecto2.Mensajeria;
    exports proyecto2.Servidor;
}

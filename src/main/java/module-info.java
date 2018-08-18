module org.wkh.arete {
    requires java.sql;
    requires javafx.controls;

    requires slf4j.api;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.jdbc;

    exports org.wkh.arete;
}
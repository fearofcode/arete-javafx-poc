/* see, e.g., https://carlfx.wordpress.com/2016/04/26/a-javafx-helloworld-using-java-9s-project-jigsaw-in-60-seconds/ */
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
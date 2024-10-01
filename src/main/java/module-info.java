module org.example.task7 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires java.desktop;
    requires jdk.compiler;

    opens org.example.task7 to javafx.fxml;
    exports org.example.task7;
    exports org.example.task7.model;
    opens org.example.task7.model to javafx.fxml;
    exports org.example.task7.config;
    opens org.example.task7.config to javafx.fxml;
    exports org.example.task7.utility;
    opens org.example.task7.utility to javafx.fxml;
    exports org.example.task7.repository;
    opens org.example.task7.repository to javafx.fxml;
    exports org.example.task7.service;
    opens org.example.task7.service to javafx.fxml;
}
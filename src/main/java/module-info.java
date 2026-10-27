module com.example.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.senai.javafx.entidades to javafx.fxml;
    exports com.senai.javafx.entidades;

    opens com.senai.javafx.controllers to javafx.fxml;
    exports com.senai.javafx.controllers;
}
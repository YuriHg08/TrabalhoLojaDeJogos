module com.senai.javafx {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.senai.javafx to javafx.graphics, javafx.fxml;
    opens com.senai.javafx.controllers to javafx.fxml;

    exports com.senai.javafx;
    exports com.senai.javafx.controllers;
}
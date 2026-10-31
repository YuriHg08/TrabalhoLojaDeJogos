module com.senai.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // Declarações dos Módulos JNA necessários para chamadas nativas Win32
    requires com.sun.jna;
    requires com.sun.jna.platform;

    // Permite reflexão do JavaFX para FXML e Inicialização de Aplicação
    opens com.senai.javafx to javafx.graphics, javafx.fxml;
    opens com.senai.javafx.controllers to javafx.fxml;
    opens com.senai.javafx.entidades to javafx.graphics, javafx.fxml;

    // Exporta pacotes da aplicação
    exports com.senai.javafx;
    exports com.senai.javafx.controllers;
    exports com.senai.javafx.entidades;
    exports com.senai.javafx.controllers.LadoAdministrador;
    opens com.senai.javafx.controllers.LadoAdministrador to javafx.fxml;
    exports com.senai.javafx.controllers.LadoCliente;
    opens com.senai.javafx.controllers.LadoCliente to javafx.fxml;
}
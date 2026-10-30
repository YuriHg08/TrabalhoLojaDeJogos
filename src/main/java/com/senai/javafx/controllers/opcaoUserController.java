package com.senai.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class opcaoUserController {

    @FXML
    private void handleIniciarSessao(ActionEvent event) {
        trocarTela(event, "/com/senai/javafx/loginUser.fxml");
    }

    @FXML
    private void handleCadastreSe(ActionEvent event) {
        trocarTela(event, "/com/senai/javafx/cadastroClientesUser.fxml");
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        trocarTela(event, "/com/senai/javafx/opcaoUser.fxml");
    }

    private void trocarTela(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/senai/javafx/styleMenu.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText(null);
            alert.setContentText("Erro ao carregar a tela: " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}
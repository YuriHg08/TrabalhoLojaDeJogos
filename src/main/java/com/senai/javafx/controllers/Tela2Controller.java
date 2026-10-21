package com.senai.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Tela2Controller {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField nome;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Hello World");
    }

    @FXML
    private void cadastroClientes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/senai/javafx/cadastroClientes.fxml")
        );

        Parent root = loader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setTitle("Cadastrar um cliente");
        stage.setScene(scene);
        stage.show();

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }
    @FXML
    private void consultarClientes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/senai/javafx/consultaclientes.fxml")
        );

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Consultar clientes");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
package com.senai.javafx.controllers;

import com.senai.javafx.entidades.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

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

        HelloApplication.WindowManager.abrirJanela("/com/senai/javafx/consultaClientes.fxml", "Consultar clientes");

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }
}
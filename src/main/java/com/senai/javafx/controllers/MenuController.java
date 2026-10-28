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

        HelloApplication.WindowManager.abrirJanela("/com/senai/javafx/cadastroClientes.fxml", "Consultar clientes");

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }

    @FXML
    private void consultarClientes(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/senai/javafx/consultaClientes.fxml"));

        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Cadastre-se na Loja");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }


    @FXML
    private void cadastroJogos(ActionEvent event) throws IOException {
        HelloApplication.WindowManager.abrirJanela("/com/senai/javafx/cadastroJogos.fxml", "Cadastrar um jogo");

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }

    @FXML
    private void cadastroCategoria(ActionEvent event) throws IOException {
        HelloApplication.WindowManager.abrirJanela("/com/senai/javafx/cadastroCategoria.fxml", "Cadastrar um jogo");

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }

    @FXML
    private void consultarJogos(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/senai/javafx/consultarJogos.fxml"));

        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 603, 475);

        stage.setTitle("Consultar jogos");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);

        Stage telaAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        telaAtual.close();
    }
}

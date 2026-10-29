package com.senai.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import static com.senai.javafx.entidades.GerenciamentoJanela.abrirJanela;

public class SidebarController {

    private static boolean modoEscuroAtivo = false;

    private void trocarTela(ActionEvent event, String fxmlPath) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean estavaMaximizado = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (modoEscuroAtivo) {
                root.getStyleClass().add("modo-escuro");
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(estavaMaximizado);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void abrirCadastroCliente(ActionEvent event) {
        abrirJanela("/com/senai/javafx/views/cadastroClientes.fxml", "Cadastrar um cliente");
    }

    @FXML
    void abrirCadastroCategorias(ActionEvent event) {
        abrirJanela("/com/senai/javafx/views/cadastroCategoria.fxml", "Cadastrar uma categoria");
    }

    @FXML
    void abrirCadastroJogos(ActionEvent event) {
        abrirJanela("/com/senai/javafx/views/cadastroJogos.fxml", "Cadastrar um jogo");
    }

    @FXML
    void abrirConsultarClientes(ActionEvent event) {
        abrirJanela("/com/senai/javafx/views/consultaClientes.fxml", "Consultar clientes");
    }

    @FXML
    void abrirConsultarJogos(ActionEvent event) {
        abrirJanela("/com/senai/javafx/views/consultarJogos.fxml", "Cadastrar um jogo");
    }

    @FXML
    void alternarModoEscuro(ActionEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();
        modoEscuroAtivo = !modoEscuroAtivo;

        if (modoEscuroAtivo) {
            scene.getRoot().getStyleClass().add("modo-escuro");
        } else {
            scene.getRoot().getStyleClass().remove("modo-escuro");
        }
    }

    @FXML
    void alternarConta(ActionEvent event) {
        trocarTela(event, "/com/senai/javafx/views/telaLogin.fxml");
    }
}
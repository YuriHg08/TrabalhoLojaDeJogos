package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class opcaoUserController {

    @FXML
    private void handleIniciarSessao(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/loginUser.fxml", "Entrar na conta");
    }

    @FXML
    private void handleCadastreSe(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroClienteUser.fxml", "Nexus Store - Cadastre-se");
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus Store");
    }
}


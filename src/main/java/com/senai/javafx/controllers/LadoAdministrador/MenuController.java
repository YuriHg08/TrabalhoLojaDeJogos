package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MenuController {

    // Controller do menu administrativo
    @FXML
    private void cadastroClientes(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroClientes.fxml", "Cadastrar Cliente");
    }

    @FXML
    private void consultarClientes(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultaClientes.fxml", "Consultar Clientes");
    }

    @FXML
    private void cadastroJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroJogos.fxml", "Cadastrar Jogo");
    }

    @FXML
    private void cadastroCategoria(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroCategoria.fxml", "Cadastrar Categoria");
    }

    @FXML
    private void consultarJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultarJogos.fxml", "Consultar Jogos");
    }

}
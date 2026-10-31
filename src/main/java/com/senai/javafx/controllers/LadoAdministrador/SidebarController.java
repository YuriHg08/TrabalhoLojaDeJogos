package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class SidebarController {

    @FXML
    void abrirCadastroCliente(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroClientes.fxml", "Cadastrar Cliente");
    }

    @FXML
    void abrirCadastroCategorias(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroCategoria.fxml", "Cadastrar Categoria");
    }

    @FXML
    void abrirCadastroJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroJogos.fxml", "Cadastrar Jogo");
    }

    @FXML
    void abrirConsultarClientes(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultaClientes.fxml", "Consultar Clientes");
    }

    @FXML
    void abrirConsultarJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultarJogos.fxml", "Consultar Jogos");
    }

    @FXML
    void alternarConta(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaLogin.fxml", "Fazer Login");
    }
}
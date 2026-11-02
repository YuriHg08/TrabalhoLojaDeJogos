package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.RegistroCompras;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class MenuController {

    // Controller do menu administrativo

    // Botão "Consultar clientes": mostra quantas compras estão esperando aprovação
    @FXML
    private Button btnConsultarClientes;

    @FXML
    private void initialize() {
        atualizarContadorPendentes();
    }

    // Ex.: "Consultar clientes (2)" quando há compras pendentes
    public void atualizarContadorPendentes() {
        if (btnConsultarClientes == null) {
            return;
        }
        int pendentes = RegistroCompras.listarPendentes().size();
        btnConsultarClientes.setText(pendentes == 0 ? "Consultar clientes" : "Consultar clientes (" + pendentes + ")");
    }

    @FXML
    private void consultarClientes(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultaClientes.fxml", "Consultar Clientes");
    }

    @FXML
    private void historicoCompras(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/historicoCompras.fxml", "Histórico de compras");
    }

    @FXML
    private void cadastroJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroJogos.fxml", "Cadastrar Jogo");
    }

    @FXML
    private void cadastroAcessorios(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroAcessorios.fxml", "Cadastrar Acessório");
    }

    @FXML
    private void consultarAcessorios(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultarAcessorios.fxml", "Consultar Acessórios");
    }

    @FXML
    private void cadastroCategoria(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroCategoria.fxml", "Cadastrar Categoria");
    }

    @FXML
    private void consultarJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/consultarJogos.fxml", "Consultar Jogos");
    }

    @FXML
    private void alternarConta(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus Store - Boas-vindas");
    }

    @FXML
    private void voltarPaginaInicial(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store");

    }

}

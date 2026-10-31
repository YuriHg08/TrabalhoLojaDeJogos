package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Sessao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class telaInicialController {

    @FXML
    private StackPane areaCentral;

    @FXML
    private TextField txtPesquisa;

    @FXML
    private Button painelAdm;

    @FXML
    public void initialize() {
        String nome = Sessao.getUsuarioLogado();
        String senha = Sessao.getSenhaLogada();
        painelAdm.setVisible(false);

        if (nome.equals("admin") && senha.equals("admin")) {
            painelAdm.setVisible(true);
        }
        carregarCatalogo();

    }

    private void carregarCatalogo() {
        try {
            Parent catalogo = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/senai/javafx/catalogoJogos.fxml")));
            areaCentral.getChildren().clear();
            areaCentral.getChildren().add(catalogo);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de catálogo de jogos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void menuAdm(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcao.fxml", "Fazer Login");
    }

    @FXML
    void alternarConta(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Fazer Login");
    }
}


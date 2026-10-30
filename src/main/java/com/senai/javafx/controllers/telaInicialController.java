package com.senai.javafx.controllers;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

import java.io.IOException;

    public class telaInicialController {

        @FXML
        private StackPane areaCentral;

        @FXML
        private TextField txtPesquisa;

        @FXML
        public void initialize() {
            carregarCatalogo();
        }

        private void carregarCatalogo() {
            try {
                Parent catalogo = FXMLLoader.load(getClass().getResource("/com/senai/javafx/CatalogoJogos.fxml"));
                areaCentral.getChildren().clear();
                areaCentral.getChildren().add(catalogo);
            } catch (IOException e) {
                System.err.println("Erro ao carregar a tela de catálogo de jogos: " + e.getMessage());
                e.printStackTrace();
            }
        }

        @FXML
        void alternarModoEscuro(ActionEvent event) {
            GerenciamentoJanela.alternarModoEscuro();
        }

        @FXML
        void alternarConta(ActionEvent event) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaLogin.fxml", "Fazer Login");
        }
    }


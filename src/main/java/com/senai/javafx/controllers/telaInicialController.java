package com.senai.javafx.controllers;

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
    }


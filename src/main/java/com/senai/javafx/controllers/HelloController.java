package com.senai.javafx.controllers;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;

public class    HelloController {

    @FXML
    private Label welcomeText;

    @FXML
    private TextField nomeTextField;

    @FXML
    private TextField senhaTextField;

    @FXML
    private void prosseguirCadastro() throws IOException {
        String nome = nomeTextField.getText();
        String senha = senhaTextField.getText();

        if (!nome.equals("admin") || !senha.equals("admin")) {
            welcomeText.setText("E-mail/nome de usuário ou senha inválidos.");
        } else {
            // Apenas troca o conteúdo da janela mantendo a tela cheia
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcao.fxml", "Menu de ações");
        }
    }
}
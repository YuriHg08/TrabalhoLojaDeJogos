package com.senai.javafx.controllers;

import com.senai.javafx.entidades.HelloApplication;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CadastroJogosController {

    @FXML
    TextField nomeJogo;
    @FXML
    TextField precoJogo;
    @FXML
    private void entrar(){
        String nome = nomeJogo.getText();
    }




}

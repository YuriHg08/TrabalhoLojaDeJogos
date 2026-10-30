package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Cliente;
import com.senai.javafx.entidades.ClienteCadastro;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class CadastroClientesController {

    @FXML
    private TextField nomeField;
    @FXML
    private TextField cpfField;
    @FXML
    private TextField telefoneField;
    @FXML
    private Label mensagem;

    @FXML
    private void salvar() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String telefone = telefoneField.getText();

        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
            mensagem.setText("Preencha todos os campos!");
            return;
        }
        if (telefone.length() != 11 && cpf.length() != 11) {
            mensagem.setText("O telefone e o CPF devem ter 11 dígitos!");
            return;
        }
        if (cpf.length() != 11) {
            mensagem.setText("O CPF deve ter 11 dígitos!");
            return;
        }
        if (telefone.length() != 11) {
            mensagem.setText("O telefone deve ter 11 dígitos!");
            return;
        }
        ClienteCadastro.cadastrar(new Cliente(nome, cpf, telefone));
        mensagem.setText("Cliente cadastrado!");

        nomeField.clear();
        cpfField.clear();
        telefoneField.clear();
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();

    }
}
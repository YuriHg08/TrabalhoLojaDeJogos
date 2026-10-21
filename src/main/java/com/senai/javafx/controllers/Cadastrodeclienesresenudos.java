package com.senai.javafx.controllers;
import com.senai.javafx.entidades.Cliente;
import com.senai.javafx.entidades.Clientecadastro;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
public class Cadastrodeclienesresenudos {

    @FXML private TextField nomeField;
    @FXML private TextField cpfField;
    @FXML private TextField telefoneField;
    @FXML private Label mensagem;

    @FXML
    private void salvar() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String telefone = telefoneField.getText();

        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty()) {
            mensagem.setText("Preencha todos os textos");
            return;
        }
        if (telefone.length() != 11 && cpf.length() != 11) {
            mensagem.setText("Preencha os dois campos com 11 digitos");
            return;
        }
        if (cpf.length() != 11) {
            mensagem.setText("Preencha o cpf com 11 digitos");
            return;
        }
        if (telefone.length() != 11) {
            mensagem.setText("Preencha o telefone com 11 digitos");
            return;
        }
        Clientecadastro.cadastrar(new Cliente(nome, cpf, telefone));
        mensagem.setText("Cliente cadastrado!");

        nomeField.clear();
        cpfField.clear();
        telefoneField.clear();
    }

}

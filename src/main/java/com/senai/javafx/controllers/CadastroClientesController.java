package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Cliente;
import com.senai.javafx.entidades.ClienteCadastro;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.util.Objects;

public class CadastroClientesController {

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
            mensagem.setText("Preencha todos os campos!");
            return;
        }
        if (telefone.length() != 11 && cpf.length() != 11) {
            mensagem.setText("Preencha os dois campos com 11 dígitos!");
            return;
        }
        if (cpf.length() != 11) {
            mensagem.setText("Preencha o CPF com 11 dígitos!");
            return;
        }
        if (telefone.length() != 11) {
            mensagem.setText("Preencha o telefone com 11 dígitos!");
            return;
        }
        ClienteCadastro.cadastrar(new Cliente(nome, cpf, telefone));
        mensagem.setText("Cliente cadastrado!");

        nomeField.clear();
        cpfField.clear();
        telefoneField.clear();
    }

    @FXML
    private void voltarTela(ActionEvent event) {
        try {

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/senai/javafx/opcao.fxml")));


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            stage.setScene(new Scene(root));
            stage.setTitle("Menu Principal");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
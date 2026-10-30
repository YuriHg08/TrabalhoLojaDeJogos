package com.senai.javafx.controllers;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class loginUserController {

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtTelefone;

    @FXML
    public void initialize() {
        // Bloqueia a digitação além do limite estipulado
        aplicarLimiteCaracteres(txtEmail, 254);
        aplicarLimiteCaracteres(txtTelefone, 15);
    }

    private void aplicarLimiteCaracteres(TextField field, int max) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.length() > max) {
                field.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleLogar(ActionEvent event) {
        String nome = txtNome.getText().trim();
        String senha = txtSenha.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty() || senha.isEmpty() || email.isEmpty() || telefone.isEmpty()) {
            exibirAlerta("Atenção", "Por favor, preencha todos os campos!", Alert.AlertType.WARNING);
            return;
        }

        exibirAlerta("Sucesso", "Login realizado com sucesso!", Alert.AlertType.INFORMATION);
        redirecionarParaLoja(event);
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus - Bem-vindo");
    }

    private void redirecionarParaLoja(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/senai/javafx/telaInicial.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/senai/javafx/styleMenu.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            exibirAlerta("Erro", "Erro ao carregar a tela principal: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void exibirAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
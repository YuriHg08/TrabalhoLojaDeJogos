package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.UsuarioCadastro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


import static com.senai.javafx.entidades.GerenciamentoJanela.exibirAlerta;

public class loginUserController {

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

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

        if (nome.isEmpty() || senha.isEmpty()) {
            exibirAlerta("Atenção", "Por favor, preencha todos os campos!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        // Login do administrador (não precisa de registo)
        if (nome.equals("admin") && senha.equals("admin")) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcao.fxml", "Menu de ações");
            return;
        }

        if (!UsuarioCadastro.autenticar(nome, senha)) {
            exibirAlerta("Atenção", "Utilizador ou senha inválidos!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        GerenciamentoJanela.exibirAlerta("Sucesso", "Login realizado com sucesso!", GerenciamentoJanela.TipoAlerta.SUCESSO);
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
    }

    @FXML
    private void voltarTela(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus - Bem-vindo");
    }

    @FXML
    private void handleCadastreSe(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroClienteUser.fxml", "Cadastrar-se na Nexus");
    }


}
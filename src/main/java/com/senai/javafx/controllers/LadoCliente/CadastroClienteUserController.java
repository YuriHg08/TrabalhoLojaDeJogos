package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.UsuarioCadastro;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static com.senai.javafx.entidades.GerenciamentoJanela.exibirAlerta;

public class CadastroClienteUserController {

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private void handleCadastrar(ActionEvent event) {
        String nome = txtNome.getText().trim();
        String senha = txtSenha.getText().trim();

        if (nome.isEmpty() || senha.isEmpty()) {
            exibirAlerta("Atenção", "Por favor, preencha todos os campos!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }


        if (nome.length() <= 3) {
            exibirAlerta("Atenção", "Nome de usuário precisa ter 4 ou mais dígitos", GerenciamentoJanela.TipoAlerta.ALERTA);
        return;
        }
        if (senha.length() <= 7) {
            exibirAlerta("Atenção", "Senha precisa ter 8 ou mais dígitos", GerenciamentoJanela.TipoAlerta.ALERTA);
        return;
        }
        if (!UsuarioCadastro.cadastrar(nome, senha)) {
            exibirAlerta("Atenção", "Esse utilizador já existe!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }
        exibirAlerta("Sucesso", "Conta criada com sucesso!", GerenciamentoJanela.TipoAlerta.SUCESSO);
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/loginUser.fxml", "Entrar na conta");
    }

    @FXML
    private void voltarTela(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus - Bem-vindo");
    }

    @FXML
    private void handleIniciarSessao(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/loginUser.fxml", "Entrar na conta");
    }
}
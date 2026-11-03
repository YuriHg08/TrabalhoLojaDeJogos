package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.UsuarioCadastro;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import static com.senai.javafx.entidades.GerenciamentoJanela.exibirAlerta;

public class CadastroClienteUserController {

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private PasswordField txtConfirmarSenha;

    @FXML
    private Label lblNotificacao;

    @FXML
    private void handleIniciarSessao(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/loginUser.fxml", "Nexus - Login");
    }

    @FXML
    private void handleCadastrar(ActionEvent event) {
        String nome = txtNome.getText().trim();
        String senha = txtSenha.getText().trim();
        String confirmarSenha = txtConfirmarSenha.getText().trim();

        if (nome.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            GerenciamentoJanela.mostrarNotificacao(lblNotificacao, "Preencha todos os campos!", GerenciamentoJanela.TipoNotificacao.ERRO);
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            GerenciamentoJanela.mostrarNotificacao(lblNotificacao, "As senhas não coincidem!", GerenciamentoJanela.TipoNotificacao.ERRO);
            return;
        }

        if (UsuarioCadastro.existe(nome)) {
            GerenciamentoJanela.mostrarNotificacao(lblNotificacao, "Este nome de usuário já está cadastrado!", GerenciamentoJanela.TipoNotificacao.ERRO);
            return;
        }

        if (senha.length() < 8) {
            GerenciamentoJanela.mostrarNotificacao(
                    lblNotificacao,
                    "A senha deve ter pelo menos 8 dígitos!",
                    GerenciamentoJanela.TipoNotificacao.ERRO
            );
            return;
        }



        // Salva o novo usuário
        UsuarioCadastro.cadastrar(nome, senha);

        // Sucesso! Notifica e redireciona após 1.5s
        GerenciamentoJanela.mostrarNotificacao(lblNotificacao, "Cadastro realizado com sucesso!", GerenciamentoJanela.TipoNotificacao.SUCESSO);

        PauseTransition pausa = new PauseTransition(Duration.millis(1500));
        pausa.setOnFinished(e ->
                GerenciamentoJanela.abrirJanela("/com/senai/javafx/loginUser.fxml", "Nexus - Login")
        );
        pausa.play();
    }

    @FXML
    private void voltarTela(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus - Login");
    }
}

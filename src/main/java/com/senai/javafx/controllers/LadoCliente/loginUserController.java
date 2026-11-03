package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Sessao;
import com.senai.javafx.entidades.UsuarioCadastro;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class loginUserController {

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private Label lblNotificacao;

    private PauseTransition delayNotificacao;

    @FXML
    public void initialize() {
        aplicarLimiteCaracteres(txtNome, 50);
        aplicarLimiteCaracteres(txtSenha, 30);

        // Enquanto o usuário digita, as capas da loja já vão sendo carregadas
        Platform.runLater(CatalogoJogosController::preCarregarCapas);
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

        if (nome.isEmpty() || senha.isEmpty()) {
            mostrarNotificacao("Por favor, preencha todos os campos!");
            return;
        }

        // Login do administrador
        if (nome.equals("admin") && senha.equals("admin")) {
            Sessao.iniciarSessao(nome, senha);
            entrarNaLoja(event, "Menu de ações");
            return;
        }

        // Login de usuário padrão
        if (!UsuarioCadastro.autenticar(nome, senha)) {
            mostrarNotificacao("Utilizador ou senha inválidos!");
            return;
        }

        // Salva as credenciais validadas na sessão global
        Sessao.iniciarSessao(nome, senha);

        entrarNaLoja(event, "Nexus Store - Página inicial");
    }

    // Exibe a barra de notificação animada (Toast)
    private void mostrarNotificacao(String mensagem) {
        if (lblNotificacao == null) return;

        if (delayNotificacao != null) {
            delayNotificacao.stop();
        }

        lblNotificacao.setText(mensagem);
        lblNotificacao.setOpacity(1.0);
        lblNotificacao.setVisible(true);
        lblNotificacao.setManaged(true);

        // Permanece visível por 3 segundos antes do fade-out
        delayNotificacao = new PauseTransition(Duration.seconds(3));
        delayNotificacao.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), lblNotificacao);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                lblNotificacao.setVisible(false);
                lblNotificacao.setManaged(false);
            });
            fadeOut.play();
        });

        delayNotificacao.play();
    }

    // Mostra "Entrando..." e só depois carrega a loja, para a tela não parecer congelada
    private void entrarNaLoja(ActionEvent event, String titulo) {
        if (event.getSource() instanceof Button botao) {
            botao.setDisable(true);
            botao.setText("Entrando...");
        }

        // Pausa de 60 ms só para o texto ser desenhado antes do carregamento pesado
        PauseTransition pausa = new PauseTransition(Duration.millis(60));
        pausa.setOnFinished(e ->
                GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", titulo));
        pausa.play();
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
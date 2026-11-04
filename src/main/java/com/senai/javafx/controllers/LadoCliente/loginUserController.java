package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Sessao;
import com.senai.javafx.entidades.Usuario;
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
    private Button btnLogar;

    @FXML
    private Label lblNotificacao;

    private PauseTransition delayNotificacao;

    @FXML
    public void initialize() {
        aplicarLimiteCaracteres(txtNome, 50);
        aplicarLimiteCaracteres(txtSenha, 30);

        // Ao apertar ENTER em qualquer campo, dispara exatamente o clique físico do botão Entrar
        txtNome.setOnAction(e -> btnLogar.fire());
        txtSenha.setOnAction(e -> btnLogar.fire());

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
        // 1. PRIORIDADE VISUAL: Altera o botão no exato instante da ação
        btnLogar.setDisable(true);
        btnLogar.setText("Entrando...");

        // 2. Delay perceptível (450ms) para exibir a transição de carregamento
        PauseTransition delayCarregamento = new PauseTransition(Duration.millis(450));
        delayCarregamento.setOnFinished(e -> processarLogin());
        delayCarregamento.play();
    }

    private void processarLogin() {
        String nome = txtNome.getText().trim();
        String senha = txtSenha.getText().trim();

        if (nome.isEmpty() || senha.isEmpty()) {
            restaurarBotao();
            mostrarNotificacao("Por favor, preencha todos os campos!");
            return;
        }

        // Login do administrador
        if (nome.equals("admin") && senha.equals("admin")) {
            Sessao.iniciarSessao(nome, senha);
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Menu de ações");
            return;
        }

        // Login de usuário padrão
        if (!UsuarioCadastro.autenticar(nome, senha)) {
            restaurarBotao();
            mostrarNotificacao("Utilizador ou senha inválidos!");
            return;
        }

        // Verificação de bloqueio
        Usuario usuario = UsuarioCadastro.listar().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        if (usuario != null && usuario.isBloqueado()) {
            restaurarBotao();
            mostrarNotificacao("Acesso bloqueado pela administração!");
            return;
        }

        // Login aprovado: entra na loja
        Sessao.iniciarSessao(nome, senha);
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
    }

    // Devolve o botão ao estado original caso a autenticação falhe
    private void restaurarBotao() {
        if (btnLogar != null) {
            btnLogar.setDisable(false);
            btnLogar.setText("Entrar");
        }
    }

    // Exibe a notificação animada (Toast)
    private void mostrarNotificacao(String mensagem) {
        if (lblNotificacao == null) return;

        if (delayNotificacao != null) {
            delayNotificacao.stop();
        }

        lblNotificacao.setText(mensagem);
        lblNotificacao.setOpacity(1.0);
        lblNotificacao.setVisible(true);
        lblNotificacao.setManaged(true);

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

    @FXML
    private void voltarTela(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus - Bem-vindo");
    }

    @FXML
    private void handleCadastreSe(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/cadastroClienteUser.fxml", "Cadastrar-se na Nexus");
    }
}
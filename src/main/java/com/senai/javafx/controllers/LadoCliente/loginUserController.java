package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Sessao;
import com.senai.javafx.entidades.UsuarioCadastro;
import com.senai.javafx.entidades.Usuario;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.animation.PauseTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

import static com.senai.javafx.entidades.GerenciamentoJanela.exibirAlerta;

public class loginUserController {

    @FXML
    private TextField txtNome;

    @FXML
    private PasswordField txtSenha;

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
            exibirAlerta("Atenção", "Por favor, preencha todos os campos!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        // Login do administrador
        if (nome.equals("admin") && senha.equals("admin")) {
            Sessao.iniciarSessao(nome, senha);
            entrarNaLoja(event, "Menu de ações");
            return;
        }

        // 1. Valida nome e senha
        if (!UsuarioCadastro.autenticar(nome, senha)) {
            exibirAlerta("Atenção", "Utilizador ou senha inválidos!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        // 2. Busca o objeto do usuário na lista cadastrada
        Usuario usuario = UsuarioCadastro.listar().stream()
                .filter(u -> u.getNome().equalsIgnoreCase(nome))
                .findFirst()
                .orElse(null);

        // 3. Verifica a Lista Negra / Bloqueio
        if (usuario != null && usuario.isBloqueado()) {
            exibirAlerta("Acesso Bloqueado",
                    "Seu acesso foi bloqueado pela administração da loja.",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        // Salva as credenciais validadas na sessão global
        Sessao.iniciarSessao(nome, senha);

        GerenciamentoJanela.exibirAlerta("Sucesso", "Login realizado com sucesso!", GerenciamentoJanela.TipoAlerta.SUCESSO);
        entrarNaLoja(event, "Nexus Store - Página inicial");
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

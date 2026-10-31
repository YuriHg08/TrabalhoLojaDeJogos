package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import static com.senai.javafx.entidades.GerenciamentoJanela.exibirAlerta;

public class CadastroClienteUserController {

    @FXML
    private TextField nomeUser, senhaUser;

    @FXML
    private void handleLogar(ActionEvent event) {
        String nome = nomeUser.getText().trim();
        String senha = senhaUser.getText().trim();

        if (nome.isEmpty() || senha.isEmpty()) {
            exibirAlerta("Atenção", "Por favor, preencha todos os campos!", GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        exibirAlerta("Sucesso", "Login realizado com sucesso!", GerenciamentoJanela.TipoAlerta.ALERTA);

        // Redireciona para a loja
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
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

package com.senai.javafx.controllers;

import com.senai.javafx.entidades.HelloApplication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;

public class CadastroCategoriasController {

    @FXML
    public void initialize() {
        exibicaoCategorias.setItems(categoriasSalvas);
    }

    @FXML
    private TextField nomeCategoria;

    @FXML
    private ListView<String> exibicaoCategorias;

    @FXML
    private Button cadastrarCategoria, botaoVoltar;

    @FXML
    private Label resposta;

    @FXML
    // Cria lista fixa para armazenar as categorias
    private static final ObservableList<String> categoriasSalvas = FXCollections.observableArrayList(
            "Ação", "Aventura", "RPG"
    );

    @FXML
    void botaoCadastrarCategoria() {
        String novaCategoria = nomeCategoria.getText().trim();

        // Verifica se a categoria já existe na lista
        if (categoriasSalvas.contains(novaCategoria)) {
            resposta.setText("A categoria inserida já existe!");

        } else if (novaCategoria.isEmpty()) {
            resposta.setText("Insira um nome para a categoria!");

        } else {
            // Adiciona a nova categoria, limpa a caixa de texto e retorna mensagem de sucesso
            categoriasSalvas.add(novaCategoria);
            nomeCategoria.clear();
            resposta.setText("Categoria cadastrada!");
        }
    }

    @FXML
        public void voltarTela(ActionEvent event) {
        HelloApplication.WindowManager.voltarTela("/com/senai/javafx/opcao.fxml", event);

        }

}











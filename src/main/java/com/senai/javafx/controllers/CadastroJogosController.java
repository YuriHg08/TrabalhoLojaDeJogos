package com.senai.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import static com.senai.javafx.controllers.CadastroCategoriasController.categoriasSalvas;

public class CadastroJogosController {

    @FXML
    private TextField nomeJogo;

    @FXML
    private TextField precoJogo;

    @FXML
    private MenuButton menuCategoria;


    @FXML
    public void initialize() {

        menuCategoria.getItems().clear();

        for (String categoria : categoriasSalvas) {

            MenuItem item = new MenuItem(categoria);

            item.setOnAction(event -> {
                menuCategoria.setText(categoria);

                menuCategoria.getItems().add(item);

            });
        }
    }

    @FXML
    private void entrar() {
        String nome = nomeJogo.getText();
        String preco = precoJogo.getText();

        // Recupera a categoria selecionada no botão
        String categoria = menuCategoria.getText();

        System.out.println("Nome: " + nome);
        System.out.println("Preço: " + preco);
        System.out.println("Categoria: " + categoria);
    }
}
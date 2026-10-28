package com.senai.javafx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
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
    private ComboBox<String> menuCategoria;


    @FXML
    public void initialize() {
            menuCategoria.setItems(categoriasSalvas);
        }


    }

package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Cliente;
import com.senai.javafx.entidades.Clientecadastro;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ConsultarclienteController {

    @FXML private TableView<Cliente> tabela;
    @FXML private TableColumn<Cliente, String> colCpf;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colTelefone;

    @FXML
    private void initialize() {
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCpf()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomecliente()));
        colTelefone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefone()));

        tabela.setItems(FXCollections.observableArrayList(Clientecadastro.listar()));
    }
}


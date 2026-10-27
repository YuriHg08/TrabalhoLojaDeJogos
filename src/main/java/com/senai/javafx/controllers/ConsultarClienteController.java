package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Cliente;
import com.senai.javafx.entidades.ClienteCadastro;
import com.senai.javafx.entidades.HelloApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

import javafx.scene.Node;


public class ConsultarClienteController {

    @FXML
    private TableView<Cliente> tabela;
    @FXML
    private TableColumn<Cliente, String> colCpf;
    @FXML
    private TableColumn<Cliente, String> colNome;
    @FXML
    private TableColumn<Cliente, String> colTelefone;

    @FXML
    private void initialize() {
        colCpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCpf()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomecliente()));
        colTelefone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefone()));

        tabela.setItems(FXCollections.observableArrayList(ClienteCadastro.listar()));

    }

    @FXML
    public void voltarTela(ActionEvent event) {
        HelloApplication.WindowManager.voltarTela("/com/senai/javafx/opcao.fxml", event);

    }
}

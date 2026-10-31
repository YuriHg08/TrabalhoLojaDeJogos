package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.Cliente;
import com.senai.javafx.entidades.ClienteCadastro;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.event.ActionEvent;


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
        GerenciamentoJanela.voltarTela();

    }
}

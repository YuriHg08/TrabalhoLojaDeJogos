package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Usuario;
import com.senai.javafx.entidades.UsuarioCadastro;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ConsultarClienteController {

    @FXML
    private TableView<Usuario> tabela;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private Label lblTotal;

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        tabela.setPlaceholder(new Label("Nenhum cliente cadastrado ainda."));
        tabela.setItems(FXCollections.observableArrayList(UsuarioCadastro.listar()));

        int total = tabela.getItems().size();
        lblTotal.setText("Total: " + total + (total == 1 ? " cliente" : " clientes"));
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();
    }
}
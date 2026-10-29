package com.senai.javafx.controllers;

import com.senai.javafx.entidades.CadastroJogo;
import com.senai.javafx.entidades.HelloApplication;
import com.senai.javafx.entidades.Jogo;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;

public class ConsultarJogosController {

    @FXML
    private TableView<Jogo> tabelaJogos;
    @FXML
    private TableColumn<Jogo, Double> colPrecoJogo;
    @FXML
    private TableColumn<Jogo, String> colNomeJogo;
    @FXML
    private TableColumn<Jogo, String> colCategoriaJogo;

    @FXML
    private void initialize() {

        colPrecoJogo.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getPrecoJogo()).asObject()
        );
        colNomeJogo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeJogo()));
        colCategoriaJogo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoriaJogo()));

        // Função para adicionar imagem na coluna
        adicionarImagemETextoNaColuna(colNomeJogo);

        tabelaJogos.setItems(CadastroJogosController.jogosSalvos);
    }

    /**
     * Customiza a célula da coluna para exibir uma imagem ao lado do texto.
     */
    private void adicionarImagemETextoNaColuna(TableColumn<Jogo, String> coluna) {
        coluna.setCellFactory(param -> new TableCell<Jogo, String>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final HBox container = new HBox(8); // Espaçamento de 8px entre imagem e texto

            {
                imageView.setFitWidth(32);  // Largura da imagem
                imageView.setFitHeight(32); // Altura da imagem
                imageView.setPreserveRatio(true);
                container.setAlignment(Pos.CENTER_LEFT);
                container.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Jogo jogo = getTableView().getItems().get(getIndex());

                    // Supondo que a classe Jogo tenha o método getCaminhoImagem() ou getUrlImagem()
                    // Exemplo 1: Carregando dos recursos da aplicação (/imagens/nome.png)
                    // Image img = new Image(getClass().getResourceAsStream(jogo.getCaminhoImagem()));

                    // Exemplo 2: URL ou caminho relativo simples
                    if (jogo.getCaminhoImagem() != null && !jogo.getCaminhoImagem().isEmpty()) {
                        Image img = new Image(jogo.getCaminhoImagem(), true);
                        imageView.setImage(img);
                    } else {
                        imageView.setImage(null);
                    }

                    label.setText(item);
                    setGraphic(container);
                }
            }
        });


    }

    @FXML
    public void voltarTela(ActionEvent event) {
        HelloApplication.WindowManager.voltarTela("/com/senai/javafx/opcao.fxml", event);

    }
}
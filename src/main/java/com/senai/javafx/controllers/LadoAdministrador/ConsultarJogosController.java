package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Jogo;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.OptionalInt;

public class ConsultarJogosController {

    @FXML
    private TableView<Jogo> tabelaJogos;
    @FXML
    private TableColumn<Jogo, Integer> colEstoqueJogo;
    @FXML
    private TableColumn<Jogo, String> colNomeJogo;
    @FXML
    private TableColumn<Jogo, String> colCategoriaJogo;
    @FXML
    private TableColumn<Jogo, Double> colPrecoJogo;

    @FXML
    private void initialize() {
        // Vinculação da coluna de Estoque
        colEstoqueJogo.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getEstoqueJogo()).asObject()
        );

        colPrecoJogo.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getPrecoJogo()).asObject()
        );
        colNomeJogo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeJogo()));
        colCategoriaJogo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoriaJogo()));

        // Mantém a miniatura ao lado do nome do jogo
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
            private final HBox container = new HBox(8);

            {
                imageView.setFitWidth(32);
                imageView.setFitHeight(32);
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

    // Define o mesmo estoque para todos os jogos cadastrados, no número que o administrador escolher
    @FXML
    private void definirEstoqueDeTodos() {
        int total = CadastroJogosController.jogosSalvos.size();
        if (total == 0) {
            GerenciamentoJanela.exibirAlerta("Atenção", "Não há jogos cadastrados.",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }

        OptionalInt novoEstoque = GerenciamentoJanela.solicitarNumeroInteiro(
                "Definir estoque de todos",
                "Digite o estoque que TODOS os " + total + " jogos vão passar a ter.",
                "Aplicar");

        if (novoEstoque.isEmpty()) {
            return; // cancelou
        }

        for (Jogo jogo : CadastroJogosController.jogosSalvos) {
            jogo.setEstoqueJogo(novoEstoque.getAsInt());
        }

        // A coluna de estoque lê o valor na hora de criar a célula; refresh() força reler
        tabelaJogos.refresh();

        GerenciamentoJanela.exibirAlerta("Sucesso",
                "O estoque de " + total + " jogos foi definido para " + novoEstoque.getAsInt() + ".",
                GerenciamentoJanela.TipoAlerta.SUCESSO);
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();
    }
}
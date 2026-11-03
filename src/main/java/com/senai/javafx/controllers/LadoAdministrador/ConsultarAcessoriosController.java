package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.Acessorio;
import com.senai.javafx.entidades.CatalogoAcessorios;
import com.senai.javafx.entidades.GerenciamentoJanela;
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

import java.text.NumberFormat;
import java.util.Locale;

// Lista os acessórios cadastrados (só consulta). A edição é feita pelo admin na própria loja,
// clicando no acessório, como acontece com os jogos.
public class ConsultarAcessoriosController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private TableView<Acessorio> tabelaAcessorios;
    @FXML
    private TableColumn<Acessorio, Integer> colEstoque;
    @FXML
    private TableColumn<Acessorio, String> colNome;
    @FXML
    private TableColumn<Acessorio, String> colTipo;
    @FXML
    private TableColumn<Acessorio, String> colPlataforma;
    @FXML
    private TableColumn<Acessorio, Double> colPreco;

    @FXML
    private void initialize() {
        colEstoque.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getEstoque()).asObject());
        colPreco.setCellValueFactory(c ->
                new SimpleDoubleProperty(c.getValue().getPreco()).asObject());
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colPlataforma.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPlataforma()));

        colPreco.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(Double preco, boolean vazio) {
                super.updateItem(preco, vazio);
                setText(vazio || preco == null ? null : FORMATO_PRECO.format(preco));
            }
        });

        adicionarImagemETextoNaColuna(colNome);

        tabelaAcessorios.setItems(CatalogoAcessorios.TODOS);
    }

    // Miniatura ao lado do nome, como na tabela de jogos
    private void adicionarImagemETextoNaColuna(TableColumn<Acessorio, String> coluna) {
        coluna.setCellFactory(param -> new TableCell<Acessorio, String>() {
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

                if (empty || item == null || getIndex() < 0 || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    return;
                }
                Acessorio acessorio = getTableView().getItems().get(getIndex());
                String caminho = acessorio.getCaminhoImagem();
                imageView.setImage(caminho != null && !caminho.isBlank() ? new Image(caminho, 64, 64, true, true, true) : null);
                label.setText(item);
                setGraphic(container);
            }
        });
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();
    }
}

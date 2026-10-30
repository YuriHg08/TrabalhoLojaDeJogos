package com.senai.javafx.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class CatalogoJogosController {

    @FXML
    private FlowPane containerJogos;

    @FXML
    private Label lblCategoriaAtual;

    @FXML
    public void initialize() {
        carregarJogos();
    }

    private void carregarJogos() {
        containerJogos.getChildren().clear();

        String[][] jogos = {
                {"God of War Ragnarök", "PS5 / PC", "R$ 349,90"},
                {"Elden Ring", "MULTI", "R$ 299,90"},
                {"Spider-Man 2", "PS5", "R$ 349,90"},
                {"Call of Duty: MWIII", "MULTI", "R$ 299,90"},
                {"Cyberpunk 2077", "MULTI", "R$ 199,90"},
                {"Resident Evil Village", "MULTI", "R$ 149,90"}
        };

        for (String[] jogo : jogos) {
            containerJogos.getChildren().add(criarCardJogo(jogo[0], jogo[1], jogo[2]));
        }
    }

    private VBox criarCardJogo(String titulo, String plataforma, String preco) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "card-jogo");
        card.setPrefWidth(200);

        StackPane imgHolder = new StackPane();
        imgHolder.getStyleClass().add("imagem-placeholder");
        Label lblFoto = new Label("FOTO DO JOGO");
        lblFoto.getStyleClass().add("texto-mutado");
        imgHolder.getChildren().add(lblFoto);

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("subtitulo");

        Label lblPlataforma = new Label(plataforma);
        lblPlataforma.getStyleClass().add("texto-mutado");

        HBox rodapeCard = new HBox();
        rodapeCard.setAlignment(Pos.CENTER_LEFT);

        Label lblPreco = new Label(preco);
        lblPreco.getStyleClass().add("label-preco");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnComprar = new Button("Comprar");
        btnComprar.getStyleClass().addAll("button", "botao-primario");

        rodapeCard.getChildren().addAll(lblPreco, spacer, btnComprar);
        card.getChildren().addAll(imgHolder, lblTitulo, lblPlataforma, rodapeCard);

        return card;
    }
}
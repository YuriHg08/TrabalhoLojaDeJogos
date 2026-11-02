package com.senai.javafx.controllers.LadoCliente;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;

// Banner da Loja de Acessórios: logo (controle) na largura da tela e "LOJA DE JOGOS" embaixo,
// em tons de roxo. A linha pequena embaixo mostra o filtro atual (ex.: "CONSOLES • XBOX").
public class BannerLoja extends VBox {

    public static final String SLOGAN_PADRAO = "ACESSÓRIOS • CONSOLES • PCS • CONTROLES";

    private final Label lblSlogan = new Label(SLOGAN_PADRAO);

    public BannerLoja() {
        super(6);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(22, 20, 18, 20));
        setMaxWidth(Double.MAX_VALUE);
        getStyleClass().add("banner-loja");

        Text titulo = new Text("LOJA DE JOGOS");
        titulo.getStyleClass().add("banner-loja-titulo");

        lblSlogan.getStyleClass().add("banner-loja-slogan");

        getChildren().addAll(criarLogo(), titulo, lblSlogan);
    }

    public void setSlogan(String texto) {
        lblSlogan.setText(texto == null || texto.isBlank() ? SLOGAN_PADRAO : texto.toUpperCase());
    }

    // Controle de videogame desenhado em código (não depende de arquivo de imagem)
    private Node criarLogo() {
        SVGPath corpo = new SVGPath();
        corpo.setContent("M30 6 H70 C80 6 86 10 90 20 L98 46 C101 56 96 62 88 62 C82 62 78 58 74 52 "
                + "C71 48 68 46 64 46 H36 C32 46 29 48 26 52 C22 58 18 62 12 62 C4 62 -1 56 2 46 "
                + "L10 20 C14 10 20 6 30 6 Z");
        corpo.getStyleClass().add("banner-logo-corpo");

        SVGPath direcional = new SVGPath();
        direcional.setContent("M24 18 h4 v5 h5 v4 h-5 v5 h-4 v-5 h-5 v-4 h5 z");
        direcional.getStyleClass().add("banner-logo-direcional");

        Group logo = new Group(
                ondas("M-8 20 Q-14 32 -8 44"), ondas("M-16 13 Q-25 32 -16 51"),
                ondas("M108 20 Q114 32 108 44"), ondas("M116 13 Q125 32 116 51"),
                corpo, direcional,
                botao(72, 17, "#a78bfa"), botao(80, 25, "#e879f9"),
                botao(72, 33, "#c4b5fd"), botao(64, 25, "#818cf8"));
        logo.setScaleX(1.9);
        logo.setScaleY(1.9);

        // O Group escalado não ocupa mais espaço no layout; este container reserva o tamanho certo
        VBox reserva = new VBox(logo);
        reserva.setAlignment(Pos.CENTER);
        reserva.setPadding(new Insets(34, 0, 34, 0));
        return reserva;
    }

    private SVGPath ondas(String caminho) {
        SVGPath onda = new SVGPath();
        onda.setContent(caminho);
        onda.getStyleClass().add("banner-logo-onda");
        onda.setFill(null);
        onda.setStrokeLineCap(StrokeLineCap.ROUND);
        return onda;
    }

    private Circle botao(double x, double y, String cor) {
        Circle c = new Circle(x, y, 3.6);
        c.setStyle("-fx-fill: " + cor + ";");
        return c;
    }
}

package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.controllers.LadoCliente.CatalogoJogosController;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Jogo;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Tela de vitrine (só estética): todos os jogos girando em carrossel, com nome, foto e valor
public class VitrineController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static final double LARGURA_CARD = 240;
    private static final double ALTURA_CARD = 360;
    private static final double ALTURA_FOTO = 250;
    private static final double VELOCIDADE_GRAUS_POR_SEGUNDO = -16;

    @FXML
    private StackPane palco;

    @FXML
    private StackPane raiz;

    private final List<VBox> cards = new ArrayList<>();
    private double anguloBase = 0;
    private long ultimoInstante = 0;

    private final AnimationTimer giro = new AnimationTimer() {
        @Override
        public void handle(long agora) {
            if (ultimoInstante != 0) {
                double segundos = (agora - ultimoInstante) / 1_000_000_000.0;
                anguloBase = (anguloBase + VELOCIDADE_GRAUS_POR_SEGUNDO * segundos) % 360;
            }
            ultimoInstante = agora;
            posicionarCards();
        }
    };

    private final EventHandler<KeyEvent> aoTeclar = e -> {
        if (e.getCode() == KeyCode.ESCAPE) {
            sair(null);
        }
    };

    @FXML
    private void initialize() {
        montarCards();

        // ESC também sai da vitrine (o handler é removido ao sair, para não ficar preso na janela)
        raiz.sceneProperty().addListener((obs, antiga, nova) -> {
            if (antiga != null) {
                antiga.removeEventHandler(KeyEvent.KEY_PRESSED, aoTeclar);
            }
            if (nova != null) {
                nova.addEventHandler(KeyEvent.KEY_PRESSED, aoTeclar);
                giro.start();
            } else {
                // A tela saiu da janela: para a animação para não gastar processamento
                giro.stop();
            }
        });
    }

    private void montarCards() {
        List<Jogo> jogos = new ArrayList<>(CadastroJogosController.jogosSalvos);

        if (jogos.isEmpty()) {
            Label vazio = new Label("Nenhum jogo cadastrado ainda.");
            vazio.getStyleClass().add("texto-mutado");
            palco.getChildren().add(vazio);
            return;
        }

        for (Jogo jogo : jogos) {
            VBox card = criarCard(jogo);
            cards.add(card);
            palco.getChildren().add(card);
        }
        posicionarCards();
    }

    private VBox criarCard(Jogo jogo) {
        StackPane foto = new StackPane();
        foto.getStyleClass().add("vitrine-foto");
        foto.setMinSize(LARGURA_CARD - 24, ALTURA_FOTO);
        foto.setPrefSize(LARGURA_CARD - 24, ALTURA_FOTO);
        foto.setMaxSize(LARGURA_CARD - 24, ALTURA_FOTO);

        Image capa = null;
        String caminho = jogo.getCaminhoImagem();
        if (caminho != null && !caminho.isBlank()) {
            capa = CatalogoJogosController.obterCapa(caminho);
        }
        if (capa != null) {
            aplicarCapaEnquadrada(capa, foto);
        } else {
            Label semFoto = new Label("FOTO");
            semFoto.getStyleClass().add("texto-mutado");
            foto.getChildren().add(semFoto);
        }

        Label nome = new Label(jogo.getNomeJogo());
        nome.getStyleClass().add("vitrine-nome");
        nome.setWrapText(true);
        nome.setAlignment(Pos.CENTER);
        nome.setMaxWidth(LARGURA_CARD - 24);
        nome.setMinHeight(40);
        nome.setPrefHeight(40);

        Label preco = new Label(FORMATO_PRECO.format(jogo.getPrecoJogo()));
        preco.getStyleClass().add("label-preco");

        VBox card = new VBox(10, foto, nome, preco);
        card.getStyleClass().add("vitrine-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setMinSize(LARGURA_CARD, ALTURA_CARD);
        card.setPrefSize(LARGURA_CARD, ALTURA_CARD);
        card.setMaxSize(LARGURA_CARD, ALTURA_CARD);
        // Estética: não reage a clique nem passa o mouse
        card.setMouseTransparent(true);
        return card;
    }

    // Enquadra a capa inteira no quadro da foto (sem cortar nem deformar), com fundo desfocado nas sobras
    private void aplicarCapaEnquadrada(Image capa, StackPane foto) {
        double largura = LARGURA_CARD - 24;
        double altura = ALTURA_FOTO;

        // Recorte arredondado, para acompanhar a borda do quadro
        Rectangle recorte = new Rectangle(largura, altura);
        recorte.setArcWidth(16);
        recorte.setArcHeight(16);
        foto.setClip(recorte);

        Runnable montar = () -> {
            if (capa.isError() || capa.getWidth() <= 0 || capa.getHeight() <= 0) {
                return;
            }

            double proporcaoAlvo = largura / altura;
            double proporcaoImagem = capa.getWidth() / capa.getHeight();

            double x = 0;
            double y = 0;
            double larguraCorte = capa.getWidth();
            double alturaCorte = capa.getHeight();

            if (proporcaoImagem > proporcaoAlvo) {
                // Imagem mais larga que o quadro: corta as laterais
                larguraCorte = capa.getHeight() * proporcaoAlvo;
                x = (capa.getWidth() - larguraCorte) / 2;
            } else {
                // Imagem mais alta que o quadro: corta em cima e embaixo
                alturaCorte = capa.getWidth() / proporcaoAlvo;
                y = (capa.getHeight() - alturaCorte) / 2;
            }

            // Fundo: a própria capa ampliada e desfocada, só para preencher as sobras do quadro
            ImageView fundo = new ImageView(capa);
            fundo.setViewport(new Rectangle2D(x, y, larguraCorte, alturaCorte));
            fundo.setFitWidth(largura);
            fundo.setFitHeight(altura);
            fundo.setPreserveRatio(false);
            fundo.setOpacity(0.45);
            fundo.setEffect(new GaussianBlur(18));

            // Frente: a imagem inteira, sem cortar nada
            ImageView frente = new ImageView(capa);
            frente.setFitWidth(largura);
            frente.setFitHeight(altura);
            frente.setPreserveRatio(true);
            frente.setSmooth(true);

            foto.getChildren().setAll(fundo, frente);
        };

        // A capa pode ainda estar carregando em segundo plano
        if (capa.getProgress() >= 1.0) {
            montar.run();
        } else {
            capa.progressProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> obs, Number antigo, Number novo) {
                    if (novo.doubleValue() >= 1.0) {
                        capa.progressProperty().removeListener(this);
                        montar.run();
                    }
                }
            });
        }
    }

    // Distribui os cards num anel: os da frente ficam grandes e nítidos, os de trás pequenos e apagados
    private void posicionarCards() {
        int total = cards.size();
        if (total == 0) {
            return;
        }

        double raio = Math.max(240, Math.min(palco.getWidth() * 0.36, 520));

        for (int i = 0; i < total; i++) {
            double angulo = Math.toRadians(anguloBase + i * 360.0 / total);
            double profundidade = (Math.cos(angulo) + 1) / 2; // 1 = frente, 0 = fundo

            VBox card = cards.get(i);
            card.setTranslateX(Math.sin(angulo) * raio);
            card.setTranslateY(-(1 - profundidade) * 50);
            card.setScaleX(0.45 + 0.55 * profundidade);
            card.setScaleY(0.45 + 0.55 * profundidade);
            card.setOpacity(0.2 + 0.8 * profundidade);
            // viewOrder menor = mais à frente
            card.setViewOrder(1 - profundidade);
        }
    }

    @FXML
    private void sair(ActionEvent event) {
        giro.stop();
        // Volta para a tela que já estava montada (instantâneo).
        // Se não houver, reconstrói a página inicial.
        if (!GerenciamentoJanela.restaurarTelaGuardada("Nexus Store - Página inicial")) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
        }
    }

    // Chamado pelo botão da tela inicial: guarda a tela atual e abre a vitrine por cima
    public static void abrir(Parent telaAtual) {
        GerenciamentoJanela.guardarTela(telaAtual);
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/vitrine.fxml", "Nexus Store - Vitrine");
    }
}
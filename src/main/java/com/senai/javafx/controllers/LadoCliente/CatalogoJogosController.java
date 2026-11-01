
package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.controllers.LadoAdministrador.CadastroJogosController;
import com.senai.javafx.entidades.FiltroCatalogo;
import com.senai.javafx.entidades.Jogo;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Node;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;
import com.senai.javafx.controllers.LadoCliente.DetalhesJogosController;

public class CatalogoJogosController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private FlowPane containerJogos;

    @FXML
    private Label lblCategoriaAtual;

    private final InvalidationListener aoMudarFiltro = obs -> carregarJogos();
    private final ListChangeListener<Jogo> aoMudarJogos = mudanca -> carregarJogos();

    @FXML
    public void initialize() {

        FiltroCatalogo.categoriaProperty().addListener(aoMudarFiltro);
        FiltroCatalogo.consoleProperty().addListener(aoMudarFiltro);
        CadastroJogosController.jogosSalvos.addListener(aoMudarJogos);
        FiltroCatalogo.buscaProperty().addListener(aoMudarFiltro);
        FiltroCatalogo.precoMinProperty().addListener(aoMudarFiltro);
        FiltroCatalogo.precoMaxProperty().addListener(aoMudarFiltro);

        // Remove os listeners quando a tela sair da cena
        containerJogos.sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova == null) {
                FiltroCatalogo.categoriaProperty().removeListener(aoMudarFiltro);
                FiltroCatalogo.consoleProperty().removeListener(aoMudarFiltro);
                CadastroJogosController.jogosSalvos.removeListener(aoMudarJogos);
                FiltroCatalogo.buscaProperty().removeListener(aoMudarFiltro);
                FiltroCatalogo.precoMinProperty().removeListener(aoMudarFiltro);
                FiltroCatalogo.precoMaxProperty().removeListener(aoMudarFiltro);
            }
        });

        carregarJogos();
    }

    private void carregarJogos() {

        containerJogos.getChildren().clear();
        atualizarTitulo();

        for (Jogo jogo : CadastroJogosController.jogosSalvos) {
            if (FiltroCatalogo.aceita(jogo)) {
                containerJogos.getChildren().add(criarCardJogo(jogo));
            }
        }

        if (containerJogos.getChildren().isEmpty()) {
            Label vazio = new Label(FiltroCatalogo.temBuscaOuPreco()
                    ? "Nenhum jogo encontrado para essa busca."
                    : "Nenhum jogo encontrado com esse filtro.");
            containerJogos.getChildren().add(vazio);
        }
    }

    private void atualizarTitulo() {

        String categoria = FiltroCatalogo.getCategoria();
        String console = FiltroCatalogo.getConsole();

        if (categoria == null && console == null) {
            lblCategoriaAtual.setText("Catálogo de jogos");
        } else if (categoria != null && console != null) {
            lblCategoriaAtual.setText(categoria + " • " + console);
        } else {
            lblCategoriaAtual.setText(categoria != null ? categoria : console);
        }
    }

    private VBox criarCardJogo(Jogo jogo) {

        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "card-jogo");
        card.setPrefWidth(200);

        // Área da imagem
        StackPane imgHolder = new StackPane();
        imgHolder.getStyleClass().add("imagem-placeholder");
        imgHolder.setPrefSize(176, 110);

        // Imagem padrão caso o jogo não tenha capa
        Label lblFoto = new Label("FOTO DO JOGO");
        lblFoto.getStyleClass().add("texto-mutado");
        imgHolder.getChildren().add(lblFoto);

        // Carrega automaticamente a capa do jogo
        String caminho = jogo.getCaminhoImagem();

        if (caminho != null && !caminho.isBlank()) {

            // Carregamento síncrono para obter as dimensões da imagem
            Image imagem = new Image(caminho);

            if (!imagem.isError() && imagem.getWidth() > 0 && imagem.getHeight() > 0) {

                ImageView imagemView = new ImageView(imagem);

                double largura = 176;
                double altura = 110;

                double proporcaoAlvo = largura / altura;
                double proporcaoImagem = imagem.getWidth() / imagem.getHeight();

                double x = 0;
                double y = 0;

                double larguraCorte = imagem.getWidth();
                double alturaCorte = imagem.getHeight();

                // Corta as laterais ou a parte superior/inferior
                // para preencher todo o espaço sem deformar
                if (proporcaoImagem > proporcaoAlvo) {

                    larguraCorte = imagem.getHeight() * proporcaoAlvo;
                    x = (imagem.getWidth() - larguraCorte) / 2;

                } else {

                    alturaCorte = imagem.getWidth() / proporcaoAlvo;
                    y = (imagem.getHeight() - alturaCorte) / 2;
                }

                // Define a área que será exibida
                imagemView.setViewport(new Rectangle2D(
                        x,
                        y,
                        larguraCorte,
                        alturaCorte
                ));

                imagemView.setFitWidth(largura);
                imagemView.setFitHeight(altura);
                imagemView.setPreserveRatio(false);
                imagemView.setSmooth(true);

                imgHolder.getChildren().setAll(imagemView);
            }
        }

        // Nome do jogo
        Label lblTitulo = new Label(jogo.getNomeJogo());
        lblTitulo.getStyleClass().add("subtitulo");

        // Console
        Label lblPlataforma = new Label(
                jogo.getConsoleJogo() != null ? jogo.getConsoleJogo() : ""
        );
        lblPlataforma.getStyleClass().add("texto-mutado");

        // Rodapé do card
        HBox rodapeCard = new HBox();
        rodapeCard.setAlignment(Pos.CENTER_LEFT);

        Label lblPreco = new Label(
                FORMATO_PRECO.format(jogo.getPrecoJogo())
        );
        lblPreco.getStyleClass().add("label-preco");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnComprar = new Button("Comprar");
        btnComprar.getStyleClass().addAll("button", "botao-primario");

        rodapeCard.getChildren().addAll(lblPreco, spacer, btnComprar);

        // Adiciona os elementos ao card
        card.getChildren().addAll(
                imgHolder,
                lblTitulo,
                lblPlataforma,
                rodapeCard
        );


        card.setOnMouseClicked(e -> abrirDetalhesJogo(card, jogo));

        // Usa setOnMouseClicked no botão para que o e.consume() evite o duplo clique no card
        btnComprar.setOnMouseClicked(e -> {
            e.consume();
            abrirDetalhesJogo(btnComprar, jogo);
        });

        return card;
    }
        private void abrirDetalhesJogo(javafx.scene.Node node, Jogo jogo) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/senai/javafx/detalhesJogos.fxml")
                );
                Parent root = loader.load();

                // Passa o objeto Jogo completo para a nova tela
                DetalhesJogosController controller = loader.getController();
                controller.setJogo(jogo);

                Scene scene = ((Stage) node.getScene().getWindow()).getScene();
                scene.getStylesheets().add(getClass().getResource("/com/senai/javafx/styleMenu.css").toExternalForm());
                scene.setRoot(root);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





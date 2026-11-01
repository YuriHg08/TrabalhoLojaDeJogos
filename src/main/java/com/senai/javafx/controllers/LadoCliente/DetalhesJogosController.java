package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Jogo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class DetalhesJogosController {

    @FXML
    private Label lblNomeJogo;

    @FXML
    private ImageView imgJogo;

    @FXML
    private Label lblInfoExtra;

    @FXML
    private Button btnComprar;

    @FXML
    private Button btnVoltar;

    private double precoAtual;
    private Jogo jogoAtual;

    public void setJogo(Jogo jogo) {
        this.jogoAtual = jogo;

        if (lblNomeJogo != null) {
            lblNomeJogo.setText(jogo.getNomeJogo());
        }

        if (lblInfoExtra != null) {
            String categoria = jogo.getCategoriaJogo() != null ? jogo.getCategoriaJogo() : "GERAL";
            String console = jogo.getConsoleJogo() != null ? jogo.getConsoleJogo() : "TODAS";
            lblInfoExtra.setText(categoria.toUpperCase() + " | " + console.toUpperCase());
        }

        if (btnComprar != null) {
            btnComprar.setText(String.format("COMPRAR R$ %.2f", jogo.getPrecoJogo()));
        }

        // Carrega a imagem do jogo
        carregarImagemComCorte(jogo.getCaminhoImagem(), 390, 270);
    }

    private void carregarImagemComCorte(String caminho, double larguraAlvo, double alturaAlvo) {
        if (caminho == null || caminho.isBlank() || imgJogo == null) {
            return;
        }

        try {
            Image imagem = new Image(caminho);

            if (!imagem.isError() && imagem.getWidth() > 0 && imagem.getHeight() > 0) {
                double proporcaoAlvo = larguraAlvo / alturaAlvo;
                double proporcaoImagem = imagem.getWidth() / imagem.getHeight();

                double x = 0;
                double y = 0;
                double larguraCorte = imagem.getWidth();
                double alturaCorte = imagem.getHeight();

                if (proporcaoImagem > proporcaoAlvo) {
                    larguraCorte = imagem.getHeight() * proporcaoAlvo;
                    x = (imagem.getWidth() - larguraCorte) / 2;
                } else {
                    alturaCorte = imagem.getWidth() / proporcaoAlvo;
                    y = (imagem.getHeight() - alturaCorte) / 2;
                }

                imgJogo.setViewport(new Rectangle2D(x, y, larguraCorte, alturaCorte));
                imgJogo.setFitWidth(larguraAlvo);
                imgJogo.setFitHeight(alturaAlvo);
                imgJogo.setPreserveRatio(false);
                imgJogo.setSmooth(true);
                imgJogo.setImage(imagem);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem em detalhes: " + e.getMessage());
        }
    }

    /**
     * Método para preencher a tela dinamicamente quando o usuário clica num jogo.
     */
    public void setDadosJogo(String nome, String categoria, String plataforma, double preco, String caminhoImagem) {
        this.precoAtual = preco;
        lblNomeJogo.setText(nome);
        lblInfoExtra.setText(categoria.toUpperCase() + " | " + plataforma.toUpperCase());
        btnComprar.setText(String.format("COMPRAR R$ %.2f", preco));

        if (caminhoImagem != null && !caminhoImagem.isEmpty()) {
            try {
                imgJogo.setImage(new Image(getClass().getResourceAsStream(caminhoImagem)));
            } catch (Exception e) {
                // Mantém a caixa sem imagem se o caminho falhar
            }
        }
    }

    @FXML
    private void handleComprar(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra Realizada");
        alert.setHeaderText(null);
        alert.setContentText("Obrigado por adquirir " + lblNomeJogo.getText() + "!");
        alert.showAndWait();
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/senai/javafx/telaInicial.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1200, 700);
            scene.getStylesheets().add(getClass().getResource("/com/senai/javafx/styleMenu.css").toExternalForm());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
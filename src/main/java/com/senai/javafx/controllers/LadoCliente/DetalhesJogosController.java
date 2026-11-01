package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.GerenciamentoJanela;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

public class DetalhesJogosController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private Label lblNomeJogo;

    @FXML
    private ImageView imgJogo;

    @FXML
    private Label lblInfoExtra;

    @FXML
    private Label lblEstoque;

    @FXML
    private Spinner<Integer> spnQuantidade;

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

        atualizarControlesDeEstoque();

        // Carrega a imagem do jogo
        carregarImagemComCorte(jogo.getCaminhoImagem(), 390, 270);
    }
    // Atualiza seletor, texto de estoque e botão conforme o estoque atual do jogo
    private void atualizarControlesDeEstoque() {
        int estoque = jogoAtual.getEstoqueJogo();
        boolean semEstoque = estoque <= 0;

        if (spnQuantidade != null) {
            // A quantidade máxima é o estoque disponível (0 a 0 quando não há nada)
            SpinnerValueFactory<Integer> valueFactory = semEstoque
                    ? new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0)
                    : new SpinnerValueFactory.IntegerSpinnerValueFactory(1, estoque, 1);
            spnQuantidade.setValueFactory(valueFactory);
            spnQuantidade.setDisable(semEstoque);
        }

        atualizarTextoEstoque(estoque);

        if (semEstoque) {
            mostrarSemEstoque();
        } else {
            restaurarBotaoCompra();
            atualizarTextoBotaoCompra(1);
        }
    }

    private void restaurarBotaoCompra() {
        if (btnComprar == null) {
            return;
        }
        btnComprar.getStyleClass().remove("botao-sem-estoque");
        if (!btnComprar.getStyleClass().contains("botao-primario")) {
            btnComprar.getStyleClass().add("botao-primario");
        }
        btnComprar.setDisable(false);
    }

    // Mostra para o cliente quantas unidades existem no estoque
    private void atualizarTextoEstoque(int estoque) {
        if (lblEstoque == null) {
            return;
        }

        lblEstoque.getStyleClass().removeAll("estoque-ok", "estoque-baixo", "estoque-zerado");

        if (estoque <= 0) {
            lblEstoque.setText("Produto esgotado");
            lblEstoque.getStyleClass().add("estoque-zerado");
        } else if (estoque <= 5) {
            lblEstoque.setText(estoque == 1
                    ? "Resta apenas 1 unidade em estoque!"
                    : "Restam apenas " + estoque + " unidades em estoque!");
            lblEstoque.getStyleClass().add("estoque-baixo");
        } else {
            lblEstoque.setText("Em estoque: " + estoque + " unidades");
            lblEstoque.getStyleClass().add("estoque-ok");
        }
    }

    // Troca o botão de comprar por "SEM ESTOQUE" em vermelho
    private void mostrarSemEstoque() {
        if (btnComprar == null) {
            return;
        }

        btnComprar.setText("SEM ESTOQUE");
        btnComprar.getStyleClass().remove("botao-primario");
        if (!btnComprar.getStyleClass().contains("botao-sem-estoque")) {
            btnComprar.getStyleClass().add("botao-sem-estoque");
        }
        btnComprar.setDisable(true);
    }

    private void atualizarTextoBotaoCompra(int quantidade) {
        if (btnComprar != null && jogoAtual != null && jogoAtual.getEstoqueJogo() > 0) {
            double total = jogoAtual.getPrecoJogo() * quantidade;
            btnComprar.setText("COMPRAR " + FORMATO_PRECO.format(total));
        }
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
        btnComprar.setText(String.format("Comprar (R$ %.2f)", preco));

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
        // Proteção extra: nunca permite comprar mais do que existe em estoque
        int estoque = jogoAtual != null ? jogoAtual.getEstoqueJogo() : 0;
        int quantidade = spnQuantidade.getValue() != null ? spnQuantidade.getValue() : 0;

        if (estoque <= 0 || quantidade < 1 || quantidade > estoque) {
            GerenciamentoJanela.exibirAlerta("Atenção",
                    "Quantidade acima do estoque disponível!",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }
        // Desconta do estoque a quantidade que o cliente comprou
        jogoAtual.setEstoqueJogo(estoque - quantidade);

        // Atualiza a tela na hora (estoque restante, limite do seletor, botão "SEM ESTOQUE")
        atualizarControlesDeEstoque();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Compra realizada");
        alert.setHeaderText(null);
        alert.setContentText("Obrigado por adquirir " + quantidade + "x " + lblNomeJogo.getText() + "!");
        alert.showAndWait();
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        // Volta para a tela que já estava montada (instantâneo).
        // Se não houver, reconstrói como antes.
        if (!GerenciamentoJanela.restaurarTelaGuardada("Nexus Store - Página inicial")) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
        }
    }
    @FXML
    public void initialize() {
        // Escutador (registrado uma vez só) para atualizar o preço total ao mudar a quantidade
        spnQuantidade.valueProperty().addListener((obs, valorAntigo, valorNovo) -> {
            if (valorNovo != null) {
                atualizarTextoBotaoCompra(valorNovo);
            }
        });
    }
}
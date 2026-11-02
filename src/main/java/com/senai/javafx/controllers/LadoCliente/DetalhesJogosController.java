package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Carrinho;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Acessorio;
import com.senai.javafx.entidades.Produto;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.text.NumberFormat;
import java.util.Locale;

public class DetalhesJogosController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static final double LARGURA_IMAGEM = 390;
    private static final double ALTURA_IMAGEM = 270;

    // Estilos que o botão de compra alterna entre si (a classe "button" nunca é mexida)
    private static final String[] CLASSES_BOTAO = {"botao-primario", "botao-secundario", "botao-sem-estoque"};

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

    // Espaço no rodapé onde a barra do carrinho é colocada
    @FXML
    private StackPane rodape;

    // Pode ser um jogo ou um item da Loja de Acessórios
    private Produto jogoAtual;

    // Mostra "✓ ADICIONADO" por um instante e depois volta o botão ao normal
    private final PauseTransition fimDoAviso = new PauseTransition(Duration.millis(1200));

    @FXML
    public void initialize() {
        rodape.getChildren().add(new BarraCarrinho());

        // Registrado uma vez só: atualiza o preço do botão conforme a quantidade escolhida
        spnQuantidade.valueProperty().addListener((obs, antigo, novo) -> {
            if (jogoAtual != null && novo != null && novo > 0 && !btnComprar.isDisable()) {
                btnComprar.setText("COMPRAR " + FORMATO_PRECO.format(jogoAtual.getPreco() * novo));
            }
        });
        fimDoAviso.setOnFinished(e -> atualizarControlesDeEstoque());
    }

    // Abre a tela de detalhes de um produto (jogo ou acessório), guardando a loja de onde veio
    public static void abrir(Node origem, Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    DetalhesJogosController.class.getResource("/com/senai/javafx/detalhesJogos.fxml"));
            Parent root = loader.load();

            DetalhesJogosController controller = loader.getController();
            controller.setProduto(produto);

            Scene scene = ((Stage) origem.getScene().getWindow()).getScene();

            // Só adiciona o CSS se ainda não estiver na cena
            String css = DetalhesJogosController.class.getResource("/com/senai/javafx/styleMenu.css").toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
            GerenciamentoJanela.guardarTela(scene.getRoot());
            scene.setRoot(root);

        } catch (Exception e) {
            // O erro não pode ficar só no console: o botão pareceria "morto"
            e.printStackTrace();
            GerenciamentoJanela.exibirAlerta("Erro",
                    "Não foi possível abrir os detalhes de \"" + produto.getNome() + "\".\n" + e,
                    GerenciamentoJanela.TipoAlerta.ERRO);
        }
    }

    // Chamado logo depois de carregar a tela
    public void setProduto(Produto produto) {
        this.jogoAtual = produto;

        lblNomeJogo.setText(produto.getNome());

        lblInfoExtra.setWrapText(true);
        lblInfoExtra.setText(produto.getEtiqueta());

        atualizarControlesDeEstoque();
        carregarImagemComCorte(produto.getCaminhoImagem());
    }

    // Atualiza seletor, texto de estoque e botão conforme o estoque atual do jogo
    private void atualizarControlesDeEstoque() {
        int estoque = jogoAtual.getEstoque();
        int noCarrinho = Carrinho.quantidadeDoProduto(jogoAtual);
        // O que ainda dá para adicionar: estoque menos o que já está no carrinho
        int disponivel = Carrinho.disponivelParaAdicionar(jogoAtual);

        spnQuantidade.setValueFactory(disponivel <= 0
                ? new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0)
                : new SpinnerValueFactory.IntegerSpinnerValueFactory(1, disponivel, 1));
        spnQuantidade.setDisable(disponivel <= 0);

        atualizarTextoEstoque(estoque, noCarrinho);

        if (estoque <= 0) {
            configurarBotao("SEM ESTOQUE", "botao-sem-estoque", true);
        } else if (disponivel <= 0) {
            configurarBotao("TODO O ESTOQUE JÁ ESTÁ NO CARRINHO", "botao-secundario", true);
        } else {
            configurarBotao("COMPRAR " + FORMATO_PRECO.format(jogoAtual.getPreco()), "botao-primario", false);
        }
    }

    private void configurarBotao(String texto, String classe, boolean desabilitado) {
        btnComprar.setText(texto);
        btnComprar.getStyleClass().removeAll(CLASSES_BOTAO);
        btnComprar.getStyleClass().add(classe);
        btnComprar.setDisable(desabilitado);
    }

    // Mostra para o cliente quantas unidades existem no estoque
    private void atualizarTextoEstoque(int estoque, int noCarrinho) {
        lblEstoque.getStyleClass().removeAll("estoque-ok", "estoque-baixo", "estoque-zerado");

        String texto;
        String classe;
        if (estoque <= 0) {
            texto = "Produto esgotado";
            classe = "estoque-zerado";
        } else if (estoque <= 5) {
            texto = estoque == 1
                    ? "Resta apenas 1 unidade em estoque!"
                    : "Restam apenas " + estoque + " unidades em estoque!";
            classe = "estoque-baixo";
        } else {
            texto = "Em estoque: " + estoque + " unidades";
            classe = "estoque-ok";
        }

        if (noCarrinho > 0) {
            texto += "  •  " + noCarrinho + " no seu carrinho";
        }
        lblEstoque.setText(texto);
        lblEstoque.getStyleClass().add(classe);
    }

    private void carregarImagemComCorte(String caminho) {
        if (caminho == null || caminho.isBlank()) {
            return;
        }

        try {
            Image imagem = new Image(caminho);
            if (imagem.isError() || imagem.getWidth() <= 0 || imagem.getHeight() <= 0) {
                return;
            }

            // Corta as laterais (ou o topo/rodapé) para preencher a moldura sem deformar
            double proporcaoAlvo = LARGURA_IMAGEM / ALTURA_IMAGEM;
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
            imgJogo.setFitWidth(LARGURA_IMAGEM);
            imgJogo.setFitHeight(ALTURA_IMAGEM);
            imgJogo.setPreserveRatio(false);
            imgJogo.setSmooth(true);
            imgJogo.setImage(imagem);
        } catch (Exception e) {
            System.err.println("Erro ao carregar a imagem em detalhes: " + e.getMessage());
        }
    }

    @FXML
    private void handleComprar(ActionEvent event) {
        // Comprar = colocar no carrinho. O estoque só é descontado quando o pagamento é concluído.
        // Carrinho.adicionar já recusa quantidade acima do estoque disponível.
        Integer quantidade = spnQuantidade.getValue();

        if (jogoAtual == null || quantidade == null || !Carrinho.adicionar(jogoAtual, quantidade)) {
            GerenciamentoJanela.exibirAlerta("Atenção",
                    "Quantidade acima do estoque disponível!",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            if (jogoAtual != null) {
                atualizarControlesDeEstoque();
            }
            return;
        }

        // A barra "Ir para o carrinho" aparece sozinha embaixo; aqui atualizamos o seletor e o botão
        atualizarControlesDeEstoque();

        // Confirmação visível no próprio botão (se ainda houver o que comprar)
        if (!btnComprar.isDisable()) {
            btnComprar.setText("✓ ADICIONADO AO CARRINHO");
            fimDoAviso.playFromStart();
        }
    }

    @FXML
    private void handleVoltar(ActionEvent event) {
        // Volta para a tela que já estava montada (instantâneo).
        // Se não houver, reconstrói como antes.
        if (!GerenciamentoJanela.restaurarTelaGuardada("Nexus Store - Página inicial")) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
        }
    }
}

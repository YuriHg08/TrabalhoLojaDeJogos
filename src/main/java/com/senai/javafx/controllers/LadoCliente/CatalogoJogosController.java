package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.controllers.LadoAdministrador.CadastroJogosController;
import com.senai.javafx.controllers.apoieNosController;
import com.senai.javafx.entidades.FiltroCatalogo;
import com.senai.javafx.entidades.Jogo;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import com.senai.javafx.controllers.LadoAdministrador.EditarJogoController;
import com.senai.javafx.entidades.Sessao;
import javafx.scene.CacheHint;
import javafx.scene.Node;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CatalogoJogosController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Tamanho máximo (em pixels) em que as capas são mantidas na memória.
    private static final double LIMITE_CAPA = 420;

    // Cache de imagens: cada capa é lida só uma vez
    private static final Map<String, Image> CACHE_IMAGENS = new HashMap<>();

    @FXML
    private FlowPane containerJogos;

    @FXML
    private Label lblCategoriaAtual;

    // Cards já montados: ao filtrar, só reaproveitamos os que já existem
    private final Map<Jogo, VBox> cacheCards = new HashMap<>();

    // Evita recarregar várias vezes seguidas quando vários filtros mudam juntos
    private boolean recargaPendente = false;
    private boolean listenersLigados = false;
    private final InvalidationListener aoMudarFiltro = obs -> agendarRecarga();
    private final ListChangeListener<Jogo> aoMudarJogos = mudanca -> {
        cacheCards.clear(); // jogo novo/removido/editado: refaz os cards
        agendarRecarga();
    };

    // Botão "Apoie-nos": abre a janela com a imagem
    @FXML
    private void abrirApoieNos() {
        apoieNosController.abrir(containerJogos.getScene().getWindow());
    }

    @FXML
    public void initialize() {

        ligarListeners();

        // Tira os listeners quando a tela sai da cena e recoloca quando ela volta
        containerJogos.sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova == null) {
                desligarListeners();
            } else if (!listenersLigados) {
                ligarListeners();
                // Algo pode ter mudado enquanto estava fora (ex.: estoque após uma compra)
                cacheCards.clear();
                agendarRecarga();
            }
        });

        carregarJogos();
    }

    private void ligarListeners() {
        if (listenersLigados) {
            return;
        }
        listenersLigados = true;
        FiltroCatalogo.categoriasSelecionadas().addListener(aoMudarFiltro);
        FiltroCatalogo.consolesSelecionados().addListener(aoMudarFiltro);
        FiltroCatalogo.buscaProperty().addListener(aoMudarFiltro);
        FiltroCatalogo.precoMinProperty().addListener(aoMudarFiltro);
        FiltroCatalogo.precoMaxProperty().addListener(aoMudarFiltro);
        CadastroJogosController.jogosSalvos.addListener(aoMudarJogos);
    }

    private void desligarListeners() {
        if (!listenersLigados) {
            return;
        }
        listenersLigados = false;
        FiltroCatalogo.categoriasSelecionadas().removeListener(aoMudarFiltro);
        FiltroCatalogo.consolesSelecionados().removeListener(aoMudarFiltro);
        FiltroCatalogo.buscaProperty().removeListener(aoMudarFiltro);
        FiltroCatalogo.precoMinProperty().removeListener(aoMudarFiltro);
        FiltroCatalogo.precoMaxProperty().removeListener(aoMudarFiltro);
        CadastroJogosController.jogosSalvos.removeListener(aoMudarJogos);
    }

    // Junta várias mudanças de filtro (busca + preço mín + preço máx) em uma só recarga
    private void agendarRecarga() {
        if (recargaPendente) {
            return;
        }
        recargaPendente = true;
        Platform.runLater(() -> {
            recargaPendente = false;
            carregarJogos();
        });
    }

    private void carregarJogos() {

        atualizarTitulo();

        List<Node> visiveis = new ArrayList<>();

        for (Jogo jogo : CadastroJogosController.jogosSalvos) {
            if (FiltroCatalogo.aceita(jogo)) {
                // Reaproveita o card se já foi criado antes
                visiveis.add(cacheCards.computeIfAbsent(jogo, this::criarCardJogo));
            }
        }

        if (visiveis.isEmpty()) {
            Label vazio = new Label(FiltroCatalogo.temBuscaOuPreco()
                    ? "Nenhum jogo encontrado para essa busca."
                    : "Nenhum jogo encontrado com esse filtro.");
            vazio.getStyleClass().add("texto-mutado");
            visiveis.add(vazio);
        }

        // Uma única atualização da tela em vez de clear() + vários add()
        containerJogos.getChildren().setAll(visiveis);
    }

    private void atualizarTitulo() {

        String categoria = String.join(", ", FiltroCatalogo.categoriasSelecionadas());
        String console = String.join(", ", FiltroCatalogo.consolesSelecionados());

        if (categoria.isEmpty() && console.isEmpty()) {
            lblCategoriaAtual.setText("Catálogo de jogos");
        } else if (!categoria.isEmpty() && !console.isEmpty()) {
            lblCategoriaAtual.setText(categoria + " • " + console);
        } else {
            lblCategoriaAtual.setText(!categoria.isEmpty() ? categoria : console);
        }
    }

    // Carrega a capa uma única vez, em segundo plano, com tamanho limitado
    public static Image obterCapa(String caminho) {
        try {
            return CACHE_IMAGENS.computeIfAbsent(caminho,
                    c -> new Image(c, LIMITE_CAPA, LIMITE_CAPA, true, true, true));
        } catch (Exception e) {
            return null;
        }
    }
    // Chamado antes de abrir a loja: começa a carregar as capas em segundo plano
    public static void preCarregarCapas() {
        for (Jogo jogo : CadastroJogosController.jogosSalvos) {
            String caminho = jogo.getCaminhoImagem();
            if (caminho != null && !caminho.isBlank()) {
                obterCapa(caminho);
            }
        }
    }

    // Coloca a capa no card assim que ela estiver pronta (sem travar a tela)
    public static void aplicarCapa(Image imagem, StackPane imgHolder) {

        if (imagem.getProgress() >= 1.0) {
            montarCapa(imagem, imgHolder);
            return;
        }

        imagem.progressProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number antigo, Number novo) {
                if (novo.doubleValue() >= 1.0) {
                    imagem.progressProperty().removeListener(this);
                    montarCapa(imagem, imgHolder);
                }
            }
        });
    }

    public static void montarCapa(Image imagem, StackPane imgHolder) {

        if (imagem.isError() || imagem.getWidth() <= 0 || imagem.getHeight() <= 0) {
            return;
        }

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

    private VBox criarCardJogo(Jogo jogo) {

        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "card-jogo");
        card.setPrefWidth(200);

        // Guarda o card já desenhado (com sombra) como "foto"
        card.setCache(true);
        card.setCacheHint(CacheHint.SPEED);

        // Área da imagem
        StackPane imgHolder = new StackPane();
        imgHolder.getStyleClass().add("imagem-placeholder");
        imgHolder.setPrefSize(176, 110);

        // Imagem padrão caso o jogo não tenha capa
        Label lblFoto = new Label("FOTO DO JOGO");
        lblFoto.getStyleClass().add("texto-mutado");
        imgHolder.getChildren().add(lblFoto);

        // Carrega a capa do jogo (cache + segundo plano)
        String caminho = jogo.getCaminhoImagem();

        if (caminho != null && !caminho.isBlank()) {
            Image imagem = obterCapa(caminho);
            if (imagem != null) {
                aplicarCapa(imagem, imgHolder);
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
        lblPlataforma.setWrapText(true);
        lblPlataforma.setMaxWidth(176);

        // Rodapé do card
        HBox rodapeCard = new HBox();
        rodapeCard.setAlignment(Pos.CENTER_LEFT);
        rodapeCard.setSpacing(8); // espaço entre o preço e o botão

        Label lblPreco = new Label(
                FORMATO_PRECO.format(jogo.getPrecoJogo())
        );
        lblPreco.getStyleClass().add("label-preco");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Administrador vê "Editar"; cliente vê "Comprar" (ou "Sem estoque" em vermelho)
        boolean admin = Sessao.isAdmin();
        boolean semEstoque = jogo.getEstoqueJogo() <= 0;

        String textoBotao;
        String classeBotao;
        if (admin) {
            textoBotao = "Editar";
            classeBotao = "botao-primario";
        } else if (semEstoque) {
            textoBotao = "Sem estoque";
            classeBotao = "botao-sem-estoque";
        } else {
            textoBotao = "Comprar";
            classeBotao = "botao-primario";
        }

        Button btnComprar = new Button(textoBotao);
        btnComprar.getStyleClass().addAll("button", classeBotao);
        rodapeCard.getChildren().addAll(lblPreco, spacer, btnComprar);

        // Adiciona os elementos ao card
        card.getChildren().addAll(
                imgHolder,
                lblTitulo,
                lblPlataforma,
                rodapeCard
        );

        // O botão usa onAction: dispara ao soltar o mouse (mesmo com um leve movimento) e também pelo teclado.
        // O clique no resto do card ignora o que veio do botão, para não abrir a tela duas vezes.
        btnComprar.setOnAction(e -> abrirJogo(btnComprar, jogo, admin));
        card.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Node alvo) || !estaDentro(alvo, btnComprar)) {
                abrirJogo(card, jogo, admin);
            }
        });

        return card;
    }

    // true se "alvo" é o próprio "ancestral" ou está dentro dele (ex.: o texto de dentro de um botão)
    private static boolean estaDentro(Node alvo, Node ancestral) {
        for (Node n = alvo; n != null; n = n.getParent()) {
            if (n == ancestral) {
                return true;
            }
        }
        return false;
    }

    // Admin abre a janela de edição; cliente abre a tela de detalhes/compra
    private void abrirJogo(Node origem, Jogo jogo, boolean admin) {
        if (admin) {
            EditarJogoController.abrir(jogo, origem.getScene().getWindow());
        } else {
            abrirDetalhesJogo(origem, jogo);
        }
    }

    private void abrirDetalhesJogo(Node node, Jogo jogo) {
        DetalhesJogosController.abrir(node, jogo);
    }

}

package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Acessorio;
import com.senai.javafx.entidades.Carrinho;
import com.senai.javafx.entidades.CatalogoAcessorios;
import com.senai.javafx.entidades.FiltroCatalogo;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

// Loja de Acessórios: consoles (PS2 ao PS5, Xbox 360 ao Series, Switch 1 e 2), PCs gamer e controles.
// Funciona como a loja de jogos: card -> detalhes -> carrinho -> pagamento -> nota.
public class LojaAcessoriosController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static final String CLASSE_ATIVO = "botao-categoria-ativo";

    @FXML
    private StackPane areaCentral;
    @FXML
    private TextField txtPesquisa, txtPrecoMin, txtPrecoMax;
    @FXML
    private VBox boxTipos, boxPlataformas;
    @FXML
    private Button btnTodos;

    // Montados no código (como o catálogo de jogos)
    private final BannerLoja banner = new BannerLoja();
    private final FlowPane containerProdutos = new FlowPane(15, 15);

    // Filtros: dentro de cada grupo basta bater com UM marcado; os dois grupos valem juntos
    private final Set<Acessorio.Tipo> tipos = new LinkedHashSet<>();
    private final Set<String> plataformas = new LinkedHashSet<>();
    private String busca;
    private Double precoMin;
    private Double precoMax;

    private final Map<Acessorio, VBox> cacheCards = new HashMap<>();
    private final PauseTransition atrasoBusca = new PauseTransition(Duration.millis(250));

    @FXML
    public void initialize() {
        montarBotoesTipo();
        montarBotoesPlataforma();
        configurarBusca();
        montarAreaCentral();

        // Ao voltar para esta tela (ex.: depois de uma compra) o estoque pode ter mudado
        containerProdutos.sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova != null) {
                cacheCards.clear();
                carregarProdutos();
            }
        });

        carregarProdutos();
    }

    // ---------------------------------------------------------------- MONTAGEM DA TELA

    private void montarAreaCentral() {
        VBox conteudo = new VBox(20, banner, containerProdutos);
        conteudo.getStyleClass().add("conteudo-loja");
        conteudo.setPadding(new Insets(20));

        ScrollPane rolagem = new ScrollPane(conteudo);
        rolagem.setFitToWidth(true);
        rolagem.getStyleClass().add("custom-scroll-pane");
        VBox.setVgrow(rolagem, Priority.ALWAYS);

        // Catálogo em cima e, embaixo, a barra do carrinho (só aparece com itens)
        areaCentral.getChildren().setAll(new VBox(rolagem, new BarraCarrinho()));
    }

    private void montarBotoesTipo() {
        for (Acessorio.Tipo tipo : Acessorio.Tipo.values()) {
            Button botao = criarBotaoFiltro(tipo.getRotuloPlural(), null);
            botao.setUserData(tipo);
            botao.setOnAction(e -> {
                if (!tipos.remove(tipo)) {
                    tipos.add(tipo);
                }
                aoMudarFiltro();
            });
            boxTipos.getChildren().add(botao);
        }
    }

    private void montarBotoesPlataforma() {
        for (String plataforma : CatalogoAcessorios.PLATAFORMAS) {
            Button botao = criarBotaoFiltro(plataforma, iconeDaPlataforma(plataforma));
            botao.setUserData(plataforma);
            botao.setOnAction(e -> {
                if (!plataformas.remove(plataforma)) {
                    plataformas.add(plataforma);
                }
                aoMudarFiltro();
            });
            boxPlataformas.getChildren().add(botao);
        }
    }

    private Button criarBotaoFiltro(String texto, Node icone) {
        Button botao = new Button(texto);
        botao.setMaxWidth(Double.MAX_VALUE);
        botao.getStyleClass().addAll("button", "botao-categoria");
        if (icone != null) {
            botao.setGraphic(icone);
            botao.setContentDisplay(ContentDisplay.LEFT);
            botao.setGraphicTextGap(12);
        }
        return botao;
    }

    // Mesmos ícones já usados na pasta de imagens (Nintendo, Playstation, Xbox e Desktop para PC)
    private Node iconeDaPlataforma(String plataforma) {
        String arquivo = switch (plataforma) {
            case "Nintendo" -> "Nintendo.png";
            case "Playstation" -> "Playstation.png";
            case "Xbox" -> "Xbox.png";
            default -> "Desktop.png";
        };
        URL url = getClass().getResource("/com/senai/javafx/imagens/" + arquivo);
        if (url == null) {
            return null;
        }
        ImageView icone = new ImageView(new Image(url.toExternalForm(), 44, 44, true, true));
        icone.setFitWidth(22);
        icone.setFitHeight(22);
        icone.setPreserveRatio(true);
        return icone;
    }

    // ---------------------------------------------------------------- BUSCA E FILTROS

    private void configurarBusca() {
        atrasoBusca.setOnFinished(e -> aplicarBusca());

        txtPesquisa.textProperty().addListener((obs, antigo, novo) -> atrasoBusca.playFromStart());
        txtPesquisa.setOnAction(e -> aplicarBusca());

        for (TextField campo : List.of(txtPrecoMin, txtPrecoMax)) {
            // Só aceita números, vírgula e ponto
            campo.setTextFormatter(new TextFormatter<String>(
                    mudanca -> mudanca.getControlNewText().matches("[0-9.,]*") ? mudanca : null));
            campo.textProperty().addListener((obs, antigo, novo) -> atrasoBusca.playFromStart());
            campo.setOnAction(e -> aplicarBusca());
        }
    }

    private void aplicarBusca() {
        atrasoBusca.stop();
        String texto = txtPesquisa.getText();
        busca = texto == null || texto.isBlank() ? null : texto.trim();
        precoMin = FiltroCatalogo.converterPreco(txtPrecoMin.getText());
        precoMax = FiltroCatalogo.converterPreco(txtPrecoMax.getText());
        aoMudarFiltro();
    }

    @FXML
    private void mostrarTodos(ActionEvent event) {
        txtPesquisa.clear();
        txtPrecoMin.clear();
        txtPrecoMax.clear();
        atrasoBusca.stop();
        busca = null;
        precoMin = null;
        precoMax = null;
        tipos.clear();
        plataformas.clear();
        aoMudarFiltro();
    }

    private void aoMudarFiltro() {
        atualizarDestaques();
        carregarProdutos();
    }

    private boolean aceita(Acessorio produto) {
        boolean okTipo = tipos.isEmpty() || tipos.contains(produto.getTipoAcessorio());
        boolean okPlataforma = plataformas.isEmpty()
                || plataformas.stream().anyMatch(produto::temPlataforma);
        return okTipo && okPlataforma && aceitaNome(produto) && aceitaPreco(produto);
    }

    private boolean aceitaNome(Acessorio produto) {
        if (busca == null) {
            return true;
        }
        return normalizar(produto.getNome()).contains(normalizar(busca));
    }

    private boolean aceitaPreco(Acessorio produto) {
        Double min = precoMin;
        Double max = precoMax;
        // Se o usuário inverter os valores, corrige sozinho
        if (min != null && max != null && min > max) {
            Double troca = min;
            min = max;
            max = troca;
        }
        double preco = produto.getPreco();
        return (min == null || preco >= min) && (max == null || preco <= max);
    }

    // Ignora maiúsculas/minúsculas e acentos: "série" encontra "Series"
    private static String normalizar(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    // Destaca na sidebar o que está filtrado
    private void atualizarDestaques() {
        boolean semFiltro = tipos.isEmpty() && plataformas.isEmpty() && busca == null
                && precoMin == null && precoMax == null;
        setAtivo(btnTodos, semFiltro);

        for (Node botao : boxTipos.getChildren()) {
            setAtivo((Button) botao, tipos.contains(botao.getUserData()));
        }
        for (Node botao : boxPlataformas.getChildren()) {
            setAtivo((Button) botao, plataformas.contains(botao.getUserData()));
        }
    }

    private void setAtivo(Button botao, boolean ativo) {
        botao.getStyleClass().remove(CLASSE_ATIVO);
        if (ativo) {
            botao.getStyleClass().add(CLASSE_ATIVO);
        }
    }

    // ---------------------------------------------------------------- CARDS

    private void carregarProdutos() {
        atualizarTitulo();

        List<Node> visiveis = new ArrayList<>();
        for (Acessorio produto : CatalogoAcessorios.TODOS) {
            if (aceita(produto)) {
                // Reaproveita o card se já foi criado antes
                visiveis.add(cacheCards.computeIfAbsent(produto, this::criarCard));
            }
        }

        if (visiveis.isEmpty()) {
            Label vazio = new Label("Nenhum produto encontrado com esse filtro.");
            vazio.getStyleClass().add("texto-mutado");
            visiveis.add(vazio);
        }

        containerProdutos.getChildren().setAll(visiveis);
    }

    private void atualizarTitulo() {
        List<String> partes = new ArrayList<>();
        tipos.forEach(t -> partes.add(t.getRotuloPlural()));
        partes.addAll(plataformas);
        banner.setSlogan(partes.isEmpty() ? BannerLoja.SLOGAN_PADRAO : String.join(" • ", partes));
    }

    private VBox criarCard(Acessorio produto) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "card-jogo");
        card.setPrefWidth(200);
        card.setCache(true);
        card.setCacheHint(CacheHint.SPEED);

        // Imagem (mesma moldura e mesmo corte dos jogos)
        StackPane imgHolder = new StackPane();
        imgHolder.getStyleClass().add("imagem-placeholder");
        imgHolder.setPrefSize(176, 110);
        Label lblFoto = new Label("FOTO");
        lblFoto.getStyleClass().add("texto-mutado");
        imgHolder.getChildren().add(lblFoto);

        String caminho = produto.getCaminhoImagem();
        if (caminho != null && !caminho.isBlank()) {
            Image imagem = CatalogoJogosController.obterCapa(caminho);
            if (imagem != null) {
                CatalogoJogosController.aplicarCapa(imagem, imgHolder);
            }
        }

        Label lblNome = new Label(produto.getNome());
        lblNome.getStyleClass().add("subtitulo");

        // Plataforma e detalhes (ex.: "Playstation • 1 TB"); altura fixa para os cards ficarem alinhados
        Label lblDescricao = new Label(produto.getDescricao());
        lblDescricao.getStyleClass().add("texto-mutado");
        lblDescricao.setWrapText(true);
        lblDescricao.setMaxWidth(176);
        lblDescricao.setMinHeight(Region.USE_PREF_SIZE);
        lblDescricao.setPrefHeight(48);
        lblDescricao.setMaxHeight(48);
        lblDescricao.setAlignment(Pos.TOP_LEFT);

        Label lblPreco = new Label(FORMATO_PRECO.format(produto.getPreco()));
        lblPreco.getStyleClass().add("label-preco");

        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);

        boolean semEstoque = produto.getEstoque() <= 0;
        Button btnComprar = new Button(semEstoque ? "Sem estoque" : "Comprar");
        btnComprar.getStyleClass().addAll("button", semEstoque ? "botao-sem-estoque" : "botao-primario");

        HBox rodape = new HBox(8, lblPreco, espaco, btnComprar);
        rodape.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(imgHolder, lblNome, lblDescricao, rodape);

        // O botão usa onAction (também funciona pelo teclado); o clique no resto do card
        // ignora o que veio do botão para não abrir a tela duas vezes.
        btnComprar.setOnAction(e -> DetalhesJogosController.abrir(btnComprar, produto));
        card.setOnMouseClicked(e -> {
            if (!(e.getTarget() instanceof Node alvo) || !estaDentro(alvo, btnComprar)) {
                DetalhesJogosController.abrir(card, produto);
            }
        });

        return card;
    }

    private static boolean estaDentro(Node alvo, Node ancestral) {
        for (Node n = alvo; n != null; n = n.getParent()) {
            if (n == ancestral) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- NAVEGAÇÃO

    // Mesmo botão da loja de jogos: volta para a escolha de conta/login
    @FXML
    private void alternarConta(ActionEvent event) {
        // Com itens no carrinho o cliente precisa concluir o pagamento (ou remover os itens) primeiro
        if (!Carrinho.isVazio()) {
            GerenciamentoJanela.exibirAlerta("Atenção",
                    "Você tem itens no carrinho. Conclua o pagamento antes de trocar de conta.",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Fazer Login");
    }

    @FXML
    private void voltarLojaJogos(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
    }
}

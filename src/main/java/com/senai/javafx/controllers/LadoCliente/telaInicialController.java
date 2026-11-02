package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Acessorio;
import com.senai.javafx.entidades.Carrinho;
import com.senai.javafx.entidades.CatalogoAcessorios;
import com.senai.javafx.entidades.FiltroCatalogo;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Sessao;
import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.senai.javafx.controllers.LadoAdministrador.CadastroCategoriasController.categoriasSalvas;

public class telaInicialController {

    private static final String CLASSE_ATIVO = "botao-categoria-ativo";
    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private StackPane areaCentral;

    @FXML
    private TextField txtPesquisa;

    @FXML
    private TextField txtPrecoMin;

    @FXML
    private TextField txtPrecoMax;

    private final PauseTransition atrasoBusca = new PauseTransition(Duration.millis(250));

    @FXML
    private Button painelAdm;

    @FXML
    private VBox boxCategorias;

    @FXML
    private Button btnInicio, btnNintendo, btnPlaystation, btnXbox, btnPC;

    // Guarda a plataforma única selecionada (null = modo catálogo de jogos)
    private String plataformaSelecionada = null;

    private final Map<Acessorio, VBox> cacheCardsAcessorios = new HashMap<>();

    private final InvalidationListener aoMudarFiltro = obs -> atualizarDestaques();
    private final ListChangeListener<String> aoMudarCategorias = mudanca -> montarCategorias();

    @FXML
    public void initialize() {
        String nome = Sessao.getUsuarioLogado();
        String senha = Sessao.getSenhaLogada();
        painelAdm.setVisible(false);

        if (nome.equals("admin") && senha.equals("admin")) {
            painelAdm.setVisible(true);
        }

        FiltroCatalogo.limpar();
        configurarBusca();

        montarCategorias();
        FiltroCatalogo.categoriasSelecionadas().addListener(new WeakInvalidationListener(aoMudarFiltro));
        categoriasSalvas.addListener(new WeakListChangeListener<>(aoMudarCategorias));

        carregarCatalogo();
    }

    private void configurarBusca() {
        atrasoBusca.setOnFinished(e -> aplicarBusca());

        txtPesquisa.textProperty().addListener((obs, antigo, novo) -> atrasoBusca.playFromStart());
        txtPesquisa.setOnAction(e -> aplicarBusca());

        for (TextField campo : List.of(txtPrecoMin, txtPrecoMax)) {
            campo.setTextFormatter(new TextFormatter<String>(
                    mudanca -> mudanca.getControlNewText().matches("[0-9.,]*") ? mudanca : null));
            campo.textProperty().addListener((obs, antigo, novo) -> atrasoBusca.playFromStart());
            campo.setOnAction(e -> aplicarBusca());
        }
    }

    private void aplicarBusca() {
        atrasoBusca.stop();
        if (plataformaSelecionada != null) {
            // Se estiver navegando nos acessórios, atualiza a exibição deles
            carregarAcessorios(plataformaSelecionada);
        } else {
            // Se estiver nos jogos, atualiza o filtro global de jogos
            FiltroCatalogo.setBusca(txtPesquisa.getText());
            FiltroCatalogo.setPrecoMin(FiltroCatalogo.converterPreco(txtPrecoMin.getText()));
            FiltroCatalogo.setPrecoMax(FiltroCatalogo.converterPreco(txtPrecoMax.getText()));
        }
    }

    private void montarCategorias() {
        boxCategorias.getChildren().clear();

        for (String categoria : categoriasSalvas) {
            Button botao = new Button(categoria);
            botao.setMaxWidth(Double.MAX_VALUE);
            botao.getStyleClass().addAll("button", "botao-categoria");
            botao.setUserData(categoria);
            botao.setOnAction(e -> {
                // Se estava em acessórios, volta para a tela de jogos
                if (plataformaSelecionada != null) {
                    plataformaSelecionada = null;
                    carregarCatalogo();
                }
                FiltroCatalogo.alternarCategoria(categoria);
                atualizarDestaques();
            });
            boxCategorias.getChildren().add(botao);
        }

        atualizarDestaques();
    }

    private void atualizarDestaques() {
        Set<String> categorias = FiltroCatalogo.categoriasSelecionadas();
        boolean noInicio = categorias.isEmpty() && plataformaSelecionada == null;
        setAtivo(btnInicio, noInicio);

        // Seleção única: apenas a plataforma ativa ganha a classe de destaque
        for (Button botao : List.of(btnNintendo, btnPlaystation, btnXbox, btnPC)) {
            setAtivo(botao, Objects.equals(plataformaSelecionada, botao.getUserData()));
        }

        for (Node botao : boxCategorias.getChildren()) {
            setAtivo((Button) botao, plataformaSelecionada == null && categorias.contains(botao.getUserData()));
        }
    }

    private void setAtivo(Button botao, boolean ativo) {
        botao.getStyleClass().remove(CLASSE_ATIVO);
        if (ativo) {
            botao.getStyleClass().add(CLASSE_ATIVO);
        }
    }

    @FXML
    void filtrarInicio(ActionEvent event) {
        plataformaSelecionada = null;
        txtPesquisa.clear();
        txtPrecoMin.clear();
        txtPrecoMax.clear();
        atrasoBusca.stop();
        FiltroCatalogo.limpar();
        carregarCatalogo();
        atualizarDestaques();
    }

    // Ação única para os botões de plataforma: filtra a loja de acessórios
    @FXML
    void filtrarConsole(ActionEvent event) {
        String console = (String) ((Node) event.getSource()).getUserData();

        // Se clicou na mesma plataforma já ativa, desseleciona e volta para o catálogo inicial
        if (Objects.equals(plataformaSelecionada, console)) {
            plataformaSelecionada = null;
            FiltroCatalogo.limpar();
            carregarCatalogo();
        } else {
            // Seleção única: ativa apenas a nova plataforma
            plataformaSelecionada = console;
            FiltroCatalogo.limpar(); // Desmarca filtros de jogos
            cacheCardsAcessorios.clear();
            carregarAcessorios(console);
        }
        atualizarDestaques();
    }

    private void carregarCatalogo() {
        try {
            Parent catalogo = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/senai/javafx/catalogoJogos.fxml")));
            VBox.setVgrow(catalogo, Priority.ALWAYS);
            areaCentral.getChildren().setAll(new VBox(catalogo, new BarraCarrinho()));
        } catch (IOException e) {
            System.err.println("Erro ao carregar catálogo de jogos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Exibe na área central os acessórios/consoles da plataforma escolhida
    private void carregarAcessorios(String plataforma) {
        FlowPane containerProdutos = new FlowPane(15, 15);
        containerProdutos.setPadding(new Insets(20));

        List<Node> visiveis = new ArrayList<>();
        for (Acessorio produto : CatalogoAcessorios.TODOS) {
            if (produto.temPlataforma(plataforma) && aceitaAcessorio(produto)) {
                visiveis.add(cacheCardsAcessorios.computeIfAbsent(produto, this::criarCardAcessorio));
            }
        }

        if (visiveis.isEmpty()) {
            Label vazio = new Label("Nenhum acessório ou console encontrado para " + plataforma + ".");
            vazio.getStyleClass().add("texto-mutado");
            visiveis.add(vazio);
        }

        containerProdutos.getChildren().setAll(visiveis);

        ScrollPane rolagem = new ScrollPane(containerProdutos);
        rolagem.setFitToWidth(true);
        rolagem.getStyleClass().add("custom-scroll-pane");
        VBox.setVgrow(rolagem, Priority.ALWAYS);

        areaCentral.getChildren().setAll(new VBox(rolagem, new BarraCarrinho()));
    }

    private boolean aceitaAcessorio(Acessorio produto) {
        String texto = txtPesquisa.getText();
        if (texto != null && !texto.isBlank()) {
            String buscaNorm = normalizar(texto.trim());
            String nomeNorm = normalizar(produto.getNome());
            if (!nomeNorm.contains(buscaNorm)) {
                return false;
            }
        }

        Double min = FiltroCatalogo.converterPreco(txtPrecoMin.getText());
        Double max = FiltroCatalogo.converterPreco(txtPrecoMax.getText());
        if (min != null && max != null && min > max) {
            Double temp = min;
            min = max;
            max = temp;
        }

        double preco = produto.getPreco();
        return (min == null || preco >= min) && (max == null || preco <= max);
    }

    private static String normalizar(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    private VBox criarCardAcessorio(Acessorio produto) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "card-jogo");
        card.setPrefWidth(200);
        card.setCache(true);
        card.setCacheHint(CacheHint.SPEED);

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
            if (n == ancestral) return true;
        }
        return false;
    }

    @FXML
    void menuAdm(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcao.fxml", "Fazer Login");
    }

    @FXML
    void alternarConta(ActionEvent event) {
        if (!Carrinho.isVazio()) {
            GerenciamentoJanela.exibirAlerta("Atenção",
                    "Você tem itens no carrinho. Conclua o pagamento antes de trocar de conta.",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Fazer Login");
    }
}
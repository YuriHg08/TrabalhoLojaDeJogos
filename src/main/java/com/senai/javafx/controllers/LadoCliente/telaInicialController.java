package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.FiltroCatalogo;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Sessao;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.WeakListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.senai.javafx.controllers.LadoAdministrador.CadastroCategoriasController.categoriasSalvas;

public class telaInicialController {

    private static final String CLASSE_ATIVO = "botao-categoria-ativo";

    @FXML
    private StackPane areaCentral;

    @FXML
    private TextField txtPesquisa;

    @FXML
    private Button painelAdm;

    @FXML
    private VBox boxCategorias;

    @FXML
    private Button btnInicio, btnNintendo, btnPlaystation, btnXbox, btnPC;

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

        montarCategorias();
        FiltroCatalogo.categoriaProperty().addListener(new WeakInvalidationListener(aoMudarFiltro));
        FiltroCatalogo.consoleProperty().addListener(new WeakInvalidationListener(aoMudarFiltro));
        categoriasSalvas.addListener(new WeakListChangeListener<String>(aoMudarCategorias));

        carregarCatalogo();
    }

    // Cria um botão para cada categoria cadastrada
    private void montarCategorias() {
        boxCategorias.getChildren().clear();

        for (String categoria : categoriasSalvas) {
            Button botao = new Button(categoria);
            botao.setMaxWidth(Double.MAX_VALUE);
            botao.getStyleClass().addAll("button", "botao-categoria");
            botao.setUserData(categoria);
            botao.setOnAction(e -> FiltroCatalogo.alternarCategoria(categoria));
            boxCategorias.getChildren().add(botao);
        }

        atualizarDestaques();
    }

    // Destaca na sidebar o que está filtrado
    private void atualizarDestaques() {
        String categoria = FiltroCatalogo.getCategoria();
        String console = FiltroCatalogo.getConsole();

        setAtivo(btnInicio, categoria == null && console == null);

        for (Button botao : List.of(btnNintendo, btnPlaystation, btnXbox, btnPC)) {
            setAtivo(botao, Objects.equals(botao.getUserData(), console));
        }

        for (Node botao : boxCategorias.getChildren()) {
            setAtivo((Button) botao, Objects.equals(botao.getUserData(), categoria));
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
        FiltroCatalogo.limpar();
    }

    @FXML
    void filtrarConsole(ActionEvent event) {
        String console = (String) ((Node) event.getSource()).getUserData();
        FiltroCatalogo.alternarConsole(console);
    }

    private void carregarCatalogo() {
        try {
            Parent catalogo = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/senai/javafx/catalogoJogos.fxml")));
            areaCentral.getChildren().clear();
            areaCentral.getChildren().add(catalogo);
        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de catálogo de jogos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void menuAdm(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcao.fxml", "Fazer Login");
    }

    @FXML
    void alternarConta(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Fazer Login");
    }
}
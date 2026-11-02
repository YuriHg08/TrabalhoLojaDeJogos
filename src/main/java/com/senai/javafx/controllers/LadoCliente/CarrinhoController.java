package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Carrinho;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.ItemCarrinho;
import com.senai.javafx.entidades.CatalogoProdutos;
import com.senai.javafx.entidades.Produto;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarrinhoController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private VBox listaItens;

    @FXML
    private Label lblContagem;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnPagamento;

    @FXML
    public void initialize() {
        // O estoque pode ter mudado desde que o cliente adicionou o produto
        List<String> avisos = Carrinho.ajustarAoEstoque(CatalogoProdutos::existe);

        montarLista();

        if (!avisos.isEmpty()) {
            // runLater: o alerta é uma janela modal e não pode abrir enquanto a tela ainda carrega
            Platform.runLater(() -> GerenciamentoJanela.exibirAlerta(
                    "Carrinho atualizado", String.join("\n", avisos), GerenciamentoJanela.TipoAlerta.ALERTA));
        }
    }

    private void montarLista() {
        listaItens.getChildren().clear();

        if (Carrinho.isVazio()) {
            Label vazio = new Label("Seu carrinho está vazio.");
            vazio.getStyleClass().add("texto-mutado");
            listaItens.getChildren().add(vazio);
        } else {
            for (ItemCarrinho item : Carrinho.getItens()) {
                listaItens.getChildren().add(criarLinha(item));
            }
        }

        atualizarResumo();
    }

    private HBox criarLinha(ItemCarrinho item) {
        Produto produto = item.getProduto();

        Label lblNome = new Label(produto.getNome());
        lblNome.getStyleClass().add("subtitulo");

        // Jogo: plataformas • Console/Controle/PC: tipo e detalhes
        String linhaApoio = "Jogo".equals(produto.getTipo())
                ? produto.getDescricao()
                : produto.getTipo() + " • " + produto.getDescricao();
        Label lblConsole = new Label(linhaApoio);
        lblConsole.getStyleClass().add("texto-mutado");

        VBox info = new VBox(3, lblNome, lblConsole);
        info.setMinWidth(0);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label lblUnitario = new Label(FORMATO_PRECO.format(produto.getPreco()) + " cada");
        lblUnitario.getStyleClass().add("texto-mutado");
        lblUnitario.setMinWidth(110);

        // A quantidade máxima é o estoque disponível
        Spinner<Integer> spnQuantidade = new Spinner<>();
        spnQuantidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                1, Math.max(1, produto.getEstoque()), item.getQuantidade()));
        spnQuantidade.getStyleClass().add("spinner-custom");
        spnQuantidade.setPrefWidth(90);

        Label lblSubtotal = new Label(FORMATO_PRECO.format(item.getSubtotal()));
        lblSubtotal.getStyleClass().add("label-preco");
        lblSubtotal.setMinWidth(110);
        lblSubtotal.setAlignment(Pos.CENTER_RIGHT);

        spnQuantidade.valueProperty().addListener((obs, antigo, novo) -> {
            if (novo == null) {
                return;
            }
            Carrinho.definirQuantidade(item, novo);
            lblSubtotal.setText(FORMATO_PRECO.format(item.getSubtotal()));
            atualizarResumo();
        });

        Button btnRemover = new Button("Remover");
        btnRemover.getStyleClass().addAll("button", "botao-secundario");
        btnRemover.setOnAction(e -> {
            Carrinho.remover(item);
            montarLista();
        });

        HBox linha = new HBox(16, info, lblUnitario, spnQuantidade, lblSubtotal, btnRemover);
        linha.setAlignment(Pos.CENTER_LEFT);
        linha.getStyleClass().add("item-carrinho");
        return linha;
    }

    private void atualizarResumo() {
        int produtos = Carrinho.getQuantidadeItens();
        int unidades = Carrinho.getQuantidadeUnidades();

        lblContagem.setText(produtos + (produtos == 1 ? " item" : " itens")
                + "  •  " + unidades + (unidades == 1 ? " unidade" : " unidades"));
        lblTotal.setText(FORMATO_PRECO.format(Carrinho.getSubtotal()));
        btnPagamento.setDisable(Carrinho.isVazio());
    }

    // Para quem entrou no carrinho sem querer
    @FXML
    private void voltarLoja(ActionEvent event) {
        if (!GerenciamentoJanela.restaurarTelaGuardada("Nexus Store - Página inicial")) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
        }
    }

    @FXML
    private void irParaPagamento(ActionEvent event) {
        if (Carrinho.isVazio()) {
            return;
        }
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/pagamento.fxml", "Nexus Store - Pagamento");
    }
}

package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Compra;
import com.senai.javafx.entidades.ItemCompra;
import com.senai.javafx.entidades.RegistroCompras;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// Histórico de compras do administrador (compras que ele já permitiu)
public class ConsultarComprasController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TableView<Compra> tabelaCompras;
    @FXML
    private TableColumn<Compra, String> colNota, colData, colCliente, colItens,
            colQuantidade, colPagamento, colTotal;
    @FXML
    private TableColumn<Compra, Void> colNotaDetalhe;
    @FXML
    private Label lblResumo;

    @FXML
    private void initialize() {
        colNota.setCellValueFactory(c -> new SimpleStringProperty(numeroNota(c.getValue())));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataCompra().format(FORMATO_DATA)));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeCliente()));
        colItens.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResumoItens()));
        colQuantidade.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getQuantidadeTotal())));
        colPagamento.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFormaPagamento().getRotulo()));
        colTotal.setCellValueFactory(c ->
                new SimpleStringProperty(FORMATO_PRECO.format(c.getValue().getTotal())));

        colNotaDetalhe.setCellFactory(coluna -> new TableCell<Compra, Void>() {
            private final Button btnVer = new Button("Ver nota");

            {
                btnVer.getStyleClass().addAll("button", "botao-secundario", "botao-tabela");
                btnVer.setOnAction(e -> {
                    Compra compra = getTableRow().getItem();
                    if (compra != null) {
                        abrirNota(compra);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                super.updateItem(item, vazio);
                setGraphic(vazio || getTableRow() == null || getTableRow().getItem() == null ? null : btnVer);
            }
        });

        Label vazio = new Label("Nenhuma compra no histórico ainda.");
        vazio.getStyleClass().add("texto-mutado");
        tabelaCompras.setPlaceholder(vazio);

        List<Compra> compras = RegistroCompras.listarHistorico();
        tabelaCompras.setItems(FXCollections.observableArrayList(compras));

        double faturamento = compras.stream().mapToDouble(Compra::getTotal).sum();
        lblResumo.setText(compras.size() + (compras.size() == 1 ? " compra" : " compras")
                + "  •  Total vendido: " + FORMATO_PRECO.format(faturamento));
    }

    private static String numeroNota(Compra compra) {
        return String.format("%04d", compra.getNumero());
    }

    // Janela com a nota completa: cliente, data, pagamento, item por item e totais
    private void abrirNota(Compra compra) {
        Stage janela = new Stage();
        janela.initModality(Modality.APPLICATION_MODAL);
        janela.initOwner(tabelaCompras.getScene().getWindow());
        janela.setTitle("Nota nº " + numeroNota(compra));
        janela.setResizable(false);

        Label titulo = new Label("NOTA Nº " + numeroNota(compra));
        titulo.getStyleClass().add("titulo-tela");

        VBox cabecalho = new VBox(4,
                linhaTexto("Cliente: ", compra.getNomeCliente()),
                linhaTexto("CPF: ", compra.getCpf()),
                linhaTexto("Data da compra: ", compra.getDataCompra().format(FORMATO_DATA)),
                linhaTexto("Forma de pagamento: ", compra.getFormaPagamento().getRotulo()));

        // Tabela de itens: nome | qtd | unitário | subtotal
        GridPane itens = new GridPane();
        itens.setHgap(18);
        itens.setVgap(6);
        ColumnConstraints colNome = new ColumnConstraints();
        colNome.setHgrow(Priority.ALWAYS);
        itens.getColumnConstraints().add(colNome);

        itens.add(cabecalhoGrade("ITEM"), 0, 0);
        itens.add(cabecalhoGrade("QTD."), 1, 0);
        itens.add(cabecalhoGrade("UNITÁRIO"), 2, 0);
        itens.add(cabecalhoGrade("SUBTOTAL"), 3, 0);

        int linha = 1;
        for (ItemCompra item : compra.getItens()) {
            Label nome = new Label(item.getNome());
            nome.getStyleClass().add("subtitulo");
            nome.setWrapText(true);
            nome.setMaxWidth(260);
            // Segunda linha: o que é (Jogo, Console, PC Gamer, Controle) e plataforma/detalhes
            Label tipo = new Label(item.getResumoTipo());
            tipo.getStyleClass().add("texto-mutado");
            tipo.setWrapText(true);
            tipo.setMaxWidth(260);
            itens.add(new VBox(2, nome, tipo), 0, linha);
            itens.add(celulaGrade(String.valueOf(item.getQuantidade())), 1, linha);
            itens.add(celulaGrade(FORMATO_PRECO.format(item.getPrecoUnitario())), 2, linha);
            itens.add(celulaGrade(FORMATO_PRECO.format(item.getSubtotal())), 3, linha);
            linha++;
        }

        VBox totais = new VBox(6,
                linhaValor("Subtotal", FORMATO_PRECO.format(compra.getSubtotal()), false),
                linhaValor(compra.getFormaPagamento().getPercentualDesconto() == 0
                                ? "Desconto"
                                : "Desconto " + compra.getFormaPagamento().getRotulo()
                                + " (" + compra.getFormaPagamento().getPercentualDesconto() + "%)",
                        compra.getDesconto() == 0 ? "—" : "- " + FORMATO_PRECO.format(compra.getDesconto()), false),
                linhaValor("TOTAL PAGO", FORMATO_PRECO.format(compra.getTotal()), true));
        totais.getStyleClass().add("nota-linha-total");

        Label qtdTotal = new Label(compra.getQuantidadeTotal()
                + (compra.getQuantidadeTotal() == 1 ? " unidade" : " unidades") + " no total");
        qtdTotal.getStyleClass().add("texto-mutado");

        Button btnFechar = new Button("Fechar");
        btnFechar.getStyleClass().addAll("button", "botao-primario");
        btnFechar.setOnAction(e -> janela.close());
        HBox rodape = new HBox(btnFechar);
        rodape.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(14, titulo, cabecalho, itens, qtdTotal, totais, rodape);
        layout.getStyleClass().add("nota-compra");
        layout.setPadding(new Insets(24));
        layout.setMinWidth(480);

        Scene cena = new Scene(layout);
        URL css = getClass().getResource("/com/senai/javafx/styleMenu.css");
        if (css != null) {
            cena.getStylesheets().add(css.toExternalForm());
        }
        janela.setScene(cena);
        janela.showAndWait();
    }

    private HBox linhaTexto(String rotulo, String valor) {
        Label lblRotulo = new Label(rotulo);
        lblRotulo.getStyleClass().add("texto-mutado");
        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add("subtitulo");
        return new HBox(4, lblRotulo, lblValor);
    }

    private Label celulaGrade(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("subtitulo");
        return label;
    }

    private Label cabecalhoGrade(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("label-campo");
        return label;
    }

    private HBox linhaValor(String rotulo, String valor, boolean destaque) {
        Label lblRotulo = new Label(rotulo);
        lblRotulo.getStyleClass().add(destaque ? "subtitulo" : "texto-mutado");
        Label lblValor = new Label(valor);
        lblValor.getStyleClass().add(destaque ? "label-total" : "subtitulo");
        Region espaco = new Region();
        HBox.setHgrow(espaco, Priority.ALWAYS);
        HBox caixa = new HBox(lblRotulo, espaco, lblValor);
        caixa.setAlignment(Pos.CENTER_LEFT);
        return caixa;
    }
}

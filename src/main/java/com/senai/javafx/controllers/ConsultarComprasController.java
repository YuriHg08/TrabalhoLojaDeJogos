package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Compra;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.RegistroCompras;
import com.senai.javafx.entidades.Sessao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// Histórico de compras: o administrador vê todas as compras que já permitiu;
// o cliente vê somente as da própria conta
public class ConsultarComprasController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private TableView<Compra> tabelaCompras;
    @FXML
    private TableColumn<Compra, String> colNota, colData, colCliente, colItens,
            colQuantidade, colPagamento, colStatus, colTotal;
    @FXML
    private TableColumn<Compra, Void> colNotaDetalhe;
    @FXML
    private Label lblResumo;

    @FXML
    private void initialize() {
        colNota.setCellValueFactory(c -> new SimpleStringProperty(numeroNota(c.getValue())));
        colData.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDataExibicao().format(FORMATO_DATA)));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeCliente()));
        colItens.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResumoItens()));
        colQuantidade.setCellValueFactory(c ->
                new SimpleStringProperty(String.valueOf(c.getValue().getQuantidadeTotal())));
        colPagamento.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFormaPagamento().getRotulo()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRotuloStatus()));
        colStatus.setCellFactory(coluna -> new TableCell<Compra, String>() {
            @Override
            protected void updateItem(String texto, boolean vazio) {
                super.updateItem(texto, vazio);
                getStyleClass().removeAll("status-aprovada", "status-pendente", "status-recusada");
                Compra compra = vazio || getTableRow() == null ? null : getTableRow().getItem();
                if (compra == null) {
                    setText(null);
                    return;
                }
                setText(texto);
                getStyleClass().add(classeStatus(compra.getStatus()));
            }
        });
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

        boolean admin = Sessao.isAdmin();

        // Cliente: só as compras da conta logada (pelo id, não pelo nome)
        // (o cliente também vê as em andamento e as recusadas; o admin só as aprovadas)
        List<Compra> compras = admin
                ? RegistroCompras.listarHistorico()
                : RegistroCompras.notasDoCliente(Sessao.getIdUsuarioLogado());
        tabelaCompras.setItems(FXCollections.observableArrayList(compras));

        // O cliente só vê as próprias compras, então a coluna "Cliente" não faz sentido para ele
        colCliente.setVisible(admin);
        // Para o admin só existem compras aprovadas, então o status só interessa ao cliente
        colStatus.setVisible(!admin);

        // Só conta o que foi aprovado: compra recusada ou em andamento não foi cobrada ainda
        double total = compras.stream().filter(c -> c.getStatus() == Compra.Status.APROVADA)
                .mapToDouble(Compra::getTotal).sum();
        long emAndamento = compras.stream().filter(c -> c.getStatus() == Compra.Status.PENDENTE).count();
        lblResumo.setText(compras.size() + (compras.size() == 1 ? " compra" : " compras")
                + (admin ? "  •  Total vendido: " : "  •  Total gasto: ") + FORMATO_PRECO.format(total)
                + (!admin && emAndamento > 0 ? "  •  " + emAndamento + " em andamento" : ""));
    }

    // Botão "Voltar" da versão do cliente
    @FXML
    private void voltarLoja() {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store");
    }

    private static String classeStatus(Compra.Status status) {
        return switch (status) {
            case PENDENTE -> "status-pendente";
            case APROVADA -> "status-aprovada";
            case RECUSADA -> "status-recusada";
        };
    }

    // Aviso colorido no topo da nota explicando o que o status significa para o cliente
    private Label avisoStatus(Compra compra) {
        String texto = switch (compra.getStatus()) {
            case APROVADA -> "✔ Compra aprovada";
            case PENDENTE -> "⏳ Em andamento: pagamento registrado, aguardando a loja aprovar a compra.";
            case RECUSADA -> "✖ Compra recusada: o valor não foi cobrado e os itens voltaram para o estoque.";
        };
        Label aviso = new Label(texto);
        aviso.setWrapText(true);
        aviso.setMaxWidth(440);
        aviso.getStyleClass().addAll("nota-status", classeStatus(compra.getStatus()));
        return aviso;
    }

    private static String numeroNota(Compra compra) {
        return String.format("%04d", compra.getNumero());
    }

    // Janela com a nota completa: cliente, data, pagamento, item por item e totais
    private void abrirNota(Compra compra) {
        NotaCompraController.abrir(compra, tabelaCompras.getScene().getWindow());
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

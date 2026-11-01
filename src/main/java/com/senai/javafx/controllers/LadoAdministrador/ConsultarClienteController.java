package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.Compra;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.RegistroCompras;
import com.senai.javafx.entidades.Usuario;
import com.senai.javafx.entidades.UsuarioCadastro;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConsultarClienteController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private TableView<Usuario> tabela;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, Void> colAcao;
    @FXML
    private Label lblTotal;

    // Compras pagas pelos clientes, aguardando o admin permitir
    @FXML
    private TableView<Compra> tabelaPendentes;
    @FXML
    private TableColumn<Compra, String> colCompraNumero, colCompraCliente, colCompraCpf,
            colCompraItens, colCompraPagamento, colCompraTotal;
    @FXML
    private TableColumn<Compra, Void> colCompraData, colCompraAcoes;
    @FXML
    private Label lblPendentes;

    // Data escolhida para cada compra pendente (começa em hoje)
    private final Map<Compra, LocalDate> datasEscolhidas = new HashMap<>();

    // Controller da barra lateral incluída (fx:id="sideBar" no FXML) para atualizar o contador
    @FXML
    private MenuController sideBarController;

    @FXML
    private void initialize() {
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));

        tabela.setPlaceholder(textoVazio("Nenhum cliente cadastrado ainda."));
        tabela.setItems(FXCollections.observableArrayList(UsuarioCadastro.listar()));
        configurarColunaExpulsar();
        atualizarTotal();

        configurarTabelaPendentes();
        atualizarPendentes();
    }

    private void atualizarTotal() {
        int total = tabela.getItems().size();
        lblTotal.setText("Total: " + total + (total == 1 ? " cliente" : " clientes"));
    }

    // Botão "Expulsar" em cada linha da tabela de clientes
    private void configurarColunaExpulsar() {
        colAcao.setCellFactory(coluna -> new TableCell<Usuario, Void>() {
            private final Button btnExpulsar = new Button("Expulsar");

            {
                btnExpulsar.getStyleClass().addAll("button", "botao-perigo", "botao-tabela");
                btnExpulsar.setOnAction(e -> {
                    Usuario cliente = getTableRow().getItem();
                    if (cliente != null) {
                        expulsar(cliente);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                super.updateItem(item, vazio);
                setAlignment(Pos.CENTER);
                setGraphic(vazio || getTableRow() == null || getTableRow().getItem() == null ? null : btnExpulsar);
            }
        });
    }

    // Pede confirmação, remove a conta e cancela as compras que o cliente ainda tinha em análise
    private void expulsar(Usuario cliente) {
        int pendentes = RegistroCompras.pendentesDoCliente(cliente.getNome()).size();

        String mensagem = "Expulsar o cliente \"" + cliente.getNome() + "\"? A conta será removida e ele não poderá mais entrar.";
        if (pendentes > 0) {
            mensagem += "\n\nEle tem " + pendentes + (pendentes == 1 ? " compra" : " compras")
                    + " aguardando aprovação, que será recusada (os jogos voltam para o estoque).";
        }
        if (!confirmar("Expulsar cliente", mensagem)) {
            return;
        }

        UsuarioCadastro.remover(cliente.getNome());
        RegistroCompras.cancelarPendentesDoCliente(cliente.getNome());

        tabela.getItems().remove(cliente);
        atualizarTotal();
        atualizarPendentes();

        GerenciamentoJanela.exibirAlerta("Cliente expulso",
                "\"" + cliente.getNome() + "\" foi removido da loja.",
                GerenciamentoJanela.TipoAlerta.SUCESSO);
    }

    // Caixa de confirmação Sim/Não no tema do app (usada por "Recusar" e "Expulsar")
    private boolean confirmar(String titulo, String mensagem) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, mensagem, ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle(titulo);
        confirmacao.setHeaderText(null);

        var css = getClass().getResource("/com/senai/javafx/styleMenu.css");
        if (css != null) {
            confirmacao.getDialogPane().getStylesheets().add(css.toExternalForm());
        }
        return confirmacao.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }

    private Label textoVazio(String texto) {
        Label label = new Label(texto);
        label.getStyleClass().add("texto-mutado");
        return label;
    }

    private void configurarTabelaPendentes() {
        colCompraNumero.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%04d", c.getValue().getNumero())));
        colCompraCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomeCliente()));
        colCompraCpf.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCpf()));
        colCompraItens.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getResumoItens()));
        colCompraPagamento.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getFormaPagamento().getRotulo()));
        colCompraTotal.setCellValueFactory(c ->
                new SimpleStringProperty(FORMATO_PRECO.format(c.getValue().getTotal())));

        colCompraData.setCellFactory(coluna -> new TableCell<Compra, Void>() {
            private final DatePicker seletor = new DatePicker();

            {
                seletor.setEditable(false);
                seletor.setMaxWidth(Double.MAX_VALUE);
                seletor.valueProperty().addListener((obs, antiga, nova) -> {
                    Compra compra = getTableRow() == null ? null : getTableRow().getItem();
                    if (compra != null && nova != null) {
                        datasEscolhidas.put(compra, nova);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                super.updateItem(item, vazio);
                Compra compra = getTableRow() == null ? null : getTableRow().getItem();
                if (vazio || compra == null) {
                    setGraphic(null);
                    return;
                }
                seletor.setValue(datasEscolhidas.computeIfAbsent(compra, c -> LocalDate.now()));
                setGraphic(seletor);
            }
        });

        colCompraAcoes.setCellFactory(coluna -> new TableCell<Compra, Void>() {
            private final Button btnAprovar = new Button("Permitir");
            private final Button btnRecusar = new Button("Recusar");
            private final HBox caixa = new HBox(8, btnAprovar, btnRecusar);

            {
                btnAprovar.getStyleClass().addAll("button", "botao-primario", "botao-tabela");
                btnRecusar.getStyleClass().addAll("button", "botao-secundario", "botao-tabela");
                caixa.setAlignment(Pos.CENTER);

                btnAprovar.setOnAction(e -> {
                    Compra compra = getTableRow().getItem();
                    if (compra != null) {
                        aprovar(compra);
                    }
                });
                btnRecusar.setOnAction(e -> {
                    Compra compra = getTableRow().getItem();
                    if (compra != null) {
                        recusar(compra);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                super.updateItem(item, vazio);
                setGraphic(vazio || getTableRow() == null || getTableRow().getItem() == null ? null : caixa);
            }
        });

        tabelaPendentes.setPlaceholder(textoVazio("Nenhuma compra aguardando aprovação."));
    }

    private void atualizarPendentes() {
        tabelaPendentes.setItems(FXCollections.observableArrayList(RegistroCompras.listarPendentes()));

        int pendentes = tabelaPendentes.getItems().size();
        lblPendentes.setText(pendentes == 0
                ? "Nenhuma compra aguardando aprovação"
                : pendentes + (pendentes == 1 ? " compra aguardando aprovação" : " compras aguardando aprovação"));

        if (sideBarController != null) {
            sideBarController.atualizarContadorPendentes();
        }
    }

    private void aprovar(Compra compra) {
        LocalDate data = datasEscolhidas.getOrDefault(compra, LocalDate.now());
        RegistroCompras.aprovar(compra, data);
        datasEscolhidas.remove(compra);
        atualizarPendentes();

        GerenciamentoJanela.exibirAlerta("Compra permitida",
                "A compra nº " + String.format("%04d", compra.getNumero())
                        + " foi permitida e já está no histórico de compras.",
                GerenciamentoJanela.TipoAlerta.SUCESSO);
    }

    private void recusar(Compra compra) {
        if (confirmar("Recusar compra",
                "Recusar a compra nº " + String.format("%04d", compra.getNumero())
                        + " de " + compra.getNomeCliente() + "? Os jogos voltam para o estoque.")) {
            RegistroCompras.recusar(compra);
            datasEscolhidas.remove(compra);
            atualizarPendentes();
        }
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();
    }
}

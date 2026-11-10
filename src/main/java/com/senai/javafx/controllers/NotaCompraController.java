package com.senai.javafx.controllers;

import com.senai.javafx.entidades.Compra;
import com.senai.javafx.entidades.ItemCompra;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NotaCompraController {

    private static final NumberFormat FORMATO_PRECO = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private VBox rootLayout;
    @FXML private Label lblTitulo;
    @FXML private Label lblAvisoStatus;
    @FXML private Label lblCliente;
    @FXML private Label lblCpf;
    @FXML private Label lblRotuloData;
    @FXML private Label lblData;
    @FXML private Label lblSituacao;
    @FXML private Label lblFormaPagamento;
    @FXML private GridPane gridItens;
    @FXML private Label lblQtdTotal;
    @FXML private Label lblSubtotal;
    @FXML private HBox boxDesconto;
    @FXML private Label lblRotuloDesconto;
    @FXML private Label lblValorDesconto;
    @FXML private Label lblRotuloTotal;
    @FXML private Label lblValorTotal;

    private Stage stage;

    public static void abrir(Compra compra, Window dono) {
        try {
            FXMLLoader loader = new FXMLLoader(NotaCompraController.class.getResource("/com/senai/javafx/notaCompra.fxml"));
            Parent root = loader.load();

            NotaCompraController controller = loader.getController();

            Stage janela = new Stage();
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.initOwner(dono);
            janela.initStyle(StageStyle.UNDECORATED);
            janela.setResizable(false);

            // Permite arrastar a janela livremente
            final double[] arrasto = new double[2];
            root.setOnMousePressed(e -> {
                arrasto[0] = janela.getX() - e.getScreenX();
                arrasto[1] = janela.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                janela.setX(e.getScreenX() + arrasto[0]);
                janela.setY(e.getScreenY() + arrasto[1]);
            });

            Scene cena = new Scene(root);
            var css = NotaCompraController.class.getResource("/com/senai/javafx/styleMenu.css");
            if (css != null) {
                cena.getStylesheets().add(css.toExternalForm());
            }

            janela.setScene(cena);
            controller.configurar(compra, janela);
            janela.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configurar(Compra compra, Stage stage) {
        this.stage = stage;

        String numero = String.format("%06d", compra.getIdCliente());
        lblTitulo.setText("Nota nº " + numero);

        // Aviso de Status
        if (compra.getStatus() == Compra.Status.RECUSADA) {
            lblAvisoStatus.setText("Compra recusada");
            lblAvisoStatus.setStyle("-fx-text-fill: #e53e3e;");
        } else if (compra.getStatus() == Compra.Status.PENDENTE) {
            lblAvisoStatus.setText("Aguardando confirmação de pagamento");
            lblAvisoStatus.setStyle("-fx-text-fill: #ecc94b;");
        } else {
            lblAvisoStatus.setVisible(false);
            lblAvisoStatus.setManaged(false);
        }

        // Cabeçalho da Compra
        lblCliente.setText(compra.getNomeCliente());
        lblCpf.setText(compra.getCpf());
        lblRotuloData.setText(compra.getStatus() == Compra.Status.APROVADA ? "Data da compra: " : "Data do pedido: ");
        lblData.setText(compra.getDataExibicao().format(FORMATO_DATA));
        lblSituacao.setText(compra.getRotuloStatus());
        lblFormaPagamento.setText(compra.getFormaPagamento().getRotulo());

        // Preenchimento dos itens
        int linha = 1;
        for (ItemCompra item : compra.getItens()) {
            Label nome = new Label(item.getNome());
            nome.getStyleClass().add("subtitulo");
            nome.setWrapText(true);
            nome.setMaxWidth(220);

            Label tipo = new Label(item.getResumoTipo());
            tipo.getStyleClass().add("texto-ajuda");
            tipo.setWrapText(true);
            tipo.setMaxWidth(220);

            VBox boxItem = new VBox(2, nome, tipo);
            Label lblQtd = new Label(String.valueOf(item.getQuantidade()));
            lblQtd.getStyleClass().add("texto-ajuda");


            Label lblUnitario = new Label(FORMATO_PRECO.format(item.getPrecoUnitario()));
            lblUnitario.getStyleClass().add("texto-ajuda");

            Label lblSub = new Label(FORMATO_PRECO.format(item.getSubtotal()));
            lblSub.getStyleClass().add("texto-ajuda");

            gridItens.add(boxItem, 0, linha);
            gridItens.add(lblQtd, 1, linha);
            gridItens.add(lblUnitario, 2, linha);
            gridItens.add(lblSub, 3, linha);
            linha++;
        }

        // Totais e Desconto

        lblQtdTotal.setText(compra.getQuantidadeTotal() + (compra.getQuantidadeTotal() == 1 ? " unidade" : " unidades") + " no total");
        lblSubtotal.setText(FORMATO_PRECO.format(compra.getSubtotal()));

        if (compra.getFormaPagamento().getPercentualDesconto() > 0) {
            lblRotuloDesconto.setText("Desconto - " + compra.getFormaPagamento().getRotulo() + " (" + compra.getFormaPagamento().getPercentualDesconto() + "%)");
            lblValorDesconto.setText("- " + FORMATO_PRECO.format(compra.getDesconto()));
        } else {
            lblRotuloDesconto.setText("Desconto");
            lblValorDesconto.setText("—");
        }

        lblRotuloTotal.setText(switch (compra.getStatus()) {
            case APROVADA -> "Total pago";
            case PENDENTE -> "Total a pagar (em análise)";
            case RECUSADA -> "Total (não cobrado)";
        });
        lblValorTotal.setText(FORMATO_PRECO.format(compra.getTotal()));
    }

    @FXML
    private void fechar() {
        if (stage != null) {
            stage.close();
        }
    }
}
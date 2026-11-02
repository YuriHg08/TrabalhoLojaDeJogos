package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Carrinho;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

// Barra fixa no rodapé das lojas (jogos e acessórios) e da tela de detalhes: só aparece com itens no carrinho
// e mostra os produtos, a quantidade e o total, com o botão "Ir para o carrinho".
public class BarraCarrinho extends HBox {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private final Label lblTitulo = new Label();
    private final Label lblResumo = new Label();
    private final Label lblTotal = new Label();

    // Referência única para poder tirar o ouvinte do Carrinho quando a tela sai da cena
    private final Runnable aoMudarCarrinho = this::atualizar;

    public BarraCarrinho() {
        getStyleClass().add("barra-carrinho");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(20);

        lblTitulo.getStyleClass().add("barra-carrinho-titulo");
        lblResumo.getStyleClass().add("barra-carrinho-resumo");
        lblResumo.setMaxWidth(Double.MAX_VALUE); // texto longo termina em "..."

        VBox textos = new VBox(2, lblTitulo, lblResumo);
        textos.setMinWidth(0);
        HBox.setHgrow(textos, Priority.ALWAYS);

        Label rotuloTotal = new Label("TOTAL A PAGAR");
        rotuloTotal.getStyleClass().add("barra-carrinho-titulo");
        lblTotal.getStyleClass().add("label-total");
        VBox total = new VBox(2, rotuloTotal, lblTotal);
        total.setAlignment(Pos.CENTER_RIGHT);

        Button btnIr = new Button("Ir para o carrinho  →");
        btnIr.getStyleClass().addAll("button", "botao-primario");
        btnIr.setOnAction(e -> abrirCarrinho());

        getChildren().addAll(textos, total, btnIr);

        // Só escuta o carrinho enquanto está na tela; ao voltar, já se atualiza
        sceneProperty().addListener((obs, antiga, nova) -> {
            if (nova == null) {
                Carrinho.removeOuvinte(aoMudarCarrinho);
            } else {
                Carrinho.addOuvinte(aoMudarCarrinho);
                atualizar();
            }
        });

        atualizar();
    }

    private void atualizar() {
        boolean vazio = Carrinho.isVazio();
        setVisible(!vazio);
        setManaged(!vazio);
        if (vazio) {
            return;
        }

        int produtos = Carrinho.getQuantidadeItens();
        int unidades = Carrinho.getQuantidadeUnidades();
        lblTitulo.setText("SEU CARRINHO  •  " + produtos + (produtos == 1 ? " item" : " itens")
                + "  •  " + unidades + (unidades == 1 ? " unidade" : " unidades"));
        lblResumo.setText(Carrinho.getResumoItens());
        lblTotal.setText(FORMATO_PRECO.format(Carrinho.getSubtotal()));
    }

    private void abrirCarrinho() {
        Scene cena = getScene();
        if (cena == null) {
            return;
        }
        // Guarda a loja para o "Voltar à loja" do carrinho (se já veio dos detalhes, ela já está guardada)
        if (!GerenciamentoJanela.temTelaGuardada()) {
            GerenciamentoJanela.guardarTela(cena.getRoot());
        }
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/carrinho.fxml", "Nexus Store - Carrinho");
    }
}

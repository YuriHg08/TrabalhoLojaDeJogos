package com.senai.javafx.controllers.LadoCliente;

import com.senai.javafx.entidades.Carrinho;
import com.senai.javafx.entidades.CatalogoProdutos;
import com.senai.javafx.entidades.Compra;
import com.senai.javafx.entidades.Cpf;
import com.senai.javafx.entidades.FormaPagamento;
import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.RegistroCompras;
import com.senai.javafx.entidades.Sessao;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

import java.text.NumberFormat;
import java.util.Locale;

public class PagamentoController {

    private static final NumberFormat FORMATO_PRECO =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @FXML
    private ToggleGroup grupoPagamento;

    @FXML
    private ToggleButton tglPix, tglCartao, tglDinheiro;

    @FXML
    private VBox boxPix, boxCartao;

    @FXML
    private TextField txtCpf, txtNomeCartao;

    @FXML
    private PasswordField txtSenhaCartao;

    @FXML
    private Label lblItens, lblSubtotal, lblRotuloDesconto, lblDesconto, lblTotal;

    @FXML
    private Button btnConfirmar;

    @FXML
    public void initialize() {
        configurarOpcao(tglPix, FormaPagamento.PIX);
        configurarOpcao(tglCartao, FormaPagamento.CARTAO);
        configurarOpcao(tglDinheiro, FormaPagamento.DINHEIRO);

        // Clicar na opção já marcada não pode deixar o grupo sem nenhuma escolha
        grupoPagamento.selectedToggleProperty().addListener((obs, antigo, novo) -> {
            if (novo == null && antigo != null) {
                antigo.setSelected(true);
                return;
            }
            aoEscolherForma();
        });

        // CPF: só números, formatado como 000.000.000-00 enquanto digita
        txtCpf.setTextFormatter(new TextFormatter<String>(mudanca -> {
            if (!mudanca.isContentChange()) {
                return mudanca;
            }
            String digitos = Cpf.somenteDigitos(mudanca.getControlNewText());
            if (digitos.length() > 11) {
                return null;
            }
            String formatado = Cpf.formatar(digitos);
            mudanca.setRange(0, mudanca.getControlText().length());
            mudanca.setText(formatado);
            mudanca.selectRange(formatado.length(), formatado.length());
            return mudanca;
        }));

        // Nome do titular: só letras, espaço, apóstrofo e hífen
        txtNomeCartao.setTextFormatter(new TextFormatter<String>(mudanca ->
                mudanca.getControlNewText().matches("[\\p{L} '\\-]{0,60}") ? mudanca : null));

        // Senha do cartão: só números, no máximo 6
        txtSenhaCartao.setTextFormatter(new TextFormatter<String>(mudanca ->
                mudanca.getControlNewText().matches("\\d{0,6}") ? mudanca : null));

        lblItens.setText(Carrinho.getResumoItens());
        atualizarResumo(null);

        // Sem itens não há o que pagar: volta para a loja
        if (Carrinho.isVazio()) {
            Platform.runLater(this::voltarParaLoja);
        }
    }

    private void configurarOpcao(ToggleButton botao, FormaPagamento forma) {
        botao.setUserData(forma);
        botao.setText(forma.getRotulo() + "\n" + forma.getDescricaoDesconto());
    }

    private FormaPagamento formaSelecionada() {
        Toggle escolhido = grupoPagamento.getSelectedToggle();
        return escolhido == null ? null : (FormaPagamento) escolhido.getUserData();
    }

    private void aoEscolherForma() {
        FormaPagamento forma = formaSelecionada();

        // Pix pede CPF; cartão pede nome e senha; dinheiro não pede nada
        mostrar(boxPix, forma == FormaPagamento.PIX);
        mostrar(boxCartao, forma == FormaPagamento.CARTAO);
        btnConfirmar.setDisable(forma == null);

        atualizarResumo(forma);

        if (forma == FormaPagamento.PIX) {
            Platform.runLater(txtCpf::requestFocus);
        } else if (forma == FormaPagamento.CARTAO) {
            Platform.runLater(txtNomeCartao::requestFocus);
        }
    }

    private void mostrar(VBox caixa, boolean visivel) {
        caixa.setVisible(visivel);
        caixa.setManaged(visivel);
    }

    private void atualizarResumo(FormaPagamento forma) {
        double subtotal = Carrinho.getSubtotal();
        lblSubtotal.setText(FORMATO_PRECO.format(subtotal));

        if (forma == null) {
            lblRotuloDesconto.setText("Desconto");
            lblDesconto.setText("Escolha a forma de pagamento");
            lblTotal.setText(FORMATO_PRECO.format(subtotal));
            return;
        }

        if (forma.getPercentualDesconto() == 0) {
            lblRotuloDesconto.setText("Desconto");
            lblDesconto.setText("Sem desconto");
        } else {
            lblRotuloDesconto.setText("Desconto " + forma.getRotulo() + " (" + forma.getPercentualDesconto() + "%)");
            lblDesconto.setText("- " + FORMATO_PRECO.format(forma.calcularDesconto(subtotal)));
        }
        lblTotal.setText(FORMATO_PRECO.format(forma.calcularTotal(subtotal)));
    }

    @FXML
    private void confirmarPagamento(ActionEvent event) {
        FormaPagamento forma = formaSelecionada();
        if (forma == null) {
            GerenciamentoJanela.exibirAlerta("Atenção", "Escolha a forma de pagamento.",
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            return;
        }
        if (!dadosValidos(forma)) {
            return;
        }

        try {
            Compra compra = RegistroCompras.finalizarPagamento(
                    Sessao.getUsuarioLogado(), forma, txtCpf.getText(),
                    txtNomeCartao.getText(), txtSenhaCartao.getText(),
                    CatalogoProdutos::existe);

            GerenciamentoJanela.exibirAlerta("Pagamento realizado",
                    "Obrigado! Sua compra nº " + compra.getNumero() + " ("
                            + FORMATO_PRECO.format(compra.getTotal()) + " em " + forma.getRotulo()
                            + ") foi enviada para a aprovação do administrador.",
                    GerenciamentoJanela.TipoAlerta.SUCESSO);
            voltarParaLoja();

        } catch (IllegalStateException e) {
            // Normalmente estoque que acabou enquanto o cliente pagava: nada foi cobrado.
            // Ajusta o carrinho e leva o cliente de volta para conferir.
            Carrinho.ajustarAoEstoque(CatalogoProdutos::existe);
            GerenciamentoJanela.exibirAlerta("Não foi possível concluir", e.getMessage(),
                    GerenciamentoJanela.TipoAlerta.ALERTA);
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/carrinho.fxml", "Nexus Store - Carrinho");
        }
    }

    // Confere só os dados que a forma escolhida pede; mostra o aviso e foca o campo com problema
    private boolean dadosValidos(FormaPagamento forma) {
        switch (forma) {
            case PIX -> {
                if (!Cpf.valido(txtCpf.getText())) {
                    avisar("Informe um CPF válido para usar como chave Pix.", txtCpf);
                    return false;
                }
            }
            case CARTAO -> {
                if (!RegistroCompras.nomeCompletoValido(txtNomeCartao.getText())) {
                    avisar("Informe o nome completo do titular do cartão.", txtNomeCartao);
                    return false;
                }
                if (!txtSenhaCartao.getText().matches("\\d{4,6}")) {
                    avisar("A senha do cartão deve ter de 4 a 6 números.", txtSenhaCartao);
                    return false;
                }
            }
            case DINHEIRO -> { } // não pede nada
        }
        return true;
    }

    private void avisar(String mensagem, TextField campo) {
        GerenciamentoJanela.exibirAlerta("Atenção", mensagem, GerenciamentoJanela.TipoAlerta.ALERTA);
        campo.requestFocus();
    }

    @FXML
    private void voltarCarrinho(ActionEvent event) {
        GerenciamentoJanela.abrirJanela("/com/senai/javafx/carrinho.fxml", "Nexus Store - Carrinho");
    }

    private void voltarParaLoja() {
        if (!GerenciamentoJanela.restaurarTelaGuardada("Nexus Store - Página inicial")) {
            GerenciamentoJanela.abrirJanela("/com/senai/javafx/telaInicial.fxml", "Nexus Store - Página inicial");
        }
    }
}

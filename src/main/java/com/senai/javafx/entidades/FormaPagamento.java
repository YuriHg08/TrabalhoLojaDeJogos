package com.senai.javafx.entidades;

// Formas de pagamento da loja e o desconto de cada uma
public enum FormaPagamento {
    PIX("Pix", 5),
    CARTAO("Cartão", 0),
    DINHEIRO("Dinheiro", 10);

    private final String rotulo;
    private final int percentualDesconto;

    FormaPagamento(String rotulo, int percentualDesconto) {
        this.rotulo = rotulo;
        this.percentualDesconto = percentualDesconto;
    }

    public String getRotulo() {
        return rotulo;
    }

    public int getPercentualDesconto() {
        return percentualDesconto;
    }

    public String getDescricaoDesconto() {
        return percentualDesconto == 0 ? "Sem desconto" : percentualDesconto + "% de desconto";
    }

    // Valor (em reais, 2 casas) que sai do subtotal
    public double calcularDesconto(double subtotal) {
        return arredondar(subtotal * percentualDesconto / 100.0);
    }

    public double calcularTotal(double subtotal) {
        return arredondar(subtotal - calcularDesconto(subtotal));
    }

    public static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return rotulo;
    }
}

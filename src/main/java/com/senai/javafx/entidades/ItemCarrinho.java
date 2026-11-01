package com.senai.javafx.entidades;

public class ItemCarrinho {
    private final Jogo jogo;
    private int quantidade;

    public ItemCarrinho(Jogo jogo, int quantidade) {
        this.jogo = jogo;
        this.quantidade = quantidade;
    }

    public Jogo getJogo() { return jogo; }

    public int getQuantidade() { return quantidade; }

    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() {
        return FormaPagamento.arredondar(jogo.getPrecoJogo() * quantidade);
    }
}

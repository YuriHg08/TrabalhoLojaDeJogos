package com.senai.javafx.entidades;

// Foto de um item no momento da compra (o preço não muda se o admin editar o jogo depois)
public class ItemCompra {
    private final Jogo jogo; // usado só para devolver ao estoque se a compra for recusada
    private final String nomeJogo;
    private final double precoUnitario;
    private final int quantidade;

    public ItemCompra(Jogo jogo, int quantidade) {
        this.jogo = jogo;
        this.nomeJogo = jogo.getNomeJogo();
        this.precoUnitario = jogo.getPrecoJogo();
        this.quantidade = quantidade;
    }

    public Jogo getJogo() { return jogo; }
    public String getNomeJogo() { return nomeJogo; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidade() { return quantidade; }

    public double getSubtotal() {
        return FormaPagamento.arredondar(precoUnitario * quantidade);
    }
}

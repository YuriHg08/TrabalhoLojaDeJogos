package com.senai.javafx.entidades;

// Foto de um item no momento da compra (o preço não muda se o admin editar o produto depois)
public class ItemCompra {
    private final Produto produto; // usado só para devolver ao estoque se a compra for recusada
    private final String nome;
    private final String tipo;      // "Jogo", "Console", "PC Gamer" ou "Controle" (sai na nota)
    private final String descricao; // plataforma / detalhes (sai na nota)
    private final double precoUnitario;
    private final int quantidade;

    public ItemCompra(Produto produto, int quantidade) {
        this.produto = produto;
        this.nome = produto.getNome();
        this.tipo = produto.getTipo();
        this.descricao = produto.getDescricao();
        this.precoUnitario = produto.getPreco();
        this.quantidade = quantidade;
    }

    public Produto getProduto() { return produto; }
    public String getNome() { return nome; }
    public String getTipo() { return tipo; }
    public String getDescricao() { return descricao; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidade() { return quantidade; }

    // Ex.: "Console • Playstation • 1 TB" (segunda linha do item na nota)
    public String getResumoTipo() {
        return descricao == null || descricao.isBlank() ? tipo : tipo + " • " + descricao;
    }

    public double getSubtotal() {
        return FormaPagamento.arredondar(precoUnitario * quantidade);
    }
}

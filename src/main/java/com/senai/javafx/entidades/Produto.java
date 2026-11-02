package com.senai.javafx.entidades;

// Qualquer coisa que a loja vende e que pode ir para o carrinho e para a nota:
// jogos (Jogo) e consoles, PCs e controles (Acessorio).
public interface Produto {

    String getNome();

    double getPreco();

    int getEstoque();

    void setEstoque(int estoque);

    String getCaminhoImagem();

    // "Jogo", "Console", "PC Gamer" ou "Controle" (aparece na nota)
    String getTipo();

    // Linha de apoio embaixo do nome, ex.: "PC, Playstation" ou "Playstation • 1 TB"
    String getDescricao();

    // Selo da tela de detalhes, ex.: "AÇÃO | PLAYSTATION"
    String getEtiqueta();
}

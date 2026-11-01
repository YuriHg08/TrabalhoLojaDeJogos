package com.senai.javafx.entidades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Jogo {
    private final List<String> consoles = new ArrayList<>();
    private final List<String> categorias = new ArrayList<>();
    private String nomeJogo;
    private double precoJogo;
    private String caminhoImagem; // Atributo para armazenar o caminho/URL da imagem
    private int estoqueJogo;

    // Construtor completo: o jogo pode ter várias categorias e vários consoles
    public Jogo(String nomeJogo, Collection<String> categorias, Collection<String> consoles,
                double precoJogo, String caminhoImagem, int estoqueJogo) {
        this.nomeJogo = nomeJogo;
        setCategorias(categorias);
        setConsoles(consoles);
        this.precoJogo = precoJogo;
        this.caminhoImagem = caminhoImagem;
        this.estoqueJogo = estoqueJogo;
    }

    // Atalho para jogo com uma única categoria e um único console
    public Jogo(String nomeJogo, String categoriaJogo, String consoleJogo, double precoJogo, String caminhoImagem, int estoqueJogo) {
        this(nomeJogo,
                categoriaJogo == null ? List.<String>of() : List.of(categoriaJogo),
                consoleJogo == null ? List.<String>of() : List.of(consoleJogo),
                precoJogo, caminhoImagem, estoqueJogo);
    }

    // --- GETTERS E SETTERS ---

    public String getCaminhoImagem() {
        return caminhoImagem;
    }

    public void setCaminhoImagem(String caminhoImagem) {
        this.caminhoImagem = caminhoImagem;
    }

    public String getNomeJogo() {
        return nomeJogo;
    }

    public void setNomeJogo(String nomeJogo) {
        this.nomeJogo = nomeJogo;
    }

    public double getPrecoJogo() {
        return precoJogo;
    }

    public void setPrecoJogo(double precoJogo) {
        this.precoJogo = precoJogo;
    }

    public int getEstoqueJogo() { return estoqueJogo; }

    public void setEstoqueJogo(int estoqueJogo) { this.estoqueJogo = estoqueJogo; }

    // --- CATEGORIAS ---

    public List<String> getCategorias() { return List.copyOf(categorias); }

    public void setCategorias(Collection<String> novas) {
        categorias.clear();
        if (novas != null) {
            categorias.addAll(novas);
        }
    }

    // Texto para exibir na tela, ex.: "Ação, RPG" (null se não houver nenhuma)
    public String getCategoriaJogo() {
        return categorias.isEmpty() ? null : String.join(", ", categorias);
    }

    public boolean temCategoria(String categoria) {
        return categorias.stream().anyMatch(c -> c.equalsIgnoreCase(categoria));
    }

    // --- CONSOLES / PLATAFORMAS ---

    public List<String> getConsoles() { return List.copyOf(consoles); }

    public void setConsoles(Collection<String> novos) {
        consoles.clear();
        if (novos != null) {
            consoles.addAll(novos);
        }
    }

    // Texto para exibir na tela, ex.: "PC, Xbox" (null se não houver nenhum)
    public String getConsoleJogo() {
        return consoles.isEmpty() ? null : String.join(", ", consoles);
    }

    public boolean temConsole(String console) {
        return consoles.stream().anyMatch(c -> c.equalsIgnoreCase(console));
    }
}
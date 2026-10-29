    package com.senai.javafx.entidades;

    public class Jogo {

        private String nomeJogo;
        private String categoriaJogo;
        private double precoJogo;
        private String caminhoImagem; // Atributo para armazenar o caminho/URL da imagem

        // Construtor completo
        public Jogo(String nomeJogo, String categoriaJogo, double precoJogo, String caminhoImagem) {
            this.nomeJogo = nomeJogo;
            this.categoriaJogo = categoriaJogo;
            this.precoJogo = precoJogo;
            this.caminhoImagem = caminhoImagem;
        }

        // Construtor sem imagem (opcional)
        public Jogo(String nomeJogo, String categoriaJogo, double precoJogo) {
            this(nomeJogo, categoriaJogo, precoJogo, null);
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

        public String getCategoriaJogo() {
            return categoriaJogo;
        }

        public void setCategoriaJogo(String categoriaJogo) {
            this.categoriaJogo = categoriaJogo;
        }

        public double getPrecoJogo() {
            return precoJogo;
        }

        public void setPrecoJogo(double precoJogo) {
            this.precoJogo = precoJogo;
        }

        public int getEstoqueJogo() {
        }
    }


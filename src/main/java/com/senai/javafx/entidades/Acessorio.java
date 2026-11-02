package com.senai.javafx.entidades;

// Item da Loja de Acessórios: console, PC gamer ou controle
public class Acessorio implements Produto {

    public enum Tipo {
        CONSOLE("Console", "Consoles"),
        COMPUTADOR("PC Gamer", "PCs"),
        CONTROLE("Controle", "Controles");

        private final String rotulo;
        private final String rotuloPlural;

        Tipo(String rotulo, String rotuloPlural) {
            this.rotulo = rotulo;
            this.rotuloPlural = rotuloPlural;
        }

        public String getRotulo() { return rotulo; }
        public String getRotuloPlural() { return rotuloPlural; }
    }

    private String nome;
    private final Tipo tipo;
    private final String plataforma; // Playstation, Xbox, Nintendo ou PC
    private final String detalhe;    // ex.: "1 TB • com leitor de disco"
    private double preco;
    private final String caminhoImagem;
    private int estoque;

    public Acessorio(String nome, Tipo tipo, String plataforma, String detalhe,
                     double preco, String caminhoImagem, int estoque) {
        this.nome = nome;
        this.tipo = tipo;
        this.plataforma = plataforma;
        this.detalhe = detalhe;
        this.preco = preco;
        this.caminhoImagem = caminhoImagem;
        this.estoque = estoque;
    }

    // O administrador pode corrigir nome, preço e estoque
    public void setNome(String nome) { this.nome = nome; }

    public void setPreco(double preco) { this.preco = preco; }

    public Tipo getTipoAcessorio() { return tipo; }

    public String getPlataforma() { return plataforma; }

    public String getDetalhe() { return detalhe; }

    public boolean temPlataforma(String outra) {
        return plataforma.equalsIgnoreCase(outra);
    }

    // --- Produto ---

    @Override
    public String getNome() { return nome; }

    @Override
    public double getPreco() { return preco; }

    @Override
    public int getEstoque() { return estoque; }

    @Override
    public void setEstoque(int estoque) { this.estoque = estoque; }

    @Override
    public String getCaminhoImagem() { return caminhoImagem; }

    @Override
    public String getTipo() { return tipo.getRotulo(); }

    @Override
    public String getDescricao() {
        boolean temDetalhe = detalhe != null && !detalhe.isBlank();
        // O detalhe do controle já diz para qual console ele serve ("Para PlayStation 5")
        if (tipo == Tipo.CONTROLE && temDetalhe) {
            return detalhe;
        }
        return temDetalhe ? plataforma + " • " + detalhe : plataforma;
    }

    @Override
    public String getEtiqueta() {
        String texto = tipo.getRotulo() + " | " + plataforma;
        if (detalhe != null && !detalhe.isBlank()) {
            texto += " | " + detalhe;
        }
        return texto.toUpperCase();
    }
}

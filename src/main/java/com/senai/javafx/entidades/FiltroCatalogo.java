package com.senai.javafx.entidades;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.text.Normalizer;
import java.util.List;
import java.util.Objects;

public final class FiltroCatalogo {

    public static final List<String> CONSOLES = List.of("Nintendo", "Playstation", "Xbox", "PC");

    private static final StringProperty categoria = new SimpleStringProperty();
    private static final StringProperty console = new SimpleStringProperty();

    // Busca por nome e faixa de preço (null = sem limite)
    private static final StringProperty busca = new SimpleStringProperty();
    private static final ObjectProperty<Double> precoMin = new SimpleObjectProperty<>();
    private static final ObjectProperty<Double> precoMax = new SimpleObjectProperty<>();

    private FiltroCatalogo() {}

    public static StringProperty categoriaProperty() { return categoria; }
    public static StringProperty consoleProperty() { return console; }
    public static StringProperty buscaProperty() { return busca; }
    public static ObjectProperty<Double> precoMinProperty() { return precoMin; }
    public static ObjectProperty<Double> precoMaxProperty() { return precoMax; }

    public static String getCategoria() { return categoria.get(); }
    public static String getConsole() { return console.get(); }
    public static String getBusca() { return busca.get(); }
    public static Double getPrecoMin() { return precoMin.get(); }
    public static Double getPrecoMax() { return precoMax.get(); }

    public static void setBusca(String texto) {
        busca.set(texto == null || texto.isBlank() ? null : texto.trim());
    }

    public static void setPrecoMin(Double valor) { precoMin.set(valor); }
    public static void setPrecoMax(Double valor) { precoMax.set(valor); }

    public static boolean temBuscaOuPreco() {
        return busca.get() != null || precoMin.get() != null || precoMax.get() != null;
    }

    // Clicar no que já está selecionado remove o filtro
    public static void alternarCategoria(String nova) {
        categoria.set(Objects.equals(categoria.get(), nova) ? null : nova);
    }

    public static void alternarConsole(String novo) {
        console.set(Objects.equals(console.get(), novo) ? null : novo);
    }

    public static void limpar() {
        categoria.set(null);
        console.set(null);
        busca.set(null);
        precoMin.set(null);
        precoMax.set(null);
    }

    public static boolean aceita(Jogo jogo) {
        boolean okCategoria = categoria.get() == null
                || categoria.get().equalsIgnoreCase(jogo.getCategoriaJogo());
        boolean okConsole = console.get() == null
                || console.get().equalsIgnoreCase(jogo.getConsoleJogo());

        return okCategoria && okConsole && aceitaNome(jogo) && aceitaPreco(jogo);
    }

    private static boolean aceitaNome(Jogo jogo) {
        if (busca.get() == null) {
            return true;
        }
        String nome = jogo.getNomeJogo() == null ? "" : jogo.getNomeJogo();
        return normalizar(nome).contains(normalizar(busca.get()));
    }

    private static boolean aceitaPreco(Jogo jogo) {
        Double min = precoMin.get();
        Double max = precoMax.get();

        // Se o usuário inverter os valores (ex.: de 60 até 10), corrige sozinho
        if (min != null && max != null && min > max) {
            double troca = min;
            min = max;
            max = troca;
        }

        double preco = jogo.getPrecoJogo();
        return (min == null || preco >= min) && (max == null || preco <= max);
    }

    // Ignora maiúsculas/minúsculas e acentos: "pokémon" encontra "Pokemon"
    private static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    // Converte "10", "10,50", "R$ 1.234,56" ou "10.5" em número; vazio/inválido vira null
    public static Double converterPreco(String texto) {
        if (texto == null) {
            return null;
        }
        String limpo = texto.replaceAll("[^0-9.,]", "");
        if (limpo.isEmpty()) {
            return null;
        }
        if (limpo.contains(",")) {
            limpo = limpo.replace(".", "").replace(",", ".");
        } else if (limpo.indexOf('.') != limpo.lastIndexOf('.')) {
            limpo = limpo.replace(".", "");
        }
        try {
            return Double.parseDouble(limpo);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

package com.senai.javafx.entidades;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.List;
import java.util.Objects;

public final class FiltroCatalogo {

    public static final List<String> CONSOLES = List.of("Nintendo", "Playstation", "Xbox", "PC");

    private static final StringProperty categoria = new SimpleStringProperty();
    private static final StringProperty console = new SimpleStringProperty();

    private FiltroCatalogo() {}

    public static StringProperty categoriaProperty() { return categoria; }
    public static StringProperty consoleProperty() { return console; }
    public static String getCategoria() { return categoria.get(); }
    public static String getConsole() { return console.get(); }

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
    }

    public static boolean aceita(Jogo jogo) {
        boolean okCategoria = categoria.get() == null
                || categoria.get().equalsIgnoreCase(jogo.getCategoriaJogo());
        boolean okConsole = console.get() == null
                || console.get().equalsIgnoreCase(jogo.getConsoleJogo());
        return okCategoria && okConsole;
    }
}
package com.senai.javafx.entidades;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

// Transforma um MenuButton em uma lista de opções com caixinhas (várias escolhas)
public class SeletorMultiplo {

    private final MenuButton botao;
    private final ObservableList<String> opcoes;
    private final String textoVazio;
    private final Set<String> selecionados = new LinkedHashSet<>();

    // Guarda uma referência forte para o listener fraco não ser coletado
    private final ListChangeListener<String> aoMudarOpcoes = c -> reconstruir();

    public SeletorMultiplo(MenuButton botao, ObservableList<String> opcoes, String textoVazio) {
        this.botao = botao;
        this.opcoes = opcoes;
        this.textoVazio = textoVazio;
        this.opcoes.addListener(new WeakListChangeListener<>(aoMudarOpcoes));
        reconstruir();
    }

    // Itens marcados, na mesma ordem das opções
    public List<String> getSelecionados() {
        List<String> resultado = new ArrayList<>();
        for (String opcao : todasAsOpcoes()) {
            if (selecionados.contains(opcao)) {
                resultado.add(opcao);
            }
        }
        return resultado;
    }

    public void setSelecionados(Collection<String> novos) {
        selecionados.clear();
        if (novos != null) {
            selecionados.addAll(novos);
        }
        reconstruir();
    }

    public void limpar() {
        setSelecionados(null);
    }

    // Opções disponíveis + itens já marcados que não estão mais na lista
    private Set<String> todasAsOpcoes() {
        Set<String> todas = new LinkedHashSet<>(opcoes);
        todas.addAll(selecionados);
        return todas;
    }

    private void reconstruir() {
        List<MenuItem> itens = new ArrayList<>();

        for (String opcao : todasAsOpcoes()) {
            CheckBox caixa = new CheckBox(opcao);
            caixa.setSelected(selecionados.contains(opcao));
            caixa.selectedProperty().addListener((obs, antigo, novo) -> {
                if (novo) {
                    selecionados.add(opcao);
                } else {
                    selecionados.remove(opcao);
                }
                atualizarTexto();
            });

            // hideOnClick = false: o menu fica aberto para marcar várias opções
            CustomMenuItem item = new CustomMenuItem(caixa, false);
            itens.add(item);
        }

        botao.getItems().setAll(itens);
        atualizarTexto();
    }

    private void atualizarTexto() {
        List<String> marcados = getSelecionados();
        botao.setText(marcados.isEmpty() ? textoVazio : String.join(", ", marcados));
    }
}
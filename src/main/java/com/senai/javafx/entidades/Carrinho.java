package com.senai.javafx.entidades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

// Carrinho do cliente logado (um só para o app inteiro, como a Sessao)
public final class Carrinho {

    private static final List<ItemCarrinho> itens = new ArrayList<>();
    private static final List<Runnable> ouvintes = new ArrayList<>();

    private Carrinho() {}

    // --- OUVINTES (telas que precisam se atualizar quando o carrinho muda) ---

    public static void addOuvinte(Runnable ouvinte) {
        if (!ouvintes.contains(ouvinte)) {
            ouvintes.add(ouvinte);
        }
    }

    public static void removeOuvinte(Runnable ouvinte) {
        ouvintes.remove(ouvinte);
    }

    private static void avisar() {
        for (Runnable ouvinte : new ArrayList<>(ouvintes)) {
            ouvinte.run();
        }
    }

    // --- CONSULTAS ---

    public static List<ItemCarrinho> getItens() {
        return Collections.unmodifiableList(new ArrayList<>(itens));
    }

    public static boolean isVazio() {
        return itens.isEmpty();
    }

    // Quantos jogos diferentes
    public static int getQuantidadeItens() {
        return itens.size();
    }

    // Soma das unidades de todos os jogos
    public static int getQuantidadeUnidades() {
        return itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
    }

    public static double getSubtotal() {
        return FormaPagamento.arredondar(itens.stream().mapToDouble(ItemCarrinho::getSubtotal).sum());
    }

    public static int quantidadeDoJogo(Jogo jogo) {
        ItemCarrinho item = buscar(jogo);
        return item == null ? 0 : item.getQuantidade();
    }

    // Quantas unidades ainda podem ser colocadas no carrinho (estoque - o que já está nele)
    public static int disponivelParaAdicionar(Jogo jogo) {
        return Math.max(0, jogo.getEstoqueJogo() - quantidadeDoJogo(jogo));
    }

    // Ex.: "2x Elden Ring, 1x GTA VI"
    public static String getResumoItens() {
        List<String> partes = new ArrayList<>();
        for (ItemCarrinho item : itens) {
            partes.add(item.getQuantidade() + "x " + item.getJogo().getNomeJogo());
        }
        return String.join(", ", partes);
    }

    // --- ALTERAÇÕES ---

    // Devolve false (e não altera nada) se passar do estoque
    public static boolean adicionar(Jogo jogo, int quantidade) {
        if (quantidade < 1 || quantidade > disponivelParaAdicionar(jogo)) {
            return false;
        }
        ItemCarrinho item = buscar(jogo);
        if (item == null) {
            itens.add(new ItemCarrinho(jogo, quantidade));
        } else {
            item.setQuantidade(item.getQuantidade() + quantidade);
        }
        avisar();
        return true;
    }

    // Define a quantidade exata (limitada ao estoque); quantidade < 1 remove o item
    public static void definirQuantidade(ItemCarrinho item, int quantidade) {
        if (quantidade < 1) {
            itens.remove(item);
        } else {
            item.setQuantidade(Math.min(quantidade, Math.max(1, item.getJogo().getEstoqueJogo())));
        }
        avisar();
    }

    public static void remover(ItemCarrinho item) {
        if (itens.remove(item)) {
            avisar();
        }
    }

    public static void limpar() {
        if (!itens.isEmpty()) {
            itens.clear();
            avisar();
        }
    }

    // Corrige o carrinho se o estoque mudou ou o jogo saiu do catálogo.
    // Devolve as mensagens do que foi mudado (lista vazia = nada mudou).
    public static List<String> ajustarAoEstoque(Predicate<Jogo> aindaExiste) {
        List<String> avisos = new ArrayList<>();
        for (ItemCarrinho item : new ArrayList<>(itens)) {
            Jogo jogo = item.getJogo();
            int estoque = jogo.getEstoqueJogo();
            if (!aindaExiste.test(jogo)) {
                itens.remove(item);
                avisos.add("\"" + jogo.getNomeJogo() + "\" saiu do catálogo e foi removido do carrinho.");
            } else if (estoque <= 0) {
                itens.remove(item);
                avisos.add("\"" + jogo.getNomeJogo() + "\" esgotou e foi removido do carrinho.");
            } else if (item.getQuantidade() > estoque) {
                item.setQuantidade(estoque);
                avisos.add("\"" + jogo.getNomeJogo() + "\": só restam " + estoque
                        + " unidade(s); a quantidade foi ajustada.");
            }
        }
        if (!avisos.isEmpty()) {
            avisar();
        }
        return avisos;
    }

    private static ItemCarrinho buscar(Jogo jogo) {
        for (ItemCarrinho item : itens) {
            if (item.getJogo() == jogo) {
                return item;
            }
        }
        return null;
    }
}

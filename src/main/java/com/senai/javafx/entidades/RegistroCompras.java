package com.senai.javafx.entidades;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

// Guarda as compras: pendentes (aguardando o admin) e o histórico (aprovadas)
public final class RegistroCompras {

    private static final List<Compra> pendentes = new ArrayList<>();
    private static final List<Compra> historico = new ArrayList<>();
    private static int proximoNumero = 1;

    private RegistroCompras() {}

    public static List<Compra> listarPendentes() {
        return Collections.unmodifiableList(new ArrayList<>(pendentes));
    }

    // Mais recentes primeiro (pela data escolhida pelo admin)
    public static List<Compra> listarHistorico() {
        List<Compra> copia = new ArrayList<>(historico);
        copia.sort(Comparator.comparing(Compra::getDataCompra).thenComparing(Compra::getNumero).reversed());
        return copia;
    }

    // Compras pendentes de um cliente (para saber se ele ainda tem algo em análise)
    public static List<Compra> pendentesDoCliente(String nome) {
        List<Compra> lista = new ArrayList<>();
        for (Compra c : pendentes) {
            if (c.getNomeCliente().equalsIgnoreCase(nome)) {
                lista.add(c);
            }
        }
        return lista;
    }

    /**
     * Finaliza o pagamento do carrinho: confere o estoque, desconta, cria a compra
     * como PENDENTE e esvazia o carrinho.
     *
     * @throws IllegalStateException com a mensagem para o cliente, se algo estiver errado
     */
    public static Compra finalizarPagamento(String nomeCliente, FormaPagamento forma, String cpf,
                                            String nomeTitular, String senhaCartao,
                                            Predicate<Jogo> aindaExiste) {
        if (Carrinho.isVazio()) {
            throw new IllegalStateException("O carrinho está vazio.");
        }
        if (forma == null) {
            throw new IllegalStateException("Escolha a forma de pagamento.");
        }

        // Cada forma de pagamento pede dados diferentes (a senha do cartão nunca é guardada)
        String cpfRegistrado = "—";
        switch (forma) {
            case PIX -> {
                if (!Cpf.valido(cpf)) {
                    throw new IllegalStateException("CPF inválido. Confira os números digitados.");
                }
                cpfRegistrado = Cpf.formatar(cpf);
            }
            case CARTAO -> {
                if (!nomeCompletoValido(nomeTitular)) {
                    throw new IllegalStateException("Informe o nome completo do titular do cartão.");
                }
                if (senhaCartao == null || !senhaCartao.matches("\\d{4,6}")) {
                    throw new IllegalStateException("A senha do cartão deve ter de 4 a 6 números.");
                }
            }
            case DINHEIRO -> { } // não pede nada
        }

        // Confere tudo ANTES de mexer no estoque, para não deixar a compra pela metade
        List<ItemCarrinho> itensCarrinho = Carrinho.getItens();
        for (ItemCarrinho item : itensCarrinho) {
            Jogo jogo = item.getJogo();
            if (!aindaExiste.test(jogo)) {
                throw new IllegalStateException("\"" + jogo.getNomeJogo() + "\" não está mais no catálogo.");
            }
            if (item.getQuantidade() > jogo.getEstoqueJogo()) {
                throw new IllegalStateException("Estoque insuficiente de \"" + jogo.getNomeJogo()
                        + "\" (disponível: " + jogo.getEstoqueJogo() + ").");
            }
        }

        List<ItemCompra> itensCompra = new ArrayList<>();
        for (ItemCarrinho item : itensCarrinho) {
            Jogo jogo = item.getJogo();
            jogo.setEstoqueJogo(jogo.getEstoqueJogo() - item.getQuantidade());
            itensCompra.add(new ItemCompra(jogo, item.getQuantidade()));
        }

        Compra compra = new Compra(proximoNumero++, nomeCliente, cpfRegistrado, itensCompra, forma);
        pendentes.add(compra);
        Carrinho.limpar();
        return compra;
    }

    // Recusa todas as compras pendentes do cliente (os jogos voltam ao estoque). Devolve quantas foram.
    public static int cancelarPendentesDoCliente(String nome) {
        List<Compra> doCliente = pendentesDoCliente(nome); // já é uma cópia: pode recusar enquanto percorre
        doCliente.forEach(RegistroCompras::recusar);
        return doCliente.size();
    }

    // "Nome inteiro": pelo menos duas palavras (nome e sobrenome)
    public static boolean nomeCompletoValido(String nome) {
        return nome != null && nome.trim().split("\\s+").length >= 2;
    }

    // Admin libera a compra e define a data dela; ela passa para o histórico
    public static void aprovar(Compra compra, LocalDate data) {
        if (compra == null || data == null || !pendentes.remove(compra)) {
            return;
        }
        compra.setDataCompra(data);
        compra.setStatus(Compra.Status.APROVADA);
        historico.add(compra);
    }

    // Admin recusa: os jogos voltam para o estoque
    public static void recusar(Compra compra) {
        if (compra == null || !pendentes.remove(compra)) {
            return;
        }
        for (ItemCompra item : compra.getItens()) {
            Jogo jogo = item.getJogo();
            jogo.setEstoqueJogo(jogo.getEstoqueJogo() + item.getQuantidade());
        }
        compra.setStatus(Compra.Status.RECUSADA);
    }
}

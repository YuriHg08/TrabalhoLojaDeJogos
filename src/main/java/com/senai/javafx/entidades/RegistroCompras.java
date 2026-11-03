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
    private static final List<Compra> recusadas = new ArrayList<>();
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

    // Histórico (aprovadas) só da conta informada, mais recentes primeiro.
    // Compara pelo id da conta: outra conta com o mesmo nome não enxerga estas compras.
    public static List<Compra> historicoDoCliente(int idCliente) {
        List<Compra> lista = new ArrayList<>();
        for (Compra c : listarHistorico()) {
            if (c.getIdCliente() == idCliente) {
                lista.add(c);
            }
        }
        return lista;
    }

    // Todas as notas da conta (em andamento, aprovadas e recusadas), mais recentes primeiro
    public static List<Compra> notasDoCliente(int idCliente) {
        List<Compra> lista = new ArrayList<>();
        for (List<Compra> origem : List.of(pendentes, historico, recusadas)) {
            for (Compra c : origem) {
                if (c.getIdCliente() == idCliente) {
                    lista.add(c);
                }
            }
        }
        lista.sort(Comparator.comparing(Compra::getCriadaEm).thenComparing(Compra::getNumero).reversed());
        return lista;
    }

    // Compras pendentes de uma conta (para saber se ela ainda tem algo em análise)
    public static List<Compra> pendentesDoCliente(int idCliente) {
        List<Compra> lista = new ArrayList<>();
        for (Compra c : pendentes) {
            if (c.getIdCliente() == idCliente) {
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
    public static Compra finalizarPagamento(int idCliente, String nomeCliente, FormaPagamento forma, String cpf,
                                            String nomeTitular, String senhaCartao,
                                            Predicate<Produto> aindaExiste) {
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
            Produto produto = item.getProduto();
            if (!aindaExiste.test(produto)) {
                throw new IllegalStateException("\"" + produto.getNome() + "\" não está mais no catálogo.");
            }
            if (item.getQuantidade() > produto.getEstoque()) {
                throw new IllegalStateException("Estoque insuficiente de \"" + produto.getNome()
                        + "\" (disponível: " + produto.getEstoque() + ").");
            }
        }

        List<ItemCompra> itensCompra = new ArrayList<>();
        for (ItemCarrinho item : itensCarrinho) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() - item.getQuantidade());
            itensCompra.add(new ItemCompra(produto, item.getQuantidade()));
        }

        Compra compra = new Compra(proximoNumero++, idCliente, nomeCliente, cpfRegistrado, itensCompra, forma);
        pendentes.add(compra);
        Carrinho.limpar();
        return compra;
    }

    // Recusa todas as compras pendentes do cliente (os produtos voltam ao estoque). Devolve quantas foram.
    public static int cancelarPendentesDoCliente(int idCliente) {
        List<Compra> doCliente = pendentesDoCliente(idCliente); // já é uma cópia: pode recusar enquanto percorre
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

    // Admin recusa: os produtos voltam para o estoque
    public static void recusar(Compra compra) {
        if (compra == null || !pendentes.remove(compra)) {
            return;
        }
        for (ItemCompra item : compra.getItens()) {
            Produto produto = item.getProduto();
            produto.setEstoque(produto.getEstoque() + item.getQuantidade());
        }
        compra.setStatus(Compra.Status.RECUSADA);
        recusadas.add(compra);
    }
}

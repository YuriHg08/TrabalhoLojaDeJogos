package com.senai.javafx.entidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Uma compra (a "nota"): nasce PENDENTE no pagamento e o admin aprova ou recusa
public class Compra {

    public enum Status { PENDENTE, APROVADA, RECUSADA }

    private final int numero;
    private final String nomeCliente;
    private final String cpf;
    private final List<ItemCompra> itens;
    private final FormaPagamento formaPagamento;
    private final double subtotal;
    private final double desconto;
    private final double total;
    private final LocalDateTime criadaEm = LocalDateTime.now();

    private Status status = Status.PENDENTE;
    private LocalDate dataCompra; // escolhida pelo admin ao aprovar

    public Compra(int numero, String nomeCliente, String cpf, List<ItemCompra> itens, FormaPagamento formaPagamento) {
        this.numero = numero;
        this.nomeCliente = nomeCliente;
        this.cpf = cpf;
        this.itens = Collections.unmodifiableList(new ArrayList<>(itens));
        this.formaPagamento = formaPagamento;
        this.subtotal = FormaPagamento.arredondar(itens.stream().mapToDouble(ItemCompra::getSubtotal).sum());
        this.desconto = formaPagamento.calcularDesconto(subtotal);
        this.total = FormaPagamento.arredondar(subtotal - desconto);
    }

    public int getNumero() { return numero; }
    public String getNomeCliente() { return nomeCliente; }
    public String getCpf() { return cpf; }
    public List<ItemCompra> getItens() { return itens; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public double getSubtotal() { return subtotal; }
    public double getDesconto() { return desconto; }
    public double getTotal() { return total; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public Status getStatus() { return status; }
    public LocalDate getDataCompra() { return dataCompra; }

    void setStatus(Status status) { this.status = status; }
    void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }

    public int getQuantidadeTotal() {
        return itens.stream().mapToInt(ItemCompra::getQuantidade).sum();
    }

    // Ex.: "2x Elden Ring, 1x GTA VI"
    public String getResumoItens() {
        List<String> partes = new ArrayList<>();
        for (ItemCompra item : itens) {
            partes.add(item.getQuantidade() + "x " + item.getNomeJogo());
        }
        return String.join(", ", partes);
    }
}

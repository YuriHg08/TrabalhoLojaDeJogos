package com.senai.javafx.entidades;


public class Usuario {
    private static int proximoId = 1;

    // Identificador único da conta: nunca se repete, mesmo se outra conta usar o mesmo nome depois
    private final int id = proximoId++;
    private final String nome;
    private final String senha;

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public int getId() { return id; }

    public String getNome() { return nome; }
    public String getSenha() { return senha; }

    private boolean bloqueado = false;

    public boolean isBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(boolean bloqueado) {
        this.bloqueado = bloqueado;
    }
}
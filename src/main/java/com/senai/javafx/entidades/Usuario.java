package com.senai.javafx.entidades;


public class Usuario {
    private final String nome;
    private final String senha;

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

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
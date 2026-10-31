package com.senai.javafx.entidades;

public class Sessao {
    private static String usuarioLogado;
    private static String senhaLogada;

    public static void iniciarSessao(String usuario, String senha) {
        usuarioLogado = usuario;
        senhaLogada = senha;
    }

    public static void encerrarSessao() {
        usuarioLogado = null;
        senhaLogada = null;
    }

    public static String getUsuarioLogado() {
        return usuarioLogado;
    }

    public static String getSenhaLogada() {
        return senhaLogada;
    }
}
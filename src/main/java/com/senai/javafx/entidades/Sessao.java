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
    // Mesmo critério usado no login: admin / admin
    public static boolean isAdmin() {
        return "admin".equals(usuarioLogado) && "admin".equals(senhaLogada);
    }

    public static String getUsuarioLogado() {
        return usuarioLogado;
    }

    public static String getSenhaLogada() {
        return senhaLogada;
    }
}
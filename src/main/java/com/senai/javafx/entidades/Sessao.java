package com.senai.javafx.entidades;

public class Sessao {
    private static String usuarioLogado;
    private static String senhaLogada;
    private static int idUsuarioLogado = UsuarioCadastro.SEM_CONTA;

    public static void iniciarSessao(String usuario, String senha) {
        usuarioLogado = usuario;
        senhaLogada = senha;
        // O admin não é uma conta cadastrada; os clientes têm um id único
        idUsuarioLogado = "admin".equals(usuario) && "admin".equals(senha)
                ? 0
                : UsuarioCadastro.buscarId(usuario);
    }

    public static void encerrarSessao() {
        usuarioLogado = null;
        senhaLogada = null;
        idUsuarioLogado = UsuarioCadastro.SEM_CONTA;
    }

    // Id da conta logada (usado para ligar as compras à conta, e não só ao nome)
    public static int getIdUsuarioLogado() {
        return idUsuarioLogado;
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
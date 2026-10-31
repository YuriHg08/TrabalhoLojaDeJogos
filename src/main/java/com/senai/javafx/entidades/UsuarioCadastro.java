package com.senai.javafx.entidades;

import java.util.ArrayList;
import java.util.List;

public class UsuarioCadastro {
    private static final List<Usuario> usuarios = new ArrayList<>();

    public static boolean existe(String nome) {
        return usuarios.stream().anyMatch(u -> u.getNome().equalsIgnoreCase(nome));
    }

    public static boolean cadastrar(String nome, String senha) {
        if (existe(nome)) return false;
        usuarios.add(new Usuario(nome, senha));
        return true;
    }

    public static boolean autenticar(String nome, String senha) {
        return usuarios.stream().anyMatch(u ->
                u.getNome().equalsIgnoreCase(nome) && u.getSenha().equals(senha));
    }
}
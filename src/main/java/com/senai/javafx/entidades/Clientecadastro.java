package com.senai.javafx.entidades;
import java.util.ArrayList;
import java.util.List;
public class Clientecadastro {
    private static final List<Cliente> clientes = new ArrayList<>();

    public static void cadastrar(Cliente cliente) {
        clientes.add(cliente);
    }
    public static List<Cliente> listar() {
        return clientes;

    }
}

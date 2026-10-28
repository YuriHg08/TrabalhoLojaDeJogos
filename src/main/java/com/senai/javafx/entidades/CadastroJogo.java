package com.senai.javafx.entidades;
import java.util.ArrayList;
import java.util.List;
public class CadastroJogo {
    private static final List<Jogo> jogosCadastrados = new ArrayList<>();

    public static void cadastrar(Jogo nomeJogo) {
        jogosCadastrados.add(nomeJogo);
    }
    public static List<Jogo> listar() {
        return jogosCadastrados;

    }

}

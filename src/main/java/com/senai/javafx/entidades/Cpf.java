package com.senai.javafx.entidades;

// Utilitário para o CPF usado como "chave Pix"
public final class Cpf {

    private Cpf() {}

    public static String somenteDigitos(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }

    // 12345678909 -> 123.456.789-09 (formata o quanto já foi digitado)
    public static String formatar(String texto) {
        String d = somenteDigitos(texto);
        if (d.length() > 11) {
            d = d.substring(0, 11);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d.length(); i++) {
            if (i == 3 || i == 6) {
                sb.append('.');
            } else if (i == 9) {
                sb.append('-');
            }
            sb.append(d.charAt(i));
        }
        return sb.toString();
    }

    // Confere tamanho, dígitos repetidos (111.111.111-11) e os dois dígitos verificadores
    public static boolean valido(String texto) {
        String d = somenteDigitos(texto);
        if (d.length() != 11 || d.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(d, 9) == d.charAt(9) - '0'
                && digitoVerificador(d, 10) == d.charAt(10) - '0';
    }

    private static int digitoVerificador(String d, int quantidade) {
        int soma = 0;
        for (int i = 0; i < quantidade; i++) {
            soma += (d.charAt(i) - '0') * (quantidade + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}

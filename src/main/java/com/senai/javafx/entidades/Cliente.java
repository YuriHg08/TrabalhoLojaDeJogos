package com.senai.javafx.entidades;

public class Cliente {
    private String cpf;
    private String nomecliente;
    private String telefone;

    public Cliente(String nomecliente, String cpf, String telefone) {
        this.cpf = cpf;
        this.nomecliente = nomecliente;
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNomecliente() {
        return nomecliente;
    }

    public void setNomecliente(String nomecliente) {
        this.nomecliente = nomecliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}

package com.senai.javafx.entidades;

import com.senai.javafx.controllers.LadoAdministrador.CadastroJogosController;

// Pergunta única "esse produto ainda está à venda?", valendo para jogos e acessórios.
// Usada pelo carrinho e pelo pagamento.
public final class CatalogoProdutos {

    private CatalogoProdutos() {}

    public static boolean existe(Produto produto) {
        if (produto instanceof Acessorio) {
            return CatalogoAcessorios.contem(produto);
        }
        return CadastroJogosController.jogosSalvos.contains(produto);
    }
}

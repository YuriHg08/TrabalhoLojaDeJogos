package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.Acessorio;
import com.senai.javafx.entidades.CatalogoAcessorios;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.Locale;

// Janela usada pelo administrador para editar nome, preço e estoque de um acessório
public class EditarAcessorioController {

    @FXML
    private TextField txtNomeAcessorio;
    @FXML
    private TextField txtPrecoAcessorio;
    @FXML
    private TextField txtEstoqueAcessorio;
    @FXML
    private Label lblInfo;
    @FXML
    private Label lblResposta;

    private Acessorio acessorio;
    private Stage stage;

    // Abre a janela de edição (modal) para o acessório escolhido
    public static void abrir(Acessorio acessorio, Window dono) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditarAcessorioController.class.getResource("/com/senai/javafx/editarAcessorio.fxml"));
            Parent root = loader.load();

            EditarAcessorioController controller = loader.getController();

            Stage janela = new Stage();
            janela.initOwner(dono);
            janela.initModality(Modality.APPLICATION_MODAL);

            // 1. Remove a barra padrão do Windows
            janela.initStyle(StageStyle.UNDECORATED);
            janela.setResizable(false);

            // 2. Permite movimentar a janela clicando e arrastando pelo ecrã
            final double[] arrasto = new double[2];
            root.setOnMousePressed(e -> {
                arrasto[0] = janela.getX() - e.getScreenX();
                arrasto[1] = janela.getY() - e.getScreenY();
            });
            root.setOnMouseDragged(e -> {
                janela.setX(e.getScreenX() + arrasto[0]);
                janela.setY(e.getScreenY() + arrasto[1]);
            });

            Scene cena = new Scene(root);
            cena.getStylesheets().add(
                    EditarAcessorioController.class.getResource("/com/senai/javafx/styleMenu.css").toExternalForm());
            janela.setScene(cena);

            controller.stage = janela;
            controller.carregar(acessorio);

            janela.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            GerenciamentoJanela.exibirAlerta("Erro",
                    "Não foi possível abrir a edição: " + causa,
                    GerenciamentoJanela.TipoAlerta.ERRO);
        }
    }

    private void carregar(Acessorio acessorio) {
        this.acessorio = acessorio;
        txtNomeAcessorio.setText(acessorio.getNome());
        txtPrecoAcessorio.setText(String.format(Locale.US, "%.2f", acessorio.getPreco()));
        txtEstoqueAcessorio.setText(String.valueOf(acessorio.getEstoque()));
        lblInfo.setText(acessorio.getTipo() + " • " + acessorio.getDescricao());
    }

    @FXML
    private void salvar() {
        String nome = txtNomeAcessorio.getText().trim();
        String precoTexto = txtPrecoAcessorio.getText().trim();
        String estoqueTexto = txtEstoqueAcessorio.getText().trim();

        if (nome.isEmpty()) {
            lblResposta.setText("Insira um nome para o acessório!");
            return;
        }

        // O nome não pode repetir o de OUTRO acessório (pode manter o próprio nome)
        boolean nomeEmUso = CatalogoAcessorios.TODOS.stream()
                .anyMatch(a -> a != acessorio && a.getNome().equalsIgnoreCase(nome));
        if (nomeEmUso) {
            lblResposta.setText("Já existe outro acessório com esse nome!");
            return;
        }

        if (precoTexto.isEmpty()) {
            lblResposta.setText("Insira o preço do acessório!");
            return;
        }
        if (estoqueTexto.isEmpty()) {
            lblResposta.setText("Insira o estoque do acessório!");
            return;
        }

        double preco;
        int estoque;
        try {
            preco = Double.parseDouble(precoTexto.replace(",", "."));
            estoque = Integer.parseInt(estoqueTexto);
        } catch (NumberFormatException e) {
            lblResposta.setText("Preço ou estoque inválido! Ex: 599.90 e 5");
            return;
        }

        if (preco < 0) {
            lblResposta.setText("O preço não pode ser negativo!");
            return;
        }
        if (estoque < 0) {
            lblResposta.setText("O estoque não pode ser negativo!");
            return;
        }

        acessorio.setNome(nome);
        acessorio.setPreco(preco);
        acessorio.setEstoque(estoque);

        // Avisa quem observa a lista (tabela e loja) que este acessório mudou
        int indice = CatalogoAcessorios.TODOS.indexOf(acessorio);
        if (indice >= 0) {
            CatalogoAcessorios.TODOS.set(indice, acessorio);
        }

        stage.close();
    }

    @FXML
    private void cancelar() {
        stage.close();
    }
}
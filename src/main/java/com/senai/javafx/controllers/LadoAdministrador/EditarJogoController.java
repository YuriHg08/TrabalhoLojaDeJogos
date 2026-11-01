package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.FiltroCatalogo;
import com.senai.javafx.entidades.Jogo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.File;
import java.util.Locale;

import static com.senai.javafx.controllers.LadoAdministrador.CadastroCategoriasController.categoriasSalvas;

// Janela usada pelo administrador para editar ou remover um jogo do catálogo
public class EditarJogoController {

    @FXML
    private TextField txtNomeJogo;
    @FXML
    private TextField txtPrecoJogo;
    @FXML
    private TextField txtEstoqueJogo;
    @FXML
    private ComboBox<String> menuCategoria;
    @FXML
    private ComboBox<String> menuConsole;
    @FXML
    private ImageView imgPreview;
    @FXML
    private Label lblResposta;
    @FXML
    private Button btnRemover;

    private Jogo jogo;
    private Stage stage;
    private String caminhoImagem;
    private boolean confirmandoRemocao = false;

    // Abre a janela de edição (modal) para o jogo escolhido
    public static void abrir(Jogo jogo, Window dono) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    EditarJogoController.class.getResource("/com/senai/javafx/editarJogo.fxml"));
            Parent root = loader.load();

            EditarJogoController controller = loader.getController();

            Stage janela = new Stage();
            janela.initOwner(dono);
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.setTitle("Editar jogo");
            janela.setResizable(false);
            janela.setScene(new Scene(root));

            controller.stage = janela;
            controller.carregar(jogo);

            janela.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        menuCategoria.setItems(categoriasSalvas);
        menuConsole.getItems().setAll(FiltroCatalogo.CONSOLES);
    }

    private void carregar(Jogo jogo) {
        this.jogo = jogo;
        this.caminhoImagem = jogo.getCaminhoImagem();

        txtNomeJogo.setText(jogo.getNomeJogo());
        txtPrecoJogo.setText(String.format(Locale.US, "%.2f", jogo.getPrecoJogo()));
        txtEstoqueJogo.setText(String.valueOf(jogo.getEstoqueJogo()));
        menuCategoria.setValue(jogo.getCategoriaJogo());
        menuConsole.setValue(jogo.getConsoleJogo());
        mostrarPreview();
    }

    private void mostrarPreview() {
        if (caminhoImagem == null || caminhoImagem.isBlank()) {
            imgPreview.setImage(null);
            return;
        }
        try {
            imgPreview.setImage(new Image(caminhoImagem, 220, 220, true, true, true));
        } catch (Exception e) {
            imgPreview.setImage(null);
        }
    }

    @FXML
    private void trocarCapa(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Capa do Jogo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Arquivos de Imagem (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));

        File arquivo = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());
        if (arquivo != null) {
            caminhoImagem = arquivo.toURI().toString();
            mostrarPreview();
        }
    }

    @FXML
    private void salvar() {
        desfazerConfirmacaoRemocao();

        String nome = txtNomeJogo.getText().trim();
        String precoTexto = txtPrecoJogo.getText().trim();
        String estoqueTexto = txtEstoqueJogo.getText().trim();
        String categoria = menuCategoria.getValue();
        String console = menuConsole.getValue();

        if (nome.isEmpty()) {
            lblResposta.setText("Insira um nome para o jogo!");
            return;
        }

        // O nome não pode repetir o de OUTRO jogo (o jogo pode manter o próprio nome)
        boolean nomeEmUso = CadastroJogosController.jogosSalvos.stream()
                .anyMatch(j -> j != jogo && j.getNomeJogo().equalsIgnoreCase(nome));
        if (nomeEmUso) {
            lblResposta.setText("Já existe outro jogo com esse nome!");
            return;
        }

        if (precoTexto.isEmpty()) {
            lblResposta.setText("Insira o preço do jogo!");
            return;
        }
        if (estoqueTexto.isEmpty()) {
            lblResposta.setText("Insira o estoque do jogo!");
            return;
        }
        if (categoria == null || categoria.isEmpty()) {
            lblResposta.setText("Selecione uma categoria!");
            return;
        }
        if (console == null || console.isEmpty()) {
            lblResposta.setText("Selecione uma plataforma!");
            return;
        }

        double preco;
        int estoque;
        try {
            preco = Double.parseDouble(precoTexto.replace(",", "."));
            estoque = Integer.parseInt(estoqueTexto);
        } catch (NumberFormatException e) {
            lblResposta.setText("Preço ou estoque inválido! Ex: 59.90 e 5");
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

        jogo.setNomeJogo(nome);
        jogo.setPrecoJogo(preco);
        jogo.setEstoqueJogo(estoque);
        jogo.setCategoriaJogo(categoria);
        jogo.setConsoleJogo(console);
        jogo.setCaminhoImagem(caminhoImagem);

        // Avisa quem observa a lista (catálogo e tabela) que este jogo mudou
        int indice = CadastroJogosController.jogosSalvos.indexOf(jogo);
        if (indice >= 0) {
            CadastroJogosController.jogosSalvos.set(indice, jogo);
        }

        stage.close();
    }

    // Primeiro clique pede confirmação; o segundo remove de fato
    @FXML
    private void removerJogo() {
        if (!confirmandoRemocao) {
            confirmandoRemocao = true;
            btnRemover.setText("Clique de novo para confirmar");
            lblResposta.setText("Isso remove o jogo do catálogo.");
            return;
        }
        CadastroJogosController.jogosSalvos.remove(jogo);
        stage.close();
    }

    @FXML
    private void cancelar() {
        stage.close();
    }

    private void desfazerConfirmacaoRemocao() {
        confirmandoRemocao = false;
        btnRemover.setText("Remover do catálogo");
    }
}
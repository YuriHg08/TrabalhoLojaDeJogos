package com.senai.javafx.controllers;

import com.senai.javafx.entidades.HelloApplication;
import com.senai.javafx.entidades.Jogo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

import static com.senai.javafx.controllers.CadastroCategoriasController.categoriasSalvas;

public class CadastroJogosController {

    @FXML
    private TextField txtNomeJogo;

    @FXML
    private TextField txtPrecoJogo;

    @FXML
    private ComboBox<String> menuCategoria;

    @FXML
    private ImageView imgPreview;

    @FXML
    private Label respostaJogo;

    // Lista de objetos do tipo Jogo
    public static final ObservableList<Jogo> jogosSalvos = FXCollections.observableArrayList();

    private String caminhoImagemSelecionada;

    @FXML
    public void initialize() {
        if (categoriasSalvas != null) {
            menuCategoria.setItems(categoriasSalvas);
        }
    }


    @FXML
    private void selecionarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Capa do Jogo");

        // Filtro para aceitar apenas formatos de imagem
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "Arquivos de Imagem (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"
        );
        fileChooser.getExtensionFilters().add(extFilter);

        // Obtém o Stage atual a partir do clique do botão
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File arquivo = fileChooser.showOpenDialog(stage);

        if (arquivo != null) {
            caminhoImagemSelecionada = arquivo.toURI().toString();
            imgPreview.setImage(new Image(caminhoImagemSelecionada));
        }
    }

    @FXML
    void cadastrarJogo() {
        String nome = txtNomeJogo.getText().trim();
        String precoTexto = txtPrecoJogo.getText().trim();
        String categoria = menuCategoria.getValue();

        // Validações dos campos obrigatórios
        if (nome.isEmpty()) {
            respostaJogo.setText("Insira um nome para o jogo!");
            return;
        }

        // Verifica se já existe um jogo cadastrado com o mesmo nome
        boolean jogoExiste = jogosSalvos.stream()
                .anyMatch(j -> j.getNomeJogo().equalsIgnoreCase(nome));

        if (jogoExiste) {
            respostaJogo.setText("O jogo já existe!");
            return;
        }

        if (precoTexto.isEmpty()) {
            respostaJogo.setText("Insira o preço do jogo!");
            return;
        }

        if (categoria == null || categoria.isEmpty()) {
            respostaJogo.setText("Selecione uma categoria!");
            return;
        }

        try {
            // Trata se o usuário digitar vírgula ao invés de ponto no valor
            double preco = Double.parseDouble(precoTexto.replace(",", "."));

            // Instancia o novo objeto Jogo com a imagem selecionada
            Jogo novoJogo = new Jogo(nome, categoria, preco, caminhoImagemSelecionada);
            jogosSalvos.add(novoJogo);

            // Limpa os campos da tela após salvar
            txtNomeJogo.clear();
            txtPrecoJogo.clear();
            menuCategoria.getSelectionModel().clearSelection();
            imgPreview.setImage(null);
            caminhoImagemSelecionada = null;

            respostaJogo.setText("Jogo cadastrado com sucesso!");

        } catch (NumberFormatException e) {
            respostaJogo.setText("Preço inválido! Ex: 59.90");
        }
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        HelloApplication.WindowManager.voltarTela("/com/senai/javafx/opcao.fxml", event);
    }
}
package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.Acessorio;
import com.senai.javafx.entidades.CatalogoAcessorios;
import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.collections.FXCollections;
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
import javafx.util.StringConverter;

import java.io.File;

// Tela do administrador para cadastrar console, PC gamer ou controle na Loja de Acessórios
public class CadastroAcessoriosController {

    @FXML
    private TextField txtNomeAcessorio;
    @FXML
    private TextField txtPrecoAcessorio;
    @FXML
    private TextField txtEstoqueAcessorio;
    @FXML
    private TextField txtDetalheAcessorio;
    @FXML
    private ComboBox<Acessorio.Tipo> cmbTipo;
    @FXML
    private ComboBox<String> cmbPlataforma;
    @FXML
    private ImageView imgPreview;
    @FXML
    private Label respostaAcessorio;

    private String caminhoImagemSelecionada;

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(Acessorio.Tipo.values()));
        cmbTipo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Acessorio.Tipo tipo) {
                return tipo == null ? "" : tipo.getRotulo();
            }

            @Override
            public Acessorio.Tipo fromString(String texto) {
                return null;
            }
        });
        cmbPlataforma.setItems(FXCollections.observableArrayList(CatalogoAcessorios.PLATAFORMAS));

        // Quando o tipo for PC Gamer, a plataforma é sempre "PC": preenche e bloqueia o menu
        cmbTipo.valueProperty().addListener((obs, tipoAnterior, tipoNovo) -> {
            if (tipoNovo == Acessorio.Tipo.COMPUTADOR) {
                cmbPlataforma.setValue("PC");
                cmbPlataforma.setDisable(true);
            } else {
                cmbPlataforma.setDisable(false);
                if (tipoAnterior == Acessorio.Tipo.COMPUTADOR) {
                    cmbPlataforma.setValue(null);
                }
            }
        });
    }

    @FXML
    private void selecionarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Foto do Acessório");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                "Arquivos de Imagem (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File arquivo = fileChooser.showOpenDialog(stage);

        if (arquivo != null) {
            caminhoImagemSelecionada = arquivo.toURI().toString();
            imgPreview.setImage(new Image(caminhoImagemSelecionada));
        }
    }

    @FXML
    private void cadastrarAcessorio() {
        String nome = txtNomeAcessorio.getText().trim();
        String precoTexto = txtPrecoAcessorio.getText().trim();
        String estoqueTexto = txtEstoqueAcessorio.getText().trim();
        String detalhe = txtDetalheAcessorio.getText().trim();
        Acessorio.Tipo tipo = cmbTipo.getValue();
        String plataforma = cmbPlataforma.getValue();

        if (nome.isEmpty()) {
            respostaAcessorio.setText("Insira um nome para o acessório!");
            return;
        }

        boolean existe = CatalogoAcessorios.TODOS.stream()
                .anyMatch(a -> a.getNome().equalsIgnoreCase(nome));
        if (existe) {
            respostaAcessorio.setText("O acessório já existe!");
            return;
        }

        if (tipo == null) {
            respostaAcessorio.setText("Selecione o tipo (console, PC ou controle)!");
            return;
        }
        if (plataforma == null) {
            respostaAcessorio.setText("Selecione a plataforma!");
            return;
        }
        if (precoTexto.isEmpty()) {
            respostaAcessorio.setText("Insira o preço do acessório!");
            return;
        }
        if (estoqueTexto.isEmpty()) {
            respostaAcessorio.setText("Insira o estoque do acessório!");
            return;
        }

        try {
            double preco = Double.parseDouble(precoTexto.replace(",", "."));
            int estoque = Integer.parseInt(estoqueTexto);

            if (preco < 0) {
                respostaAcessorio.setText("O preço não pode ser negativo!");
                return;
            }
            if (estoque < 0) {
                respostaAcessorio.setText("O estoque não pode ser negativo!");
                return;
            }

            CatalogoAcessorios.TODOS.add(new Acessorio(nome, tipo, plataforma, detalhe,
                    preco, caminhoImagemSelecionada, estoque));

            txtNomeAcessorio.clear();
            txtPrecoAcessorio.clear();
            txtEstoqueAcessorio.clear();
            txtDetalheAcessorio.clear();
            cmbTipo.setValue(null);
            cmbPlataforma.setValue(null);
            imgPreview.setImage(null);
            caminhoImagemSelecionada = null;

            respostaAcessorio.setText("Acessório cadastrado com sucesso!");
        } catch (NumberFormatException e) {
            respostaAcessorio.setText("Preço ou estoque inválido! Ex: 599.90 e 5");
        }
    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();
    }
}
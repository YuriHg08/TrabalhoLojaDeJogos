package com.senai.javafx.controllers.LadoAdministrador;

import com.senai.javafx.entidades.GerenciamentoJanela;
import com.senai.javafx.entidades.Jogo;
import com.senai.javafx.entidades.FiltroCatalogo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import com.senai.javafx.entidades.SeletorMultiplo;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

import static com.senai.javafx.controllers.LadoAdministrador.CadastroCategoriasController.categoriasSalvas;

public class CadastroJogosController {

    @FXML
    private TextField txtNomeJogo;

    @FXML
    private TextField txtPrecoJogo;

    @FXML
    private MenuButton menuCategoria;

    @FXML
    private MenuButton menuConsole;

    @FXML
    private ImageView imgPreview;

    @FXML
    private Label respostaJogo;

    @FXML
    private TextField txtEstoqueJogo;

    // Lista de objetos do tipo Jogo
    public static final ObservableList<Jogo> jogosSalvos = FXCollections.observableArrayList();
    static {

                jogosSalvos.addAll(
                        new Jogo("God of War Ragnarök", List.of("Ação", "Aventura"), List.of("Playstation", "PC"), 349.90, imagem("god_of_war.png"), 10),
                        new Jogo("Spider-Man 2", List.of("Ação", "Aventura"), List.of("Playstation", "PC"), 349.90, imagem("spider_man_2.png"), 10),
                        new Jogo("Elden Ring", List.of("RPG", "Ação"), List.of("PC", "Playstation", "Xbox"), 299.90, imagem("elden_ring.png"), 10),
                        new Jogo("Cyberpunk 2077", List.of("RPG", "Ação"), List.of("PC", "Playstation", "Xbox"), 199.90, imagem("cyberpunk_2077.png"), 10),
                        new Jogo("Call of Duty: MWIII", List.of("Ação"), List.of("PC", "Playstation", "Xbox"), 299.90, imagem("call_of_duty_mwiii.png"), 10),
                        new Jogo("Resident Evil Village", List.of("Ação", "Aventura"), List.of("PC", "Playstation", "Xbox"), 149.90, imagem("resident_evil_village.png"), 10),
                        new Jogo("Zelda: Tears of the Kingdom", List.of("Aventura", "Ação"), List.of("Nintendo"), 349.90, imagem("zelda_totk.png"), 10),
                        new Jogo("Grand Theft Auto VI", List.of("Ação", "Aventura"), List.of("Playstation", "Xbox"), 449.90, imagem("GTA_VI.png"), 10),
                        new Jogo("Pokemon Legend Arceus", List.of("RPG", "Aventura"), List.of("Nintendo"), 349.00, imagem("Pokemon_A.png"), 10)
                );
    }

    // Procura a imagem em resources/com/senai/javafx/imagens/jogos/
    // Se o arquivo não existir, devolve null e o catálogo mostra o texto "FOTO DO JOGO"
    private static String imagem(String arquivo) {
        URL url = CadastroJogosController.class.getResource("/com/senai/javafx/imagens/jogos/" + arquivo);
        return url != null ? url.toExternalForm() : null;
    }

    private String caminhoImagemSelecionada;
    // Seletores de várias opções (um jogo pode ter mais de uma categoria e console)
    private SeletorMultiplo seletorCategorias;
    private SeletorMultiplo seletorConsoles;

    @FXML
    public void initialize() {
        seletorCategorias = new SeletorMultiplo(menuCategoria, categoriasSalvas, "Selecione categorias");
        seletorConsoles = new SeletorMultiplo(menuConsole,
                FXCollections.observableArrayList(FiltroCatalogo.CONSOLES), "Selecione plataformas");
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

        // Abre a tela de importar imagem
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
        String estoqueTexto = txtEstoqueJogo.getText().trim();
        java.util.List<String> categorias = seletorCategorias.getSelecionados();
        java.util.List<String> consoles = seletorConsoles.getSelecionados();

        // Verificando os campos obrigatórios
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

        if (estoqueTexto.isEmpty()) {
            respostaJogo.setText("Insira o estoque do jogo!");
            return;
        }

        if (categorias.isEmpty()) {
            respostaJogo.setText("Selecione pelo menos uma categoria!");
            return;
        }

        if (consoles.isEmpty()) {
            respostaJogo.setText("Selecione pelo menos uma plataforma!");
            return;
        }

        try {
            // A conversão fica aqui dentro para um valor inválido mostrar mensagem em vez de travar
            double preco = Double.parseDouble(precoTexto.replace(",", "."));
            int estoque = Integer.parseInt(estoqueTexto);
            if (estoque < 0) {
                respostaJogo.setText("O estoque não pode ser negativo!");
                return;
            }

            // Cria o novo jogo e adiciona na lista atual
            Jogo novoJogo = new Jogo(nome, categorias, consoles, preco, caminhoImagemSelecionada, estoque);
            jogosSalvos.add(novoJogo);

            // Limpa os campos da tela após salvar
            txtNomeJogo.clear();
            txtPrecoJogo.clear();
            txtEstoqueJogo.clear();
            seletorCategorias.limpar();
            seletorConsoles.limpar();
            imgPreview.setImage(null);
            caminhoImagemSelecionada = null;

            respostaJogo.setText("Jogo cadastrado com sucesso!");

        } catch (NumberFormatException e) {
            respostaJogo.setText("Preço ou estoque inválido! Ex: 59.90 e 5");
        }

    }

    @FXML
    public void voltarTela(ActionEvent event) {
        GerenciamentoJanela.voltarTela();

    }
}
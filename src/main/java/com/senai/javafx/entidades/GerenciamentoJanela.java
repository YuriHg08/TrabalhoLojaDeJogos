package com.senai.javafx.entidades;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;

public class GerenciamentoJanela extends Application {

    private static Stage primaryStage;
    private static boolean modoEscuroAtivo = false;

    public enum TipoAlerta {
        INFO("Aviso", "#00f0ff"),
        SUCESSO("Sucesso", "#00ff88"),
        ALERTA("Atenção", "#ffc700"),
        ERRO("Erro", "#e53935");

        final String tituloPadrao;
        final String corDestaque;

        TipoAlerta(String tituloPadrao, String corDestaque) {
            this.tituloPadrao = tituloPadrao;
            this.corDestaque = corDestaque;
        }
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        abrirJanela("/com/senai/javafx/opcaoUser.fxml", "Nexus");
    }


    public static void abrirJanela(String fxmlPath, String titulo) {
        try {
            URL fxmlUrl = GerenciamentoJanela.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Arquivo FXML não encontrado: " + fxmlPath);
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            if (modoEscuroAtivo) {
                root.getStyleClass().add("modo-escuro");
            }

            if (primaryStage == null) {
                primaryStage = new Stage();
            }

            Scene scene = primaryStage.getScene();

            if (scene == null) {
                scene = new Scene(root);

                // Carrega styleMenu.css
                URL menuCssUrl = GerenciamentoJanela.class.getResource("/com/senai/javafx/styleMenu.css");
                if (menuCssUrl != null) {
                    scene.getStylesheets().add(menuCssUrl.toExternalForm());
                } else {
                    System.err.println("Aviso: /com/senai/javafx/styleMenu.css não encontrado.");
                }

                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            primaryStage.setTitle(titulo);
            primaryStage.setResizable(true);

            if (!primaryStage.isShowing()) {
                primaryStage.setMaximized(true);
                primaryStage.show();
            } else {
                Platform.runLater(() -> {
                    if (!primaryStage.isMaximized()) {
                        primaryStage.setMaximized(true);
                    }
                });
            }

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void voltarTela() {
        abrirJanela("/com/senai/javafx/opcao.fxml", "Menu de opções");
    }

    /**
     * Exibe um modal customizado integrado ao tema visual da aplicação.
     */
    public static void exibirAlerta(String titulo, String mensagem, TipoAlerta tipo) {
        Stage modalStage = new Stage();

        modalStage.initModality(Modality.APPLICATION_MODAL);
        if (primaryStage != null) {
            modalStage.initOwner(primaryStage);
        }
        modalStage.initStyle(StageStyle.UNDECORATED);
        modalStage.setResizable(false);

        // --- BARRA SUPERIOR (TÍTULO E FECHAR) ---
        Label lblTitulo = new Label(titulo != null && !titulo.isBlank() ? titulo : tipo.tituloPadrao);
        lblTitulo.setStyle(
                "-fx-text-fill: " + tipo.corDestaque + "; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 14px;"
        );

        Region espacador = new Region();
        HBox.setHgrow(espacador, Priority.ALWAYS);

        Button btnFechar = new Button("✕");
        btnFechar.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-text-fill: -cor-texto-secundario; " +
                        "-fx-font-size: 13px; " +
                        "-fx-cursor: hand;"
        );
        btnFechar.setOnAction(e -> modalStage.close());

        HBox barraSuperior = new HBox(lblTitulo, espacador, btnFechar);
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.setPadding(new Insets(12, 16, 8, 16));

        // --- CORPO DA MENSAGEM ---
        Label lblMensagem = new Label(mensagem);
        lblMensagem.setWrapText(true);
        lblMensagem.setMaxWidth(380);
        lblMensagem.setStyle(
                "-fx-text-fill: -cor-texto-principal; " +
                        "-fx-font-size: 13px;"
        );
        lblMensagem.setAlignment(Pos.CENTER);

        VBox corpo = new VBox(lblMensagem);
        corpo.setPadding(new Insets(16, 20, 20, 20));
        corpo.setAlignment(Pos.CENTER);

        // --- BOTÃO DE CONFIRMAÇÃO ---
        Button btnOk = new Button("OK");
        btnOk.getStyleClass().add("botao-primario");
        btnOk.setPrefWidth(90);
        btnOk.setOnAction(e -> modalStage.close());

        HBox rodape = new HBox(btnOk);
        rodape.setAlignment(Pos.CENTER_RIGHT);
        rodape.setPadding(new Insets(8, 20, 16, 20));

        // --- CONTAINER DO DIÁLOGO ---
        VBox layout = new VBox(barraSuperior, corpo, rodape);
        layout.getStyleClass().add("dialogo-custom");

        // Ajusta classe do tema claro/escuro
        if (!modoEscuroAtivo) {
            layout.getStyleClass().add("modo-claro");
        }

        // Permite arrastar a janela sem bordas
        final double[] arrasto = new double[2];
        barraSuperior.setOnMousePressed(e -> {
            arrasto[0] = modalStage.getX() - e.getScreenX();
            arrasto[1] = modalStage.getY() - e.getScreenY();
        });
        barraSuperior.setOnMouseDragged(e -> {
            modalStage.setX(e.getScreenX() + arrasto[0]);
            modalStage.setY(e.getScreenY() + arrasto[1]);
        });

        Scene scene = new Scene(layout);
        URL cssUrl = GerenciamentoJanela.class.getResource("/com/senai/javafx/styleMenu.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        modalStage.setScene(scene);

        // Garante a centralização correta logo após o modal ser desenhado na tela
        modalStage.setOnShown(e -> {
            modalStage.centerOnScreen();
        });

        modalStage.showAndWait();
    }
}
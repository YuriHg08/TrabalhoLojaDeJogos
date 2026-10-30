package com.senai.javafx.entidades;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GerenciamentoJanela extends Application {

    private static Stage primaryStage;
    private static boolean modoEscuroAtivo = false;

    @Override
    public void start(Stage stage) {
        setPrimaryStage(stage);
        abrirJanela("/com/senai/javafx/telaLogin.fxml", "Fazer Login");
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static boolean isModoEscuroAtivo() {
        return modoEscuroAtivo;
    }

    public static void setModoEscuroAtivo(boolean ativo) {
        modoEscuroAtivo = ativo;
    }

    public static void alternarModoEscuro() {
        modoEscuroAtivo = !modoEscuroAtivo;

        if (primaryStage != null && primaryStage.getScene() != null) {
            Parent root = primaryStage.getScene().getRoot();
            if (modoEscuroAtivo) {
                if (!root.getStyleClass().contains("modo-escuro")) {
                    root.getStyleClass().add("modo-escuro");
                }
            } else {
                root.getStyleClass().remove("modo-escuro");
            }
        }
    }

    public static void abrirJanela(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(GerenciamentoJanela.class.getResource(fxmlPath));
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
                String css = Objects.requireNonNull(GerenciamentoJanela.class.getResource("/com/senai/javafx/style.css")).toExternalForm();
                scene.getStylesheets().add(css);
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
                // Aguarda o nó renderizar para garantir o estado maximizado sem conflitos
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
}
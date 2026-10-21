package com.senai.javafx.entidades;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;


import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {
    @Override
    // Janela inicial (Login com nome e senha)
    public void start(Stage primeiraTela) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/senai/javafx/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 500);

        primeiraTela.setTitle("Cadastre-se na Loja");
        primeiraTela.setScene(scene);
        primeiraTela.show();
        primeiraTela.setResizable(false);


    }

    public static class WindowManager {

        public static void abrirJanela(String fxmlPath, String titulo) {

            try {

                FXMLLoader loader = new FXMLLoader(WindowManager.class.getResource(fxmlPath));
                Parent root = loader.load();


                Scene scene = new Scene(root);


                String css = Objects.requireNonNull(WindowManager.class.getResource("/com/senai/javafx/style.css")).toExternalForm();
                scene.getStylesheets().add(css);


                Stage stage = new Stage();
                stage.setTitle(titulo);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}

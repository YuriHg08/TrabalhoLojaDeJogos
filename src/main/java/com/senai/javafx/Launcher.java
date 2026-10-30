package com.senai.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("opcaoUser.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);

        scene.getStylesheets().add(getClass().getResource("styleMenu.css").toExternalForm());



        stage.setTitle("GAME POINT - Loja de Jogos e Consoles");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.senai.javafx.controllers;

import com.senai.javafx.entidades.GerenciamentoJanela;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class apoieNosController {

    private Stage stage;

    // Abre a janela "Apoie-nos" (modal) por cima da tela atual
    public static void abrir(Window dono) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    apoieNosController.class.getResource("/com/senai/javafx/apoieNos.fxml"));
            Parent root = loader.load();

            apoieNosController controller = loader.getController();

            Stage janela = new Stage();
            janela.initOwner(dono);
            janela.initModality(Modality.APPLICATION_MODAL);
            janela.initStyle(StageStyle.UNDECORATED);
            janela.setResizable(false);

            // Permite mover a janela clicando e arrastando
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
                    apoieNosController.class.getResource("/com/senai/javafx/styleMenu.css").toExternalForm());
            // ESC também fecha a janela
            cena.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    janela.close();
                }
            });
            janela.setScene(cena);

            controller.stage = janela;

            janela.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Throwable causa = e.getCause() != null ? e.getCause() : e;
            GerenciamentoJanela.exibirAlerta("Erro",
                    "Não foi possível abrir a janela: " + causa,
                    GerenciamentoJanela.TipoAlerta.ERRO);
        }
    }

    @FXML
    private void fechar() {
        if (stage != null) {
            stage.close();
        }
    }
}

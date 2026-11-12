package com.senai.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class TitleBarController {

    @FXML private HBox barraTitulo;
    @FXML private Region areaArrasto;
    @FXML private Label lblTituloJanela;

    private double xOffset = 0;
    private double yOffset = 0;

    // Guarda o tamanho e a posição anteriores para quando desmaximizar
    private double xAnterior = 100;
    private double yAnterior = 100;
    private double larguraAnterior = 1200;
    private double alturaAnterior = 700;
    private boolean isMaximizada = true;

    @FXML
    public void initialize() {
        barraTitulo.setOnMousePressed(event -> {
            Stage stage = getStage();
            if (stage != null && !isMaximizada) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        barraTitulo.setOnMouseDragged(event -> {
            Stage stage = getStage();
            if (stage != null && !isMaximizada) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        barraTitulo.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                alternarMaximizacao();
            }
        });
    }

    private Stage getStage() {
        if (barraTitulo.getScene() != null && barraTitulo.getScene().getWindow() != null) {
            return (Stage) barraTitulo.getScene().getWindow();
        }
        return null;
    }

    @FXML
    void minimizar(ActionEvent event) {
        Stage stage = getStage();
        if (stage != null) {
            stage.setIconified(true);
        }
    }

    @FXML
    void maximizar(ActionEvent event) {
        alternarMaximizacao();
    }

    private void alternarMaximizacao() {
        Stage stage = getStage();
        if (stage == null) return;

        if (stage.isFullScreen()) {
            // Sai da tela cheia e volta para a janela menor
            stage.setFullScreen(false);
            stage.setX(xAnterior);
            stage.setY(yAnterior);
            stage.setWidth(larguraAnterior);
            stage.setHeight(alturaAnterior);
            isMaximizada = false;
        } else {
            xAnterior = stage.getX();
            yAnterior = stage.getY();
            larguraAnterior = stage.getWidth();
            alturaAnterior = stage.getHeight();

            stage.setFullScreen(true);
            isMaximizada = true;
        }
    }

    @FXML
    void fechar(ActionEvent event) {
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }

    public void setSubtitulo(String subtitulo) {
        if (lblTituloJanela != null) {
            lblTituloJanela.setText("• " + subtitulo);
        }
    }
}
package com.senai.javafx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField nome;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Hello World");
    }


}






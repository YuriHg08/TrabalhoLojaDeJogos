package com.example.javafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label tenhobrenrot;
    @FXML
    public void onButtonClick() {
        tenhobrenrot.setText("Parabéns");
    }
}



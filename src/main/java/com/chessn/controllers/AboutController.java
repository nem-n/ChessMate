package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AboutController {
    
    @FXML
    private VBox root;
    
    @FXML
    private Button closeButton;
    
    private Stage stage;
    
    @FXML
    public void initialize() {
        // Set up close button
        closeButton.setOnAction(e -> {
            if (stage != null) {
                stage.close();
            }
        });
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
} 
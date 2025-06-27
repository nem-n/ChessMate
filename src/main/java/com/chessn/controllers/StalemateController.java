package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;

public class StalemateController {
    
    @FXML private Text titleText;
    @FXML private Text dialogTitle;
    @FXML private Text victoryConditionText;
    @FXML private Button closeButton;
    @FXML private Button newGameButton;
    @FXML private Button mainMenuButton;
    
    private Stage stage;
    private Runnable onNewGame;
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setOnNewGame(Runnable onNewGame) {
        this.onNewGame = onNewGame;
    }
    
    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleNewGame() {
        if (onNewGame != null) {
            onNewGame.run();
        }
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleMainMenu() {
        Platform.exit();
    }
} 
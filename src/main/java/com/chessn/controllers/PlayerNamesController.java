package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PlayerNamesController {
    
    @FXML
    private Text titleText;
    
    @FXML
    private TextField whiteField;
    
    @FXML
    private TextField blackField;
    
    @FXML
    private Button startButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Button closeButton;
    
    private Stage stage;
    private PlayerNamesControllerCallback callback;
    private boolean isFromGame;
    
    public interface PlayerNamesControllerCallback {
        void onStartGame(String whitePlayerName, String blackPlayerName);
        void onBack();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setCallback(PlayerNamesControllerCallback callback) {
        this.callback = callback;
    }
    
    public void setFromGame(boolean fromGame) {
        this.isFromGame = fromGame;
        if (fromGame) {
            backButton.setText("Cancel");
        } else {
            backButton.setText("Back to Menu");
        }
    }
    
    public void setDefaultNames(String whiteName, String blackName) {
        whiteField.setText(whiteName);
        blackField.setText(blackName);
    }
    
    @FXML
    private void handleStartGame() {
        String whiteName = whiteField.getText().trim().isEmpty() ? "Player 1" : whiteField.getText().trim();
        String blackName = blackField.getText().trim().isEmpty() ? "Player 2" : blackField.getText().trim();
        
        if (callback != null) {
            callback.onStartGame(whiteName, blackName);
        }
        
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleBack() {
        if (callback != null) {
            callback.onBack();
        }
        
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleClose() {
        if (stage != null) {
            stage.close();
        }
    }
} 
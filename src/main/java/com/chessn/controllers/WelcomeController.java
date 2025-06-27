package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;

public class WelcomeController {
    
    @FXML
    private Text titleText;
    
    @FXML
    private Text welcomeText;
    
    @FXML
    private Button startNewGameButton;
    
    @FXML
    private Button startAIButton;
    
    @FXML
    private Button howToPlayButton;
    
    @FXML
    private Button aboutButton;
    
    @FXML
    private Button exitButton;
    
    @FXML
    private Button minimizeButton;
    
    @FXML
    private Button resizeButton;
    
    @FXML
    private Button closeButton;
    
    private Stage stage;
    private WelcomeControllerCallback callback;
    
    public interface WelcomeControllerCallback {
        void onStartNewGame();
        void onStartAI();
        void onHowToPlay();
        void onAbout();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setCallback(WelcomeControllerCallback callback) {
        this.callback = callback;
    }
    
    @FXML
    private void handleStartNewGame() {
        if (callback != null) {
            callback.onStartNewGame();
        }
    }
    
    @FXML
    private void handleStartAI() {
        if (callback != null) {
            callback.onStartAI();
        }
    }
    
    @FXML
    private void handleHowToPlay() {
        if (callback != null) {
            callback.onHowToPlay();
        }
    }
    
    @FXML
    private void handleAbout() {
        if (callback != null) {
            callback.onAbout();
        }
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    @FXML
    private void handleMinimize() {
        if (stage != null) {
            stage.setIconified(true);
        }
    }
    
    @FXML
    private void handleResize() {
        if (stage != null) {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                // Get the screen bounds excluding the taskbar
                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
                stage.setMaximized(true);
            }
        }
    }
    
    @FXML
    private void handleClose() {
        Platform.exit();
    }
} 
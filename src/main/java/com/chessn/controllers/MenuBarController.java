package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.application.Platform;

public class MenuBarController {
    
    @FXML private MenuBar menuBar;
    @FXML private Menu gameMenu;
    @FXML private Menu helpMenu;
    @FXML private MenuItem newGameItem;
    @FXML private MenuItem resetItem;
    @FXML private MenuItem backToWelcomeItem;
    @FXML private MenuItem exitItem;
    @FXML private MenuItem howToPlayItem;
    @FXML private MenuItem aboutItem;
    @FXML private Menu toolsMenu;
    @FXML private MenuItem undoItem;
    @FXML private MenuItem redoItem;
    
    private MenuBarCallback callback;
    
    public interface MenuBarCallback {
        void onNewGame();
        void onReset();
        void onBackToWelcome();
        void onHowToPlay();
        void onAbout();
        void onUndo();
        void onRedo();
    }
    
    public void setCallback(MenuBarCallback callback) {
        this.callback = callback;
    }
    
    @FXML
    private void handleNewGame() {
        if (callback != null) {
            callback.onNewGame();
        }
    }
    
    @FXML
    private void handleReset() {
        if (callback != null) {
            callback.onReset();
        }
    }
    
    @FXML
    private void handleBackToWelcome() {
        if (callback != null) {
            callback.onBackToWelcome();
        }
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
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
    private void handleUndo() {
        if (callback != null) {
            callback.onUndo();
        }
    }
    
    @FXML
    private void handleRedo() {
        if (callback != null) {
            callback.onRedo();
        }
    }
} 
package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class GameModeDialogController {
    private Stage stage;
    private GameModeDialogCallback callback;
    private double dragOffsetX, dragOffsetY;

    @FXML private HBox titleBar;

    public interface GameModeDialogCallback {
        void onAI();
        void onPassNPlay();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        //draggable by title bar
        if (titleBar != null) {
            titleBar.setOnMousePressed(this::handleDragPressed);
            titleBar.setOnMouseDragged(this::handleDragDragged);
        }
    }

    public void setCallback(GameModeDialogCallback callback) {
        this.callback = callback;
    }

    @FXML
    private void handleAI() {
        if (callback != null) callback.onAI();
        if (stage != null) stage.close();
    }

    @FXML
    private void handlePassNPlay() {
        if (callback != null) callback.onPassNPlay();
        if (stage != null) stage.close();
    }

    @FXML
    private void handleClose() {
        if (stage != null) stage.close();
    }

    private void handleDragPressed(MouseEvent e) {
        dragOffsetX = stage.getX() - e.getScreenX();
        dragOffsetY = stage.getY() - e.getScreenY();
    }

    private void handleDragDragged(MouseEvent e) {
        stage.setX(e.getScreenX() + dragOffsetX);
        stage.setY(e.getScreenY() + dragOffsetY);
    }
} 
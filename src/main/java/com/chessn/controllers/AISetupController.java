package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class AISetupController {

    public enum Difficulty {
        EASY(0, 1, 4),      // Skill 0, Depth 1, MultiPV 4
        MEDIUM(0, 1, 3),    // Skill 0, Depth 1, MultiPV 3
        HARD(1, 2, 2);      // Skill 1, Depth 2, MultiPV 2

        public final int level;
        public final int depth;
        public final int multiPV;

        Difficulty(int level, int depth, int multiPV) {
            this.level = level;
            this.depth = depth;
            this.multiPV = multiPV;
        }
    }

    public enum PlayerColor {
        WHITE, BLACK, RANDOM
    }

    @FXML
    private ToggleGroup difficultyGroup;
    @FXML
    private ToggleGroup colorGroup;

    private Stage stage;
    private AISetupCallback callback;

    public interface AISetupCallback {
        void onStart(Difficulty difficulty, PlayerColor playerColor);
        void onBack();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCallback(AISetupCallback callback) {
        this.callback = callback;
    }

    @FXML
    private void handleStart() {
        if (callback != null) {
            RadioButton selectedDifficulty = (RadioButton) difficultyGroup.getSelectedToggle();
            RadioButton selectedColor = (RadioButton) colorGroup.getSelectedToggle();
            
            Difficulty difficulty;
            switch (selectedDifficulty.getText()) {
                case "Easy":
                    difficulty = Difficulty.EASY;
                    break;
                case "Medium":
                    difficulty = Difficulty.MEDIUM;
                    break;
                case "Hard":
                default:
                    difficulty = Difficulty.HARD;
                    break;
            }

            PlayerColor playerColor;
            switch (selectedColor.getText()) {
                case "White":
                    playerColor = PlayerColor.WHITE;
                    break;
                case "Black":
                    playerColor = PlayerColor.BLACK;
                    break;
                case "Random":
                default:
                    playerColor = PlayerColor.RANDOM;
                    break;
            }
            
            callback.onStart(difficulty, playerColor);
            stage.close();
        }
    }

    @FXML
    private void handleBack() {
        if (callback != null) {
            callback.onBack();
        }
        stage.close();
    }
} 
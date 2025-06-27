package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.util.List;
import com.chessn.Piece;

public class PlayerInfoPanelBlackController {
    
    @FXML
    private Text blackPlayerName;
    
    @FXML
    private Text blackStatus;
    
    @FXML
    private HBox blackPlayerRow;
    
    @FXML
    private HBox blackCapturedPiecesBox;
    
    public void setPlayerName(String blackName) {
        blackPlayerName.setText(blackName + " (Black)");
    }
    
    public void updateStatus(boolean isActive, boolean inCheck, boolean inCheckmate, boolean inStalemate, boolean opponentInCheckmate) {
        blackPlayerRow.getStyleClass().remove("active");
        
        blackStatus.setText("");
        blackStatus.getStyleClass().remove("check");
        blackStatus.getStyleClass().remove("checkmate");
        
        if (inStalemate) {
            blackStatus.setText("Stalemate");
            return;
        }
        
        if (inCheckmate) {
            blackStatus.setText("CHECKMATE!");
            blackStatus.getStyleClass().add("checkmate");
            return;
        }
        
        if (inCheck && isActive) { 
            blackStatus.setText("CHECK!");
            blackStatus.getStyleClass().add("check");
        }
        
        if (isActive) {
            blackPlayerRow.getStyleClass().add("active");
        }
    }
    
    public void updateStatusThinking(boolean isThinking) {
        if (isThinking) {
            blackStatus.setText("Thinking...");
            blackStatus.getStyleClass().remove("check");
            blackStatus.getStyleClass().remove("checkmate");
        } else {
        }
    }
    
    public void updateCapturedPieces(List<Piece> captured) {
        blackCapturedPiecesBox.getChildren().clear();
        if (captured == null || captured.isEmpty()) return;
        captured.sort((a, b) -> {
            int cmp = Integer.compare(b.getValue(), a.getValue());
            if (cmp == 0) return Character.compare(a.getSymbol(), b.getSymbol());
            return cmp;
        });
        char lastSymbol = 0;
        int lastValue = -1;
        int count = 0;
        for (int i = 0; i <= captured.size(); i++) {
            char symbol = (i < captured.size()) ? captured.get(i).getSymbol() : 0;
            int value = (i < captured.size()) ? captured.get(i).getValue() : -1;
            if (i == 0) {
                lastSymbol = symbol;
                lastValue = value;
                count = 1;
            } else if (i < captured.size() && symbol == lastSymbol && value == lastValue) {
                count++;
            } else {
                Text t = new Text(String.valueOf(lastSymbol));
                t.getStyleClass().add("captured-piece-symbol");
                blackCapturedPiecesBox.getChildren().add(t);
                if (count > 1) {
                    Text countText = new Text("x" + count);
                    countText.getStyleClass().add("captured-piece-count");
                    blackCapturedPiecesBox.getChildren().add(countText);
                }
                if (i < captured.size()) {
                    lastSymbol = symbol;
                    lastValue = value;
                    count = 1;
                }
            }
        }
    }
} 
package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.Node;
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
        // Remove active class
        blackPlayerRow.getStyleClass().remove("active");
        
        // Clear status text
        blackStatus.setText("");
        blackStatus.getStyleClass().remove("check");
        blackStatus.getStyleClass().remove("checkmate");
        
        if (inStalemate) {
            blackStatus.setText("Stalemate");
            return;
        }
        
        // If this player's king is in checkmate, display CHECKMATE!
        if (inCheckmate) {
            blackStatus.setText("CHECKMATE!");
            blackStatus.getStyleClass().add("checkmate");
            return;
        }
        
        if (inCheck && isActive) { // Only show check if it's their turn
            blackStatus.setText("CHECK!");
            blackStatus.getStyleClass().add("check");
        }
        
        // Highlight if it's black's turn
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
            // This will be cleared by the next call to updateStatus
        }
    }
    
    public void updateCapturedPieces(List<Piece> captured) {
        blackCapturedPiecesBox.getChildren().clear();
        if (captured == null || captured.isEmpty()) return;
        // Sort by value descending, then by symbol
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
                // Display the symbol
                Text t = new Text(String.valueOf(lastSymbol));
                t.getStyleClass().add("captured-piece-symbol");
                blackCapturedPiecesBox.getChildren().add(t);
                // If more than one, display the count
                if (count > 1) {
                    Text countText = new Text("x" + count);
                    countText.getStyleClass().add("captured-piece-count");
                    blackCapturedPiecesBox.getChildren().add(countText);
                }
                // Reset for next group
                if (i < captured.size()) {
                    lastSymbol = symbol;
                    lastValue = value;
                    count = 1;
                }
            }
        }
    }
} 
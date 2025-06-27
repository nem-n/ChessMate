package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.Node;
import java.util.List;
import com.chessn.Piece;

public class PlayerInfoPanelWhiteController {
    
    @FXML
    private Text whitePlayerName;
    
    @FXML
    private Text whiteStatus;
    
    @FXML
    private HBox whitePlayerRow;
    
    @FXML
    private HBox whiteCapturedPiecesBox;
    
    public void setPlayerName(String whiteName) {
        whitePlayerName.setText(whiteName + " (White)");
    }
    
    public void updateStatus(boolean isActive, boolean inCheck, boolean inCheckmate, boolean inStalemate, boolean opponentInCheckmate) {
        // Remove active class
        whitePlayerRow.getStyleClass().remove("active");
        
        // Clear status text
        whiteStatus.setText("");
        whiteStatus.getStyleClass().remove("check");
        whiteStatus.getStyleClass().remove("checkmate");
        
        if (inStalemate) {
            whiteStatus.setText("Stalemate");
            return;
        }
        
        // If this player's king is in checkmate, display CHECKMATE!
        if (inCheckmate) {
            whiteStatus.setText("CHECKMATE!");
            whiteStatus.getStyleClass().add("checkmate");
            return;
        }
        
        if (inCheck && isActive) { // Only show check if it's their turn
            whiteStatus.setText("CHECK!");
            whiteStatus.getStyleClass().add("check");
        }
        
        // Highlight if it's white's turn
        if (isActive) {
            whitePlayerRow.getStyleClass().add("active");
        }
    }
    
    public void updateStatusThinking(boolean isThinking) {
        if (isThinking) {
            whiteStatus.setText("Thinking...");
            whiteStatus.getStyleClass().remove("check");
            whiteStatus.getStyleClass().remove("checkmate");
        } else {
            // This will be cleared by the next call to updateStatus
        }
    }
    
    public void updateCapturedPieces(List<Piece> captured) {
        whiteCapturedPiecesBox.getChildren().clear();
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
                whiteCapturedPiecesBox.getChildren().add(t);
                // If more than one, display the count
                if (count > 1) {
                    Text countText = new Text("x" + count);
                    countText.getStyleClass().add("captured-piece-count");
                    whiteCapturedPiecesBox.getChildren().add(countText);
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
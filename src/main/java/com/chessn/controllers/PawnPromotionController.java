package com.chessn.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class PawnPromotionController {
    
    @FXML
    private Text titleText;
    
    @FXML
    private Text promotionText;
    
    @FXML
    private Text queenSymbol;
    
    @FXML
    private Text rookSymbol;
    
    @FXML
    private Text bishopSymbol;
    
    @FXML
    private Text knightSymbol;
    
    private Stage stage;
    private PawnPromotionCallback callback;
    private int promotionRow;
    private int promotionCol;
    
    public interface PawnPromotionCallback {
        void onPromotion(int row, int col, String pieceType);
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void setCallback(PawnPromotionCallback callback) {
        this.callback = callback;
    }
    
    public void setPromotionPosition(int row, int col) {
        this.promotionRow = row;
        this.promotionCol = col;
    }
    
    public void setPieceColor(boolean isWhite) {
        // Update piece symbols based on color
        queenSymbol.setText(isWhite ? "♛" : "♕");
        rookSymbol.setText(isWhite ? "♜" : "♖");
        bishopSymbol.setText(isWhite ? "♝" : "♗");
        knightSymbol.setText(isWhite ? "♞" : "♘");
        
        // Set color based on piece color
        Color pieceColor = Color.web("391e10"); // Same color for both white and black pieces
        queenSymbol.setFill(pieceColor);
        bishopSymbol.setFill(pieceColor);
        rookSymbol.setFill(pieceColor);
        knightSymbol.setFill(pieceColor);
    }
    
    @FXML
    private void handleQueenPromotion() {
        if (callback != null) {
            callback.onPromotion(promotionRow, promotionCol, "Queen");
        }
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleRookPromotion() {
        if (callback != null) {
            callback.onPromotion(promotionRow, promotionCol, "Rook");
        }
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleBishopPromotion() {
        if (callback != null) {
            callback.onPromotion(promotionRow, promotionCol, "Bishop");
        }
        if (stage != null) {
            stage.close();
        }
    }
    
    @FXML
    private void handleKnightPromotion() {
        if (callback != null) {
            callback.onPromotion(promotionRow, promotionCol, "Knight");
        }
        if (stage != null) {
            stage.close();
        }
    }
} 
package com.chessn;

public abstract class Piece {
    // Properties that all pieces will have
    protected int currentRow;        // current row position (0-7)
    protected int currentCol;        // current column position (0-7)
    protected boolean isWhite; // true for white pieces, false for black
    
    // Constructor
    public Piece(boolean isWhite, int currentRow, int currentCol) {
        this.isWhite = isWhite;
        this.currentRow = currentRow;
        this.currentCol = currentCol;
    }
    
    // Getters and setters
    public int getRow() {
        return currentRow;
    }
    
    public int getCol() {
        return currentCol;
    }
    
    public boolean isWhite() {
        return isWhite;
    }
    
    public void setPosition(int currentRow, int currentCol) {
        this.currentRow = currentRow;
        this.currentCol = currentCol;
    }
    
    // Check if this piece can capture another piece
    public boolean canCapture(Piece targetPiece) {
        // Can't capture pieces of the same color
        if (targetPiece == null || this.isWhite == targetPiece.isWhite) {
            return false;
        }
        
        // By default, pieces can capture using their normal movement rules
        return isValidMove(targetPiece.getRow(), targetPiece.getCol());
    }
    
    // Method to get piece symbol (will be different for each piece type)
    public abstract char getSymbol();
    
    // Method to check if a move is valid (will be different for each piece type)
    public abstract boolean isValidMove(int toRow, int toCol);
    
    public abstract int getValue();
} 
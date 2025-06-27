package com.chessn;

public class Pawn extends Piece {
    private boolean hasMoved = false;
    
    public Pawn(boolean isWhite, int currentRow, int currentCol) {
        super(isWhite, currentRow, currentCol);
    }
    
    @Override
    public char getSymbol() {
        return isWhite ? '♙' : '♟';
    }
    
    @Override
    public boolean isValidMove(int toRow, int toCol) {
        // Pawns move forward (direction depends on color)
        int direction = isWhite ? -1 : 1;
        
        // Check if moving in correct direction
        if ((toRow - currentRow) * direction <= 0) {
            return false;
        }
        
        // stay in same column for normal moves
        if (toCol != currentCol) {
            return false;
        }
        
        // can move 2 squares on first move
        int maxSquares = hasMoved ? 1 : 2;
        return Math.abs(toRow - currentRow) <= maxSquares;
    }
    
    @Override
    public boolean canCapture(Piece targetPiece) {
        if (targetPiece == null || this.isWhite == targetPiece.isWhite) {
            return false;
        }
        
        int direction = isWhite ? -1 : 1;
        int targetRow = targetPiece.getRow();
        int targetCol = targetPiece.getCol();
        
        // only capture diagonally
        return targetRow == currentRow + direction && 
               Math.abs(targetCol - currentCol) == 1;
    }
    
    public void moved() {
        hasMoved = true;
    }
    
    @Override
    public int getValue() {
        return 1;
    }
} 
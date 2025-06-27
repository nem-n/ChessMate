package com.chessn;

public class Rook extends Piece {
    private boolean hasMoved;
    
    public Rook(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
        this.hasMoved = false;
    }
    
    @Override
    public char getSymbol() {
        return isWhite ? '♖' : '♜';
    }
    
    @Override
    public boolean isValidMove(int toRow, int toCol) {
        // Rook can only move in straight lines
        // Either the row must stay the same (horizontal move)
        // Or the column must stay the same (vertical move)
        return (currentRow == toRow && currentCol != toCol) ||  // horizontal move
               (currentCol == toCol && currentRow != toRow);    // vertical move
        
      
    }
    
    public void moved() {
        this.hasMoved = true;
    }
    
    public boolean getHasMoved() {
        return hasMoved;
    }
    
    @Override
    public int getValue() {
        return 5;
    }
} 
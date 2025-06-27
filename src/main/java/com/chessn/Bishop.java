package com.chessn;

public class Bishop extends Piece {
    
    public Bishop(boolean isWhite, int currentRow, int currentCol) {
        super(isWhite, currentRow, currentCol);
    }
    
    @Override
    public char getSymbol() {
        return isWhite ? '♗' : '♝';
    }
    
    @Override
    public boolean isValidMove(int toRow, int toCol) {
        // Calculate differences
        int rowDiff = Math.abs(toRow - currentRow);
        int colDiff = Math.abs(toCol - currentCol);
        
        // Bishop moves diagonally, so row and column differences should be equal
        return rowDiff == colDiff && rowDiff > 0;
        
       
    }
    
    @Override
    public int getValue() {
        return 3;
    }
} 
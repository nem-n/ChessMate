package com.chessn;

public class Knight extends Piece {
    
    public Knight(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }
    
    @Override
    public char getSymbol() {
        return isWhite ? '♘' : '♞';
    }
    
    @Override
    public boolean isValidMove(int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - currentRow);
        int colDiff = Math.abs(toCol - currentCol);
        
        // Knight moves in L-shape: 2 squares in one direction and 1 in the other
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
    
    @Override
    public int getValue() {
        return 3;
    }
} 
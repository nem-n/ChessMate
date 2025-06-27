package com.chessn;

public class Queen extends Piece {
    
    public Queen(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
    }
    
    @Override
    public char getSymbol() {
        return isWhite ? '♕' : '♛';
    }
    
    @Override
    public boolean isValidMove(int toRow, int toCol) {
        // Queen can move like a rook (horizontally/vertically) or bishop (diagonally)
        int rowDiff = Math.abs(toRow - currentRow);
        int colDiff = Math.abs(toCol - currentCol);
        
        // Horizontal or vertical movement (like a rook)
        boolean rookLikeMove = (toRow == currentRow && toCol != currentCol) || (toRow != currentRow && toCol == currentCol);
        
        // Diagonal movement (like a bishop)
        boolean bishopLikeMove = rowDiff == colDiff && rowDiff > 0;
        
        return rookLikeMove || bishopLikeMove;
    }
    
    @Override
    public int getValue() {
        return 9;
    }
} 
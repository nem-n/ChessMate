package com.chessn;

public class King extends Piece {
    private boolean hasMoved;
    
    public King(boolean isWhite, int row, int col) {
        super(isWhite, row, col);
        this.hasMoved = false;
    }
    
    @Override
    public char getSymbol() {
        return isWhite ? '♔' : '♚';
    }
    
    @Override
    public boolean isValidMove(int toRow, int toCol) {
        int currentRow = getRow();
        int currentCol = getCol();
        
        // Calculate the absolute difference in rows and columns
        int rowDiff = Math.abs(toRow - currentRow);
        int colDiff = Math.abs(toCol - currentCol);
        
        // Normal king move: can move one square in any direction
        boolean normalMove = rowDiff <= 1 && colDiff <= 1 && (rowDiff > 0 || colDiff > 0);
        
        // Castling move: two squares horizontally from starting position
        boolean castlingMove = !hasMoved && rowDiff == 0 && Math.abs(toCol - currentCol) == 2;
        
        return normalMove || castlingMove;
    }
    
    public void moved() {
        this.hasMoved = true;
    }
    
    public boolean getHasMoved() {
        return hasMoved;
    }
    
    @Override
    public int getValue() {
        return 0;
    }
} 
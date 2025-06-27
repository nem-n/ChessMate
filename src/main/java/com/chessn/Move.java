package com.chessn;

public class Move {
    public final int fromRow, fromCol, toRow, toCol;
    public final Piece movedPiece;
    public final Piece capturedPiece;
    public final String promotionType; // e.g., "Queen", "Rook", etc. Null if not a promotion.
    public final String fenBefore;
    public final String fenAfter;

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece movedPiece, Piece capturedPiece, String promotionType, String fenBefore, String fenAfter) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.movedPiece = movedPiece;
        this.capturedPiece = capturedPiece;
        this.promotionType = promotionType;
        this.fenBefore = fenBefore;
        this.fenAfter = fenAfter;
    }
} 
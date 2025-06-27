package com.chessn;

public class ChessGame {
    private Piece[][] board;
    private boolean isWhiteTurn = true;
    private int selectedPieceRow = -1; 
    private int selectedPieceCol = -1; 
    private java.util.List<Piece> whiteCaptured = new java.util.ArrayList<>();
    private java.util.List<Piece> blackCaptured = new java.util.ArrayList<>();
    private java.util.List<Move> moveHistory = new java.util.ArrayList<>();
    private int movePointer = 0; // Points to the next move to be made (for undo/redo)
    private int halfmoveClock = 0;
    private int fullmoveNumber = 1;
    private String enPassantTarget = "-";

    public ChessGame() {
        board = new Piece[8][8];
        setupBoard();
        moveHistory = new java.util.ArrayList<>();
        movePointer = 0;
        halfmoveClock = 0;
        fullmoveNumber = 1;
        enPassantTarget = "-";
    }

    public void resetGame() {
        setupBoard();
        selectedPieceRow = -1;
        selectedPieceCol = -1;
        isWhiteTurn = true;
        whiteCaptured.clear();
        blackCaptured.clear();
        moveHistory.clear();
        movePointer = 0;
        halfmoveClock = 0;
        fullmoveNumber = 1;
        enPassantTarget = "-";
    }

    private void setupBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }

        board[0][0] = new Rook(false, 0, 0);      // Black Rook
        board[0][1] = new Knight(false, 0, 1);    // Black Knight
        board[0][2] = new Bishop(false, 0, 2);    // Black Bishop
        board[0][3] = new Queen(false, 0, 3);     // Black Queen
        board[0][4] = new King(false, 0, 4);      // Black King
        board[0][5] = new Bishop(false, 0, 5);    // Black Bishop
        board[0][6] = new Knight(false, 0, 6);    // Black Knight
        board[0][7] = new Rook(false, 0, 7);      // Black Rook

        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(false, 1, col);
        }

        board[7][0] = new Rook(true, 7, 0);      // White Rook
        board[7][1] = new Knight(true, 7, 1);    // White Knight
        board[7][2] = new Bishop(true, 7, 2);    // White Bishop
        board[7][3] = new Queen(true, 7, 3);     // White Queen
        board[7][4] = new King(true, 7, 4);      // White King
        board[7][5] = new Bishop(true, 7, 5);    // White Bishop
        board[7][6] = new Knight(true, 7, 6);    // White Knight
        board[7][7] = new Rook(true, 7, 7);      // White Rook

        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(true, 6, col);
        }
    }


    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece movingPiece = board[fromRow][fromCol];
        Piece targetPiece = board[toRow][toCol];

        if (movingPiece == null) {
            return false; 
        }

        if (movingPiece.isWhite() != isWhiteTurn) {
            return false; }

        // Save FEN before move
        String fenBefore = toFEN();
        String promotionType = null;
        boolean isPromotion = false;

        // Check for castling
        if (movingPiece instanceof King && Math.abs(toCol - fromCol) == 2) {
            if (!isValidCastling(fromRow, fromCol, toRow, toCol)) {
                return false;
            }
            executeCastling(fromRow, fromCol, toRow, toCol);
            isWhiteTurn = !isWhiteTurn;
            // Save FEN after move
            String fenAfter = toFEN();
            moveHistory.add(new Move(fromRow, fromCol, toRow, toCol, movingPiece, null, "Castling", fenBefore, fenAfter));
            return true;
        }

        if (targetPiece != null) {
            if (!movingPiece.canCapture(targetPiece)) {
                return false;  
            }
            if (movingPiece.isWhite()) {
                blackCaptured.add(targetPiece);
            } else {
                whiteCaptured.add(targetPiece);
            }
        } else {
            if (!movingPiece.isValidMove(toRow, toCol)) {
                return false; 
            }
        }

        if (!(movingPiece instanceof Knight)) {
            if (!isPathClear(fromRow, fromCol, toRow, toCol)) {
                return false;
            }
        }

        if (wouldBeInCheck(fromRow, fromCol, toRow, toCol)) {
            return false;  
        }

        if (movingPiece instanceof Pawn && (toRow == 0 || toRow == 7)) {
            isPromotion = true;
        }

        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
        movingPiece.setPosition(toRow, toCol);

        if (movingPiece instanceof Pawn) {
            ((Pawn) movingPiece).moved();
        } else if (movingPiece instanceof King) {
            ((King) movingPiece).moved();
        } else if (movingPiece instanceof Rook) {
            ((Rook) movingPiece).moved();
        }

        // Save FEN after move
        if (movingPiece instanceof Pawn || targetPiece != null) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }
        if (!isWhiteTurn) {
            fullmoveNumber++;
        }
        // En passant target square 
        enPassantTarget = "-";
        if (movingPiece instanceof Pawn && Math.abs(toRow - fromRow) == 2) {
            int epRow = (fromRow + toRow) / 2;
            enPassantTarget = toAlgebraic(epRow, fromCol);
        }
        String fenAfter = toFEN();
        moveHistory.add(new Move(fromRow, fromCol, toRow, toCol, movingPiece, targetPiece, isPromotion ? "Pending" : null, fenBefore, fenAfter));
        movePointer++;

        // Switch turns
        isWhiteTurn = !isWhiteTurn;
        
        return true;
    }

    /**
     * Check if the path between two positions is clear of pieces.
     * Used for all pieces except Knights.
     */
    public boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDirection = Integer.compare(toRow - fromRow, 0);  // -1 for up, 1 for down, 0 for same row
        int colDirection = Integer.compare(toCol - fromCol, 0);  // -1 for left, 1 for right, 0 for same column

        int currentRow = fromRow + rowDirection;
        int currentCol = fromCol + colDirection;

        while (currentRow != toRow || currentCol != toCol) {
            if (board[currentRow][currentCol] != null) {
                return false; 
            }
            currentRow += rowDirection;
            currentCol += colDirection;
        }

        return true;
    }

    /**
     * Find the position of a king of the specified color
     */
    private int[] findKing(boolean isWhiteKing) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece instanceof King && piece.isWhite() == isWhiteKing) {
                    return new int[]{row, col};
                }
            }
        }
        return null; 
    }

   
    public boolean isInCheck(boolean isWhiteKing) {
        int[] kingPos = findKing(isWhiteKing);
        if (kingPos == null) return false;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.isWhite() != isWhiteKing) {
                    if (piece.canCapture(board[kingPos[0]][kingPos[1]]) &&
                        (piece instanceof Knight || isPathClear(row, col, kingPos[0], kingPos[1]))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

 
    public boolean wouldBeInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        Piece movingPiece = board[fromRow][fromCol];
        Piece capturedPiece = board[toRow][toCol];

        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
        movingPiece.setPosition(toRow, toCol);  

        boolean inCheck = isInCheck(movingPiece.isWhite());

        board[fromRow][fromCol] = movingPiece;
        board[toRow][toCol] = capturedPiece;
        movingPiece.setPosition(fromRow, fromCol);  
        return inCheck;
    }

 
    public boolean hasLegalMoves(boolean isWhitePlayer) {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece piece = board[fromRow][fromCol];
                if (piece == null || piece.isWhite() != isWhitePlayer) {
                    continue;
                }

                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (fromRow == toRow && fromCol == toCol) {
                            continue;
                        }

                        Piece targetPiece = board[toRow][toCol];
                        boolean isValidMove = false;

                        if (targetPiece == null) {
                            isValidMove = piece.isValidMove(toRow, toCol) &&
                                        (piece instanceof Knight || isPathClear(fromRow, fromCol, toRow, toCol));
                        } else if (targetPiece.isWhite() != isWhitePlayer) {
                            isValidMove = piece.canCapture(targetPiece) &&
                                        (piece instanceof Knight || isPathClear(fromRow, fromCol, toRow, toCol));
                        }

                        if (isValidMove && !wouldBeInCheck(fromRow, fromCol, toRow, toCol)) {
                            return true;  
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isInCheckmate(boolean isWhiteKing) {
        if (!isInCheck(isWhiteKing)) {
            return false;
        }

        return !hasLegalMoves(isWhiteKing);
    }

    public boolean isStalemate(boolean isWhitePlayer) {
        return !isInCheck(isWhitePlayer) && !hasLegalMoves(isWhitePlayer);
    }

    public boolean isValidCastling(int fromRow, int fromCol, int toRow, int toCol) {
        Piece king = board[fromRow][fromCol];
        if (!(king instanceof King) || ((King)king).getHasMoved()) {
            return false;
        }

        if (fromRow != toRow || Math.abs(toCol - fromCol) != 2) {
            return false;
        }

        boolean isKingside = toCol > fromCol;
        int rookCol = isKingside ? 7 : 0;

        Piece rook = board[fromRow][rookCol];
        if (!(rook instanceof Rook) || ((Rook)rook).getHasMoved()) {
            return false;
        }

        int step = isKingside ? 1 : -1;
        for (int col = fromCol + step; isKingside ? col < rookCol : col > rookCol; col += step) {
            if (board[fromRow][col] != null) {
                return false;
            }
        }

        if (isInCheck(king.isWhite())) {
            return false;
        }

        int midCol = fromCol + step;
        board[fromRow][midCol] = king;
        board[fromRow][fromCol] = null;
        boolean passesThroughCheck = isInCheck(king.isWhite());
        board[fromRow][fromCol] = king;
        board[fromRow][midCol] = null;

        if (passesThroughCheck) {
            return false;
        }

        return true;
    }

     // castling
    private void executeCastling(int fromRow, int fromCol, int toRow, int toCol) {
        boolean isKingside = toCol > fromCol;
        int rookFromCol = isKingside ? 7 : 0;
        int rookToCol = isKingside ? toCol - 1 : toCol + 1;

        Piece king = board[fromRow][fromCol];
        board[toRow][toCol] = king;
        board[fromRow][fromCol] = null;
        king.setPosition(toRow, toCol);
        ((King)king).moved();

        Piece rook = board[fromRow][rookFromCol];
        board[fromRow][rookToCol] = rook;
        board[fromRow][rookFromCol] = null;
        rook.setPosition(fromRow, rookToCol);
        ((Rook)rook).moved();
    }

    /**
     *board state to console (for debugging)
     */
    public void printBoard() {
        System.out.println("  a b c d e f g h");
        System.out.println("  ---------------");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "|"); 
            for (int col = 0; col < 8; col++) {
                char symbol = (board[row][col] != null) ? board[row][col].getSymbol() : '-';
                System.out.print(symbol + " ");
            }
            System.out.println("|" + (8 - row)); 
        }
        System.out.println("  ---------------");
        System.out.println("  a b c d e f g h"); 
    }
    
    // --- Getters for the GUI to access game state ---
    public Piece getPieceAt(int row, int col) {
        return board[row][col];
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public int getSelectedPieceRow() {
        return selectedPieceRow;
    }

    public int getSelectedPieceCol() {
        return selectedPieceCol;
    }

    public void setSelectedPiece(int row, int col) {
        this.selectedPieceRow = row;
        this.selectedPieceCol = col;
    }

    public void clearSelectedPiece() {
        this.selectedPieceRow = -1;
        this.selectedPieceCol = -1;
    }

    //  promotePawn 
    public void promotePawn(int row, int col, String pieceType) {
        boolean isWhite = board[row][col].isWhite();
        switch (pieceType) {
            case "Queen":
                board[row][col] = new Queen(isWhite, row, col);
                break;
            case "Rook":
                board[row][col] = new Rook(isWhite, row, col);
                break;
            case "Bishop":
                board[row][col] = new Bishop(isWhite, row, col);
                break;
            case "Knight":
                board[row][col] = new Knight(isWhite, row, col);
                break;
        }

        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.get(movePointer - 1);
            if (lastMove.promotionType != null && lastMove.promotionType.equals("Pending")) {
                Move promotedMove = new Move(
                    lastMove.fromRow, lastMove.fromCol, lastMove.toRow, lastMove.toCol,
                    lastMove.movedPiece, lastMove.capturedPiece, pieceType, lastMove.fenBefore, lastMove.fenAfter
                );
                moveHistory.set(movePointer - 1, promotedMove);
            }
        }
    }

    public java.util.List<Piece> getWhiteCaptured() {
        return whiteCaptured;
    }

    public java.util.List<Piece> getBlackCaptured() {
        return blackCaptured;
    }

    // board state to FEN string
    public String toFEN() {
        StringBuilder fen = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int empty = 0;
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece == null) {
                    empty++;
                } else {
                    if (empty > 0) {
                        fen.append(empty);
                        empty = 0;
                    }

                    char symbol = piece.getSymbol();
                    char pieceChar = ' ';
                    switch (symbol) {
                        case '♔': pieceChar = 'K'; break;
                        case '♕': pieceChar = 'Q'; break;
                        case '♖': pieceChar = 'R'; break;
                        case '♗': pieceChar = 'B'; break;
                        case '♘': pieceChar = 'N'; break;
                        case '♙': pieceChar = 'P'; break;
                        case '♚': pieceChar = 'k'; break;
                        case '♛': pieceChar = 'q'; break;
                        case '♜': pieceChar = 'r'; break;
                        case '♝': pieceChar = 'b'; break;
                        case '♞': pieceChar = 'n'; break;
                        case '♟': pieceChar = 'p'; break;
                    }
                    fen.append(pieceChar);
                }
            }
            if (empty > 0) fen.append(empty);
            if (row < 7) fen.append('/');
        }
        fen.append(' ');
        fen.append(isWhiteTurn ? 'w' : 'b');
        fen.append(' ');
        StringBuilder castling = new StringBuilder();
        Piece whiteKing = getPieceAt(7, 4);
        Piece blackKing = getPieceAt(0, 4);
        
        if (whiteKing instanceof King && !((King) whiteKing).getHasMoved()) {
            Piece whiteRookK = getPieceAt(7, 7);
            Piece whiteRookQ = getPieceAt(7, 0);
            if (whiteRookK instanceof Rook && !((Rook) whiteRookK).getHasMoved()) castling.append("K");
            if (whiteRookQ instanceof Rook && !((Rook) whiteRookQ).getHasMoved()) castling.append("Q");
        }
        if (blackKing instanceof King && !((King) blackKing).getHasMoved()) {
            Piece blackRookK = getPieceAt(0, 7);
            Piece blackRookQ = getPieceAt(0, 0);
            if (blackRookK instanceof Rook && !((Rook) blackRookK).getHasMoved()) castling.append("k");
            if (blackRookQ instanceof Rook && !((Rook) blackRookQ).getHasMoved()) castling.append("q");
        }
        
        fen.append(castling.length() > 0 ? castling.toString() : "-");
        
        fen.append(' ');
        fen.append(enPassantTarget);
        fen.append(' ');
        fen.append(halfmoveClock);
        fen.append(' ');
        fen.append(fullmoveNumber);
        return fen.toString();
    }

    public boolean undoMove() {
        if (movePointer == 0) return false;
        Move lastMove = moveHistory.get(movePointer - 1);
        loadFEN(lastMove.fenBefore);
        movePointer--;
        if (lastMove.capturedPiece != null) {
            if (lastMove.movedPiece.isWhite()) {
                blackCaptured.remove(blackCaptured.size() - 1);
            } else {
                whiteCaptured.remove(whiteCaptured.size() - 1);
            }
        }
        isWhiteTurn = !isWhiteTurn;
        return true;
    }

    public boolean redoMove() {
        if (movePointer >= moveHistory.size()) return false;
        Move nextMove = moveHistory.get(movePointer);
        loadFEN(nextMove.fenAfter);
        movePointer++;
        if (nextMove.capturedPiece != null) {
            if (nextMove.movedPiece.isWhite()) {
                blackCaptured.add(nextMove.capturedPiece);
            } else {
                whiteCaptured.add(nextMove.capturedPiece);
            }
        }
        isWhiteTurn = !isWhiteTurn;
        return true;
    }

    private void loadFEN(String fen) {
        String[] parts = fen.split(" ");
        String[] rows = parts[0].split("/");
        for (int row = 0; row < 8; row++) {
            int col = 0;
            for (char c : rows[row].toCharArray()) {
                if (Character.isDigit(c)) {
                    int empty = c - '0';
                    for (int i = 0; i < empty; i++) {
                        board[row][col++] = null;
                    }
                } else {
                    boolean isWhite = Character.isUpperCase(c);
                    char pieceChar = Character.toLowerCase(c);
                    switch (pieceChar) {
                        case 'k': board[row][col] = new King(isWhite, row, col); break;
                        case 'q': board[row][col] = new Queen(isWhite, row, col); break;
                        case 'r': board[row][col] = new Rook(isWhite, row, col); break;
                        case 'b': board[row][col] = new Bishop(isWhite, row, col); break;
                        case 'n': board[row][col] = new Knight(isWhite, row, col); break;
                        case 'p': board[row][col] = new Pawn(isWhite, row, col); break;
                        default: board[row][col] = null;
                    }
                    col++;
                }
            }
        }
        isWhiteTurn = parts[1].equals("w");
    }

    // move history and pointer
    public java.util.List<Move> getMoveHistory() {
        return moveHistory;
    }
    public int getMovePointer() {
        return movePointer;
    }

    public Move getLastMove() {
        if (moveHistory.isEmpty() || movePointer == 0) return null;
        return moveHistory.get(movePointer - 1);
    }

    // board coordinates to algebraic notation
    private String toAlgebraic(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
} 


package com.chessn;

public class ChessGame {
    private Piece[][] board;
    private boolean isWhiteTurn = true;
    private int selectedPieceRow = -1; // Keep selected piece state here for simplicity for now
    private int selectedPieceCol = -1; // Keep selected piece state here for simplicity for now
    private java.util.List<Piece> whiteCaptured = new java.util.ArrayList<>();
    private java.util.List<Piece> blackCaptured = new java.util.ArrayList<>();
    private java.util.List<Move> moveHistory = new java.util.ArrayList<>();
    private int movePointer = 0; // Points to the next move to be made (for undo/redo)
    private int halfmoveClock = 0;
    private int fullmoveNumber = 1;
    private String enPassantTarget = "-";

    // Constructor
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

    /**
     * Initialize the chess board with all pieces in their starting positions.
     */
    private void setupBoard() {
        // Clear the board first
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }

        // Set up black pieces
        board[0][0] = new Rook(false, 0, 0);      // Black Rook
        board[0][1] = new Knight(false, 0, 1);    // Black Knight
        board[0][2] = new Bishop(false, 0, 2);    // Black Bishop
        board[0][3] = new Queen(false, 0, 3);     // Black Queen
        board[0][4] = new King(false, 0, 4);      // Black King
        board[0][5] = new Bishop(false, 0, 5);    // Black Bishop
        board[0][6] = new Knight(false, 0, 6);    // Black Knight
        board[0][7] = new Rook(false, 0, 7);      // Black Rook

        // Set up black pawns
        for (int col = 0; col < 8; col++) {
            board[1][col] = new Pawn(false, 1, col);
        }

        // Set up white pieces
        board[7][0] = new Rook(true, 7, 0);      // White Rook
        board[7][1] = new Knight(true, 7, 1);    // White Knight
        board[7][2] = new Bishop(true, 7, 2);    // White Bishop
        board[7][3] = new Queen(true, 7, 3);     // White Queen
        board[7][4] = new King(true, 7, 4);      // White King
        board[7][5] = new Bishop(true, 7, 5);    // White Bishop
        board[7][6] = new Knight(true, 7, 6);    // White Knight
        board[7][7] = new Rook(true, 7, 7);      // White Rook

        // Set up white pawns
        for (int col = 0; col < 8; col++) {
            board[6][col] = new Pawn(true, 6, col);
        }
    }

    /**
     * Attempt to move a piece from one position to another.
     * Returns true if the move was successful.
     */
    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        Piece movingPiece = board[fromRow][fromCol];
        Piece targetPiece = board[toRow][toCol];

        // Basic move validation
        if (movingPiece == null) {
            return false;  // No piece to move
        }

        // Check if it's the correct player's turn
        if (movingPiece.isWhite() != isWhiteTurn) {
            return false;  // Wrong player's turn
        }

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

        // Check if this is a capture attempt
        if (targetPiece != null) {
            if (!movingPiece.canCapture(targetPiece)) {
                return false;  // Invalid capture
            }
            // Track captured piece
            if (movingPiece.isWhite()) {
                blackCaptured.add(targetPiece);
            } else {
                whiteCaptured.add(targetPiece);
            }
        } else {
            // Normal move validation
            if (!movingPiece.isValidMove(toRow, toCol)) {
                return false;  // Invalid move
            }
        }

        // Check if path is clear (except for Knights)
        if (!(movingPiece instanceof Knight)) {
            if (!isPathClear(fromRow, fromCol, toRow, toCol)) {
                return false;
            }
        }

        // Check if this move would leave/put the king in check
        if (wouldBeInCheck(fromRow, fromCol, toRow, toCol)) {
            return false;  // Can't make a move that puts/leaves your king in check
        }

        // Check for pawn promotion (before move)
        if (movingPiece instanceof Pawn && (toRow == 0 || toRow == 7)) {
            isPromotion = true;
        }

        // Execute the move
        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
        movingPiece.setPosition(toRow, toCol);

        // Update piece's move status
        if (movingPiece instanceof Pawn) {
            ((Pawn) movingPiece).moved();
        } else if (movingPiece instanceof King) {
            ((King) movingPiece).moved();
        } else if (movingPiece instanceof Rook) {
            ((Rook) movingPiece).moved();
        }

        // Save FEN after move
        // Update halfmove clock and fullmove number
        if (movingPiece instanceof Pawn || targetPiece != null) {
            halfmoveClock = 0;
        } else {
            halfmoveClock++;
        }
        if (!isWhiteTurn) {
            fullmoveNumber++;
        }
        // En passant target square (basic version)
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
        // Get the direction of movement
        int rowDirection = Integer.compare(toRow - fromRow, 0);  // -1 for up, 1 for down, 0 for same row
        int colDirection = Integer.compare(toCol - fromCol, 0);  // -1 for left, 1 for right, 0 for same column

        // Start checking from the square after the start position
        int currentRow = fromRow + rowDirection;
        int currentCol = fromCol + colDirection;

        // Check each square until we reach the destination
        while (currentRow != toRow || currentCol != toCol) {
            if (board[currentRow][currentCol] != null) {
                return false;  // There's a piece in the way
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
        return null; // Should never happen in a valid game
    }

    /**
     * Check if a king is in check
     */
    public boolean isInCheck(boolean isWhiteKing) {
        // Find the king's position
        int[] kingPos = findKing(isWhiteKing);
        if (kingPos == null) return false;

        // Check if any opponent's piece can capture the king
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board[row][col];
                if (piece != null && piece.isWhite() != isWhiteKing) {
                    // Check if this piece can capture the king
                    if (piece.canCapture(board[kingPos[0]][kingPos[1]]) &&
                        (piece instanceof Knight || isPathClear(row, col, kingPos[0], kingPos[1]))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Test if a move would leave the moving player's king in check
     */
    public boolean wouldBeInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        // Store current board state
        Piece movingPiece = board[fromRow][fromCol];
        Piece capturedPiece = board[toRow][toCol];

        // Make the move temporarily
        board[toRow][toCol] = movingPiece;
        board[fromRow][fromCol] = null;
        movingPiece.setPosition(toRow, toCol);  // Update piece's position temporarily

        // Check if the move puts/leaves the king in check
        boolean inCheck = isInCheck(movingPiece.isWhite());

        // Restore the board state
        board[fromRow][fromCol] = movingPiece;
        board[toRow][toCol] = capturedPiece;
        movingPiece.setPosition(fromRow, fromCol);  // Restore piece's original position

        return inCheck;
    }

    /**
     * Check if the specified player has any legal moves available.
     * This method iterates through all of the player's pieces and all possible
     * squares to see if any move is valid and does not leave the king in check.
     */
    public boolean hasLegalMoves(boolean isWhitePlayer) {
        // Try every possible move for every piece of the same color
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                Piece piece = board[fromRow][fromCol];
                // Skip empty squares and opponent's pieces
                if (piece == null || piece.isWhite() != isWhitePlayer) {
                    continue;
                }

                // Try moving to every square on the board
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        // Skip the current position
                        if (fromRow == toRow && fromCol == toCol) {
                            continue;
                        }

                        // Check if this is a valid move
                        Piece targetPiece = board[toRow][toCol];
                        boolean isValidMove = false;

                        if (targetPiece == null) {
                            // Normal move
                            isValidMove = piece.isValidMove(toRow, toCol) &&
                                        (piece instanceof Knight || isPathClear(fromRow, fromCol, toRow, toCol));
                        } else if (targetPiece.isWhite() != isWhitePlayer) {
                            // Capture move
                            isValidMove = piece.canCapture(targetPiece) &&
                                        (piece instanceof Knight || isPathClear(fromRow, fromCol, toRow, toCol));
                        }

                        // If it's a valid move, check if it gets us out of check
                        if (isValidMove && !wouldBeInCheck(fromRow, fromCol, toRow, toCol)) {
                            return true;  // Found a legal move
                        }
                    }
                }
            }
        }
        // If we get here, no legal moves were found
        return false;
    }

    /**
     * Check if a king is in checkmate
     */
    public boolean isInCheckmate(boolean isWhiteKing) {
        // First, check if the king is in check
        if (!isInCheck(isWhiteKing)) {
            return false;
        }

        return !hasLegalMoves(isWhiteKing);
    }

    /**
     * Check if a king is in stalemate. Stalemate occurs when a player has no legal moves
     * but their king is NOT in check.
     */
    public boolean isStalemate(boolean isWhitePlayer) {
        return !isInCheck(isWhitePlayer) && !hasLegalMoves(isWhitePlayer);
    }

    /**
     * Check if castling is valid
     */
    public boolean isValidCastling(int fromRow, int fromCol, int toRow, int toCol) {
        // Must be a king
        Piece king = board[fromRow][fromCol];
        if (!(king instanceof King) || ((King)king).getHasMoved()) {
            return false;
        }

        // Must be moving horizontally in the same row
        if (fromRow != toRow || Math.abs(toCol - fromCol) != 2) {
            return false;
        }

        // Determine if it's kingside or queenside castling
        boolean isKingside = toCol > fromCol;
        int rookCol = isKingside ? 7 : 0;

        // Check if rook is in place and hasn't moved
        Piece rook = board[fromRow][rookCol];
        if (!(rook instanceof Rook) || ((Rook)rook).getHasMoved()) {
            return false;
        }

        // Check if path is clear
        int step = isKingside ? 1 : -1;
        for (int col = fromCol + step; isKingside ? col < rookCol : col > rookCol; col += step) {
            if (board[fromRow][col] != null) {
                return false;
            }
        }

        // Check if king is in check or passes through check
        if (isInCheck(king.isWhite())) {
            return false;
        }

        // Check if king passes through attacked square
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

    /**
     * Execute castling move
     */
    private void executeCastling(int fromRow, int fromCol, int toRow, int toCol) {
        boolean isKingside = toCol > fromCol;
        int rookFromCol = isKingside ? 7 : 0;
        int rookToCol = isKingside ? toCol - 1 : toCol + 1;

        // Move king
        Piece king = board[fromRow][fromCol];
        board[toRow][toCol] = king;
        board[fromRow][fromCol] = null;
        king.setPosition(toRow, toCol);
        ((King)king).moved();

        // Move rook
        Piece rook = board[fromRow][rookFromCol];
        board[fromRow][rookToCol] = rook;
        board[fromRow][rookFromCol] = null;
        rook.setPosition(fromRow, rookToCol);
        ((Rook)rook).moved();
    }

    /**
     * Print the current board state to console (for debugging)
     */
    public void printBoard() {
        System.out.println("  a b c d e f g h"); // Column labels
        System.out.println("  ---------------");
        for (int row = 0; row < 8; row++) {
            System.out.print((8 - row) + "|"); // Row labels
            for (int col = 0; col < 8; col++) {
                char symbol = (board[row][col] != null) ? board[row][col].getSymbol() : '-';
                System.out.print(symbol + " ");
            }
            System.out.println("|" + (8 - row)); // Row labels on the right
        }
        System.out.println("  ---------------");
        System.out.println("  a b c d e f g h"); // Column labels
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

    // Add promotePawn method
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
        // Update the last move in moveHistory with the promotion type
        if (!moveHistory.isEmpty()) {
            Move lastMove = moveHistory.get(movePointer - 1);
            if (lastMove.promotionType != null && lastMove.promotionType.equals("Pending")) {
                // Create a new Move with the correct promotion type and replace the last move
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

    // Export the current board state as a FEN string
    public String toFEN() {
        StringBuilder fen = new StringBuilder();
        // 1. Piece placement
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
        // 2. Active color
        fen.append(' ');
        fen.append(isWhiteTurn ? 'w' : 'b');
        // 3. Castling rights
        fen.append(' ');
        StringBuilder castling = new StringBuilder();
        // Check for unmoved kings and rooks more reliably
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
        
        // 4. En passant target square
        fen.append(' ');
        fen.append(enPassantTarget);
        // 5. Halfmove clock (for 50-move rule)
        fen.append(' ');
        fen.append(halfmoveClock);
        // 6. Fullmove number
        fen.append(' ');
        fen.append(fullmoveNumber);
        return fen.toString();
    }

    // Undo the last move
    public boolean undoMove() {
        if (movePointer == 0) return false;
        Move lastMove = moveHistory.get(movePointer - 1);
        // Restore board state from FEN before the move
        loadFEN(lastMove.fenBefore);
        movePointer--;
        // Update captured pieces lists
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

    // Redo the next move
    public boolean redoMove() {
        if (movePointer >= moveHistory.size()) return false;
        Move nextMove = moveHistory.get(movePointer);
        loadFEN(nextMove.fenAfter);
        movePointer++;
        // Update captured pieces lists
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

    // Load board state from FEN (simple version, only for undo/redo)
    private void loadFEN(String fen) {
        // Only supports piece placement and turn for now
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

    // Getters for move history and pointer
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

    // Helper: convert board coordinates to algebraic notation
    private String toAlgebraic(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }
} 


package com.chessmove.model.game;

import com.chessmove.model.piece.Piece;
import com.chessmove.util.Color;
import com.chessmove.util.MoveType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.chessmove.util.CalculationConstants.DELIMITER;
import static com.chessmove.util.CalculationConstants.EMPTY;
import static com.chessmove.util.CalculationConstants.LINE_BREAK;
import static com.chessmove.util.CalculationConstants.SEPARATOR;

public final class Board {

    private final Piece[][] squares;
    private final String enPassantTarget;
    private final Set<String> whiteCastlingRights;
    private final Set<String> blackCastlingRights;

    public Board(Piece[][] squares, String enPassantTarget,
                 Set<String> whiteCastlingRights, Set<String> blackCastlingRights) {
        this.squares = squares;
        this.enPassantTarget = enPassantTarget;
        this.whiteCastlingRights = whiteCastlingRights;
        this.blackCastlingRights = blackCastlingRights;
    }

    public Board(Piece[][] squares) {
        this(squares, null, new HashSet<>(), new HashSet<>());
    }

    public Board() {
        squares = new Piece[8][8];
        this.enPassantTarget = "-";
        this.whiteCastlingRights = new HashSet<>();
        this.blackCastlingRights = new HashSet<>();
    }

    public Board copy() {
        Piece[][] squaresCopy = new Piece[8][8];
        for (int row = 0; row < 8; row++) {
            squaresCopy[row] = Arrays.copyOf(this.squares[row], 8);
        }
        return new Board(squaresCopy, enPassantTarget, whiteCastlingRights, blackCastlingRights);
    }

    public Piece[][] getSquares() {
        return squares;
    }

    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    public Set<String> getWhiteCastlingRights() {
        return whiteCastlingRights;
    }

    public Set<String> getBlackCastlingRights() {
        return blackCastlingRights;
    }

    public boolean squareUnderAttack(int row, int col, Color attackerColor) {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Piece piece = squares[r][c];
                if (piece != null && piece.getColor() == attackerColor && piece.canAttackSquare(r, c, row, col, this)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Piece getPieceAt(int row, int col) {
        if (isInBounds(row, col)) {
            return squares[row][col];
        }
        return null;
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public boolean isEmpty(int row, int col) {
        return isInBounds(row, col) && squares[row][col] == null;
    }

    public boolean isEnemyPiece(int row, int col, Color color) {
        return isInBounds(row, col) && squares[row][col] != null && squares[row][col].getColor() != color;
    }

    public boolean isAttackerPiece(int row, int col, Color color) {
        return !isInBounds(row, col) || squares[row][col] == null || squares[row][col].getColor() != color;
    }

    public String drawBoard() {
        StringBuilder boardRepresentation = new StringBuilder();
        drawHorizontalCoordinates(boardRepresentation);
        for (int row = 0; row < 8; row++) {
            drawVerticalCoordinates(boardRepresentation, row).append(SEPARATOR);
            drawLine(row, boardRepresentation);
            drawVerticalCoordinates(boardRepresentation, row).append(LINE_BREAK);
        }
        drawHorizontalCoordinates(boardRepresentation);
        return boardRepresentation.toString();
    }

    private void drawLine(int row, StringBuilder boardRepresentation) {
        for (int col = 0; col < 8; col++) {
            Piece piece = squares[row][col];
            if (piece != null) {
                char pieceSymbol = piece.getType().getSymbol(piece.getColor());
                if (piece.getColor() == Color.BLACK) {
                    pieceSymbol = Character.toLowerCase(pieceSymbol); // Black pieces in lowercase
                }
                boardRepresentation.append(pieceSymbol).append(SEPARATOR);
            } else {
                boardRepresentation.append(EMPTY).append(SEPARATOR);
            }
        }
    }

    private static StringBuilder drawVerticalCoordinates(StringBuilder boardRepresentation, int row) {
        return boardRepresentation.append(DELIMITER).append(8 - row).append(DELIMITER);
    }

    private static void drawHorizontalCoordinates(StringBuilder boardRepresentation) {
        boardRepresentation
                .append(DELIMITER.repeat(4))
                .append("a").append(EMPTY)
                .append("b").append(EMPTY)
                .append("c").append(EMPTY)
                .append("d").append(EMPTY)
                .append("e").append(EMPTY)
                .append("f").append(EMPTY)
                .append("g").append(EMPTY)
                .append("h").append(LINE_BREAK);
    }

    /**
     * Formats the move into a human-readable description.
     *
     * @param piece The piece being moved
     * @param move  The move being made
     * @return A string describing the move
     */
    public static String formatMoveDescription(Piece piece, Move move) {
        String pieceName = piece.getType().toString().toLowerCase();
        String fromSquare = toAlgebraicNotation(move.fromRow(), move.fromCol());
        String toSquare = toAlgebraicNotation(move.toRow(), move.toCol());

        String moveDescription = String.format("%s from %s to %s", pieceName, fromSquare, toSquare);

        // Include captured piece if present
        if (move.getCapturedPiece() != null) {
            String capturedPieceName = move.getCapturedPiece().getType().toString().toLowerCase();
            moveDescription += String.format(" (capturing %s)", capturedPieceName);
        }

        // Append move type if it's castling or en-passant
        if (move.getMoveType() == MoveType.CASTLING) {
            moveDescription += " (castling)";
        } else if (move.getMoveType() == MoveType.EN_PASSANT) {
            moveDescription += " (en-passant)";
        }

        return moveDescription;
    }

    /**
     * Converts board coordinates to algebraic notation.
     *
     * @param row The row index (0-7)
     * @param col The column index (0-7)
     * @return The square in algebraic notation (e.g., 'e4')
     */
    private static String toAlgebraicNotation(int row, int col) {
        char file = (char) ('a' + col);
        int rank = 8 - row;
        return "" + file + rank;
    }

}

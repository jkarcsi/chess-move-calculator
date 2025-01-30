package com.chessmove.util;

import com.chessmove.model.game.Board;
import com.chessmove.model.piece.*;
import java.util.*;

import static com.chessmove.util.CalculationConstants.BLACK;
import static com.chessmove.util.CalculationConstants.EMPTY_INPUT;
import static com.chessmove.util.CalculationConstants.FEN_SEPARATOR;
import static com.chessmove.util.CalculationConstants.WHITE;
import static com.chessmove.util.CalculationConstants.WHITE_SPACE;
import static com.chessmove.util.CalculationMessage.BOTH_KINGS_ARE_IN_CHECK;
import static com.chessmove.util.CalculationMessage.CONSECUTIVE_DIGITS_IN_RANK;
import static com.chessmove.util.CalculationMessage.FEN_STRING_IS_INCOMPLETE;
import static com.chessmove.util.CalculationMessage.INCORRECT_NUMBER_OF_RANKS;
import static com.chessmove.util.CalculationMessage.INCORRECT_NUMBER_OF_SQUARES_IN_RANK;
import static com.chessmove.util.CalculationMessage.INVALID_ACTIVE_COLOR;
import static com.chessmove.util.CalculationMessage.INVALID_CASTLING_RIGHTS;
import static com.chessmove.util.CalculationMessage.INVALID_NUMBER_OF_EMPTY_SQUARES;
import static com.chessmove.util.CalculationMessage.INVALID_NUMBER_OF_KINGS;
import static com.chessmove.util.CalculationMessage.INVALID_PIECE_TYPE;
import static com.chessmove.util.CalculationMessage.NON_ACTIVE_PLAYERS_KING_IS_IN_CHECK;
import static com.chessmove.util.CalculationMessage.TOO_MANY_PAWNS;
import static com.chessmove.util.CalculationMessage.TOO_MANY_PIECE;
import static com.chessmove.util.CalculationMessage.TOO_MANY_PIECES;
import static com.chessmove.util.CalculationMessage.TOO_MANY_PROMOTED_PIECES;
import static com.chessmove.util.CalculationMessage.TOO_MANY_QUEENS;
import static com.chessmove.util.CalculationMessage.TOO_MANY_SQUARES_IN_RANK;

public class FENValidator {

    private Piece[][] squares;
    private Board board;
    private String fen;
    private String activeColor;
    private Set<String> whiteCastlingRights;
    private Set<String> blackCastlingRights;
    private String enPassantTarget;
    private String halfmoveClock;
    private String fullmoveNumber;
    private boolean fullMode;

    public FENValidator(String fen) throws InvalidFENException {
        validateFENdata(fen);
    }

    public void validateFENdata(String fen) throws InvalidFENException {

        String[] parts = fen.trim().split(WHITE_SPACE);
        checkAdequacy(parts.length < 1, FEN_STRING_IS_INCOMPLETE);

        String piecePlacement = parts[0];
        String turn = parts.length > 1 ? parts[1] : EMPTY_INPUT;
        String castlingAvailability = parts.length > 2 ? parts[2] : EMPTY_INPUT;
        String enPassantSquare = parts.length > 3 ? parts[3] : EMPTY_INPUT;
        String halfMove = parts.length > 4 ? parts[4] : EMPTY_INPUT;
        String fullMove = parts.length > 5 ? parts[5] : EMPTY_INPUT;

        Piece[][] grid = new Piece[8][8];
        String[] ranks = piecePlacement.split(FEN_SEPARATOR);
        checkAdequacy(ranks.length != 8, INCORRECT_NUMBER_OF_RANKS);

        Map<Color, Map<PieceType, Integer>> pieceCounts = new EnumMap<>(Color.class);
        pieceCounts.put(Color.WHITE, new EnumMap<>(PieceType.class));
        pieceCounts.put(Color.BLACK, new EnumMap<>(PieceType.class));
        int[] totalPieces = new int[]{0, 0}; // Index 0 for WHITE, 1 for BLACK

        validateBoard(ranks, pieceCounts, totalPieces, grid);
        validatePieces(pieceCounts, totalPieces);

        this.setFen(piecePlacement);
        parseActiveColor(turn);

        parseCastlingAvailability(castlingAvailability);
        this.setEnPassantTarget(enPassantSquare);
        this.setHalfmoveClock(parseMove(halfMove));
        this.setFullmoveNumber(parseMove(fullMove));
        this.setSquares(grid);

        this.setFullMode(isFullMode(parts, turn, castlingAvailability, enPassantSquare, halfMove, fullMove));

        this.board = new Board(grid, this.getEnPassantTarget(), this.getWhiteCastlingRights(), this.getBlackCastlingRights());
        this.setBoard(board);

        validateKingsNotInCheck(board);
    }

    private static boolean isFullMode(String[] parts,
                                      String turn,
                                      String castlingAvailability,
                                      String enPassantSquare,
                                      String halfMove,
                                      String fullMove) {
        return parts.length > 1 && (
                !turn.equals(EMPTY_INPUT) ||
                        !castlingAvailability.equals(EMPTY_INPUT) ||
                        !enPassantSquare.equals(EMPTY_INPUT) ||
                        !halfMove.equals(EMPTY_INPUT) ||
                        !fullMove.equals(EMPTY_INPUT)
        );
    }

    private String parseMove(String move) {
        StringBuilder ret = new StringBuilder();
        char[] c = move.toCharArray();
        for (char cc : c) {
            if (Character.isDigit(cc)) {
                ret.append(cc);
            }
        }
        return ret.toString().isEmpty() ? EMPTY_INPUT : ret.toString();
    }

    private void parseActiveColor(String color) throws InvalidFENException {
        switch (color.toLowerCase()) {
            case WHITE -> this.setActiveColor(Color.WHITE.getColorName());
            case BLACK -> this.setActiveColor(Color.BLACK.getColorName());
            case EMPTY_INPUT -> this.setActiveColor(EMPTY_INPUT);
            default -> throw new InvalidFENException(INVALID_ACTIVE_COLOR + color);
        }
    }

    private void parseCastlingAvailability(String castlingAvailability) throws InvalidFENException {
        this.whiteCastlingRights = new HashSet<>();
        this.blackCastlingRights = new HashSet<>();
        for (char c : castlingAvailability.toCharArray()) {
            switch (c) {
                case 'K' -> whiteCastlingRights.add("K");
                case 'Q' -> whiteCastlingRights.add("Q");
                case 'k' -> blackCastlingRights.add("k");
                case 'q' -> blackCastlingRights.add("q");
                case '-' -> {
                    blackCastlingRights.add(EMPTY_INPUT);
                    whiteCastlingRights.add(EMPTY_INPUT);
                }
                default -> throw new InvalidFENException(INVALID_CASTLING_RIGHTS + castlingAvailability);
            }
        }
        this.setWhiteCastlingRights(whiteCastlingRights);
        this.setBlackCastlingRights(blackCastlingRights);
    }

    private void validateKingsNotInCheck(Board board) throws InvalidFENException {
        if (this.getActiveColor() == null || this.getActiveColor().equals(EMPTY_INPUT)) {
            // Active color not specified; check if both kings are in check
            boolean whiteKingInCheck = isKingInCheck(board, Color.WHITE);
            boolean blackKingInCheck = isKingInCheck(board, Color.BLACK);

            if (whiteKingInCheck && blackKingInCheck) {
                throw new InvalidFENException(BOTH_KINGS_ARE_IN_CHECK);
            }
            // Do not throw an exception if only one king is in check
            // I will infer the active color based on which king is in check
        } else {
            // Active color specified; validate that the non-active king is not in check
            validateKingNotInCheck(board);
        }
    }

    private void validateKingNotInCheck(Board board) throws InvalidFENException {
        Color active = Color.fromName(this.getActiveColor());
        Color nonActive = Color.getOppositeColor(active);

        boolean isNonActiveKingInCheck = isKingInCheck(board, nonActive);

        if (isNonActiveKingInCheck) {
            throw new InvalidFENException(NON_ACTIVE_PLAYERS_KING_IS_IN_CHECK);
        }
    }

    public static boolean isKingInCheck(Board board, Color kingColor) {
        int[] kingPosition = findKingPosition(board, kingColor);
        if (kingPosition[0] == -1 && kingPosition[1] == -1) {
            // King not found (should not happen)
            return true;
        }

        int kingRow = kingPosition[0];
        int kingCol = kingPosition[1];

        return board.squareUnderAttack(kingRow, kingCol, Color.getOppositeColor(kingColor));
    }

    public static int[] findKingPosition(Board board, Color kingColor) {
        Piece[][] grid = board.getSquares();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = grid[row][col];
                if (piece != null && piece.getColor() == kingColor && piece.getType() == PieceType.KING) {
                    return new int[]{row, col}; // Return the position as an array [row, col]
                }
            }
        }
        return new int[]{-1, -1}; // King not found
    }

    private void validateBoard(String[] ranks, Map<Color, Map<PieceType, Integer>> pieceCounts, int[] totalPieces,
                               Piece[][] squares) throws InvalidFENException {
        for (int i = 0; i < 8; i++) {
            String rank = ranks[i];
            int file = 0;
            Character prevChar = null;

            for (int j = 0; j < rank.length(); j++) {
                checkAdequacy(file >= 8, TOO_MANY_SQUARES_IN_RANK + (1 + i));
                char c = rank.charAt(j);

                // Check for consecutive digits
                file = validateDigits(pieceCounts, totalPieces, squares, c, prevChar, i, file);
                prevChar = c;
            }
            checkAdequacy(file != 8, INCORRECT_NUMBER_OF_SQUARES_IN_RANK + (1 + i));
        }
    }

    private int validateDigits(Map<Color, Map<PieceType, Integer>> pieceCounts,
                        int[] totalPieces,
                        Piece[][] squares,
                        char c,
                        Character prevChar,
                        int i,
                        int file) throws InvalidFENException {
        if (Character.isDigit(c)) {
            if (prevChar != null && Character.isDigit(prevChar)) {
                throw new InvalidFENException(CONSECUTIVE_DIGITS_IN_RANK + (8 - i));
            }
            int emptySquares = c - '0';
            checkAdequacy(emptySquares < 1 || emptySquares > 8,
                    INVALID_NUMBER_OF_EMPTY_SQUARES + c);
            file += emptySquares;
        } else {
            Piece piece = getPieceFromChar(c);
            squares[i][file] = piece;
            file++;

            // Update piece counts
            updatePieceCounts(pieceCounts, totalPieces, piece);
        }
        return file;
    }

    private void updatePieceCounts(Map<Color, Map<PieceType, Integer>> pieceCounts,
                                   int[] totalPieces,
                                   Piece piece) {
        Color color = piece.getColor();
        PieceType type = piece.getType();
        Map<PieceType, Integer> counts = pieceCounts.get(color);
        counts.put(type, counts.getOrDefault(type, 0) + 1);
        totalPieces[color == Color.WHITE ? 0 : 1]++;
    }

    private void validatePieces(Map<Color, Map<PieceType, Integer>> pieceCounts, int[] totalPieces)
            throws InvalidFENException {
        for (Color color : Color.values()) {
            Map<PieceType, Integer> counts = pieceCounts.get(color);

            // Check for exactly one king
            exactlyOneKing(color, counts);

            // Maximum number of pawns is 8
            int pawnCount = maximumNumberOfPawns(color, counts);

            // Total number of pieces cannot exceed 16
            checkAdequacy(totalPieces[color == Color.WHITE ? 0 : 1] > 16,
                    TOO_MANY_PIECES + color + ": " + totalPieces[color == Color.WHITE ? 0 : 1]);

            // Maximum counts for other pieces (before promotions)
            maximumCountForOtherPieces(color, counts);

            // Maximum one queen before promotions
            oneQueenBeforePromotion(color, counts, pawnCount);

            // For other pieces, check if the counts exceed the standard number plus possible promotions
            countExceeding(color, pawnCount, counts);
        }
    }

    private void exactlyOneKing(Color color, Map<PieceType, Integer> counts) throws InvalidFENException {
        int kingCount = counts.getOrDefault(PieceType.KING, 0);
        checkAdequacy(kingCount != 1, INVALID_NUMBER_OF_KINGS + color + ": " + kingCount);
    }

    private int maximumNumberOfPawns(Color color, Map<PieceType, Integer> counts) throws InvalidFENException {
        int pawnCount = counts.getOrDefault(PieceType.PAWN, 0);
        checkAdequacy(pawnCount > 8, TOO_MANY_PAWNS + color + ": " + pawnCount);
        return pawnCount;
    }

    private void maximumCountForOtherPieces(Color color, Map<PieceType, Integer> counts)
            throws InvalidFENException {
        int maxCount = 2;
        for (PieceType type : Arrays.asList(PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP)) {
            int count = counts.getOrDefault(type, 0);
            checkAdequacy(count > maxCount, TOO_MANY_PIECE + type.toString()
                    .toLowerCase() + "s for " + color + ": " + count);
        }
    }

    private void oneQueenBeforePromotion(Color color, Map<PieceType, Integer> counts, int pawnCount)
            throws InvalidFENException {
        int queenCount = counts.getOrDefault(PieceType.QUEEN, 0);
        if (queenCount > 1) {
            int missingPawns = 8 - pawnCount;
            int extraQueens = queenCount - 1;
            checkAdequacy(extraQueens > missingPawns, TOO_MANY_QUEENS + color + ": " + queenCount);
        }
    }

    private void countExceeding(Color color, int pawnCount, Map<PieceType, Integer> counts)
            throws InvalidFENException {
        int missingPawns = 8 - pawnCount;
        int promotionPieces = counts.getOrDefault(PieceType.QUEEN, 0) - 1 +
                counts.getOrDefault(PieceType.ROOK, 0) - 2 +
                counts.getOrDefault(PieceType.BISHOP, 0) - 2 +
                counts.getOrDefault(PieceType.KNIGHT, 0) - 2;
        promotionPieces = Math.max(0, promotionPieces);
        checkAdequacy(promotionPieces > missingPawns,
                TOO_MANY_PROMOTED_PIECES + color + ": " + promotionPieces);
    }

    private static void checkAdequacy(boolean inadequate, String reason) throws InvalidFENException {
        if (inadequate) {
            throw new InvalidFENException(reason);
        }
    }

    private Piece getPieceFromChar(char c) throws InvalidFENException {
        Color color = Character.isUpperCase(c) ? Color.WHITE : Color.BLACK;
        return switch (Character.toUpperCase(c)) {
            case 'K' -> new King(color);
            case 'Q' -> new Queen(color);
            case 'R' -> new Rook(color);
            case 'B' -> new Bishop(color);
            case 'N' -> new Knight(color);
            case 'P' -> new Pawn(color);
            default -> throw new InvalidFENException(INVALID_PIECE_TYPE + c);
        };
    }

    public Piece[][] getSquares() {
        return squares;
    }

    public Board getBoard() {
        return board;
    }

    public String getFen() {
        return fen;
    }

    public String getActiveColor() {
        return activeColor;
    }

    public Set<String> getWhiteCastlingRights() {
        return whiteCastlingRights;
    }

    public Set<String> getBlackCastlingRights() {
        return blackCastlingRights;
    }

    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    public String getHalfmoveClock() {
        return halfmoveClock;
    }

    public String getFullmoveNumber() {
        return fullmoveNumber;
    }

    public boolean isFullMode() {
        return fullMode;
    }

    public void setSquares(Piece[][] squares) {
        this.squares = squares;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setFen(String fen) {
        this.fen = fen;
    }

    public void setActiveColor(String activeColor) {
        this.activeColor = activeColor;
    }

    public void setWhiteCastlingRights(Set<String> whiteCastlingRights) {
        this.whiteCastlingRights = whiteCastlingRights;
    }

    public void setBlackCastlingRights(Set<String> blackCastlingRights) {
        this.blackCastlingRights = blackCastlingRights;
    }

    public void setEnPassantTarget(String enPassantTarget) {
        this.enPassantTarget = enPassantTarget;
    }

    public void setHalfmoveClock(String halfmoveClock) {
        this.halfmoveClock = halfmoveClock;
    }

    public void setFullmoveNumber(String fullmoveNumber) {
        this.fullmoveNumber = fullmoveNumber;
    }

    public void setFullMode(boolean fullMode) {
        this.fullMode = fullMode;
    }
}

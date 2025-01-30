package com.chessmove.service;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.model.piece.Piece;
import com.chessmove.util.*;

import java.util.*;

import static com.chessmove.model.game.Board.formatMoveDescription;
import static com.chessmove.util.CalculationConstants.*;
import static com.chessmove.util.FENValidator.isKingInCheck;
import static com.chessmove.util.LoggerUtility.*;

public class ChessMoveCalculatorService {

    private Board board;
    private final Piece[][] squares;
    private final String fen;
    private final boolean fullMode;
    private final String enPassantTarget;
    private final String activeColor;
    private final String halfmoveClock;
    private final String fullmoveNumber;
    private final Set<String> whiteCastlingRights = new HashSet<>();
    private final Set<String> blackCastlingRights = new HashSet<>();

    public ChessMoveCalculatorService(String fen) throws InvalidFENException {

        FENValidator validatedFENData = new FENValidator(fen);

        this.fen = validatedFENData.getFen();
        this.squares = validatedFENData.getSquares();
        this.activeColor = validatedFENData.getActiveColor();
        this.whiteCastlingRights.addAll(validatedFENData.getWhiteCastlingRights());
        this.blackCastlingRights.addAll(validatedFENData.getBlackCastlingRights());
        this.enPassantTarget = validatedFENData.getEnPassantTarget();
        this.halfmoveClock = validatedFENData.getHalfmoveClock();
        this.fullmoveNumber = validatedFENData.getFullmoveNumber();
        this.fullMode = validatedFENData.isFullMode();

        // Initialize the board
        this.board = validatedFENData.getBoard();
    }

    /**
     * Returns the list of possible moves based on the provided squares.
     *
     * @param squares The squares from FEN string representing the current board state
     * @param color   The color of the active player
     * @return A list of move descriptions
     */
    public List<String> getMoves(Piece[][] squares, Color color) {
        this.board = new Board(squares, enPassantTarget, whiteCastlingRights, blackCastlingRights);
        return getMoveDescriptions(color);
    }

    /**
     * Generates and returns the list of possible moves for the specified color.
     *
     * @param color The color of the active player
     * @return A list of move descriptions
     */
    private List<String> getMoveDescriptions(Color color) {
        List<String> moveDescriptions = new ArrayList<>();

        boolean kingInCheck = isKingInCheck(board, color);

        DrawStatus drawStatus = evaluateDrawStatus();

        if (drawStatus.isDeadPosition || drawStatus.onlyKingsLeft || drawStatus.isFiftyRule) {
            moveDescriptions.add(AUTOMATIC_DRAW);
        } else {
            generateMoveDescriptions(color, moveDescriptions);
        }

        if (moveDescriptions.isEmpty()) {
            moveDescriptions.add(kingInCheck ? CHECKMATE : STALEMATE);
        }

        return moveDescriptions;
    }

    private boolean checkIfPawnsUnmoved() {
        for (int col = 0; col < 8; col++) {
            Piece whitePawn = getSquares()[6][col];
            Piece blackPawn = getSquares()[1][col];
            if (isPawnNotInStartingPosition(whitePawn, Color.WHITE) || isPawnNotInStartingPosition(blackPawn,
                    Color.BLACK)) {
                return false;
            }
        }
        return true;
    }

    private boolean isPawnNotInStartingPosition(Piece piece, Color color) {
        return piece == null || piece.getType() != PieceType.PAWN || piece.getColor() != color;
    }

    private DrawStatus evaluateDrawStatus() {
        int whiteMinorPieceCount = countMinorPieces(Color.WHITE);
        int blackMinorPieceCount = countMinorPieces(Color.BLACK);

        boolean whiteHasOtherPiece = hasOtherPiece(Color.WHITE);
        boolean blackHasOtherPiece = hasOtherPiece(Color.BLACK);
        boolean pawnsUnmoved = checkIfPawnsUnmoved();

        boolean onlyKingsLeft = checkOnlyKingsLeft(whiteMinorPieceCount, blackMinorPieceCount);
        boolean isDeadPosition = checkDeadPosition(whiteMinorPieceCount,
                blackMinorPieceCount,
                whiteHasOtherPiece,
                blackHasOtherPiece);
        boolean isFiftyRule = isFiftyMoveRuleDraw(pawnsUnmoved);

        return new DrawStatus(isDeadPosition, onlyKingsLeft, isFiftyRule);
    }

    private int countMinorPieces(Color color) {
        int minorPieceCount = 0;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == color && isNotMinorPiece(piece)) {
                    minorPieceCount++;
                }
            }
        }
        return minorPieceCount;
    }

    private boolean hasOtherPiece(Color color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == color && isNotMinorPiece(piece) && piece.getType() != PieceType.KING) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkOnlyKingsLeft(int whiteMinorPieceCount, int blackMinorPieceCount) {
        return whiteMinorPieceCount == 0 && blackMinorPieceCount == 0;
    }

    private boolean checkDeadPosition(int whiteMinorPieceCount,
                                      int blackMinorPieceCount,
                                      boolean whiteHasOtherPiece,
                                      boolean blackHasOtherPiece) {
        return (whiteMinorPieceCount <= 1 && blackMinorPieceCount <= 1 && !whiteHasOtherPiece && !blackHasOtherPiece);
    }

    private boolean isNotMinorPiece(Piece piece) {
        return piece.getType() != PieceType.BISHOP && piece.getType() != PieceType.KNIGHT;
    }

    private void generateMoveDescriptions(Color color, List<String> moveDescriptions) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPieceAt(row, col);
                if (piece != null && piece.getColor() == color) {
                    getTotalMoves(piece, color, row, col, moveDescriptions);
                }
            }
        }
    }

    private boolean isFiftyMoveRuleDraw(boolean pawnsUnmoved) {
        if (!pawnsUnmoved || EMPTY_INPUT.equals(fullmoveNumber) || EMPTY_INPUT.equals(halfmoveClock)) {
            return false;
        }

        int whitePieces = countPieces(Color.WHITE);
        int blackPieces = countPieces(Color.BLACK);

        return whitePieces == TOTAL_PIECES && blackPieces == TOTAL_PIECES &&
                Integer.parseInt(fullmoveNumber) >= FIFTY_RULE_THRESHOLD &&
                Integer.parseInt(halfmoveClock) >= FIFTY_RULE_THRESHOLD;
    }

    private int countPieces(Color color) {
        int pieceCount = 0;
        for (Piece[] row : getSquares()) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() == color) {
                    pieceCount++;
                }
            }
        }
        return pieceCount;
    }

    private void getTotalMoves(Piece piece, Color color, int row, int col, List<String> moveDescriptions) {
        if (piece != null && piece.getColor() == color) {
            List<Move> moves = piece.generateMoves(row, col, board);
            for (Move move : moves) {
                if (!moveLeavesKingInCheck(move, color)) {
                    String moveDescription = formatMoveDescription(piece, move);
                    moveDescriptions.add(moveDescription);
                }
            }
        }
    }

    public boolean moveLeavesKingInCheck(Move move, Color color) {
        Board copy = board.copy();
        Piece[][] grid = copy.getSquares();
        Piece movingPiece = grid[move.fromRow()][move.fromCol()];

        // Make the move
        grid[move.toRow()][move.toCol()] = movingPiece;
        grid[move.fromRow()][move.fromCol()] = null;

        // Find the king's position
        return isKingInCheck(copy, color);
    }

    public static void calculateMovesForBothColors(ChessMoveCalculatorService service, Piece[][] squares) {
        calculateMovesForColor(service, squares, Color.WHITE);
        calculateMovesForColor(service, squares, Color.BLACK);
    }

    public static void calculateMovesForColor(ChessMoveCalculatorService service, Piece[][] squares, Color color) {
        List<String> moveDescriptions = service.getMoves(squares, color);

        if (moveDescriptions.size() == 1 && (
                moveDescriptions.get(0).equals(CHECKMATE) ||
                        moveDescriptions.get(0).equals(STALEMATE) ||
                        moveDescriptions.get(0).equals(AUTOMATIC_DRAW))) {
            displayNoMoves(color, moveDescriptions);
        } else {
            displayMoves(color, moveDescriptions);
        }
    }

    public static String parseFEN(String[] args) {
        if (args.length == 1 && (args[0].equalsIgnoreCase(HELP_ONE_SWITCH) || args[0].equalsIgnoreCase(HELP_TWO_SWITCH))) {
            displayHelp();
            System.exit(1);
        }

        if (args.length != 1) {
            displayInvalidNumber();
            System.exit(1);
        }

        return args[0];
    }

    public Color inferActiveColorBasedOnCheck() {
        boolean whiteKingInCheck = isKingInCheck(board, Color.WHITE);
        boolean blackKingInCheck = isKingInCheck(board, Color.BLACK);

        if (whiteKingInCheck && !blackKingInCheck) {
            // White king is in check, so it's White's turn (defending)
            return Color.WHITE;
        } else if (blackKingInCheck && !whiteKingInCheck) {
            // Black king is in check, so it's Black's turn (defending)
            return Color.BLACK;
        } else {
            // Neither king is in check
            return null; // It'll display moves for both colors
        }
    }

    /**
     * Executes the chess move calculation process based on the provided FEN (Forsyth-Edwards Notation) input.
     * This method initializes the ChessMoveCalculatorService with a validated FEN, displays the board settings,
     * calculates possible moves, and displays the results based on the active player's color (if specified).
     *
     * @param args Command-line arguments, where the first argument should be the FEN string.
     *             Additional arguments will be ignored.
     */
    public static void runCalculation(String[] args) {
        try {
            String fen = parseFEN(args);
            ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

            displayStandardSettings(service.getFen());

            if (service.isFullMode()) {
                displayAdditionalSettings(service.getActiveColor(), service.getWhiteCastlingRights(),
                        service.getBlackCastlingRights(), service.getEnPassantTarget(), service.getHalfmoveClock(),
                        service.getFullmoveNumber());
            }

            Board board = service.getBoard();

            displayBoard(board.drawBoard());

            calculateMoves(service, board);

        } catch (InvalidFENException e) {
            displayError(e);
            System.exit(1);
        }
    }

    private static void calculateMoves(ChessMoveCalculatorService service, Board board) throws InvalidFENException {
        if (service.getActiveColor().equals(EMPTY_INPUT)) {
            // Active player not given
            Color inferredActiveColor = service.inferActiveColorBasedOnCheck();
            if (inferredActiveColor != null) {
                // Only display moves for the defending player
                calculateMovesForColor(service, board.getSquares(), inferredActiveColor);
            } else {
                // Neither king is in check, display moves for both colors
                calculateMovesForBothColors(service, board.getSquares());
            }
        } else {
            // Active player given, display moves for that player
            Color activeColor = Color.fromName(service.getActiveColor());
            calculateMovesForColor(service, board.getSquares(), activeColor);
        }
    }

    public String getActiveColor() {
        return activeColor;
    }

    public String getHalfmoveClock() {
        return halfmoveClock;
    }

    public String getFullmoveNumber() {
        return fullmoveNumber;
    }

    public String getFen() {
        return fen;
    }

    public Piece[][] getSquares() {
        return squares;
    }

    public String getEnPassantTarget() {
        return enPassantTarget;
    }

    public boolean isFullMode() {
        return fullMode;
    }

    public Set<String> getWhiteCastlingRights() {
        return whiteCastlingRights;
    }

    public Set<String> getBlackCastlingRights() {
        return blackCastlingRights;
    }

    public Board getBoard() {
        return board;
    }

    private static class DrawStatus {
        boolean isDeadPosition;
        boolean onlyKingsLeft;
        boolean isFiftyRule;

        DrawStatus(boolean isDeadPosition, boolean onlyKingsLeft, boolean isFiftyRule) {
            this.isDeadPosition = isDeadPosition;
            this.onlyKingsLeft = onlyKingsLeft;
            this.isFiftyRule = isFiftyRule;
        }
    }
}

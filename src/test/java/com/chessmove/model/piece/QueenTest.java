package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testGenerateMoves_EmptyBoard() throws InvalidFENException {
        String fen = "6K1/8/8/8/3Q4/8/8/7k"; // Queen on d4
        Board board = createBoardFromFEN(fen);

        // Get the Queen on d4
        Piece queen = board.getPieceAt(4, 3);
        assertNotNull(queen);
        assertInstanceOf(Queen.class, queen);

        // Generate moves for the Queen
        List<Move> moves = queen.generateMoves(4, 3, board);

        // Expecting 27 moves (Rook + Bishop-like moves)
        assertEquals(27, moves.size(), "Queen should generate 27 valid moves on an empty board");
    }

    @Test
    void testGenerateMoves_BlockedByFriendlyPiece() throws InvalidFENException {
        // Set up a board where a Queen is at d4 (3,3) and has friendly pieces around it
        String fen = "6K1/8/8/8/3PQ3/8/8/k7"; // Queen on d4, with friendly Pawn blocking
        Board board = createBoardFromFEN(fen);

        // Get the Queen on d4
        Piece queen = board.getPieceAt(4, 4);
        assertNotNull(queen);
        assertInstanceOf(Queen.class, queen);

        // Generate moves for the Queen
        List<Move> moves = queen.generateMoves(4, 4, board);

        // Expecting 23 moves (friendly Pawn blocks some moves)
        assertEquals(23, moves.size(), "Queen should generate 23 valid moves when a friendly piece is blocking it");
    }

    @Test
    void testGenerateMoves_TakesEnemyPiece() throws InvalidFENException {
        // Set up a board where a Queen can capture enemy pieces diagonally or vertically/horizontally
        String fen = "8/7k/8/4p3/3Qp3/8/8/7K"; // Queen on d4, with enemy pawns on c4 and e4
        Board board = createBoardFromFEN(fen);

        // Get the Queen on d4
        Piece queen = board.getPieceAt(4, 3);
        assertNotNull(queen);
        assertInstanceOf(Queen.class, queen);

        // Generate moves for the Queen
        List<Move> moves = queen.generateMoves(4, 3, board);

        // Expecting 21 moves (the Queen can capture the enemy pawns like a Bishop and also like a Rook)
        assertEquals(21, moves.size(), "Queen should generate 21 valid moves and can capture enemy pieces");
    }

    @Test
    void testCanAttackSquare() throws InvalidFENException {
        // Set up a board where a Queen is at d4 (3,3) and test attack range
        String fen = "8/7k/8/8/3Q4/8/8/7K"; // Queen on d4
        Board board = createBoardFromFEN(fen);

        // Get the Queen on d4
        Piece queen = board.getPieceAt(4, 3);
        assertNotNull(queen);
        assertInstanceOf(Queen.class, queen);

        // Test attack on valid rook and bishop moves
        assertTrue(queen.canAttackSquare(4, 3, 7, 3, board)); // d4 to d7 (Rook-like)
        assertTrue(queen.canAttackSquare(4, 3, 1, 6, board)); // d4 to g1 (Bishop-like)
    }

    @Test
    void testCannotAttackTooFar() throws InvalidFENException {
        // Set up a board where a Queen is at d4 (3,3) and test attack out of range
        String fen = "8/7k/8/8/3Q4/8/8/7K"; // Queen on d4
        Board board = createBoardFromFEN(fen);

        // Get the Queen on d4
        Piece queen = board.getPieceAt(4, 3);
        assertNotNull(queen);
        assertInstanceOf(Queen.class, queen);

        // Test invalid attack (out of range for both rook and bishop-like moves)
        assertFalse(queen.canAttackSquare(4, 3, 8, 6, board)); // d4 to g7 (out of range for both rook and bishop)
        assertFalse(queen.canAttackSquare(4, 3, 0, 0, board)); // d4 to a1 (blocked by board edge)
    }

}
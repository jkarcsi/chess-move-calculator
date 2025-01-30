package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testGenerateMoves_EmptyBoard() throws InvalidFENException {
        // Set up a board where a white Pawn is at d2 (6, 3) with no other pieces
        String fen = "7K/8/8/7k/8/8/3P4/8"; // Pawn on d2
        Board board = createBoardFromFEN(fen);

        // Get the Pawn on d2
        Piece pawn = board.getPieceAt(6, 3);
        assertNotNull(pawn);
        assertInstanceOf(Pawn.class, pawn);

        // Generate moves for the Pawn
        List<Move> moves = pawn.generateMoves(6, 3, board);

        // Expecting 2 moves (single move and double move from starting position)
        assertEquals(2, moves.size(), "Pawn should generate 2 valid moves from starting position");
        assertTrue(moves.contains(new Move(6, 3, 5, 3))); // d2 to d3
        assertTrue(moves.contains(new Move(6, 3, 4, 3))); // d2 to d4 (double move)
    }

    @Test
    void testGenerateMoves_DoubleMoveFromStartingPosition() throws InvalidFENException {
        // Set up a board where a black Pawn is at d7 (1, 3) with no other pieces
        String fen = "7K/8/7k/8/8/8/8/3p4"; // Black Pawn on d7
        Board board = createBoardFromFEN(fen);

        // Get the Pawn on d7
        Piece pawn = board.getPieceAt(7, 3);
        assertNotNull(pawn);
        assertInstanceOf(Pawn.class, pawn);

        // Generate moves for the Pawn
        List<Move> moves = pawn.generateMoves(1, 3, board);

        // Expecting 2 moves (single move and double move from starting position)
        assertEquals(2, moves.size(), "Black Pawn should generate 2 valid moves from starting position");
        assertTrue(moves.contains(new Move(1, 3, 2, 3))); // d7 to d6
        assertTrue(moves.contains(new Move(1, 3, 3, 3))); // d7 to d5 (double move)
    }

    @Test
    void testGenerateMoves_BlockedByPiece() throws InvalidFENException {
        // Set up a board where a white Pawn is blocked by another piece on d3
        String fen = "7K/8/7k/8/8/3P4/3P4/8"; // White Pawns on d3 and d2
        Board board = createBoardFromFEN(fen);

        // Get the Pawn on d2
        Piece pawn = board.getPieceAt(6, 3);
        assertNotNull(pawn);
        assertInstanceOf(Pawn.class, pawn);

        // Generate moves for the Pawn
        List<Move> moves = pawn.generateMoves(6, 3, board);

        // Expecting 0 moves (the Pawn is blocked)
        assertEquals(0, moves.size(), "Pawn should generate 0 valid moves when blocked by another piece");
    }

    @Test
    void testGenerateMoves_TakesEnemyPiece() throws InvalidFENException {
        // Set up a board where a white Pawn can take an enemy piece diagonally
        String fen = "7K/8/7k/8/3p4/4P3/8/8"; // Black Pawn on d4 and white Pawn on e3
        Board board = createBoardFromFEN(fen);

        // Get the White Pawn on e2
        Piece pawn = board.getPieceAt(5, 4);
        assertNotNull(pawn);
        assertInstanceOf(Pawn.class, pawn);

        // Generate moves for the Pawn
        List<Move> moves = pawn.generateMoves(5, 4, board);

        // Expecting 2 moves (one forward and one capture)
        assertEquals(2, moves.size(), "Pawn should generate 2 valid moves (one forward, one capture)");
        assertTrue(moves.contains(new Move(5, 4, 4, 4))); // e3 to e4
        assertTrue(moves.contains(new Move(5, 4, 4, 3))); // e3 to d4 (capture)
    }

    @Test
    void testCanAttackSquare() throws InvalidFENException {
        String fen = "7K/8/7k/8/8/3p4/4P3/8"; // White Pawn on e2, black Pawn on d3
        Board board = createBoardFromFEN(fen);

        // Get the White Pawn on e2
        Piece pawn = board.getPieceAt(6, 4);
        assertNotNull(pawn);
        assertInstanceOf(Pawn.class, pawn);

        // Test attack on the diagonal (d3)
        assertTrue(pawn.canAttackSquare(6, 4, 5, 3, board)); // e2 to d3

        // Test invalid attack on a non-diagonal square
        assertFalse(pawn.canAttackSquare(6, 4, 5, 4, board)); // e2 to e3 (not a capture move)
    }

    @Test
    void testCannotCaptureKing() throws InvalidFENException {
        String fen = "7K/8/8/8/8/3kP3/8/8"; // Black King on d3, White Pawn on e3
        Board board = createBoardFromFEN(fen);

        // Get the White Pawn on e2
        Piece pawn = board.getPieceAt(5, 4);
        assertNotNull(pawn);
        assertInstanceOf(Pawn.class, pawn);

        // Generate moves for the Pawn
        List<Move> moves = pawn.generateMoves(5, 4, board);

        // Expecting only 1 move (the Pawn can't capture the King)
        assertEquals(1, moves.size(), "Pawn should only generate 1 valid move as it can't capture the King");
        assertTrue(moves.contains(new Move(5, 4, 4, 4))); // e3 to e4
    }

}
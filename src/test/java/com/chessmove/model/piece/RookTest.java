package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testGenerateMoves_EmptyBoard() throws InvalidFENException {
        // Set up a board where a Rook is at d4 (3,3) with no other pieces
        String fen = "7K/8/8/8/3R4/8/8/7k"; // Rook on d4
        Board board = createBoardFromFEN(fen);

        // Get the Rook on d4
        Piece rook = board.getPieceAt(4, 3);
        assertNotNull(rook);
        assertInstanceOf(Rook.class, rook);

        // Generate moves for the Rook
        List<Move> moves = rook.generateMoves(4, 3, board);

        // Expecting 14 moves (horizontal and vertical)
        assertEquals(14, moves.size(), "Rook should generate 14 valid moves on an empty board");
    }

    @Test
    void testGenerateMoves_BlockedByFriendlyPiece() throws InvalidFENException {
        // Set up a board where a Rook is at d4 (3,3) and has friendly pieces around it
        String fen = "7K/8/8/8/3RP3/8/8/7k"; // Rook on d4, with friendly Pawn blocking
        Board board = createBoardFromFEN(fen);

        // Get the Rook on d4
        Piece rook = board.getPieceAt(4, 3);
        assertNotNull(rook);
        assertInstanceOf(Rook.class, rook);

        // Generate moves for the Rook
        List<Move> moves = rook.generateMoves(4, 3, board);

        // Expecting fewer moves due to the blocking friendly piece
        assertEquals(10, moves.size(), "Rook should generate 10 valid moves when blocked by a friendly piece");
    }

    @Test
    void testGenerateMoves_TakesEnemyPiece() throws InvalidFENException {
        // Set up a board where a Rook can capture enemy pieces
        String fen = "7K/8/8/8/3Rp3/8/8/7k"; // Rook on d4, with enemy Pawn on e4
        Board board = createBoardFromFEN(fen);

        // Get the Rook on d4
        Piece rook = board.getPieceAt(4, 3);
        assertNotNull(rook);
        assertInstanceOf(Rook.class, rook);

        // Generate moves for the Rook
        List<Move> moves = rook.generateMoves(4, 3, board);

        // Expecting 13 moves (Rook can capture the enemy pawn)
        assertEquals(11, moves.size(), "Rook should generate 11 valid moves and can capture enemy pieces");
        assertTrue(moves.contains(new Move(4, 3, 4, 4))); // d4 to e4 (capture)
    }

    @Test
    void testCanAttackSquare() throws InvalidFENException {
        // Set up a board where a Rook is at d4 (3,3) and test attack range
        String fen = "7K/8/8/8/3R4/8/8/7k"; // Rook on d4
        Board board = createBoardFromFEN(fen);

        // Get the Rook on d4
        Piece rook = board.getPieceAt(4, 3);
        assertNotNull(rook);
        assertInstanceOf(Rook.class, rook);

        // Test attack on valid Rook moves
        assertTrue(rook.canAttackSquare(4, 3, 4, 7, board)); // d4 to h4
        assertTrue(rook.canAttackSquare(4, 3, 0, 3, board)); // d4 to d1
    }

    @Test
    void testCannotAttackTooFar() throws InvalidFENException {
        // Set up a board where a Rook is at d4 (3,3) and test attack out of range
        String fen = "7K/8/8/8/3R4/8/8/7k"; // Rook on d4
        Board board = createBoardFromFEN(fen);

        // Get the Rook on d4
        Piece rook = board.getPieceAt(4, 3);
        assertNotNull(rook);
        assertInstanceOf(Rook.class, rook);

        // Test invalid attack (out of range for a Rook)
        assertFalse(rook.canAttackSquare(4, 3, 7, 7, board)); // d4 to h8 (invalid for Rook)
        assertFalse(rook.canAttackSquare(4, 3, 3, 7, board)); // c4 to h4 (blocked)
    }

}
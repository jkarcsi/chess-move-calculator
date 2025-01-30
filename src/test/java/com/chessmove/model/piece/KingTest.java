package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KingTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testGenerateMoves_EmptyBoard() throws InvalidFENException {
        // Set up a board where a King is at d4 (3,3) with no other pieces
        String fen = "7k/8/8/8/3K4/8/8/8"; // King on d4
        Board board = createBoardFromFEN(fen);

        // Get the King on d4
        Piece king = board.getPieceAt(4, 3);
        assertNotNull(king);
        assertInstanceOf(King.class, king);

        // Generate moves for the King
        List<Move> moves = king.generateMoves(4, 3, board);

        // Expecting 8 moves (all adjacent squares are empty)
        assertEquals(8, moves.size(), "King should generate 8 valid moves on an empty board");
    }

    @Test
    void testGenerateMoves_BlockedByFriendlyPiece() throws InvalidFENException {
        String fen = "7k/8/8/8/2PKP3/8/8/8"; // King on d4, with Pawns blocking it
        Board board = createBoardFromFEN(fen);

        // Get the King on d4
        Piece king = board.getPieceAt(4, 3);
        assertNotNull(king);
        assertInstanceOf(King.class, king);

        // Generate moves for the King
        List<Move> moves = king.generateMoves(4, 3, board);

        // Expecting only 5 moves (3 squares blocked by friendly pawns)
        assertEquals(6, moves.size(), "King should generate 6 valid moves with friendly pieces blocking some moves");
    }

    @Test
    void testGenerateMoves_TakesEnemyPiece() throws InvalidFENException {
        // Set up a board where a King is at d4 (3,3) and has enemy pieces around it
        String fen = "7k/8/8/8/2pKp3/8/8/8"; // King on d4, with enemy Pawns around it
        Board board = createBoardFromFEN(fen);

        // Get the King on d4
        Piece king = board.getPieceAt(4, 3);
        assertNotNull(king);
        assertInstanceOf(King.class, king);

        // Generate moves for the King
        List<Move> moves = king.generateMoves(4, 3, board);

        // Expecting 7 moves (the King can capture enemy pieces, but do not leave in check)
        assertEquals(7, moves.size(), "King should generate 7 valid moves with enemy pieces around it");
    }

    @Test
    void testCanAttackSquare() throws InvalidFENException {
        // Set up a board where a King is at d4 (3,3) and test attack range
        String fen = "7k/8/8/8/3K4/8/8/8"; // King on d4
        Board board = createBoardFromFEN(fen);

        // Get the King on d4
        Piece king = board.getPieceAt(4, 3);
        assertNotNull(king);
        assertInstanceOf(King.class, king);

        // Test attack on adjacent squares
        assertTrue(king.canAttackSquare(4, 3, 5, 3, board)); // d4 to d5
        assertTrue(king.canAttackSquare(4, 3, 3, 3, board)); // d4 to d3
        assertTrue(king.canAttackSquare(4, 3, 4, 2, board)); // d4 to c4
        assertTrue(king.canAttackSquare(4, 3, 4, 4, board)); // d4 to e4
    }

    @Test
    void testCannotAttackTooFar() throws InvalidFENException {
        // Set up a board where a King is at d4 (3,3) and test attack out of range
        String fen = "7k/8/8/8/3K4/8/8/8"; // King on d4
        Board board = createBoardFromFEN(fen);

        // Get the King on d4
        Piece king = board.getPieceAt(4, 3);
        assertNotNull(king);
        assertInstanceOf(King.class, king);

        // Test attack out of range
        assertFalse(king.canAttackSquare(4, 3, 6, 3, board)); // d4 to d6 (too far)
        assertFalse(king.canAttackSquare(4, 3, 4, 5, board)); // d4 to f4 (too far)
    }

}
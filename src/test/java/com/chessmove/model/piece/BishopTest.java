package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testGenerateMoves_EmptyBoard() throws InvalidFENException {
        // Set up a board where a Bishop is at d4 (column 3), row index 4
        String fen = "K7/8/8/8/3B4/8/7k/8"; // Bishop on d4
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4 (index [4][3])
        Piece bishop = board.getSquares()[4][3];

        // Ensure the Bishop is correctly placed
        assertNotNull(bishop, "Bishop should not be null");
        assertInstanceOf(Bishop.class, bishop, "Piece should be a Bishop");

        // Generate moves for the Bishop
        List<Move> moves = bishop.generateMoves(4, 3, board);

        // Expecting Bishop to move along diagonals (total 13 moves)
        assertEquals(13, moves.size(), "Bishop should generate 13 valid moves on an empty board");

        // Verify that some example moves are included
        assertTrue(moves.contains(new Move(4, 3, 7, 0))); // Move to a7
        assertTrue(moves.contains(new Move(4, 3, 1, 0))); // Move to a3
    }

    @Test
    void testGenerateMoves_BlockedByFriendlyPiece() throws InvalidFENException {
        String fen = "K7/8/5B2/8/3B4/8/8/k7"; // Bishop on d4 and friendly Bishop on f6
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4
        Piece bishop = board.getSquares()[4][3];
        assertNotNull(bishop);
        assertInstanceOf(Bishop.class, bishop);

        // Generate moves for the Bishop
        List<Move> moves = bishop.generateMoves(4, 3, board);

        // Bishop should be blocked by the friendly piece at f6
        assertEquals(9, moves.size(), "Bishop should generate fewer moves when blocked by friendly piece");
        assertFalse(moves.contains(new Move(4, 3, 2, 5)), "Bishop should not be able to move to f6 (blocked by friendly piece)");
    }

    @Test
    void testGenerateMoves_BlockedByEnemyPiece() throws InvalidFENException {
        // Set up a board where a Bishop is at d4 and an enemy piece is at f6
        String fen = "7K/8/5b2/8/3B4/8/8/7k"; // Bishop on d4 and enemy Bishop on f6
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4
        Piece bishop = board.getSquares()[4][3];
        assertNotNull(bishop);
        assertInstanceOf(Bishop.class, bishop);

        // Generate moves for the Bishop
        List<Move> moves = bishop.generateMoves(4, 3, board);

        // Bishop can capture the enemy piece at f6
        assertEquals(11, moves.size(), "Bishop should generate moves including capture of enemy piece");
        assertTrue(moves.contains(new Move(4, 3, 6, 5)), "Bishop should be able to capture enemy piece at f6");
    }

    @Test
    void testCanAttackSquare_ValidAttack() throws InvalidFENException {
        String fen = "7K/8/5b2/8/3B4/8/8/7k"; // Bishop on d4 and enemy Bishop on f6
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4
        Piece bishop = board.getSquares()[4][3];
        assertNotNull(bishop);
        assertInstanceOf(Bishop.class, bishop);

        // Check if Bishop can attack f6
        assertTrue(bishop.canAttackSquare(4, 3, 6, 5, board), "Bishop should be able to attack enemy piece on f6");
    }

    @Test
    void testCanAttackSquare_InvalidAttack() throws InvalidFENException {
        String fen = "7K/8/5b2/8/3B4/8/8/7k"; // Bishop on d4 and enemy Bishop on f6
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4
        Piece bishop = board.getSquares()[4][3];
        assertNotNull(bishop);
        assertInstanceOf(Bishop.class, bishop);

        // Check if Bishop can attack g7, which is blocked
        assertFalse(bishop.canAttackSquare(4, 3, 1, 6, board), "Bishop should not be able to attack g7 because it's blocked by enemy on f6");
    }

    @Test
    void testCanAttackSquare_EmptyPath() throws InvalidFENException {
        String fen = "7K/8/8/8/3B4/8/8/7k"; // Bishop on d4
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4
        Piece bishop = board.getSquares()[4][3];
        assertNotNull(bishop);
        assertInstanceOf(Bishop.class, bishop);

        // Check if Bishop can attack an empty square f6 (6,5)
        assertTrue(bishop.canAttackSquare(4, 3, 6, 5, board), "Bishop should be able to attack empty square f6");
    }

    @Test
    void testCanAttackSquare_DifferentRowColDiff() throws InvalidFENException {
        String fen = "7K/8/8/8/3B4/8/8/7k"; // Bishop on d4
        Board board = createBoardFromFEN(fen);

        // Get the Bishop on d4
        Piece bishop = board.getSquares()[4][3];
        assertNotNull(bishop);
        assertInstanceOf(Bishop.class, bishop);

        // Check invalid attack (not a diagonal)
        assertFalse(bishop.canAttackSquare(4, 3, 4, 5, board), "Bishop should not be able to attack non-diagonal square");
    }
}

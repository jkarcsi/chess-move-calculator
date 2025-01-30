package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testGenerateMoves_EmptyBoard() throws InvalidFENException {
        String fen = "7K/8/8/8/3N4/8/8/7k"; // Knight on d4
        Board board = createBoardFromFEN(fen);

        // Get the Knight on d4
        Piece knight = board.getPieceAt(4, 3);
        assertNotNull(knight);
        assertInstanceOf(Knight.class, knight);

        // Generate moves for the Knight
        List<Move> moves = knight.generateMoves(4, 3, board);

        // Expecting 8 moves (the Knight can move to 8 different squares)
        assertEquals(8, moves.size(), "Knight should generate 8 valid moves on an empty board");
    }

    @Test
    void testGenerateMoves_BlockedByFriendlyPiece() throws InvalidFENException {
        String fen = "7K/8/8/8/3N4/5P2/8/7k"; // Knight on d4, with friendly Pawn blocking one move
        Board board = createBoardFromFEN(fen);

        // Get the Knight on d4
        Piece knight = board.getPieceAt(4, 3);
        assertNotNull(knight);
        assertInstanceOf(Knight.class, knight);

        // Generate moves for the Knight
        List<Move> moves = knight.generateMoves(4, 3, board);

        // Expecting 7 moves (1 square blocked by a friendly pawn)
        assertEquals(7, moves.size(), "Knight should generate 7 valid moves when one move is blocked by a friendly piece");
    }

    @Test
    void testGenerateMoves_TakesEnemyPiece() throws InvalidFENException {
        String fen = "7K/8/8/8/3nP3/8/8/7k"; // Knight on d4, with enemy Pawn around it
        Board board = createBoardFromFEN(fen);

        // Get the Knight on d4
        Piece knight = board.getPieceAt(4, 3);
        assertNotNull(knight);
        assertInstanceOf(Knight.class, knight);

        // Generate moves for the Knight
        List<Move> moves = knight.generateMoves(4, 3, board);

        // Expecting 8 moves (the Knight can move to 8 different squares and capture enemy pieces)
        assertEquals(8, moves.size(), "Knight should generate 8 valid moves and can capture enemy pieces");
    }

    @Test
    void testCanAttackSquare() throws InvalidFENException {
        String fen = "7K/8/8/8/3N4/8/8/7k"; // Knight on d4
        Board board = createBoardFromFEN(fen);

        // Get the Knight on d4
        Piece knight = board.getPieceAt(4, 3);
        assertNotNull(knight);
        assertInstanceOf(Knight.class, knight);

        // Test attack on valid knight moves
        assertTrue(knight.canAttackSquare(4, 3, 6, 2, board)); // d4 to b6
        assertTrue(knight.canAttackSquare(4, 3, 6, 4, board)); // d4 to e6
        assertTrue(knight.canAttackSquare(4, 3, 2, 2, board)); // d4 to b2
        assertTrue(knight.canAttackSquare(4, 3, 2, 4, board)); // d4 to e2
    }

    @Test
    void testCannotAttackTooFar() throws InvalidFENException {
        String fen = "7K/8/8/8/3N4/8/8/7k"; // Knight on d4
        Board board = createBoardFromFEN(fen);

        // Get the Knight on d4
        Piece knight = board.getPieceAt(4, 3);
        assertNotNull(knight);
        assertInstanceOf(Knight.class, knight);

        // Test attack out of range (Knight cannot move here)
        assertFalse(knight.canAttackSquare(4, 3, 4, 5, board)); // d4 to f4 (invalid knight move)
        assertFalse(knight.canAttackSquare(4, 3, 7, 3, board)); // d4 to d7 (invalid knight move)
    }

}
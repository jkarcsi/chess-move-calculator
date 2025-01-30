package com.chessmove.model;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.service.ChessMoveCalculatorService;
import com.chessmove.util.Color;
import com.chessmove.util.FENValidator;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    // Helper method to create a Board from a FEN string
    private Board createBoardFromFEN(String fen) throws InvalidFENException {
        FENValidator validator = new FENValidator(fen);
        return validator.getBoard();
    }

    @Test
    void testValidFENString() {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        assertDoesNotThrow(() -> new ChessMoveCalculatorService(fen), "Valid FEN should not throw exception");
    }

    @Test
    void testInvalidFENString_RankCount() {
        String invalidFEN = "rnbqkbnr/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR"; // Only 7 ranks
        InvalidFENException exception = assertThrows(InvalidFENException.class, () -> new ChessMoveCalculatorService(invalidFEN));
        assertTrue(exception.getMessage().contains("Incorrect number of ranks"), "Should detect incorrect rank count");
    }

    @Test
    void testInvalidFENString_TooManyPawns() {
        String invalidFEN = "rnbqkbnr/pppppppp/pppppppp/8/8/8/PPPPPPPP/RNBQKBNR"; // 16 pawns
        InvalidFENException exception = assertThrows(InvalidFENException.class, () -> new ChessMoveCalculatorService(invalidFEN));
        assertTrue(exception.getMessage().contains("Too many pawns"), "Should detect too many pawns");
    }

    @Test
    void testInvalidFENString_NoKing() {
        String invalidFEN = "rnbq1bnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQ1BNR"; // No kings
        InvalidFENException exception = assertThrows(InvalidFENException.class, () -> new ChessMoveCalculatorService(invalidFEN));
        assertTrue(exception.getMessage().contains("Invalid number of kings"), "Should detect there are no kings on board");
    }

    @Test
    void testKingPosition_Valid() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);
        Board board = service.getBoard();

        int[] kingPosition = FENValidator.findKingPosition(board, Color.WHITE);

        assertEquals(7, kingPosition[0], "White king should be at row 7");
        assertEquals(4, kingPosition[1], "White king should be at column 4 (e)");
    }

    @Test
    void testCountWhiteMoves_InitialPosition() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        List<String> whiteMoves = service.getMoves(service.getSquares(), Color.WHITE);
        assertEquals(20, whiteMoves.size(), "White should have 20 possible moves in the initial position");
    }

    @Test
    void testMoveLeavesKingInCheck_False() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        Move move = new Move(6, 4, 4, 4); // Move white pawn from e2 to e4
        boolean leavesKingInCheck = service.moveLeavesKingInCheck(move, Color.WHITE);
        assertFalse(leavesKingInCheck, "This move should not leave the king in check");
    }

    @Test
    void testMoveLeavesKingInCheck_True() throws InvalidFENException {
        String fen = "bnrbkn1r/pppppppp/8/8/1q6/8/PPPPPPPP/BNRBKNQR";
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        Move move = new Move(6, 3, 5, 3); // Move white pawn from d2 to d3
        boolean leavesKingInCheck = service.moveLeavesKingInCheck(move, Color.WHITE);
        assertTrue(leavesKingInCheck, "This move should leave the king in check");
    }

    @Test
    void testSquareUnderAttack() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Board board = createBoardFromFEN(fen);

        // Check if black queen's starting square is under attack by white
        boolean isUnderAttack = board.squareUnderAttack(0, 3, Color.WHITE); // d8 square
        assertFalse(isUnderAttack, "Black queen's starting square should not be under attack initially");
    }

    @Test
    void testIsEmpty() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Board board = createBoardFromFEN(fen);
        assertTrue(board.isEmpty(4, 4), "The center of the board (e4) should be empty in the initial position");
    }

    @Test
    void testIsEnemyPiece() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        Board board = createBoardFromFEN(fen);

        assertTrue(board.isEnemyPiece(0, 0, Color.WHITE), "Black rook at a8 should be an enemy piece for white");
        assertFalse(board.isEnemyPiece(7, 0, Color.WHITE), "White rook at a1 should not be an enemy piece for white");
    }
}

package com.chessmove.application;

import com.chessmove.model.game.Board;
import com.chessmove.model.piece.Piece;
import com.chessmove.service.ChessMoveCalculatorService;
import com.chessmove.util.Color;
import com.chessmove.util.InvalidFENException;
import com.chessmove.util.LoggerUtility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChessMoveCalculatorTest {

    @Test
    void testValidFEN() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"; // Initial chess position

        // Initialize the service with the FEN
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Get the squares and board
        Piece[][] squares = service.getSquares();
        Board board = service.getBoard();

        // Simulate calculating moves and drawing table
        List<String> moves = service.getMoves(squares, Color.WHITE);

        // Drawing table
        String boardDrawing = board.drawBoard();

        // Assertions
        assertNotNull(boardDrawing);
        assertEquals(20, moves.size(), "Expected 20 possible moves for white in the initial position");
    }

    @Test
    void testInvalidFEN() {
        String invalidFen = "invalid_fen_code"; // Invalid FEN

        // Simulate the exception when creating the service
        Exception exception = assertThrows(InvalidFENException.class, () -> new ChessMoveCalculatorService(invalidFen));

        // Assertions
        assertEquals("Incorrect number of ranks. Expected 8 ranks separated by '/'.", exception.getMessage(), "Expected Invalid FEN code error");
    }

    @Test
    void testHelpArgument() {
        String[] args = {"-h"};

        // Mock the logger
        Logger logger = Mockito.mock(Logger.class);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        // Simulate the help argument check
        if (args.length != 1 || args[0].equals("-h") || args[0].equals("--help")) {
            logger.info(LoggerUtility.logHelpMessage());
        }

        // Capture the argument and verify
        verify(logger).info(argumentCaptor.capture());
        assertEquals(LoggerUtility.logHelpMessage(), argumentCaptor.getValue());
    }

    @Test
    void testMoveCountCalculation() throws InvalidFENException {
        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR"; // Initial chess position

        // Initialize the service with the FEN
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Run the logic
        List<String> moveCount = service.getMoves(service.getSquares(), Color.WHITE);

        // Assertions
        assertEquals(20, moveCount.size(), "Expected 20 possible moves for white in the initial position");
        assertTrue(moveCount.contains("pawn from f2 to f3"), "Expected move 'pawn from f2 to f3' not found in the move list");
        assertFalse(moveCount.contains("pawn from f2 to d3"), "Wrong move 'pawn from f2 to d3' found in the move list");
    }

    @Test
    void testStalematePosition() throws InvalidFENException {
        String stalemateFEN = "QRRBBK2/PPPPPP2/8/6q1/8/8/8/7k w"; // Specifying active color
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(stalemateFEN);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Since it's a stalemate, the only entry should be "Stalemate"
        assertEquals(1, moveDescriptions.size(), "Expected only one message in move descriptions.");
        assertEquals("Stalemate", moveDescriptions.get(0), "Expected the game to be a stalemate.");
    }

    @Test
    void testCheckmatePosition() throws InvalidFENException {
        String checkmateFEN = "3RKBNR/1P1BP3/7P/P6q/8/8/8/7k w"; // Specifying active color
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(checkmateFEN);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Since it's a checkmate, the only entry should be "Checkmate"
        assertEquals(1, moveDescriptions.size(), "Expected only one message in move descriptions.");
        assertEquals("Checkmate", moveDescriptions.get(0), "Expected the game to be a checkmate.");
    }

    @Test
    void testOnlyOneMove() throws InvalidFENException {
        String fen = "QRRBBK2/PPPPPP2/7q/8/8/8/8/7k w"; // Specifying active color
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // It's not a checkmate, but only one move should be possible
        assertEquals(1, moveDescriptions.size(), "Expected only one move in move descriptions.");
        assertNotEquals("Checkmate", moveDescriptions.get(0), "Expected the game to be not a checkmate or stalemate.");
        assertNotEquals("Stalemate", moveDescriptions.get(0), "Expected the game to be not a checkmate or stalemate.");
    }

    @Test
    void testEnPassantAndCastlingInCompleteFen() throws InvalidFENException {
        String fen = "rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/R3K2R w KQkq e6 0 3";
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Assert that en-passant move is included
        assertTrue(moveDescriptions.contains("pawn from f5 to e6 (capturing pawn) (en-passant)"), "En-passant move should be available.");

        // Assert that castling moves are included
        assertTrue(moveDescriptions.contains("king from e1 to g1 (castling)"), "Kingside castling should be available.");
        assertTrue(moveDescriptions.contains("king from e1 to c1 (castling)"), "Queenside castling should be available.");
    }

    @Test
    void testEnPassantAndCastlingNotInIncompleteFen() throws InvalidFENException {
        String fen = "rnbqkbnr/pppp1ppp/8/4pP2/8/8/PPPP1PPP/R3K2R w - - 0 3";
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Assert that en-passant move is not included
        assertFalse(moveDescriptions.contains("pawn from f5 to e6 (capturing pawn) (en-passant)"), "En-passant move should not be available.");

        // Assert that castling moves are not included
        assertFalse(moveDescriptions.contains("king from e1 to g1 (castling)"), "Kingside castling should not be available.");
        assertFalse(moveDescriptions.contains("king from e1 to c1 (castling)"), "Queenside castling should not be available.");
    }

    @Test
    void testCaptureMoveWithDescription() throws InvalidFENException {
        String fen = "rnbqkbnr/ppppppp1/8/8/8/4P3/PPP1PpP1/RNBQKBNR w KQkq - 0 1"; // White rook at h1 can capture black at h8
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Assert the capture move description includes the captured piece's name
        assertTrue(moveDescriptions.contains("rook from h1 to h8 (capturing rook)"),
                "Expected capture move 'rook from h1 to h8 (capturing rook)' not found in the move list");
    }

    @Test
    void testComplexCaptureScenario() throws InvalidFENException {
        String fen = "5r2/pp3k2/5r2/q1p2Q2/3P4/6R1/PPP2PP1/1K6"; // Multiple captureable pieces for both white and black
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        // Run the logic for white and black
        List<String> whiteMoveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);
        List<String> blackMoveDescriptions = service.getMoves(service.getSquares(), Color.BLACK);

        assertTrue(whiteMoveDescriptions.contains("queen from f5 to c5 (capturing pawn)"),
                "Expected capture move 'queen from f5 to c5 (capturing pawn)' not found in the move list");
        assertTrue(whiteMoveDescriptions.contains("pawn from d4 to c5 (capturing pawn)"),
                "Expected capture move 'pawn from d4 to c5 (capturing pawn)' not found in the move list");
        assertTrue(blackMoveDescriptions.contains("rook from f6 to f5 (capturing queen)"),
                "Expected capture move 'rook from f6 to f5 (capturing queen)' not found in the move list");
        assertTrue(blackMoveDescriptions.contains("pawn from c5 to d4 (capturing pawn)"),
                "Expected capture move 'pawn from c5 to d4 (capturing pawn)' not found in the move list");
    }

    @Test
    void testBothKingsInCheck() {
        String fen = "rnb2bnr/pppp1ppp/4k3/4Q3/4q3/4K3/PPPP1PPP/RNB2BNR"; // Both kings are exposed to checks
        Exception exception = assertThrows(InvalidFENException.class, () -> new ChessMoveCalculatorService(fen));

        // Assertions
        assertEquals("Invalid FEN: Both kings are in check.", exception.getMessage(), "Expected error for both kings in check");
    }

    @Test
    void testAttackingPlayerHasMovesWhileOpponentKingInCheck() {
        // White's turn, but black king is in check and black has possible moves (invalid)
        String fen = "rnb1kbnr/pppppppp/4q3/8/8/4K3/PPPPPPPP/RNBQ1BNR b"; // White king is in check, and black moves

        Exception exception = assertThrows(InvalidFENException.class, () -> new ChessMoveCalculatorService(fen));

        // Assertions
        assertEquals("Invalid FEN: The non-active player's king is in check.", exception.getMessage(), "Expected error when non-active player's king is in check");
    }

    @Test
    void testDeadPositionKingVsKing() throws InvalidFENException {
        // Dead position: Only two kings on the board
        String deadPositionFEN = "7k/8/8/8/8/8/8/7K w - - 0 1"; // Both players only have their kings

        ChessMoveCalculatorService service = new ChessMoveCalculatorService(deadPositionFEN);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Since it's a dead position, the only entry should be "Automatic Draw"
        assertEquals(1, moveDescriptions.size(), "Expected only one message in move descriptions.");
        assertEquals("Draw", moveDescriptions.get(0), "Expected the game to be declared as a draw.");
    }

    @Test
    void testDeadPositionKingAndBishopVsKing() throws InvalidFENException {
        // Dead position: King and bishop vs. king
        String deadPositionFEN = "7K/8/8/8/8/8/7B/7k w - - 0 1"; // White has a king and bishop, Black has a king

        ChessMoveCalculatorService service = new ChessMoveCalculatorService(deadPositionFEN);

        // Run the logic
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Since it's a dead position, the only entry should be "Automatic Draw"
        assertEquals(1, moveDescriptions.size(), "Expected only one message in move descriptions.");
        assertEquals("Draw", moveDescriptions.get(0), "Expected the game to be declared as a draw.");
    }

    @Test
    void testFiftyMoveRuleDrawWithInitialPawnPosition() throws InvalidFENException {
        // FEN with all pawns in their initial positions and no captures or pawn moves for 50 moves
        String fiftyMoveRuleFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w - - 50 100"; // Pawns in starting position, 50 halfmoves

        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fiftyMoveRuleFEN);

        // Run the logic to calculate moves
        List<String> moveDescriptions = service.getMoves(service.getSquares(), Color.WHITE);

        // Since the fifty-move rule is triggered, the game should be declared a draw
        assertEquals(1, moveDescriptions.size(), "Expected only one message in move descriptions.");
        assertEquals("Draw", moveDescriptions.get(0), "Expected the game to be declared a draw by the fifty-move rule.");
    }

}

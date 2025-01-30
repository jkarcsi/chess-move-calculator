package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.MoveType;
import com.chessmove.util.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends Piece {

    public Pawn(Color color) {
        super(PieceType.PAWN, color);
    }

    @Override
    public List<Move> generateMoves(int fromRow, int fromCol, Board board) {
        List<Move> moves = new ArrayList<>();
        int direction = color == Color.WHITE ? -1 : 1;
        int startRow = color == Color.WHITE ? 6 : 1;
        int nextRow = fromRow + direction;

        // Move forward
        if (board.isEmpty(nextRow, fromCol)) {
            moves.add(new Move(fromRow, fromCol, nextRow, fromCol));
            // Double move from starting position
            if (fromRow == startRow && board.isEmpty(fromRow + 2 * direction, fromCol)) {
                moves.add(new Move(fromRow, fromCol, fromRow + 2 * direction, fromCol));
            }
        }
        // Captures
        captures(fromRow, fromCol, board, nextRow, moves);
        // En-passant captures
        if (board.getEnPassantTarget() != null) {
            enPassantCaptures(fromRow, fromCol, board, moves);
        }

        return moves;
    }

    private void captures(int fromRow, int fromCol, Board board, int nextRow, List<Move> moves) {
        int[] colOffsets = {-1, 1};
        for (int colOffset : colOffsets) {
            int newCol = fromCol + colOffset;
            if (newCol >= 0 && newCol < 8 && board.isEnemyPiece(nextRow, newCol, color)) {
                Piece targetPiece = board.getPieceAt(nextRow, newCol);
                if (null != targetPiece && targetPiece.getType() != PieceType.KING) {
                    moves.add(new Move(fromRow, fromCol, nextRow, newCol, MoveType.NORMAL, targetPiece));
                }
            }
        }
    }

    private void enPassantCaptures(int fromRow, int fromCol, Board board, List<Move> moves) {
        String enPassantTarget = board.getEnPassantTarget();
        if (!enPassantTarget.equals("-")) {
            int targetRow = 8 - Character.getNumericValue(enPassantTarget.charAt(1));
            int targetCol = enPassantTarget.charAt(0) - 'a';
            int direction = color == Color.WHITE ? -1 : 1;

            // Check that the pawn is on the correct rank for en-passant
            int enPassantRow = color == Color.WHITE ? 3 : 4;

            if (fromRow == enPassantRow && fromRow + direction == targetRow && Math.abs(fromCol - targetCol) == 1) {
                int capturedPawnRow = targetRow - direction;
                Piece capturedPawn = board.getPieceAt(capturedPawnRow, targetCol);
                moves.add(new Move(fromRow, fromCol, targetRow, targetCol, MoveType.EN_PASSANT, capturedPawn));
            }
        }
    }

    @Override
    public boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int direction = color == Color.WHITE ? -1 : 1;
        if (toRow == fromRow + direction && (toCol == fromCol + 1 || toCol == fromCol - 1)) {
            return isOccupiedByKing(toRow, toCol, board);
        }
        return false;
    }
}

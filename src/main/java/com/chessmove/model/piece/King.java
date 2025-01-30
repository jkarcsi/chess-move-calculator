package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.MoveType;
import com.chessmove.util.PieceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class King extends Piece {
    public King(Color color) {
        super(PieceType.KING, color);
    }

    @Override
    public List<Move> generateMoves(int fromRow, int fromCol, Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] deltas = {
                {1, 0}, {1, 1}, {1, -1},
                {0, 1}, {0, -1},
                {-1, 0}, {-1, 1}, {-1, -1}
        };
        for (int[] delta : deltas) {
            int newRow = fromRow + delta[0];
            int newCol = fromCol + delta[1];
            if (board.isInBounds(newRow, newCol)) {
                Piece targetPiece = board.getPieceAt(newRow, newCol);
                makeMove(fromRow, fromCol, board, targetPiece, newRow, newCol, moves);
            }
        }
        // Castling moves
        addCastlingMoves(fromRow, fromCol, board, moves);

        return moves;
    }

    private void makeMove(int fromRow,
                           int fromCol,
                           Board board,
                           Piece targetPiece,
                           int newRow,
                           int newCol,
                           List<Move> moves) {
        if ((targetPiece == null || (targetPiece.getColor() != color && targetPiece.getType() != PieceType.KING)) &&
            !board.squareUnderAttack(newRow, newCol, Color.getOppositeColor(color))) {
            if (targetPiece != null && targetPiece.getColor() != color) {
                moves.add(new Move(fromRow, fromCol, newRow, newCol, MoveType.NORMAL, targetPiece));
            } else {
                moves.add(new Move(fromRow, fromCol, newRow, newCol));
            }
        }
    }

    private void addCastlingMoves(int fromRow, int fromCol, Board board, List<Move> moves) {
        // Check if the king is in its initial position and hasn't moved
        if ((color == Color.WHITE && fromRow == 7 && fromCol == 4) ||
            (color == Color.BLACK && fromRow == 0 && fromCol == 4)) {

            // Get castling rights
            Set<String> castlingRights = color == Color.WHITE ? board.getWhiteCastlingRights() : board.getBlackCastlingRights();

            // Kingside castling
            if ((castlingRights.contains("K") || castlingRights.contains("k")) && canCastleKingSide(fromRow, fromCol, board)) {
                    moves.add(new Move(fromRow, fromCol, fromRow, fromCol + 2, MoveType.CASTLING));
            }

            // Queenside castling
            if ((castlingRights.contains("Q") || castlingRights.contains("q")) && canCastleQueenSide(fromRow, fromCol, board)) {
                    moves.add(new Move(fromRow, fromCol, fromRow, fromCol - 2, MoveType.CASTLING));
            }
        }
    }

    private boolean canCastleKingSide(int row, int col, Board board) {
        // Check if squares between king and rook are empty and not under attack
        for (int c = col + 1; c <= col + 2; c++) {
            if (!board.isEmpty(row, c) || board.squareUnderAttack(row, c, Color.getOppositeColor(color))) {
                return false;
            }
        }
        // Check that the rook is in the correct position and hasn't moved
        Piece rook = board.getPieceAt(row, col + 3);
        return rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color;
    }

    private boolean canCastleQueenSide(int row, int col, Board board) {
        // Check if squares between king and rook are empty and not under attack
        for (int c = col - 1; c >= col - 3; c--) {
            if (!board.isEmpty(row, c) || (c != col - 3 && board.squareUnderAttack(row, c, Color.getOppositeColor(color)))) {
                return false;
            }
        }
        // Check that the rook is in the correct position and hasn't moved
        Piece rook = board.getPieceAt(row, col - 4);
        return rook != null && rook.getType() == PieceType.ROOK && rook.getColor() == color;
    }

    @Override
    public boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        if (rowDiff <= 1 && colDiff <= 1 && (rowDiff + colDiff) > 0) {
            return isOccupiedByKing(toRow, toCol, board);
        }
        return false;
    }

}

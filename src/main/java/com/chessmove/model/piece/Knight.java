package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.MoveType;
import com.chessmove.util.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {

    public Knight(Color color) {
        super(PieceType.KNIGHT, color);
    }

    @Override
    public List<Move> generateMoves(int fromRow, int fromCol, Board board) {
        List<Move> moves = new ArrayList<>();
        int[][] deltas = {
                {-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
                {1, -2}, {1, 2}, {2, -1}, {2, 1}
        };
        for (int[] delta : deltas) {
            int newRow = fromRow + delta[0];
            int newCol = fromCol + delta[1];
            if (board.isInBounds(newRow, newCol)) {
                Piece targetPiece = board.getPieceAt(newRow, newCol);
                makeMove(fromRow, fromCol, targetPiece, moves, newRow, newCol);
            }
        }
        return moves;
    }

    private void makeMove(int fromRow, int fromCol, Piece targetPiece, List<Move> moves, int newRow, int newCol) {
        if (targetPiece == null || (targetPiece.getColor() != color && targetPiece.getType() != PieceType.KING)) {
            if (targetPiece != null && targetPiece.getColor() != color) {
                moves.add(new Move(fromRow, fromCol, newRow, newCol, MoveType.NORMAL, targetPiece));
            } else {
                moves.add(new Move(fromRow, fromCol, newRow, newCol));
            }
        }
    }

    @Override
    public boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
            return isOccupiedByKing(toRow, toCol, board);
        }
        return false;
    }

}

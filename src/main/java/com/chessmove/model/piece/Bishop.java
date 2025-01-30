package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.PieceType;

import java.util.List;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(PieceType.BISHOP, color);
    }

    @Override
    public List<Move> generateMoves(int fromRow, int fromCol, Board board) {
        return generateSlidingMoves(fromRow, fromCol, board, new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}});
    }

    @Override
    public boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int rowDiff = toRow - fromRow;
        int colDiff = toCol - fromCol;
        if (Math.abs(rowDiff) == Math.abs(colDiff)) {
            int rowDirection = Integer.compare(toRow, fromRow);
            int colDirection = Integer.compare(toCol, fromCol);
            int currentRow = fromRow + rowDirection;
            int currentCol = fromCol + colDirection;
            while (currentRow != toRow && currentCol != toCol) {
                if (!board.isEmpty(currentRow, currentCol)) {
                    return false;
                }
                currentRow += rowDirection;
                currentCol += colDirection;
            }
            // Check if the target square is occupied by a friendly piece
            return board.isAttackerPiece(toRow, toCol, color);
        }
        return false;
    }


}

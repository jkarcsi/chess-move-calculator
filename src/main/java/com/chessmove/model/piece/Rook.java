package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.PieceType;

import java.util.List;

public class Rook extends Piece {
    public Rook(Color color) {
        super(PieceType.ROOK, color);
    }

    @Override
    public List<Move> generateMoves(int fromRow, int fromCol, Board board) {
        return generateSlidingMoves(fromRow, fromCol, board, new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}});
    }

    @Override
    public boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        if (fromRow == toRow) {
            int colDirection = Integer.compare(toCol, fromCol);
            int currentCol = fromCol + colDirection;
            while (currentCol != toCol) {
                if (!board.isEmpty(fromRow, currentCol)) {
                    return false;
                }
                currentCol += colDirection;
            }
        } else if (fromCol == toCol) {
            int rowDirection = Integer.compare(toRow, fromRow);
            int currentRow = fromRow + rowDirection;
            while (currentRow != toRow) {
                if (!board.isEmpty(currentRow, fromCol)) {
                    return false;
                }
                currentRow += rowDirection;
            }
        } else {
            return false; // Not in the same row or column
        }
        // Check if the target square is occupied by a friendly piece
        return board.isAttackerPiece(toRow, toCol, color);
    }


}

package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.PieceType;

import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    public Queen(Color color) {
        super(PieceType.QUEEN, color);
    }

    @Override
    public List<Move> generateMoves(int fromRow, int fromCol, Board board) {
        List<Move> moves = new ArrayList<>();
        moves.addAll(generateSlidingMoves(fromRow, fromCol, board,
                new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}})); // Rook-like moves
        moves.addAll(generateSlidingMoves(fromRow, fromCol, board,
                new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}})); // Bishop-like moves
        return moves;
    }

    @Override
    public boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        // Use the canAttackSquare methods from Rook and Bishop
        Rook rook = new Rook(color);
        Bishop bishop = new Bishop(color);
        return rook.canAttackSquare(fromRow, fromCol, toRow, toCol, board) ||
               bishop.canAttackSquare(fromRow, fromCol, toRow, toCol, board);
    }
}

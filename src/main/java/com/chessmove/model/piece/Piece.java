package com.chessmove.model.piece;

import com.chessmove.model.game.Board;
import com.chessmove.model.game.Move;
import com.chessmove.util.Color;
import com.chessmove.util.MoveType;
import com.chessmove.util.PieceType;

import java.util.ArrayList;
import java.util.List;

public abstract class Piece {
    protected PieceType type;
    protected Color color;

    protected Piece(PieceType type, Color color) {
        this.type = type;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    public abstract List<Move> generateMoves(int fromRow, int fromCol, Board board);

    public abstract boolean canAttackSquare(int fromRow, int fromCol, int toRow, int toCol, Board board);

    protected List<Move> generateSlidingMoves(int fromRow, int fromCol, Board board, int[][] directions) {
        List<Move> moves = new ArrayList<>();
        for (int[] dir : directions) {
            int newRow = fromRow;
            int newCol = fromCol;
            do {
                newRow += dir[0];
                newCol += dir[1];
            } while (isLegalSlidingMove(fromRow, fromCol, board, newRow, newCol, moves));
        }
        return moves;
    }

    boolean isLegalSlidingMove(int fromRow, int fromCol, Board board, int newRow, int newCol, List<Move> moves) {
        if (!board.isInBounds(newRow, newCol)) return false;
        if (board.isEmpty(newRow, newCol)) {
            moves.add(new Move(fromRow, fromCol, newRow, newCol));
        } else {
            if (board.isEnemyPiece(newRow, newCol, color)) {
                Piece targetPiece = board.getPieceAt(newRow, newCol);
                if (null != targetPiece && targetPiece.getType() != PieceType.KING) {
                    moves.add(new Move(fromRow, fromCol, newRow, newCol, MoveType.NORMAL, targetPiece));
                }
            }
            return false;
        }
        return true;
    }

    boolean isOccupiedByKing(int toRow, int toCol, Board board) {
        Piece targetPiece = board.getPieceAt(toRow, toCol);
        return targetPiece == null || targetPiece.getType() != PieceType.KING || targetPiece.getColor() == color;
    }
}

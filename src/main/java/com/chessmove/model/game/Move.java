package com.chessmove.model.game;

import com.chessmove.model.piece.Piece;
import com.chessmove.util.MoveType;

public class Move {
    private final int fromRow;
    private final int fromCol;
    private final int toRow;
    private final int toCol;
    private final MoveType moveType;
    private final Piece capturedPiece; // New field

    public Move(int fromRow, int fromCol, int toRow, int toCol, MoveType moveType, Piece capturedPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.moveType = moveType;
        this.capturedPiece = capturedPiece;
    }

    // Overloaded constructors for existing uses
    public Move(int fromRow, int fromCol, int toRow, int toCol, MoveType moveType) {
        this(fromRow, fromCol, toRow, toCol, moveType, null);
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(fromRow, fromCol, toRow, toCol, MoveType.NORMAL, null);
    }

    // Getters
    public int fromRow() {
        return fromRow;
    }

    public int fromCol() {
        return fromCol;
    }

    public int toRow() {
        return toRow;
    }

    public int toCol() {
        return toCol;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    @Override
    public String toString() {
        return "(" + fromRow + ", " + fromCol + ") -> (" + toRow + ", " + toCol + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (fromRow != move.fromRow) return false;
        if (fromCol != move.fromCol) return false;
        if (toRow != move.toRow) return false;
        if (toCol != move.toCol) return false;
        return moveType == move.moveType;
    }

    @Override
    public int hashCode() {
        int result = fromRow;
        result = 31 * result + fromCol;
        result = 31 * result + toRow;
        result = 31 * result + toCol;
        result = 31 * result + (moveType != null ? moveType.hashCode() : 0);
        return result;
    }

}

package com.chessmove.util;

public enum PieceType {
    KING('♔', '♚'),
    QUEEN('♕', '♛'),
    ROOK('♖', '♜'),
    BISHOP('♗', '♝'),
    KNIGHT('♘', '♞'),
    PAWN('♙', '♟');

    private final char whiteSymbol;
    private final char blackSymbol;

    PieceType(char whiteSymbol, char blackSymbol) {
        this.whiteSymbol = whiteSymbol;
        this.blackSymbol = blackSymbol;
    }

    public char getSymbol(Color color) {
        return color == Color.WHITE ? whiteSymbol : blackSymbol;
    }
}

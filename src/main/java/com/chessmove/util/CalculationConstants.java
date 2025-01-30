package com.chessmove.util;

public class CalculationConstants {

    private CalculationConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String FEN_SEPARATOR = "/";
    public static final String WHITE_SPACE = "\\s+";
    public static final String DELIMITER = " ";
    public static final String LINE_BREAK = "\n";
    public static final String SEPARATOR = "|";
    public static final String EMPTY = "\u2003";
    public static final String CHECKMATE = "Checkmate";
    public static final String STALEMATE = "Stalemate";
    public static final String AUTOMATIC_DRAW = "Draw";
    public static final String HELP_ONE_SWITCH = "-h";
    public static final String HELP_TWO_SWITCH = "--help";
    public static final String EMPTY_INPUT = "-";
    public static final int FIFTY_RULE_THRESHOLD = 50;
    public static final int TOTAL_PIECES = 16;
    public static final String WHITE = "w";
    public static final String BLACK = "b";
}

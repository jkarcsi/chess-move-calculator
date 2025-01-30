package com.chessmove.util;

public class CalculationMessage {

    // Private constructor to avoid instantiating
    private CalculationMessage() {
        throw new IllegalStateException("Utility class");
    }

    public static final String FEN_STRING_IS_INCOMPLETE = "FEN string is incomplete.";
    public static final String CONSECUTIVE_DIGITS_IN_RANK = "Invalid FEN: Multiple consecutive digits in rank ";

    public static final String BOTH_KINGS_ARE_IN_CHECK = "Invalid FEN: Both kings are in check.";
    public static final String NON_ACTIVE_PLAYERS_KING_IS_IN_CHECK = "Invalid FEN: The non-active player's king is in check.";

    public static final String INCORRECT_NUMBER_OF_RANKS = "Incorrect number of ranks. Expected 8 ranks separated by '/'.";
    public static final String INCORRECT_NUMBER_OF_SQUARES_IN_RANK = "Incorrect number of squares in rank ";

    public static final String INVALID_ACTIVE_COLOR = "Invalid active color: ";
    public static final String INVALID_CASTLING_RIGHTS = "Invalid castling rights: ";
    public static final String INVALID_NUMBER_OF_EMPTY_SQUARES = "Invalid number of empty squares: ";
    public static final String INVALID_NUMBER_OF_KINGS = "Invalid number of kings for ";
    public static final String INVALID_PIECE_TYPE = "Invalid piece type: ";
    public static final String INVALID_FEN_CODE = "Invalid FEN code: {0}";
    public static final String INVALID_NUMBER_OF_ARGUMENTS = "Invalid number of arguments. Use {0} or {1} for usage instructions.";

    public static final String TOO_MANY_PIECES = "Too many pieces for ";
    public static final String TOO_MANY_SQUARES_IN_RANK = "Too many squares in rank ";
    public static final String TOO_MANY_PAWNS = "Too many pawns for ";
    public static final String TOO_MANY_PIECE = "Too many ";
    public static final String TOO_MANY_QUEENS = "Too many queens for ";
    public static final String TOO_MANY_PROMOTED_PIECES = "Too many promoted pieces for ";

    public static final String COULD_NOT_READ_HELP_MESSAGE_FROM_FILE = "Could not read help message from file: {}";
    public static final String NO_POSSIBLE_MOVES = "No possible moves for {0}: {1}";

    public static final String HELP_MESSAGE = "Please ensure the FEN code is correct. For help, use {0} or {1} argument.";
    public static final String ERROR_LOADING_HELP_MESSAGE = "Error loading help message.";

    public static final String DISPLAY_STANDARD_SETTINGS = """

            Piece placement by ranks: {0}""";
    public static final String DISPLAY_ADDITIONAL_SETTINGS = """
            Active player: {0}
            Castling side availabilities:
                For white: {1}
                For black: {2}
            Possible target for an en passant capture: {3}
            Moves since last pawn advance or piece capture: {4}
            Number of completed turns: {5}""";
    public static final String DISPLAY_BOARD = """
            Current Board:

            {0}""";
    public static final String DISPLAY_MOVES = """
            Possible moves for {0}: {1}.
            Total moves: {2}
            """;
}

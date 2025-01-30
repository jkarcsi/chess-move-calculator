package com.chessmove.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import static com.chessmove.util.CalculationConstants.HELP_ONE_SWITCH;
import static com.chessmove.util.CalculationConstants.HELP_TWO_SWITCH;
import static com.chessmove.util.CalculationConstants.LINE_BREAK;
import static com.chessmove.util.CalculationMessage.COULD_NOT_READ_HELP_MESSAGE_FROM_FILE;
import static com.chessmove.util.CalculationMessage.DISPLAY_ADDITIONAL_SETTINGS;
import static com.chessmove.util.CalculationMessage.DISPLAY_BOARD;
import static com.chessmove.util.CalculationMessage.DISPLAY_MOVES;
import static com.chessmove.util.CalculationMessage.DISPLAY_STANDARD_SETTINGS;
import static com.chessmove.util.CalculationMessage.ERROR_LOADING_HELP_MESSAGE;
import static com.chessmove.util.CalculationMessage.HELP_MESSAGE;
import static com.chessmove.util.CalculationMessage.INVALID_FEN_CODE;
import static com.chessmove.util.CalculationMessage.INVALID_NUMBER_OF_ARGUMENTS;
import static com.chessmove.util.CalculationMessage.NO_POSSIBLE_MOVES;

public class LoggerUtility {

    private static final Logger LOGGER = Logger.getLogger(LoggerUtility.class.getName());
    private static final Logger SIMPLE_LOGGER = Logger.getLogger("SimpleLogger");

    // Private constructor to avoid instantiating
    private LoggerUtility() {
        throw new IllegalStateException("Utility class");
    }

    static {
        // Configure default logger
        LOGGER.setUseParentHandlers(false);
        ConsoleHandler defaultHandler = new ConsoleHandler();
        defaultHandler.setFormatter(new SimpleFormatter()); // Use default formatting
        LOGGER.addHandler(defaultHandler);

        // Configure simple logger (no level, no timestamp)
        SIMPLE_LOGGER.setUseParentHandlers(false);
        ConsoleHandler simpleHandler = new ConsoleHandler();
        simpleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord log) {
                return formatMessage(log) + LINE_BREAK;
            }
        });
        SIMPLE_LOGGER.addHandler(simpleHandler);
    }

    public static String logHelpMessage() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(LoggerUtility.class.getResourceAsStream("/help_message.txt"))))) {
            return reader.lines().collect(Collectors.joining(LINE_BREAK));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, COULD_NOT_READ_HELP_MESSAGE_FROM_FILE, e.getMessage());
            return ERROR_LOADING_HELP_MESSAGE;
        }
    }

    public static void displayError(InvalidFENException e) {
        LOGGER.log(Level.WARNING, INVALID_FEN_CODE, e.getMessage());
        LOGGER.log(Level.WARNING,
                HELP_MESSAGE,
                new Object[]{HELP_ONE_SWITCH, HELP_TWO_SWITCH});
    }

    public static void displayInvalidNumber() {
        LOGGER.log(Level.WARNING, INVALID_NUMBER_OF_ARGUMENTS, new Object[]{HELP_ONE_SWITCH, HELP_TWO_SWITCH});
    }

    public static void displayBoard(String board) {
        SIMPLE_LOGGER.log(Level.INFO, DISPLAY_BOARD, board);
    }

    public static void displayStandardSettings(String fen) {
        SIMPLE_LOGGER.log(Level.INFO, DISPLAY_STANDARD_SETTINGS, fen);
    }

    public static void displayAdditionalSettings(String activeColor,
                                                 Set<String> whiteCastlingRights,
                                                 Set<String> blackCastlingRights,
                                                 String enPassantTarget,
                                                 String halfMoveClock,
                                                 String fullMoveNumber) {
        SIMPLE_LOGGER.log(Level.INFO,
                DISPLAY_ADDITIONAL_SETTINGS,
                new Object[]{activeColor, String.join(" and ", whiteCastlingRights), String.join(" and ",
                        blackCastlingRights), enPassantTarget, halfMoveClock, fullMoveNumber});
    }

    public static void displayMoves(Color color, List<String> moveDescriptions) {
        SIMPLE_LOGGER.log(Level.INFO,
                DISPLAY_MOVES,
                new Object[]{color.getColorName(), String.join(", ", moveDescriptions), moveDescriptions.size()});
    }

    public static void displayNoMoves(Color color, List<String> moveDescriptions) {
        SIMPLE_LOGGER.log(Level.INFO,
                NO_POSSIBLE_MOVES,
                new Object[]{color.getColorName(), moveDescriptions.get(0)});
    }

    public static void displayHelp() {
        LOGGER.info(LoggerUtility::logHelpMessage);
    }

}

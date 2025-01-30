package com.chessmove.service;

import com.chessmove.util.Color;
import com.chessmove.util.InvalidFENException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChessMoveCalculatorServiceTest {

    @ParameterizedTest(name = "{2}")
    @CsvFileSource(resources = "/chess_tests.csv", numLinesToSkip = 1)
    void testFENStrings(String fen, int expectedMoveCount, String testName) throws InvalidFENException {
        ChessMoveCalculatorService service = new ChessMoveCalculatorService(fen);

        String activeColorString = service.getActiveColor();
        Color activeColor;
        if (activeColorString == null || activeColorString.isEmpty() || activeColorString.equals("-")) {
            // For test purposes, assume white if not specified
            activeColor = Color.WHITE;
        } else {
            activeColor = Color.fromName(activeColorString);
        }

        List<String> actualMoves = service.getMoves(service.getSquares(), activeColor);

        // Assert the number of possible moves matches the expected count
        assertEquals(expectedMoveCount, actualMoves.size(),
                String.format("Expected %d moves for test case '%s', but got %d", expectedMoveCount, testName, actualMoves.size()));
    }
}

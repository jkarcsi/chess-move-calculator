package com.chessmove.util;

public enum Color {
    WHITE("white"),
    BLACK("black");

    private final String colorName;

    Color(String colorName) {
        this.colorName = colorName;
    }

    public String getColorName() {
        return colorName;
    }

    public static Color fromName(String colorName) throws InvalidFENException {
        for (Color color : Color.values()) {
            if (color.getColorName().equalsIgnoreCase(colorName)) {
                return color;
            }
        }
        throw new InvalidFENException("Invalid color name: " + colorName);
    }

    public static Color getOppositeColor(Color input) {
        for (Color color : Color.values()) {
            if (!input.equals(color)) {
                return color;
            }
        }
        return null;
    }
}

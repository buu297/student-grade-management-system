package com.buu297.grade.model;

import java.math.BigDecimal;

public enum LetterGrade {
    A_PLUS("A+", 4.0, "Excellent (High Distinction)"),
    A("A", 3.7, "Excellent"),
    B_PLUS("B+", 3.5, "Very Good"),
    B("B", 3.0, "Good"),
    C_PLUS("C+", 2.5, "Fairly Good"),
    C("C", 2.0, "Average"),
    D_PLUS("D+", 1.5, "Marginal Pass"),
    D("D", 1.0, "Pass"),
    F("F", 0.0, "Fail");

    private final String symbol;
    private final double gpa4;
    private final String description;

    LetterGrade(String symbol, double gpa4, String description) {
        this.symbol = symbol;
        this.gpa4 = gpa4;
        this.description = description;
    }

    public String getSymbol() { return symbol; }
    public double getGpa4() { return gpa4; }
    public String getDescription() { return description; }

    public static LetterGrade fromScore10(BigDecimal score) {
        if (score == null) return F;
        double s = score.doubleValue();
        if (s >= 9.0) return A_PLUS;
        if (s >= 8.5) return A;
        if (s >= 8.0) return B_PLUS;
        if (s >= 7.0) return B;
        if (s >= 6.5) return C_PLUS;
        if (s >= 5.5) return C;
        if (s >= 5.0) return D_PLUS;
        if (s >= 4.0) return D;
        return F;
    }

    public static LetterGrade fromString(String text) {
        if (text == null) return F;
        for (LetterGrade lg : values()) {
            if (lg.symbol.equalsIgnoreCase(text) || lg.name().equalsIgnoreCase(text)) {
                return lg;
            }
        }
        return F;
    }
}
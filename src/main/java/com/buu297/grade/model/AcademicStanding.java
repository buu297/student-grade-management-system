package com.buu297.grade.model;

public enum AcademicStanding {
    DEAN_LIST("Dean's List / High Distinction", "#15803d", "#dcfce7"),
    DISTINCTION("First Class / Distinction", "#2563eb", "#eff6ff"),
    GOOD_STANDING("Good Standing", "#0284c7", "#e0f2fe"),
    SATISFACTORY("Satisfactory", "#d97706", "#fef3c7"),
    ACADEMIC_WARNING("Academic Warning", "#ea580c", "#ffedd5"),
    PROBATION("Academic Probation", "#e11d48", "#ffe4e6");

    private final String displayName;
    private final String textColor;
    private final String bgColor;

    AcademicStanding(String displayName, String textColor, String bgColor) {
        this.displayName = displayName;
        this.textColor = textColor;
        this.bgColor = bgColor;
    }

    public String getDisplayName() { return displayName; }
    public String getTextColor() { return textColor; }
    public String getBgColor() { return bgColor; }

    public static AcademicStanding evaluate(double cpa) {
        if (cpa >= 3.60) return DEAN_LIST;
        if (cpa >= 3.20) return DISTINCTION;
        if (cpa >= 2.50) return GOOD_STANDING;
        if (cpa >= 2.00) return SATISFACTORY;
        if (cpa >= 1.50) return ACADEMIC_WARNING;
        return PROBATION;
    }
}
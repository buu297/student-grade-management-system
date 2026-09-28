package com.buu297.grade.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class StudentTranscript {
    private Student student;
    private String semesterId;
    private String semesterName;
    private List<GradeRecord> grades = new ArrayList<>();

    private int creditsAttempted = 0;
    private int creditsEarned = 0;
    private double semesterGpa = 0.0;
    private double cumulativeGpa = 0.0;
    private double averageScore10 = 0.0;
    private AcademicStanding standing = AcademicStanding.GOOD_STANDING;

    public StudentTranscript() {}

    public void recalculate() {
        int totalCredits = 0;
        int passedCredits = 0;
        double weightedGpaSum = 0.0;
        double weightedScore10Sum = 0.0;

        for (GradeRecord g : grades) {
            int cr = g.getCredits() > 0 ? g.getCredits() : 3;
            totalCredits += cr;
            if ("PASSED".equalsIgnoreCase(g.getStatus())) {
                passedCredits += cr;
            }
            if (g.getGpa4() != null) {
                weightedGpaSum += g.getGpa4().doubleValue() * cr;
            }
            if (g.getTotalScore10() != null) {
                weightedScore10Sum += g.getTotalScore10().doubleValue() * cr;
            }
        }

        this.creditsAttempted = totalCredits;
        this.creditsEarned = passedCredits;

        if (totalCredits > 0) {
            this.semesterGpa = BigDecimal.valueOf(weightedGpaSum / totalCredits).setScale(2, RoundingMode.HALF_UP).doubleValue();
            this.averageScore10 = BigDecimal.valueOf(weightedScore10Sum / totalCredits).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } else {
            this.semesterGpa = 0.0;
            this.averageScore10 = 0.0;
        }

        this.cumulativeGpa = this.semesterGpa; // or aggregated over all semesters
        this.standing = AcademicStanding.evaluate(this.cumulativeGpa);
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public List<GradeRecord> getGrades() { return grades; }
    public void setGrades(List<GradeRecord> grades) { this.grades = grades; }

    public int getCreditsAttempted() { return creditsAttempted; }
    public void setCreditsAttempted(int creditsAttempted) { this.creditsAttempted = creditsAttempted; }

    public int getCreditsEarned() { return creditsEarned; }
    public void setCreditsEarned(int creditsEarned) { this.creditsEarned = creditsEarned; }

    public double getSemesterGpa() { return semesterGpa; }
    public void setSemesterGpa(double semesterGpa) { this.semesterGpa = semesterGpa; }

    public double getCumulativeGpa() { return cumulativeGpa; }
    public void setCumulativeGpa(double cumulativeGpa) { this.cumulativeGpa = cumulativeGpa; }

    public double getAverageScore10() { return averageScore10; }
    public void setAverageScore10(double averageScore10) { this.averageScore10 = averageScore10; }

    public AcademicStanding getStanding() { return standing; }
    public void setStanding(AcademicStanding standing) { this.standing = standing; }
}
package com.buu297.grade.model;

import java.math.BigDecimal;

public class Course {
    private String courseCode;
    private String courseName;
    private String department;
    private int credits;
    private BigDecimal weightAttendance = new BigDecimal("0.10");
    private BigDecimal weightMidterm = new BigDecimal("0.20");
    private BigDecimal weightLab = new BigDecimal("0.20");
    private BigDecimal weightFinal = new BigDecimal("0.50");

    public Course() {}

    public Course(String courseCode, String courseName, String department, int credits,
                  BigDecimal weightAttendance, BigDecimal weightMidterm, BigDecimal weightLab, BigDecimal weightFinal) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.department = department;
        this.credits = credits;
        this.weightAttendance = weightAttendance;
        this.weightMidterm = weightMidterm;
        this.weightLab = weightLab;
        this.weightFinal = weightFinal;
    }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public BigDecimal getWeightAttendance() { return weightAttendance; }
    public void setWeightAttendance(BigDecimal weightAttendance) { this.weightAttendance = weightAttendance; }

    public BigDecimal getWeightMidterm() { return weightMidterm; }
    public void setWeightMidterm(BigDecimal weightMidterm) { this.weightMidterm = weightMidterm; }

    public BigDecimal getWeightLab() { return weightLab; }
    public void setWeightLab(BigDecimal weightLab) { this.weightLab = weightLab; }

    public BigDecimal getWeightFinal() { return weightFinal; }
    public void setWeightFinal(BigDecimal weightFinal) { this.weightFinal = weightFinal; }

    @Override
    public String toString() {
        return courseCode + " - " + courseName + " (" + credits + " cr)";
    }
}
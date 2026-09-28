package com.buu297.grade.model;

import java.util.HashMap;
import java.util.Map;

public class CourseAnalytics {
    private String courseCode;
    private String courseName;
    private int enrolledCount = 0;
    private double classMean = 0.0;
    private double stdDev = 0.0;
    private double minScore = 0.0;
    private double maxScore = 0.0;
    private int passCount = 0;
    private int failCount = 0;
    private double passRatePct = 0.0;
    private Map<String, Integer> gradeDistribution = new HashMap<>();

    public CourseAnalytics() {}

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getEnrolledCount() { return enrolledCount; }
    public void setEnrolledCount(int enrolledCount) { this.enrolledCount = enrolledCount; }

    public double getClassMean() { return classMean; }
    public void setClassMean(double classMean) { this.classMean = classMean; }

    public double getStdDev() { return stdDev; }
    public void setStdDev(double stdDev) { this.stdDev = stdDev; }

    public double getMinScore() { return minScore; }
    public void setMinScore(double minScore) { this.minScore = minScore; }

    public double getMaxScore() { return maxScore; }
    public void setMaxScore(double maxScore) { this.maxScore = maxScore; }

    public int getPassCount() { return passCount; }
    public void setPassCount(int passCount) { this.passCount = passCount; }

    public int getFailCount() { return failCount; }
    public void setFailCount(int failCount) { this.failCount = failCount; }

    public double getPassRatePct() { return passRatePct; }
    public void setPassRatePct(double passRatePct) { this.passRatePct = passRatePct; }

    public Map<String, Integer> getGradeDistribution() { return gradeDistribution; }
    public void setGradeDistribution(Map<String, Integer> gradeDistribution) { this.gradeDistribution = gradeDistribution; }
}
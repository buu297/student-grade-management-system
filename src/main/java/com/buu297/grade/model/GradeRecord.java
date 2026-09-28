package com.buu297.grade.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class GradeRecord {
    private String recordId;
    private String studentId;
    private String studentName;
    private String department;
    private String courseCode;
    private String courseName;
    private int credits;
    private String semesterId;
    private String semesterName;

    private BigDecimal attendanceScore;
    private BigDecimal midtermScore;
    private BigDecimal labScore;
    private BigDecimal finalScore;

    private BigDecimal totalScore10;
    private BigDecimal gpa4;
    private LetterGrade letterGrade;
    private String status = "ENROLLED"; // ENROLLED, PASSED, FAILED
    private String notes;
    private LocalDateTime updatedAt;

    public GradeRecord() {}

    public GradeRecord(String recordId, String studentId, String courseCode, String semesterId,
                       BigDecimal attendanceScore, BigDecimal midtermScore, BigDecimal labScore, BigDecimal finalScore) {
        this.recordId = recordId;
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.semesterId = semesterId;
        this.attendanceScore = attendanceScore;
        this.midtermScore = midtermScore;
        this.labScore = labScore;
        this.finalScore = finalScore;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the weighted composite score, maps to 4.0 GPA and LetterGrade.
     */
    public void computeGrades(Course course) {
        if (attendanceScore == null || midtermScore == null || labScore == null || finalScore == null) {
            this.status = "ENROLLED";
            return;
        }

        BigDecimal wAtt = course != null ? course.getWeightAttendance() : new BigDecimal("0.10");
        BigDecimal wMid = course != null ? course.getWeightMidterm() : new BigDecimal("0.20");
        BigDecimal wLab = course != null ? course.getWeightLab() : new BigDecimal("0.20");
        BigDecimal wFin = course != null ? course.getWeightFinal() : new BigDecimal("0.50");

        BigDecimal weighted = attendanceScore.multiply(wAtt)
                .add(midtermScore.multiply(wMid))
                .add(labScore.multiply(wLab))
                .add(finalScore.multiply(wFin))
                .setScale(2, RoundingMode.HALF_UP);

        this.totalScore10 = weighted;
        this.letterGrade = LetterGrade.fromScore10(weighted);
        this.gpa4 = BigDecimal.valueOf(letterGrade.getGpa4()).setScale(2, RoundingMode.HALF_UP);

        if (this.letterGrade == LetterGrade.F || this.finalScore.compareTo(new BigDecimal("4.00")) < 0) {
            this.status = "FAILED";
        } else {
            this.status = "PASSED";
        }
        this.updatedAt = LocalDateTime.now();
    }

    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getSemesterId() { return semesterId; }
    public void setSemesterId(String semesterId) { this.semesterId = semesterId; }

    public String getSemesterName() { return semesterName; }
    public void setSemesterName(String semesterName) { this.semesterName = semesterName; }

    public BigDecimal getAttendanceScore() { return attendanceScore; }
    public void setAttendanceScore(BigDecimal attendanceScore) { this.attendanceScore = attendanceScore; }

    public BigDecimal getMidtermScore() { return midtermScore; }
    public void setMidtermScore(BigDecimal midtermScore) { this.midtermScore = midtermScore; }

    public BigDecimal getLabScore() { return labScore; }
    public void setLabScore(BigDecimal labScore) { this.labScore = labScore; }

    public BigDecimal getFinalScore() { return finalScore; }
    public void setFinalScore(BigDecimal finalScore) { this.finalScore = finalScore; }

    public BigDecimal getTotalScore10() { return totalScore10; }
    public void setTotalScore10(BigDecimal totalScore10) { this.totalScore10 = totalScore10; }

    public BigDecimal getGpa4() { return gpa4; }
    public void setGpa4(BigDecimal gpa4) { this.gpa4 = gpa4; }

    public LetterGrade getLetterGrade() { return letterGrade; }
    public void setLetterGrade(LetterGrade letterGrade) { this.letterGrade = letterGrade; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
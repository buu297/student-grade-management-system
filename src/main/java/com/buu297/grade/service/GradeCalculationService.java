package com.buu297.grade.service;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GradeCalculationService {
    private final GradeRecordDAO gradeDAO;
    private final CourseDAO courseDAO;

    public GradeCalculationService() {
        this.gradeDAO = DatabaseManager.getInstance().getGradeRecordDAO();
        this.courseDAO = DatabaseManager.getInstance().getCourseDAO();
    }

    public GradeRecord recordOrUpdateGrade(String studentId, String courseCode, String semesterId,
                                          BigDecimal attendance, BigDecimal midterm, BigDecimal lab, BigDecimal finalExam,
                                          String notes) {
        Course course = courseDAO.findByCode(courseCode)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseCode));

        Optional<GradeRecord> existing = gradeDAO.findByStudentAndCourse(studentId, courseCode, semesterId);
        GradeRecord record = existing.orElseGet(() -> {
            GradeRecord g = new GradeRecord();
            g.setRecordId("GR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            g.setStudentId(studentId);
            g.setCourseCode(courseCode);
            g.setSemesterId(semesterId);
            return g;
        });

        record.setAttendanceScore(attendance);
        record.setMidtermScore(midterm);
        record.setLabScore(lab);
        record.setFinalScore(finalExam);
        record.setNotes(notes);

        record.computeGrades(course);
        gradeDAO.saveOrUpdate(record);
        return record;
    }

    public void applyScoreCurve(String courseCode, String semesterId, BigDecimal curveBonus) {
        List<GradeRecord> list = gradeDAO.findByCourseCode(courseCode, semesterId);
        Course course = courseDAO.findByCode(courseCode).orElse(null);

        for (GradeRecord g : list) {
            if (g.getFinalScore() != null) {
                BigDecimal curved = g.getFinalScore().add(curveBonus);
                if (curved.compareTo(new BigDecimal("10.00")) > 0) {
                    curved = new BigDecimal("10.00");
                }
                g.setFinalScore(curved);
                g.computeGrades(course);
                gradeDAO.saveOrUpdate(g);
            }
        }
    }
}
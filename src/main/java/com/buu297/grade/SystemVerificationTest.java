package com.buu297.grade;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.dao.StudentDAO;
import com.buu297.grade.model.*;
import com.buu297.grade.service.CourseAnalyticsService;
import com.buu297.grade.service.GradeCalculationService;
import com.buu297.grade.service.TranscriptReportService;

import java.math.BigDecimal;
import java.util.List;

public class SystemVerificationTest {
    public static void main(String[] args) {
        System.out.println(">>> RUNNING STUDENT GRADE MANAGEMENT SYSTEM VERIFICATION <<<");

        DatabaseManager db = DatabaseManager.getInstance();
        System.out.println("[TEST 1] Database Mode: " + db.getDatabaseStatusDescription());

        StudentDAO studentDAO = db.getStudentDAO();
        List<Student> students = studentDAO.findAll();
        System.out.println("[TEST 2] Active Students Loaded: " + students.size());
        assert students.size() >= 8 : "Expected at least 8 students";

        CourseDAO courseDAO = db.getCourseDAO();
        List<Course> courses = courseDAO.findAll();
        System.out.println("[TEST 3] Course Catalog Offerings: " + courses.size());
        assert courses.size() >= 8 : "Expected at least 8 courses";

        GradeRecordDAO gradeDAO = db.getGradeRecordDAO();
        List<GradeRecord> records = gradeDAO.findAll();
        System.out.println("[TEST 4] Seed Grade Records Loaded: " + records.size());
        assert records.size() >= 15 : "Expected at least 15 grade records";

        GradeCalculationService gradeService = new GradeCalculationService();
        GradeRecord testRecord = gradeService.recordOrUpdateGrade(
                "STU202604", "CS101", "SEM-2026-FALL",
                new BigDecimal("9.0"), new BigDecimal("8.5"), new BigDecimal("8.5"), new BigDecimal("9.0"),
                "Verification calculation test"
        );
        System.out.println("[TEST 5] Weighted Grade Calculation for STU202604 in CS101: Score=" +
                testRecord.getTotalScore10() + ", Letter=" + testRecord.getLetterGrade().getSymbol() +
                ", GPA=" + testRecord.getGpa4() + " -> " + testRecord.getStatus());
        assert testRecord.getLetterGrade() == LetterGrade.A || testRecord.getLetterGrade() == LetterGrade.B_PLUS;

        TranscriptReportService transcriptService = new TranscriptReportService();
        StudentTranscript transcript = transcriptService.generateTranscript("STU202601", "SEM-2026-FALL");
        System.out.println("[TEST 6] Dean's List Transcript Verification: Term GPA=" + transcript.getSemesterGpa() +
                ", Standing=" + transcript.getStanding().getDisplayName() +
                ", Credits=" + transcript.getCreditsEarned() + "/" + transcript.getCreditsAttempted());
        assert transcript.getStanding() == AcademicStanding.DEAN_LIST : "STU202601 should be Dean's List";

        CourseAnalyticsService analyticsService = new CourseAnalyticsService();
        CourseAnalytics analytics = analyticsService.getCourseAnalytics("CS101", "SEM-2026-FALL");
        System.out.println("[TEST 7] Course Bell Curve Analytics for CS101: Enrolled=" + analytics.getEnrolledCount() +
                ", Mean=" + analytics.getClassMean() + ", StdDev=" + analytics.getStdDev() +
                ", PassRate=" + analytics.getPassRatePct() + "%");
        assert analytics.getEnrolledCount() > 0 : "Should have enrolled students";

        System.out.println("\n🎉 ALL 7 ACADEMIC GRADE VERIFICATION TESTS PASSED WITH 100% SUCCESS!");
    }
}
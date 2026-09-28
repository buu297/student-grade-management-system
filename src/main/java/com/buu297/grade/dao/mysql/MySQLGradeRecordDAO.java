package com.buu297.grade.dao.mysql;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.*;

public class MySQLGradeRecordDAO implements GradeRecordDAO {
    private final DatabaseManager dbManager;

    public MySQLGradeRecordDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<GradeRecord> findById(String recordId) {
        String sql = "SELECT g.*, CONCAT(s.last_name, ' ', s.first_name) AS student_name, s.department, " +
                     "c.course_name, c.credits, sem.term_name AS semester_name " +
                     "FROM grade_records g " +
                     "JOIN students s ON g.student_id = s.student_id " +
                     "JOIN courses c ON g.course_code = c.course_code " +
                     "JOIN semesters sem ON g.semester_id = sem.semester_id " +
                     "WHERE g.record_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, recordId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public Optional<GradeRecord> findByStudentAndCourse(String studentId, String courseCode, String semesterId) {
        String sql = "SELECT g.*, CONCAT(s.last_name, ' ', s.first_name) AS student_name, s.department, " +
                     "c.course_name, c.credits, sem.term_name AS semester_name " +
                     "FROM grade_records g " +
                     "JOIN students s ON g.student_id = s.student_id " +
                     "JOIN courses c ON g.course_code = c.course_code " +
                     "JOIN semesters sem ON g.semester_id = sem.semester_id " +
                     "WHERE g.student_id = ? AND g.course_code = ? AND g.semester_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            ps.setString(3, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<GradeRecord> findByStudentId(String studentId) {
        List<GradeRecord> list = new ArrayList<>();
        String sql = "SELECT g.*, CONCAT(s.last_name, ' ', s.first_name) AS student_name, s.department, " +
                     "c.course_name, c.credits, sem.term_name AS semester_name " +
                     "FROM grade_records g " +
                     "JOIN students s ON g.student_id = s.student_id " +
                     "JOIN courses c ON g.course_code = c.course_code " +
                     "JOIN semesters sem ON g.semester_id = sem.semester_id " +
                     "WHERE g.student_id = ? " +
                     "ORDER BY g.course_code";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<GradeRecord> findByCourseCode(String courseCode, String semesterId) {
        List<GradeRecord> list = new ArrayList<>();
        String sql = "SELECT g.*, CONCAT(s.last_name, ' ', s.first_name) AS student_name, s.department, " +
                     "c.course_name, c.credits, sem.term_name AS semester_name " +
                     "FROM grade_records g " +
                     "JOIN students s ON g.student_id = s.student_id " +
                     "JOIN courses c ON g.course_code = c.course_code " +
                     "JOIN semesters sem ON g.semester_id = sem.semester_id " +
                     "WHERE g.course_code = ? AND (? IS NULL OR g.semester_id = ?) " +
                     "ORDER BY g.student_id";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, semesterId);
            ps.setString(3, semesterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<GradeRecord> findAll() {
        return filterGrades(null, null, null, null);
    }

    @Override
    public List<GradeRecord> filterGrades(String semesterId, String department, String courseCode, String query) {
        List<GradeRecord> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT g.*, CONCAT(s.last_name, ' ', s.first_name) AS student_name, s.department, " +
                "c.course_name, c.credits, sem.term_name AS semester_name " +
                "FROM grade_records g " +
                "JOIN students s ON g.student_id = s.student_id " +
                "JOIN courses c ON g.course_code = c.course_code " +
                "JOIN semesters sem ON g.semester_id = sem.semester_id " +
                "WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (semesterId != null && !semesterId.isEmpty() && !semesterId.equalsIgnoreCase("ALL")) {
            sql.append("AND g.semester_id = ? ");
            params.add(semesterId);
        }
        if (department != null && !department.isEmpty() && !department.equalsIgnoreCase("ALL")) {
            sql.append("AND s.department = ? ");
            params.add(department);
        }
        if (courseCode != null && !courseCode.isEmpty() && !courseCode.equalsIgnoreCase("ALL")) {
            sql.append("AND g.course_code = ? ");
            params.add(courseCode);
        }
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (g.student_id LIKE ? OR s.first_name LIKE ? OR s.last_name LIKE ? OR g.course_code LIKE ?) ");
            String p = "%" + query.trim() + "%";
            params.add(p); params.add(p); params.add(p); params.add(p);
        }
        sql.append("ORDER BY g.student_id, g.course_code");

        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void saveOrUpdate(GradeRecord g) {
        String sql = "INSERT INTO grade_records (record_id, student_id, course_code, semester_id, attendance_score, midterm_score, lab_score, final_score, total_score_10, gpa_4, letter_grade, status, notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE attendance_score=VALUES(attendance_score), midterm_score=VALUES(midterm_score), lab_score=VALUES(lab_score), final_score=VALUES(final_score), total_score_10=VALUES(total_score_10), gpa_4=VALUES(gpa_4), letter_grade=VALUES(letter_grade), status=VALUES(status), notes=VALUES(notes)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, g.getRecordId());
            ps.setString(2, g.getStudentId());
            ps.setString(3, g.getCourseCode());
            ps.setString(4, g.getSemesterId());
            ps.setBigDecimal(5, g.getAttendanceScore());
            ps.setBigDecimal(6, g.getMidtermScore());
            ps.setBigDecimal(7, g.getLabScore());
            ps.setBigDecimal(8, g.getFinalScore());
            ps.setBigDecimal(9, g.getTotalScore10());
            ps.setBigDecimal(10, g.getGpa4());
            ps.setString(11, g.getLetterGrade() != null ? g.getLetterGrade().getSymbol() : null);
            ps.setString(12, g.getStatus());
            ps.setString(13, g.getNotes());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(String recordId) {
        String sql = "DELETE FROM grade_records WHERE record_id = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, recordId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public StudentTranscript getStudentTranscript(String studentId, String semesterId) {
        StudentTranscript transcript = new StudentTranscript();
        dbManager.getStudentDAO().findById(studentId).ifPresent(transcript::setStudent);
        transcript.setSemesterId(semesterId);

        List<GradeRecord> list = filterGrades(semesterId, null, null, studentId);
        transcript.setGrades(list);
        transcript.recalculate();
        return transcript;
    }

    @Override
    public CourseAnalytics getCourseAnalytics(String courseCode, String semesterId) {
        CourseAnalytics analytics = new CourseAnalytics();
        analytics.setCourseCode(courseCode);
        dbManager.getCourseDAO().findByCode(courseCode).ifPresent(c -> analytics.setCourseName(c.getCourseName()));

        List<GradeRecord> records = findByCourseCode(courseCode, semesterId);
        analytics.setEnrolledCount(records.size());
        if (records.isEmpty()) return analytics;

        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        int pass = 0, fail = 0;
        Map<String, Integer> dist = new HashMap<>();

        for (GradeRecord g : records) {
            if (g.getTotalScore10() != null) {
                double score = g.getTotalScore10().doubleValue();
                sum += score;
                if (score < min) min = score;
                if (score > max) max = score;
            }
            if ("PASSED".equalsIgnoreCase(g.getStatus())) pass++;
            else fail++;

            String letter = g.getLetterGrade() != null ? g.getLetterGrade().getSymbol() : "N/A";
            dist.put(letter, dist.getOrDefault(letter, 0) + 1);
        }

        double mean = sum / records.size();
        analytics.setClassMean(BigDecimal.valueOf(mean).setScale(2, RoundingMode.HALF_UP).doubleValue());
        analytics.setMinScore(min == Double.MAX_VALUE ? 0.0 : min);
        analytics.setMaxScore(max == Double.MIN_VALUE ? 0.0 : max);
        analytics.setPassCount(pass);
        analytics.setFailCount(fail);
        analytics.setPassRatePct(BigDecimal.valueOf((double) pass / records.size() * 100.0).setScale(1, RoundingMode.HALF_UP).doubleValue());
        analytics.setGradeDistribution(dist);

        double varianceSum = 0.0;
        for (GradeRecord g : records) {
            if (g.getTotalScore10() != null) {
                varianceSum += Math.pow(g.getTotalScore10().doubleValue() - mean, 2);
            }
        }
        double stdDev = Math.sqrt(varianceSum / records.size());
        analytics.setStdDev(BigDecimal.valueOf(stdDev).setScale(2, RoundingMode.HALF_UP).doubleValue());

        return analytics;
    }

    private GradeRecord mapResultSet(ResultSet rs) throws SQLException {
        GradeRecord g = new GradeRecord();
        g.setRecordId(rs.getString("record_id"));
        g.setStudentId(rs.getString("student_id"));
        g.setCourseCode(rs.getString("course_code"));
        g.setSemesterId(rs.getString("semester_id"));
        g.setAttendanceScore(rs.getBigDecimal("attendance_score"));
        g.setMidtermScore(rs.getBigDecimal("midterm_score"));
        g.setLabScore(rs.getBigDecimal("lab_score"));
        g.setFinalScore(rs.getBigDecimal("final_score"));
        g.setTotalScore10(rs.getBigDecimal("total_score_10"));
        g.setGpa4(rs.getBigDecimal("gpa_4"));
        g.setLetterGrade(LetterGrade.fromString(rs.getString("letter_grade")));
        g.setStatus(rs.getString("status"));
        g.setNotes(rs.getString("notes"));
        try {
            g.setStudentName(rs.getString("student_name"));
            g.setDepartment(rs.getString("department"));
            g.setCourseName(rs.getString("course_name"));
            g.setCredits(rs.getInt("credits"));
            g.setSemesterName(rs.getString("semester_name"));
        } catch (SQLException ignored) {}
        return g;
    }
}
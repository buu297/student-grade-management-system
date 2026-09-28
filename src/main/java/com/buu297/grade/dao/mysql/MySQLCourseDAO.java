package com.buu297.grade.dao.mysql;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MySQLCourseDAO implements CourseDAO {
    private final DatabaseManager dbManager;

    public MySQLCourseDAO(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Optional<Course> findByCode(String courseCode) {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return Optional.empty();
    }

    @Override
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY course_code";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public List<Course> findByDepartment(String department) {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE department = ? ORDER BY course_code";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public void save(Course c) {
        String sql = "INSERT INTO courses (course_code, course_name, department, credits, weight_attendance, weight_midterm, weight_lab, weight_final) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getDepartment());
            ps.setInt(4, c.getCredits());
            ps.setBigDecimal(5, c.getWeightAttendance());
            ps.setBigDecimal(6, c.getWeightMidterm());
            ps.setBigDecimal(7, c.getWeightLab());
            ps.setBigDecimal(8, c.getWeightFinal());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(Course c) {
        String sql = "UPDATE courses SET course_name=?, department=?, credits=?, weight_attendance=?, weight_midterm=?, weight_lab=?, weight_final=? WHERE course_code=?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getDepartment());
            ps.setInt(3, c.getCredits());
            ps.setBigDecimal(4, c.getWeightAttendance());
            ps.setBigDecimal(5, c.getWeightMidterm());
            ps.setBigDecimal(6, c.getWeightLab());
            ps.setBigDecimal(7, c.getWeightFinal());
            ps.setString(8, c.getCourseCode());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private Course mapResultSet(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setCourseCode(rs.getString("course_code"));
        c.setCourseName(rs.getString("course_name"));
        c.setDepartment(rs.getString("department"));
        c.setCredits(rs.getInt("credits"));
        c.setWeightAttendance(rs.getBigDecimal("weight_attendance"));
        c.setWeightMidterm(rs.getBigDecimal("weight_midterm"));
        c.setWeightLab(rs.getBigDecimal("weight_lab"));
        c.setWeightFinal(rs.getBigDecimal("weight_final"));
        return c;
    }
}
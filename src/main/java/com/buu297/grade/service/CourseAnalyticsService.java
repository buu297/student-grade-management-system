package com.buu297.grade.service;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.model.CourseAnalytics;

public class CourseAnalyticsService {
    private final GradeRecordDAO gradeDAO;

    public CourseAnalyticsService() {
        this.gradeDAO = DatabaseManager.getInstance().getGradeRecordDAO();
    }

    public CourseAnalytics getCourseAnalytics(String courseCode, String semesterId) {
        return gradeDAO.getCourseAnalytics(courseCode, semesterId);
    }
}
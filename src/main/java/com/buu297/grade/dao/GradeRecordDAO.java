package com.buu297.grade.dao;

import com.buu297.grade.model.CourseAnalytics;
import com.buu297.grade.model.GradeRecord;
import com.buu297.grade.model.StudentTranscript;

import java.util.List;
import java.util.Optional;

public interface GradeRecordDAO {
    Optional<GradeRecord> findById(String recordId);
    Optional<GradeRecord> findByStudentAndCourse(String studentId, String courseCode, String semesterId);
    List<GradeRecord> findByStudentId(String studentId);
    List<GradeRecord> findByCourseCode(String courseCode, String semesterId);
    List<GradeRecord> findAll();
    List<GradeRecord> filterGrades(String semesterId, String department, String courseCode, String query);
    void saveOrUpdate(GradeRecord record);
    void delete(String recordId);
    StudentTranscript getStudentTranscript(String studentId, String semesterId);
    CourseAnalytics getCourseAnalytics(String courseCode, String semesterId);
}
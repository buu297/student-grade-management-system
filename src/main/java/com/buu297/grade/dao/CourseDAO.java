package com.buu297.grade.dao;

import com.buu297.grade.model.Course;
import java.util.List;
import java.util.Optional;

public interface CourseDAO {
    Optional<Course> findByCode(String courseCode);
    List<Course> findAll();
    List<Course> findByDepartment(String department);
    void save(Course course);
    void update(Course course);
}
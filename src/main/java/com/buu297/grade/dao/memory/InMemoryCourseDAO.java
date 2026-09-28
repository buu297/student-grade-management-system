package com.buu297.grade.dao.memory;

import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.model.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryCourseDAO implements CourseDAO {
    private final InMemoryDatabase db = InMemoryDatabase.getInstance();

    @Override
    public Optional<Course> findByCode(String courseCode) {
        return Optional.ofNullable(db.courses.get(courseCode));
    }

    @Override
    public List<Course> findAll() {
        return new ArrayList<>(db.courses.values());
    }

    @Override
    public List<Course> findByDepartment(String department) {
        if (department == null || department.isEmpty() || department.equalsIgnoreCase("ALL")) return findAll();
        return db.courses.values().stream()
                .filter(c -> c.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Course course) {
        db.courses.put(course.getCourseCode(), course);
    }

    @Override
    public void update(Course course) {
        db.courses.put(course.getCourseCode(), course);
    }
}
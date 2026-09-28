package com.buu297.grade.dao.memory;

import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryGradeRecordDAO implements GradeRecordDAO {
    private final InMemoryDatabase db = InMemoryDatabase.getInstance();

    @Override
    public Optional<GradeRecord> findById(String recordId) {
        return Optional.ofNullable(db.gradeRecords.get(recordId));
    }

    @Override
    public Optional<GradeRecord> findByStudentAndCourse(String studentId, String courseCode, String semesterId) {
        return db.gradeRecords.values().stream()
                .filter(g -> g.getStudentId().equalsIgnoreCase(studentId)
                        && g.getCourseCode().equalsIgnoreCase(courseCode)
                        && (semesterId == null || g.getSemesterId().equalsIgnoreCase(semesterId)))
                .findFirst();
    }

    @Override
    public List<GradeRecord> findByStudentId(String studentId) {
        return db.gradeRecords.values().stream()
                .filter(g -> g.getStudentId().equalsIgnoreCase(studentId))
                .sorted(Comparator.comparing(GradeRecord::getCourseCode))
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeRecord> findByCourseCode(String courseCode, String semesterId) {
        return db.gradeRecords.values().stream()
                .filter(g -> g.getCourseCode().equalsIgnoreCase(courseCode)
                        && (semesterId == null || semesterId.isEmpty() || semesterId.equalsIgnoreCase("ALL") || g.getSemesterId().equalsIgnoreCase(semesterId)))
                .sorted(Comparator.comparing(GradeRecord::getStudentId))
                .collect(Collectors.toList());
    }

    @Override
    public List<GradeRecord> findAll() {
        return new ArrayList<>(db.gradeRecords.values());
    }

    @Override
    public List<GradeRecord> filterGrades(String semesterId, String department, String courseCode, String query) {
        return db.gradeRecords.values().stream()
                .filter(g -> semesterId == null || semesterId.isEmpty() || semesterId.equalsIgnoreCase("ALL") || g.getSemesterId().equalsIgnoreCase(semesterId))
                .filter(g -> department == null || department.isEmpty() || department.equalsIgnoreCase("ALL") || (g.getDepartment() != null && g.getDepartment().equalsIgnoreCase(department)))
                .filter(g -> courseCode == null || courseCode.isEmpty() || courseCode.equalsIgnoreCase("ALL") || g.getCourseCode().equalsIgnoreCase(courseCode))
                .filter(g -> {
                    if (query == null || query.trim().isEmpty()) return true;
                    String q = query.toLowerCase().trim();
                    return g.getStudentId().toLowerCase().contains(q)
                            || (g.getStudentName() != null && g.getStudentName().toLowerCase().contains(q))
                            || g.getCourseCode().toLowerCase().contains(q);
                })
                .sorted(Comparator.comparing(GradeRecord::getStudentId).thenComparing(GradeRecord::getCourseCode))
                .collect(Collectors.toList());
    }

    @Override
    public void saveOrUpdate(GradeRecord record) {
        Course c = db.courses.get(record.getCourseCode());
        Student s = db.students.get(record.getStudentId());
        Semester sem = db.semesters.get(record.getSemesterId());

        if (c != null) {
            record.setCourseName(c.getCourseName());
            record.setCredits(c.getCredits());
        }
        if (s != null) {
            record.setStudentName(s.getFullName());
            record.setDepartment(s.getDepartment());
        }
        if (sem != null) {
            record.setSemesterName(sem.getTermName());
        }

        record.computeGrades(c);
        db.gradeRecords.put(record.getRecordId(), record);
    }

    @Override
    public void delete(String recordId) {
        db.gradeRecords.remove(recordId);
    }

    @Override
    public StudentTranscript getStudentTranscript(String studentId, String semesterId) {
        StudentTranscript transcript = new StudentTranscript();
        Student s = db.students.get(studentId);
        transcript.setStudent(s);
        transcript.setSemesterId(semesterId);

        Semester sem = db.semesters.get(semesterId);
        if (sem != null) {
            transcript.setSemesterName(sem.getTermName());
        }

        List<GradeRecord> list = db.gradeRecords.values().stream()
                .filter(g -> g.getStudentId().equalsIgnoreCase(studentId)
                        && (semesterId == null || semesterId.isEmpty() || semesterId.equalsIgnoreCase("ALL") || g.getSemesterId().equalsIgnoreCase(semesterId)))
                .sorted(Comparator.comparing(GradeRecord::getCourseCode))
                .collect(Collectors.toList());

        transcript.setGrades(list);
        transcript.recalculate();
        return transcript;
    }

    @Override
    public CourseAnalytics getCourseAnalytics(String courseCode, String semesterId) {
        CourseAnalytics analytics = new CourseAnalytics();
        analytics.setCourseCode(courseCode);
        Course c = db.courses.get(courseCode);
        if (c != null) analytics.setCourseName(c.getCourseName());

        List<GradeRecord> records = findByCourseCode(courseCode, semesterId);
        analytics.setEnrolledCount(records.size());

        if (records.isEmpty()) return analytics;

        double sum = 0.0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        int pass = 0;
        int fail = 0;
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

        // Standard Deviation
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
}
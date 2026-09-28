package com.buu297.grade.dao.memory;

import com.buu297.grade.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryDatabase {
    private static final InMemoryDatabase INSTANCE = new InMemoryDatabase();

    public static InMemoryDatabase getInstance() {
        return INSTANCE;
    }

    public final Map<String, Student> students = new ConcurrentHashMap<>();
    public final Map<String, Course> courses = new ConcurrentHashMap<>();
    public final Map<String, Semester> semesters = new ConcurrentHashMap<>();
    public final Map<String, GradeRecord> gradeRecords = new ConcurrentHashMap<>();

    private InMemoryDatabase() {
        seedInitialData();
    }

    private void seedInitialData() {
        // 1. Semesters
        semesters.put("SEM-2026-FALL", new Semester("SEM-2026-FALL", "Fall Semester 2026", 2026, LocalDate.of(2026, 9, 1), LocalDate.of(2026, 12, 31), true));
        semesters.put("SEM-2026-SPRING", new Semester("SEM-2026-SPRING", "Spring Semester 2026", 2026, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 5, 30), false));
        semesters.put("SEM-2026-SUMMER", new Semester("SEM-2026-SUMMER", "Summer Term 2026", 2026, LocalDate.of(2026, 6, 15), LocalDate.of(2026, 8, 20), false));

        // 2. Students
        addStudent(new Student("STU202601", "An", "Nguyen Van", "an.nguyen@university.edu.vn", "+84 901 234 567", "Computer Science", "Software Engineering", 2026, "ACTIVE"));
        addStudent(new Student("STU202602", "Bich", "Tran Thi", "bich.tran@university.edu.vn", "+84 912 345 678", "Information Systems", "Data Analytics", 2026, "ACTIVE"));
        addStudent(new Student("STU202603", "Cuong", "Le Quoc", "cuong.le@university.edu.vn", "+84 923 456 789", "Computer Science", "Artificial Intelligence", 2025, "ACTIVE"));
        addStudent(new Student("STU202604", "Duc", "Pham Minh", "duc.pham@university.edu.vn", "+84 934 567 890", "Business Administration", "International Business", 2026, "ACTIVE"));
        addStudent(new Student("STU202605", "Bao", "Hoang Gia", "bao.hoang@university.edu.vn", "+84 945 678 901", "Electrical Engineering", "Embedded Systems", 2024, "ACTIVE"));
        addStudent(new Student("STU202606", "Ha", "Vu Thanh", "ha.vu@university.edu.vn", "+84 956 789 012", "Computer Science", "Cybersecurity", 2025, "ACTIVE"));
        addStudent(new Student("STU202607", "Kiet", "Dang Tuan", "kiet.dang@university.edu.vn", "+84 967 890 123", "Business Administration", "Finance & Banking", 2026, "ACTIVE"));
        addStudent(new Student("STU202608", "Linh", "Do Ngoc", "linh.do@university.edu.vn", "+84 978 901 234", "Information Systems", "E-Commerce", 2026, "ACTIVE"));

        // 3. Courses
        addCourse(new Course("CS101", "Introduction to Computer Science", "Computer Science", 3, new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.20"), new BigDecimal("0.50")));
        addCourse(new Course("CS202", "Data Structures & Algorithms", "Computer Science", 4, new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.30"), new BigDecimal("0.40")));
        addCourse(new Course("CS303", "Database Management Systems", "Computer Science", 3, new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.20"), new BigDecimal("0.50")));
        addCourse(new Course("CS404", "Artificial Intelligence & ML", "Computer Science", 4, new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("0.25"), new BigDecimal("0.40")));
        addCourse(new Course("IS201", "Enterprise Information Systems", "Information Systems", 3, new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.20"), new BigDecimal("0.50")));
        addCourse(new Course("IS305", "Business Data Analytics & BI", "Information Systems", 3, new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.30"), new BigDecimal("0.40")));
        addCourse(new Course("EE210", "Digital Logic & Microprocessors", "Electrical Engineering", 4, new BigDecimal("0.10"), new BigDecimal("0.20"), new BigDecimal("0.30"), new BigDecimal("0.40")));
        addCourse(new Course("BA101", "Principles of Modern Management", "Business Administration", 3, new BigDecimal("0.10"), new BigDecimal("0.30"), new BigDecimal("0.10"), new BigDecimal("0.50")));

        // 4. Grade Records
        addGrade("GR-001", "STU202601", "CS101", "SEM-2026-FALL", "10.0", "9.5", "9.0", "9.0", "Exemplary project work");
        addGrade("GR-002", "STU202601", "CS202", "SEM-2026-FALL", "10.0", "9.0", "8.5", "8.5", "Strong algorithmic proofs");
        addGrade("GR-003", "STU202601", "CS303", "SEM-2026-FALL",  "9.5", "8.5", "9.0", "9.0", "Optimized query design");
        addGrade("GR-004", "STU202601", "IS201", "SEM-2026-FALL", "10.0", "9.0", "9.5", "9.5", "Full marks on ER modeling");

        addGrade("GR-005", "STU202602", "CS101", "SEM-2026-FALL",  "9.0", "8.0", "8.5", "8.5", "Regular active attendance");
        addGrade("GR-006", "STU202602", "IS201", "SEM-2026-FALL", "10.0", "8.5", "9.0", "8.5", "Good enterprise analysis");
        addGrade("GR-007", "STU202602", "IS305", "SEM-2026-FALL",  "9.0", "9.0", "8.5", "9.0", "Excellent Tableau dashboards");

        addGrade("GR-008", "STU202603", "CS202", "SEM-2026-FALL",  "8.5", "7.5", "8.0", "7.0", "Solid coursework");
        addGrade("GR-009", "STU202603", "CS303", "SEM-2026-FALL",  "9.0", "8.0", "8.5", "8.0", "Passed normalization lab");
        addGrade("GR-010", "STU202603", "CS404", "SEM-2026-FALL", "10.0", "9.5", "9.5", "9.0", "Outstanding CNN architecture");

        addGrade("GR-011", "STU202604", "BA101", "SEM-2026-FALL",  "9.0", "8.0", "8.0", "7.5", "Good case presentation");
        addGrade("GR-012", "STU202604", "IS201", "SEM-2026-FALL",  "8.0", "7.0", "7.5", "6.5", "Satisfactory understanding");

        addGrade("GR-013", "STU202605", "CS101", "SEM-2026-FALL",  "9.0", "8.5", "8.0", "7.5", "Good C programming");
        addGrade("GR-014", "STU202605", "EE210", "SEM-2026-FALL", "10.0", "9.0", "9.0", "8.5", "Excellent FPGA design lab");

        addGrade("GR-015", "STU202606", "CS202", "SEM-2026-FALL",  "9.5", "8.5", "9.0", "8.5", "Solid problem solving");
        addGrade("GR-016", "STU202606", "CS303", "SEM-2026-FALL",  "8.5", "7.5", "8.0", "7.5", "Good SQL security test");

        addGrade("GR-017", "STU202607", "BA101", "SEM-2026-FALL",  "6.0", "5.0", "5.0", "4.0", "Low attendance record");
        addGrade("GR-018", "STU202607", "IS201", "SEM-2026-FALL",  "5.0", "4.0", "4.0", "3.0", "Failed final exam threshold");

        addGrade("GR-019", "STU202608", "IS201", "SEM-2026-FALL",  "9.0", "8.0", "8.5", "8.0", "Good group coordination");
        addGrade("GR-020", "STU202608", "IS305", "SEM-2026-FALL",  "8.5", "7.5", "8.0", "7.5", "Clean predictive model");
    }

    private void addStudent(Student s) { students.put(s.getStudentId(), s); }
    private void addCourse(Course c) { courses.put(c.getCourseCode(), c); }

    private void addGrade(String id, String studentId, String courseCode, String semId,
                          String att, String mid, String lab, String fin, String notes) {
        Course c = courses.get(courseCode);
        Student s = students.get(studentId);
        Semester sem = semesters.get(semId);

        GradeRecord g = new GradeRecord(id, studentId, courseCode, semId,
                new BigDecimal(att), new BigDecimal(mid), new BigDecimal(lab), new BigDecimal(fin));
        if (c != null) {
            g.setCourseName(c.getCourseName());
            g.setCredits(c.getCredits());
        }
        if (s != null) {
            g.setStudentName(s.getFullName());
            g.setDepartment(s.getDepartment());
        }
        if (sem != null) {
            g.setSemesterName(sem.getTermName());
        }
        g.setNotes(notes);
        g.computeGrades(c);
        gradeRecords.put(id, g);
    }
}
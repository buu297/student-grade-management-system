package com.buu297.grade.ui;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.dao.StudentDAO;
import com.buu297.grade.model.*;
import com.buu297.grade.ui.components.ModernTable;
import com.buu297.grade.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final StatCard cardStudents;
    private final StatCard cardCourses;
    private final StatCard cardAvgGpa;
    private final StatCard cardPassRate;

    private final DefaultTableModel topStudentsModel;
    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final GradeRecordDAO gradeDAO;

    public DashboardPanel(Runnable onNavigateToGrades, Runnable onNavigateToTranscript, Runnable onNavigateToAnalytics) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        this.studentDAO = DatabaseManager.getInstance().getStudentDAO();
        this.courseDAO = DatabaseManager.getInstance().getCourseDAO();
        this.gradeDAO = DatabaseManager.getInstance().getGradeRecordDAO();

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Academic Performance & Grading Dashboard");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);

        JLabel subtitle = new JLabel("Dean's Office Overview • Fall Semester 2026 Academic Term");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_MUTED);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel actionBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionBox.setOpaque(false);

        JButton btnEntry = Theme.createPrimaryButton("+ Grade Entry");
        btnEntry.addActionListener(e -> onNavigateToGrades.run());

        JButton btnTranscript = Theme.createSecondaryButton("Generate Transcript");
        btnTranscript.addActionListener(e -> onNavigateToTranscript.run());

        JButton btnAnalytics = Theme.createSecondaryButton("📊 Course Analytics");
        btnAnalytics.addActionListener(e -> onNavigateToAnalytics.run());

        actionBox.add(btnTranscript);
        actionBox.add(btnAnalytics);
        actionBox.add(btnEntry);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(actionBox, BorderLayout.EAST);

        // 2. Metrics Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setPreferredSize(new Dimension(800, 100));

        cardStudents = new StatCard("Active Students", "0", "Enrolled cohorts", Theme.PRIMARY);
        cardCourses = new StatCard("Course Catalog", "0", "Curriculum offerings", Theme.PURPLE);
        cardAvgGpa = new StatCard("Institutional GPA", "0.00", "4.0 Weighted Scale", Theme.SUCCESS);
        cardPassRate = new StatCard("Pass Rate", "0.0%", "Academic success index", Theme.WARNING);

        metricsGrid.add(cardStudents);
        metricsGrid.add(cardCourses);
        metricsGrid.add(cardAvgGpa);
        metricsGrid.add(cardPassRate);

        JPanel topContainer = new JPanel(new BorderLayout(0, 16));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(metricsGrid, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 3. Top Honor Roll / Recent Submissions Table
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.setBackground(Theme.BG_CARD);
        bottomPanel.setBorder(Theme.createCardBorder());

        JLabel sectionTitle = new JLabel("Top Academic Achievers & Semester Honor Roll (Fall 2026)");
        sectionTitle.setFont(Theme.FONT_HEADER);
        sectionTitle.setForeground(Theme.TEXT_MAIN);
        bottomPanel.add(sectionTitle, BorderLayout.NORTH);

        String[] cols = {"Student ID", "Student Name", "Department", "Major", "Credits Earned", "GPA (4.0)", "Academic Standing"};
        topStudentsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        ModernTable honorTable = new ModernTable(topStudentsModel);
        JScrollPane scroll = new JScrollPane(honorTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        bottomPanel.add(scroll, BorderLayout.CENTER);

        add(bottomPanel, BorderLayout.CENTER);

        refreshData();
    }

    public void refreshData() {
        int studentCount = studentDAO.countActiveStudents();
        int courseCount = courseDAO.findAll().size();

        List<GradeRecord> allGrades = gradeDAO.findAll();
        double sumGpa = 0.0;
        int passCount = 0;
        int totalGraded = 0;

        for (GradeRecord g : allGrades) {
            if (g.getGpa4() != null) {
                sumGpa += g.getGpa4().doubleValue();
                totalGraded++;
            }
            if ("PASSED".equalsIgnoreCase(g.getStatus())) {
                passCount++;
            }
        }

        double avgGpa = totalGraded > 0 ? (sumGpa / totalGraded) : 0.0;
        double passRate = allGrades.size() > 0 ? ((double) passCount / allGrades.size() * 100.0) : 0.0;

        cardStudents.setValue(String.valueOf(studentCount));
        cardCourses.setValue(String.valueOf(courseCount));
        cardAvgGpa.setValue(String.format(java.util.Locale.US, "%.2f", avgGpa));
        cardPassRate.setValue(String.format(java.util.Locale.US, "%.1f%%", passRate));

        // Load Honor Students
        topStudentsModel.setRowCount(0);
        List<Student> students = studentDAO.findAll();
        for (Student s : students) {
            StudentTranscript t = gradeDAO.getStudentTranscript(s.getStudentId(), "SEM-2026-FALL");
            topStudentsModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getFullName(),
                    s.getDepartment(),
                    s.getMajor(),
                    t.getCreditsEarned() + " / " + t.getCreditsAttempted() + " cr",
                    String.format(java.util.Locale.US, "%.2f", t.getSemesterGpa()),
                    t.getStanding().getDisplayName()
            });
        }
    }
}
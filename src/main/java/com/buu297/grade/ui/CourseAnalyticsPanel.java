package com.buu297.grade.ui;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.model.Course;
import com.buu297.grade.model.CourseAnalytics;
import com.buu297.grade.service.CourseAnalyticsService;
import com.buu297.grade.ui.components.ModernTable;
import com.buu297.grade.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class CourseAnalyticsPanel extends JPanel {
    private final CourseDAO courseDAO;
    private final CourseAnalyticsService analyticsService;

    private final JComboBox<Course> cbCourse;
    private final JComboBox<String> cbSemester;

    private final StatCard cardEnrolled;
    private final StatCard cardMean;
    private final StatCard cardStdDev;
    private final StatCard cardPassRate;

    private final DefaultTableModel distModel;

    public CourseAnalyticsPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        this.courseDAO = DatabaseManager.getInstance().getCourseDAO();
        this.analyticsService = new CourseAnalyticsService();

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Course Analytics & Bell Curve Statistics");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);

        JLabel subtitle = new JLabel("Normal distribution metrics, grade standard deviations, and class performance histograms.");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_MUTED);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        headerPanel.add(titleBox, BorderLayout.WEST);

        // 2. Control Bar
        JPanel controlCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        controlCard.setBackground(Theme.BG_CARD);
        controlCard.setBorder(Theme.createCardBorder());

        controlCard.add(new JLabel("Select Course:"));
        List<Course> courses = courseDAO.findAll();
        cbCourse = new JComboBox<>(courses.toArray(new Course[0]));
        controlCard.add(cbCourse);

        controlCard.add(new JLabel("Term:"));
        cbSemester = new JComboBox<>(new String[]{"SEM-2026-FALL", "SEM-2026-SPRING", "SEM-2026-SUMMER"});
        controlCard.add(cbSemester);

        JButton btnCompute = Theme.createPrimaryButton("Analyze Course");
        btnCompute.addActionListener(e -> loadAnalytics());
        controlCard.add(btnCompute);

        // 3. Metrics Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setPreferredSize(new Dimension(800, 100));

        cardEnrolled = new StatCard("Enrolled Students", "0", "Class cohort size", Theme.PRIMARY);
        cardMean = new StatCard("Class Mean (μ)", "0.00", "Average score (10.0)", Theme.SUCCESS);
        cardStdDev = new StatCard("Std Deviation (σ)", "0.00", "Spread variance", Theme.PURPLE);
        cardPassRate = new StatCard("Class Pass Rate", "0.0%", "Success benchmark", Theme.WARNING);

        metricsGrid.add(cardEnrolled);
        metricsGrid.add(cardMean);
        metricsGrid.add(cardStdDev);
        metricsGrid.add(cardPassRate);

        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(controlCard, BorderLayout.CENTER);
        topContainer.add(metricsGrid, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 4. Grade Distribution Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Theme.BG_CARD);
        centerPanel.setBorder(Theme.createCardBorder());

        JLabel sectionTitle = new JLabel("Letter Grade Frequency & Cohort Distribution");
        sectionTitle.setFont(Theme.FONT_HEADER);
        sectionTitle.setForeground(Theme.TEXT_MAIN);
        centerPanel.add(sectionTitle, BorderLayout.NORTH);

        String[] cols = {"Letter Grade", "Frequency (Count)", "Percentage Share (%)", "Performance Category"};
        distModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        ModernTable distTable = new ModernTable(distModel);
        JScrollPane scroll = new JScrollPane(distTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadAnalytics();
    }

    public void loadAnalytics() {
        Course c = (Course) cbCourse.getSelectedItem();
        if (c == null) return;
        String sem = (String) cbSemester.getSelectedItem();

        CourseAnalytics a = analyticsService.getCourseAnalytics(c.getCourseCode(), sem);

        cardEnrolled.setValue(String.valueOf(a.getEnrolledCount()));
        cardMean.setValue(String.format(java.util.Locale.US, "%.2f", a.getClassMean()));
        cardMean.setSubtitle("Range: [" + a.getMinScore() + " - " + a.getMaxScore() + "]");
        cardStdDev.setValue(String.format(java.util.Locale.US, "%.2f", a.getStdDev()));
        cardPassRate.setValue(String.format(java.util.Locale.US, "%.1f%%", a.getPassRatePct()));
        cardPassRate.setSubtitle(a.getPassCount() + " Passed, " + a.getFailCount() + " Failed");

        distModel.setRowCount(0);
        int total = a.getEnrolledCount();
        String[] allLetters = {"A+", "A", "B+", "B", "C+", "C", "D+", "D", "F"};
        Map<String, Integer> dist = a.getGradeDistribution();

        for (String letter : allLetters) {
            int count = dist.getOrDefault(letter, 0);
            double pct = total > 0 ? ((double) count / total * 100.0) : 0.0;
            String cat = "F".equals(letter) ? "Failed (Unearned)" : ("A+".equals(letter) || "A".equals(letter) ? "Honors" : "Passing");
            distModel.addRow(new Object[]{
                    letter,
                    count + " students",
                    String.format(java.util.Locale.US, "%.1f%%", pct),
                    cat
            });
        }
    }
}
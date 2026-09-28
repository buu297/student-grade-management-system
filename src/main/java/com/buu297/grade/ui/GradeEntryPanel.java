package com.buu297.grade.ui;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.dao.StudentDAO;
import com.buu297.grade.model.*;
import com.buu297.grade.service.GradeCalculationService;
import com.buu297.grade.service.TranscriptReportService;
import com.buu297.grade.ui.components.ModernTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class GradeEntryPanel extends JPanel {
    private final GradeRecordDAO gradeDAO;
    private final CourseDAO courseDAO;
    private final StudentDAO studentDAO;
    private final GradeCalculationService gradeService;
    private final TranscriptReportService reportService;

    private final JComboBox<String> cbSemester;
    private final JComboBox<String> cbDepartment;
    private final JComboBox<String> cbCourse;
    private final JTextField tfSearch;

    private final DefaultTableModel tableModel;
    private final ModernTable gradeTable;

    public GradeEntryPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        this.gradeDAO = DatabaseManager.getInstance().getGradeRecordDAO();
        this.courseDAO = DatabaseManager.getInstance().getCourseDAO();
        this.studentDAO = DatabaseManager.getInstance().getStudentDAO();
        this.gradeService = new GradeCalculationService();
        this.reportService = new TranscriptReportService();

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Grade Entry & Assessment Evaluation");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);

        JLabel subtitle = new JLabel("Manage course gradebooks, perform grading curves, and verify pass/fail criteria.");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_MUTED);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActions.setOpaque(false);

        JButton btnCurve = Theme.createSecondaryButton("⚡ Apply Curve Bonus");
        btnCurve.addActionListener(e -> showCurveDialog());

        JButton btnExportCSV = Theme.createSecondaryButton("Export CSV");
        btnExportCSV.addActionListener(e -> exportCSV());

        JButton btnEditGrade = Theme.createPrimaryButton("+ Enter / Edit Grade");
        btnEditGrade.addActionListener(e -> showEditGradeDialog());

        topActions.add(btnCurve);
        topActions.add(btnExportCSV);
        topActions.add(btnEditGrade);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(topActions, BorderLayout.EAST);

        // 2. Filter Bar
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterCard.setBackground(Theme.BG_CARD);
        filterCard.setBorder(Theme.createCardBorder());

        filterCard.add(new JLabel("Semester:"));
        cbSemester = new JComboBox<>(new String[]{"ALL", "SEM-2026-FALL", "SEM-2026-SPRING", "SEM-2026-SUMMER"});
        filterCard.add(cbSemester);

        filterCard.add(new JLabel("Department:"));
        cbDepartment = new JComboBox<>(new String[]{"ALL", "Computer Science", "Information Systems", "Business Administration", "Electrical Engineering"});
        filterCard.add(cbDepartment);

        filterCard.add(new JLabel("Course:"));
        cbCourse = new JComboBox<>(new String[]{"ALL", "CS101", "CS202", "CS303", "CS404", "IS201", "IS305", "EE210", "BA101"});
        filterCard.add(cbCourse);

        filterCard.add(new JLabel("Search:"));
        tfSearch = new JTextField(12);
        filterCard.add(tfSearch);

        JButton btnFilter = Theme.createPrimaryButton("Filter");
        btnFilter.addActionListener(e -> loadData());
        filterCard.add(btnFilter);

        JButton btnReset = Theme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            cbSemester.setSelectedIndex(0);
            cbDepartment.setSelectedIndex(0);
            cbCourse.setSelectedIndex(0);
            tfSearch.setText("");
            loadData();
        });
        filterCard.add(btnReset);

        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(filterCard, BorderLayout.CENTER);
        add(topContainer, BorderLayout.NORTH);

        // 3. Table
        String[] cols = {"Record ID", "Student ID", "Student Name", "Course", "Credits", "Attendance", "Midterm", "Lab", "Final", "Score (10)", "Letter", "GPA (4.0)", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 10) return LetterGrade.class;
                return Object.class;
            }
        };

        gradeTable = new ModernTable(tableModel);
        JScrollPane scroll = new JScrollPane(gradeTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(scroll, BorderLayout.CENTER);

        loadData();
    }

    public void loadData() {
        String sem = (String) cbSemester.getSelectedItem();
        String dept = (String) cbDepartment.getSelectedItem();
        String course = (String) cbCourse.getSelectedItem();
        String query = tfSearch.getText().trim();

        List<GradeRecord> list = gradeDAO.filterGrades(sem, dept, course, query);
        tableModel.setRowCount(0);

        for (GradeRecord g : list) {
            tableModel.addRow(new Object[]{
                    g.getRecordId(),
                    g.getStudentId(),
                    g.getStudentName(),
                    g.getCourseCode(),
                    g.getCredits() + " cr",
                    g.getAttendanceScore(),
                    g.getMidtermScore(),
                    g.getLabScore(),
                    g.getFinalScore(),
                    g.getTotalScore10(),
                    g.getLetterGrade(),
                    g.getGpa4(),
                    g.getStatus()
            });
        }
    }

    private void showEditGradeDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Record / Update Student Grades", true);
        dialog.setSize(550, 460);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(15, 15));

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 20, 10, 20));

        List<Student> students = studentDAO.findAll();
        JComboBox<Student> stuCombo = new JComboBox<>(students.toArray(new Student[0]));

        List<Course> courses = courseDAO.findAll();
        JComboBox<Course> courseCombo = new JComboBox<>(courses.toArray(new Course[0]));

        JComboBox<String> semCombo = new JComboBox<>(new String[]{"SEM-2026-FALL", "SEM-2026-SPRING", "SEM-2026-SUMMER"});

        JTextField tfAtt = new JTextField("9.0");
        JTextField tfMid = new JTextField("8.5");
        JTextField tfLab = new JTextField("8.5");
        JTextField tfFin = new JTextField("8.0");

        form.add(new JLabel("Select Student:"));
        form.add(stuCombo);
        form.add(new JLabel("Select Course:"));
        form.add(courseCombo);
        form.add(new JLabel("Semester Term:"));
        form.add(semCombo);
        form.add(new JLabel("Attendance Score (0-10):"));
        form.add(tfAtt);
        form.add(new JLabel("Midterm Exam Score (0-10):"));
        form.add(tfMid);
        form.add(new JLabel("Laboratory / Project (0-10):"));
        form.add(tfLab);
        form.add(new JLabel("Final Examination (0-10):"));
        form.add(tfFin);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        JButton btnSubmit = Theme.createPrimaryButton("Calculate & Save");
        JButton btnCancel = Theme.createSecondaryButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());

        btnSubmit.addActionListener(e -> {
            try {
                Student s = (Student) stuCombo.getSelectedItem();
                Course c = (Course) courseCombo.getSelectedItem();
                String sem = (String) semCombo.getSelectedItem();

                BigDecimal att = new BigDecimal(tfAtt.getText().trim());
                BigDecimal mid = new BigDecimal(tfMid.getText().trim());
                BigDecimal lab = new BigDecimal(tfLab.getText().trim());
                BigDecimal fin = new BigDecimal(tfFin.getText().trim());

                GradeRecord res = gradeService.recordOrUpdateGrade(s.getStudentId(), c.getCourseCode(), sem, att, mid, lab, fin, "Manual grade entry");

                JOptionPane.showMessageDialog(dialog,
                        "Grade Successfully Calculated & Saved!\n\n" +
                        "10-Point Score: " + res.getTotalScore10() + "\n" +
                        "Letter Grade: " + res.getLetterGrade().getSymbol() + "\n" +
                        "GPA (4.0): " + res.getGpa4() + "\n" +
                        "Status: " + res.getStatus(),
                        "Grading Complete", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showCurveDialog() {
        String courseCode = (String) cbCourse.getSelectedItem();
        if (courseCode == null || courseCode.equals("ALL")) {
            JOptionPane.showMessageDialog(this, "Please select a specific course in the filter bar first to apply a curve adjustment.");
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter bonus points to add to final exams for " + courseCode + " (e.g. 0.50):", "0.50");
        if (input != null && !input.trim().isEmpty()) {
            try {
                BigDecimal bonus = new BigDecimal(input.trim());
                String sem = (String) cbSemester.getSelectedItem();
                gradeService.applyScoreCurve(courseCode, sem, bonus);
                JOptionPane.showMessageDialog(this, "Curve bonus of +" + bonus + " applied to all enrolled students in " + courseCode + "!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid number: " + ex.getMessage());
            }
        }
    }

    private void exportCSV() {
        String sem = (String) cbSemester.getSelectedItem();
        String dept = (String) cbDepartment.getSelectedItem();
        String course = (String) cbCourse.getSelectedItem();
        List<GradeRecord> list = gradeDAO.filterGrades(sem, dept, course, tfSearch.getText());

        String csv = reportService.exportGradesToCSV(list);
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("grade_master_sheet_" + System.currentTimeMillis() + ".csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                reportService.saveCSVToFile(csv, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Master Grade Sheet successfully exported!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving CSV: " + ex.getMessage());
            }
        }
    }
}
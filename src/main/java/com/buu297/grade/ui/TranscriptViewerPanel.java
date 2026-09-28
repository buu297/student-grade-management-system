package com.buu297.grade.ui;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.StudentDAO;
import com.buu297.grade.model.*;
import com.buu297.grade.service.TranscriptReportService;
import com.buu297.grade.ui.components.ModernTable;
import com.buu297.grade.ui.components.StatCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TranscriptViewerPanel extends JPanel {
    private final StudentDAO studentDAO;
    private final TranscriptReportService transcriptService;

    private final JComboBox<Student> cbStudents;
    private final JComboBox<String> cbSemester;

    private final StatCard cardCredits;
    private final StatCard cardGpa;
    private final StatCard cardScore10;
    private final StatCard cardStanding;

    private final DefaultTableModel transcriptModel;
    private StudentTranscript currentTranscript;

    public TranscriptViewerPanel() {
        setLayout(new BorderLayout(16, 16));
        setBackground(Theme.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        this.studentDAO = DatabaseManager.getInstance().getStudentDAO();
        this.transcriptService = new TranscriptReportService();

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Official Student Transcript & Honor Audit");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT_MAIN);

        JLabel subtitle = new JLabel("Academic standing verification, credit audit, and institutional transcript generation.");
        subtitle.setFont(Theme.FONT_SUBTITLE);
        subtitle.setForeground(Theme.TEXT_MUTED);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActions.setOpaque(false);

        JButton btnPrint = Theme.createPrimaryButton("Print Official Transcript");
        btnPrint.addActionListener(e -> previewTranscriptHTML());
        topActions.add(btnPrint);

        headerPanel.add(titleBox, BorderLayout.WEST);
        headerPanel.add(topActions, BorderLayout.EAST);

        // 2. Filter Bar
        JPanel filterCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        filterCard.setBackground(Theme.BG_CARD);
        filterCard.setBorder(Theme.createCardBorder());

        filterCard.add(new JLabel("Select Student:"));
        List<Student> students = studentDAO.findAll();
        cbStudents = new JComboBox<>(students.toArray(new Student[0]));
        filterCard.add(cbStudents);

        filterCard.add(new JLabel("Academic Term:"));
        cbSemester = new JComboBox<>(new String[]{"SEM-2026-FALL", "SEM-2026-SPRING", "SEM-2026-SUMMER"});
        filterCard.add(cbSemester);

        JButton btnLoad = Theme.createPrimaryButton("Load Transcript");
        btnLoad.addActionListener(e -> loadTranscript());
        filterCard.add(btnLoad);

        // 3. Metrics Summary Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setPreferredSize(new Dimension(800, 100));

        cardCredits = new StatCard("Credits Earned", "0 cr", "Completed credits", Theme.PRIMARY);
        cardGpa = new StatCard("Term GPA (4.0)", "0.00", "Weighted index", Theme.SUCCESS);
        cardScore10 = new StatCard("Average (10.0)", "0.00", "Institutional scale", Theme.PURPLE);
        cardStanding = new StatCard("Academic Standing", "GOOD", "Status", Theme.INFO);

        metricsGrid.add(cardCredits);
        metricsGrid.add(cardGpa);
        metricsGrid.add(cardScore10);
        metricsGrid.add(cardStanding);

        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(filterCard, BorderLayout.CENTER);
        topContainer.add(metricsGrid, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // 4. Transcript Table
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(Theme.BG_CARD);
        centerPanel.setBorder(Theme.createCardBorder());

        JLabel sectionTitle = new JLabel("Enrolled Curriculum & Assessment Line Items");
        sectionTitle.setFont(Theme.FONT_HEADER);
        sectionTitle.setForeground(Theme.TEXT_MAIN);
        centerPanel.add(sectionTitle, BorderLayout.NORTH);

        String[] cols = {"Code", "Course Name", "Credits", "Attendance (10%)", "Midterm (20%)", "Lab (20%)", "Final (50%)", "Score (10)", "Letter", "GPA (4.0)", "Status"};
        transcriptModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 8) return LetterGrade.class;
                return Object.class;
            }
        };

        ModernTable transcriptTable = new ModernTable(transcriptModel);
        JScrollPane scroll = new JScrollPane(transcriptTable);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        centerPanel.add(scroll, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        loadTranscript();
    }

    public void loadTranscript() {
        Student s = (Student) cbStudents.getSelectedItem();
        if (s == null) return;
        String sem = (String) cbSemester.getSelectedItem();

        currentTranscript = transcriptService.generateTranscript(s.getStudentId(), sem);

        cardCredits.setValue(currentTranscript.getCreditsEarned() + " / " + currentTranscript.getCreditsAttempted() + " cr");
        cardGpa.setValue(String.format(java.util.Locale.US, "%.2f", currentTranscript.getSemesterGpa()));
        cardScore10.setValue(String.format(java.util.Locale.US, "%.2f", currentTranscript.getAverageScore10()));
        cardStanding.setValue(currentTranscript.getStanding().getDisplayName());
        cardStanding.setSubtitle("Evaluated for " + sem);

        transcriptModel.setRowCount(0);
        for (GradeRecord g : currentTranscript.getGrades()) {
            transcriptModel.addRow(new Object[]{
                    g.getCourseCode(),
                    g.getCourseName(),
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

    private void previewTranscriptHTML() {
        if (currentTranscript == null) return;
        String html = transcriptService.generateHTMLTranscript(currentTranscript);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Official Transcript Preview", true);
        dialog.setSize(850, 680);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JEditorPane pane = new JEditorPane("text/html", html);
        pane.setEditable(false);
        pane.setCaretPosition(0);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnPrint = Theme.createPrimaryButton("Print Document");
        btnPrint.addActionListener(e -> {
            try { pane.print(); } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, ex.getMessage()); }
        });
        JButton btnClose = Theme.createSecondaryButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());

        bottom.add(btnPrint);
        bottom.add(btnClose);

        dialog.add(new JScrollPane(pane), BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
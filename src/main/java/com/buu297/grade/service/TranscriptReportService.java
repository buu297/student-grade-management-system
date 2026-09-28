package com.buu297.grade.service;

import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TranscriptReportService {
    private final GradeRecordDAO gradeDAO;

    public TranscriptReportService() {
        this.gradeDAO = DatabaseManager.getInstance().getGradeRecordDAO();
    }

    public StudentTranscript generateTranscript(String studentId, String semesterId) {
        return gradeDAO.getStudentTranscript(studentId, semesterId);
    }

    public String exportGradesToCSV(List<GradeRecord> records) {
        StringBuilder sb = new StringBuilder();
        sb.append("Student ID,Student Name,Department,Course Code,Course Name,Credits,Attendance (10%),Midterm (20%),Lab (20%),Final (50%),Composite Score (10),GPA (4.0),Letter Grade,Status,Notes\n");

        for (GradeRecord g : records) {
            sb.append(escape(g.getStudentId())).append(",")
              .append(escape(g.getStudentName())).append(",")
              .append(escape(g.getDepartment())).append(",")
              .append(escape(g.getCourseCode())).append(",")
              .append(escape(g.getCourseName())).append(",")
              .append(g.getCredits()).append(",")
              .append(g.getAttendanceScore()).append(",")
              .append(g.getMidtermScore()).append(",")
              .append(g.getLabScore()).append(",")
              .append(g.getFinalScore()).append(",")
              .append(g.getTotalScore10()).append(",")
              .append(g.getGpa4()).append(",")
              .append(g.getLetterGrade() != null ? g.getLetterGrade().getSymbol() : "").append(",")
              .append(g.getStatus()).append(",")
              .append(escape(g.getNotes())).append("\n");
        }
        return sb.toString();
    }

    public void saveCSVToFile(String csv, File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(csv);
        }
    }

    public String generateHTMLTranscript(StudentTranscript transcript) {
        Student s = transcript.getStudent();
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='utf-8'>");
        html.append("<title>Official Academic Transcript</title>");
        html.append("<style>");
        html.append("body { font-family: 'Segoe UI', Tahoma, sans-serif; margin: 40px; color: #0f172a; background: #fff; }");
        html.append(".header { display: flex; justify-content: space-between; border-bottom: 2px solid #2563eb; padding-bottom: 20px; }");
        html.append(".univ h1 { margin: 0; color: #1e3a8a; font-size: 22px; }");
        html.append(".univ p { margin: 4px 0 0 0; color: #64748b; font-size: 13px; }");
        html.append(".title-box { text-align: right; }");
        html.append(".title-box h2 { margin: 0; color: #2563eb; font-size: 20px; }");
        html.append(".meta-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin: 25px 0; font-size: 14px; background: #f8fafc; padding: 15px; border-radius: 8px; border: 1px solid #e2e8f0; }");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        html.append("th { background: #f1f5f9; padding: 10px; border-bottom: 2px solid #cbd5e1; text-align: left; font-size: 13px; }");
        html.append("td { padding: 10px; border-bottom: 1px solid #e2e8f0; font-size: 13px; }");
        html.append(".kpi-summary { display: flex; gap: 20px; margin-top: 25px; padding: 16px; background: #f8fafc; border-radius: 8px; border: 1px solid #cbd5e1; }");
        html.append(".kpi-item { flex: 1; text-align: center; }");
        html.append(".kpi-val { font-size: 22px; font-weight: bold; color: #2563eb; margin-top: 4px; }");
        html.append(".badge { display: inline-block; padding: 4px 10px; border-radius: 9999px; font-weight: bold; font-size: 11px; }");
        html.append(".standing-pill { display: inline-block; padding: 6px 14px; border-radius: 9999px; font-weight: bold; font-size: 13px; background: ").append(transcript.getStanding().getBgColor()).append("; color: ").append(transcript.getStanding().getTextColor()).append("; }");
        html.append(".footer { margin-top: 50px; text-align: center; color: #94a3b8; font-size: 12px; border-top: 1px solid #e2e8f0; padding-top: 15px; }");
        html.append("</style></head><body>");

        html.append("<div class='header'>");
        html.append("<div class='univ'><h1>UNIVERSITY ACADEMIC REGISTRAR</h1><p>Office of Academic Affairs & Student Records</p></div>");
        html.append("<div class='title-box'><h2>OFFICIAL ACADEMIC TRANSCRIPT</h2><p>Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).append("</p></div>");
        html.append("</div>");

        html.append("<div class='meta-grid'>");
        html.append("<div><strong>Student Name:</strong> ").append(s != null ? s.getFullName() : "N/A").append("<br>");
        html.append("<strong>Student ID:</strong> ").append(s != null ? s.getStudentId() : "").append("<br>");
        html.append("<strong>Major:</strong> ").append(s != null ? s.getMajor() : "").append("</div>");
        html.append("<div><strong>Department:</strong> ").append(s != null ? s.getDepartment() : "").append("<br>");
        html.append("<strong>Academic Term:</strong> ").append(transcript.getSemesterName() != null ? transcript.getSemesterName() : "Fall 2026").append("<br>");
        html.append("<strong>Academic Standing:</strong> <span class='standing-pill'>").append(transcript.getStanding().getDisplayName()).append("</span></div>");
        html.append("</div>");

        html.append("<h3>Coursework & Grade Details</h3>");
        html.append("<table><thead><tr><th>Code</th><th>Course Name</th><th>Credits</th><th>Attendance</th><th>Midterm</th><th>Lab</th><th>Final</th><th>Score (10)</th><th>Letter</th><th>GPA (4.0)</th><th>Status</th></tr></thead><tbody>");
        for (GradeRecord g : transcript.getGrades()) {
            html.append("<tr>");
            html.append("<td><strong>").append(g.getCourseCode()).append("</strong></td>");
            html.append("<td>").append(g.getCourseName()).append("</td>");
            html.append("<td>").append(g.getCredits()).append("</td>");
            html.append("<td>").append(g.getAttendanceScore()).append("</td>");
            html.append("<td>").append(g.getMidtermScore()).append("</td>");
            html.append("<td>").append(g.getLabScore()).append("</td>");
            html.append("<td>").append(g.getFinalScore()).append("</td>");
            html.append("<td><strong>").append(g.getTotalScore10()).append("</strong></td>");
            html.append("<td><strong style='color:#2563eb;'>").append(g.getLetterGrade() != null ? g.getLetterGrade().getSymbol() : "-").append("</strong></td>");
            html.append("<td>").append(g.getGpa4()).append("</td>");
            html.append("<td><span class='badge' style='background:#dcfce7; color:#15803d;'>").append(g.getStatus()).append("</span></td>");
            html.append("</tr>");
        }
        html.append("</tbody></table>");

        html.append("<div class='kpi-summary'>");
        html.append("<div class='kpi-item'><div>Credits Attempted</div><div class='kpi-val'>").append(transcript.getCreditsAttempted()).append("</div></div>");
        html.append("<div class='kpi-item'><div>Credits Earned</div><div class='kpi-val' style='color:#15803d;'>").append(transcript.getCreditsEarned()).append("</div></div>");
        html.append("<div class='kpi-item'><div>Average Score (10.0)</div><div class='kpi-val'>").append(transcript.getAverageScore10()).append("</div></div>");
        html.append("<div class='kpi-item'><div>Semester GPA (4.0)</div><div class='kpi-val' style='color:#2563eb;'>").append(transcript.getSemesterGpa()).append("</div></div>");
        html.append("<div class='kpi-item'><div>Cumulative GPA (CPA)</div><div class='kpi-val' style='color:#4f46e5;'>").append(transcript.getCumulativeGpa()).append("</div></div>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p>This document is an official institutional academic record certified by the University Registrar.</p>");
        html.append("<p>Student Grade Management System (github.com/buu297/student-grade-management-system)</p>");
        html.append("</div></body></html>");

        return html.toString();
    }

    private String escape(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
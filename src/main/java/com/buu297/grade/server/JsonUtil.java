package com.buu297.grade.server;

import com.buu297.grade.model.*;

import java.util.List;
import java.util.Map;

public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String studentToJson(Student s) {
        return "{" +
                "\"studentId\":\"" + escape(s.getStudentId()) + "\"," +
                "\"firstName\":\"" + escape(s.getFirstName()) + "\"," +
                "\"lastName\":\"" + escape(s.getLastName()) + "\"," +
                "\"fullName\":\"" + escape(s.getFullName()) + "\"," +
                "\"email\":\"" + escape(s.getEmail()) + "\"," +
                "\"phone\":\"" + escape(s.getPhone()) + "\"," +
                "\"department\":\"" + escape(s.getDepartment()) + "\"," +
                "\"major\":\"" + escape(s.getMajor()) + "\"," +
                "\"academicYear\":" + s.getAcademicYear() + "," +
                "\"enrollmentStatus\":\"" + escape(s.getEnrollmentStatus()) + "\"" +
                "}";
    }

    public static String studentsToJson(List<Student> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(studentToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String courseToJson(Course c) {
        return "{" +
                "\"courseCode\":\"" + escape(c.getCourseCode()) + "\"," +
                "\"courseName\":\"" + escape(c.getCourseName()) + "\"," +
                "\"department\":\"" + escape(c.getDepartment()) + "\"," +
                "\"credits\":" + c.getCredits() + "," +
                "\"weightAttendance\":" + c.getWeightAttendance() + "," +
                "\"weightMidterm\":" + c.getWeightMidterm() + "," +
                "\"weightLab\":" + c.getWeightLab() + "," +
                "\"weightFinal\":" + c.getWeightFinal() +
                "}";
    }

    public static String coursesToJson(List<Course> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(courseToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String gradeRecordToJson(GradeRecord g) {
        return "{" +
                "\"recordId\":\"" + escape(g.getRecordId()) + "\"," +
                "\"studentId\":\"" + escape(g.getStudentId()) + "\"," +
                "\"studentName\":\"" + escape(g.getStudentName() != null ? g.getStudentName() : "") + "\"," +
                "\"department\":\"" + escape(g.getDepartment() != null ? g.getDepartment() : "") + "\"," +
                "\"courseCode\":\"" + escape(g.getCourseCode()) + "\"," +
                "\"courseName\":\"" + escape(g.getCourseName() != null ? g.getCourseName() : "") + "\"," +
                "\"credits\":" + g.getCredits() + "," +
                "\"semesterId\":\"" + escape(g.getSemesterId()) + "\"," +
                "\"semesterName\":\"" + escape(g.getSemesterName() != null ? g.getSemesterName() : "") + "\"," +
                "\"attendanceScore\":" + g.getAttendanceScore() + "," +
                "\"midtermScore\":" + g.getMidtermScore() + "," +
                "\"labScore\":" + g.getLabScore() + "," +
                "\"finalScore\":" + g.getFinalScore() + "," +
                "\"totalScore10\":" + g.getTotalScore10() + "," +
                "\"gpa4\":" + g.getGpa4() + "," +
                "\"letterGrade\":\"" + (g.getLetterGrade() != null ? g.getLetterGrade().getSymbol() : "") + "\"," +
                "\"status\":\"" + escape(g.getStatus()) + "\"," +
                "\"notes\":\"" + escape(g.getNotes() != null ? g.getNotes() : "") + "\"" +
                "}";
    }

    public static String gradeRecordsToJson(List<GradeRecord> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(gradeRecordToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String transcriptToJson(StudentTranscript t) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"student\":").append(t.getStudent() != null ? studentToJson(t.getStudent()) : "null").append(",");
        sb.append("\"semesterId\":\"").append(escape(t.getSemesterId())).append("\",");
        sb.append("\"semesterName\":\"").append(escape(t.getSemesterName() != null ? t.getSemesterName() : "")).append("\",");
        sb.append("\"creditsAttempted\":").append(t.getCreditsAttempted()).append(",");
        sb.append("\"creditsEarned\":").append(t.getCreditsEarned()).append(",");
        sb.append("\"semesterGpa\":").append(t.getSemesterGpa()).append(",");
        sb.append("\"cumulativeGpa\":").append(t.getCumulativeGpa()).append(",");
        sb.append("\"averageScore10\":").append(t.getAverageScore10()).append(",");
        sb.append("\"standing\":\"").append(t.getStanding().name()).append("\",");
        sb.append("\"standingDisplayName\":\"").append(escape(t.getStanding().getDisplayName())).append("\",");
        sb.append("\"standingTextColor\":\"").append(t.getStanding().getTextColor()).append("\",");
        sb.append("\"standingBgColor\":\"").append(t.getStanding().getBgColor()).append("\",");
        sb.append("\"grades\":").append(gradeRecordsToJson(t.getGrades()));
        sb.append("}");
        return sb.toString();
    }

    public static String analyticsToJson(CourseAnalytics a) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"courseCode\":\"").append(escape(a.getCourseCode())).append("\",");
        sb.append("\"courseName\":\"").append(escape(a.getCourseName() != null ? a.getCourseName() : "")).append("\",");
        sb.append("\"enrolledCount\":").append(a.getEnrolledCount()).append(",");
        sb.append("\"classMean\":").append(a.getClassMean()).append(",");
        sb.append("\"stdDev\":").append(a.getStdDev()).append(",");
        sb.append("\"minScore\":").append(a.getMinScore()).append(",");
        sb.append("\"maxScore\":").append(a.getMaxScore()).append(",");
        sb.append("\"passCount\":").append(a.getPassCount()).append(",");
        sb.append("\"failCount\":").append(a.getFailCount()).append(",");
        sb.append("\"passRatePct\":").append(a.getPassRatePct()).append(",");
        sb.append("\"gradeDistribution\":{");
        int i = 0;
        for (Map.Entry<String, Integer> e : a.getGradeDistribution().entrySet()) {
            if (i++ > 0) sb.append(",");
            sb.append("\"").append(escape(e.getKey())).append("\":").append(e.getValue());
        }
        sb.append("}}");
        return sb.toString();
    }
}
package com.buu297.grade.server;

import com.buu297.grade.config.AppConfig;
import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.dao.CourseDAO;
import com.buu297.grade.dao.GradeRecordDAO;
import com.buu297.grade.dao.StudentDAO;
import com.buu297.grade.model.*;
import com.buu297.grade.service.CourseAnalyticsService;
import com.buu297.grade.service.GradeCalculationService;
import com.buu297.grade.service.TranscriptReportService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

public class GradeApiServer {
    private static final Logger LOGGER = Logger.getLogger(GradeApiServer.class.getName());
    private final int port;
    private final File webRoot;
    private HttpServer server;

    private final StudentDAO studentDAO;
    private final CourseDAO courseDAO;
    private final GradeRecordDAO gradeDAO;
    private final GradeCalculationService gradeService;
    private final TranscriptReportService transcriptService;
    private final CourseAnalyticsService analyticsService;

    public GradeApiServer(int port, File webRoot) {
        this.port = port;
        this.webRoot = webRoot;
        this.studentDAO = DatabaseManager.getInstance().getStudentDAO();
        this.courseDAO = DatabaseManager.getInstance().getCourseDAO();
        this.gradeDAO = DatabaseManager.getInstance().getGradeRecordDAO();
        this.gradeService = new GradeCalculationService();
        this.transcriptService = new TranscriptReportService();
        this.analyticsService = new CourseAnalyticsService();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/status", this::handleStatus);
        server.createContext("/api/students", this::handleStudents);
        server.createContext("/api/courses", this::handleCourses);
        server.createContext("/api/grades", this::handleGrades);
        server.createContext("/api/transcript", this::handleTranscript);
        server.createContext("/api/analytics", this::handleAnalytics);

        server.createContext("/", new StaticFileHandler(webRoot));

        server.setExecutor(null);
        server.start();
        LOGGER.info("Student Grade Web Portal & API Server running at http://localhost:" + port);
    }

    public void stop() {
        if (server != null) server.stop(0);
    }

    private void handleStatus(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
        String json = "{\"status\":\"ONLINE\",\"database\":\"" + JsonUtil.escape(DatabaseManager.getInstance().getDatabaseStatusDescription()) + "\"}";
        sendJsonResponse(exchange, 200, json);
    }

    private void handleStudents(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
        List<Student> students = studentDAO.findAll();
        sendJsonResponse(exchange, 200, JsonUtil.studentsToJson(students));
    }

    private void handleCourses(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
        List<Course> courses = courseDAO.findAll();
        sendJsonResponse(exchange, 200, JsonUtil.coursesToJson(courses));
    }

    private void handleGrades(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }

        if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            String body = readBody(exchange);
            Map<String, String> data = parseSimpleJson(body);

            try {
                String stuId = data.get("studentId");
                String courseCode = data.get("courseCode");
                String semId = data.getOrDefault("semesterId", "SEM-2026-FALL");
                BigDecimal att = new BigDecimal(data.getOrDefault("attendance", "0"));
                BigDecimal mid = new BigDecimal(data.getOrDefault("midterm", "0"));
                BigDecimal lab = new BigDecimal(data.getOrDefault("lab", "0"));
                BigDecimal fin = new BigDecimal(data.getOrDefault("finalScore", "0"));
                String notes = data.get("notes");

                GradeRecord record = gradeService.recordOrUpdateGrade(stuId, courseCode, semId, att, mid, lab, fin, notes);
                sendJsonResponse(exchange, 200, "{\"success\":true,\"record\":" + JsonUtil.gradeRecordToJson(record) + "}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
            }
            return;
        }

        Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
        String stuId = query.get("studentId");
        String courseCode = query.get("courseCode");
        String semId = query.get("semesterId");

        List<GradeRecord> list;
        if (stuId != null && !stuId.isEmpty()) {
            list = gradeDAO.findByStudentId(stuId);
        } else if (courseCode != null && !courseCode.isEmpty()) {
            list = gradeDAO.findByCourseCode(courseCode, semId);
        } else {
            list = gradeDAO.findAll();
        }
        sendJsonResponse(exchange, 200, JsonUtil.gradeRecordsToJson(list));
    }

    private void handleTranscript(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
        Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
        String stuId = query.getOrDefault("studentId", "STU202601");
        String semId = query.getOrDefault("semesterId", "SEM-2026-FALL");

        StudentTranscript transcript = transcriptService.generateTranscript(stuId, semId);
        sendJsonResponse(exchange, 200, JsonUtil.transcriptToJson(transcript));
    }

    private void handleAnalytics(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) { exchange.sendResponseHeaders(204, -1); return; }
        Map<String, String> query = parseQuery(exchange.getRequestURI().getQuery());
        String courseCode = query.getOrDefault("courseCode", "CS101");
        String semId = query.getOrDefault("semesterId", "SEM-2026-FALL");

        CourseAnalytics analytics = analyticsService.getCourseAnalytics(courseCode, semId);
        sendJsonResponse(exchange, 200, JsonUtil.analyticsToJson(analytics));
    }

    private void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }

    private void sendJsonResponse(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[2048];
            int r;
            while ((r = is.read(buf)) != -1) baos.write(buf, 0, r);
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isEmpty()) return map;
        for (String pair : query.split("&")) {
            int idx = pair.indexOf("=");
            try {
                if (idx > 0) map.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                else map.put(URLDecoder.decode(pair, "UTF-8"), "");
            } catch (Exception ignored) {}
        }
        return map;
    }

    private Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String t = json.trim();
        if (t.startsWith("{")) t = t.substring(1);
        if (t.endsWith("}")) t = t.substring(0, t.length() - 1);
        for (String token : t.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)")) {
            String[] kv = token.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (kv.length == 2) {
                map.put(kv[0].trim().replace("\"", ""), kv[1].trim().replace("\"", ""));
            }
        }
        return map;
    }
}
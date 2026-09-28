package com.buu297.grade;

import com.buu297.grade.config.AppConfig;
import com.buu297.grade.config.DatabaseManager;
import com.buu297.grade.server.GradeApiServer;
import com.buu297.grade.ui.AcademicConsoleApp;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("          STUDENT GRADE MANAGEMENT SYSTEM");
        System.out.println("  Dual Interfaces: Web Grade Portal & Java Swing Registrar Console");
        System.out.println("  Stack: HTML, CSS, JavaScript, Java Swing, MySQL");
        System.out.println("  Author: buu297 (github.com/buu297/student-grade-management-system)");
        System.out.println("==================================================================");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        System.out.println("[DB STATUS] " + dbManager.getDatabaseStatusDescription());

        boolean startServer = true;
        boolean startGui = true;

        if (args.length > 0) {
            if ("--server".equalsIgnoreCase(args[0]) || "-s".equalsIgnoreCase(args[0])) {
                startGui = false;
            } else if ("--admin".equalsIgnoreCase(args[0]) || "-a".equalsIgnoreCase(args[0])) {
                startServer = false;
            }
        }

        if (startServer) {
            int port = AppConfig.getInt("server.port", 8081);
            File webRoot = new File("web");
            if (!webRoot.exists()) webRoot = new File("./web");

            try {
                GradeApiServer apiServer = new GradeApiServer(port, webRoot);
                apiServer.start();
                System.out.println("[WEB GRADE PORTAL] http://localhost:" + port);
                System.out.println("[REST API]         http://localhost:" + port + "/api/students");
                System.out.println("[REST API]         http://localhost:" + port + "/api/courses");
                System.out.println("[REST API]         http://localhost:" + port + "/api/grades");
                System.out.println("[REST API]         http://localhost:" + port + "/api/transcript");
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to start HTTP Web Server: " + e.getMessage());
            }
        }

        if (startGui) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SwingUtilities.invokeLater(() -> {
                AcademicConsoleApp app = new AcademicConsoleApp();
                app.setVisible(true);
                System.out.println("[SWING CONSOLE] Academic Registrar & Dean's Console launched successfully.");
            });
        }
    }
}
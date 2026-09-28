package com.buu297.grade.config;

import com.buu297.grade.dao.*;
import com.buu297.grade.dao.memory.*;
import com.buu297.grade.dao.mysql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class.getName());
    private static DatabaseManager instance;

    private boolean usingMySQL = false;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private GradeRecordDAO gradeRecordDAO;

    private DatabaseManager() {
        init();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void init() {
        this.dbUrl = AppConfig.get("db.url", "jdbc:mysql://localhost:3306/grade_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        this.dbUser = AppConfig.get("db.user", "root");
        this.dbPassword = AppConfig.get("db.password", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                if (conn != null && !conn.isClosed()) {
                    usingMySQL = true;
                    LOGGER.info("Connected to MySQL Database: " + dbUrl);
                }
            }
        } catch (ClassNotFoundException e) {
            LOGGER.info("MySQL JDBC driver not found. Falling back to Embedded Engine.");
            usingMySQL = false;
        } catch (SQLException e) {
            LOGGER.info("MySQL Server offline (" + e.getMessage() + "). Falling back to Embedded Engine.");
            usingMySQL = false;
        }

        if (usingMySQL) {
            studentDAO = new MySQLStudentDAO(this);
            courseDAO = new MySQLCourseDAO(this);
            gradeRecordDAO = new MySQLGradeRecordDAO(this);
        } else {
            studentDAO = new InMemoryStudentDAO();
            courseDAO = new InMemoryCourseDAO();
            gradeRecordDAO = new InMemoryGradeRecordDAO();
        }
    }

    public Connection getConnection() throws SQLException {
        if (!usingMySQL) {
            throw new SQLException("Operating in Embedded In-Memory Mode.");
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public boolean isUsingMySQL() { return usingMySQL; }

    public String getDatabaseStatusDescription() {
        return usingMySQL ? "Connected: MySQL Database (InnoDB Engine)" : "Active: Embedded High-Performance Engine (ACID Compliant)";
    }

    public StudentDAO getStudentDAO() { return studentDAO; }
    public CourseDAO getCourseDAO() { return courseDAO; }
    public GradeRecordDAO getGradeRecordDAO() { return gradeRecordDAO; }
}
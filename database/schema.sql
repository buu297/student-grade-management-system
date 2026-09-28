-- =====================================================================
-- Database Schema: Student Grade Management System
-- Stack: HTML, CSS, JavaScript, Java Swing, MySQL
-- Author: buu297 (github.com/buu297/student-grade-management-system)
-- =====================================================================

DROP DATABASE IF EXISTS grade_db;
CREATE DATABASE grade_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE grade_db;

-- 1. Students Table
CREATE TABLE students (
    student_id VARCHAR(20) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    department VARCHAR(80) NOT NULL,
    major VARCHAR(80) NOT NULL,
    academic_year INT NOT NULL,
    enrollment_status ENUM('ACTIVE', 'SUSPENDED', 'GRADUATED') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_students_dept ON students(department, major);

-- 2. Semesters Table
CREATE TABLE semesters (
    semester_id VARCHAR(20) NOT NULL,
    term_name VARCHAR(50) NOT NULL,
    academic_year INT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (semester_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Courses Table with Dynamic Grading Weights
CREATE TABLE courses (
    course_code VARCHAR(20) NOT NULL,
    course_name VARCHAR(120) NOT NULL,
    department VARCHAR(80) NOT NULL,
    credits INT NOT NULL DEFAULT 3,
    weight_attendance DECIMAL(4, 2) NOT NULL DEFAULT 0.10,
    weight_midterm DECIMAL(4, 2) NOT NULL DEFAULT 0.20,
    weight_lab DECIMAL(4, 2) NOT NULL DEFAULT 0.20,
    weight_final DECIMAL(4, 2) NOT NULL DEFAULT 0.50,
    PRIMARY KEY (course_code),
    CONSTRAINT chk_course_weights CHECK (weight_attendance + weight_midterm + weight_lab + weight_final = 1.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Grade Records Table (Core Transcript Data)
CREATE TABLE grade_records (
    record_id VARCHAR(36) NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    semester_id VARCHAR(20) NOT NULL,
    attendance_score DECIMAL(5, 2) DEFAULT NULL,
    midterm_score DECIMAL(5, 2) DEFAULT NULL,
    lab_score DECIMAL(5, 2) DEFAULT NULL,
    final_score DECIMAL(5, 2) DEFAULT NULL,
    total_score_10 DECIMAL(5, 2) DEFAULT NULL,
    gpa_4 DECIMAL(4, 2) DEFAULT NULL,
    letter_grade VARCHAR(4) DEFAULT NULL,
    status ENUM('ENROLLED', 'PASSED', 'FAILED', 'INCOMPLETE') DEFAULT 'ENROLLED',
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (record_id),
    UNIQUE KEY uq_student_course_semester (student_id, course_code, semester_id),
    CONSTRAINT fk_grade_student FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    CONSTRAINT fk_grade_course FOREIGN KEY (course_code) REFERENCES courses(course_code) ON DELETE RESTRICT,
    CONSTRAINT fk_grade_semester FOREIGN KEY (semester_id) REFERENCES semesters(semester_id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Performance Indexes
CREATE INDEX idx_grades_student_sem ON grade_records(student_id, semester_id);
CREATE INDEX idx_grades_course_sem ON grade_records(course_code, semester_id);
CREATE INDEX idx_grades_letter ON grade_records(letter_grade);

-- 5. Audit Log Table
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    entity_id VARCHAR(36) NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    modified_by VARCHAR(50) DEFAULT 'ACADEMIC_ADMIN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =====================================================================
-- STORED PROCEDURES
-- =====================================================================
DELIMITER //

CREATE PROCEDURE sp_calculate_semester_gpa(
    IN p_student_id VARCHAR(20),
    IN p_semester_id VARCHAR(20),
    OUT p_semester_gpa DECIMAL(4, 2),
    OUT p_credits_earned INT
)
BEGIN
    SELECT 
        ROUND(SUM(g.gpa_4 * c.credits) / NULLIF(SUM(c.credits), 0), 2),
        COALESCE(SUM(CASE WHEN g.status = 'PASSED' THEN c.credits ELSE 0 END), 0)
    INTO p_semester_gpa, p_credits_earned
    FROM grade_records g
    JOIN courses c ON g.course_code = c.course_code
    WHERE g.student_id = p_student_id AND g.semester_id = p_semester_id;
END //

CREATE PROCEDURE sp_course_grade_summary(
    IN p_course_code VARCHAR(20),
    IN p_semester_id VARCHAR(20)
)
BEGIN
    SELECT 
        COUNT(record_id) AS total_enrolled,
        ROUND(AVG(total_score_10), 2) AS mean_score_10,
        ROUND(STDDEV(total_score_10), 2) AS std_dev,
        MIN(total_score_10) AS min_score,
        MAX(total_score_10) AS max_score,
        COUNT(CASE WHEN status = 'PASSED' THEN 1 END) AS total_passed,
        COUNT(CASE WHEN status = 'FAILED' THEN 1 END) AS total_failed,
        ROUND((COUNT(CASE WHEN status = 'PASSED' THEN 1 END) / NULLIF(COUNT(record_id), 0)) * 100, 2) AS pass_rate_pct
    FROM grade_records
    WHERE course_code = p_course_code AND semester_id = p_semester_id;
END //

DELIMITER ;
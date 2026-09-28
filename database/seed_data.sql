USE grade_db;

-- 1. Insert Semesters
INSERT INTO semesters (semester_id, term_name, academic_year, start_date, end_date, is_active) VALUES
('SEM-2026-FALL',   'Fall Semester 2026',   2026, '2026-09-01', '2026-12-31', TRUE),
('SEM-2026-SPRING', 'Spring Semester 2026', 2026, '2026-01-15', '2026-05-30', FALSE),
('SEM-2026-SUMMER', 'Summer Term 2026',     2026, '2026-06-15', '2026-08-20', FALSE);

-- 2. Insert Students
INSERT INTO students (student_id, first_name, last_name, email, phone, department, major, academic_year, enrollment_status) VALUES
('STU202601', 'An',     'Nguyen Van', 'an.nguyen@university.edu.vn',   '+84 901 234 567', 'Computer Science',       'Software Engineering',    2026, 'ACTIVE'),
('STU202602', 'Bich',   'Tran Thi',   'bich.tran@university.edu.vn',   '+84 912 345 678', 'Information Systems',    'Data Analytics',          2026, 'ACTIVE'),
('STU202603', 'Cuong',  'Le Quoc',    'cuong.le@university.edu.vn',    '+84 923 456 789', 'Computer Science',       'Artificial Intelligence', 2025, 'ACTIVE'),
('STU202604', 'Duc',    'Pham Minh',  'duc.pham@university.edu.vn',    '+84 934 567 890', 'Business Administration', 'International Business', 2026, 'ACTIVE'),
('STU202605', 'Bao',    'Hoang Gia',  'bao.hoang@university.edu.vn',   '+84 945 678 901', 'Electrical Engineering',  'Embedded Systems',        2024, 'ACTIVE'),
('STU202606', 'Ha',     'Vu Thanh',   'ha.vu@university.edu.vn',       '+84 956 789 012', 'Computer Science',       'Cybersecurity',           2025, 'ACTIVE'),
('STU202607', 'Kiet',   'Dang Tuan',  'kiet.dang@university.edu.vn',   '+84 967 890 123', 'Business Administration', 'Finance & Banking',       2026, 'ACTIVE'),
('STU202608', 'Linh',   'Do Ngoc',    'linh.do@university.edu.vn',     '+84 978 901 234', 'Information Systems',    'E-Commerce',              2026, 'ACTIVE');

-- 3. Insert Courses
INSERT INTO courses (course_code, course_name, department, credits, weight_attendance, weight_midterm, weight_lab, weight_final) VALUES
('CS101', 'Introduction to Computer Science', 'Computer Science',       3, 0.10, 0.20, 0.20, 0.50),
('CS202', 'Data Structures & Algorithms',     'Computer Science',       4, 0.10, 0.20, 0.30, 0.40),
('CS303', 'Database Management Systems',      'Computer Science',       3, 0.10, 0.20, 0.20, 0.50),
('CS404', 'Artificial Intelligence & ML',     'Computer Science',       4, 0.10, 0.25, 0.25, 0.40),
('IS201', 'Enterprise Information Systems',   'Information Systems',    3, 0.10, 0.20, 0.20, 0.50),
('IS305', 'Business Data Analytics & BI',     'Information Systems',    3, 0.10, 0.20, 0.30, 0.40),
('EE210', 'Digital Logic & Microprocessors',  'Electrical Engineering', 4, 0.10, 0.20, 0.30, 0.40),
('BA101', 'Principles of Modern Management',  'Business Administration',3, 0.10, 0.30, 0.10, 0.50);

-- 4. Insert Grade Records (Pre-calculated weighted scores)
-- Score mapping standard:
-- >= 9.0: A+ (4.0), 8.5-8.9: A (3.7), 8.0-8.4: B+ (3.5), 7.0-7.9: B (3.0),
-- 6.5-6.9: C+ (2.5), 5.5-6.4: C (2.0), 5.0-5.4: D+ (1.5), 4.0-4.9: D (1.0), < 4.0: F (0.0)

INSERT INTO grade_records (record_id, student_id, course_code, semester_id, attendance_score, midterm_score, lab_score, final_score, total_score_10, gpa_4, letter_grade, status, notes) VALUES
-- Student STU202601 (Nguyen Van An - Outstanding Dean's List)
('GR-001', 'STU202601', 'CS101', 'SEM-2026-FALL', 10.0, 9.5, 9.0, 9.0, 9.20, 4.0, 'A+', 'PASSED', 'Exemplary project work'),
('GR-002', 'STU202601', 'CS202', 'SEM-2026-FALL', 10.0, 9.0, 8.5, 8.5, 8.75, 3.7, 'A',  'PASSED', 'Strong algorithmic proofs'),
('GR-003', 'STU202601', 'CS303', 'SEM-2026-FALL',  9.5, 8.5, 9.0, 9.0, 8.95, 3.7, 'A',  'PASSED', 'Optimized query design'),
('GR-004', 'STU202601', 'IS201', 'SEM-2026-FALL', 10.0, 9.0, 9.5, 9.5, 9.45, 4.0, 'A+', 'PASSED', 'Full marks on ER modeling'),

-- Student STU202602 (Tran Thi Bich - High Distinction)
('GR-005', 'STU202602', 'CS101', 'SEM-2026-FALL',  9.0, 8.0, 8.5, 8.5, 8.45, 3.5, 'B+', 'PASSED', 'Regular active attendance'),
('GR-006', 'STU202602', 'IS201', 'SEM-2026-FALL', 10.0, 8.5, 9.0, 8.5, 8.75, 3.7, 'A',  'PASSED', 'Good enterprise analysis'),
('GR-007', 'STU202602', 'IS305', 'SEM-2026-FALL',  9.0, 9.0, 8.5, 9.0, 8.85, 3.7, 'A',  'PASSED', 'Excellent Tableau dashboards'),

-- Student STU202603 (Le Quoc Cuong - AI Specialist)
('GR-008', 'STU202603', 'CS202', 'SEM-2026-FALL',  8.5, 7.5, 8.0, 7.0, 7.55, 3.0, 'B',  'PASSED', 'Solid coursework'),
('GR-009', 'STU202603', 'CS303', 'SEM-2026-FALL',  9.0, 8.0, 8.5, 8.0, 8.20, 3.5, 'B+', 'PASSED', 'Passed normalization lab'),
('GR-010', 'STU202603', 'CS404', 'SEM-2026-FALL', 10.0, 9.5, 9.5, 9.0, 9.35, 4.0, 'A+', 'PASSED', 'Outstanding CNN architecture'),

-- Student STU202604 (Pham Minh Duc - Business)
('GR-011', 'STU202604', 'BA101', 'SEM-2026-FALL',  9.0, 8.0, 8.0, 7.5, 7.85, 3.0, 'B',  'PASSED', 'Good case presentation'),
('GR-012', 'STU202604', 'IS201', 'SEM-2026-FALL',  8.0, 7.0, 7.5, 6.5, 6.95, 2.5, 'C+', 'PASSED', 'Satisfactory understanding'),

-- Student STU202605 (Hoang Gia Bao - Embedded)
('GR-013', 'STU202605', 'CS101', 'SEM-2026-FALL',  9.0, 8.5, 8.0, 7.5, 7.95, 3.0, 'B',  'PASSED', 'Good C programming'),
('GR-014', 'STU202605', 'EE210', 'SEM-2026-FALL', 10.0, 9.0, 9.0, 8.5, 8.90, 3.7, 'A',  'PASSED', 'Excellent FPGA design lab'),

-- Student STU202606 (Vu Thanh Ha - Cybersecurity)
('GR-015', 'STU202606', 'CS202', 'SEM-2026-FALL',  9.5, 8.5, 9.0, 8.5, 8.75, 3.7, 'A',  'PASSED', 'Solid problem solving'),
('GR-016', 'STU202606', 'CS303', 'SEM-2026-FALL',  8.5, 7.5, 8.0, 7.5, 7.70, 3.0, 'B',  'PASSED', 'Good SQL injection security test'),

-- Student STU202607 (Dang Tuan Kiet - Academic Warning Case)
('GR-017', 'STU202607', 'BA101', 'SEM-2026-FALL',  6.0, 5.0, 5.0, 4.0, 4.60, 1.0, 'D',  'PASSED', 'Low attendance record'),
('GR-018', 'STU202607', 'IS201', 'SEM-2026-FALL',  5.0, 4.0, 4.0, 3.0, 3.60, 0.0, 'F',  'FAILED', 'Failed final exam threshold'),

-- Student STU202608 (Do Ngoc Linh)
('GR-019', 'STU202608', 'IS201', 'SEM-2026-FALL',  9.0, 8.0, 8.5, 8.0, 8.20, 3.5, 'B+', 'PASSED', 'Good group coordination'),
('GR-020', 'STU202608', 'IS305', 'SEM-2026-FALL',  8.5, 7.5, 8.0, 7.5, 7.75, 3.0, 'B',  'PASSED', 'Clean predictive model');

-- 5. Audit Log Seed
INSERT INTO audit_logs (entity_id, action, details) VALUES
('INIT', 'SYSTEM_SEED', 'Academic database seeded with 8 students, 8 courses, and 20 grade records.');
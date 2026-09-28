USE grade_db;

-- 1. Student Cumulative GPA (CPA) Weighted Query
SELECT 
    s.student_id,
    CONCAT(s.last_name, ' ', s.first_name) AS full_name,
    s.department,
    COUNT(g.record_id) AS courses_enrolled,
    SUM(c.credits) AS total_credits_attempted,
    SUM(CASE WHEN g.status = 'PASSED' THEN c.credits ELSE 0 END) AS credits_earned,
    ROUND(SUM(g.gpa_4 * c.credits) / NULLIF(SUM(c.credits), 0), 2) AS cumulative_gpa,
    CASE 
        WHEN ROUND(SUM(g.gpa_4 * c.credits) / NULLIF(SUM(c.credits), 0), 2) >= 3.60 THEN 'DEANS_LIST'
        WHEN ROUND(SUM(g.gpa_4 * c.credits) / NULLIF(SUM(c.credits), 0), 2) >= 3.20 THEN 'DISTINCTION'
        WHEN ROUND(SUM(g.gpa_4 * c.credits) / NULLIF(SUM(c.credits), 0), 2) >= 2.50 THEN 'GOOD_STANDING'
        WHEN ROUND(SUM(g.gpa_4 * c.credits) / NULLIF(SUM(c.credits), 0), 2) >= 2.00 THEN 'SATISFACTORY'
        ELSE 'ACADEMIC_WARNING'
    END AS academic_standing
FROM students s
LEFT JOIN grade_records g ON s.student_id = g.student_id
LEFT JOIN courses c ON g.course_code = c.course_code
GROUP BY s.student_id, s.last_name, s.first_name, s.department
ORDER BY cumulative_gpa DESC;

-- 2. Course Bell Curve & Grade Distribution Analytics
SELECT 
    c.course_code,
    c.course_name,
    COUNT(g.record_id) AS enrolled_students,
    ROUND(AVG(g.total_score_10), 2) AS class_mean,
    ROUND(STDDEV(g.total_score_10), 2) AS class_std_dev,
    MIN(g.total_score_10) AS lowest_score,
    MAX(g.total_score_10) AS highest_score,
    COUNT(CASE WHEN g.letter_grade IN ('A+', 'A') THEN 1 END) AS count_excellent_A,
    COUNT(CASE WHEN g.letter_grade IN ('B+', 'B') THEN 1 END) AS count_good_B,
    COUNT(CASE WHEN g.letter_grade IN ('C+', 'C') THEN 1 END) AS count_average_C,
    COUNT(CASE WHEN g.letter_grade IN ('D+', 'D') THEN 1 END) AS count_marginal_D,
    COUNT(CASE WHEN g.letter_grade = 'F' THEN 1 END) AS count_failed_F
FROM courses c
JOIN grade_records g ON c.course_code = g.course_code
WHERE g.semester_id = 'SEM-2026-FALL'
GROUP BY c.course_code, c.course_name
ORDER BY class_mean DESC;
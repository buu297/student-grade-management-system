# Database Architecture: Student Grade Management System

## Overview
This directory contains the schema DDL, test datasets, and academic performance analytical queries for the **Student Grade Management System**.

## Tables
1. **`students`**: Student demographics, enrollment status, major and department.
2. **`semesters`**: Academic calendar terms (Fall, Spring, Summer).
3. **`courses`**: Course catalog with credit values and customizable assessment weightings (Attendance, Midterm, Lab, Final).
4. **`grade_records`**: Enrolled course results with itemized component scores, calculated 10-point score, 4.0 GPA, letter grade, and pass/fail status.
5. **`audit_logs`**: Immutable history of grade alterations.

## Setup
Run `setup_mysql.bat` or execute in MySQL:
```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed_data.sql
```
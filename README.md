# 📜 Student Grade Management System

[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/)
[![GUI](https://img.shields.io/badge/GUI-Java%20Swing-blue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![Web Portal](https://img.shields.io/badge/Web%20Portal-HTML5%20%7C%20CSS3%20%7C%20ES6+-yellow.svg)]()
[![Database](https://img.shields.io/badge/Database-MySQL%208.0%20(InnoDB)-00758F.svg)](https://www.mysql.com/)
[![Academic Scale](https://img.shields.io/badge/Grading-Dual%20Scale%20(4.0%20GPA%20%7C%2010.0%20Score)-success.svg)]()
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)]()

A comprehensive, institutional-grade academic performance tracking and grade management system engineered in **Java Swing**, **HTML5/CSS3/JavaScript**, and **MySQL**. 

Designed for universities and higher education institutions, the system provides **dual interfaces** (an interactive online **Student & Lecturer Grade Web Portal** and a desktop **Dean & Academic Registrar Console**) equipped with a multi-criteria weighted grading engine, cumulative GPA/CPA calculators, bell curve analytics, and official transcript generators.

---

## 🌟 Key Engineering Highlights

- **Dual-Interface Architecture**:
  - 🌐 **Student & Lecturer Grade Web Portal (HTML5, CSS3, Modern JavaScript)**: Responsive web portal where students can review their enrolled course breakdown (Attendance, Midterm, Labs, Final Exam), track semester GPA/CPA, view academic honors, and download/print official academic transcripts.
  - 🖥️ **Dean & Academic Registrar Console (Java Swing)**: Desktop application for academic affairs officers featuring real-time enrollment metrics, quick gradebook entry, curve adjustments, honor roll auditing, and grade master sheet CSV exports.
- **Dual-Scale Weighted Grading Engine**:
  - Supports customizable course assessment weighting ($10\%$ Attendance, $20\%$ Midterm, $20\%$ Lab/Project, $50\%$ Final Examination).
  - Automatically maps 10-point raw scores to **Letter Grades** ($A+, A, B+, B, C+, C, D+, D, F$) and **4.0 GPA scale**.
  - Computes credit-weighted Semester GPA and Cumulative GPA (CPA):
    $$\text{CPA} = \frac{\sum (\text{GPA}_i \times \text{Credits}_i)}{\sum \text{Credits}_i}$$
- **Institutional Academic Standing & Honors Determination**:
  - Automatically evaluates academic standing based on cumulative GPA:
    - $\ge 3.60$: **Dean's List / High Distinction** 🏆
    - $3.20 - 3.59$: **First Class / Distinction** ⭐
    - $2.50 - 3.19$: **Good Standing** ✔
    - $2.00 - 2.49$: **Satisfactory**
    - $1.50 - 1.99$: **Academic Warning** ⚠️
    - $< 1.50$: **Academic Probation** ❌
- **Statistical Analytics & Bell Curve Normal Distribution**:
  - Calculates class enrollment size, Class Mean ($\mu$), Standard Deviation ($\sigma$), Minimum/Maximum scores, and Pass Rate %.
  - Generates letter grade frequency histograms to analyze course grading curves.
- **Zero-Friction Dual Execution Engine**:
  - Seamlessly integrates with **MySQL 8.0 (InnoDB)**.
  - Automatically switches to an embedded in-memory transactional database if MySQL is not running, ensuring **100% out-of-the-box 1-click execution** with zero setup errors!

---

## 🏛️ System Architecture

```
+---------------------------------------------------------------------------------------------------+
|                                 STUDENT GRADE MANAGEMENT SYSTEM                                   |
+---------------------------------------------------------------------------------------------------+
|                                                                                                   |
|    [ STUDENT & LECTURER PORTAL ]                                [ REGISTRAR & DEAN CONSOLE ]      |
|  +-------------------------------+                            +------------------------------+    |
|  |   Student Grade Web Portal    |                            |    Java Swing Desktop App    |    |
|  |   HTML5 / CSS3 / ES6+ JS      |                            | Modern Card Layout & Theme   |    |
|  +-------------------------------+                            +------------------------------+    |
|         |                 |                                                  |                    |
|  (REST API / HTTP)   (Direct HTML)                                   (Desktop Event Bus)          |
|         |                 |                                                  |                    |
|         v                 v                                                  v                    |
|  +-------------------------------------------------------------------------------------------+    |
|  |                         ACADEMIC APPLICATION ENGINE (Java 21 LTS)                         |    |
|  |                                                                                           |    |
|  |  +----------------------+  +--------------------------+  +-----------------------------+  |    |
|  |  | Embedded HTTP Server |  | GradeCalculationService  |  | CourseAnalyticsService      |  |    |
|  |  | (Port 8081 REST)     |  | [Dual-Scale GPA & Curve] |  | [Bell Curve & Mean / StdDev]|  |    |
|  |  +----------------------+  +--------------------------+  +-----------------------------+  |    |
|  |                                         |                                                 |    |
|  |                            +--------------------------+                                   |    |
|  |                            |     DatabaseManager      |                                   |    |
|  |                            +--------------------------+                                   |    |
|  +-----------------------------------------|-------------------------------------------------+    |
|                                            |                                                      |
|                   +------------------------+------------------------+                             |
|                   |                                                 |                             |
|                   v                                                 v                             |
|     +---------------------------+                     +---------------------------+               |
|     |     MySQL 8.0 (InnoDB)    |                     |   Embedded Engine Store   |               |
|     |  * Relational Schema      |                     |  * Thread-Safe Storage    |               |
|     |  * GPA Stored Procedures  |                     |  * Pre-loaded Seed Data   |               |
|     |  * Composite Indexes      |                     |  * Zero-Setup Fallback    |               |
|     +---------------------------+                     +---------------------------+               |
|                                                                                                   |
+---------------------------------------------------------------------------------------------------+
```

---

## 📐 Grading Formula & Standard Conversion Matrix

The composite 10-point course score is evaluated via weighted assessment components:
$$\text{Total Score}_{10} = w_{\text{att}} \times \text{Att} + w_{\text{mid}} \times \text{Mid} + w_{\text{lab}} \times \text{Lab} + w_{\text{final}} \times \text{Final}$$

### Institutional Dual-Scale Mapping Table

| 10-Point Score Range | Letter Grade | 4.0 Scale GPA | Qualitative Classification | Result |
|---|---|---|---|---|
| **$9.0 - 10.0$** | **A+** | **4.0** | Excellent (High Distinction) | Passed |
| **$8.5 - 8.9$** | **A** | **3.7** | Excellent | Passed |
| **$8.0 - 8.4$** | **B+** | **3.5** | Very Good | Passed |
| **$7.0 - 7.9$** | **B** | **3.0** | Good | Passed |
| **$6.5 - 6.9$** | **C+** | **2.5** | Fairly Good | Passed |
| **$5.5 - 6.4$** | **C** | **2.0** | Average | Passed |
| **$5.0 - 5.4$** | **D+** | **1.5** | Marginal Pass | Passed |
| **$4.0 - 4.9$** | **D** | **1.0** | Pass | Passed |
| **$< 4.0$** | **F** | **0.0** | Fail (Unearned Credits) | Failed |

---

## 📊 Database Schema (Relational DDL)

```
  +--------------------+         +--------------------+         +--------------------+
  |      students      | 1     * |   grade_records    | *     1 |      courses       |
  +--------------------+---------+--------------------+---------+--------------------+
  | PK  student_id     |         | PK  record_id      |         | PK  course_code    |
  |     first_name     |         | FK  student_id     |         |     course_name    |
  |     last_name      |         | FK  course_code    |         |     department     |
  |     email (UQ)     |         | FK  semester_id    |         |     credits        |
  |     phone          |         |     attendance_score|        |     weight_att     |
  |     department     |         |     midterm_score  |         |     weight_mid     |
  |     major          |         |     lab_score      |         |     weight_lab     |
  |     academic_year  |         |     final_score    |         |     weight_final   |
  |     enrollment_stat|         |     total_score_10 |         +--------------------+
  +--------------------+         |     gpa_4          |
                                 |     letter_grade   |
                                 |     status         |
                                 |     updated_at     |
                                 +--------------------+
```

---

## 🖥️ User Interfaces

### 1. Student & Lecturer Grade Web Portal
- **Active Student Switcher**: Inspect performance metrics across different student profiles (*Nguyen Van An*, *Tran Thi Bich*, *Le Quoc Cuong*, *Dang Tuan Kiet*).
- **Academic KPI Cards**: Displays Semester GPA (4.0), 10-point Average, Credits Completed, and Academic Standing.
- **Curriculum Gradebook Table**: Detailed line-item breakdown of Attendance, Midterm, Lab, Final, Score 10, Letter Grade, GPA, and Status.
- **One-Click Official Transcript**: Generates a certified, printable academic transcript complete with institutional seal formatting.

### 2. Dean & Academic Registrar Console (Java Swing)
- **Executive Dashboard**: Aggregate metrics on total active students, course offerings, institutional average GPA, and pass rate.
- **Grade Entry & Gradebook Management**: Search and filter by semester, department, or course; modal for entering component scores with live GPA computation; button to apply a class-wide **Grading Curve Bonus**.
- **Student Transcript Viewer**: Select any student and generate their term transcript with credit audit and standing evaluation.
- **Course Analytics & Bell Curve**: Compute class mean ($\mu$), standard deviation ($\sigma$), min/max, pass rate, and letter grade distribution histograms.

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java JDK 21+** installed and available in `PATH`.
- (Optional) **MySQL 8.0+** running on `localhost:3306` (The system automatically falls back to its embedded engine if MySQL is offline).

### 1-Click Launch (Windows)

| File | Description |
|---|---|
| **`run-all.bat`** | Launches both the Web Server (port 8081) and the Java Swing Registrar Console simultaneously. |
| **`run-admin.bat`** | Launches the desktop **Java Swing Registrar & Dean's Console**. |
| **`run-web.bat`** | Starts the embedded HTTP server and opens the **Student Grade Portal** in your default browser. |
| **`test.bat`** | Runs the automated 7-point system verification and grading test suite. |
| **`compile.bat`** | Compiles all Java source code using standalone `javac`. |

### Launching via Command Line
```bash
# 1. Compile the project
compile.bat

# 2. Run the application
java -cp "bin;lib/*;src/main/resources" com.buu297.grade.Main
```

### Running with Apache Maven
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.buu297.grade.Main"
```

---

## 🗄️ MySQL Database Setup (Optional)

```bash
# Automated setup:
cd database
setup_mysql.bat

# Or manual execution:
mysql -u root -p < database/schema.sql
mysql -u root -p < database/seed_data.sql
```

Credentials can be configured in `src/main/resources/application.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/grade_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=
server.port=8081
```

---

## 🧪 Automated System Verification Suite

Run the built-in system verification suite to validate all 7 subsystem components:
```bash
java -ea -cp "bin;lib/*;src/main/resources" com.buu297.grade.SystemVerificationTest
```

**Verification Results**:
- ✔ Database Manager connection & fallback detection
- ✔ Student DAO lookup & cohort records
- ✔ Course catalog offerings & credit weights
- ✔ Seed grade records verification
- ✔ Weighted grade calculation, letter grade mapping, and 4.0 GPA conversion
- ✔ Dean's list transcript & academic standing evaluation
- ✔ Course bell curve statistics (Mean, StdDev, Pass Rate %)

---

## 📂 Project Structure

```
student-grade-management-system/
├── database/
│   ├── schema.sql              # MySQL DDL, indexes & stored procedures
│   ├── seed_data.sql           # Realistic students, courses & grade records
│   ├── analytics_queries.sql   # Statistical GPA & bell curve queries
│   ├── setup_mysql.bat         # Automated MySQL setup script
│   └── README.md               # Database architecture guide
├── web/                        # Dual Interface 1: Web Grade Portal
│   ├── index.html              # Modern student portal layout
│   ├── css/
│   │   └── style.css           # Modern academic UI design
│   └── js/
│       ├── app.js              # Gradebook logic & printable transcript
│       └── mockData.js         # Offline fallback data
├── lib/
│   └── mysql-connector-j-8.3.0.jar # MySQL JDBC Driver
├── src/
│   ├── main/
│   │   ├── java/com/buu297/grade/
│   │   │   ├── Main.java                   # Unified entry point
│   │   │   ├── SystemVerificationTest.java # 7-point verification suite
│   │   │   ├── config/
│   │   │   │   ├── AppConfig.java          # Configuration properties loader
│   │   │   │   └── DatabaseManager.java    # Connection manager with fallback
│   │   │   ├── model/                      # Domain models & grading enums
│   │   │   │   ├── Student.java
│   │   │   │   ├── Course.java
│   │   │   │   ├── Semester.java
│   │   │   │   ├── GradeRecord.java
│   │   │   │   ├── LetterGrade.java
│   │   │   │   ├── AcademicStanding.java
│   │   │   │   ├── StudentTranscript.java
│   │   │   │   └── CourseAnalytics.java
│   │   │   ├── dao/                        # Data Access Object pattern
│   │   │   │   ├── StudentDAO.java
│   │   │   │   ├── CourseDAO.java
│   │   │   │   ├── GradeRecordDAO.java
│   │   │   │   ├── memory/                 # Thread-safe in-memory DAOs
│   │   │   │   └── mysql/                  # MySQL JDBC DAOs
│   │   │   ├── service/                    # Business & Calculation logic
│   │   │   │   ├── GradeCalculationService.java
│   │   │   │   ├── TranscriptReportService.java
│   │   │   │   └── CourseAnalyticsService.java
│   │   │   ├── server/                     # Embedded Web & REST API Server
│   │   │   │   ├── GradeApiServer.java
│   │   │   │   ├── StaticFileHandler.java
│   │   │   │   └── JsonUtil.java
│   │   │   └── ui/                         # Dual Interface 2: Java Swing App
│   │   │       ├── Theme.java
│   │   │       ├── AcademicConsoleApp.java # Main JFrame with sidebar
│   │   │       ├── DashboardPanel.java     # Academic KPI cards & Honor Roll
│   │   │       ├── GradeEntryPanel.java    # Grade entry & Curve bonus
│   │   │       ├── TranscriptViewerPanel.java # Transcript audit & Print preview
│   │   │       ├── CourseAnalyticsPanel.java  # Bell curve & Histograms
│   │   │       └── components/
│   │   │           ├── StatCard.java
│   │   │           └── ModernTable.java
│   │   └── resources/
│   │       └── application.properties
├── pom.xml                     # Maven project descriptor
├── compile.bat                 # Direct compilation script
├── run-all.bat                 # Dual-mode launch script
├── run-admin.bat               # Desktop console launch
├── run-web.bat                 # Web portal launch
├── test.bat                    # Verification suite launch
├── .gitignore
└── README.md
```

---

## 👨‍💻 Author

- **GitHub**: [@buu297](https://github.com/buu297)
- **Repository**: [https://github.com/buu297/student-grade-management-system](https://github.com/buu297/student-grade-management-system)
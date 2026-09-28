window.MOCK_DATA = {
  students: [
    { studentId: "STU202601", fullName: "Nguyen Van An", email: "an.nguyen@university.edu.vn", department: "Computer Science", major: "Software Engineering", academicYear: 2026 },
    { studentId: "STU202602", fullName: "Tran Thi Bich", email: "bich.tran@university.edu.vn", department: "Information Systems", major: "Data Analytics", academicYear: 2026 },
    { studentId: "STU202603", fullName: "Le Quoc Cuong", email: "cuong.le@university.edu.vn", department: "Computer Science", major: "Artificial Intelligence", academicYear: 2025 },
    { studentId: "STU202604", fullName: "Pham Minh Duc", email: "duc.pham@university.edu.vn", department: "Business Administration", major: "International Business", academicYear: 2026 },
    { studentId: "STU202607", fullName: "Dang Tuan Kiet", email: "kiet.dang@university.edu.vn", department: "Business Administration", major: "Finance & Banking", academicYear: 2026 }
  ],
  transcripts: {
    "STU202601": {
      creditsAttempted: 13,
      creditsEarned: 13,
      semesterGpa: 3.85,
      cumulativeGpa: 3.85,
      averageScore10: 9.09,
      standingDisplayName: "Dean's List / High Distinction",
      standingTextColor: "#15803d",
      standingBgColor: "#dcfce7",
      grades: [
        { courseCode: "CS101", courseName: "Introduction to Computer Science", credits: 3, attendanceScore: 10.0, midtermScore: 9.5, labScore: 9.0, finalScore: 9.0, totalScore10: 9.20, letterGrade: "A+", gpa4: 4.0, status: "PASSED" },
        { courseCode: "CS202", courseName: "Data Structures & Algorithms", credits: 4, attendanceScore: 10.0, midtermScore: 9.0, labScore: 8.5, finalScore: 8.5, totalScore10: 8.75, letterGrade: "A", gpa4: 3.7, status: "PASSED" },
        { courseCode: "CS303", courseName: "Database Management Systems", credits: 3, attendanceScore: 9.5, midtermScore: 8.5, labScore: 9.0, finalScore: 9.0, totalScore10: 8.95, letterGrade: "A", gpa4: 3.7, status: "PASSED" },
        { courseCode: "IS201", courseName: "Enterprise Information Systems", credits: 3, attendanceScore: 10.0, midtermScore: 9.0, labScore: 9.5, finalScore: 9.5, totalScore10: 9.45, letterGrade: "A+", gpa4: 4.0, status: "PASSED" }
      ]
    },
    "STU202602": {
      creditsAttempted: 9,
      creditsEarned: 9,
      semesterGpa: 3.63,
      cumulativeGpa: 3.63,
      averageScore10: 8.68,
      standingDisplayName: "Dean's List / High Distinction",
      standingTextColor: "#15803d",
      standingBgColor: "#dcfce7",
      grades: [
        { courseCode: "CS101", courseName: "Introduction to Computer Science", credits: 3, attendanceScore: 9.0, midtermScore: 8.0, labScore: 8.5, finalScore: 8.5, totalScore10: 8.45, letterGrade: "B+", gpa4: 3.5, status: "PASSED" },
        { courseCode: "IS201", courseName: "Enterprise Information Systems", credits: 3, attendanceScore: 10.0, midtermScore: 8.5, labScore: 9.0, finalScore: 8.5, totalScore10: 8.75, letterGrade: "A", gpa4: 3.7, status: "PASSED" },
        { courseCode: "IS305", courseName: "Business Data Analytics & BI", credits: 3, attendanceScore: 9.0, midtermScore: 9.0, labScore: 8.5, finalScore: 9.0, totalScore10: 8.85, letterGrade: "A", gpa4: 3.7, status: "PASSED" }
      ]
    },
    "STU202607": {
      creditsAttempted: 6,
      creditsEarned: 3,
      semesterGpa: 0.50,
      cumulativeGpa: 0.50,
      averageScore10: 4.10,
      standingDisplayName: "Academic Warning",
      standingTextColor: "#ea580c",
      standingBgColor: "#ffedd5",
      grades: [
        { courseCode: "BA101", courseName: "Principles of Modern Management", credits: 3, attendanceScore: 6.0, midtermScore: 5.0, labScore: 5.0, finalScore: 4.0, totalScore10: 4.60, letterGrade: "D", gpa4: 1.0, status: "PASSED" },
        { courseCode: "IS201", courseName: "Enterprise Information Systems", credits: 3, attendanceScore: 5.0, midtermScore: 4.0, labScore: 4.0, finalScore: 3.0, totalScore10: 3.60, letterGrade: "F", gpa4: 0.0, status: "FAILED" }
      ]
    }
  }
};
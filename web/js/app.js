const API_BASE = window.location.protocol.startsWith("http") ? "" : null;
let currentStudentId = "STU202601";
let studentsList = [];
let currentTranscript = null;

document.addEventListener("DOMContentLoaded", async () => {
  await loadStudents();
});

async function loadStudents() {
  try {
    if (API_BASE !== null) {
      const res = await fetch("/api/students");
      if (res.ok) studentsList = await res.json();
    }
  } catch (e) {
    console.warn("Using offline mock students:", e);
  }

  if (!studentsList || studentsList.length === 0) {
    studentsList = window.MOCK_DATA.students;
  }

  const select = document.getElementById("studentSelect");
  select.innerHTML = "";
  studentsList.forEach(s => {
    const opt = document.createElement("option");
    opt.value = s.studentId;
    opt.textContent = `${s.fullName} (${s.studentId})`;
    if (s.studentId === currentStudentId) opt.selected = true;
    select.appendChild(opt);
  });

  select.addEventListener("change", (e) => {
    currentStudentId = e.target.value;
    loadTranscript();
  });

  await loadTranscript();
}

async function loadTranscript() {
  const currentStudent = studentsList.find(s => s.studentId === currentStudentId) || studentsList[0];

  document.getElementById("studentName").textContent = currentStudent.fullName;
  document.getElementById("studentIdDisplay").textContent = currentStudent.studentId;
  document.getElementById("studentDept").textContent = currentStudent.department;
  document.getElementById("studentMajor").textContent = currentStudent.major;

  try {
    if (API_BASE !== null) {
      const res = await fetch(`/api/transcript?studentId=${currentStudentId}&semesterId=SEM-2026-FALL`);
      if (res.ok) currentTranscript = await res.json();
    }
  } catch (e) {
    console.warn("Using offline mock transcript:", e);
  }

  if (!currentTranscript || !currentTranscript.grades) {
    currentTranscript = window.MOCK_DATA.transcripts[currentStudentId] || window.MOCK_DATA.transcripts["STU202601"];
  }

  // Render KPIs
  document.getElementById("gpaDisplay").textContent = parseFloat(currentTranscript.semesterGpa || 0).toFixed(2);
  document.getElementById("score10Display").textContent = parseFloat(currentTranscript.averageScore10 || 0).toFixed(2);
  document.getElementById("creditsDisplay").textContent = `${currentTranscript.creditsEarned} / ${currentTranscript.creditsAttempted} cr`;

  const badge = document.getElementById("standingBadge");
  badge.textContent = currentTranscript.standingDisplayName || "Good Standing";
  badge.style.backgroundColor = currentTranscript.standingBgColor || "#dcfce7";
  badge.style.color = currentTranscript.standingTextColor || "#15803d";

  // Render Table
  const tbody = document.getElementById("gradesTableBody");
  tbody.innerHTML = "";

  (currentTranscript.grades || []).forEach(g => {
    const tr = document.createElement("tr");
    const letter = g.letterGrade || "P";
    const letterClass = letter.replace("+", "_PLUS");

    tr.innerHTML = `
      <td><strong>${g.courseCode}</strong></td>
      <td>${g.courseName}</td>
      <td>${g.credits}</td>
      <td>${g.attendanceScore != null ? g.attendanceScore : '-'}</td>
      <td>${g.midtermScore != null ? g.midtermScore : '-'}</td>
      <td>${g.labScore != null ? g.labScore : '-'}</td>
      <td>${g.finalScore != null ? g.finalScore : '-'}</td>
      <td><strong>${g.totalScore10 != null ? g.totalScore10 : '-'}</strong></td>
      <td><span class="badge badge-${letterClass}">${letter}</span></td>
      <td><strong>${g.gpa4 != null ? g.gpa4 : '-'}</strong></td>
      <td><span style="font-weight:600; color:${g.status === 'PASSED' ? 'var(--success)' : 'var(--danger)'};">${g.status}</span></td>
    `;
    tbody.appendChild(tr);
  });
}

function printTranscript() {
  const currentStudent = studentsList.find(s => s.studentId === currentStudentId);
  const w = window.open('', '_blank');
  w.document.write(`
    <!DOCTYPE html><html><head><title>Transcript - ${currentStudent ? currentStudent.studentId : ''}</title>
    <style>body{font-family:'Segoe UI',Arial,sans-serif;padding:40px;color:#0f172a;}
    .header{display:flex;justify-content:space-between;border-bottom:2px solid #2563eb;padding-bottom:15px;}
    table{width:100%;border-collapse:collapse;margin-top:20px;} th,td{border-bottom:1px solid #cbd5e1;padding:8px 12px;text-align:left;font-size:13px;}
    th{background:#f8fafc;} .kpi{display:flex;gap:20px;margin-top:20px;background:#f8fafc;padding:15px;border-radius:8px;}
    </style></head><body onload="window.print()">
    <div class="header"><div><h2>ACADEMIC TRANSCRIPT</h2><p>University Registrar</p></div><div style="text-align:right;"><p>Date: ${new Date().toLocaleDateString()}</p></div></div>
    <p><strong>Student:</strong> ${currentStudent ? currentStudent.fullName : ''} (${currentStudent ? currentStudent.studentId : ''}) | <strong>Major:</strong> ${currentStudent ? currentStudent.major : ''}</p>
    <table><thead><tr><th>Code</th><th>Course Name</th><th>Credits</th><th>Attendance</th><th>Midterm</th><th>Lab</th><th>Final</th><th>Score (10)</th><th>Letter</th><th>GPA</th></tr></thead>
    <tbody>
      ${(currentTranscript.grades || []).map(g => `<tr><td>${g.courseCode}</td><td>${g.courseName}</td><td>${g.credits}</td><td>${g.attendanceScore}</td><td>${g.midtermScore}</td><td>${g.labScore}</td><td>${g.finalScore}</td><td><strong>${g.totalScore10}</strong></td><td><strong>${g.letterGrade}</strong></td><td>${g.gpa4}</td></tr>`).join('')}
    </tbody></table>
    <div class="kpi">
      <div>Credits Earned: <strong>${currentTranscript.creditsEarned}</strong></div>
      <div>Semester GPA (4.0): <strong>${parseFloat(currentTranscript.semesterGpa).toFixed(2)}</strong></div>
      <div>Average Score (10.0): <strong>${parseFloat(currentTranscript.averageScore10).toFixed(2)}</strong></div>
      <div>Standing: <strong>${currentTranscript.standingDisplayName}</strong></div>
    </div>
    </body></html>
  `);
  w.document.close();
}
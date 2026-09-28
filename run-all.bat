@echo off
chcp 65001 > nul
if not exist "bin\com\buu297\grade\Main.class" (
    echo Binaries not found. Running compiler first...
    call compile.bat
)
echo Starting Student Grade Management System (Dual Mode: Web Server + Swing Console)...
start "" "http://localhost:8081"
java -cp "bin;lib\*;src\main\resources" com.buu297.grade.Main
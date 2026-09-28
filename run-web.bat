@echo off
chcp 65001 > nul
if not exist "bin\com\buu297\grade\Main.class" (
    echo Binaries not found. Running compiler first...
    call compile.bat
)
echo Starting Web Server for Student Grade Portal at http://localhost:8081...
start "" "http://localhost:8081"
java -cp "bin;lib\*;src\main\resources" com.buu297.grade.Main --server
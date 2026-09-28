@echo off
chcp 65001 > nul
if not exist "bin\com\buu297\grade\SystemVerificationTest.class" (
    echo Binaries not found. Running compiler first...
    call compile.bat
)
echo Running Academic System Verification and Grade Test Suite...
java -ea -cp "bin;lib\*;src\main\resources" com.buu297.grade.SystemVerificationTest
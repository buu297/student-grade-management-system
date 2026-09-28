@echo off
chcp 65001 > nul
if not exist "bin\com\buu297\grade\Main.class" (
    echo Binaries not found. Running compiler first...
    call compile.bat
)
echo Starting Java Swing Registrar & Dean's Console...
java -cp "bin;lib\*;src\main\resources" com.buu297.grade.Main --admin
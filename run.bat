@echo off
REM Compile and run the Library Management System (needs JDK 25 or newer)
if not exist out mkdir out
dir /s /b src\*.java > sources.txt
javac --release 25 -d out @sources.txt
if errorlevel 1 (echo Compilation failed & exit /b 1)
java -cp "out;lib\sqlite-jdbc.jar" com.library.Main

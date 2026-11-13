@echo off
REM Start the local Spring Boot web interface (needs JDK 25 or newer).
cd /d "%~dp0"
call "%~dp0web\mvnw.cmd" -f "%~dp0web\pom.xml" "-Dspring-boot.run.workingDirectory=%~dp0." spring-boot:run

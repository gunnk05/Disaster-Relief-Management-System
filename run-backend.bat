@echo off
title DRRMS Backend (Spring Boot)
cd /d "%~dp0backend"
echo ===================================================
echo Starting DRRMS Backend on http://localhost:8080/api
echo ===================================================
if exist "target\relieflink-api-1.0.0.jar" (
    java -jar "target\relieflink-api-1.0.0.jar"
) else (
    call .maven\bin\mvn.cmd spring-boot:run
)
pause

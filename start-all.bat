@echo off
echo =============================================================
echo Starting Disaster Relief Resource Management System (DRRMS)
echo =============================================================
echo.
echo 1. Launching Backend (Spring Boot)...
start "DRRMS Backend" cmd /c "%~dp0run-backend.bat"
echo Waiting for backend initialization...
timeout /t 5 /nobreak >nul
echo 2. Launching Frontend (React + Vite)...
start "DRRMS Frontend" cmd /c "%~dp0run-frontend.bat"
echo.
echo =============================================================
echo Both services are starting in separate windows!
echo - Backend API:  http://localhost:8080/api (Health: http://localhost:8080/api/health)
echo - Frontend App: http://localhost:5173
echo.
echo Test Logins (from database seed):
echo - Admin:     admin@example.com / adminpass
echo - Volunteer: raj@example.com   / volpass
echo - Citizen:   deepa@example.com / deeppass
echo =============================================================
pause

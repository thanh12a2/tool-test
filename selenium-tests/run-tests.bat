@echo off
REM Test Execution Script for Selenium Test Suite (Windows)
REM Usage: run-tests.bat [option]

setlocal enabledelayedexpansion

REM Color codes (Windows 10+)
set GREEN=[92m
set BLUE=[94m
set YELLOW=[93m
set NC=[0m

REM Show help
if "%1"=="" (
    call :show_help
    exit /b 0
)

if /i "%1"=="help" (
    call :show_help
    exit /b 0
)

if /i "%1"=="all" (
    call :run_all_tests
    exit /b 0
)

if /i "%1"=="login" (
    call :run_test_class "LoginTest"
    exit /b 0
)

if /i "%1"=="register" (
    call :run_test_class "RegisterTest"
    exit /b 0
)

if /i "%1"=="password" (
    call :run_test_class "ChangePasswordTest"
    exit /b 0
)

if /i "%1"=="doctor" (
    call :run_test_class "DoctorManagementTest"
    exit /b 0
)

if /i "%1"=="patient" (
    call :run_test_class "PatientManagementTest"
    exit /b 0
)

if /i "%1"=="appointment" (
    call :run_test_class "ManageAppointmentsTest"
    exit /b 0
)

if /i "%1"=="treatment" (
    call :run_test_class "TreatmentPlanTest"
    exit /b 0
)

if /i "%1"=="medical" (
    call :run_test_class "MedicalRecordTest"
    exit /b 0
)

if /i "%1"=="dashboard" (
    call :run_test_class "DashboardTest"
    exit /b 0
)

if /i "%1"=="quick" (
    call :run_quick_tests
    exit /b 0
)

if /i "%1"=="clean" (
    call :clean_build
    exit /b 0
)

if /i "%1"=="rebuild" (
    call :rebuild
    exit /b 0
)

echo Unknown option: %1
call :show_help
exit /b 1

:show_help
echo.
echo Test Execution Script for Selenium Test Suite
echo.
echo Usage: run-tests.bat [option]
echo.
echo Available Options:
echo   all              Run all tests ^(55+ test methods^)
echo   login            Run LoginTest ^(9 tests^)
echo   register         Run RegisterTest ^(4 tests^)
echo   password         Run ChangePasswordTest ^(4 tests^)
echo   doctor           Run DoctorManagementTest ^(4 tests^)
echo   patient          Run PatientManagementTest ^(4 tests^)
echo   appointment      Run ManageAppointmentsTest ^(5 tests^)
echo   treatment        Run TreatmentPlanTest ^(3 tests^)
echo   medical          Run MedicalRecordTest ^(3 tests^)
echo   dashboard        Run DashboardTest ^(5 tests^)
echo.
echo   quick            Run quick suite ^(5 core features^)
echo   clean            Clean Maven build
echo   rebuild          Clean and rebuild
echo   help             Show this help message
echo.
echo Examples:
echo   run-tests.bat login          # Run login tests
echo   run-tests.bat all            # Run all tests
echo   run-tests.bat clean          # Clean build
echo.
goto :eof

:run_all_tests
echo ================================================
echo Running All Tests ^(55+ test methods^)
echo ================================================
mvn clean test
echo.
echo Test suite completed!
goto :eof

:run_test_class
echo ================================================
echo Running %~1
echo ================================================
mvn test -Dtest=%~1
echo.
echo %~1 completed!
goto :eof

:run_quick_tests
echo ================================================
echo Running Quick Test Suite
echo ================================================
echo Features to test:
echo   1. LoginTest ^(9 tests^)
echo   2. RegisterTest ^(4 tests^)
echo   3. DashboardTest ^(5 tests^)
echo   4. PatientManagementTest ^(4 tests^)
echo   5. DoctorManagementTest ^(4 tests^)
echo.
mvn test -Dtest=LoginTest,RegisterTest,DashboardTest,PatientManagementTest,DoctorManagementTest
echo.
echo Quick test suite completed!
goto :eof

:clean_build
echo ================================================
echo Cleaning Maven Build
echo ================================================
mvn clean
echo.
echo Build cleaned!
goto :eof

:rebuild
echo ================================================
echo Rebuilding Project
echo ================================================
mvn clean install
echo.
echo Rebuild completed!
goto :eof

endlocal

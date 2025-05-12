@echo off
echo Compiling Java server...
javac AlarmClockServer.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Compilation successful.
echo.
echo Starting Alarm Clock Web Server...
java AlarmClockServer
pause 
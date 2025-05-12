@echo off
echo Compiling Java files...
javac AlarmClockApp.java AlarmItem.java AlarmSoundPlayer.java AlarmDataManager.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Compilation successful.
echo.
echo Running Alarm Clock Application...
java AlarmClockApp
pause 
@echo off
echo Compiling Java Alarm Clock...
javac SimpleAlarmClock.java AlarmItem.java AlarmSoundPlayer.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Compilation successful.
echo.
echo Running Java Alarm Clock...
java SimpleAlarmClock
pause 
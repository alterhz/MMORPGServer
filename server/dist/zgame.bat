@echo off
set JDK_HOME=C:\Users\Administrator\.jdks\ms-11.0.28
set JAVA=%JDK_HOME%\bin\java.exe

echo Starting zgame server...
echo Using JDK: %JDK_HOME%

%JAVA% -cp "zgame.jar;config/*;libs/*;" org.game.GameStartUp

if %ERRORLEVEL% NEQ 0 (
    echo Failed to start zgame server.
    pause
)
@echo off
chcp 65001 > nul
set JDK_HOME=C:\Users\Administrator\.jdks\corretto-11.0.28
set JAVA=%JDK_HOME%\bin\java.exe

echo Starting zgame server...
echo Using JDK: %JDK_HOME%

%JAVA% -Dfile.encoding=UTF-8 -cp "zgame.jar;config/*;libs/*;" org.game.tool.ProtoAnnotationAdder "../../proto/json/ProtoIds.ini" "../zgame/src/main/java/org/game/proto"

if %ERRORLEVEL% NEQ 0 (
    echo Failed to start zgame server.
    pause
)
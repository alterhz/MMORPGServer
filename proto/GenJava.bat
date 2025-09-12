@echo off
setlocal enabledelayedexpansion

set PROTO_DIR=Proto
set JAVA_OUT_DIR=java

for /D %%F in (%PROTO_DIR%\*) do (
    set "FOLDER=%%~nxF"
    set "PACKAGE=!FOLDER: =_!"
    
    echo Generating code for !FOLDER!...
    
    if not exist "!JAVA_OUT_DIR!\!FOLDER!\" (
        mkdir "!JAVA_OUT_DIR!\!FOLDER!"
    )
    
    call quicktype -l java --array-type list --just-types --package "org.game.proto.!PACKAGE!" --src "!PROTO_DIR!\!FOLDER!" --out "!JAVA_OUT_DIR!\!FOLDER!\!FOLDER!.java"
    
    if !errorlevel! equ 0 (
        echo ? Successfully generated !FOLDER!
    ) else (
        echo ? Failed to generate !FOLDER!
    )
)

echo All proto files processed!
pause
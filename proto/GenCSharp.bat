@echo off
setlocal enabledelayedexpansion

set PROTO_DIR=Proto
set CSHARP_OUT_DIR=csharp

for /D %%F in (%PROTO_DIR%\*) do (
    set "FOLDER=%%~nxF"
    set "PACKAGE=!FOLDER: =_!"
    set "PACKAGE=!PACKAGE!"
    
    echo Generating code for !FOLDER!...
    
    if not exist "!CSHARP_OUT_DIR!\" (
        mkdir "!CSHARP_OUT_DIR!"
    )
    
	call quicktype -l csharp --array-type list --features just-types --namespace ZGame --src "!PROTO_DIR!\!FOLDER!" -o "!CSHARP_OUT_DIR!\!FOLDER!.cs"
	
    if !errorlevel! equ 0 (
        echo ? Successfully generated !FOLDER!
    ) else (
        echo ? Failed to generate !FOLDER!
    )
)

echo All proto files processed!
pause


REM quicktype -l csharp --array-type list --features just-types --namespace org.game.proto.login --src Proto/Login/ -o csharp/Login.cs
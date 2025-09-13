@echo off
setlocal enabledelayedexpansion

set PROTO_DIR=json
set JAVA_OUT_DIR=..\client\zgame\Assets\Scripts\Proto

:: 遍历 PROTO_DIR 下的每个子文件夹
for /D %%F in (%PROTO_DIR%\*) do (
    set "FOLDER=%%~nxF"
    set "PACKAGE=!FOLDER: =_!"  # 替换空格为下划线

    echo Processing folder: !FOLDER!

    :: 遍历子文件夹中的每个 .json 文件
    for %%J in ("%%F\*.json") do (
        set "JSON_FILE=%%~nJ"          # 获取文件名（无扩展名）
        set "OUTPUT_DIR=!JAVA_OUT_DIR!\!FOLDER!"
        set "OUTPUT_FILE=!OUTPUT_DIR!\!JSON_FILE!.cs"
        set "FULL_JSON_PATH=%%J"

        echo Generating !JSON_FILE!.cs from %%J ...

        :: 创建输出目录（如果不存在）
        if not exist "!OUTPUT_DIR!" (
            mkdir "!OUTPUT_DIR!"
        )
		
		call quicktype -l csharp --array-type list --features just-types --namespace ZGame --src "!FULL_JSON_PATH!" -o "!OUTPUT_FILE!"

        if !errorlevel! equ 0 (
            echo ? Successfully generated !JSON_FILE!.cs
        ) else (
            echo ? Failed to generate !JSON_FILE!.cs
        )
    )
)

echo All JSON files processed!
pause
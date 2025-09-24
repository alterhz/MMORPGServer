@echo off
setlocal enabledelayedexpansion

set PROTO_DIR=json
set JAVA_OUT_DIR=..\server\zgame\src\main\java\org\game\proto

:: 删除文件夹
rmdir /s /q %JAVA_OUT_DIR%

:: ���� PROTO_DIR �µ�ÿ�����ļ���
for /D %%F in (%PROTO_DIR%\*) do (
    set "FOLDER=%%~nxF"
    set "PACKAGE=!FOLDER: =_!"  # �滻�ո�Ϊ�»���

    echo Processing folder: !FOLDER!

    :: �������ļ����е�ÿ�� .json �ļ�
    for %%J in ("%%F\*.json") do (
        set "JSON_FILE=%%~nJ"          # ��ȡ�ļ���������չ����
        set "OUTPUT_DIR=!JAVA_OUT_DIR!\!FOLDER!"
        set "OUTPUT_FILE=!OUTPUT_DIR!\!JSON_FILE!.java"
        set "FULL_JSON_PATH=%%J"

        echo Generating !JSON_FILE!.java from %%J ...

        :: �������Ŀ¼����������ڣ�
        if not exist "!OUTPUT_DIR!" (
            mkdir "!OUTPUT_DIR!"
        )

        :: ���� quicktype Ϊ���� JSON �ļ����� Java ��
        call quicktype -l java --array-type list --just-types --acronym-style original --package "org.game.proto.!PACKAGE!" --src "!FULL_JSON_PATH!" --out "!OUTPUT_FILE!"

        if !errorlevel! equ 0 (
            echo ? Successfully generated !JSON_FILE!.java
        ) else (
            echo ? Failed to generate !JSON_FILE!.java
        )
    )
)

echo All JSON files processed!
pause

cd ../server/dist
call GenProtoIds.bat

echo ע���������

pause


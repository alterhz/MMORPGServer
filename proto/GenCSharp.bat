@echo off
setlocal enabledelayedexpansion

set PROTO_DIR=json
set JAVA_OUT_DIR=..\client\zgame\Assets\Scripts\Proto

:: ���� PROTO_DIR �µ�ÿ�����ļ���
for /D %%F in (%PROTO_DIR%\*) do (
    set "FOLDER=%%~nxF"
    set "PACKAGE=!FOLDER: =_!"  # �滻�ո�Ϊ�»���

    echo Processing folder: !FOLDER!

    :: �������ļ����е�ÿ�� .json �ļ�
    for %%J in ("%%F\*.json") do (
        set "JSON_FILE=%%~nJ"          # ��ȡ�ļ���������չ����
        set "OUTPUT_DIR=!JAVA_OUT_DIR!\!FOLDER!"
        set "OUTPUT_FILE=!OUTPUT_DIR!\!JSON_FILE!.cs"
        set "FULL_JSON_PATH=%%J"

        echo Generating !JSON_FILE!.cs from %%J ...

        :: �������Ŀ¼����������ڣ�
        if not exist "!OUTPUT_DIR!" (
            mkdir "!OUTPUT_DIR!"
        )
		
		call quicktype -l csharp --array-type list --features just-types-and-namespace --namespace ZGame --keep-property-name --src "!FULL_JSON_PATH!" -o "!OUTPUT_FILE!"

        if !errorlevel! equ 0 (
            echo ? Successfully generated !JSON_FILE!.cs
        ) else (
            echo ? Failed to generate !JSON_FILE!.cs
        )
    )
)

echo All JSON files processed!

GenClientProto\Debug\net8.0\GenClientProto.exe json\ProtoIds.ini ..\client\zgame\Assets\Scripts\Proto\

echo Э��ID����������

pause
@echo off
REM Script para instalar o APK no emulador ou dispositivo conectado

echo ====================================
echo Instalador APK - Gestao de Risco
echo ====================================
echo.

REM Verificar se adb existe
where adb >nul 2>nul
if %errorlevel% neq 0 (
    echo ERRO: adb nao encontrado no PATH
    echo Configure o Android SDK Platform Tools no PATH
    pause
    exit /b 1
)

REM Listar dispositivos conectados
echo Dispositivos conectados:
adb devices
echo.

REM Caminho do APK
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

if not exist "%APK_PATH%" (
    echo ERRO: APK nao encontrado em %APK_PATH%
    echo Execute primeiro: gradlew assembleDebug
    pause
    exit /b 1
)

echo APK encontrado: %APK_PATH%
echo Tamanho: 
dir "%APK_PATH%" | find "app-debug"
echo.

echo Instalando APK...
adb install -r "%APK_PATH%"

if %errorlevel% equ 0 (
    echo.
    echo ====================================
    echo INSTALACAO CONCLUIDA COM SUCESSO!
    echo ====================================
    echo.
    echo Para iniciar o app:
    echo   adb shell am start -n com.example.project_gestoderisco/.MainActivity
) else (
    echo.
    echo ERRO na instalacao!
    echo Verifique se o emulador/dispositivo esta conectado
)

pause

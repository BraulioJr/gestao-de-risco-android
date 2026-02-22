@echo off
set PKG=com.example.project_gestoderisco
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

echo ==========================================
echo       GESTAO DE RISCO - AUTO TEST
echo ==========================================

echo [1/4] Compilando APK...
:: O comando 'call' garante que o script continue apos o Gradle

call .\gradlew.bat assembleDebug --stacktrace

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [!] ERRO: Falha na compilacao. 
    echo.
    echo [DICA] Se o erro for "KSP plugin... task class not found":
    echo        Edite o build.gradle da RAIZ: REMOVA o plugin 'kotlin-android' do bloco plugins {}.
    echo        Mantenha apenas Hilt e KSP com 'apply false' para evitar conflitos.
    echo        Veja o log acima para detalhes.
    pause
    exit /b
)

echo.
echo [2/4] Instalando no dispositivo...
adb wait-for-device
adb install -r "%APK_PATH%"

echo.
echo [3/4] Iniciando App...
adb shell am start -n %PKG%/.view.SplashActivity

echo.
echo [4/4] Monitorando Logs...
adb logcat -c
adb logcat -v time | findstr /i "%PKG%"

pause

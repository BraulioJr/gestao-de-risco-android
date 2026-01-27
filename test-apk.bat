@echo off
REM ========================================================
REM Gestão de Risco - APK Tester Script
REM ========================================================
REM Este script testa automaticamente o APK gerado
REM Funciona com Genymotion, Oracle VM ou Android Emulator

setlocal enabledelayedexpansion

REM Cores para output
set COLOR_SUCCESS=[92m
set COLOR_ERROR=[91m
set COLOR_INFO=[94m
set COLOR_RESET=[0m

echo.
echo ==================================================
echo    Gestão de Risco - APK Tester
echo ==================================================
echo.

REM Verifica se ADB está disponível
echo [*] Verificando ADB...
where adb >nul 2>&1
if errorlevel 1 (
    echo [ERROR] ADB não encontrado no PATH
    echo Adicione Android SDK ao PATH e tente novamente
    echo PATH esperado: C:\Users\user\AppData\Local\Android\Sdk\platform-tools
    pause
    exit /b 1
)
echo [OK] ADB disponível

REM Caminhos
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
set PACKAGE_NAME=com.example.project_gestoderisco
set MAIN_ACTIVITY=%PACKAGE_NAME%/.MainActivity

REM Verifica se APK existe
echo.
echo [*] Verificando APK...
if not exist "!APK_PATH!" (
    echo [ERROR] APK não encontrado em !APK_PATH!
    echo Execute primeiro: gradlew assembleDebug
    pause
    exit /b 1
)
echo [OK] APK encontrado (!APK_PATH!)

REM Aguarda dispositivo
echo.
echo [*] Aguardando dispositivo...
echo Se nenhum dispositivo aparecer, inicie seu emulador:
echo   - Genymotion
echo   - Android Emulator
echo   - Oracle VM
echo.

REM Loop para aguardar conexão
set TIMEOUT=0
:wait_device
adb devices | find /v "List" | find /v "^$" >nul
if errorlevel 1 (
    if !TIMEOUT! equ 0 echo [AGUARDANDO]
    set /a TIMEOUT=TIMEOUT+1
    if !TIMEOUT! gtr 30 (
        echo [ERROR] Nenhum dispositivo conectado após 30 segundos
        echo Dicas de troubleshooting:
        echo 1. Inicie Genymotion ou Android Emulator
        echo 2. Se usando Genymotion, execute: adb connect 127.0.0.1:5555
        echo 3. Verifique: adb devices
        pause
        exit /b 1
    )
    timeout /t 1 /nobreak
    goto wait_device
)
echo [OK] Dispositivo conectado!

REM Listar dispositivos
echo.
echo [*] Dispositivos conectados:
adb devices

REM Instalar APK
echo.
echo [*] Instalando APK...
adb install -r "!APK_PATH!"
if errorlevel 1 (
    echo [ERROR] Falha na instalação do APK
    echo Dicas:
    echo - Limpe dados: adb shell pm clear !PACKAGE_NAME!
    echo - Desinstale: adb uninstall !PACKAGE_NAME!
    echo - Tente novamente: adb install -r !APK_PATH!
    pause
    exit /b 1
)
echo [OK] APK instalado com sucesso!

REM Verificar instalação
echo.
echo [*] Verificando instalação...
adb shell pm list packages | find "!PACKAGE_NAME!" >nul
if errorlevel 1 (
    echo [WARNING] App não aparece na lista de pacotes
) else (
    echo [OK] App verificado na lista de pacotes
)

REM Iniciar app
echo.
echo [*] Iniciando aplicativo...
adb shell am start -n "!MAIN_ACTIVITY!"
if errorlevel 1 (
    echo [WARNING] Erro ao iniciar app via adb
    echo Dicas:
    echo - Tente iniciar manualmente do device
    echo - Verifique logs: adb logcat
) else (
    echo [OK] App iniciado!
    echo.
    echo [*] Monitorando logs (pressione Ctrl+C para parar)...
    adb logcat | find /i "gestaoderisco"
)

echo.
echo ==================================================
echo    Teste concluído!
echo ==================================================
echo.

pause

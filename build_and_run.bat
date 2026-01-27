@echo off
REM Script para compilar, instalar e executar o app no emulador
REM Projeto: Gestão de Risco - Prevenção de Perdas Inteligente

setlocal enabledelayedexpansion

echo.
echo ========================================
echo  GESTAO DE RISCO - Build & Deploy
echo ========================================
echo.

REM Definir caminhos
set PROJECT_DIR=C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco
set GRADLE_WRAPPER=%PROJECT_DIR%\gradlew.bat
set APP_PACKAGE=com.example.project_gestoderisco
set MAIN_ACTIVITY=%APP_PACKAGE%.view.LoginActivity

echo [1/4] Compilando APK Debug...
cd /d %PROJECT_DIR%
call %GRADLE_WRAPPER% clean assembleDebug -x lint

if errorlevel 1 (
    echo ERRO: Falha na compilacao!
    exit /b 1
)

echo.
echo [2/4] APK compilado com sucesso!
echo.

REM Verificar se APK foi gerado
set APK_PATH=%PROJECT_DIR%\app\build\outputs\apk\debug\app-debug.apk

if not exist %APK_PATH% (
    echo ERRO: APK nao encontrado em %APK_PATH%
    exit /b 1
)

echo [3/4] Instalando APK no emulador...
call adb install -r %APK_PATH%

if errorlevel 1 (
    echo ERRO: Falha na instalacao!
    exit /b 1
)

echo.
echo [4/4] Executando aplicativo...
call adb shell am start -n %APP_PACKAGE%/%MAIN_ACTIVITY%

echo.
echo ========================================
echo  SUCESSO! App iniciando no emulador...
echo ========================================
echo.

pause

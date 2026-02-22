@echo off
:: Define o pacote para evitar repetições
set PKG=com.example.project_gestoderisco
set APK_PATH=app\build\outputs\apk\debug\app-debug.apk

echo ==========================================
echo       GESTAO DE RISCO - AUTO TEST
echo ==========================================

:: Configurar ADB Automaticamente
set ADB_CMD=adb
where adb >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
        set ADB_CMD="%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
        echo [i] ADB encontrado em: %LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
    ) else (
        echo [!] ADB nao encontrado. Verifique se o Android SDK esta instalado.
    )
)

:: 1. Verificação de Dispositivo
echo [1/4] Verificando dispositivos...
echo     [Aguardando dispositivo... Abra o emulador se nao estiver rodando]
%ADB_CMD% wait-for-device
%ADB_CMD% devices

:: 2. Build e Instalação
echo.
echo [2/4] Compilando (Build)...
call .\gradlew.bat assembleDebug
if %ERRORLEVEL% NEQ 0 (
    echo [!] Erro na compilacao. Abortando.
    pause
    exit /b 1
)

echo [2.1/4] Instalando APK...

if not exist "%APK_PATH%" (
    echo [!] ERRO CRITICO: A compilacao falhou ou o APK nao foi gerado.
    echo.
    echo [DICA DE CORRECAO]
    echo Se o erro for "The KSP plugin was detected... task class could not be found":
    echo 1. Abra o arquivo build.gradle.kts da RAIZ (fora da pasta app)
    echo 2. Adicione: alias(libs.plugins.ksp) apply false no bloco plugins {}
    echo Verifique os logs acima para corrigir o erro.
    pause
    exit /b 1
)

%ADB_CMD% install -r "%APK_PATH%"
if %ERRORLEVEL% NEQ 0 (
    echo [!] Falha na instalacao. Tentando desinstalar e instalar de novo...
    %ADB_CMD% uninstall %PKG%
    %ADB_CMD% install -r "%APK_PATH%"
)

:: 3. Inicialização
echo.
echo [3/4] Iniciando App...
%ADB_CMD% shell am start -n %PKG%/.view.MainActivity
if %ERRORLEVEL% NEQ 0 (
    %ADB_CMD% shell am start -n %PKG%/.MainActivity
)

:: 4. Logs e Trava de Janela
echo.
echo [4/4] Monitorando Logs (Pressione Ctrl+C para parar)...
echo ------------------------------------------
%ADB_CMD% logcat -c
%ADB_CMD% logcat -v time | findstr /i "%PKG%"

:: Se o logcat for interrompido, a janela permanece aberta
echo.
echo Execucao finalizada ou interrompida.
pause
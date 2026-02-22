#!/bin/bash

# Definições do Projeto
PKG="com.example.project_gestoderisco"
APK_PATH="app/build/outputs/apk/debug/app-debug.apk"

echo "=========================================="
echo "      GESTAO DE RISCO - AUTO TEST (Bash)"
echo "=========================================="

# 1. Configurar ADB
ADB="adb"
if ! command -v adb &> /dev/null; then
    # Tenta encontrar no caminho padrão do Windows via Git Bash
    ADB_LOCAL="/c/Users/$USER/AppData/Local/Android/Sdk/platform-tools/adb.exe"
    if [ -f "$ADB_LOCAL" ]; then
        ADB="$ADB_LOCAL"
        echo "[i] ADB encontrado em: $ADB"
    else
        echo "[!] ADB não encontrado no PATH. Verifique o Android SDK."
    fi
fi

echo "[1/4] Verificando dispositivos..."
"$ADB" wait-for-device
"$ADB" devices

# 2. Compilar (Build)
echo ""
echo "[2/4] Compilando (Build)..."
chmod +x ./gradlew
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "[!] Erro na compilação. Verifique os logs acima."
    exit 1
fi

# 3. Instalar
echo "[2.1/4] Instalando APK..."
if [ ! -f "$APK_PATH" ]; then
    echo "[!] APK não encontrado: $APK_PATH"
    exit 1
fi

"$ADB" install -r "$APK_PATH"

# 4. Testar (Iniciar e Logar)
echo ""
echo "[3/4] Iniciando App..."
"$ADB" shell am start -n "$PKG/.view.MainActivity"

echo ""
echo "[4/4] Monitorando Logs (Pressione Ctrl+C para parar)..."
"$ADB" logcat -c
"$ADB" logcat -v time | grep -i "project_gestoderisco"
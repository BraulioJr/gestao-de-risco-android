#!/bin/bash

# Detectar usuário corretamente no Git Bash (fallback para whoami)
CURRENT_USER="$USER"
if [ -z "$CURRENT_USER" ]; then
    CURRENT_USER=$(whoami)
fi

# Caminho do SDK no Windows (ajustado para Git Bash)
ANDROID_HOME="/c/Users/$CURRENT_USER/AppData/Local/Android/Sdk"
EMULATOR="$ANDROID_HOME/emulator/emulator.exe"

if [ ! -f "$EMULATOR" ]; then
    echo "[!] Executável do emulador não encontrado em: $EMULATOR"
    echo "    Verifique se o Android SDK está instalado corretamente."
    exit 1
fi

echo "=== Lista de Emuladores Disponíveis ==="
"$EMULATOR" -list-avds

# Pega o primeiro da lista
AVD=$("$EMULATOR" -list-avds | head -n 1 | tr -d '\r')

if [ -n "$AVD" ]; then
    echo ""
    echo "[i] Iniciando emulador: $AVD ..."
    "$EMULATOR" -avd "$AVD" &
else
    echo "[!] Nenhum emulador encontrado."
    echo "    Crie um no Android Studio: Tools > Device Manager > Create Device"
fi
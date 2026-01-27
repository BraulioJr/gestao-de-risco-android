# 🐚 Configuração Oracle VM + Android Studio - Gestão de Risco

## ⚡ Resumo Rápido

Seu APK foi compilado com sucesso! Agora você precisa testar em um emulador.

**Arquivo gerado:** `app/build/outputs/apk/debug/app-debug.apk` (12 MB)  
**Status:** ✅ Pronto para instalação  
**Próximo passo:** Conectar Oracle VM/Genymotion ao ADB e instalar o APK

---

## 📋 Opção 1: Usando Genymotion (Recomendado)

### Pré-requisitos
- VirtualBox instalado
- Genymotion instalado (https://www.genymotion.com)
- Conta Genymotion (gratuita ou paga)

### Passos

**1. Iniciar Genymotion**
```bash
# Windows
"C:\Program Files\Genymotion\genymotion.exe"

# Ou abra pelo menu Iniciar
```

**2. Criar ou iniciar um dispositivo**
- Clique em "Add" ou escolha um existente
- Exemplos: Pixel 6, Pixel 7, Samsung Galaxy S21
- Clique "Start" para iniciar a máquina virtual

**3. Conectar ADB ao Genymotion**
```bash
# Verifique a porta (geralmente 5555)
adb connect 127.0.0.1:5555

# Ou se tiver IP da VM:
adb connect <IP_DA_VM>:5555

# Verifique se conectou
adb devices
# Deve aparecer: 127.0.0.1:5555 connected
```

**4. Instalar o APK**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Ou use o script Windows:
install-apk.bat
```

**5. Iniciar o app**
```bash
adb shell am start -n com.example.project_gestoderisco/.MainActivity
```

---

## 📋 Opção 2: Usando Oracle VM + Imagem Android

### Pré-requisitos
- VirtualBox instalado
- Imagem Android x86 (Android-x86 project ou Android Emulator image)
- 4GB RAM mínimo para VM
- 20GB espaço em disco

### Passos

**1. Importar/Criar VM no VirtualBox**
```bash
# Se tiver arquivo .ova (Android image):
VBoxManage import path/to/android.ova

# Ou crie VM manualmente:
# - New VM
# - Linux (32-bit ou 64-bit conforme imagem)
# - RAM: 4GB (ou mais)
# - HD: 20-30GB
# - Rede: Bridged Adapter para acessar ADB
```

**2. Iniciar VM**
```bash
# Via VirtualBox GUI ou:
VBoxManage startvm "Android-VM" --type headless

# Verifique IP:
adb connect <IP_DA_VM>:5555
```

**3. Instalar APK**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📋 Opção 3: Android Emulator Padrão (Android Studio)

### Se preferir usar o emulador nativo do Android Studio

**1. Criar AVD (Android Virtual Device)**
```bash
# Abra Android Studio
# Tools > Device Manager > Create Device

# Ou via terminal:
sdkmanager --install "system-images;android-34;google_apis;x86_64"
avdmanager create avd -n Pixel7 -k "system-images;android-34;google_apis;x86_64" -d "Pixel 7"
```

**2. Iniciar emulador**
```bash
emulator -avd Pixel7 -no-boot-anim
```

**3. Instalar APK**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔧 Troubleshooting

### ❌ "adb: command not found"
**Solução:**
```bash
# Adicione Android SDK ao PATH
# Windows: 
# Defina variável de ambiente:
ANDROID_SDK_ROOT=C:\Users\user\AppData\Local\Android\Sdk
PATH=%ANDROID_SDK_ROOT%\platform-tools;%PATH%

# Depois feche e reabra o terminal
```

### ❌ "Cannot connect to localhost:5555"
**Solução:**
```bash
# Certifique-se que Genymotion está rodando
# Verifique porta SSH da VM (pode ser diferente de 5555)
# Em Genymotion, clique na VM, veja porta SSH/ADB

# Tente conectar com IP real (não localhost):
adb connect 192.168.x.x:5555  # Use IP real da VM
```

### ❌ "App crashes on startup"
**Solução:**
```bash
# Veja logs:
adb logcat | grep "gestaoderisco"

# Limpe dados do app:
adb shell pm clear com.example.project_gestoderisco

# Reinstale:
adb uninstall com.example.project_gestoderisco
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### ❌ "Permission denied" ao instalar APK
**Solução:**
```bash
# Verifique USB debugging na VM (se conectado via USB)
# Ou use adb kill-server e reconecte:
adb kill-server
adb connect 127.0.0.1:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 📊 Verificação de Instalação

Após instalar, verifique se o app foi instalado:

```bash
# Listar apps instalados
adb shell pm list packages | grep gestaoderisco

# Resultado esperado:
# package:com.example.project_gestoderisco

# Iniciar app manualmente:
adb shell am start -n com.example.project_gestoderisco/.MainActivity

# Ver logs de inicialização:
adb logcat | grep "gestaoderisco"
```

---

## ✅ Checklist de Teste

Depois de instalar, teste:

- [ ] App aparece na lista de apps
- [ ] App abre sem crashes
- [ ] Tela inicial (MainActivity) carrega
- [ ] Navegação funciona (se implementada)
- [ ] Nenhum erro no logcat
- [ ] Permissões são solicitadas corretamente
- [ ] App responde aos toques

---

## 🚀 Próximos Passos (Desenvolvimento)

1. **Adicionar `google-services.json`**
   - Copie de Firebase Console para `app/`
   - Rebuild: `./gradlew assembleDebug`

2. **Testar Firebase**
   ```bash
   adb logcat | grep "Firebase"
   ```

3. **Implementar Features**
   - LoginActivity
   - OcorrenciaRepository
   - DashboardFragment
   - SyncWorker

4. **Testes Automatizados**
   ```bash
   ./gradlew test              # Unit tests
   ./gradlew connectedAndroidTest  # Integration tests
   ```

---

## 📞 Suporte Rápido

Se tiver dúvidas, use estes comandos para debug:

```bash
# Status geral do dispositivo
adb shell getprop ro.build.version.release  # Android version
adb shell getprop ro.product.device        # Device model

# Instalações anteriores
adb shell dumpsys package com.example.project_gestoderisco

# Performance
adb shell dumpsys meminfo | grep TOTAL

# Temperatura/bateria
adb shell dumpsys batterymanager
```

---

## 📝 Documentação Relacionada

- **FINAL_STATUS.txt** — Status completo do build
- **BUILD_SUMMARY.md** — Resumo de artefatos
- **INSTALLATION_GUIDE.pt-BR.md** — Guia em português
- **.github/copilot-instructions.md** — Arquitetura do projeto

---

**Seu APK está pronto! Basta conectar o emulador e instalar. Boa sorte! 🚀**

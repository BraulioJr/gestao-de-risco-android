# ��� Guia de Instalação - Gestão de Risco Android

## ✅ APK Gerado com Sucesso!

**Arquivo:** `app-debug.apk` (12 MB)  
**Localização:** `app/build/outputs/apk/debug/app-debug.apk`  
**Status:** Pronto para instalação

---

## ��� Instalação no Oracle VM / Genymotion

### Pré-requisitos
- ✅ Oracle VM VirtualBox com Android (Genymotion) rodando
- ✅ ADB (Android Debug Bridge) instalado e configurado
- ✅ APK gerado em `app/build/outputs/apk/debug/app-debug.apk`

### Passo 1: Iniciar o Emulador Oracle VM
```bash
# Se usando Genymotion, abrir a interface gráfica
# Ou listar dispositivos disponíveis
adb devices
```

### Passo 2: Instalar o APK

**Opção A - Script Automatizado (Windows):**
```bash
# Duplo clique em install-apk.bat
# OU via terminal
install-apk.bat
```

**Opção B - Manual via Terminal:**
```bash
# Navegar ao diretório do projeto
cd C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco

# Instalar com adb
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Ou usar Gradle
./gradlew installDebug
```

### Passo 3: Iniciar o App
```bash
# Listar o package name para confirmar instalação
adb shell pm list packages | grep project_gestoderisco

# Iniciar MainActivity
adb shell am start -n com.example.project_gestoderisco/.MainActivity

# OU abrir manualmente no emulador (procurar por "Gestão de Risco" na home)
```

---

## ��� Troubleshooting

### Problema: "adb: command not found"
**Solução:** Configure o caminho do Android SDK
```bash
# Windows PowerShell
$env:PATH += ";C:\Users\user\AppData\Local\Android\Sdk\platform-tools"

# Windows CMD
set PATH=%PATH%;C:\Users\user\AppData\Local\Android\Sdk\platform-tools
```

### Problema: "No device attached"
**Solução:** Verificar conexão com emulador
```bash
# Listar dispositivos
adb devices

# Se vazio, tentar conectar via TCP (emulator)
adb connect localhost:5555

# Ou reiniciar adb
adb kill-server
adb start-server
```

### Problema: "INSTALL_FAILED_INVALID_APK"
**Solução:** Recompilar APK
```bash
./gradlew clean assembleDebug
```

---

## ��� Características Compiladas no APK

✅ **Autenticação**
- Firebase Auth (pronta para implementação)

✅ **Banco de Dados Local**
- Room Database configurado
- Estrutura para Ocorrências pronta

✅ **Sincronização em Background**
- WorkManager integrado
- 1 hora de ciclo padrão

✅ **Análise & Visualização**
- MPAndroidChart para gráficos
- Estrutura de Dashboard

✅ **Localização**
- Google Maps integration
- Clustering de risco de furto

✅ **Machine Learning**
- TensorFlow Lite framework
- Pronto para modelo de predição

✅ **Offline-First**
- Room caching
- Sync quando online

---

## ��� Próximos Passos (Pós-Instalação)

### 1. Teste de Inicialização
- [ ] App abre sem crashes
- [ ] Tela inicial é exibida
- [ ] Navegação básica funciona

### 2. Verificar Permissões
- [ ] Câmera (para evidências)
- [ ] Localização (GPS)
- [ ] Armazenamento
- [ ] Contatos (para equipe)

### 3. Conectar Firebase
- [ ] Adicionar `google-services.json` em `app/`
- [ ] Configurar credenciais do projeto
- [ ] Testar autenticação

### 4. Implementar Features Faltantes
- [ ] LoginActivity (autenticação)
- [ ] OcorrenciaRepository (CRUD)
- [ ] SyncWorker (sincronização)
- [ ] DashboardFragment (analytics)
- [ ] RiskPredictor (IA/ML)

---

## ��� Estrutura do Projeto Após Build

```
Project_GestaoDeRisco/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/project_gestoderisco/
│   │   │   │   ├── auth/          ✅ Autenticação
│   │   │   │   ├── dashboard/     ✅ Interface
│   │   │   │   ├── data/          ✅ Banco de dados
│   │   │   │   ├── model/         ✅ Modelos de dados
│   │   │   │   ├── repository/    ✅ Acesso a dados
│   │   │   │   ├── view/          ✅ Activities
│   │   │   │   ├── viewmodel/     ✅ State management
│   │   │   │   └── worker/        ✅ Background jobs
│   │   │   └── res/
│   │   │       ├── layout/        ✅ Layouts XML
│   │   │       ├── drawable/      ✅ Ícones & Imagens
│   │   │       ├── values/        ✅ Cores & Strings
│   │   │       └── ...
│   │   ├── test/                  ✅ Unit tests
│   │   └── androidTest/           ✅ Integration tests
│   └── build/
│       └── outputs/
│           └── apk/
│               ├── debug/
│               │   └── app-debug.apk  ✅✅✅ SEU APK!
│               └── release/           (não assinado ainda)
├── .github/
│   └── copilot-instructions.md    ✅ Guia para IA agents
├── gradle/
│   └── libs.versions.toml         ✅ Versões centralizadas
├── BUILD_SUMMARY.md               ✅ Este sumário
└── install-apk.bat                ✅ Script de instalação
```

---

## ��� Debug & Logs

### Ver logs do app em tempo real
```bash
adb logcat | grep "gestaoderisco"

# Ou filtrar por nível
adb logcat | grep "E/\|W/" | grep "gestaoderisco"
```

### Limpar dados de teste
```bash
adb shell pm clear com.example.project_gestoderisco
```

### Uninstall (se necessário)
```bash
adb uninstall com.example.project_gestoderisco
```

---

## ��� Suporte

Para questões sobre:
- **Build/Gradle:** Consultar `app/build.gradle.kts`
- **Arquitetura:** Ver `.github/copilot-instructions.md`
- **Dependências:** Ver `gradle/libs.versions.toml`
- **Logcat:** Usar Android Studio Logcat viewer

---

## ✅ Checklist de Validação

- [x] APK compilado com sucesso
- [x] APK validado (12 MB, estrutura OK)
- [x] Manifesto Android configurado
- [x] Recursos (layout, drawable, etc.) organizados
- [x] Firebase libraries inclusos
- [x] Room database pronto
- [x] WorkManager pronto
- [x] TensorFlow Lite pronto
- [ ] APK instalado em emulador (próximo passo)
- [ ] App inicializa sem crashes (próximo passo)
- [ ] Autenticação funcional (a implementar)
- [ ] Database sincroniza (a implementar)

---

**Status Final:** BUILD SUCCESSFUL ✅  
**Data:** 27 de Janeiro, 2026  
**Próximo:** Instale no Oracle VM e teste!


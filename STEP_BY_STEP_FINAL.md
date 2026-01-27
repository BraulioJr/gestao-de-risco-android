# 🚀 PASSO-A-PASSO FINAL - Executar no Emulador
## Gestão de Risco v1.0

---

## ⚠️ IMPORTANTE

Se você está tendo problemas com terminal, a melhor opção é usar **Android Studio diretamente**. Siga os passos abaixo:

---

## 📱 MÉTODO 1: Android Studio (RECOMENDADO)

### Passo 1: Abrir o projeto
1. Abra **Android Studio**
2. Menu: **File → Open**
3. Navegue até: `C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco`
4. Clique em **Open** (ou **as a Project**)

### Passo 2: Aguardar sincronização
- Você verá uma barra de progresso
- Android Studio está indexando projeto e sincronizando Gradle
- Espere a mensagem: **"Gradle build successful"** ou **"Gradle sync finished"**
- ⏱️ Tempo: 2-5 minutos na primeira vez

### Passo 3: Preparar emulador
1. Menu: **Tools → Device Manager** (ou buscar por "Device Manager")
2. Você verá lista de emuladores (ou lista vazia se nenhum criado)

#### Se NÃO tiver emulador:
   1. Clique em **Create Device**
   2. Selecione: **Pixel 6** (ou similar)
   3. Clique em **Next**
   4. Escolha versão: **Android 13** ou **Android 14** (ambas boas)
   5. Clique em **Next**
   6. Clique em **Finish**

#### Se JÁ tiver emulador:
   1. Localize o emulador na lista
   2. Clique no botão ▶️ verde **Launch**
   3. Aguarde 1-2 minutos para ele iniciar

### Passo 4: Compilar e rodar
1. Emulador deve estar **pronto/inicializado** (aparece em `Running devices`)
2. Menu: **Run → Run 'app'**
   - Ou pressione: **Shift + F10** (Windows)
3. Uma janela pode aparecer pedindo para selecionar dispositivo
   - Selecione seu emulador da lista
   - Clique em **OK**
4. Android Studio vai:
   - Compilar código
   - Gerar APK
   - Instalar no emulador
   - Iniciar o app

### Passo 5: Ver resultado
✅ A tela de login deve aparecer no emulador em ~30-60 segundos

---

## 🔐 Login no App

Após a tela de login aparecer:

```
Email:    teste@exemplo.com
Senha:    Teste123!
```

⚠️ **NOTA:** Se esses dados não funcionarem, significa que Firebase não foi configurado. Nesse caso:

1. Acesse: https://console.firebase.google.com
2. Vá até seu projeto
3. Crie manualmente um usuário em **Authentication → Email/Password**
4. Use essas credenciais para logar

---

## 📝 APÓS LOGAR

Você deve ver:

1. **Dashboard** com gráficos (pode estar vazio se primeira vez)
2. Botão **"+"** para registrar novo incidente
3. Menu de navegação inferior com opções

---

## 🆘 TROUBLESHOOTING - Android Studio

### Erro: "Cannot find 'gradle' in PATH"
**Solução:**
1. Feche Android Studio
2. Abra pasta do projeto
3. Verifique se `gradlew` e `gradlew.bat` existem
4. Se não existirem, restaure do git: `git checkout gradlew*`
5. Reabra projeto no Android Studio

### Erro: "Gradle sync failed"
**Solução:**
1. Menu: **File → Sync Now**
2. Aguarde concluir
3. Se ainda falhar:
   - Menu: **File → Invalidate Caches / Restart...**
   - Clique em **Invalidate and Restart**
   - Android Studio vai reiniciar e reindexar

### Erro: "Build failed - Lint errors"
**Solução:**
1. Esses são warnings/erros de código (não impedem build)
2. Você pode:
   - Opção A: Corrigir os problemas de lint
   - Opção B: Desabilitar lint para build debug:
     ```gradle
     android {
         lint {
             disable 'MissingClass'  // ou outros erros
         }
     }
     ```

### Emulador não inicia
**Solução:**
1. Verifique se seu PC tem virtualização ativada (BIOS)
2. Ou use dispositivo físico ao invés de emulador:
   - Conecte Android phone via USB
   - Ative "USB Debugging" no device
   - Device vai aparecer em Device Manager

### App casha ao abrir
**Solução:**
1. Abra **Logcat** em Android Studio (View → Tool Windows → Logcat)
2. Veja qual é o erro específico
3. Erros comuns:
   - `FirebaseApp.initializeApp()` - falta `google-services.json`
   - `MissingClass` - dependência não instalada
   - `NullPointerException` - dados não inicializados

---

## 📊 MÉTODO 2: Linha de Comando (Se preferir)

### Passo 1: Compilar
```bash
cd C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco
./gradlew clean assembleDebug
```
⏱️ Espere 5-10 minutos

### Passo 2: Instalar
```bash
adb install -r app\build\outputs\apk\debug\app-debug.apk
```
Você deve ver: `Success`

### Passo 3: Rodar
```bash
adb shell am start -n com.example.project_gestoderisco/.view.LoginActivity
```

---

## 🎯 VALIDAÇÃO FINAL

Após app abrir no emulador:

- [ ] Tela de login é exibida
- [ ] Pode digitar email
- [ ] Pode digitar senha
- [ ] Botão login está habilitado
- [ ] Nenhum crash/erro vermelho

**Se tudo OK:** ✅ **APP RODANDO COM SUCESSO!**

---

## 📱 PRÓXIMOS PASSOS (Uso do App)

1. **Login** com credenciais
2. **Dashboard** - vê gráficos e estatísticas
3. **Novo Incidente** - clique em "+" para registrar
4. **Preencher form** - categoria, loja, descrição, foto
5. **Salvar** - salva localmente
6. **Ver Risco** - predição aparece
7. **Exportar** - gera CSV com dados

---

## ✅ CHECKLIST

Antes de executar:
- [ ] Android Studio instalado
- [ ] Projeto clonado em `C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco`
- [ ] `app/google-services.json` existe
- [ ] Emulador criado OU device Android conectado
- [ ] Conexão internet (para Firebase)

---

## 💡 DICAS

1. **Primeira compilação é mais lenta** (5-15 min)
   - Gradle baixa dependências (~500 MB)
   - Próximas compilações são rápidas (1-2 min)

2. **Se emulador tiver problema de performance:**
   - Aumente RAM alocada no Device Manager (mínimo 2 GB)
   - Desabilite "Show device frame" no emulador

3. **Para debugar código:**
   - Coloque breakpoint no código (clique na linha)
   - Menu: **Run → Debug 'app'**
   - Execution pausará no breakpoint

4. **Para ver logs em tempo real:**
   - Android Studio → Logcat (já aberto embaixo)
   - Filtre por: `package:com.example.project_gestoderisco`

---

## 🎬 RESUMO DE TEMPOS

| Etapa | Tempo |
|-------|-------|
| Sincronização Gradle | 2-5 min |
| Compilação primeira vez | 5-15 min |
| Compilação subsequente | 1-2 min |
| Emulador startup | 1-2 min |
| Instalação APK | 20-30 seg |
| App startup | 5-10 seg |
| **TOTAL (Primeira execução)** | **15-30 min** |
| **TOTAL (Execuções posteriores)** | **5-10 min** |

---

## 📞 SUPORTE

Se algo der errado:

1. **Veja o log completo:**
   - Android Studio → View → Tool Windows → Logcat
   - Copie toda mensagem de erro

2. **Procure por padrão:**
   - Procure por: `ERROR`, `FATAL`, `Exception`, `Caused by:`

3. **Google a mensagem de erro:**
   - Cole a mensagem no Google
   - 99% dos problemas já foram solucionados online

4. **Se ainda estiver com problema:**
   - Veja arquivo: `TROUBLESHOOTING.md` (futura adição)
   - Ou consulte: `README_PROFESSIONAL.md`

---

**Boa sorte! 🚀**

Você está prestes a ver a **Plataforma de Prevenção de Perdas Inteligente** em ação! 

Data: 27 de Janeiro de 2026

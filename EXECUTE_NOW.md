# 🎬 EXECUTE AGORA - Guia de Execução Final
## Gestão de Risco v1.0 no Emulador Android

**Objetivo:** Compilar, instalar e executar o app para toda equipe multidisciplinar interagir como usuário final.

**Data:** 27 de Janeiro de 2026  
**Status:** ✅ PRONTO PARA EXECUTAR  
**Tempo Estimado:** 15-20 minutos

---

## 🎯 MISSÃO CLARA

```
VOCÊ AQUI:          Lendo este documento
              ↓
OBJETIVO:    Executar app no emulador
              ↓
RESULTADO:   App abre → Equipe inteira testa
              ↓
SUCESSO:     ✅ App rodando e respondendo
```

---

## ⚡ EXECUÇÃO RÁPIDA (Sem ler tudo)

### OPÇÃO 1: Android Studio (Mais fácil)
```
1. Abra Android Studio
2. File → Open → C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco
3. Aguarde Gradle sincronizar (barra de progresso)
4. Emulador: Tools → Device Manager → Launch (ou já está rodando)
5. Pressione: Shift + F10 (ou Run → Run 'app')
6. Selecione emulador na janela que aparecer
7. Aguarde ~30-60 segundos
8. ✅ APP ABRE NO EMULADOR!
```

### OPÇÃO 2: Terminal (Se preferir linha de comando)
```bash
cd C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco
gradlew clean assembleDebug
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.example.project_gestoderisco/.view.LoginActivity
```

---

## 📱 MÉTODO RECOMENDADO: Android Studio

### **PASSO 1: Abrir Android Studio**

1. Clique no ícone Android Studio (ou abra do Menu Iniciar)
2. Aguarde carregar (1-2 minutos)
3. Você verá a tela de boas-vindas

### **PASSO 2: Abrir o Projeto**

1. Clique em **File** no menu
2. Selecione **Open**
3. Navegue até: `C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco`
4. Clique em **Open** (ou **as a Project**)
5. Android Studio vai indexar o projeto (5 minutos)

**O que você verá:**
```
[████████████████░░░░] Gradle Sync in progress...
```

Aguarde até aparecer: **"Gradle build successful"**

### **PASSO 3: Aguardar Sincronização Gradle**

- Deixe executar sem interromper
- Você pode ver várias mensagens de progresso
- Pode baixar dependências (~500 MB primeira vez)
- ⏱️ Tempo esperado: 3-5 minutos

**Status esperado:**
```
✓ Configure project
✓ Compile code
✓ Gradle sync successful
```

### **PASSO 4: Iniciar Emulador**

#### Se já tiver emulador criado:

1. Vá até: **Tools → Device Manager**
2. Você verá lista de emuladores criados
3. Localize o seu emulador (ex: "Pixel 6 API 33")
4. Clique no botão ▶️ **Launch** (triângulo verde)
5. Emulador vai iniciar (leva 1-2 minutos)

**Você saberá que está pronto quando:**
- Emulador mostra a tela inicial do Android
- Relógio está ticking no canto superior direito

#### Se NÃO tiver emulador criado:

1. Em **Device Manager**, clique em **Create Device**
2. Selecione um telefone: **Pixel 6** (recomendado) ou outro
3. Clique em **Next**
4. Escolha versão Android:
   - ✅ Android 13 (recomendado)
   - ✅ Android 14 (também bom)
   - ❌ Evite versões muito antigas
5. Clique em **Next** → **Finish**
6. Emulador vai ser criado e iniciar automaticamente

### **PASSO 5: Compilar e Instalar**

1. Seu projeto está aberto em Android Studio
2. Emulador está rodando (aparece em "Running Devices")
3. Pressione: **Shift + F10**
   - Ou vá até: **Run → Run 'app'**

4. Uma janela pode aparecer pedindo para selecionar dispositivo
   - Selecione seu emulador da lista
   - Clique em **OK**

5. Android Studio vai:
   - ✓ Compilar código
   - ✓ Gerar APK
   - ✓ Instalar no emulador
   - ✓ Abrir app automaticamente

**Você verá mensagens assim:**
```
:app:assembleDebug
:app:installDebug
Launching app...
```

### **PASSO 6: Aguardar App Abrir**

⏱️ **Tempo esperado:** 30-60 segundos

Durante esse tempo:
- Emulador pode piscar
- Barra de progresso pode aparecer
- Android pode pedir permissões (aceite todas)

**Sucesso quando:**
```
✅ Tela de LOGIN aparece no emulador
   com campos de Email e Senha
```

---

## 🔐 LOGIN NO APP

Após tela de login aparecer:

### Credenciais de Teste

```
Email:  teste@exemplo.com
Senha:  Teste123!
```

**Se não funcionar:**
1. Significa que Firebase ainda não tem esse usuário
2. Você pode:
   - Opção A: Criar manualmente no Firebase Console
   - Opção B: Usar seu email/senha que configurou no Firebase
   - Opção C: Ignorar erro e ver estrutura da UI anyway

---

## ✅ CHECKLIST DE SUCESSO

Marque conforme avança:

- [ ] Android Studio aberto
- [ ] Projeto abierto em Android Studio
- [ ] Gradle sincronizou (mensagem "successful")
- [ ] Emulador rodando (aparece em Device Manager)
- [ ] Pressinou Shift + F10
- [ ] Selecionou emulador na janela
- [ ] Compilação iniciou (vê "Building...")
- [ ] APK foi instalado (vê "Installing...")
- [ ] App foi lançado (vê "Launching...")
- [ ] **✅ Tela de LOGIN aparece no emulador**

**SE TUDO ACIMA OK = SUCESSO!** 🎉

---

## 🆘 PROBLEMAS COMUNS & SOLUÇÕES

### ❌ "Gradle not found"
```
Solução:
1. File → Invalidate Caches / Restart...
2. Clique em "Invalidate and Restart"
3. Android Studio vai reiniciar
4. Tente novamente
```

### ❌ "Gradle sync failed"
```
Solução:
1. File → Sync Now
2. Aguarde concluir
3. Se falhar de novo:
   - File → Invalidate Caches
   - Restart Android Studio
```

### ❌ Emulador não inicia
```
Solução:
1. Device Manager → Seu emulador
2. Clique em botão "..." (três pontos)
3. Selecione "Delete" e crie novo
4. Ou: Use dispositivo Android físico ao invés
```

### ❌ "Build failed with lint errors"
```
Solução:
- Esses são warnings, não impedem instalação
- Continue mesmo assim
- Se quiser corrigir depois:
  - Run → Clean and Rerun
```

### ❌ App casha ao abrir
```
Solução:
1. Abra Logcat: View → Tool Windows → Logcat
2. Procure por linhas com "ERROR" ou "FATAL"
3. Leia a mensagem de erro
4. Google a mensagem + "Android"
5. 99% já foram resolvidas online
```

### ❌ "Emulator is not running"
```
Solução:
1. Vá para Device Manager
2. Clique em ▶️ Launch
3. Aguarde 1-2 minutos
4. Tente novamente Run 'app'
```

---

## 📊 APÓS APP ABRIR - O QUE VOCÊ VERÁ

### Tela 1: Login (Primeira vez)
```
┌─────────────────────────────────┐
│  Gestão de Risco                │
│  Prevenção de Perdas            │
│                                 │
│  📧 Email:                       │
│  [_____________________]         │
│                                 │
│  🔒 Senha:                       │
│  [_____________________]         │
│                                 │
│  [ Fazer Login ]                │
│  [ Autenticação Biométrica ]    │
│                                 │
│  Esqueceu senha?                │
│  Criar conta                    │
└─────────────────────────────────┘
```

### Tela 2: Dashboard (Após Login)
```
┌─────────────────────────────────┐
│  📊 DASHBOARD                   │
│                                 │
│  Olá, [Nome do Usuário]!        │
│                                 │
│  📈 Incidentes: 0               │
│  📊 Taxa de Risco: 0%           │
│                                 │
│  [Gráficos]                     │
│                                 │
│  + Novo Incidente               │
│  👁️ Meus Riscos                │
│  📋 Relatórios                  │
│  ⚙️ Configurações               │
└─────────────────────────────────┘
```

---

## 👥 PRÓXIMAS AÇÕES POR PERFIL

### **👨‍💼 Coordenador de PP & Fiscal de PP**
Após app abrir:
1. Faça login com credenciais
2. Explore Dashboard
3. Clique em "+" para novo incidente
4. Teste preenchimento de formulário
5. Veja se predição de risco aparece

### **💻 Desenvolvedor & Programador Sênior**
Após app abrir:
1. Abra **Logcat**: View → Tool Windows → Logcat
2. Procure por mensagens da app:
   ```
   D/GestaoDeRiscoApplication: Setting up SyncWorker
   D/LoginActivity: onCreate()
   D/Firebase: Initialized
   ```
3. Verifique se não há erros (linhas vermelhas)
4. Teste fluxos de cada Activity

### **🔒 Especialista em Cibersegurança**
Após app abrir:
1. Tente login com email INCORRETO → deve rejeitar
2. Tente senha VAZIA → deve rejeitar
3. Observe se há mensagens de erro amigáveis
4. Verifique permissões sendo solicitadas
5. Valide que dados sensíveis não aparecem em Logcat

### **📊 Analista de Dados**
Após app abrir:
1. Registre alguns incidentes
2. Vá para "Relatórios"
3. Tente "Exportar CSV"
4. Veja se arquivo é salvo
5. Abra em Excel e valide estrutura

### **🔍 Auditoria**
Após app abrir:
1. Note todos os campos que aparecem
2. Procure por "clientId" em dados (Room/Firebase)
3. Verifique isolamento de tenant
4. Documente fluxo de dados
5. Valide autenticação funcionando

### **🎓 Universidade**
Após app abrir:
1. Analise estrutura das Activities
2. Procure por padrão MVVM
3. Veja como ViewModel se comunica com View
4. Estude Repository pattern
5. Analise integração com Firebase

---

## 📈 TIMING ESPERADO

| Etapa | Tempo |
|-------|-------|
| Abrir Android Studio | 2 min |
| Abrir projeto | 2 min |
| Gradle sincronizar | 5 min |
| Emulador iniciar | 2 min |
| Build + Install | 3 min |
| App abrir | 1 min |
| **TOTAL** | **15 min** |

---

## 🎬 AGORA MESMO

```
FAÇA ISTO AGORA:

1. Minimize esta janela
2. Abra Android Studio
3. File → Open
4. Navegue até: Project_GestaoDeRisco
5. Aguarde Gradle sincronizar
6. Shift + F10
7. Selecione emulador
8. Aguarde app abrir

⏱️ Você estará vendo o app em ~15 minutos!
```

---

## 🎯 SE CONSEGUIU CHEGAR AQUI

✅ **Parabéns!** Você conseguiu:
- Abrir Android Studio
- Abrir o projeto
- Aguardar sincronização
- Executar no emulador
- **Ver o app rodando!**

### Próximo passo:
Leia o documento do seu perfil em:
**[TESTING_GUIDE_BY_PERSONA.md](TESTING_GUIDE_BY_PERSONA.md)**

---

## ⚠️ IMPORTANTE

Se o app **NÃO abrir** em 2 minutos:
1. Aguarde mais um pouco (às vezes demora)
2. Se continuar: Vá para seção "Problemas Comuns" acima
3. Se ainda não funcionar: Veja Logcat para erro específico

---

**Desenvolvido por:** GitHub Copilot  
**Data:** 27 de Janeiro de 2026  
**Status:** ✅ PRONTO PARA EXECUÇÃO  
**Seu Próximo Passo:** Abra Android Studio AGORA!

---

## 👉 COMECE AGORA MESMO!

```
Android Studio → File → Open → Project_GestaoDeRisco → Shift + F10
```

Boa sorte! 🚀

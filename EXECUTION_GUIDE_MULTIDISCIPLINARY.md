# 📱 GUIA DE EXECUÇÃO - Gestão de Risco v1.0
## Plataforma de Prevenção de Perdas Inteligente (SaaS)

---

## 🎯 OBJETIVO
Compilar, instalar e executar o aplicativo Android **Gestão de Risco** no emulador para permitir que toda a equipe multidisciplinar interaja com o sistema como usuários finais.

---

## 👥 EQUIPE MULTIDISCIPLINAR

### 1. **Desenvolvedor Sênior**
   - Responsável pela arquitetura genérica e multi-tenant
   - Monitora isolamento de dados por cliente (`clientId` em todas as queries)
   - Garante padrão MVVM + Repository

### 2. **Especialista em Cibersegurança Sênior**
   - Valida autenticação Firebase (email/biometric)
   - Audita regras Firestore (`firestore.rules`)
   - Verifica isolamento de tenant em todas as operações
   - Monitora permissões Android

### 3. **Programador Sênior**
   - Implementa features seguindo padrões
   - Testa offline-first sync com WorkManager
   - Valida integração Room ↔ Firestore

### 4. **Analista de Dados Sênior**
   - Exporta dados em CSV/Excel via `CsvGenerator.kt`
   - Valida integridade dos dados sincronizados
   - Prepara datasets para treinamento de IA

### 5. **Coordenador de PP Sênior**
   - Testa fluxo de registro de incidentes
   - Valida dashboard com predições de risco
   - Verifica relatórios semanais automáticos

### 6. **Auditoria Interna & Externa Senior**
   - Valida isolamento de dados multi-tenant
   - Audita logs de sincronização
   - Verifica compliance LGPD/GDPR

### 7. **Reitor & Professor (Universidade)**
   - Analisa arquitetura como case de estudo SaaS
   - Valida padrões acadêmicos (MVVM, Clean Architecture)
   - Propõe melhorias para futuras iterações

---

## 🚀 PASSOS DE EXECUÇÃO

### **PASSO 1: Pré-requisitos**
✅ Android Studio instalado (versão 2023.1+)
✅ Android SDK 26-34 instalado
✅ Emulador Android criado (`Tools → Device Manager`)
✅ JDK 17+ instalado

### **PASSO 2: Compilar**
```bash
cd C:\Users\user\AndroidStudioProjects\Project_GestaoDeRisco
./gradlew clean assembleDebug
```
⏱️ Tempo: ~5-10 minutos (primeira vez mais lenta)

### **PASSO 3: Instalar**
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### **PASSO 4: Executar**
```bash
adb shell am start -n com.example.project_gestoderisco/.view.LoginActivity
```

---

## 🎬 FLUXO DO APP (Visão de Usuário Final)

### **1. Tela de Login**
- Email: `teste@exemplo.com`
- Senha: `Teste123!`
- ✓ Autenticação Firebase
- ✓ Sincronização de perfil do usuário

### **2. Dashboard Principal**
- Visualização de gráficos de risco
- Estatísticas por loja/categoria
- Últimos incidentes registrados

### **3. Registro de Incidente**
- Captura de foto (câmera/galeria)
- Preenchimento de formulário
- Seleção de localização
- **Dados salvos localmente** (offline-first)

### **4. Sincronização Background**
- WorkManager executa a cada 1 hora
- Envia imagens → Firebase Storage
- Sincroniza dados → Firestore
- Marca registros como "sincronizados"

### **5. Análise de Risco**
- Modelo TensorFlow Lite prediz risco
- Score + confiança da predição
- Fallback estatístico se modelo falhar

### **6. Relatórios**
- Exporta CSV com histórico de incidentes
- Gera DOCX com análises semanais
- Isolado por cliente (`clientId`)

---

## 🔍 PONTOS-CHAVE A VALIDAR

### **Desenvolvedor Sênior**
- [ ] Arquitetura MVVM funcionando
- [ ] Repository sincroniza Room ↔ Firebase
- [ ] Injeção de dependências (Hilt) funcionando
- [ ] Coroutines e Flow processando corretamente

### **Especialista em Cibersegurança**
- [ ] Autenticação Firebase segura
- [ ] Isolamento de tenant em queries Room
- [ ] Firestore rules bloqueando acessos não autorizados
- [ ] Permissões Android pedidas corretamente
- [ ] Certificados SSL validados

### **Programador Sênior**
- [ ] Offline-first: dados salvos antes de sincronizar
- [ ] SyncWorker executando sem crashes
- [ ] DAO queries retornam dados por clientId
- [ ] Testes unitários passando

### **Analista de Dados**
- [ ] Dados sendo exportados em CSV corretamente
- [ ] Timestamps com timezone corretos
- [ ] Sem vazamento de dados entre clientes
- [ ] Histórico completo de alterações

### **Coordenador PP**
- [ ] Login funciona
- [ ] Registro de incidente intuitivo
- [ ] Câmera captura fotos
- [ ] Predição de risco aparece após registro
- [ ] Dashboard mostra métricas relevantes

### **Auditoria**
- [ ] Logs de sincronização disponíveis (`Logcat`)
- [ ] Nenhum SQL injection possível (queries parametrizadas)
- [ ] Tokens Firebase não expostos em logs
- [ ] Dados sensíveis criptografados em trânsito

### **Universidade**
- [ ] Padrão MVVM bem implementado
- [ ] Repository Pattern encapsulando dados
- [ ] TensorFlow Lite integrado para predições
- [ ] Multi-tenant design escalável

---

## 📊 ARQUITETURA RESUMIDA

```
┌─────────────────────────────────────────────────┐
│          UI Layer (Activities/Fragments)        │
│  (LoginActivity, DashboardActivity, etc)        │
└────────────┬────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────────────┐
│      ViewModel Layer (UI State Management)      │
│  (RiskViewModel, DashboardViewModel, etc)       │
└────────────┬────────────────────────────────────┘
             ↓
┌─────────────────────────────────────────────────┐
│      Repository Layer (Business Logic)          │
│  - OcorrenciaRepository                         │
│  - Orquestra Room + Firebase                    │
│  - Aplica filtros de clientId                   │
└────────┬──────────────────────────┬─────────────┘
         ↓                          ↓
    ┌────────┐              ┌─────────────┐
    │  Room  │              │  Firebase   │
    │  (BD   │              │  (Cloud DB) │
    │ Local) │              │  (Storage)  │
    └────────┘              └─────────────┘
```

---

## 🔧 TROUBLESHOOTING

### **Emulador não inicia**
```bash
# Criar novo emulador
emulator -list-avds
emulator -avd Pixel_6_API_33 -writable-system
```

### **Build falha com "gradle not found"**
```bash
# Usar wrapper direto
cmd /c gradlew.bat build
```

### **APK não instala**
```bash
# Desinstalar versão anterior
adb uninstall com.example.project_gestoderisco

# Instalar novamente
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### **App casha ao abrir**
```bash
# Ver logs
adb logcat | grep -i "com.example.project_gestoderisco"
```

---

## 📈 PRÓXIMAS ETAPAS (After Validation)

1. **Teste de Carga:** Registrar 100+ incidentes e validar performance
2. **Teste de Sincronização:** Desativar WiFi, registrar offline, reativar WiFi
3. **Teste Multi-Tenant:** Simular múltiplos clientes com dados isolados
4. **Teste de IA:** Validar predições de risco com dados conhecidos
5. **Performance:** Profile com Android Profiler (Memory, CPU, Network)
6. **Security:** Teste de penetração com ferramentas especializadas

---

## ✅ CHECKLIST FINAL

- [ ] APK compilado com sucesso
- [ ] Emulador rodando
- [ ] APK instalado no emulador
- [ ] App iniciou sem crashes
- [ ] Login funcionando
- [ ] Dashboard carregando dados
- [ ] Registro de incidente salvo localmente
- [ ] Foto anexada com sucesso
- [ ] Predição de risco calculada
- [ ] WorkManager programado para sync

---

## 📞 SUPORTE

Para dúvidas ou problemas:
- Desenvolvedores: Verificar `Logcat` para erro específico
- Cibersegurança: Revisar `firestore.rules` e permissões Android
- Dados: Exportar CSV e validar integridade
- PP: Testar fluxo completo de registro a análise
- Auditoria: Revisar logs em `app/logs/` ou Firebase Console

**Data:** 27 de Janeiro de 2026
**Versão do App:** 1.0 (Build 1)
**Status:** PRONTO PARA TESTE EM EMULADOR

---

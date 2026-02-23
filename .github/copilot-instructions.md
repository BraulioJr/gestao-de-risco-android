# AI Copilot Instructions for Gestão de Risco

**Potencial SaaS — Plataforma de Prevenção de Perdas Inteligente**

Este é um **aplicativo Android offline-first** desenhado como produto escalável (SaaS) para redes varejistas registrarem, analisarem e preverem incidentes de furto usando Room (BD local), Firestore sync, Firebase e TensorFlow Lite. A arquitetura foi projetada para ser **genérica, configurável por cliente e facilmente adaptável** a diferentes redes, mantendo um núcleo sólido compartilhado.

## Architecture Overview

**Pattern:** MVVM + Clean Architecture (UseCases) + Repository Pattern com Offline-First + **Tenant-Aware Design** (SaaS)

- **UI Layer:** Android Views (Material Design 3) com ViewBinding; Activities/Fragments comunicam via ViewModels
- **ViewModel Layer:** Gerencia estado da UI com LiveData/Flow; recupera de process death
- **Domain Layer (UseCases):** Regras de negócio puras; orquestra chamadas aos repositórios
- **Repository Layer:** Fonte única da verdade; coordena entre Room local e Firestore remoto
- **Data Layer (Tenant-Aware):** Room DB é primária; synca com Firestore via `SyncWorker` a cada 1h; dados isolados por `clientId`/`tenantId`
- **AI Prediction Layer:** TensorFlow Lite para predições em tempo real; fallback estatístico garantido
- **Dependency Injection:** Hilt para gerenciamento de dependências e escopos
- **Arquivos-Chave:**
  - `data/local/AppDatabase.kt` — Definição Room; migrations automáticas; tabelas com filtros por tenant
  - `data/local/OcorrenciaDao.kt` — CRUD com escopo de tenant (`WHERE clientId = ?`)
  - `repository/OcorrenciaRepository.kt` — Orquestra Room ↔ Firebase; aplica lógica de negócio de IA
  - `worker/SyncWorker.kt` — Sync background (imagens → Firebase Storage, dados → Firestore) com isolamento de tenant
  - `model/Ocorrencia.kt` — Modelo de domínio (puro); sempre inclui `clientId` para isolamento
  - `viewmodel/RiskViewModel.kt` — Predições e lógica de IA expostas para UI
  - `domain/usecase/PredictRiskUseCase.kt` — Encapsula lógica de predição

## Considerações para SaaS & Multi-Tenant

1. **Isolamento de Dados por Cliente:** Todo registro (Ocorrencia, UserProfile) inclui `clientId`/`tenantId`; consultas Room sempre filtram por cliente
2. **Configurabilidade por Cliente:** Diferentes redes podem ter:
   - **Categorias de produtos customizadas** — armazenar em Firestore como doc `clients/{clientId}/config/categories`
   - **Horários críticos de operação** — modificar thresholds de alerta por cliente
   - **Modelos de IA ajustados** — múltiplos `risk_model_{clientId}.tflite` em assets ou download dinâmico de Firestore
   - **Branding** — cores, logos, strings de app por cliente (já suportado via `values/colors.xml`, `strings.xml`)
3. **Sincronização Isolada:** `SyncWorker` responde apenas por dados do cliente autenticado (evita exposição cross-tenant)
4. **Escalabilidade:** Firestore está pronto para múltiplos tenants; estrutura: `clients/{clientId}/ocorrencias/{id}`
5. **Suporte Multidisciplinar:**
   - **Desenvolvedor Sênior:** Mantém código genérico, ativa features por feature flags por cliente
   - **Analista de Dados:** Exportações CSV (`CsvGenerator.kt`) filtram por client; dados prontos para treinamento de IA
   - **Coordenador PP:** Usa dashboard agnóstico ao cliente; relatórios semanais isolados por tenant

## Offline-First Sync Strategy

1. **Record locally first** — All incidents saved to Room immediately (no network required)
2. **Background sync via WorkManager** — `SyncWorker` runs every 1 hour when network available
3. **NetworkBoundResource Pattern:**
   - Flow: Local DB -> UI
   - Fetch: API call
   - Save: API result -> Local DB
   - Re-emit: Updated Local DB -> UI
4. **Conflict Resolution:**
   - **Last-Write-Wins:** Server timestamp vs Local timestamp.
   - **Smart Merge:** Merge non-conflicting fields.
5. **Incremental Sync:** Send `lastSyncTimestamp` to API; receive only deltas.
6. **Sync pattern:** Mark records as unsynced in Room → upload attachments to Firebase Storage → push to Firestore → mark as synced
7. **Fallbacks:** If image upload fails, sync text data anyway; if sync fails, retry next cycle

## Build & Run

- **Min SDK:** 24 | **Target SDK:** 34 | **JVM Target:** 17 (Kotlin)
- **Gradle:** Version catalog in `gradle/libs.versions.toml`; JitPack maven for MPAndroidChart
- **Key dependencies:** Hilt (DI), Coroutines, Firebase BOM, Room, WorkManager, MPAndroidChart
- **Build:** `./gradlew build` (Windows: `gradlew.bat build`)
- **Run:** Open in Android Studio → Sync Gradle → Run on emulator/device

## Firebase Setup

- **Required:** `google-services.json` in `app/` directory (from Firebase Console)
- **Emulator:** `FirebaseEmulatorSetup.kt` handles local development setup
- **Services Used:** Auth (email), Firestore (incidents), Cloud Storage (evidence photos), Cloud Messaging (push notifications)

## Authentication & Authorization

- **Pattern:** `AuthRepository` wraps Firebase Auth; results use `Result<T>` sealed class
- **Flow:** LoginActivity → AuthRepository.signInWithEmail() → Firestore user profile sync
- **Biometric Protection:** BiometricPrompt API guards sensitive features (config, exports)

## Testing

- **Unit tests:** JUnit + Mockk + Turbine (Flow testing); use `MainCoroutineRule` for coroutine tests
- **UI Tests:** Espresso for instrumentation tests
- **Static Analysis:** Detekt and Ktlint for code style and smell detection
- **CI/CD:** GitHub Actions for automated testing; Firebase App Distribution for QA builds
- **Test files:** `app/src/test/` and `app/src/androidTest/`
- **Example:** `MainViewModelTest.kt`, `OcorrenciasRepositoryTest.kt`, `WordGeneratorTest.kt`

## Observability & Monitoring

- **Crash Reporting:** Firebase Crashlytics for fatal/non-fatal errors.
- **Performance:** Firebase Performance Monitoring for network latency and app startup time.
- **Logging:** Use `Timber` for structured logging.
  - Debug: Verbose logs.
  - Release: Only warnings/errors (or send to Crashlytics).
- **Feature Flags:** Use Firebase Remote Config to enable/disable features per tenant/client without app updates.

## Estrutura de Arquivos Detalhada

```
app/src/main/java/com/example/project_gestoderisco/
├── auth/
│   ├── AuthRepository.kt                 # Firebase Auth wrapper; Result<T> pattern
│   ├── LoginActivity.kt                  # Login UI; biometric auth trigger
│   └── LoginViewModel.kt                 # Auth state management
├── data/
│   └── local/
│       ├── AppDatabase.kt                # Room DB definition; entities migration
│       ├── OcorrenciaDao.kt             # CRUD: @Insert, @Update, @Delete, @Query
│       ├── OcorrenciaEntity.kt          # Room entity; mirrors Ocorrencia model
│       └── Converters.kt                # Type converters (Date, List, etc.)
├── dashboard/
│   ├── DashboardFragment.kt             # Main analytics UI; ViewPager2 tabs
│   ├── DashboardViewModel.kt            # Chart data aggregation
│   ├── DashboardModels.kt               # UI state (bubble, line, bar chart data)
│   ├── DashboardPagerAdapter.kt         # Fragment tab adapter
│   └── RankingAdapter.kt                # RecyclerView for incident rankings
├── model/
│   ├── Ocorrencia.kt                    # Domain model (pure data)
│   ├── OcorrenciaEntity.kt              # Room entity (with @Entity, @PrimaryKey)
│   ├── OcorrenciaRequest.kt             # API request DTO
│   ├── OcorrenciaResponse.kt            # Firestore response DTO
│   ├── Risk.kt                          # Risk prediction model
│   ├── UserProfile.kt                   # User info (synced to Firestore)
│   └── LgpdDetails.kt                   # LGPD compliance metadata
├── domain/
│   ├── usecase/                         # Regras de negócio puras
│   │   ├── PredictRiskUseCase.kt
│   │   └── SyncDataUseCase.kt
│   └── repository/                      # Interfaces de repositório (Clean Arch)
├── repository/
│   ├── OcorrenciaRepository.kt          # Business logic; Room ↔ Firebase bridge
│   ├── NotificationRepository.kt        # FCM & push notification handling
│   └── ApiService.kt                    # Retrofit API client definition
├── worker/
│   ├── SyncWorker.kt                    # WorkManager: upload images + sync to Firestore (1h cycle)
│   ├── ArchiveWorker.kt                 # Auto-archive old incidents
│   └── WeeklyReportWorker.kt            # Generate & email weekly CSV reports
├── viewmodel/
│   ├── MainViewModel.kt                 # Root app state
│   ├── RegistroOcorrenciaViewModel.kt   # Incident form state
│   ├── RiskViewModel.kt                 # Risk list & details state
│   ├── DashboardViewModel.kt            # Chart data aggregation
│   ├── AuthViewModel.kt                 # Auth state
│   └── *ViewModelFactory.kt             # Factory constructors (dependency injection)
├── di/
│   ├── AppModule.kt                     # Hilt modules (Singleton)
│   ├── RepositoryModule.kt              # Binds/Provides repositories
│   └── DatabaseModule.kt                # Room provider
├── view/ or *Activity.kt
│   ├── MainActivity.kt                  # Home/navigation hub
│   ├── LoginActivity.kt                 # Auth entry point
│   ├── RegistroOcorrenciaActivity.kt    # Incident form + camera/gallery
│   ├── RiskDetailActivity.kt            # Single incident details
│   ├── DashboardActivity.kt             # Analytics dashboard container
│   └── MapActivity.kt                   # Incident location clustering (Google Maps)
├── utils/
│   ├── CsvGenerator.kt                  # Export incidents to CSV
│   ├── WordGenerator.kt                 # Generate .docx reports
│   ├── ReportSchedulerUtils.kt          # Weekly report scheduling logic
│   ├── CustomMarkerView.kt              # MPAndroidChart custom markers
│   └── RiskClusterRenderer.kt           # Map cluster visualization
├── GestaoDeRiscoApplication.kt          # App entry; sets up SyncWorker (1h interval)
├── MyFirebaseMessagingService.kt        # FCM message handler (push notifications)
├── FirebaseEmulatorSetup.kt             # Dev setup for Firebase local emulator
└── ThemeManager.kt                      # Dark/light theme management

app/src/main/res/
├── layout/
│   ├── activity_main.xml                # Bottom navigation + Fragment container
│   ├── activity_login.xml               # Login form
│   ├── activity_registro.xml            # Incident form (multi-section)
│   ├── fragment_dashboard.xml          # ViewPager2 for chart tabs
│   ├── item_ocorrencia.xml             # List item for incidents
│   └── ...
├── values/
│   ├── strings.xml                      # All user-facing text (i18n ready)
│   ├── colors.xml                       # Material Design 3 color palette
│   ├── themes.xml                       # Light/dark theme definitions
│   └── dimens.xml                       # Spacing & sizing constants
├── drawable/
│   └── *.xml                            # Vector assets (Material icons)
└── menu/
    └── *.xml                            # Bottom nav, action bar menus

gradle/
└── libs.versions.toml                   # Centralized dependency versions (Version Catalog)

app/build.gradle.kts                     # App-level Gradle config; includes all plugins
settings.gradle.kts                      # Root config; includes JitPack maven
gradle.properties                        # JVM args, AndroidX config

firestore.rules                          # Security rules (development/staging rules)
google-services.json                     # Firebase config (must be in app/)
firebase.json                            # Firebase CLI config
```

### Layer Responsibilities

**Presentation (View/Activity/Fragment):**

- Inflate layouts, bind ViewBinding
- Observe LiveData/Flow from ViewModel
- Pass user input to ViewModel
- Do NOT touch repositories directly

**ViewModel:**

- Hold UI state in LiveData/Flow
- Call UseCases (Domain) suspend functions via coroutines
- Emit state changes (sealed UiState classes)
- Recover state on process death

**Domain (UseCase):**

- Encapsulate reusable business logic
- Combine data from multiple repositories
- Pure Kotlin (no Android dependencies ideally)

**Repository:**

- Query Room DAO (primary source)
- Handle Firebase operations with try-catch
- Return Result<T> for success/failure
- Map entities ↔ DTOs

**Data Layer (Room):**

- OcorrenciaEntity mirrored from Ocorrencia model
- Queries: `getUnsyncedOcorrencias()`, `markAsSynced()`, etc.
- Auto-migrations tracked in AppDatabase

## Conventions & Patterns

1. **Naming:** Kotlin/Android standard (camelCase variables, PascalCase classes)
2. **Coroutines:** All async work via suspend functions; use `tasks.await()` for Firebase APIs
3. **Data mapping:** Local `OcorrenciaEntity` (Room) ↔ `OcorrenciaRequest`/`OcorrenciaResponse` (API)
4. **Error handling:** Try-catch in repositories; propagate errors up through Result type or sealed States
5. **Resource strings:** All user-facing text in `res/values/strings.xml`; colors in `res/values/colors.xml`

## Key Features & Modules

- **Dashboard Analytics (Multi-Tenant):** MPAndroidChart (bubble, line, bar, scatter charts); `DashboardFragment.kt` filtra dados por cliente
- **Incident Recording:** `RegistroOcorrenciaActivity.kt` — captura de form com attachment de foto; sempre salva `clientId`
- **Risk Prediction (IA):** Modelo TensorFlow Lite (`risk_model.tflite` em `assets/`); fallback estatístico; predições em tempo real conforme novos incidentes são registrados
  - `Risk.kt` contém scores e confiança da predição
  - `RiskViewModel.kt` orquestra predições e expõe para UI
  - Exportar dados históricos de risco para treinamento contínuo do modelo
- **Automated Reports:** `ReportSchedulerUtils.kt` + `WeeklyReportWorker.kt` — relatórios semanais em CSV, isolados por cliente e e-mail do Coordenador
- **Export Tools:** `CsvGenerator.kt`, `WordGenerator.kt` para exportação de dados em formatos externos (Power BI, Tableau, Excel)
- **Map & Clustering:** `RiskClusterRenderer.kt` — visualização geográfica de hotspots de risco por loja

## Genericidade & Adaptabilidade (Design SaaS)

### Princípios de Design

1. **Código Genérico, Configuração por Cliente:**
   - UI e lógica de negócio NÃO devem hardcoding valores específicos de rede
   - Usar `SharedPreferences`, `DataStore`, ou Firestore `clients/{clientId}/config` para customizações
   - Feature flags em Firestore: `clients/{clientId}/features/{featureName}`

2. **Estrutura de Dados Agnóstica:**
   - `Ocorrencia.kt` é o modelo puro sem dependências de cliente específico
   - `OcorrenciaEntity` adiciona `clientId`, `storeId` como identificadores de escopo
   - Sempre projetar modelos pensando: "Isso funcionaria em outra rede?"

3. **Customizações por Cliente (Exemplos):**
   - **Categorias de Produtos:** Firestore doc `clients/{clientId}/config/categories` com lista dinâmica
   - **Horários de Operação:** `clients/{clientId}/config/operatingHours` define thresholds de alerta
   - **Branding:** Cores, fontes, strings em `res/values/` já suportam múltiplas configurações
   - **Modelos IA:** Baixar `risk_model_{clientId}.tflite` do Firebase Storage em login (mais escalável)
   - **Campos Customizados:** Adicionar JSON `customFields: Map<String, Any>` a `Ocorrencia` para flexibilidade

4. **Isolamento de Tenant em Todos os Níveis:**
   - **Room queries:** `WHERE clientId = ?` em CADA query
   - **Firestore rules:** Validar `clientId` em read/write
   - **WorkManager:** SyncWorker respeita `clientId` do usuário autenticado
   - **Analytics:** Eventos rastreiam `clientId` para debug multi-tenant

5. **Escalabilidade Horizontal:**
   - Firestore estrutura: `clients/{clientId}/ocorrencias/{id}` permite particionamento
   - Índices Firestore otimizados por `clientId + timestamp`
   - Storage: imagens em `gs://bucket/clients/{clientId}/evidence/{id}.jpg`
   - Considerar sharding de dados para muito alto volume por cliente no futuro

### Checklist para Nova Feature

Ao implementar qualquer funcionalidade:

- [ ] Incluir `clientId`/`tenantId` em TODOS os modelos novos?
- [ ] Room queries têm filtro `WHERE clientId = ?`?
- [ ] Firestore rules validam isolamento de tenant?
- [ ] UI é agnóstica ou permite customização?
- [ ] Testes cobrem múltiplos clientes (mocking `clientId`)?
- [ ] Documentação menciona padrão de isolamento?

- **Adicionar novo campo a incidentes:** Modificar `Ocorrencia.kt` → adicionar a `OcorrenciaEntity` → criar Room migration → atualizar sync em `SyncWorker` → garantir `clientId` em filtros
- **Customizar modelo IA por cliente:**
  - Opção A: Múltiplos `.tflite` em assets (`risk_model_{clientId}.tflite`)
  - Opção B: Download dinâmico do modelo do Firestore durante login (mais escalável para SaaS)
  - Sempre incluir fallback estatístico em `Risk.kt`
- **Ajustar intervalo de sync:** Editar WorkManager constraints em `GestaoDeRiscoApplication.kt` (atualmente 1h); configurável por cliente em Firestore
- **Adicionar função Firebase:** Seguir padrão em `AuthRepository` com `tasks.await()`; testar com emulador; garantir isolamento de tenant
- **Debugar problemas de layout:** Usar Android Studio Layout Inspector; verificar ViewBinding references nos XMLs
- **Testar regras Firestore:** Arquivo `firestore.rules` na raiz; validar com `firebase emulator:start` antes do deploy; incluir regras de isolamento de tenant:
  ```
  match /clients/{clientId}/ocorrencias/{document=**} {
    allow read, write: if request.auth.uid != null && getUserClientId(request.auth.uid) == clientId;
  }
  ```

## Multi-Tenant Data Isolation Pattern (Crítico para SaaS)

```kotlin
// Em OcorrenciaDao.kt, SEMPRE incluir clientId em queries:
@Query("SELECT * FROM ocorrencias WHERE clientId = :clientId AND synced = 0")
suspend fun getUnsyncedOcorrencias(clientId: String): List<OcorrenciaEntity>

// Em Repository, passar clientId automaticamente do usuário autenticado:
suspend fun getOcorrencias(clientId: String) = withContext(Dispatchers.IO) {
    ocorrenciaDao.getAllOcorrencias(clientId)  // NUNCA sem filtro clientId!
}

// Em ViewModel, obter clientId do AuthRepository ou SharedPreferences
val clientId = authRepository.currentUserClientId()
```

## IA & Machine Learning Strategy

- **Stack:** TensorFlow Lite + Google ML Kit (Vision API).
- **Treinamento:** Dados de múltiplos clientes (anonymizados) → modelo base em TensorFlow.
- **Deployment:** Modelo quantizado (`.tflite`) incluído em app; atualizações via Firebase Model Downloader.
- **Features:**
  - **Predição de Risco:** `RiskViewModel` usa TFLite.
  - **OCR/Vision:** Processamento de imagens de evidência para metadados automáticos.
- **Predição Real-Time:** Quando novo `Ocorrencia` é criado, chamar `RiskViewModel.predict(ocorrencia)` para score imediato
- **Feedback Loop:** Cada caso resolvido/investigado gera dados para retreinamento mensal do modelo
- **Fallback:** Se modelo falha ou ausente, usar modelo estatístico em `Risk.kt` baseado em histórico de cliente

## SaaS Deployment & Onboarding

**New Client Onboarding (Target: < 1 semana)**

1. **Setup Firebase Project:** Criar novo projeto Firebase para cliente OR usar Firestore com `clientId` isolado
2. **Gerar APK Customizado:** Build com branding do cliente (strings, cores em `res/values/`)
3. **Importar Configurações:**
   - Categorias de produto via CSV → Firestore `clients/{clientId}/config/categories`
   - Horários operacionais → `clients/{clientId}/config/operatingHours`
   - Modelo IA customizado (se houver) → Firebase Storage + ref em `clients/{clientId}/config/model`
4. **User Provisioning:** Criar usuários iniciais (Coordenador PP, Analista de Dados) via Firebase Auth
5. **Test Sync & Reporting:** Validar sincronização offline→online; testar geração de relatórios
6. **Go-Live:** Deploy em emuladores de cliente; monitoramento contínuo via Firebase Analytics

**Version Management**

- Min SDK: 24 | Target SDK: 34 | Current Build: 1.0
- Incrementar versionCode a cada release de build
- Breaking changes em DAO/Entity requerem Room migration automática (`autoMigrations` em AppDatabase)

## External Resources

- **Firebase:** Firestore, Storage, Auth, Cloud Messaging, Cloud Functions
- **TensorFlow Lite:** Embedded model inference (CPU-based for on-device predictions)
- **MPAndroidChart:** Charting library (JitPack Maven)
- **Room:** Type-safe database abstraction (migrations auto-tracked)

## Personas & Multidisciplinary Team Workflows

### 👨‍💼 **Coordenador de Prevenção de Perdas (PP) Sênior**

- Acessa dashboard agnóstico do cliente para visualizar métricas de risco
- Usa predições de IA para alocar recursos de forma preventiva (não reativa)
- Recebe relatórios semanais automáticos com análises por loja/categoria
- Acompanha ciclo de vida de casos (Aberto → Em Investigação → Resolvido)
- **Para devs:** Requisitos de novo campo em `Ocorrencia` devem vir com aprovação dele

### 📈 **Analista de Dados Sênior**

- Exporta dados CSV/Excel para análises avançadas em Power BI, Tableau
- Identifica padrões e correlações que alimentam retreinamento do modelo de IA
- Trabalha com dados limpos e filtrados por cliente (isolamento garantido)
- Valida qualidade de predições; sugere ajustes de threshold de alerta
- **Para devs:** Mudanças em `CsvGenerator.kt` e schema de `Ocorrencia` requerem aprovação

### 💻 **Desenvolvedor & Programador Sênior**

- Mantém núcleo genérico; usa feature flags para customizações por cliente
- Garante isolamento de tenant em todas as queries Room (`WHERE clientId = ?`)
- Integra novos modelos TensorFlow Lite; testa fallbacks estatísticos
- Implementa sincronização sem perda de dados; trata conflitos Room ↔ Firestore
- Escreve testes automatizados (Mockk, Robolectric) para repositórios e ViewModels
- **Responsabilidade crítica:** Garantir zero vazamento de dados entre clientes

### 💰 **Investidor Sênior (SaaS Vision)**

- Vê modelo de receita recorrente (MRR) para cada cliente
- Reconhece redução de perdas por loja como métrica de sucesso (ROI calculável)
- Identifica oportunidade de upupsell: modelos IA customizados, relatórios premium, consulting
- Avalia tempo de onboarding de novo cliente (target: < 1 semana)

## Notes for AI Agents

- Always prioritize offline functionality — assume users may lack network
- Use `suspend` functions and `Result<T>` for safety
- **CRITICAL for SaaS:** Every Room query MUST include `clientId` filter; audit for SQL injection
- Keep Firestore reads/writes minimal (large document counts impact cost & performance)
- Test background sync in isolation (use `SyncWorker` directly in tests)
- Reference existing tests as patterns; do not skip test coverage for new features
- When adding config fields: store in Firestore `clients/{clientId}/config/*`, not in app code
- IA Model: Always export prediction confidence score alongside risk score
- Multi-tenant audit: Search codebase for `WHERE`, `Query`, `.filter()` — ensure no cross-tenant leaks

## Git & Commit Standards

Follow **Conventional Commits** 1.0.0:

- Format: `type(scope): description`
- Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`
- Scopes: `auth`, `ui`, `data`, `core`, `gamification`, `roi`, `worker`
- Example: `feat(gamification): implement xp calculation engine`

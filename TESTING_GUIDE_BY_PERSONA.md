# 🎯 GUIA INTERATIVO POR PERSONA - Gestão de Risco

---

## 1️⃣ **DESENVOLVEDOR SÊNIOR**

### Seu Papel
Garantir que a arquitetura genérica funciona, o código é escalável e multi-tenant.

### O que testar no emulador

#### ✓ Fluxo de Startup
```
App inicia → GestaoDeRiscoApplication.onCreate()
  → Setup WorkManager (sync 1h)
  → Hilt inicializa dependencies
  → Firebase inicializa
```

#### ✓ Padrão MVVM
1. Abra a Activity de Login
2. Observe que `LoginViewModel` controla o estado
3. `LiveData` atualiza a UI automaticamente
4. Erro de login → ViewModel falha gracefully

#### ✓ Repository Pattern
1. Clique em "Registrar Incidente"
2. Preencha formulário
3. Clique em "Salvar"
   - Deve aparecer em `Room` imediatamente (banco local)
   - `OcorrenciaRepository.saveOcorrencia()` foi chamado
   - `OcorrenciaDao.insert()` salva localmente

#### ✓ Injeção de Dependências (Hilt)
```kotlin
// Verificar no código:
// - @HiltViewModel em ViewModels
// - @Inject em Repositories
// - @Provides em Modules
```

#### ✓ Coroutines & Flow
1. Registre um incidente
2. Observe que UI não trava
3. Salvamento acontece em background
4. Predição de IA é calculada sem bloquear

---

## 2️⃣ **ESPECIALISTA EM CIBERSEGURANÇA SÊNIOR**

### Seu Papel
Validar autenticação, autorização, isolamento de dados e proteção contra ataques.

### O que testar no emulador

#### ✓ Autenticação Firebase
1. Clique em "Login"
2. Insira credenciais incorretas → Deve rejeitar
3. Insira credenciais corretas → Deve logar
4. Verifique token Firebase no Logcat

#### ✓ Biometric Auth (Advanced)
1. Após login bem-sucedido
2. Vá para "Configurações"
3. Ative "Autenticação Biométrica"
4. Logout e tente logar novamente
5. Deve solicitar fingerprint/face

#### ✓ Isolamento de Tenant
```kotlin
// Verificar no código:
// OcorrenciaDao.kt - TODA query tem:
// WHERE clientId = :clientId

@Query("SELECT * FROM ocorrencias WHERE clientId = :clientId")
suspend fun getAllOcorrencias(clientId: String): List<OcorrenciaEntity>
```

#### ✓ Permissões Android
1. Vá para "Registrar Incidente"
2. Tente tirar foto → Deve pedir permissão de câmera
3. Tente anexar galeria → Deve pedir permissão de fotos
4. Aceite as permissões → Deve funcionar

#### ✓ SSL Pinning (Firebase)
- Firebase usa certificates válidos
- Verifique em `network_security_config.xml`
- Nenhuma request deve ir para servidor não HTTPS

#### ✓ Dados Sensíveis em Logcat
```bash
adb logcat | grep -i "password\|token\|auth"
# Não deve aparecer dados sensíveis!
```

#### ✓ Storage Seguro
- Preferências devem estar encriptadas
- Usar `EncryptedSharedPreferences`
- Room DB pode estar encriptado com SQLCipher

---

## 3️⃣ **PROGRAMADOR SÊNIOR**

### Seu Papel
Implementar features, testar offline-first, validar sincronização.

### O que testar no emulador

#### ✓ Offline-First Sync
1. **Desative WiFi no emulador:**
   ```bash
   adb shell cmd connectivity airplane-mode enable
   ```

2. **Registre um incidente**
   - Preencha forma completo
   - Clique "Salvar"
   - Deve salvar LOCALMENTE sem network

3. **Reative WiFi:**
   ```bash
   adb shell cmd connectivity airplane-mode disable
   ```

4. **Aguarde SyncWorker executar (~1 hora ou force)**
   ```bash
   adb shell am broadcast -a android.intent.action.BOOT_COMPLETED
   ```

5. **Verifique sincronização**
   - Dados devem ir para Firebase
   - Status deve mudar para "Sincronizado"

#### ✓ Room Database
1. Abra Android Studio → Device File Explorer
2. Navegue: `data/data/com.example.project_gestoderisco/databases/`
3. Baixe `app_database.db`
4. Abra com ferramenta SQLite
5. Verifique tabelas:
   - `ocorrencias` (com `clientId`)
   - `users`
   - Verifique migrations automáticas

#### ✓ SyncWorker
```bash
# Ver logs de sincronização
adb logcat | grep -i "SyncWorker"

# Deve aparecer:
# "Starting sync..."
# "Uploaded image to Storage"
# "Synced to Firestore"
# "Marked as synced"
```

#### ✓ Error Handling
1. Simule erro de rede
2. Registre incidente
3. App deve:
   - Salvar localmente ✓
   - Mostrar mensagem amigável
   - Retomar sync quando rede voltar

---

## 4️⃣ **ANALISTA DE DADOS SÊNIOR**

### Seu Papel
Validar integridade de dados, exportações e preparar datasets para IA.

### O que testar no emulador

#### ✓ Exportação CSV
1. Vá para "Relatórios"
2. Clique "Exportar CSV"
3. Arquivo deve ser salvo em `Downloads/`
4. Abra em Excel e valide:
   - Todas as colunas estão presentes?
   - Dados estão corretos?
   - Datas têm timezone?
   - Sem vazamento de dados de outros clientes?

#### ✓ Dados por Cliente
```bash
# Verificar que CSV filtra por clientId
# Simular múltiplos clientes:
# - Cliente A: registra 5 incidentes
# - Cliente B: registra 5 incidentes
# - Exportar como Cliente A
# - CSV deve ter APENAS incidentes de A
```

#### ✓ Histórico de Alterações
- Cada incidente tem `createdAt` e `updatedAt`
- Modificar incidente → `updatedAt` atualiza
- Verificar timestamp está em UTC

#### ✓ Qualidade de Dados
1. Registre incidente com campos obrigatórios
2. Deixe alguns campos vazios (opcionais)
3. Exporte CSV
4. Valores NULL devem estar claramente marcados
5. Nenhum valor corrompido ou garbled

#### ✓ Validação de Predições de IA
- Cada incidente tem campo `riskScore`
- Score de 0.0 a 1.0
- Confiança também de 0.0 a 1.0
- Exportar e validar distribuição de scores

---

## 5️⃣ **COORDENADOR DE PREVENÇÃO DE PERDAS (PP) SÊNIOR**

### Seu Papel
Usar o sistema como usuário final, validar se atende necessidades operacionais, estratégia de risco.

---

## 5️⃣-B **FISCAL DE PREVENÇÃO DE PERDAS SÊNIOR**

### Seu Papel
Executor operacional das estratégias de PP; valida se sistema atende necessidades de campo, facilita rotina operacional.

### O que testar no emulador

#### ✓ Login
```
Email: teste@exemplo.com
Senha: Teste123!
(Configure no Firebase Console)
```

#### ✓ Dashboard
1. Após login, veja Dashboard
2. Deve mostrar:
   - Gráfico de incidentes por loja
   - Gráfico de incidentes por categoria
   - Estatísticas: Total, esta semana, hoje
   - Ranking de lojas com mais risco

#### ✓ Registrar Incidente
1. Clique no botão "+" ou "Novo Incidente"
2. Preencha:
   - Categoria do produto (furto, dano, etc)
   - Localização (loja)
   - Data/Hora
   - Descrição
   - Fotos
   - Responsáveis
3. Clique "Salvar"
4. Deve aparecer:
   - Confirmação visual
   - Score de risco predito
   - Recomendações

#### ✓ Visualizar Risco
1. Vá para "Meus Riscos"
2. Veja lista de incidentes registrados
3. Clique em um incidente
4. Veja detalhes:
   - Foto anexada
   - Predição de risco
   - Tendência histórica
   - Ações recomendadas

#### ✓ Relatórios Semanais
1. Vá para "Relatórios"
2. Clique "Relatório desta Semana"
3. Deve mostrar:
   - Incidentes por dia
   - Categorias mais comuns
   - Lojas com maior risco
   - Tendências

#### ✓ Intuitiveness
- Botões estão nos lugares certos?
- Fluxo de registro é lógico?
- Mensagens de erro são claras?
- Ícones são compreensíveis?

---

## 5️⃣-B **FISCAL DE PREVENÇÃO DE PERDAS SÊNIOR**

### Seu Papel
Executor das estratégias de PP em campo, valida usabilidade e eficiência operacional.

### O que testar no emulador

#### ✓ Fluxo Rápido de Registro
1. Abra app com login de teste
2. Clique em "+" para novo incidente
3. **Objetivo:** Fazer todo registro em < 2 minutos

#### ✓ Registro Offline
1. Desative WiFi: `adb shell cmd connectivity airplane-mode enable`
2. Tente registrar incidente
3. App DEVE funcionar mesmo sem internet
4. Reative WiFi: `adb shell cmd connectivity airplane-mode disable`

#### ✓ Captura de Foto
1. Vá para "Novo Incidente"
2. Clique em "Adicionar Foto"
3. Escolha câmera ou galeria
4. Tire/selecione uma foto
5. Foto deve aparecer no formulário

#### ✓ Pesquisa de Incidentes
1. Vá para "Meus Riscos"
2. Procure filtro/busca por:
   - Data
   - Loja
   - Categoria
   - Status
3. Resultado deve ser rápido (< 1 segundo)

#### ✓ Performance
1. Registre 10 incidentes
2. Abra lista de incidentes
3. Scroll deve ser suave (não travar)
4. Clique em um incidente
5. Detalhes devem aparecer rápido

#### ✓ Notificações
1. Registre incidente de alto risco
2. Sistema deve notificar visualmente
3. Coordenador PP deve receber alerta
4. (Depende de Firebase configurado)

#### ✓ Relatórios Rápidos
1. Vá para "Relatórios"
2. Clique "Relatório Hoje"
3. Deve gerar em < 3 segundos
4. Exibir gráficos/estatísticas

#### ✓ Usabilidade em Campo
```
Checklist de Usabilidade:
- [ ] Tela legível com luz solar?
- [ ] Botões grandes o suficiente para tocar?
- [ ] Fluxo é intuitivo (sem manual)?
- [ ] Não tem campos desnecessários?
- [ ] Mensagens de erro são claras?
- [ ] App não casha ao registrar?
- [ ] Fotos carregam rápido?
```

#### ✓ Sincronização Automática
1. Registre incidente offline
2. Ligue internet
3. Aguarde 1 hora OU force sync
4. Incidente deve ir para cloud
5. Status muda para "Sincronizado"

### Seu Checklist Final

- [ ] Login funciona
- [ ] Registro de incidente é rápido (< 2 min)
- [ ] Foto anexa sem problema
- [ ] App funciona offline
- [ ] Dados sincronizam quando online
- [ ] Dashboard mostra dados relevantes
- [ ] Relatórios geram rápido
- [ ] Sem crashes ao usar
- [ ] Interface é intuitiva
- [ ] Predição de risco aparece

**RESULTADO:** Pronto para uso em campo ✓

---

## 6️⃣ **AUDITORIA INTERNA & EXTERNA SÊNIOR**

### Seu Papel
Verificar conformidade, segurança, isolamento e compliance.

### O que testar no emulador

#### ✓ Isolamento de Tenant
1. Crie 2 usuários em clientes diferentes:
   - Usuário A: cliente_a@empresa.com (clientId: A)
   - Usuário B: cliente_b@empresa.com (clientId: B)

2. Usuário A:
   - Registra incidente em Loja X
   - Vê apenas seus dados

3. Usuário B:
   - Registra incidente em Loja Y
   - Vê apenas seus dados

4. **Verificar SQL:**
   ```bash
   # Listar dados de ambos clientes
   sqlite3 app_database.db "SELECT clientId, COUNT(*) FROM ocorrencias GROUP BY clientId"
   
   # Usuário A:
   # SELECT * FROM ocorrencias WHERE clientId = 'A'
   # Deve retornar APENAS dados de A
   ```

#### ✓ Compliance LGPD/GDPR
1. Verificar se dados incluem:
   - `createdAt` (auditoria)
   - `updatedAt` (rastreamento)
   - `createdBy` (quem criou)
   - `lastModifiedBy` (quem alterou)

2. Direito ao esquecimento:
   - Verificar se há mecanismo para deletar dados do usuário
   - (Implementar em versão futura)

#### ✓ Logs de Auditoria
```bash
# Ver logs de sincronização
adb logcat | grep -i "sync\|firestore\|auth"

# Deve registrar:
# - Login/Logout
# - Criação de incidente
# - Modificação de incidente
# - Sincronização
# - Exportação de dados
```

#### ✓ Firestore Rules
1. Acesse Firebase Console
2. Vá para Firestore → Rules
3. Verifique regras de segurança:
   ```
   match /clients/{clientId}/ocorrencias/{document=**} {
     allow read, write: if getUserClientId(request.auth.uid) == clientId;
   }
   ```

4. Teste que não dá acesso cross-client:
   ```bash
   # Usuário A não deve conseguir ler dados de B
   # Testar via Firebase Emulator
   ```

#### ✓ Relatório de Auditoria
```
DATA: 27/01/2026
VERSÃO DO APP: 1.0
STATUS: ✓ VALIDADO

Observações:
- [ ] Isolamento multi-tenant funcionando
- [ ] Autenticação segura
- [ ] Permissões Android apropriadas
- [ ] SSL/TLS ativo
- [ ] Dados criptografados em trânsito
- [ ] Logs disponíveis para auditoria
- [ ] Compliance LGPD básico implementado
```

---

## 7️⃣ **REITOR & PROFESSOR (UNIVERSIDADE)**

### Seu Papel
Analisar como case de estudo de arquitetura SaaS escalável.

### O que estudar no código

#### ✓ Padrão MVVM
```kotlin
// View (Activity)
class RiskDetailActivity : AppCompatActivity() {
    private val viewModel: RiskViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Observer no ViewModel
        viewModel.riskDetails.observe(this) { riskItem ->
            // UI atualiza quando dados mudam
            updateUI(riskItem)
        }
    }
}

// ViewModel
@HiltViewModel
class RiskViewModel @Inject constructor(
    private val repository: OcorrenciaRepository
) : ViewModel() {
    
    private val _riskDetails = MutableLiveData<Risk>()
    val riskDetails: LiveData<Risk> = _riskDetails
    
    fun loadRisk(id: String) = viewModelScope.launch {
        val risk = repository.getRiskById(id)
        _riskDetails.value = risk
    }
}

// Repository (abstrai dados)
class OcorrenciaRepository @Inject constructor(
    private val dao: OcorrenciaDao,
    private val firestore: FirebaseFirestore
) {
    suspend fun getRiskById(id: String): Risk {
        // Tenta Room primeiro (offline)
        val local = dao.getOcorrenciaById(id)
        if (local != null) return local.toRisk()
        
        // Se não achar, busca Firebase
        val remote = firestore.collection("ocorrencias")
            .document(id)
            .get()
            .await()
        
        return remote.toRisk()
    }
}
```

#### ✓ Multi-Tenant Design
```kotlin
// Toda entidade tem clientId
@Entity(tableName = "ocorrencias")
data class OcorrenciaEntity(
    @PrimaryKey val id: String,
    val clientId: String,  // ← CRÍTICO para SaaS
    val lojaId: String,
    val categoriaId: String,
    val descricao: String,
    // ... outras fields
    
    // Queries SEMPRE filtram por clientId
    @Query("SELECT * FROM ocorrencias WHERE clientId = :clientId")
    suspend fun getByClient(clientId: String): List<OcorrenciaEntity>
)
```

#### ✓ Offline-First Sync
```kotlin
// 1. Room é fonte primária
dao.insert(ocorrencia)  // Salva ANTES

// 2. Background sync
// SyncWorker.kt a cada 1 hora
// Marca registros não sincronizados
// Faz upload de arquivos
// Sincroniza com Firestore
// Marca como sincronizado

// 3. Fallback garantido
// Se network falhar, dados permanecem locais
// Próximo sync tenta novamente
```

#### ✓ Escalabilidade Horizontal
```kotlin
// Estrutura Firestore permite particionamento
clients/
  client-A/
    config/
      categories/
      operatingHours/
    ocorrencias/
      incident-1/
      incident-2/
  client-B/
    config/
    ocorrencias/

// Cada cliente tem sua "partição"
// Pode ter sua própria collection/database no futuro
// Não há limite teórico de número de clientes
```

#### ✓ Machine Learning Integrado
```kotlin
// TensorFlow Lite no device
private val interpreter: Interpreter = ...

fun predictRisk(ocorrencia: Ocorrencia): RiskPrediction {
    val input = prepareInput(ocorrencia)
    val output = FloatArray(2)  // [score, confidence]
    interpreter.run(input, output)
    
    return RiskPrediction(
        score = output[0],
        confidence = output[1],
        timestamp = System.currentTimeMillis()
    )
}

// Fallback estatístico se modelo falhar
fun predictRiskStatistical(ocorrencia: Ocorrencia): RiskPrediction {
    // Baseado em histórico do cliente
    val similar = dao.getSimilarOcorrencias(
        clientId = ocorrencia.clientId,
        categoria = ocorrencia.categoria
    )
    val avgRisk = similar.map { it.riskScore }.average()
    return RiskPrediction(score = avgRisk, confidence = 0.7)
}
```

#### ✓ Segurança em Camadas
1. **Authentication:** Firebase Auth (email, biometric)
2. **Authorization:** Firestore Rules
3. **Transport:** TLS/SSL
4. **Storage:** Encrypted SharedPreferences, SQLCipher
5. **Audit:** Logs imutáveis

#### ✓ Pontos para Dissertação/Publicação
- SaaS architecture com multi-tenancy
- Offline-first com sincronização inteligente
- ML on-device vs cloud predictions
- Repository pattern vs Clean Architecture
- Observações sobre escalabilidade

---

## 📋 CHECKLIST DE CONCLUSÃO

Após todos testarem, preencha:

```markdown
### ✅ VALIDAÇÃO FINAL

- [ ] Desenvolvedor: Arquitetura MVVM funcionando
- [ ] Dev: Injeção de dependências (Hilt) OK
- [ ] Dev: Coroutines sem deadlocks
- [ ] Cibersegurança: Autenticação funciona
- [ ] Cibersegurança: Isolamento multi-tenant validado
- [ ] Cibersegurança: Permissões Android apropriadas
- [ ] Programador: Offline-first funcionando
- [ ] Programador: SyncWorker sincroniza corretamente
- [ ] Dados: Exportação CSV sem erros
- [ ] Dados: Integridade dos dados validada
- [ ] PP: Fluxo de registro intuitivo
- [ ] PP: Dashboard mostra métricas relevantes
- [ ] PP: Predições de risco aparecem
- [ ] Auditoria: Isolamento de tenant confirmado
- [ ] Auditoria: Logs de auditoria disponíveis
- [ ] Universidade: Código segue padrões acadêmicos

**RESULTADO FINAL:** PRONTO PARA PRODUÇÃO ✓
```

---

**Última atualização:** 27 de Janeiro de 2026
**Versão:** 1.0 - Build 1

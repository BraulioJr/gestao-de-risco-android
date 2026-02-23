# 🎮 Blueprint de Risco & Gamificação: Design vs Implementação

Este documento unifica a visão de Design (Experiência) e Implementação (Engenharia), definindo claramente as fronteiras entre a visualização tática e o cálculo matemático seguro.

---

# 🎨 PARTE 1: DESIGN BLUEPRINT (Visualização & Experiência)
*Definição de como o usuário percebe, visualiza e interage com o risco.*

## 1️⃣ Visualização de Risco (Interface Tática)
O sistema deve comunicar o nível de ameaça instantaneamente através de códigos visuais semânticos (Semáforo de Risco).

- **🟢 Nível Seguro (Low/Medium):**
  - **Visual:** Bordas suaves, cores frias (Verde/Azul), ícones de escudo.
  - **Feedback:** Vibração curta e leve ao registrar.
  - **Mensagem:** "Área Segura / Monitoramento Padrão".

- **🟠 Nível Atenção (High):**
  - **Visual:** Contraste aumentado, cor Laranja/Âmbar, ícones de alerta (!).
  - **Feedback:** Vibração dupla.
  - **Mensagem:** "Atividade Suspeita / Atenção Redobrada".

- **🔴 Nível Crítico (Critical):**
  - **Visual:** Modo "Red Alert", bordas piscantes (Pulse Animation), cor Vermelha.
  - **Feedback:** Vibração longa e som de sonar/bip tático.
  - **Mensagem:** "Ameaça Iminente / Intervenção Necessária (GOE)".

## 2️⃣ Estrutura de Fases do Jogo

A jornada do operador é dividida em três fases estratégicas, cada uma focada em um conjunto de habilidades e recompensas.

### Fase I: A Vigilância (Patentes: Recruta a Sentinela)
- **Objetivo:** Dominar a coleta de dados precisa e a consciência situacional.
- **Foco Mecânico:** Precisão no registro, velocidade de resposta.
- **Missões Típicas:** "Registro de Ocorrência", "Anexo de Evidência Clara", "Validação de Alerta de IA".
- **Recompensa Principal:** XP por ações individuais bem executadas.

### Fase II: A Estratégia (Patentes: Guardião a Mestre Estrategista)
- **Objetivo:** Mudar de uma postura reativa para proativa, antecipando riscos.
- **Foco Mecânico:** Análise de padrões, investigação e colaboração.
- **Missões Típicas:** "Análise de Modus Operandi", "Coleta Testemunhal", "Compartilhar Intel na Comunidade".
- **Recompensa Principal:** Multiplicadores de XP por complexidade e bônus por conclusão de investigações.

### Fase III: O Domínio (Patente: General Lendário)
- **Objetivo:** Liderar pelo exemplo, mentorar e influenciar a estratégia de prevenção em larga escala.
- **Foco Mecânico:** Resolução de casos críticos, uso de ferramentas avançadas e contribuição para a IA.
- **Missões Típicas:** "Operação Black (Nível Crítico)", "Treinar Modelo de IA com Novos Padrões", "Liderar no Conselho de Guerra".
- **Recompensa Principal:** Honra, itens de arsenal exclusivos e acesso a features beta.

## 3️⃣ Loop de Gameplay (O Ciclo de Engajamento)

O core loop que mantém o operador engajado segue um ciclo de 4 etapas:

1.  **OBSERVAR:** O operador monitora o ambiente através do **Dashboard** e recebe **Alertas da IA**.
2.  **AGIR:** Uma ocorrência é detectada. O operador inicia uma **Investigação**, que consiste em completar **Missões** (coletar evidências, analisar MO).
3.  **ANALISAR:** A ação é concluída. O sistema calcula o **XP ganho** usando a fórmula de pontuação, exibindo um feedback claro dos bônus e multiplicadores aplicados.
4.  **EVOLUIR:** O XP acumulado leva à **Progressão de Patente**, desbloqueando novas habilidades, missões e ferramentas no **Arsenal**, reiniciando o ciclo com mais capacidades.

---

# ⚙️ PARTE 2: IMPLEMENTATION BLUEPRINT (Matemática & Segurança)
*Definição de como o sistema processa, calcula e protege os dados nos bastidores.*

## 1️⃣ Cálculo Matemático de Risco (O Cérebro)
O risco não é uma opinião, é um vetor calculado.

$$ Risco_{score} = (Probabilidade \times Impacto) + (Histórico_{local} \times 0.5) + (Valor_{financeiro} \times 0.3) $$

- **Algoritmo:** `ComplexityCalculator.kt`
- **Inputs:** Valor estimado, Modus Operandi (peso), Horário (hotspot), Histórico.
- **Output:** Enum `ComplexityLevel` que alimenta a UI.

## 2️⃣ Persistência Segura (O Cofre)
Como a informação sensível é salva no dispositivo (Mobile Security).

- **Banco de Dados:** Room (`AppDatabase`) protegido.
- **Isolamento:** Campo `tenantId` obrigatório em todas as queries para garantir SaaS multi-tenant.
- **Integridade:** Hash SHA-256 gerado para cada registro de alto risco (`TacticalUtils.gerarHashIntegridade`).
- **Evidências:** Imagens movidas para armazenamento interno privado (`EvidenceVault`), inacessível à galeria pública.

## 3️⃣ Sistema de Cálculo de XP (Engine)

A fórmula de gamificação implementada no `GamificationEngine.kt`:

$$ XP_{final} = (\text{XP}_{\text{base}} + \sum \text{XP}_{\text{missões}}) \times M_{\text{precisão}} \times M_{\text{complexidade}} \times F_{\text{decaimento}} + B_{\text{streak}} $$

### Variáveis:
- **$\text{XP}_{\text{base}}$**: Valor fixo da ação principal (ex: Abrir Caso = 25 XP).
- **$\sum \text{XP}_{\text{missões}}$**: Soma do XP de todas as evidências coletadas no caso.
- **$M_{\text{precisão}}$**: Multiplicador por qualidade do relatório (1.0x a 1.5x).
- **$M_{\text{complexidade}}$**: Multiplicador do nível de ameaça, **calculado pela IA** (`ComplexityCalculator`).
- **$F_{\text{decaimento}}$**: Fator do "Protocolo de Foco Tático" para evitar spam de XP.
- **$B_{\text{streak}}$**: Bônus fixo por dias consecutivos de atividade.

### Exemplo Prático de Cálculo

Vamos simular uma ocorrência real para validar a fórmula:

**Cenário:**
- **Ação:** Registro de Ocorrência (25 XP)
- **Missões Cumpridas:** Foto Evidência (50 XP) + Depoimento (60 XP) = 110 XP
- **Qualidade:** Relatório Completo (Precisão 1.5x)
- **Contexto:** Crime Organizado (Complexidade Nível 3 - 2.0x)
- **Fadiga:** 1ª do dia (Decaimento 1.0x)
- **Sequência:** 5 dias ativos (+50 XP)

**Cálculo:**
$$ XP_{final} = ((25 + 110) \times 1.5 \times 2.0 \times 1.0) + 50 = \mathbf{455 XP} $$

## 4️⃣ Tabela de Progressão (Configuração)

A progressão agora desbloqueia mecânicas de jogo, tornando-a mais significativa.

| Nível | Patente | XP Acumulado | Desbloqueio Estratégico |
|:---:|:---|:---:|:---|
| 1 | **Recruta de Risco** | 0 | Acesso ao registro básico. |
| 5 | **Sentinela Vigilante** | 11,000 | Desbloqueia **Missões de Investigação**. |
| 10 | **Guardião Tático** | 31,000 | Desbloqueia a **Rede de Inteligência** (Comunidade). |
| 20 | **Mestre Estrategista**| 89,000 | Desbloqueia **Análise de Modus Operandi** avançada. |
| 50 | **General Lendário** | 353,000| Acesso ao **Conselho de Guerra** (features beta). |

## 5️⃣ Controle de Inflação (Algoritmo Anti-Farm)

### Mecânica Psicológica (Por que funciona?)
O protocolo não pune o esforço, ele **guia o foco**. Ao reduzir suavemente a recompensa para tarefas repetitivas de baixo valor, o sistema sinaliza ao operador que, para maximizar seu avanço, ele deve procurar desafios maiores. Isso cria um desejo intrínseco de evoluir de "vigia" para "detetive".

### Implementação Técnica
- A função `applyDiminishingReturns` no `ComplexityCalculator.kt` já implementa essa lógica.
- **Regra Chave:** A redução de XP **NUNCA** se aplica a casos de complexidade `HIGH` ou `CRITICAL`.
- **Piso Mínimo:** O operador sempre receberá no mínimo 25% do XP, garantindo que todo esforço seja recompensado.

## 6️⃣ Modelo de Dados (`GamificationEngine.kt`)

Para centralizar a lógica e facilitar a integração com o `ViewModel`, criaremos a classe `GamificationEngine`.

### Responsabilidades:
- Manter o estado do jogador (XP atual, patente, streak).
- Expor o estado via `StateFlow` para ser observado pela UI.
- Conter a função principal `calculateXpForAction(...)` que usa o `ComplexityCalculator`.
- Retornar um objeto de resultado detalhado (`XpCalculationResult`) para a UI exibir o breakdown do score.

### Exemplo de Estrutura (a ser criada):
```kotlin
// No pacote gamification

data class PlayerProfile(
    val currentXp: Long,
    val currentRank: Rank,
    val dailyStreak: Int
)

data class XpCalculationResult(
    val finalXp: Int,
    val basePoints: Int,
    val missionPoints: Int,
    val appliedMultipliers: List<String>
)

class GamificationEngine @Inject constructor(
    private val complexityCalculator: ComplexityCalculator,
    private val userRepository: UserRepository
) {
    private val _playerProfile = MutableStateFlow<PlayerProfile?>(null)
    val playerProfile: StateFlow<PlayerProfile?> = _playerProfile

    fun calculateXpForCase(case: InvestigationCase, dailyCount: Int): XpCalculationResult {
        // ... implementa a fórmula completa ...
    }
}
```
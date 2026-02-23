# 🧮 Modelo Matemático de Gamificação: O Caminho do Estrategista

Este documento define a lógica de cálculo de XP (Experiência) e progressão de patentes para o projeto Gestão de Risco. O objetivo é recompensar comportamentos de alta performance e precisão tática.

---

## 1. Fórmula Base de XP

A pontuação de cada ação é calculada pela seguinte fórmula:

$$ XP_{total} = (XP_{base} \times M_{precisao} \times M_{tatico}) + B_{streak} $$

### Variáveis:
- **$XP_{base}$**: Valor fixo da ação (ex: Registrar Ocorrência = 100 XP).
- **$M_{precisao}$**: Multiplicador de qualidade do dado (1.0x a 1.5x).
- **$M_{tatico}$**: Multiplicador de contexto (horário crítico, local de risco).
- **$B_{streak}$**: Bônus por consistência.

---

## 2. Tabela de Ações ($XP_{base}$)

| Ação | XP Base | Descrição |
| :--- | :---: | :--- |
| **Registro de Ocorrência** | 100 | Registro completo de um incidente. |
| **Prevenção Confirmada** | 150 | Registro de uma abordagem que evitou perda (recuperação). |
| **Relatório SITREP** | 50 | Envio de relatório de situação tática. |
| **Anexo de Evidência** | 20 | Adicionar foto/vídeo válido ao registro. |
| **Validação de Alerta** | 30 | Confirmar ou descartar um alerta de IA em < 5 min. |
| **Sync Completo** | 10 | Realizar sincronização de dados com sucesso. |

---

## 3. Multiplicadores

### 🎯 Multiplicador de Precisão ($M_{precisao}$)
Baseado no preenchimento dos campos opcionais e qualidade da evidência.

- **Campos Obrigatórios Apenas:** 1.0x
- **Campos Opcionais Preenchidos (>50%):** 1.2x
- **Relatório Completo (100% + Foto):** 1.5x ("Intel de Ouro")

### ⚔️ Multiplicador Tático ($M_{tatico}$)
Baseado na relevância do momento e local.

- **Horário Padrão:** 1.0x
- **Horário Crítico (Definido pela IA):** 1.3x (Ex: Sexta-feira 18h-20h)
- **Zona de Alto Risco (Hotspot):** 1.2x
- **Modo Furtivo Ativo:** 1.1x

---

## 4. Bônus de Sequência ($B_{streak}$)

Recompensa a consistência diária do operador.

- **3 Dias Consecutivos:** +50 XP
- **7 Dias Consecutivos:** +150 XP
- **30 Dias Consecutivos:** +500 XP (Medalha "Sentinela de Ferro")

---

## 5. Progressão de Patentes (Leveling)

A curva de experiência é exponencial, tornando mais difícil atingir patentes altas.

$$ XP_{necessario} = 1000 \times (Nivel)^{1.5} $$

| Nível | Patente | XP Acumulado | Benefício |
| :---: | :--- | :---: | :--- |
| 1 | **Recruta de Risco** | 0 | Acesso Básico |
| 5 | **Sentinela Vigilante** | 11,000 | Tema "Dark Ops" |
| 10 | **Guardião Tático** | 31,000 | Relatórios Avançados |
| 20 | **Mestre Estrategista** | 89,000 | Avatar Personalizado |
| 50 | **General Lendário** | 353,000 | Acesso ao "Conselho de Guerra" (Beta Features) |

---

## 6. Penalidades (Honra)

Não removemos XP, mas reduzimos a "Honra" (score secundário visível no perfil).

- **Relatório Rejeitado (Inconsistente):** -50 Honra
- **Falso Positivo (Alerta Incorreto):** -20 Honra
- **Atraso no Sync (> 3 dias):** -10 Honra

---

## 7. Estrutura de Investigação (Progressão por Caso)

A resolução de casos complexos é tratada como uma "Operação", dividida em missões menores.

### Fórmula de XP do Caso
$$ XP_{caso} = (\sum XP_{missoes}) + (B_{conclusao} \times M_{complexidade}) $$

### Tipos de Missão (Coleta de Evidências)
Cada etapa da investigação é uma missão que gera XP imediato:

| Missão | XP | Requisito |
| :--- | :---: | :--- |
| **Intel Visual** | 50 | Anexar foto clara do suspeito/local. |
| **Rastreio de Rota** | 40 | Mapear a movimentação do suspeito na loja. |
| **Coleta Testemunhal** | 60 | Registrar depoimento de funcionário/testemunha. |
| **Preservação de Prova** | 80 | Mover evidência para o Cofre Seguro (Evidence Vault). |
| **Análise de Modus Operandi** | 70 | Classificar corretamente a técnica utilizada. |

### Complexidade do Caso ($M_{complexidade}$)
Casos são classificados pela IA ou pelo Coordenador, multiplicando o bônus final.

- **Nível 1 (Simples):** 1.0x (Furto de oportunidade, item único)
- **Nível 2 (Intermediário):** 1.5x (Ocultação elaborada, valor médio)
- **Nível 3 (Complexo):** 2.0x (Quadrilha, fraude interna, alto valor)
- **Nível 4 (Crítico):** 3.0x (Ameaça à vida, valores extremos - "Operação Black")

### Barra de Progresso do Caso
O caso só pode ser encerrado (Status: Resolvido) quando a barra de progresso atinge 100%.
- Cada evidência adiciona +15% a +25% de progresso.
- A identificação do suspeito adiciona +30%.

---

## 8. Protocolo de Foco Tático (Controle de Inflação)

Para manter a economia de XP saudável e recompensar a **Visão Estratégica** em vez do volume bruto, aplicamos um **Decaimento Logarítmico** para ações repetitivas de baixo valor.

### Princípio da Precisão Cirúrgica
O XP base sofre redução conforme o volume diário de ocorrências simples aumenta, forçando o operador a focar em casos de maior complexidade para manter a pontuação alta.

$$ XP_{ajustado} = XP_{base} \times \text{FatorDecaimento}(n) $$

- **1ª a 5ª Ocorrência:** 100% XP
- **6ª a 10ª Ocorrência:** 75% XP
- **11ª+ Ocorrência:** 50% XP (Salvo se Complexidade > Nível 3)

---

## 9. Algoritmo de Complexidade (IA Heurística)

A complexidade não é subjetiva. Ela é calculada por um algoritmo de pontuação vetorial ($S_{vetor}$) que considera 5 dimensões:

1.  **Valor Financeiro ($V$):** Logarítmico base 10 do valor recuperado.
2.  **Modus Operandi ($MO$):** Peso específico da técnica (ex: Sacola revestida = peso 3.0).
3.  **Histórico ($H$):** Reincidência do suspeito ou local.
4.  **Contexto Temporal ($T$):** Horário de pico ou troca de turno.
5.  **Articulação ($A$):** Número de envolvidos.

$$ Score_{complexidade} = (V \times 0.3) + (MO \times 0.4) + (H \times 0.2) + (A \times 0.1) $$

- **Score < 2.0:** Nível 1 (Simples)
- **Score 2.0 - 4.0:** Nível 2 (Intermediário)
- **Score 4.0 - 6.0:** Nível 3 (Complexo)
- **Score > 6.0:** Nível 4 (Crítico / Crime Organizado)
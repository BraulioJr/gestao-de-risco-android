# 🗺️ Roadmap Técnico & Estratégico (12 Meses)

Este documento detalha o plano de execução para evoluir o **Gestão de Risco** de um MVP funcional para uma plataforma SaaS Enterprise líder de mercado.

---

## 📅 Q1: Fundação e Estabilização (Meses 1-3)
**Foco:** Qualidade, Segurança e Primeiros Pilotos (Alpha).

### 🛠️ Engenharia & Arquitetura
- [ ] **Refatoração Hilt:** Migração completa de injeção de dependência manual para Hilt em todos os módulos.
- [ ] **Cobertura de Testes:** Atingir 80% de cobertura em testes unitários (JUnit) e instrumentados (Espresso) nas camadas críticas (Repository/ViewModel).
- [ ] **CI/CD Pipeline:** Implementar GitHub Actions para build automático, linting e execução de testes a cada PR.
- [ ] **Monitoramento:** Integração completa com Firebase Crashlytics e Performance Monitoring.

### 🧠 IA & Dados
- [ ] **Coleta de Dados (Cold Start):** Implementar telemetria anônima para coletar padrões de uso e incidentes reais para calibração do modelo.
- [ ] **Validação do Modelo v1:** Teste A/B do modelo atual (`risk_model.tflite`) versus heurísticas simples em lojas piloto.

### 🎲 Gamificação
- [ ] **Sistema de Patentes:** Implementar visualização de progresso e cálculo de XP no backend.
- [ ] **Feedback Loop:** Ajustar valores de XP com base no comportamento real dos usuários (balanceamento).

---

## 📅 Q2: Inteligência Preditiva & Dados (Meses 4-6)
**Foco:** Provar o ROI através da redução de perdas com IA.

### 🛠️ Engenharia & Arquitetura
- [ ] **Otimização Offline:** Refinar a estratégia de sincronização do `WorkManager` para reduzir consumo de bateria e dados em 30%.
- [ ] **Segurança:** Auditoria de segurança externa (Pentest) no App e Firebase Rules.
- [ ] **Módulo de Mapa:** Implementar clusterização avançada no Google Maps SDK para visualização de hotspots regionais.

### 🧠 IA & Dados
- [ ] **Modelo v2 (Treinado):** Treinar novo modelo TensorFlow Lite com dados coletados no Q1.
- [ ] **Pipeline de Treinamento:** Automatizar o pipeline de retreinamento (MLOps) usando Google Cloud Vertex AI.
- [ ] **Análise de Sentimento:** Processar descrições de texto dos incidentes para extrair tendências de *modus operandi*.

### 💼 SaaS & Negócio
- [ ] **Onboarding Automatizado:** Fluxo de cadastro de novos tenants (clientes) sem intervenção manual.
- [ ] **Relatórios Executivos:** Geração automática de PDFs mensais com ROI calculado para diretores.

---

## 📅 Q3: Expansão SaaS & Web Portal (Meses 7-9)
**Foco:** Escalar vendas e oferecer gestão centralizada.

### 🛠️ Engenharia & Arquitetura
- [ ] **Portal Web (Gestão):** Desenvolvimento do painel administrativo em React/Next.js para gerentes regionais e diretores.
- [ ] **API Pública (v1):** Lançamento de API RESTful para integração com ERPs de varejo (SAP, TOTVS).
- [ ] **Billing Integration:** Integração com gateway de pagamento (Stripe/Iugu) para gestão de assinaturas SaaS.

### 🎲 Gamificação (Expansão)
- [ ] **Conselho de Guerra:** Lançamento dos placares de líderes (Leaderboards) globais e por rede.
- [ ] **Rede de Inteligência:** Funcionalidade social para compartilhamento de táticas entre empresas (anonimizado).
- [ ] **Arsenal (Loja):** Sistema de troca de XP por itens cosméticos no app (temas, ícones).

---

## 📅 Q4: Escala Enterprise & Ecossistema (Meses 10-12)
**Foco:** Preparação para Série A e grandes contas (Enterprise).

### 🛠️ Engenharia & Arquitetura
- [ ] **Microserviços:** Migração de Cloud Functions monolíticas para arquitetura de microserviços em Cloud Run (se necessário por escala).
- [ ] **Multi-Region Support:** Preparar infraestrutura para conformidade com GDPR (Europa) e LGPD (Brasil) simultaneamente.

### 🧠 IA & Dados
- [ ] **Visão Computacional (R&D):** Prova de conceito (PoC) para integração com câmeras IP para detecção automática de comportamento suspeito.
- [ ] **Previsão de Demanda x Risco:** Cruzar dados de estoque com risco para sugerir reforço de segurança em dias de alta venda.

### 💼 SaaS & Negócio
- [ ] **White Label:** Capacidade de personalizar o app com a marca de grandes redes varejistas.
- [ ] **Certificações:** Obtenção de ISO 27001 (Segurança da Informação).

---

## 📊 Marcos de Sucesso (KPIs)

| Marco | Mês | KPI Chave |
| :--- | :--- | :--- |
| **MVP Estável** | Mês 3 | < 1% Crash Free Users |
| **IA Validada** | Mês 6 | 70% Precisão na Predição |
| **Portal Web** | Mês 9 | 100% Gestão via Web |
| **SaaS Ready** | Mês 12 | CAC < LTV/3 |

---
**Documento Vivo:** Este roadmap deve ser revisado trimestralmente pela equipe multidisciplinar.
```

### 2. Atualização do Índice de Documentação (`DOCUMENTATION_INDEX.txt`)

Adicionando o novo documento ao índice para manter a organização.

```diff
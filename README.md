# 🛡️ Gestão de Risco - Plataforma SaaS de Prevenção de Perdas Inteligente

[![Android](https://img.shields.io/badge/Android-14%2B-green?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-purple?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Firebase](https://img.shields.io/badge/Firebase-Latest-orange?logo=firebase&logoColor=white)](https://firebase.google.com/)
[![TensorFlow Lite](https://img.shields.io/badge/TensorFlow%20Lite-AI%2FML-red?logo=tensorflow&logoColor=white)](https://www.tensorflow.org/lite)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen)](https://github.com/BraulioJr/gestao-de-risco-android)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

**Plataforma inteligente para Prevenção de Perdas em varejo com IA preditiva, análise em tempo real e sincronização offline-first**

---

## 📑 Índice

1. [🏛️ Visão Executiva (Diretoria)](#-visão-executiva-estratégia-e-negócios)
2. [🎲 Gamificação (Sun Tzu)](#-gamificação-o-caminho-do-estrategista)
3. [💻 Visão Técnica (Desenvolvimento)](#-visão-técnica-arquitetura-e-engenharia)

---

## 🏛️ Visão Executiva: Estratégia e Negócios

### 🎯 O Propósito

Inspirado na máxima de Sun Tzu — _"A suprema arte da guerra é subjugar o inimigo sem lutar"_ — o projeto **Gestão de Risco** transcende a função de um simples aplicativo de coleta de dados. Ele é uma **plataforma de inteligência estratégica** desenhada para transformar a cultura de Prevenção de Perdas (PP).

### 💎 Proposta de Valor (SaaS)

Este projeto foi concebido por uma equipe multidisciplinar de elite composta por Desenvolvedores e Programadores Sêniores, Analistas de Dados, Coordenadores de PP, Auditores Internos e Externos, e Acadêmicos (Reitores e Professores) para atender a três pilares:

1.  **Predição vs. Reação:** Utilização de IA (TensorFlow Lite) para antecipar incidentes, não apenas reportá-los.
2.  **Engajamento Operacional:** Uso de Gamificação séria para motivar a ponta da operação, transformando fiscais em "Estrategistas".
3.  **Escalabilidade Comercial:** Arquitetura multi-tenant pronta para ser licenciada como SaaS para outras redes varejistas.

---

## ✨ Principais Funcionalidades

### ⚓ Funcionalidade Âncora: Previsão de Risco com IA

O diferencial competitivo do sistema. Utiliza um modelo **TensorFlow Lite** on-device para analisar padrões em tempo real e prever a probabilidade de incidentes antes que ocorram. Diferente de relatórios estáticos, nossa IA transforma dados passados em alertas futuros, permitindo uma postura proativa.

### Outros Recursos

- **Modo Offline-First:** O aplicativo é 100% funcional sem conexão com a internet. As ocorrências são salvas em um banco de dados local (Room) e sincronizadas automaticamente com o Firestore quando a rede está disponível.
- **Inteligência Visual (ML Kit):**
  - **Reconhecimento em Campo:** Identificação automática de objetos e padrões em fotos de evidências.
  - **Leitura de Documentos (OCR):** Extração instantânea de texto de notas fiscais e documentos de identidade, agilizando o registro.
- **Edge AI Adaptativa:** Modelos que aprendem com padrões locais da loja e recebem atualizações dinâmicas via Firebase, evoluindo sem necessidade de atualizar o app.
- **Dashboard Analítico Avançado:** Uma central de inteligência visual com múltiplos gráficos para análise de dados:
  - **Mancha Criminal (Bubble Chart):** Identifica "hotspots" cruzando Horário, Categoria de Produto e Frequência de furtos.
  - **Análise Econômica:** Gráficos de barras que quantificam o prejuízo financeiro por loja.
  - **Evolução Temporal:** Gráfico de linhas que mostra a tendência de perdas ao longo dos dias.
  - **Correlação de Risco:** Gráfico de dispersão para analisar a relação entre o horário e o valor dos incidentes.
- **Gestão de Casos (Case Management):** Acompanha o ciclo de vida de cada ocorrência com status (`Aberto`, `Em Investigação`, `Resolvido`), permitindo um gerenciamento ativo dos incidentes.
- **Inteligência Tática (SITREP):** Implementação do protocolo militar **SALUTE** (Tamanho, Atividade, Local, Unidade, Tempo, Equipamento) para geração instantânea de relatórios de situação. Permite que agentes em campo enviem inteligência estruturada e criptografada com um único gesto ("Gatilho Tático").
- **Visualização Tática (Drone Recon):** Interface de mapa imersiva que utiliza a API do Google Maps em modo satélite com inclinação (tilt) e overlay tático (HUD) para simular a visão de um drone de vigilância, permitindo análise de terreno e identificação de rotas de fuga.
- **Cofre de Evidências (Evidence Vault):** Fotos capturadas em modo tático são movidas automaticamente para um armazenamento interno isolado (`.secure_evidence_vault`), ficando invisíveis na galeria pública do Android.
- **Relatórios Automatizados:** Um `Worker` agendado envia relatórios semanais em formato CSV para o e-mail do Comandante de PP.
- **Cercas Virtuais (GeoFencing):** Criação de perímetros virtuais para gerar alertas automáticos quando dispositivos monitorados entram ou saem de áreas designadas, aumentando a segurança patrimonial.
- **Exportação de Dados Detalhada:** Permite a exportação de dados brutos e análises econômicas para CSV, facilitando a integração com ferramentas externas como Power BI, Tableau ou Excel.
- **Segurança Robusta:** Protege o acesso a funcionalidades sensíveis com **autenticação biométrica**. Implementa **EncryptedSharedPreferences** e **SQLCipher** (Room criptografado) para dados em repouso, gestão de chaves via **Android Keystore**, além de **Certificate Pinning** e **Play Integrity API** para blindagem contra ataques.
- **Notificações em Tempo Real:** Utiliza **Firebase Cloud Messaging (FCM)** para enviar alertas push sobre ocorrências de alto valor.

---

## 🎨 Experiência Distintiva (UX)

O Gestão de Risco não é apenas uma ferramenta de registro, é uma extensão dos sentidos da equipe de prevenção de perdas e furtos. Projetamos microinterações que reforçam a identidade tática:

- 🔴 **Pulso Crítico:** A interface emite um pulso visual vermelho sutil ao registrar riscos de alta severidade, confirmando a urgência.
- 🔊 **Assinatura Sonora:** Notificações utilizam sons de "blip" de sonar e cliques de rádio, reforçando a atmosfera de vigilância e diferenciando-se de apps comuns.
- 📡 **Carregamento Imersivo:** Animações de varredura de radar substituem os spinners tradicionais durante o processamento de dados no Modo Tático.
- 🌑 **Modo Furtivo:** Interface de baixa luminosidade (OLED black) com feedback tátil para operações discretas em ambientes hostis, acessível via `ReconActivity`.
- ⚠️ **Protocolo Pânico (Wiping Militar):** Gesto de agitar (Shake) aciona a sanitização de dados (sobrescrita binária) antes da deleção, impedindo recuperação forense, similar ao padrão DoD 5220.22-M.
- 🔐 **Integridade Enterprise:** Logs operacionais e relatórios SITREP são assinados digitalmente com hash SHA-256 para garantir auditoria e não-repúdio, além da criptografia AES-128.

---

## 🎲 Gamificação: O Caminho do Estrategista

Inspirado nos princípios de "A Arte da Guerra" de Sun Tzu, o Gestão de Risco transcende a ferramenta de trabalho para se tornar uma plataforma de engajamento e maestria. A gamificação não é um adendo, mas uma camada estratégica que recompensa a proatividade, a precisão e a inteligência.

- **Missão da Gamificação:** Transformar cada usuário em um mestre estrategista de riscos, recompensando a prevenção acima da reação.

### Funcionalidades de Gamificação

- **Perfil de Estrategista e Patentes:** Cada usuário possui um perfil que exibe sua patente (de `Recruta` a `General Estrategista`), pontos de experiência (XP), medalhas e estatísticas de desempenho. A progressão é baseada na qualidade e no impacto de suas ações no aplicativo.
- **As 13 Campanhas (Missões):** Um sistema de missões baseado nos 13 capítulos de "A Arte da Guerra". Cada capítulo apresenta desafios que treinam o usuário em uma faceta da prevenção de perdas, desde o planejamento e análise de dados (`Capítulo I: Planejamento`) até a resposta rápida a incidentes (`Capítulo VI: Pontos Fracos e Fortes`).
- **Medalhas de Honra (Conquistas):** Conquistas específicas são concedidas por feitos notáveis, como "Primeira Prevenção Bem-Sucedida" (Medalha da Vigilância), "Relatório 100% Completo" (Medalha da Precisão) ou "Resposta a Alerta de Alto Valor em Tempo Recorde" (Medalha da Celeridade).
- **Conselho de Guerra (Placares de Líderes):** Placares de líderes semanais e mensais que classificam indivíduos e equipes (lojas/regiões) com base no XP ganho, riscos mitigados e precisão dos relatórios. Fomenta uma competição saudável e o reconhecimento dos melhores desempenhos.
- **Arsenal (Customização):** Pontos de experiência podem ser usados para desbloquear itens cosméticos, como temas de interface (ex: "Camuflagem Noturna"), sons de notificação táticos (ex: "Rádio Militar") e insígnias para o perfil.
- **Rede de Inteligência (Comunidade):** Uma área onde os usuários podem compartilhar anonimamente novos _modus operandi_ e táticas de risco observadas em campo. A comunidade avalia a utilidade da inteligência, e os melhores contribuidores ganham XP e reconhecimento, criando um espaço colaborativo para "conhecer o inimigo".

### 🧠 Redesign Psicológico: Pilares do Ecossistema

Uma abordagem baseada na psicologia comportamental para alinhar incentivos individuais aos objetivos corporativos de resiliência.

> 📚 **Para aprofundamento científico e mapeamento de código, consulte: Fundamentação Teórica de Gamificação**

1.  **Propósito & Narrativa:** Alinhar as ações do usuário à missão organizacional, transformando a técnica em um propósito interno de preservação de valor.
2.  **Colaboração Coletiva:** Substituir a competição isolada por cooperação estruturada, com metas de grupo que premiam o impacto positivo na resiliência do ecossistema.
3.  **Maestria vs. Ranking:** Focar em Níveis de Maestria (competência técnica) em vez de apenas posições comparativas, valorizando a jornada de aprendizado.
4.  **Evolução Pessoal:** Reconhecer a melhoria individual e a superação de limites próprios, utilizando feedback qualitativo para guiar o desenvolvimento.
5.  **Recompensa por Impacto:** Premiar o aprendizado compartilhado e as ações que fortalecem a segurança da colaboração.

---

## 👥 Personas e Seus Benefícios

O projeto foi desenhado para atender aos rigorosos requisitos de uma equipe multidisciplinar de alta performance:

#### 👨‍💼 Coordenador de Prevenção de Perdas (PP) Sênior

- **Estratégia Operacional:** Transforma dados brutos em inteligência tática para alocação eficiente de recursos de segurança e redução de quebras.
- **Gestão de Incidentes:** Monitoramento em tempo real do ciclo de vida das ocorrências, garantindo resposta rápida e mitigação de prejuízos.
- **Conformidade Operacional:** Assegura que as abordagens e registros sigam protocolos estritos (ex: "Fundada Suspeita"), minimizando riscos legais e operacionais.

#### 📈 Analista de Dados Sênior

- **Integridade e Padronização:** Garante que os dados coletados sejam estruturados, limpos e auditáveis, fundamentais para análises estatísticas confiáveis.
- **Inteligência Preditiva:** Utiliza modelos de IA para identificar padrões ocultos (clusters de risco) e antecipar tendências, movendo a operação de reativa para proativa.
- **Visualização de Decisão:** Dashboards avançados que traduzem métricas complexas em insights visuais claros para a diretoria.

#### 💻 Desenvolvedor e Programador Sênior

- **Excelência Técnica:** Arquitetura MVVM limpa, escalável e testável, seguindo os princípios de SOLID e Clean Architecture para facilitar manutenção e evolução.
- **Robustez e Performance:** Foco em operação Offline-First, sincronização eficiente em background e otimização de recursos do dispositivo.
- **Segurança por Design:** Implementação de criptografia, autenticação forte e práticas de codificação segura (OWASP Mobile) desde a concepção.

#### 📋 Auditoria Interna e Externa Sênior

- **Rastreabilidade e Non-Repudiation:** Logs imutáveis e assinaturas digitais (SHA-256) em relatórios garantem a cadeia de custódia e integridade da informação.
- **Compliance e Governança:** O sistema força a adesão a políticas internas e regulamentações (LGPD), com controle de acesso granular e segregação de funções.
- **Validação de Processos:** Ferramentas que permitem a verificação independente da eficácia dos controles internos de segurança e a veracidade dos dados reportados.

#### 🎓 Reitor e Professor Universitário (Nacional e Internacional)

- **Inovação Acadêmica:** Um estudo de caso vivo de aplicação de teorias complexas (Teoria dos Jogos, IA, Estatística) em problemas reais de mercado.
- **Metodologia Científica:** Abordagem baseada em evidências e dados, promovendo a pesquisa e o desenvolvimento contínuo de novas táticas de prevenção.
- **Pedagogia da Gamificação:** Aplicação dos princípios de Sun Tzu não apenas como jogo, mas como ferramenta andragógica para treinamento e engajamento de equipes de alta performance.

---

## 💻 Visão Técnica: Arquitetura e Engenharia

- **Linguagem:** Kotlin
- **Arquitetura:** MVVM (ViewModel, LiveData/Flow) + Padrão Repository
- **Fonte Única da Verdade:** O banco de dados local é a fonte primária para a UI.
- **UI:** Android Views com Material Design 3
- **Programação Assíncrona:** Kotlin Coroutines e Flow
- **Banco de Dados Local:** Room (para operação offline)
- **Sincronização Remota:** Firebase Firestore (com estratégia NetworkBoundResource e controle de conflitos)
- **Tarefas em Segundo Plano:** WorkManager
- **Gráficos:** MPAndroidChart
- **Geolocalização:** Google Maps SDK, GeoFencing API (Mapbox SDK em avaliação)
- **Machine Learning:** TensorFlow Lite + Google ML Kit (Vision/OCR) + Firebase ML
- **Notificações Push:** Firebase Cloud Messaging (FCM)
- **Observabilidade:** Firebase Crashlytics, Performance Monitoring e Logging estruturado (Timber)
- **Configuração Dinâmica:** Firebase Remote Config para ativação de features (Feature Flags)
- **Segurança:** BiometricPrompt, Android Keystore, EncryptedSharedPreferences, SQLCipher, Play Integrity API, Certificate Pinning
- **Qualidade e DevOps:** Pipeline de CI/CD com GitHub Actions, testes automatizados (JUnit, Espresso, MockK) e análise estática (Detekt, Ktlint).

---

## 🚀 Configuração do Projeto

1.  **Clonar o Repositório:**

    ```bash
    git clone [URL_DO_REPOSITORIO]
    ```

2.  **Configuração do Firebase:**
    - Faça o download do seu arquivo `google-services.json` no console do Firebase.
    - Coloque o arquivo `google-services.json` no diretório `app/`.

3.  **Dependências de Machine Learning:**
    - Certifique-se de que as seguintes dependências estão no seu arquivo `app/build.gradle`:

    ```gradle
    dependencies {
        // ...
        implementation("org.tensorflow:tensorflow-lite:2.14.0")
        implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    }
    ```

4.  **Modelo de IA:**
    - Para que a funcionalidade de previsão com IA funcione, coloque o seu modelo treinado com o nome `risk_model.tflite` na pasta `app/src/main/assets/`.
    - Se o modelo não for encontrado, o aplicativo usará um fallback baseado em estatísticas, sem quebrar.

5.  **Sincronize e Execute:**
    - Abra o projeto no Android Studio, aguarde a sincronização do Gradle e execute o aplicativo.

---

## 📂 Estrutura do Projeto

```
com.example.gestaoderisco
├── analysis/         # Lógica de processamento estatístico (StatisticsAnalyzer)
├── data/
│   └── local/        # Componentes do Room DB (AppDatabase, OcorrenciaDao, Converters)
├── ml/               # Lógica de Machine Learning (RiskPredictor)
├── models/           # Data classes (Ocorrencia)
├── repository/       # Repositório central de dados (OcorrenciasRepository)
├── service/          # Serviços em segundo plano (MyFirebaseMessagingService)
├── view/             # Activities e Fragments (UI)
├── viewmodel/        # ViewModels (lógica de UI e estado)
└── worker/           # Workers para tarefas em segundo plano (SyncWorker, WeeklyReportWorker)
```

---

## 🗺️ Roadmap e Próximos Passos

- **Treinamento do Modelo de IA:** Iniciar a coleta de dados para treinar a primeira versão do modelo preditivo.
- **Mapa Geográfico:** Implementar um mapa (Google Maps SDK) para visualizar a densidade de ocorrências por localização de loja.
- **Injeção de Dependência:** Refatorar o projeto para usar **Hilt** para gerenciar dependências.
- **Arquivamento de Dados:** Criar uma rotina para arquivar ocorrências antigas (ex: status "Resolvido" há mais de 1 ano) para otimizar a performance do banco de dados local.

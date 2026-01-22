# Gestão de Risco - Plataforma de Prevenção de Perdas

O **Gestão de Risco** é um aplicativo Android avançado, projetado como uma plataforma completa de inteligência para equipes de Prevenção de Perdas (PP). Ele vai além do simples registro de ocorrências, oferecendo análises de dados em tempo real, insights preditivos com Machine Learning e ferramentas de gerenciamento de casos para otimizar a operação de segurança no varejo.

A arquitetura do projeto é focada em robustez, escalabilidade e usabilidade em campo, com um forte componente de operação offline.

---

## ✨ Principais Funcionalidades

*   **Modo Offline-First:** O aplicativo é 100% funcional sem conexão com a internet. As ocorrências são salvas em um banco de dados local (Room) e sincronizadas automaticamente com o Firestore quando a rede está disponível.
*   **Dashboard Analítico Avançado:** Uma central de inteligência visual com múltiplos gráficos para análise de dados:
    *   **Mancha Criminal (Bubble Chart):** Identifica "hotspots" cruzando Horário, Categoria de Produto e Frequência de furtos.
    *   **Análise Econômica:** Gráficos de barras que quantificam o prejuízo financeiro por loja.
    *   **Evolução Temporal:** Gráfico de linhas que mostra a tendência de perdas ao longo dos dias.
    *   **Correlação de Risco:** Gráfico de dispersão para analisar a relação entre o horário e o valor dos incidentes.
*   **Previsão de Risco com IA:** Utiliza um modelo **TensorFlow Lite** para prever a probabilidade de um incidente ocorrer em tempo real. Possui um fallback para um modelo estatístico, garantindo que a funcionalidade esteja sempre ativa.
*   **Gestão de Casos (Case Management):** Acompanha o ciclo de vida de cada ocorrência com status (`Aberto`, `Em Investigação`, `Resolvido`), permitindo um gerenciamento ativo dos incidentes.
*   **Relatórios Automatizados:** Um `Worker` agendado envia relatórios semanais em formato CSV para o e-mail do Coordenador de PP.
*   **Exportação de Dados Detalhada:** Permite a exportação de dados brutos e análises econômicas para CSV, facilitando a integração com ferramentas externas como Power BI, Tableau ou Excel.
*   **Segurança Robusta:** Protege o acesso a funcionalidades sensíveis (configurações, exportação) com **autenticação biométrica**.
*   **Notificações em Tempo Real:** Utiliza **Firebase Cloud Messaging (FCM)** para enviar alertas push sobre ocorrências de alto valor.

---

## 👥 Personas e Seus Benefícios

O projeto foi desenhado para atender às necessidades de uma equipe multidisciplinar:

#### 👨‍💼 Coordenador de Prevenção de Perdas (PP) Sênior
*   **Visão Estratégica:** Usa o dashboard para tomar decisões rápidas sobre onde e quando alocar recursos de segurança.
*   **Gestão Ativa:** Acompanha o status de cada caso, garantindo que todos os incidentes sejam investigados e resolvidos.
*   **Conformidade:** Garante a aplicação correta dos procedimentos (ex: "Fundada Suspeita") através das ferramentas do app.

#### 📈 Analista de Dados Sênior
*   **Dados de Qualidade:** Trabalha com dados padronizados e limpos, essenciais para análises precisas.
*   **Ferramentas de Exploração:** Utiliza as visualizações avançadas do dashboard e a exportação para CSV para encontrar padrões e correlações profundas.
*   **Base para IA:** Usa os dados coletados como base para treinar e aprimorar os modelos de Machine Learning.

#### 💻 Desenvolvedor e Programador Sênior
*   **Arquitetura Moderna:** Trabalha com uma base de código bem estruturada (MVVM, Repository, Single Source of Truth), facilitando a manutenção e a adição de novas funcionalidades.
*   **Código Testável:** A separação de responsabilidades permite a criação de testes unitários e instrumentados robustos.
*   **Tecnologias Atuais:** Utiliza Kotlin, Coroutines, Flow, Room e WorkManager, seguindo as melhores práticas do ecossistema Android.

#### 💰 Investidor Sênior
*   **ROI Mensurável:** Vê claramente como o aplicativo mede um problema financeiro (perdas) e fornece as ferramentas para mitigá-lo, permitindo o cálculo do retorno sobre o investimento.
*   **Vantagem Competitiva:** Reconhece o potencial da IA para transformar a operação de reativa para preditiva, posicionando a empresa como líder em inovação no setor.
*   **Potencial de Escalabilidade (SaaS):** Identifica a oportunidade de transformar o projeto em um produto de Software como Serviço (SaaS) para outras empresas do varejo.

---

## 🛠️ Tech Stack & Arquitetura

*   **Linguagem:** Kotlin
*   **Arquitetura:** MVVM (ViewModel, LiveData/Flow) + Padrão Repository
*   **Fonte Única da Verdade:** O banco de dados local é a fonte primária para a UI.
*   **UI:** Android Views com Material Design 3
*   **Programação Assíncrona:** Kotlin Coroutines e Flow
*   **Banco de Dados Local:** Room (para operação offline)
*   **Sincronização Remota:** Firebase Firestore
*   **Tarefas em Segundo Plano:** WorkManager
*   **Gráficos:** MPAndroidChart
*   **Machine Learning:** TensorFlow Lite
*   **Notificações Push:** Firebase Cloud Messaging (FCM)
*   **Segurança:** BiometricPrompt API

---

## 🚀 Configuração do Projeto

1.  **Clonar o Repositório:**
    ```bash
    git clone [URL_DO_REPOSITORIO]
    ```

2.  **Configuração do Firebase:**
    *   Faça o download do seu arquivo `google-services.json` no console do Firebase.
    *   Coloque o arquivo `google-services.json` no diretório `app/`.

3.  **Dependências de Machine Learning:**
    *   Certifique-se de que as seguintes dependências estão no seu arquivo `app/build.gradle`:
    ```gradle
    dependencies {
        // ...
        implementation("org.tensorflow:tensorflow-lite:2.14.0")
        implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    }
    ```

4.  **Modelo de IA:**
    *   Para que a funcionalidade de previsão com IA funcione, coloque o seu modelo treinado com o nome `risk_model.tflite` na pasta `app/src/main/assets/`.
    *   Se o modelo não for encontrado, o aplicativo usará um fallback baseado em estatísticas, sem quebrar.

5.  **Sincronize e Execute:**
    *   Abra o projeto no Android Studio, aguarde a sincronização do Gradle e execute o aplicativo.

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

*   **Treinamento do Modelo de IA:** Iniciar a coleta de dados para treinar a primeira versão do modelo preditivo.
*   **Mapa Geográfico:** Implementar um mapa (Google Maps SDK) para visualizar a densidade de ocorrências por localização de loja.
*   **Injeção de Dependência:** Refatorar o projeto para usar **Hilt** para gerenciar dependências.
*   **Arquivamento de Dados:** Criar uma rotina para arquivar ocorrências antigas (ex: status "Resolvido" há mais de 1 ano) para otimizar a performance do banco de dados local.

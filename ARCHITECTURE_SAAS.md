# 🏗️ Arquitetura SaaS & Escalabilidade Visual

## 1. Visão Macro do Ecossistema

O diagrama abaixo ilustra o fluxo de dados desde a ponta (Edge/Android) até a inteligência centralizada na nuvem, destacando a natureza **Offline-First** e **Multi-Tenant**.

```mermaid
graph TD
    subgraph "Edge Computing (Lojas Físicas)"
        A[📱 App Android (Fiscal)] -->|Input Tático| B(Room DB Local)
        A -->|Inferência IA| C(TensorFlow Lite)
        B <-->|Sync Manager| D{WorkManager}
    end

    subgraph "Secure Gateway & Auth"
        D -->|TLS 1.3 / Encrypted| E[🔥 Firebase Auth]
        E -->|Token Validation| F[☁️ Cloud Functions]
    end

    subgraph "Core Backend (Serverless)"
        F -->|Write/Read| G[(🔥 Firestore NoSQL)]
        F -->|Media Upload| H[(📦 Cloud Storage)]
        G -->|Trigger| I[⚡ Event Bus / PubSub]
    end

    subgraph "Data Intelligence & Analytics"
        I -->|Stream| J[BigQuery Data Warehouse]
        J -->|Training Data| K[🧠 AI Training Pipeline]
        K -->|New Model .tflite| H
        J -->|BI Dashboard| L[💻 Web Portal (Gestão)]
    end

    subgraph "Gamification Engine"
        I -->|Event: Risk Mitigated| M[🎮 Gamification Service]
        M -->|Calc XP/Rank| G
    end
```

---

## 2. Fluxo de Sincronização (Offline-First)

Detalhe técnico de como garantimos a integridade dos dados mesmo em ambientes de conectividade hostil (subsolos, áreas remotas).

```mermaid
sequenceDiagram
    participant User as 👮 Fiscal
    participant App as 📱 App Local
    participant Room as 💾 Room DB
    participant Worker as ⚙️ SyncWorker
    participant Cloud as ☁️ Firestore

    User->>App: Registra Ocorrência (Sem Internet)
    App->>Room: INSERT (isSynced = false)
    App-->>User: Feedback: "Salvo Localmente"
    
    Note over App, Worker: ... Tempo passa ...
    
    Worker->>Worker: Detecta Conexão
    Worker->>Room: Query (isSynced = false)
    Room-->>Worker: Lista de Ocorrências
    
    loop Para cada item
        Worker->>Cloud: POST /ocorrencias (Batch Write)
        Cloud-->>Worker: 200 OK + ServerTimestamp
        Worker->>Room: UPDATE (isSynced = true)
    end
    
    Cloud->>App: Push Notification (FCM) "Sincronizado"
```

---

## 3. Modelo de Dados Multi-Tenant (Isolamento)

Estrutura lógica que permite atender múltiplos clientes (Varejista A, Varejista B) na mesma infraestrutura sem vazamento de dados.

```mermaid
erDiagram
    TENANT ||--o{ USER : employs
    TENANT ||--o{ STORE : owns
    TENANT {
        string tenantId PK "UUID"
        string planType "Basic/Pro/Enterprise"
        json config "Custom Rules"
    }
    USER ||--o{ RISK_EVENT : reports
    USER {
        string userId PK
        string tenantId FK
        string role "Fiscal/Manager"
        int currentXp
        string rank
    }
    RISK_EVENT {
        string eventId PK
        string tenantId FK
        string storeId FK
        float value
        float ai_probability
    }
```

---
**Documento Técnico Confidencial - Uso Interno e Investidores**
# Gestão de Risco - App Android

## 📝 Descrição

Este é um aplicativo Android nativo para gestão e análise de riscos. Ele permite que usuários (como fiscais ou gestores) registrem, monitorem e visualizem riscos associados às suas operações, com o objetivo de mitigar perdas e melhorar a tomada de decisão.

## ✨ Funcionalidades

- **Autenticação de Usuários:** Sistema de Login e Registro seguro utilizando Firebase Authentication.
- **CRUD de Riscos:**
    - Criação, leitura, atualização e exclusão de riscos.
    - Formulário detalhado com nome, descrição, nível de impacto e probabilidade.
- **Anexo de Arquivos:** Possibilidade de anexar imagens (evidências) a cada risco, com upload para o Firebase Storage.
- **Lista de Riscos Inteligente:**
    - Exibição da lista de riscos por usuário.
    - **Busca** em tempo real pelo nome do risco.
    - **Ordenação** por nome, data de criação ou ID.
- **Dashboard Analítico:**
    - Visualização do número total de riscos.
    - Gráfico de pizza com a distribuição de riscos por nível de impacto (Alto, Médio, Baixo).
- **Segurança:** Regras de segurança no Firestore para garantir que cada usuário só possa acessar seus próprios dados.

## 🚀 Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Componentes de Arquitetura Android:**
    - ViewModel e StateFlow para comunicação reativa com a UI.
    - ViewBinding para acesso seguro às views.
- **Backend (Firebase):**
    - **Firebase Authentication:** Para gerenciamento de usuários.
    - **Cloud Firestore:** Como banco de dados NoSQL em tempo real.
    - **Firebase Storage:** Para armazenamento de imagens e anexos.
- **Assincronismo:** Kotlin Coroutines.
- **UI & Gráficos:**
    - Material Design 3.
    - MPAndroidChart para o gráfico de pizza no Dashboard.
    - Glide para carregamento de imagens.

## ⚙️ Como Executar

1.  Clone este repositório.
2.  Abra o projeto no Android Studio.
3.  Conecte o projeto a um projeto Firebase que você tenha criado.
    - Baixe seu próprio arquivo `google-services.json` e coloque na pasta `app/`.
    - Habilite **Authentication (Email/Password)**, **Cloud Firestore** e **Storage** no console do Firebase.
4.  Execute o aplicativo em um emulador ou dispositivo físico.


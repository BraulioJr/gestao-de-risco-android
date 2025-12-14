# Gestão de Risco - Aplicativo Android Nativo

![Kotlin](https://img.shields.io/badge/language-Kotlin-blue.svg)
![Platform](https://img.shields.io/badge/platform-Android-brightgreen.svg)
![Firebase](https://img.shields.io/badge/backend-Firebase-orange.svg)

## 📝 Visão Geral

O **Gestão de Risco** é um aplicativo Android nativo, construído com as mais recentes tecnologias e melhores práticas, projetado para oferecer uma solução robusta e segura para o gerenciamento de riscos operacionais. A plataforma permite que usuários, como gestores e fiscais, identifiquem, documentem, monitorem e analisem riscos de forma eficiente, com foco especial na conformidade com a Lei Geral de Proteção de Dados (LGPD).

## ✨ Principais Funcionalidades

### Core & CRUD
- **Autenticação Segura:** Sistema completo de login e registro com Firebase Authentication.
- **Gerenciamento de Riscos (CRUD):** Funcionalidades completas para Criar, Ler, Atualizar e Excluir riscos.
- **Sincronização em Tempo Real:** A interface do usuário é atualizada instantaneamente com as mudanças no banco de dados, graças ao Cloud Firestore.

### UX & Gerenciamento de Dados
- **Busca Dinâmica:** Filtro em tempo real para encontrar riscos pelo nome.
- **Ordenação Avançada:** Classifique a lista de riscos por data, nível de impacto ou probabilidade (ascendente e descendente).
- **Interface Moderna:** UI limpa e intuitiva, construída com componentes do Material Design 3.

### Conformidade e Segurança (LGPD)
- **Privacy by Design:** O formulário inclui campos dedicados para documentar riscos que afetam dados pessoais, alinhado com os princípios da LGPD.
- **Controle do Titular:** Garante os direitos do usuário de acessar, corrigir e excluir seus próprios dados.
- **Isolamento de Dados:** Regras de segurança robustas no Firestore garantem que um usuário só possa acessar e manipular os riscos que ele mesmo criou.

## 🛠️ Arquitetura e Tecnologias

O projeto foi desenvolvido seguindo as diretrizes de arquitetura recomendadas pelo Google, garantindo um código escalável, testável e de fácil manutenção.

- **Linguagem:** **Kotlin**
- **Arquitetura:** **MVVM** (Model-View-ViewModel)
- **Assincronismo:** **Kotlin Coroutines** e **Flow** para um gerenciamento de dados reativo e eficiente.
- **Componentes de Arquitetura:**
    - **ViewModel:** Gerencia o estado da UI e a lógica de negócio.
    - **StateFlow:** Expõe o estado da UI de forma reativa e segura para o ciclo de vida.
    - **ViewBinding:** Acesso seguro e type-safe às views do layout.
- **Backend (Firebase Suite):**
    - **Firebase Authentication:** Para autenticação de usuários.
    - **Cloud Firestore:** Como banco de dados NoSQL em tempo real.
    - **Firebase Storage:** Para armazenamento de arquivos e anexos (funcionalidade futura).
- **Interface do Usuário (UI):**
    - **Material Design 3:** Para um design moderno e consistente.
    - **RecyclerView com `ListAdapter`:** Para uma exibição de listas performática e com animações automáticas.

## ⚙️ Configuração e Execução

1.  **Clonar o Repositório:**
    ```sh
    git clone https://github.com/seu-usuario/Project_GestaoDeRisco.git
    ```
2.  **Abrir no Android Studio:**
    - Abra o Android Studio e selecione `Open an existing project`.
    - Navegue até a pasta do projeto clonado e abra-o.
3.  **Conectar ao Firebase:**
    - Crie um novo projeto no Firebase Console.
    - No console, adicione um novo aplicativo Android com o `package name` `com.example.project_gestoderisco`.
    - Baixe o arquivo `google-services.json` gerado e coloque-o dentro da pasta `app/` do seu projeto no Android Studio.
    - No Firebase Console, habilite os seguintes serviços:
        - **Authentication:** Ative o provedor de **E-mail/senha**.
        - **Firestore Database:** Crie um novo banco de dados.
        - **Storage:** Crie um novo bucket de armazenamento.
4.  **Executar o Aplicativo:**
    - Com um emulador em execução ou um dispositivo físico conectado, clique no botão "Run 'app'" no Android Studio.

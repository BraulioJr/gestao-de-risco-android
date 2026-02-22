# 📜 Estratégia de Versionamento e Release - Gestão de Risco

Este documento descreve o fluxo de trabalho profissional para versionar, construir e lançar novas versões do aplicativo Gestão de Risco.

---

## 1. Versionamento Semântico (SemVer)

Adotamos o padrão **Versionamento Semântico 2.0.0**. O formato é `MAJOR.MINOR.PATCH`:

- **MAJOR**: Incrementado para mudanças incompatíveis de API (quebram a compatibilidade).
- **MINOR**: Incrementado para novas funcionalidades adicionadas de forma retrocompatível.
- **PATCH**: Incrementado para correções de bugs retrocompatíveis.

**Exemplo:** `v1.2.5`

## 2. Versionamento no Android (`versionCode` e `versionName`)

### `versionName` (Ex: "1.2.5")
- É a string de versão exibida para os usuários na Google Play Store.
- **Deve ser atualizada manualmente** no arquivo `app/build.gradle.kts` antes de cada novo lançamento.
- Segue o padrão SemVer.

### `versionCode` (Ex: 152)
- É um número inteiro, único e incremental usado internamente pelo Android e pela Play Store para identificar novas versões.
- **Este valor é gerado automaticamente**. Ele corresponde ao número total de commits no histórico do Git, garantindo que seja sempre único e crescente.

---

## 3. Fluxo de Git para Release (Git Flow Simplificado)

O processo para lançar uma nova versão é o seguinte:

1.  **Desenvolvimento Contínuo:**
    - Novas funcionalidades são desenvolvidas em branches `feature/*` (ex: `feature/dashboard-v2`).
    - Essas branches são mescladas na branch `main` (ou `develop`) após a conclusão.

2.  **Preparação para o Lançamento:**
    - Quando um conjunto de funcionalidades está pronto para ser lançado, certifique-se de que a branch `main` está estável.
    - **Atualize o `versionName`** no arquivo `app/build.gradle.kts` para a nova versão (ex: de `"1.2.4"` para `"1.3.0"`).
    - Faça o commit desta alteração: `git commit -m "chore: Bump version to 1.3.0"`.

3.  **Criação da Tag de Release:**
    - Crie uma tag Git **anotada** na branch `main` com o prefixo `v`.
    ```bash
    git tag -a v1.3.0 -m "Release v1.3.0: Novas funcionalidades do dashboard e melhorias de performance."
    ```

4.  **Publicação da Tag:**
    - Envie a tag para o repositório remoto (GitHub). Isso acionará nosso pipeline de CI/CD.
    ```bash
    git push origin v1.3.0
    ```

---

## 4. Automação com CI/CD (GitHub Actions)

Ao enviar uma nova tag no formato `v*.*.*` para o GitHub, um fluxo de trabalho automatizado (`.github/workflows/android-release.yml`) será executado.

### O que o Pipeline Faz:
1.  **Checkout do Código:** Baixa o código correspondente à tag.
2.  **Configura o Ambiente:** Instala o Java (JDK 17).
3.  **Configura a Assinatura:** Cria o arquivo de keystore e as propriedades de assinatura a partir dos *Secrets* configurados no repositório do GitHub.
4.  **Compila o App:** Executa `./gradlew assembleRelease`. O `versionCode` é calculado automaticamente com base no número de commits.
5.  **Publica na Google Play:** Faz o upload do APK assinado para a faixa `internal` (ou outra configurada) da Google Play Store.
6.  **Cria uma Release no GitHub:** Cria uma nova release na página de "Releases" do GitHub e anexa o APK gerado.

---

## 5. Configuração Necessária (Ação Requerida)

Para que o pipeline de CI/CD funcione, você precisa configurar os seguintes **Secrets** no seu repositório do GitHub (`Settings` > `Secrets and variables` > `Actions`):

### Secrets Obrigatórios:
- **`PLAY_STORE_SERVICE_ACCOUNT_JSON`**: O conteúdo do arquivo JSON da sua conta de serviço do Google Play Console.
- **`SIGNING_KEY_BASE64`**: Seu arquivo de keystore (`.jks`) de produção, codificado em Base64.
  - Para gerar, use o comando: `cat my-release-key.jks | base64`
- **`SIGNING_KEY_ALIAS`**: O alias da sua chave de assinatura.
- **`SIGNING_KEY_PASSWORD`**: A senha do seu arquivo de keystore.
- **`SIGNING_KEY_ALIAS_PASSWORD`**: A senha do seu alias de chave.

Com esta estrutura, o processo de release se torna robusto, rastreável e automatizado, minimizando erros manuais.
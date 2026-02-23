# Guia de Contribuição - Gestão de Risco

Este projeto segue padrões estritos de engenharia de software para garantir escalabilidade e manutenibilidade.

---

## 📜 Padrão de Commits (Conventional Commits)

Utilizamos a especificação **Conventional Commits 1.0.0** para mensagens de commit. Isso permite a geração automática de Changelogs e versionamento semântico.

### Estrutura da Mensagem

```text
<tipo>(<escopo opcional>): <descrição curta>

[corpo opcional: motivação da mudança]

[rodapé opcional: breaking changes ou referências a issues]
```

### Tipos Permitidos (`<tipo>`)

| Tipo         | Descrição                                                    | Exemplo                                     |
| :----------- | :----------------------------------------------------------- | :------------------------------------------ |
| **feat**     | Nova funcionalidade para o usuário (minor version bump).     | `feat(auth): add biometric login`           |
| **fix**      | Correção de bug para o usuário (patch version bump).         | `fix(graph): resolve crash on empty data`   |
| **docs**     | Alterações apenas na documentação.                           | `docs(readme): update architecture diagram` |
| **style**    | Formatação, ponto e vírgula, lint (sem alteração de lógica). | `style(ui): format xml layouts`             |
| **refactor** | Refatoração de código (sem fix ou feat).                     | `refactor(repo): migrate to flow`           |
| **perf**     | Melhoria de performance.                                     | `perf(map): optimize cluster rendering`     |
| **test**     | Adição ou correção de testes.                                | `test(unit): add gamification engine tests` |
| **build**    | Alterações no sistema de build ou dependências.              | `build(gradle): upgrade kotlin to 1.9.20`   |
| **ci**       | Alterações em arquivos de CI (GitHub Actions, etc).          | `ci(workflow): add detekt check`            |
| **chore**    | Tarefas utilitárias, manutenção de build, etc.               | `chore: update .gitignore`                  |

### Escopos Comuns (`<escopo>`)

- `auth`, `ui`, `data`, `core`, `gamification`, `roi`, `worker`, `map`

### 🛡️ Validação Automática (Git Hook)

Para garantir que todos os commits sigam este padrão, configure o git hook localmente:

1. Copie o script de validação:
   `cp config/git-hooks/commit-msg .git/hooks/commit-msg`
2. Dê permissão de execução (Linux/Mac/Git Bash):
   `chmod +x .git/hooks/commit-msg`

---

## 🌿 Fluxo de Trabalho (Git Flow Simplificado)

1. **Main:** Código de produção (estável).
2. **Develop:** Branch de integração.
3. **Feature Branches:** `feat/nome-da-feature` (ex: `feat/roi-calculator`).
4. **Fix Branches:** `fix/nome-do-bug`.

### Exemplo de Commit Completo

```bash
git commit -m "feat(gamification): implement xp decay algorithm

Adds a logarithmic decay factor to XP gain to prevent farming on low-value tasks.
Implements the 'Tactical Focus Protocol' logic.

Closes #123"
```

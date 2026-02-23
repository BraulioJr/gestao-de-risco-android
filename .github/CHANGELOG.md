# Changelog

Todas as mudanças notáveis neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
e este projeto adere ao [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **roi**: Implementação da engine avançada de ROI com slider de eficiência e métricas preditivas.
- **gamification**: Sistema de XP com preview em tempo real, dialog de feedback e sons de recompensa.
- **ui**: Novo layout para calculadora de ROI e componentes de feedback visual.
- **docs**: Blueprint de Negócios (Business Value) integrado à documentação de gamificação.

### Changed

- **docs**: Reestruturação completa dos Blueprints separando Design (UX) de Implementação (Engenharia).
- **config**: Adoção oficial do padrão Conventional Commits e Git Hooks.

## [1.0.0] - 2026-01-27

### Added

- **core**: Arquitetura MVVM + Repository + Clean Architecture inicial.
- **auth**: Integração com Firebase Auth (Email + Biometria).
- **data**: Room Database com suporte a multi-tenant (SaaS) e isolamento de dados.
- **sync**: WorkManager para sincronização offline-first com estratégia NetworkBoundResource.
- **ml**: Integração TensorFlow Lite para predição de risco em tempo real.
- **ui**: Dashboard analítico com gráficos MPAndroidChart e Material Design 3.
- **security**: Implementação de Evidence Vault, Panic Protocol e Criptografia AES-128.

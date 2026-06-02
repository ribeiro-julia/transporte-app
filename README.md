# 🚛 Sistema de Gerenciamento de Transporte

Sistema desktop para gerenciamento financeiro de motoristas de transporte escolar. Desenvolvido em Java com interface gráfica moderna e banco de dados embarcado. Confira a licença antes da sua utilização.

![Java](https://img.shields.io/badge/Java-21-blue.svg)
![JavaFX](https://img.shields.io/badge/JavaFX-21-green.svg)
![SQLite](https://img.shields.io/badge/SQLite-3.36-blue.svg)

## 📋 Descrição

O **Sistema de Gerenciamento de Transporte** é uma aplicação desktop que permite:
- Cadastrar e gerenciar motoristas
- Criar e acompanhar contratos
- Controlar receitas e despesas
- Gerar relatórios financeiros simples
- Visualizar gráficos comparativos

### Pré-requisitos

1. **Java 17, 21 ou superior** instalado
2. **JavaFX SDK 21** instalado
3. **SQLite JDBC** (já incluso na pasta `lib/`)

## ✨ Funcionalidades

### 👨‍✈️ Motoristas
- Cadastro completo (CNH, nome, telefone, email)
- Edição e remoção de registros
- Busca por nome (pesquisa parcial)
- Listagem em tabela organizada

### 📄 Contratos
- Cadastro com número, descrição e valor
- Período de validade (data início/fim)
- Associação a motoristas
- Remoção em cascata

### 💰 Transações
- Lançamento de receitas e despesas
- Associação opcional a contratos
- Histórico ordenado por data
- Tipos: INCOME (receita) e EXPENSE (despesa)

### 📊 Relatórios
- **Filtros disponíveis:**
  - Mês/Ano específico
  - Ano inteiro
  - Período personalizado (duas datas)
- **Informações geradas:**
  - Lista detalhada de transações
  - Total de receitas
  - Total de despesas
  - Saldo final
  - Situação financeira (positivo/negativo/neutro)
- **Gráfico de barras** comparativo

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Versão | Finalidade |
|------------|--------|------------|
| Java | 17, 21 ou superior | Linguagem base |
| JavaFX | 21.0.11 | Interface gráfica |
| SQLite | 3.36.0.3 | Banco de dados embarcado |
| JDBC | - | Conexão com banco de dados |

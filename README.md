# 🍷 Adega 3 Irmãos — Sistema Gerencial de Estoque Tributário

Sistema de linha de comando em **Java** para gerenciar o estoque, as vendas e o caixa da adega **Adega 3 Irmãos**.

Projeto desenvolvido por **Anderson de Araújo Júnior** no **1º semestre do curso de Análise e Desenvolvimento de Sistemas (ADS)** do **SENAC**.

> **Restrições do projeto:** sem Programação Orientada a Objetos (uma única classe, só métodos `static`) e sem banco de dados. Os dados ficam em **vetores paralelos** na memória e são perdidos ao fechar o programa.

---

## Funcionalidades

| Nº | Opção | O que faz |
|----|-------|-----------|
| 1 | **Vender Bebida (PDV)** | Registra vendas com desconto automático, imposto embutido e lucro da venda |
| 2 | **Cadastrar Novo Produto** | Cadastra produto com preço de compra e venda, estoque e estoque mínimo |
| 3 | **Relatório de Estoque Completo** | Lista todos os produtos com lucro por unidade e permite repor estoque |
| 4 | **Alerta de Estoque Baixo** | Mostra os produtos abaixo do estoque mínimo |
| 5 | **Excluir Produto** | Remove um produto, com confirmação |
| 6 | **Filtrar por Categoria** | Lista produtos por categoria (letra inicial ou nome) |
| 7 | **Relatório Tributário** | Vendas brutas, descontos, impostos e receita líquida |
| 8 | **Relatório de Lucro** | Receita líquida menos o custo das bebidas vendidas, com margem de lucro |
| 9 | **Alterar Alíquota de Imposto** | Altera a alíquota padrão ou a de um produto |
| 10 | **Alterar Produto Cadastrado** | Edita nome, categoria, preços, estoque, estoque mínimo e alíquota sem excluir o produto |
| 11 | **Sair** | Encerra o sistema |

O cabeçalho do menu mostra em tempo real o **caixa atual**, os **impostos arrecadados**, a **receita líquida** e o **lucro**.

---

## Regras de negócio

- **Desconto:** 10% a partir de 10 unidades do mesmo produto na mesma venda.
- **Imposto embutido:** cada produto tem sua alíquota. A alíquota padrão é **18%** e pode ser alterada.
- **Preço mínimo sem prejuízo:** `custo ÷ (0,90 × (1 − alíquota))`, ou seja, considera o desconto de 10% e o imposto.
- **Preço recomendado:** o mínimo acrescido de **20% de margem** sobre o custo.
- **Lucro da venda:** valor cobrado − imposto − custo das bebidas.
- **Categorias:** Cerveja, Vinho, Destilado ou **Outros** (categoria digitada pelo usuário).

### Proteções contra prejuízo

- Ao cadastrar ou alterar preços, o sistema **avisa** se o preço pode dar prejuízo e pede confirmação (S/N).
- O preço de venda nunca pode ser menor que o custo.
- Ao alterar a alíquota de um produto, o sistema mostra a **alíquota máxima sem prejuízo** e **recusa** valores acima dela.

---

## Validação de entradas

O programa foi testado com entradas inesperadas e não trava nem gera exceções. Entre as proteções:

- Conversão numérica manual e estrita (recusa `NaN`, `1e5`, `0x10`, números negativos, vazio, etc.).
- Limites: até **100 produtos**, preços de até **R$ 1.000.000,00**, quantidades de até **1.000.000**, nomes de até **50 caracteres**.
- Digitar `cancelar` volta ao menu em praticamente qualquer pergunta.
- `try/catch` no laço principal como rede de segurança, e tratamento do fim da entrada (Ctrl+D / Ctrl+Z).

---

## Como executar

**Requisito:** [JDK](https://adoptium.net/) 11 ou superior instalado.

```bash
# Compilar
javac -encoding UTF-8 Adega3Irmaos.java

# Executar
java Adega3Irmaos
```

Ou, a partir do JDK 11, direto pelo código-fonte:

```bash
java Adega3Irmaos.java
```


## Dados de teste

Ao iniciar, o sistema carrega 3 produtos de exemplo:

| Produto | Categoria | Compra | Venda | Estoque | Mínimo | Alíquota |
|---------|-----------|--------|-------|---------|--------|----------|
| Heineken 600ml | Cerveja | R$ 8,50 | R$ 14,50 | 5 | 20 | 18% |
| Vinho Casillero | Vinho | R$ 40,00 | R$ 65,00 | 12 | 10 | 15% |
| Gin Tanqueray | Destilado | R$ 85,00 | R$ 120,00 | 30 | 5 | 20% |

**Exemplo:** vender 5 Heinekens gera R$ 72,50 em caixa, R$ 13,05 de imposto e R$ 16,95 de lucro.

---

## Estrutura do código

Arquivo único: `Adega3Irmaos.java`.

- **Dados:** vetores paralelos (`nomes[]`, `categorias[]`, `precosCompra[]`, `precosVenda[]`, `quantidades[]`, `estoquesMinimos[]`, `aliquotas[]`) e acumuladores do caixa.
- **Entrada:** `lerLinha`, `converterInteiro` e `converterValor`.
- **Cálculos:** `calcularPrecoMinimo`, `calcularPrecoRecomendado`, `lucroPorUnidade` e `aliquotaMaximaSemPrejuizo`.
- **Telas:** um método por opção do menu (`realizarVenda`, `cadastrarProduto`, `alterarProduto`, etc.).

---

## Conceitos praticados

Vetores, métodos e variáveis `static`, laços e condicionais, formatação de saída com `String.format`, manipulação de `String`, validação de entrada e tratamento de exceções com `try/catch`.

---

## Licença

Projeto acadêmico, de uso educacional.

---

## Autor

**Anderson de Araújo Júnior**
Curso de Análise e Desenvolvimento de Sistemas (ADS), SENAC

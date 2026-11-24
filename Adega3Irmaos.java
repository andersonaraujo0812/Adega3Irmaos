import java.util.NoSuchElementException;
import java.util.Scanner;

public class Adega3Irmaos {

    private static Scanner scanner = new Scanner(System.in);

    private static final int MAX_PRODUTOS = 100;
    private static final double ALIQUOTA_MAXIMA = 99.0;
    private static final double PRECO_MAXIMO = 1000000.0;
    private static final int QTD_MAXIMA = 1000000;
    private static final int QTD_MINIMA_DESCONTO = 10;
    private static final double PERCENTUAL_DESCONTO = 10.0;
    private static final double MARGEM_RECOMENDADA = 20.0;
    private static int totalCadastrados = 0;
    private static double aliquotaPadrao = 18.0;

    private static String[] nomes = new String[MAX_PRODUTOS];
    private static String[] categorias = new String[MAX_PRODUTOS];
    private static double[] precosCompra = new double[MAX_PRODUTOS];
    private static double[] precosVenda = new double[MAX_PRODUTOS];
    private static int[] quantidades = new int[MAX_PRODUTOS];
    private static int[] estoquesMinimos = new int[MAX_PRODUTOS];
    private static double[] aliquotas = new double[MAX_PRODUTOS];

    private static double caixaDoDia = 0.0;
    private static double totalImpostos = 0.0;
    private static double totalDescontos = 0.0;
    private static double totalCustos = 0.0;

    public static void main(String[] args) {
        carregarDadosDeTeste();

        int opcao = 0;

        do {
            System.out.println("\n==========================================");
            System.out.println("          SISTEMA GERENCIAL ADEGA         ");
            System.out.println("==========================================");
            System.out.println(" CAIXA ATUAL: R$ " + String.format("%.2f", caixaDoDia));
            System.out.println(" IMPOSTOS ARRECADADOS: R$ " + String.format("%.2f", totalImpostos));
            System.out.println(" RECEITA LIQUIDA (sem imposto): R$ " + String.format("%.2f", caixaDoDia - totalImpostos));
            System.out.println(" LUCRO (após imposto e custo): R$ " + String.format("%.2f", caixaDoDia - totalImpostos - totalCustos));
            System.out.println("------------------------------------------");
            System.out.println("1. Vender Bebida (PDV)");
            System.out.println("2. Cadastrar Novo Produto");
            System.out.println("3. Relatório de Estoque Completo (reposição)");
            System.out.println("4. Alerta de Estoque Baixo");
            System.out.println("5. Excluir Produto");
            System.out.println("6. Filtrar por Categoria");
            System.out.println("7. Relatório Tributário");
            System.out.println("8. Relatório de Lucro");
            System.out.println("9. Alterar Alíquota de Imposto");
            System.out.println("10. Alterar Produto Cadastrado");
            System.out.println("11. Sair");

            while (true) {
                System.out.print("Escolha uma opção: ");
                String entrada = lerLinha();
                opcao = converterInteiro(entrada, 99);
                if (opcao == -1) {
                    System.out.println("[Erro] Digite apenas o número da opção desejada.");
                } else if (opcao >= 1 && opcao <= 11) {
                    break;
                } else {
                    System.out.println("[Erro] Opção inválida! Escolha entre 1 e 11.");
                }
            }

            try {
                switch (opcao) {
                    case 1: realizarVenda(); break;
                    case 2: cadastrarProduto(); break;
                    case 3: exibirEstoqueGeral(); break;
                    case 4: exibirEstoqueBaixo(); break;
                    case 5: excluirProduto(); break;
                    case 6: filtrarPorCategoria(); break;
                    case 7: relatorioTributario(); break;
                    case 8: relatorioLucro(); break;
                    case 9: alterarAliquota(); break;
                    case 10: alterarProduto(); break;
                    case 11: System.out.println("\nFechando o caixa... Até mais!"); break;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("\n[Erro] Posição inválida no estoque. Operação cancelada, voltando ao menu.");
            } catch (NumberFormatException e) {
                System.out.println("\n[Erro] Número em formato inválido. Operação cancelada, voltando ao menu.");
            } catch (Exception e) {
                System.out.println("\n[Erro inesperado] Operação cancelada, voltando ao menu.");
            }
        } while (opcao != 11);
    }

    private static String lerLinha() {
        try {
            return scanner.nextLine().trim();
        } catch (NoSuchElementException e) {
            System.out.println("\n[Aviso] Entrada encerrada. Fechando o sistema...");
            System.exit(0);
        } catch (IllegalStateException e) {
            System.out.println("\n[Aviso] Leitor de dados indisponível. Fechando o sistema...");
            System.exit(0);
        }
        return "";
    }

    private static double converterValor(String texto) {
        if (texto.length() == 0) return -1;

        int posSeparador = -1;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '.' || c == ',') {
                if (posSeparador != -1) return -1;
                posSeparador = i;
            } else if (c < '0' || c > '9') {
                return -1;
            }
        }

        int digitosInteiros = texto.length();
        int casasDecimais = 0;
        if (posSeparador != -1) {
            digitosInteiros = posSeparador;
            casasDecimais = texto.length() - posSeparador - 1;
            if (casasDecimais < 1 || casasDecimais > 2) return -1;
        }
        if (digitosInteiros < 1 || digitosInteiros > 7) return -1;

        double v;
        try {
            v = Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            return -1;
        }
        if (v > PRECO_MAXIMO) return -1;
        return v;
    }

    private static int converterInteiro(String texto, int maximo) {
        if (texto.length() < 1 || texto.length() > 7) return -1;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c < '0' || c > '9') return -1;
        }
        int v;
        try {
            v = Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (v > maximo) return -1;
        return v;
    }

    private static boolean cancelou(String texto) {
        return texto.equalsIgnoreCase("cancelar");
    }

    private static boolean nomeJaExiste(String nome) {
        return nomeJaExiste(nome, -1);
    }

    private static boolean nomeJaExiste(String nome, int posicaoIgnorada) {
        for (int i = 0; i < totalCadastrados; i++) {
            if (i != posicaoIgnorada && nomes[i].equalsIgnoreCase(nome)) return true;
        }
        return false;
    }

    private static boolean temLetraOuDigito(String texto) {
        for (int i = 0; i < texto.length(); i++) {
            if (Character.isLetterOrDigit(texto.charAt(i))) return true;
        }
        return false;
    }

    private static String formatarCapitalizado(String texto) {
        if (texto.isEmpty()) return texto;
        return texto.substring(0, 1).toUpperCase() + texto.substring(1).toLowerCase();
    }

    // Lê a categoria (C, V, D ou O = outra, digitada). Retorna null se o usuário cancelar.
    private static String lerCategoria() {
        while (true) {
            System.out.print("Categoria [C] Cerveja | [V] Vinho | [D] Destilado | [O] Outros (ou 'cancelar'): ");
            String entradaCat = lerLinha();
            if (cancelou(entradaCat)) return null;
            entradaCat = entradaCat.toUpperCase();
            if (entradaCat.equals("C")) return "Cerveja";
            if (entradaCat.equals("V")) return "Vinho";
            if (entradaCat.equals("D")) return "Destilado";
            if (entradaCat.equals("O")) {
                while (true) {
                    System.out.print("Digite o nome da nova categoria (ex: Refrigerante, Suco, Energético) ou 'cancelar': ");
                    String novaCat = lerLinha();
                    if (cancelou(novaCat)) return null;
                    if (novaCat.isEmpty()) {
                        System.out.println("[Erro] O nome da categoria não pode ser vazio.");
                    } else if (novaCat.length() > 30) {
                        System.out.println("[Erro] A categoria pode ter no máximo 30 caracteres.");
                    } else if (!Character.isLetter(novaCat.charAt(0))) {
                        System.out.println("[Erro] A categoria deve começar com uma letra.");
                    } else {
                        String categoria = formatarCapitalizado(novaCat);
                        System.out.println("  -> Categoria '" + categoria + "' definida (atalho de filtro: '" + categoria.charAt(0) + "').");
                        return categoria;
                    }
                }
            }
            System.out.println("[Erro] Letra inválida! Digite apenas C, V, D ou O.");
        }
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    private static double arredondarParaCima(double valor) {
        return Math.ceil(valor * 100.0 - 1e-9) / 100.0;
    }

    private static double calcularPrecoMinimo(double custo) {
        return calcularPrecoMinimo(custo, aliquotaPadrao);
    }

    private static double calcularPrecoMinimo(double custo, double aliquota) {
        double fatorDesconto = 1.0 - (PERCENTUAL_DESCONTO / 100.0);
        double fatorImposto = 1.0 - (aliquota / 100.0);
        return arredondarParaCima(custo / (fatorDesconto * fatorImposto));
    }

    private static double calcularPrecoRecomendado(double custo) {
        return calcularPrecoRecomendado(custo, aliquotaPadrao);
    }

    private static double calcularPrecoRecomendado(double custo, double aliquota) {
        double fatorDesconto = 1.0 - (PERCENTUAL_DESCONTO / 100.0);
        double fatorImposto = 1.0 - (aliquota / 100.0);
        return arredondarParaCima(custo * (1.0 + MARGEM_RECOMENDADA / 100.0) / (fatorDesconto * fatorImposto));
    }

    private static double lucroPorUnidade(double precoVenda, double custo, double aliquota, boolean comDesconto) {
        double valorCobrado = precoVenda;
        if (comDesconto) {
            valorCobrado = precoVenda * (1.0 - PERCENTUAL_DESCONTO / 100.0);
        }
        double liquido = valorCobrado * (1.0 - aliquota / 100.0);
        return liquido - custo;
    }

    private static void carregarDadosDeTeste() {
        nomes[0] = "Heineken 600ml"; categorias[0] = "Cerveja"; precosCompra[0] = 8.50; precosVenda[0] = 14.50; quantidades[0] = 5; estoquesMinimos[0] = 20; aliquotas[0] = 18.0;
        nomes[1] = "Vinho Casillero"; categorias[1] = "Vinho"; precosCompra[1] = 40.00; precosVenda[1] = 65.00; quantidades[1] = 12; estoquesMinimos[1] = 10; aliquotas[1] = 15.0;
        nomes[2] = "Gin Tanqueray"; categorias[2] = "Destilado"; precosCompra[2] = 85.00; precosVenda[2] = 120.00; quantidades[2] = 30; estoquesMinimos[2] = 5; aliquotas[2] = 20.0;
        totalCadastrados = 3;
    }

    private static void realizarVenda() {
        if (totalCadastrados == 0) {
            System.out.println("\n[Aviso] Estoque vazio!");
            return;
        }

        System.out.println("\n--- Selecione a Bebida ---");
        for (int i = 0; i < totalCadastrados; i++) {
            System.out.println("[" + i + "] " + nomes[i] +
                               " | Preço final: R$ " + String.format("%.2f", precosVenda[i]) +
                               " | Estoque: " + quantidades[i] +
                               " | Imposto embutido: " + String.format("%.2f", aliquotas[i]) + "%");
        }

        int indice = -1;
        while (true) {
            System.out.print("\nDigite o NÚMERO do produto (ou -1 para cancelar): ");
            String entrada = lerLinha();
            if (entrada.equals("-1")) { System.out.println("Venda cancelada."); return; }

            indice = converterInteiro(entrada, MAX_PRODUTOS);
            if (indice < 0 || indice >= totalCadastrados) {
                System.out.println("[Erro] Número não encontrado na lista.");
                continue;
            }
            if (quantidades[indice] == 0) {
                System.out.println("[Erro] O produto '" + nomes[indice] + "' está sem estoque. Escolha outro produto.");
                continue;
            }
            break;
        }

        System.out.println("\nDesconto de " + String.format("%.0f", PERCENTUAL_DESCONTO) + "% a partir de " + QTD_MINIMA_DESCONTO + " unidades!");
        while (true) {
            System.out.print("Vender quantas unidades de " + nomes[indice] + "? (0 para cancelar): ");
            String entrada = lerLinha();
            if (entrada.equals("0")) { System.out.println("Venda cancelada."); return; }

            int qtdVenda = converterInteiro(entrada, QTD_MAXIMA);
            if (qtdVenda <= 0) {
                System.out.println("[Erro] Digite um número inteiro maior que zero.");
            } else if (qtdVenda > quantidades[indice]) {
                System.out.println("[Erro] Estoque insuficiente! Restam apenas " + quantidades[indice] + " unidades.");
            } else {

                double valorBruto = arredondar(qtdVenda * precosVenda[indice]);

                double desconto = 0.0;
                if (qtdVenda >= QTD_MINIMA_DESCONTO) {
                    desconto = arredondar(valorBruto * PERCENTUAL_DESCONTO / 100.0);
                }
                double valorTotalVenda = arredondar(valorBruto - desconto);

                double imposto = arredondar(valorTotalVenda * aliquotas[indice] / 100.0);
                double receitaLiquida = arredondar(valorTotalVenda - imposto);
                double custoVenda = arredondar(qtdVenda * precosCompra[indice]);

                quantidades[indice] -= qtdVenda;
                caixaDoDia += valorTotalVenda;
                totalImpostos += imposto;
                totalDescontos += desconto;
                totalCustos += custoVenda;

                System.out.println("\n[Sucesso] Venda concluída!");
                if (desconto > 0) {
                    System.out.println(" - Subtotal (sem desconto): R$ " + String.format("%.2f", valorBruto));
                    System.out.println(" - Desconto (" + String.format("%.0f", PERCENTUAL_DESCONTO) + "%): - R$ " + String.format("%.2f", desconto));
                }
                System.out.println(" - Valor total (com imposto): R$ " + String.format("%.2f", valorTotalVenda));
                System.out.println(" - Imposto embutido (" + String.format("%.2f", aliquotas[indice]) + "%): R$ " + String.format("%.2f", imposto));
                System.out.println(" - Receita líquida (sem imposto): R$ " + String.format("%.2f", receitaLiquida));
                System.out.println(" - Lucro da venda (após custo): R$ " + String.format("%.2f", receitaLiquida - custoVenda));
                break;
            }
        }
    }

    private static void cadastrarProduto() {
        if (totalCadastrados >= MAX_PRODUTOS) {
            System.out.println("\n[Erro] Estoque cheio!");
            return;
        }

        System.out.println("\n--- Cadastro de Produto ---");
        System.out.println("(Digite 'cancelar' em qualquer campo para voltar ao menu)");

        String nome;
        while (true) {
            System.out.print("Nome do produto: ");
            nome = lerLinha();
            if (cancelou(nome)) { System.out.println("Cadastro cancelado."); return; }
            if (nome.isEmpty()) {
                System.out.println("[Erro] O nome não pode ser vazio.");
            } else if (nome.length() > 50) {
                System.out.println("[Erro] O nome pode ter no máximo 50 caracteres.");
            } else if (!temLetraOuDigito(nome)) {
                System.out.println("[Erro] O nome precisa ter pelo menos uma letra ou número.");
            } else if (nomeJaExiste(nome)) {
                System.out.println("[Erro] Já existe um produto com esse nome.");
            } else {
                break;
            }
        }

        String categoria = lerCategoria();
        if (categoria == null) { System.out.println("Cadastro cancelado."); return; }

        double precoCompra;
        while (true) {
            System.out.print("Preço de Compra (R$): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Cadastro cancelado."); return; }
            precoCompra = converterValor(entrada);
            if (precoCompra > 0) break;
            System.out.println("[Erro] Digite um valor maior que zero, até 2 casas decimais (ex: 8,50). Máximo: R$ 1.000.000,00.");
        }

        double precoMinimo = calcularPrecoMinimo(precoCompra);
        double precoRecomendado = calcularPrecoRecomendado(precoCompra);

        System.out.println("\n  >> RECOMENDAÇÃO DE PREÇO (custo R$ " + String.format("%.2f", precoCompra) + ")");
        System.out.println("     Preço mínimo sem prejuízo: R$ " + String.format("%.2f", precoMinimo)
                + " (considera " + String.format("%.0f", PERCENTUAL_DESCONTO) + "% de desconto e "
                + String.format("%.0f", aliquotaPadrao) + "% de imposto)");
        System.out.println("     Preço recomendado:         R$ " + String.format("%.2f", precoRecomendado)
                + " (garante margem mínima de " + String.format("%.0f", MARGEM_RECOMENDADA) + "% sobre o custo mesmo com desconto)");
        System.out.println("     Lucro por un. se vender com desconto: R$ "
                + String.format("%.2f", lucroPorUnidade(precoRecomendado, precoCompra, aliquotaPadrao, true)));
        if (precoRecomendado > PRECO_MAXIMO) {
            System.out.println("     [Aviso] O preço recomendado passa do limite do sistema (R$ "
                    + String.format("%.2f", PRECO_MAXIMO) + ").");
        }
        System.out.println();

        double precoVenda;
        while (true) {
            System.out.print("Preço de Venda (R$): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Cadastro cancelado."); return; }
            precoVenda = converterValor(entrada);
            if (precoVenda < 0) {
                System.out.println("[Erro] Digite um valor válido, até 2 casas decimais (ex: 14,50). Máximo: R$ 1.000.000,00.");
            } else if (precoVenda < precoCompra) {
                System.out.println("[Erro] O preço de venda não pode ser menor que o preço de custo (R$ " + String.format("%.2f", precoCompra) + ").");
            } else if (precoVenda < precoMinimo) {

                System.out.println("[Atenção] Com " + String.format("%.0f", aliquotaPadrao) + "% de imposto e "
                        + String.format("%.0f", PERCENTUAL_DESCONTO) + "% de desconto, esse preço pode dar PREJUÍZO"
                        + " (mínimo: R$ " + String.format("%.2f", precoMinimo) + ").");
                System.out.print("Manter mesmo assim? (S/N): ");
                String resposta = lerLinha();
                if (resposta.equalsIgnoreCase("S")) break;
            } else {
                break;
            }
        }

        int qtd;
        while (true) {
            System.out.print("Quantidade atual em estoque: ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Cadastro cancelado."); return; }
            qtd = converterInteiro(entrada, QTD_MAXIMA);
            if (qtd >= 0) break;
            System.out.println("[Erro] Digite um número inteiro entre 0 e " + QTD_MAXIMA + ".");
        }

        int min;
        while (true) {
            System.out.print("Estoque mínimo (Alerta de reposição): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Cadastro cancelado."); return; }
            min = converterInteiro(entrada, QTD_MAXIMA);
            if (min >= 0) break;
            System.out.println("[Erro] Digite um número inteiro entre 0 e " + QTD_MAXIMA + ".");
        }

        nomes[totalCadastrados] = nome;
        categorias[totalCadastrados] = categoria;
        precosCompra[totalCadastrados] = precoCompra;
        precosVenda[totalCadastrados] = precoVenda;
        quantidades[totalCadastrados] = qtd;
        estoquesMinimos[totalCadastrados] = min;
        aliquotas[totalCadastrados] = aliquotaPadrao;

        totalCadastrados++;
        System.out.println("\n[Sucesso] Produto adicionado! Categoria: " + categoria + " | Alíquota aplicada: " + String.format("%.2f", aliquotaPadrao) + "%");
    }

    private static void exibirEstoqueGeral() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        boolean mostrarRelatorio = true;

        while (true) {
            if (mostrarRelatorio) {
                System.out.println("\n=== ESTOQUE GERAL ===");
                for (int i = 0; i < totalCadastrados; i++) {
                    System.out.print("[" + i + "] " + nomes[i] + " (" + categorias[i] + ") | Qtd: " + quantidades[i]
                                     + " | Mínimo: " + estoquesMinimos[i]);
                    if (quantidades[i] <= estoquesMinimos[i]) {
                        System.out.print("  <-- ESTOQUE BAIXO");
                    }
                    System.out.println();
                    System.out.println("    Compra: R$ " + String.format("%.2f", precosCompra[i]) +
                                       " | Venda: R$ " + String.format("%.2f", precosVenda[i]) +
                                       " | Imposto: " + String.format("%.2f", aliquotas[i]) + "%");
                    System.out.println("    Lucro por un. (após imposto): R$ "
                                       + String.format("%.2f", lucroPorUnidade(precosVenda[i], precosCompra[i], aliquotas[i], false))
                                       + " | com desconto: R$ "
                                       + String.format("%.2f", lucroPorUnidade(precosVenda[i], precosCompra[i], aliquotas[i], true)));
                }
                mostrarRelatorio = false;
            }

            System.out.print("\nDigite o NÚMERO do produto para REPOR o estoque (ou -1 para voltar ao menu): ");
            String entrada = lerLinha();
            if (entrada.equals("-1")) return;

            int idx = converterInteiro(entrada, MAX_PRODUTOS);
            if (idx < 0 || idx >= totalCadastrados) {
                System.out.println("[Erro] Número não encontrado na lista.");
                continue;
            }

            while (true) {
                System.out.print("Quantas unidades de '" + nomes[idx] + "' entraram? (0 para cancelar): ");
                String entradaQtd = lerLinha();
                if (entradaQtd.equals("0")) { System.out.println("Reposição cancelada."); break; }

                int qtd = converterInteiro(entradaQtd, QTD_MAXIMA);
                if (qtd <= 0) {
                    System.out.println("[Erro] Digite um número inteiro maior que zero.");
                } else if (quantidades[idx] + qtd > QTD_MAXIMA) {
                    System.out.println("[Erro] O estoque não pode passar de " + QTD_MAXIMA + " unidades (atual: " + quantidades[idx] + ").");
                } else {
                    quantidades[idx] += qtd;
                    System.out.println("\n[Sucesso] Novo estoque de '" + nomes[idx] + "': " + quantidades[idx] + " unidades.");
                    mostrarRelatorio = true;
                    break;
                }
            }
        }
    }

    private static void exibirEstoqueBaixo() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== ALERTA DE ESTOQUE BAIXO ===");
        boolean temEstoqueBaixo = false;

        for (int i = 0; i < totalCadastrados; i++) {
            if (quantidades[i] <= estoquesMinimos[i]) {
                temEstoqueBaixo = true;
                System.out.println("-> COMPRAR: " + nomes[i] + " | Atual: " + quantidades[i] + " | Mínimo: " + estoquesMinimos[i]);
            }
        }
        if (!temEstoqueBaixo) System.out.println("Todos os produtos estão com estoque em nível seguro.");
        else System.out.println("\n(Para repor, use a opção 3 - Relatório de Estoque Completo)");
    }

    private static void excluirProduto() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== EXCLUSÃO DE PRODUTO ===");
        for (int i = 0; i < totalCadastrados; i++) {
            System.out.println("[" + i + "] " + nomes[i]);
        }

        int idx = -1;
        while (true) {
            System.out.print("\nDigite o NÚMERO do produto que deseja excluir (ou -1 para cancelar): ");
            String entrada = lerLinha();
            if (entrada.equals("-1")) { System.out.println("Exclusão cancelada."); return; }
            idx = converterInteiro(entrada, MAX_PRODUTOS);
            if (idx >= 0 && idx < totalCadastrados) break;
            System.out.println("[Erro] Código inválido. Escolha um número da lista.");
        }

        System.out.print("Tem certeza que deseja APAGAR '" + nomes[idx] + "'? (S/N): ");
        String confirmacao = lerLinha();

        if (confirmacao.equalsIgnoreCase("S")) {
            String nomeExcluido = nomes[idx];
            for (int j = idx; j < totalCadastrados - 1; j++) {
                nomes[j] = nomes[j + 1];
                categorias[j] = categorias[j + 1];
                precosCompra[j] = precosCompra[j + 1];
                precosVenda[j] = precosVenda[j + 1];
                quantidades[j] = quantidades[j + 1];
                estoquesMinimos[j] = estoquesMinimos[j + 1];
                aliquotas[j] = aliquotas[j + 1];
            }
            nomes[totalCadastrados - 1] = null;
            categorias[totalCadastrados - 1] = null;
            totalCadastrados--;
            System.out.println("\n[Sucesso] '" + nomeExcluido + "' foi removido permanentemente.");
        } else {
            System.out.println("\n[Aviso] Exclusão cancelada.");
        }
    }

    private static void filtrarPorCategoria() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== FILTRO POR CATEGORIA ===");

        String[] categoriasUnicas = new String[MAX_PRODUTOS];
        int totalUnicas = 0;
        for (int i = 0; i < totalCadastrados; i++) {
            boolean jaListada = false;
            for (int j = 0; j < totalUnicas; j++) {
                if (categoriasUnicas[j].equalsIgnoreCase(categorias[i])) {
                    jaListada = true;
                    break;
                }
            }
            if (!jaListada) {
                categoriasUnicas[totalUnicas] = categorias[i];
                totalUnicas++;
            }
        }

        System.out.print("Categorias disponíveis: ");
        for (int i = 0; i < totalUnicas; i++) {
            char inicial = Character.toUpperCase(categoriasUnicas[i].charAt(0));
            System.out.print("[" + inicial + "] " + categoriasUnicas[i]);
            if (i < totalUnicas - 1) System.out.print(" | ");
        }
        System.out.println(" | [O] Outros (outras categorias)");

        System.out.print("Digite a letra inicial ou o nome da categoria: ");
        String busca = lerLinha().trim();

        if (busca.isEmpty()) {
            System.out.println("[Erro] Digite uma letra inicial ou nome de categoria.");
            return;
        }

        boolean encontrou = false;
        System.out.println("\n--- Resultados para '" + busca.toUpperCase() + "' ---");

        boolean buscarOutros = busca.equalsIgnoreCase("O") || busca.equalsIgnoreCase("Outros");

        for (int i = 0; i < totalCadastrados; i++) {
            boolean ehPadrao = categorias[i].equalsIgnoreCase("Cerveja") ||
                               categorias[i].equalsIgnoreCase("Vinho") ||
                               categorias[i].equalsIgnoreCase("Destilado");

            boolean corresponde;
            if (buscarOutros) {
                corresponde = !ehPadrao || categorias[i].toLowerCase().startsWith("o");
            } else {
                corresponde = categorias[i].toLowerCase().startsWith(busca.toLowerCase());
            }

            if (corresponde) {
                encontrou = true;
                System.out.println("[" + i + "] " + nomes[i] + " (" + categorias[i] + ") | Estoque: " + quantidades[i]
                        + " | Preço: R$ " + String.format("%.2f", precosVenda[i])
                        + " | Imposto: " + String.format("%.2f", aliquotas[i]) + "%");
            }
        }
        if (!encontrou) System.out.println("Nenhum produto cadastrado nessa categoria.");
    }

    private static void relatorioTributario() {
        double vendasBrutas = caixaDoDia + totalDescontos;

        System.out.println("\n=== RELATÓRIO TRIBUTÁRIO ===");
        System.out.println("Vendas brutas (sem desconto):    R$ " + String.format("%.2f", vendasBrutas));
        System.out.println("(-) Descontos concedidos:        R$ " + String.format("%.2f", totalDescontos));
        System.out.println("(=) Total em caixa (valor cobrado): R$ " + String.format("%.2f", caixaDoDia));
        System.out.println("(-) Impostos sobre as vendas:    R$ " + String.format("%.2f", totalImpostos));
        System.out.println("(=) Receita líquida (sem imposto): R$ " + String.format("%.2f", caixaDoDia - totalImpostos));
    }

    private static void relatorioLucro() {
        double vendasBrutas = caixaDoDia + totalDescontos;
        double receitaLiquida = caixaDoDia - totalImpostos;
        double lucro = receitaLiquida - totalCustos;

        System.out.println("\n=== RELATÓRIO DE LUCRO ===");
        System.out.println("Vendas brutas (sem desconto):       R$ " + String.format("%.2f", vendasBrutas));
        System.out.println("(-) Descontos concedidos:           R$ " + String.format("%.2f", totalDescontos));
        System.out.println("(=) Total em caixa (valor cobrado): R$ " + String.format("%.2f", caixaDoDia));
        System.out.println("(-) Impostos sobre as vendas:       R$ " + String.format("%.2f", totalImpostos));
        System.out.println("(=) Receita líquida:                R$ " + String.format("%.2f", receitaLiquida));
        System.out.println("(-) Custo das bebidas vendidas:     R$ " + String.format("%.2f", totalCustos));
        System.out.println("(=) LUCRO:                          R$ " + String.format("%.2f", lucro));
        if (caixaDoDia > 0) {
            System.out.println("Margem de lucro (sobre o caixa):    " + String.format("%.2f", lucro / caixaDoDia * 100.0) + "%");
        } else {
            System.out.println("Ainda não há vendas registradas.");
        }
        if (lucro < 0) {
            System.out.println("[Atenção] Você está tendo PREJUÍZO nas vendas até agora.");
        }
    }

    private static double aliquotaMaximaSemPrejuizo(int idx) {
        double valorComDesconto = precosVenda[idx] * (1.0 - PERCENTUAL_DESCONTO / 100.0);
        double maxima = (1.0 - precosCompra[idx] / valorComDesconto) * 100.0;
        maxima = Math.floor(maxima * 100.0) / 100.0;
        if (maxima > ALIQUOTA_MAXIMA) maxima = ALIQUOTA_MAXIMA;
        return maxima;
    }

    private static double lerAliquota(double maximo) {
        while (true) {
            System.out.print("Nova alíquota (0 a " + String.format("%.2f", maximo) + "%, ex: 18 ou 12,5) ou 'cancelar': ");
            String entrada = lerLinha();
            if (cancelou(entrada)) return -1;
            double v = converterValor(entrada);
            if (v < 0) {
                System.out.println("[Erro] Digite um número válido, com até 2 casas decimais.");
            } else if (v > maximo) {
                if (maximo < ALIQUOTA_MAXIMA) {
                    System.out.println("[Erro] Essa alíquota daria PREJUÍZO. O máximo permitido é "
                            + String.format("%.2f", maximo) + "%.");
                } else {
                    System.out.println("[Erro] A alíquota não pode passar de " + String.format("%.0f", ALIQUOTA_MAXIMA) + "%.");
                }
            } else {
                return v;
            }
        }
    }

    private static void alterarAliquota() {
        System.out.println("\n=== ALTERAR ALÍQUOTA DE IMPOSTO ===");
        System.out.println("Alíquota padrão atual: " + String.format("%.2f", aliquotaPadrao) + "%");
        System.out.println("1. Alterar a alíquota padrão (novos cadastros e cálculo de preço mínimo/recomendado)");
        System.out.println("2. Alterar a alíquota de um produto já cadastrado");
        System.out.println("0. Voltar");

        int opcao;
        while (true) {
            System.out.print("Escolha: ");
            opcao = converterInteiro(lerLinha(), 2);
            if (opcao >= 0) break;
            System.out.println("[Erro] Digite 0, 1 ou 2.");
        }
        if (opcao == 0) return;

        if (opcao == 1) {
            double nova = lerAliquota(ALIQUOTA_MAXIMA);
            if (nova < 0) { System.out.println("Alteração cancelada."); return; }
            aliquotaPadrao = nova;
            System.out.println("\n[Sucesso] Nova alíquota padrão: " + String.format("%.2f", aliquotaPadrao)
                    + "%. (Produtos já cadastrados não mudam; use a opção 2 para eles.)");
            return;
        }

        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }
        for (int i = 0; i < totalCadastrados; i++) {
            System.out.println("[" + i + "] " + nomes[i] + " | Alíquota atual: " + String.format("%.2f", aliquotas[i]) + "%");
        }
        int idx;
        while (true) {
            System.out.print("\nDigite o NÚMERO do produto (ou -1 para cancelar): ");
            String entrada = lerLinha();
            if (entrada.equals("-1")) { System.out.println("Alteração cancelada."); return; }
            idx = converterInteiro(entrada, MAX_PRODUTOS);
            if (idx >= 0 && idx < totalCadastrados) break;
            System.out.println("[Erro] Número não encontrado na lista.");
        }
        alterarAliquotaDoProduto(idx);
    }

    private static void alterarAliquotaDoProduto(int idx) {
        double maxima = aliquotaMaximaSemPrejuizo(idx);
        if (maxima < 0) {
            System.out.println("\n[Erro] '" + nomes[idx] + "' dá prejuízo (vendido com desconto) mesmo com 0% de imposto."
                    + " Nenhuma alíquota resolve; aumente o preço de venda (opção 10 do menu).");
            return;
        }
        System.out.println("Alíquota máxima para '" + nomes[idx] + "' sem dar prejuízo: " + String.format("%.2f", maxima) + "%");
        double nova = lerAliquota(maxima);
        if (nova < 0) { System.out.println("Alteração cancelada."); return; }
        aliquotas[idx] = nova;
        System.out.println("\n[Sucesso] Alíquota de '" + nomes[idx] + "': " + String.format("%.2f", nova) + "%");
        System.out.println("Lucro por un. agora: R$ "
                + String.format("%.2f", lucroPorUnidade(precosVenda[idx], precosCompra[idx], aliquotas[idx], false))
                + " | com desconto: R$ "
                + String.format("%.2f", lucroPorUnidade(precosVenda[idx], precosCompra[idx], aliquotas[idx], true)));
    }

    private static void mostrarProduto(int idx) {
        System.out.println("\n[" + idx + "] " + nomes[idx] + " (" + categorias[idx] + ")");
        System.out.println("    Compra: R$ " + String.format("%.2f", precosCompra[idx])
                + " | Venda: R$ " + String.format("%.2f", precosVenda[idx])
                + " | Imposto: " + String.format("%.2f", aliquotas[idx]) + "%");
        System.out.println("    Estoque: " + quantidades[idx] + " | Mínimo: " + estoquesMinimos[idx]);
        System.out.println("    Lucro por un. (após imposto): R$ "
                + String.format("%.2f", lucroPorUnidade(precosVenda[idx], precosCompra[idx], aliquotas[idx], false))
                + " | com desconto: R$ "
                + String.format("%.2f", lucroPorUnidade(precosVenda[idx], precosCompra[idx], aliquotas[idx], true)));
    }

    private static void alterarProduto() {
        if (totalCadastrados == 0) { System.out.println("\n[Aviso] Estoque vazio!"); return; }

        System.out.println("\n=== ALTERAR PRODUTO CADASTRADO ===");
        for (int i = 0; i < totalCadastrados; i++) {
            System.out.println("[" + i + "] " + nomes[i] + " (" + categorias[i] + ")");
        }

        int idx;
        while (true) {
            System.out.print("\nDigite o NÚMERO do produto que deseja alterar (ou -1 para cancelar): ");
            String entrada = lerLinha();
            if (entrada.equals("-1")) { System.out.println("Alteração cancelada."); return; }
            idx = converterInteiro(entrada, MAX_PRODUTOS);
            if (idx >= 0 && idx < totalCadastrados) break;
            System.out.println("[Erro] Número não encontrado na lista.");
        }

        while (true) {
            mostrarProduto(idx);
            System.out.println("\nO que deseja alterar?");
            System.out.println("1. Nome");
            System.out.println("2. Categoria");
            System.out.println("3. Preço de compra (custo)");
            System.out.println("4. Preço de venda");
            System.out.println("5. Quantidade em estoque (correção)");
            System.out.println("6. Estoque mínimo");
            System.out.println("7. Alíquota deste produto");
            System.out.println("0. Voltar ao menu");

            int opcao;
            while (true) {
                System.out.print("Escolha: ");
                opcao = converterInteiro(lerLinha(), 7);
                if (opcao >= 0) break;
                System.out.println("[Erro] Digite um número de 0 a 7.");
            }

            switch (opcao) {
                case 0: return;
                case 1: alterarNome(idx); break;
                case 2: alterarCategoria(idx); break;
                case 3: alterarPrecoCompra(idx); break;
                case 4: alterarPrecoVenda(idx); break;
                case 5: alterarQuantidade(idx); break;
                case 6: alterarEstoqueMinimo(idx); break;
                case 7: alterarAliquotaDoProduto(idx); break;
            }
        }
    }

    private static void alterarNome(int idx) {
        while (true) {
            System.out.print("Novo nome (ou 'cancelar'): ");
            String nome = lerLinha();
            if (cancelou(nome)) { System.out.println("Alteração cancelada."); return; }
            if (nome.isEmpty()) {
                System.out.println("[Erro] O nome não pode ser vazio.");
            } else if (nome.length() > 50) {
                System.out.println("[Erro] O nome pode ter no máximo 50 caracteres.");
            } else if (!temLetraOuDigito(nome)) {
                System.out.println("[Erro] O nome precisa ter pelo menos uma letra ou número.");
            } else if (nomeJaExiste(nome, idx)) {
                System.out.println("[Erro] Já existe outro produto com esse nome.");
            } else {
                nomes[idx] = nome;
                System.out.println("\n[Sucesso] Nome alterado para '" + nome + "'.");
                return;
            }
        }
    }

    private static void alterarCategoria(int idx) {
        String nova = lerCategoria();
        if (nova == null) { System.out.println("Alteração cancelada."); return; }
        categorias[idx] = nova;
        System.out.println("\n[Sucesso] Categoria alterada para " + nova + ".");
    }

    private static void alterarPrecoCompra(int idx) {
        while (true) {
            System.out.print("Novo preço de compra (R$) (ou 'cancelar'): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Alteração cancelada."); return; }
            double novo = converterValor(entrada);
            if (novo <= 0) {
                System.out.println("[Erro] Digite um valor maior que zero, até 2 casas decimais (ex: 8,50). Máximo: R$ 1.000.000,00.");
            } else if (novo > precosVenda[idx]) {
                System.out.println("[Erro] O custo não pode ser maior que o preço de venda atual (R$ "
                        + String.format("%.2f", precosVenda[idx]) + "). Altere o preço de venda primeiro.");
            } else {
                double minimo = calcularPrecoMinimo(novo, aliquotas[idx]);
                if (precosVenda[idx] < minimo) {
                    System.out.println("[Atenção] Com esse custo, o preço de venda atual (R$ " + String.format("%.2f", precosVenda[idx])
                            + ") pode dar PREJUÍZO (mínimo: R$ " + String.format("%.2f", minimo) + ").");
                    System.out.print("Manter mesmo assim? (S/N): ");
                    if (!lerLinha().equalsIgnoreCase("S")) continue;
                }
                precosCompra[idx] = novo;
                System.out.println("\n[Sucesso] Preço de compra alterado para R$ " + String.format("%.2f", novo) + ".");
                return;
            }
        }
    }

    private static void alterarPrecoVenda(int idx) {
        double custo = precosCompra[idx];
        double minimo = calcularPrecoMinimo(custo, aliquotas[idx]);
        double recomendado = calcularPrecoRecomendado(custo, aliquotas[idx]);
        System.out.println("\n  >> Custo: R$ " + String.format("%.2f", custo) + " | Imposto deste produto: "
                + String.format("%.2f", aliquotas[idx]) + "%");
        System.out.println("     Preço mínimo sem prejuízo: R$ " + String.format("%.2f", minimo));
        System.out.println("     Preço recomendado:         R$ " + String.format("%.2f", recomendado));

        while (true) {
            System.out.print("Novo preço de venda (R$) (ou 'cancelar'): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Alteração cancelada."); return; }
            double novo = converterValor(entrada);
            if (novo < 0) {
                System.out.println("[Erro] Digite um valor válido, até 2 casas decimais (ex: 14,50). Máximo: R$ 1.000.000,00.");
            } else if (novo < custo) {
                System.out.println("[Erro] O preço de venda não pode ser menor que o preço de custo (R$ " + String.format("%.2f", custo) + ").");
            } else {
                if (novo < minimo) {
                    System.out.println("[Atenção] Esse preço pode dar PREJUÍZO com desconto e imposto (mínimo: R$ "
                            + String.format("%.2f", minimo) + ").");
                    System.out.print("Manter mesmo assim? (S/N): ");
                    if (!lerLinha().equalsIgnoreCase("S")) continue;
                }
                precosVenda[idx] = novo;
                System.out.println("\n[Sucesso] Preço de venda alterado para R$ " + String.format("%.2f", novo) + ".");
                return;
            }
        }
    }

    private static void alterarQuantidade(int idx) {
        while (true) {
            System.out.print("Quantidade correta em estoque (0 a " + QTD_MAXIMA + ") (ou 'cancelar'): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Alteração cancelada."); return; }
            int nova = converterInteiro(entrada, QTD_MAXIMA);
            if (nova >= 0) {
                quantidades[idx] = nova;
                System.out.println("\n[Sucesso] Estoque de '" + nomes[idx] + "' agora é " + nova + " unidades.");
                return;
            }
            System.out.println("[Erro] Digite um número inteiro entre 0 e " + QTD_MAXIMA + ".");
        }
    }

    private static void alterarEstoqueMinimo(int idx) {
        while (true) {
            System.out.print("Novo estoque mínimo (0 a " + QTD_MAXIMA + ") (ou 'cancelar'): ");
            String entrada = lerLinha();
            if (cancelou(entrada)) { System.out.println("Alteração cancelada."); return; }
            int novo = converterInteiro(entrada, QTD_MAXIMA);
            if (novo >= 0) {
                estoquesMinimos[idx] = novo;
                System.out.println("\n[Sucesso] Estoque mínimo de '" + nomes[idx] + "' agora é " + novo + ".");
                return;
            }
            System.out.println("[Erro] Digite um número inteiro entre 0 e " + QTD_MAXIMA + ".");
        }
    }
}

package ui;

import dao.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import model.*;

public class MainConsole {
    
    private static DriverDAO driverDAO = new DriverDAO();
    private static ContractDAO contractDAO = new ContractDAO();
    private static TransactionDAO transactionDAO = new TransactionDAO();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=== SISTEMA DE TRANSPORTE ===");
            System.out.println("1. Gerenciar Motoristas");
            System.out.println("2. Gerenciar Contratos");
            System.out.println("3. Gerenciar Transações");
            System.out.println("4. Relatórios Financeiros");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            
            int opcao = scanner.nextInt();
            scanner.nextLine();
            
            switch (opcao) {
                case 1: menuMotoristas(); break;
                case 2: menuContratos(); break;
                case 3: menuTransacoes(); break;
                case 4: menuRelatorios(); break;
                case 0: 
                    DatabaseManager.closeConnection();
                    System.out.println("Saindo...");
                    return;
                default: System.out.println("Opção inválida!");
            }
        }
    }
    
    private static void menuMotoristas() {
        try {
            System.out.println("\n--- MOTORISTAS ---");
            System.out.println("1. Listar todos");
            System.out.println("2. Adicionar");
            System.out.println("3. Buscar por nome");
            System.out.println("4. Remover");
            System.out.print("Escolha: ");
            
            int op = scanner.nextInt();
            scanner.nextLine();
            
            if (op == 1) {
                ArrayList<Driver> drivers = driverDAO.listarTodos();
                if (drivers.isEmpty()) {
                    System.out.println("Nenhum motorista cadastrado.");
                } else {
                    for (Driver d : drivers) {
                        System.out.println(d.getCnh() + " | " + d.getName() + " | " + d.getPhone());
                    }
                }
            } else if (op == 2) {
                System.out.print("CNH: ");
                String cnh = scanner.nextLine();
                System.out.print("Nome: ");
                String name = scanner.nextLine();
                System.out.print("Telefone: ");
                String phone = scanner.nextLine();
                System.out.print("Email: ");
                String email = scanner.nextLine();
                driverDAO.salvar(new Driver(cnh, name, phone, email));
                System.out.println("✓ Motorista salvo com sucesso!");
            } else if (op == 3) {
                System.out.print("Nome (ou parte): ");
                String nome = scanner.nextLine();
                ArrayList<Driver> drivers = driverDAO.buscarPorNome(nome);
                if (drivers.isEmpty()) {
                    System.out.println("Nenhum motorista encontrado.");
                } else {
                    for (Driver d : drivers) {
                        System.out.println(d.getCnh() + " | " + d.getName() + " | " + d.getPhone());
                    }
                }
            } else if (op == 4) {
                System.out.print("CNH do motorista: ");
                String cnh = scanner.nextLine();
                driverDAO.deletar(cnh);
                System.out.println("✓ Motorista removido!");
            }
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private static void menuContratos() {
        try {
            System.out.println("\n--- CONTRATOS ---");
            System.out.println("1. Listar todos");
            System.out.println("2. Adicionar");
            System.out.println("3. Remover");
            System.out.print("Escolha: ");
            
            int op = scanner.nextInt();
            scanner.nextLine();
            
            if (op == 1) {
                ArrayList<Contract> contracts = contractDAO.listarTodos();
                if (contracts.isEmpty()) {
                    System.out.println("Nenhum contrato cadastrado.");
                } else {
                    for (Contract c : contracts) {
                        System.out.println(c.getId() + " | " + c.getContractNumber() + " | R$" + c.getValue() + " | " + c.getStartDate() + " a " + c.getEndDate());
                    }
                }
            } else if (op == 2) {
                System.out.print("Número do contrato: ");
                String number = scanner.nextLine();
                System.out.print("Descrição: ");
                String desc = scanner.nextLine();
                System.out.print("Valor: R$ ");
                double value = scanner.nextDouble();
                scanner.nextLine();
                System.out.print("Data início (YYYY-MM-DD): ");
                LocalDate start = LocalDate.parse(scanner.nextLine());
                System.out.print("Data fim (YYYY-MM-DD): ");
                LocalDate end = LocalDate.parse(scanner.nextLine());
                System.out.print("CNH do motorista: ");
                String cnh = scanner.nextLine();
                
                Contract contract = new Contract(number, desc, value, start, end, cnh);
                contractDAO.inserir(contract);
                System.out.println("✓ Contrato salvo! ID: " + contract.getId());
            } else if (op == 3) {
                System.out.print("Número do contrato: ");
                String number = scanner.nextLine();
                contractDAO.deletar(number);
                System.out.println("✓ Contrato removido!");
            }
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private static void menuTransacoes() {
        try {
            System.out.println("\n--- TRANSAÇÕES ---");
            System.out.println("1. Listar todas");
            System.out.println("2. Adicionar DESPESA");
            System.out.println("3. Adicionar RECEITA");
            System.out.print("Escolha: ");
            
            int op = scanner.nextInt();
            scanner.nextLine();
            
            if (op == 1) {
                ArrayList<Transaction> transactions = transactionDAO.listarTodas();
                if (transactions.isEmpty()) {
                    System.out.println("Nenhuma transação encontrada.");
                } else {
                    for (Transaction t : transactions) {
                        String tipo = "INCOME".equals(t.getType()) ? "💰 RECEITA" : "📉 DESPESA";
                        System.out.printf("%s | %s | %s: R$%.2f\n", 
                            t.getTimestamp().toLocalDate(), tipo, t.getDescription(), t.getAmount());
                    }
                }
                return;
            }
            
            System.out.print("CNH do motorista: ");
            String cnh = scanner.nextLine();
            System.out.print("Descrição: ");
            String desc = scanner.nextLine();
            System.out.print("Valor: R$ ");
            double value = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Número do contrato (opcional, Enter para pular): ");
            String contract = scanner.nextLine();
            if (contract.isEmpty()) contract = null;
            
            String type = (op == 2) ? "EXPENSE" : "INCOME";
            String tipoTexto = (op == 2) ? "DESPESA" : "RECEITA";
            
            Transaction trans = new Transaction(value, type, desc, contract, cnh);
            transactionDAO.inserir(trans);
            System.out.println("✓ " + tipoTexto + " salva com sucesso! ID: " + trans.getId());
        } catch (SQLException e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }
    
    private static void menuRelatorios() {
        try {
            System.out.println("\n--- RELATÓRIOS FINANCEIROS ---");
            System.out.println("1. Por mês/ano específico");
            System.out.println("2. Por ano inteiro");
            System.out.println("3. Por período (duas datas)");
            System.out.print("Escolha: ");
            
            int op = scanner.nextInt();
            scanner.nextLine();
            
            ArrayList<Transaction> transactions;
            
            if (op == 1) {
                System.out.print("Mês (1-12): ");
                int month = scanner.nextInt();
                System.out.print("Ano (ex: 2024): ");
                int year = scanner.nextInt();
                scanner.nextLine();
                transactions = transactionDAO.buscarPorMesAno(month, year);
                System.out.println("\n--- " + month + "/" + year + " ---");
            } else if (op == 2) {
                System.out.print("Ano (ex: 2024): ");
                int year = scanner.nextInt();
                scanner.nextLine();
                transactions = transactionDAO.buscarPorAno(year);
                System.out.println("\n--- ANO " + year + " ---");
            } else {
                System.out.print("Data início (YYYY-MM-DD): ");
                LocalDate start = LocalDate.parse(scanner.nextLine());
                System.out.print("Data fim (YYYY-MM-DD): ");
                LocalDate end = LocalDate.parse(scanner.nextLine());
                transactions = transactionDAO.buscarPorPeriodo(start, end);
                System.out.println("\n--- PERÍODO: " + start + " a " + end + " ---");
            }
            
            if (transactions.isEmpty()) {
                System.out.println("Nenhuma transação encontrada no período.");
                return;
            }
            
            double totalIncome = 0, totalExpense = 0;
            System.out.println("\n--- DETALHES ---");
            for (Transaction t : transactions) {
                if ("INCOME".equals(t.getType())) {
                    totalIncome += t.getAmount();
                    System.out.printf("💰 RECEITA | %s | %s | R$%.2f\n", 
                        t.getTimestamp().toLocalDate(), t.getDescription(), t.getAmount());
                } else {
                    totalExpense += t.getAmount();
                    System.out.printf("📉 DESPESA | %s | %s | R$%.2f\n", 
                        t.getTimestamp().toLocalDate(), t.getDescription(), t.getAmount());
                }
            }
            
            System.out.println("\n=== RESUMO DO PERÍODO ===");
            System.out.printf("💰 Total de RECEITAS: R$ %.2f\n", totalIncome);
            System.out.printf("📉 Total de DESPESAS: R$ %.2f\n", totalExpense);
            System.out.printf("📊 SALDO FINAL: R$ %.2f\n", totalIncome - totalExpense);
            
            if ((totalIncome - totalExpense) > 0) {
                System.out.println("🎉 Saldo POSITIVO!");
            } else if ((totalIncome - totalExpense) < 0) {
                System.out.println("⚠️ Saldo NEGATIVO!");
            } else {
                System.out.println("⚖️ Saldo NEUTRO");
            }
            
        } catch (SQLException e) {
            System.err.println("Erro ao gerar relatório: " + e.getMessage());
        }
    }
}
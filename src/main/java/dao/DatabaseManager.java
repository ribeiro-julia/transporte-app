package dao;

import java.sql.*;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:transport.db";
    private static Connection connection = null;

    private DatabaseManager() {}

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("✓ Driver SQLite carregado com sucesso!");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ ERRO: Driver SQLite não encontrado!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DATABASE_URL);
            criarTabelas();
        }
        return connection;
    }

    private static void criarTabelas() {
        String sqlDriver = """
            CREATE TABLE IF NOT EXISTS drivers (
                cnh VARCHAR(20) PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                phone VARCHAR(20),
                email VARCHAR(100)
            )
        """;

        String sqlContract = """
            CREATE TABLE IF NOT EXISTS contracts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                contract_number VARCHAR(50) NOT NULL UNIQUE,
                description TEXT,
                value REAL NOT NULL,
                start_date DATE NOT NULL,
                end_date DATE NOT NULL,
                driver_cnh VARCHAR(20) NOT NULL,
                FOREIGN KEY (driver_cnh) REFERENCES drivers(cnh) ON DELETE CASCADE
            )
        """;

        String sqlTransaction = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp DATETIME NOT NULL,
                amount REAL NOT NULL,
                type VARCHAR(10) NOT NULL,
                description TEXT,
                contract_number VARCHAR(50),
                driver_cnh VARCHAR(20) NOT NULL,
                FOREIGN KEY (driver_cnh) REFERENCES drivers(cnh) ON DELETE CASCADE,
                FOREIGN KEY (contract_number) REFERENCES contracts(contract_number)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlDriver);
            stmt.execute(sqlContract);
            stmt.execute(sqlTransaction);
            System.out.println("✓ Tabelas verificadas/criadas com sucesso!");
        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Conexão fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
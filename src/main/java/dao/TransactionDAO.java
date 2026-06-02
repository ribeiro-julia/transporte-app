package dao;

import model.Transaction;
import model.Driver;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class TransactionDAO {

    // CREATE - Inserir nova transação
    public void inserir(Transaction transaction) throws SQLException {
        String sql = """
            INSERT INTO transactions (timestamp, amount, type, description, contract_number, driver_cnh) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        pstmt.setString(1, transaction.getTimestamp().toString());
        pstmt.setDouble(2, transaction.getAmount());
        pstmt.setString(3, transaction.getType());
        pstmt.setString(4, transaction.getDescription());
        pstmt.setString(5, transaction.getContractNumber());
        pstmt.setString(6, transaction.getDriverCnh());
        pstmt.executeUpdate();
        
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            transaction.setId(rs.getInt(1));
        }
    }

    // READ - Listar todas as transações
    public ArrayList<Transaction> listarTodas() throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY timestamp DESC";
        
        Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        DriverDAO driverDAO = new DriverDAO();
        
        while (rs.next()) {
            Transaction trans = new Transaction(
                rs.getInt("id"),
                LocalDateTime.parse(rs.getString("timestamp")),
                rs.getDouble("amount"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("contract_number"),
                rs.getString("driver_cnh")
            );
            Driver driver = driverDAO.buscarPorCnh(trans.getDriverCnh());
            trans.setDriver(driver);
            transactions.add(trans);
        }
        
        return transactions;
    }

    // READ - Buscar transações por motorista
    public ArrayList<Transaction> buscarPorDriver(String driverCnh) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE driver_cnh = ? ORDER BY timestamp DESC";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, driverCnh);
        ResultSet rs = pstmt.executeQuery();
        
        DriverDAO driverDAO = new DriverDAO();
        Driver driver = driverDAO.buscarPorCnh(driverCnh);
        
        while (rs.next()) {
            Transaction trans = new Transaction(
                rs.getInt("id"),
                LocalDateTime.parse(rs.getString("timestamp")),
                rs.getDouble("amount"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("contract_number"),
                rs.getString("driver_cnh")
            );
            trans.setDriver(driver);
            transactions.add(trans);
        }
        
        return transactions;
    }

    // READ - Buscar transações por período
    public ArrayList<Transaction> buscarPorPeriodo(LocalDate startDate, LocalDate endDate) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE DATE(timestamp) BETWEEN ? AND ? ORDER BY timestamp DESC";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, startDate.toString());
        pstmt.setString(2, endDate.toString());
        ResultSet rs = pstmt.executeQuery();
        
        DriverDAO driverDAO = new DriverDAO();
        
        while (rs.next()) {
            Transaction trans = new Transaction(
                rs.getInt("id"),
                LocalDateTime.parse(rs.getString("timestamp")),
                rs.getDouble("amount"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("contract_number"),
                rs.getString("driver_cnh")
            );
            Driver driver = driverDAO.buscarPorCnh(trans.getDriverCnh());
            trans.setDriver(driver);
            transactions.add(trans);
        }
        
        return transactions;
    }

    // READ - Buscar transações por mês e ano
    public ArrayList<Transaction> buscarPorMesAno(int month, int year) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = """
            SELECT * FROM transactions 
            WHERE strftime('%m', timestamp) = ? AND strftime('%Y', timestamp) = ? 
            ORDER BY timestamp DESC
        """;
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        String monthStr = String.format("%02d", month);
        pstmt.setString(1, monthStr);
        pstmt.setString(2, String.valueOf(year));
        ResultSet rs = pstmt.executeQuery();
        
        DriverDAO driverDAO = new DriverDAO();
        
        while (rs.next()) {
            Transaction trans = new Transaction(
                rs.getInt("id"),
                LocalDateTime.parse(rs.getString("timestamp")),
                rs.getDouble("amount"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("contract_number"),
                rs.getString("driver_cnh")
            );
            Driver driver = driverDAO.buscarPorCnh(trans.getDriverCnh());
            trans.setDriver(driver);
            transactions.add(trans);
        }
        
        return transactions;
    }

    // READ - Buscar transações por ano
    public ArrayList<Transaction> buscarPorAno(int year) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE strftime('%Y', timestamp) = ? ORDER BY timestamp DESC";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, String.valueOf(year));
        ResultSet rs = pstmt.executeQuery();
        
        DriverDAO driverDAO = new DriverDAO();
        
        while (rs.next()) {
            Transaction trans = new Transaction(
                rs.getInt("id"),
                LocalDateTime.parse(rs.getString("timestamp")),
                rs.getDouble("amount"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getString("contract_number"),
                rs.getString("driver_cnh")
            );
            Driver driver = driverDAO.buscarPorCnh(trans.getDriverCnh());
            trans.setDriver(driver);
            transactions.add(trans);
        }
        
        return transactions;
    }

    // DELETE - Remover transação
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    // READ - Calcular total por período
    public double calcularTotalPorPeriodo(LocalDate startDate, LocalDate endDate, String type) throws SQLException {
        String sql = "SELECT SUM(amount) as total FROM transactions WHERE DATE(timestamp) BETWEEN ? AND ? AND type = ?";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, startDate.toString());
        pstmt.setString(2, endDate.toString());
        pstmt.setString(3, type);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return rs.getDouble("total");
        }
        return 0;
    }
}
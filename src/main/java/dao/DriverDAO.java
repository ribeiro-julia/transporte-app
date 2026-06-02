package dao;

import model.Driver;
import java.sql.*;
import java.util.ArrayList;

public class DriverDAO {

    // CREATE - Salvar ou atualizar driver
    public void salvar(Driver driver) throws SQLException {
        String sql = "INSERT OR REPLACE INTO drivers (cnh, name, phone, email) VALUES (?, ?, ?, ?)";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, driver.getCnh());
        pstmt.setString(2, driver.getName());
        pstmt.setString(3, driver.getPhone());
        pstmt.setString(4, driver.getEmail());
        pstmt.executeUpdate();
        
        // NÃO feche nada aqui!
    }

    // READ - Buscar driver por CNH
    public Driver buscarPorCnh(String cnh) throws SQLException {
        String sql = "SELECT * FROM drivers WHERE cnh = ?";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, cnh);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            return new Driver(
                rs.getString("cnh"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email")
            );
        }
        return null;
    }

    // READ - Listar todos os drivers
    public ArrayList<Driver> listarTodos() throws SQLException {
        ArrayList<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers ORDER BY name";
        
        Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            Driver driver = new Driver(
                rs.getString("cnh"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email")
            );
            drivers.add(driver);
        }
        
        return drivers;
    }

    // READ - Buscar drivers por nome (like)
    public ArrayList<Driver> buscarPorNome(String nome) throws SQLException {
        ArrayList<Driver> drivers = new ArrayList<>();
        String sql = "SELECT * FROM drivers WHERE name LIKE ? ORDER BY name";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, "%" + nome + "%");
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            Driver driver = new Driver(
                rs.getString("cnh"),
                rs.getString("name"),
                rs.getString("phone"),
                rs.getString("email")
            );
            drivers.add(driver);
        }
        
        return drivers;
    }

    // DELETE - Remover driver
    public void deletar(String cnh) throws SQLException {
        String sql = "DELETE FROM drivers WHERE cnh = ?";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, cnh);
        pstmt.executeUpdate();
    }

    // Verificar se driver existe
    public boolean existe(String cnh) throws SQLException {
        String sql = "SELECT 1 FROM drivers WHERE cnh = ? LIMIT 1";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, cnh);
        ResultSet rs = pstmt.executeQuery();
        
        return rs.next();
    }
}
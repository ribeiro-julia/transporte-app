package dao;

import model.Contract;
import model.Driver;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ContractDAO {

    // CREATE - Inserir novo contrato
    public void inserir(Contract contract) throws SQLException {
        String sql = """
            INSERT INTO contracts (contract_number, description, value, start_date, end_date, driver_cnh) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        pstmt.setString(1, contract.getContractNumber());
        pstmt.setString(2, contract.getDescription());
        pstmt.setDouble(3, contract.getValue());
        pstmt.setString(4, contract.getStartDate().toString());
        pstmt.setString(5, contract.getEndDate().toString());
        pstmt.setString(6, contract.getDriverCnh());
        pstmt.executeUpdate();
        
        ResultSet rs = pstmt.getGeneratedKeys();
        if (rs.next()) {
            contract.setId(rs.getInt(1));
        }
    }

    // UPDATE - Atualizar contrato
    public void atualizar(Contract contract) throws SQLException {
        String sql = """
            UPDATE contracts SET description = ?, value = ?, start_date = ?, end_date = ?, driver_cnh = ?
            WHERE contract_number = ?
        """;
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        pstmt.setString(1, contract.getDescription());
        pstmt.setDouble(2, contract.getValue());
        pstmt.setString(3, contract.getStartDate().toString());
        pstmt.setString(4, contract.getEndDate().toString());
        pstmt.setString(5, contract.getDriverCnh());
        pstmt.setString(6, contract.getContractNumber());
        pstmt.executeUpdate();
    }

    // READ - Buscar contrato por número
    public Contract buscarPorNumero(String contractNumber) throws SQLException {
        String sql = "SELECT * FROM contracts WHERE contract_number = ?";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, contractNumber);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            DriverDAO driverDAO = new DriverDAO();
            Contract contract = new Contract(
                rs.getInt("id"),
                rs.getString("contract_number"),
                rs.getString("description"),
                rs.getDouble("value"),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("end_date")),
                rs.getString("driver_cnh")
            );
            Driver driver = driverDAO.buscarPorCnh(contract.getDriverCnh());
            contract.setDriver(driver);
            return contract;
        }
        return null;
    }

    // READ - Listar todos os contratos
    public ArrayList<Contract> listarTodos() throws SQLException {
        ArrayList<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts ORDER BY start_date DESC";
        
        Connection conn = DatabaseManager.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        DriverDAO driverDAO = new DriverDAO();
        
        while (rs.next()) {
            Contract contract = new Contract(
                rs.getInt("id"),
                rs.getString("contract_number"),
                rs.getString("description"),
                rs.getDouble("value"),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("end_date")),
                rs.getString("driver_cnh")
            );
            Driver driver = driverDAO.buscarPorCnh(contract.getDriverCnh());
            contract.setDriver(driver);
            contracts.add(contract);
        }
        
        return contracts;
    }

    // READ - Buscar contratos por motorista
    public ArrayList<Contract> buscarPorDriver(String driverCnh) throws SQLException {
        ArrayList<Contract> contracts = new ArrayList<>();
        String sql = "SELECT * FROM contracts WHERE driver_cnh = ? ORDER BY start_date DESC";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, driverCnh);
        ResultSet rs = pstmt.executeQuery();
        
        DriverDAO driverDAO = new DriverDAO();
        Driver driver = driverDAO.buscarPorCnh(driverCnh);
        
        while (rs.next()) {
            Contract contract = new Contract(
                rs.getInt("id"),
                rs.getString("contract_number"),
                rs.getString("description"),
                rs.getDouble("value"),
                LocalDate.parse(rs.getString("start_date")),
                LocalDate.parse(rs.getString("end_date")),
                rs.getString("driver_cnh")
            );
            contract.setDriver(driver);
            contracts.add(contract);
        }
        
        return contracts;
    }

    // DELETE - Remover contrato
    public void deletar(String contractNumber) throws SQLException {
        String sql = "DELETE FROM contracts WHERE contract_number = ?";
        
        Connection conn = DatabaseManager.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, contractNumber);
        pstmt.executeUpdate();
    }
}
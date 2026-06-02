package model;

import java.time.LocalDateTime;

public class Transaction {
    private int id;
    private LocalDateTime timestamp;
    private double amount;
    private String type; // "INCOME" ou "EXPENSE"
    private String description;
    private String contractNumber;
    private String driverCnh;
    private Driver driver;

    public Transaction(double amount, String type, String description, 
                       String contractNumber, String driverCnh) {
        this.timestamp = LocalDateTime.now();
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.contractNumber = contractNumber;
        this.driverCnh = driverCnh;
    }

    public Transaction(int id, LocalDateTime timestamp, double amount, String type,
                       String description, String contractNumber, String driverCnh) {
        this.id = id;
        this.timestamp = timestamp;
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.contractNumber = contractNumber;
        this.driverCnh = driverCnh;
    }

    // Getters
    public int getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public double getAmount() { return amount; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getContractNumber() { return contractNumber; }
    public String getDriverCnh() { return driverCnh; }
    public Driver getDriver() { return driver; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public int getDay() { return timestamp.getDayOfMonth(); }
    public int getMonth() { return timestamp.getMonthValue(); }
    public int getYear() { return timestamp.getYear(); }

    @Override
    public String toString() {
        return String.format("%s - R$%.2f - %s", type, amount, description);
    }
}

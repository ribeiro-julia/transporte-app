package model;

import java.time.LocalDate;

public class Contract {
    private int id;
    private String contractNumber;
    private String description;
    private double value;
    private LocalDate startDate;
    private LocalDate endDate;
    private String driverCnh;
    private Driver driver;

    public Contract(String contractNumber, String description, double value, 
                    LocalDate startDate, LocalDate endDate, String driverCnh) {
        this.contractNumber = contractNumber;
        this.description = description;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driverCnh = driverCnh;
    }

    public Contract(int id, String contractNumber, String description, double value,
                    LocalDate startDate, LocalDate endDate, String driverCnh) {
        this.id = id;
        this.contractNumber = contractNumber;
        this.description = description;
        this.value = value;
        this.startDate = startDate;
        this.endDate = endDate;
        this.driverCnh = driverCnh;
    }

    // Getters
    public int getId() { return id; }
    public String getContractNumber() { return contractNumber; }
    public String getDescription() { return description; }
    public double getValue() { return value; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getDriverCnh() { return driverCnh; }
    public Driver getDriver() { return driver; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDriver(Driver driver) { this.driver = driver; }
    public void setValue(double value) { this.value = value; }

    @Override
    public String toString() {
        return contractNumber + " - R$" + value;
    }
}
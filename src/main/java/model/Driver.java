package model;

import java.util.ArrayList;

public class Driver {
    private String cnh;
    private String name;
    private String phone;
    private String email;
    private ArrayList<Contract> contracts;
    private ArrayList<Transaction> transactions;

    public Driver(String cnh, String name, String phone, String email) {
        this.cnh = cnh;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.contracts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public Driver(Driver copy) {
        this.cnh = copy.cnh;
        this.name = copy.name;
        this.phone = copy.phone;
        this.email = copy.email;
        this.contracts = new ArrayList<>(copy.contracts);
        this.transactions = new ArrayList<>(copy.transactions);
    }

    // Getters
    public String getCnh() { return cnh; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public ArrayList<Contract> getContracts() { return new ArrayList<>(contracts); }
    public ArrayList<Transaction> getTransactions() { return new ArrayList<>(transactions); }

    // Setters
    public void setCnh(String cnh) { this.cnh = cnh; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    public void addContract(Contract contract) { this.contracts.add(contract); }
    public void addTransaction(Transaction transaction) { this.transactions.add(transaction); }

    @Override
    public String toString() {
        return name + " (" + cnh + ")";
    }
}
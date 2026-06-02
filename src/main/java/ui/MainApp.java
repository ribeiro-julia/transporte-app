package ui;

import dao.*;
import model.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.chart.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MainApp extends Application {
    
    private DriverDAO driverDAO = new DriverDAO();
    private ContractDAO contractDAO = new ContractDAO();
    private TransactionDAO transactionDAO = new TransactionDAO();
    
    private ObservableList<Driver> driversList = FXCollections.observableArrayList();
    private ObservableList<Contract> contractsList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionsList = FXCollections.observableArrayList();
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Gerenciamento de Transporte");
        
        // Carregar dados
        carregarDrivers();
        carregarContracts();
        carregarTransactions();
        
        // Criar abas
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            criarTabDrivers(),
            criarTabContracts(),
            criarTabTransactions(),
            criarTabRelatorios()
        );
        
        VBox root = new VBox(10, tabPane);
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #2c3e50, #34495e);");
        
        Scene scene = new Scene(root, 1300, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private void carregarDrivers() {
        try {
            driversList.clear();
            driversList.addAll(driverDAO.listarTodos());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar motoristas", e.getMessage());
        }
    }
    
    private void carregarContracts() {
        try {
            contractsList.clear();
            contractsList.addAll(contractDAO.listarTodos());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar contratos", e.getMessage());
        }
    }
    
    private void carregarTransactions() {
        try {
            transactionsList.clear();
            transactionsList.addAll(transactionDAO.listarTodas());
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar transações", e.getMessage());
        }
    }
    
    // ==================== TAB MOTORISTAS ====================
    
    private Tab criarTabDrivers() {
        Tab tab = new Tab("🚛 Motoristas");
        tab.setClosable(false);
        
        // Tabela
        TableView<Driver> tableView = new TableView<>();
        tableView.setItems(driversList);
        tableView.setStyle("-fx-font-size: 13px;");
        
        TableColumn<Driver, String> colCnh = new TableColumn<>("CNH");
        colCnh.setCellValueFactory(new PropertyValueFactory<>("cnh"));
        colCnh.setPrefWidth(150);
        
        TableColumn<Driver, String> colName = new TableColumn<>("Nome");
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colName.setPrefWidth(200);
        
        TableColumn<Driver, String> colPhone = new TableColumn<>("Telefone");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setPrefWidth(150);
        
        TableColumn<Driver, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(250);
        
        tableView.getColumns().addAll(colCnh, colName, colPhone, colEmail);
        
        // Barra de pesquisa
        TextField searchField = new TextField();
        searchField.setPromptText("Pesquisar por nome...");
        searchField.setStyle("-fx-font-size: 13px;");
        
        Button searchBtn = new Button("🔍 Buscar");
        searchBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        searchBtn.setOnAction(e -> buscarDrivers(searchField.getText()));
        
        Button clearBtn = new Button("🔄 Limpar");
        clearBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        clearBtn.setOnAction(e -> carregarDrivers());
        
        HBox searchBox = new HBox(10, searchField, searchBtn, clearBtn);
        
        // Botões de ação
        Button addBtn = new Button("➕ Adicionar");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> adicionarDriver());
        
        Button editBtn = new Button("✏️ Editar");
        editBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        editBtn.setOnAction(e -> editarDriver(tableView.getSelectionModel().getSelectedItem()));
        
        Button deleteBtn = new Button("🗑️ Remover");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> removerDriver(tableView.getSelectionModel().getSelectedItem()));
        
        HBox buttonBox = new HBox(10, addBtn, editBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(10, searchBox, tableView, buttonBox);
        vbox.setPadding(new Insets(10));
        
        tab.setContent(vbox);
        return tab;
    }
    
    private void buscarDrivers(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            carregarDrivers();
            return;
        }
        try {
            driversList.clear();
            driversList.addAll(driverDAO.buscarPorNome(nome));
        } catch (SQLException e) {
            mostrarErro("Erro na busca", e.getMessage());
        }
    }
    
    private void adicionarDriver() {
        Dialog<Driver> dialog = criarDialogDriver(null);
        dialog.showAndWait().ifPresent(driver -> {
            try {
                driverDAO.salvar(driver);
                carregarDrivers();
                mostrarInfo("Sucesso", "Motorista adicionado com sucesso!");
            } catch (SQLException e) {
                mostrarErro("Erro ao salvar", e.getMessage());
            }
        });
    }
    
    private void editarDriver(Driver driver) {
        if (driver == null) {
            mostrarInfo("Aviso", "Selecione um motorista para editar.");
            return;
        }
        Dialog<Driver> dialog = criarDialogDriver(driver);
        dialog.showAndWait().ifPresent(driverEditado -> {
            try {
                driverDAO.salvar(driverEditado);
                carregarDrivers();
                mostrarInfo("Sucesso", "Motorista atualizado com sucesso!");
            } catch (SQLException e) {
                mostrarErro("Erro ao atualizar", e.getMessage());
            }
        });
    }
    
    private void removerDriver(Driver driver) {
        if (driver == null) {
            mostrarInfo("Aviso", "Selecione um motorista para remover.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("Remover motorista");
        alert.setContentText("Tem certeza que deseja remover " + driver.getName() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    driverDAO.deletar(driver.getCnh());
                    carregarDrivers();
                    mostrarInfo("Sucesso", "Motorista removido!");
                } catch (SQLException e) {
                    mostrarErro("Erro ao remover", e.getMessage());
                }
            }
        });
    }
    
    private Dialog<Driver> criarDialogDriver(Driver driver) {
        Dialog<Driver> dialog = new Dialog<>();
        dialog.setTitle(driver == null ? "Novo Motorista" : "Editar Motorista");
        dialog.initModality(Modality.WINDOW_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField cnhField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        
        if (driver != null) {
            cnhField.setText(driver.getCnh());
            cnhField.setEditable(false);
            nameField.setText(driver.getName());
            phoneField.setText(driver.getPhone());
            emailField.setText(driver.getEmail());
        }
        
        grid.add(new Label("CNH:"), 0, 0);
        grid.add(cnhField, 1, 0);
        grid.add(new Label("Nome:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Telefone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType btnType = new ButtonType(driver == null ? "Adicionar" : "Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnType, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == btnType) {
                return new Driver(cnhField.getText(), nameField.getText(), phoneField.getText(), emailField.getText());
            }
            return null;
        });
        
        return dialog;
    }
    
    // ==================== TAB CONTRATOS ====================
    
    private Tab criarTabContracts() {
        Tab tab = new Tab("📄 Contratos");
        tab.setClosable(false);
        
        TableView<Contract> tableView = new TableView<>();
        tableView.setItems(contractsList);
        
        TableColumn<Contract, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);
        
        TableColumn<Contract, String> colNumber = new TableColumn<>("Número");
        colNumber.setCellValueFactory(new PropertyValueFactory<>("contractNumber"));
        colNumber.setPrefWidth(150);
        
        TableColumn<Contract, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(200);
        
        TableColumn<Contract, Double> colValue = new TableColumn<>("Valor (R$)");
        colValue.setCellValueFactory(new PropertyValueFactory<>("value"));
        colValue.setPrefWidth(100);
        
        TableColumn<Contract, LocalDate> colStart = new TableColumn<>("Início");
        colStart.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colStart.setPrefWidth(100);
        
        TableColumn<Contract, LocalDate> colEnd = new TableColumn<>("Fim");
        colEnd.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        colEnd.setPrefWidth(100);
        
        TableColumn<Contract, String> colDriver = new TableColumn<>("Motorista");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        colDriver.setPrefWidth(150);
        
        tableView.getColumns().addAll(colId, colNumber, colDesc, colValue, colStart, colEnd, colDriver);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Pesquisar por número do contrato...");
        
        Button searchBtn = new Button("🔍 Buscar");
        searchBtn.setOnAction(e -> buscarContracts(searchField.getText()));
        
        Button clearBtn = new Button("🔄 Limpar");
        clearBtn.setOnAction(e -> carregarContracts());
        
        HBox searchBox = new HBox(10, searchField, searchBtn, clearBtn);
        
        Button addBtn = new Button("➕ Adicionar Contrato");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> adicionarContract());
        
        Button deleteBtn = new Button("🗑️ Remover");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> removerContract(tableView.getSelectionModel().getSelectedItem()));
        
        HBox buttonBox = new HBox(10, addBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(10, searchBox, tableView, buttonBox);
        vbox.setPadding(new Insets(10));
        
        tab.setContent(vbox);
        return tab;
    }
    
    private void buscarContracts(String numero) {
        // Implementação simplificada
        if (numero == null || numero.trim().isEmpty()) {
            carregarContracts();
        }
    }
    
    private void adicionarContract() {
        Dialog<Contract> dialog = criarDialogContract(null);
        dialog.showAndWait().ifPresent(contract -> {
            try {
                contractDAO.inserir(contract);
                carregarContracts();
                mostrarInfo("Sucesso", "Contrato adicionado com sucesso!");
            } catch (SQLException e) {
                mostrarErro("Erro ao salvar", e.getMessage());
            }
        });
    }
    
    private void removerContract(Contract contract) {
        if (contract == null) {
            mostrarInfo("Aviso", "Selecione um contrato para remover.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("Remover contrato");
        alert.setContentText("Tem certeza que deseja remover o contrato " + contract.getContractNumber() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    contractDAO.deletar(contract.getContractNumber());
                    carregarContracts();
                    mostrarInfo("Sucesso", "Contrato removido!");
                } catch (SQLException e) {
                    mostrarErro("Erro ao remover", e.getMessage());
                }
            }
        });
    }
    
    private Dialog<Contract> criarDialogContract(Contract contract) {
        Dialog<Contract> dialog = new Dialog<>();
        dialog.setTitle(contract == null ? "Novo Contrato" : "Editar Contrato");
        dialog.initModality(Modality.WINDOW_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField numberField = new TextField();
        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(3);
        TextField valueField = new TextField();
        DatePicker startPicker = new DatePicker();
        DatePicker endPicker = new DatePicker();
        ComboBox<Driver> driverCombo = new ComboBox<>();
        
        try {
            driverCombo.setItems(FXCollections.observableArrayList(driverDAO.listarTodos()));
        } catch (SQLException e) {
            mostrarErro("Erro", e.getMessage());
        }
        
        if (contract != null) {
            numberField.setText(contract.getContractNumber());
            numberField.setEditable(false);
            descArea.setText(contract.getDescription());
            valueField.setText(String.valueOf(contract.getValue()));
            startPicker.setValue(contract.getStartDate());
            endPicker.setValue(contract.getEndDate());
        }
        
        grid.add(new Label("Número do Contrato:"), 0, 0);
        grid.add(numberField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(new Label("Valor (R$):"), 0, 2);
        grid.add(valueField, 1, 2);
        grid.add(new Label("Data Início:"), 0, 3);
        grid.add(startPicker, 1, 3);
        grid.add(new Label("Data Fim:"), 0, 4);
        grid.add(endPicker, 1, 4);
        grid.add(new Label("Motorista:"), 0, 5);
        grid.add(driverCombo, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType btnType = new ButtonType(contract == null ? "Adicionar" : "Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnType, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == btnType) {
                double value = Double.parseDouble(valueField.getText());
                Driver selected = driverCombo.getSelectionModel().getSelectedItem();
                return new Contract(
                    numberField.getText(),
                    descArea.getText(),
                    value,
                    startPicker.getValue(),
                    endPicker.getValue(),
                    selected.getCnh()
                );
            }
            return null;
        });
        
        return dialog;
    }
    
    // ==================== TAB TRANSAÇÕES ====================
    
    private Tab criarTabTransactions() {
        Tab tab = new Tab("💰 Transações");
        tab.setClosable(false);
        
        TableView<Transaction> tableView = new TableView<>();
        tableView.setItems(transactionsList);
        
        TableColumn<Transaction, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(60);
        
        TableColumn<Transaction, LocalDate> colDate = new TableColumn<>("Data");
        colDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colDate.setPrefWidth(100);
        
        TableColumn<Transaction, String> colType = new TableColumn<>("Tipo");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(100);
        
        TableColumn<Transaction, Double> colAmount = new TableColumn<>("Valor (R$)");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(100);
        
        TableColumn<Transaction, String> colDesc = new TableColumn<>("Descrição");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDesc.setPrefWidth(200);
        
        TableColumn<Transaction, String> colDriver = new TableColumn<>("Motorista");
        colDriver.setCellValueFactory(new PropertyValueFactory<>("driver"));
        colDriver.setPrefWidth(150);
        
        tableView.getColumns().addAll(colId, colDate, colType, colAmount, colDesc, colDriver);
        
        Button addIncomeBtn = new Button("💰 Adicionar Receita");
        addIncomeBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        addIncomeBtn.setOnAction(e -> adicionarTransacao("INCOME"));
        
        Button addExpenseBtn = new Button("📉 Adicionar Despesa");
        addExpenseBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        addExpenseBtn.setOnAction(e -> adicionarTransacao("EXPENSE"));
        
        Button deleteBtn = new Button("🗑️ Remover");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> removerTransacao(tableView.getSelectionModel().getSelectedItem()));
        
        HBox buttonBox = new HBox(10, addIncomeBtn, addExpenseBtn, deleteBtn);
        buttonBox.setAlignment(Pos.CENTER);
        
        VBox vbox = new VBox(10, tableView, buttonBox);
        vbox.setPadding(new Insets(10));
        
        tab.setContent(vbox);
        return tab;
    }
    
    private void adicionarTransacao(String type) {
        Dialog<Transaction> dialog = criarDialogTransaction(type);
        dialog.showAndWait().ifPresent(transaction -> {
            try {
                transactionDAO.inserir(transaction);
                carregarTransactions();
                mostrarInfo("Sucesso", type.equals("INCOME") ? "Receita adicionada!" : "Despesa adicionada!");
            } catch (SQLException e) {
                mostrarErro("Erro ao salvar", e.getMessage());
            }
        });
    }
    
    private void removerTransacao(Transaction transaction) {
        if (transaction == null) {
            mostrarInfo("Aviso", "Selecione uma transação para remover.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar");
        alert.setHeaderText("Remover transação");
        alert.setContentText("Tem certeza que deseja remover esta transação?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    transactionDAO.deletar(transaction.getId());
                    carregarTransactions();
                    mostrarInfo("Sucesso", "Transação removida!");
                } catch (SQLException e) {
                    mostrarErro("Erro ao remover", e.getMessage());
                }
            }
        });
    }
    
    private Dialog<Transaction> criarDialogTransaction(String type) {
        Dialog<Transaction> dialog = new Dialog<>();
        dialog.setTitle(type.equals("INCOME") ? "Nova Receita" : "Nova Despesa");
        dialog.initModality(Modality.WINDOW_MODAL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField amountField = new TextField();
        TextArea descArea = new TextArea();
        descArea.setPrefRowCount(3);
        TextField contractField = new TextField();
        contractField.setPromptText("Opcional");
        ComboBox<Driver> driverCombo = new ComboBox<>();
        
        try {
            driverCombo.setItems(FXCollections.observableArrayList(driverDAO.listarTodos()));
        } catch (SQLException e) {
            mostrarErro("Erro", e.getMessage());
        }
        
        grid.add(new Label("Valor (R$):"), 0, 0);
        grid.add(amountField, 1, 0);
        grid.add(new Label("Descrição:"), 0, 1);
        grid.add(descArea, 1, 1);
        grid.add(new Label("Contrato (opcional):"), 0, 2);
        grid.add(contractField, 1, 2);
        grid.add(new Label("Motorista:"), 0, 3);
        grid.add(driverCombo, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType btnType = new ButtonType("Adicionar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnType, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == btnType) {
                double amount = Double.parseDouble(amountField.getText());
                Driver selected = driverCombo.getSelectionModel().getSelectedItem();
                String contract = contractField.getText().isEmpty() ? null : contractField.getText();
                return new Transaction(amount, type, descArea.getText(), contract, selected.getCnh());
            }
            return null;
        });
        
        return dialog;
    }
    
    // ==================== TAB RELATÓRIOS ====================
    
    private Tab criarTabRelatorios() {
        Tab tab = new Tab("📊 Relatórios");
        tab.setClosable(false);
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        
        // Opções de período
        ToggleGroup periodGroup = new ToggleGroup();
        RadioButton rbMonth = new RadioButton("Mês/Ano");
        RadioButton rbYear = new RadioButton("Ano");
        RadioButton rbPeriod = new RadioButton("Período específico");
        rbMonth.setToggleGroup(periodGroup);
        rbYear.setToggleGroup(periodGroup);
        rbPeriod.setToggleGroup(periodGroup);
        rbMonth.setSelected(true);
        
        HBox periodBox = new HBox(20, rbMonth, rbYear, rbPeriod);
        
        // Campos para período
        ComboBox<Integer> monthCombo = new ComboBox<>();
        for (int i = 1; i <= 12; i++) monthCombo.getItems().add(i);
        monthCombo.setValue(LocalDate.now().getMonthValue());
        
        ComboBox<Integer> yearCombo = new ComboBox<>();
        for (int i = 2020; i <= 2030; i++) yearCombo.getItems().add(i);
        yearCombo.setValue(LocalDate.now().getYear());
        
        ComboBox<Integer> yearCombo2 = new ComboBox<>();
        for (int i = 2020; i <= 2030; i++) yearCombo2.getItems().add(i);
        yearCombo2.setValue(LocalDate.now().getYear());
        
        DatePicker startPicker = new DatePicker(LocalDate.now().withDayOfMonth(1));
        DatePicker endPicker = new DatePicker(LocalDate.now());
        
        HBox monthBox = new HBox(10, new Label("Mês:"), monthCombo, new Label("Ano:"), yearCombo);
        HBox yearBox = new HBox(10, new Label("Ano:"), yearCombo2);
        HBox periodBoxFields = new HBox(10, new Label("De:"), startPicker, new Label("Até:"), endPicker);
        
        monthBox.setVisible(true);
        yearBox.setVisible(false);
        periodBoxFields.setVisible(false);
        
        rbMonth.selectedProperty().addListener((obs, old, val) -> {
            monthBox.setVisible(val);
            yearBox.setVisible(false);
            periodBoxFields.setVisible(false);
        });
        rbYear.selectedProperty().addListener((obs, old, val) -> {
            monthBox.setVisible(false);
            yearBox.setVisible(val);
            periodBoxFields.setVisible(false);
        });
        rbPeriod.selectedProperty().addListener((obs, old, val) -> {
            monthBox.setVisible(false);
            yearBox.setVisible(false);
            periodBoxFields.setVisible(val);
        });
        
        Button generateBtn = new Button("Gerar Relatório");
        generateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        
        // Área de resultados
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(200);
        
        // Gráfico de barras
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Receitas vs Despesas");
        barChart.setPrefHeight(250);
        
        generateBtn.setOnAction(e -> {
            try {
                ArrayList<Transaction> transactions = new ArrayList<>();
                
                if (rbMonth.isSelected()) {
                    transactions = transactionDAO.buscarPorMesAno(monthCombo.getValue(), yearCombo.getValue());
                    resultArea.setText("Relatório de " + monthCombo.getValue() + "/" + yearCombo.getValue() + "\n\n");
                } else if (rbYear.isSelected()) {
                    transactions = transactionDAO.buscarPorAno(yearCombo2.getValue());
                    resultArea.setText("Relatório do ano " + yearCombo2.getValue() + "\n\n");
                } else {
                    transactions = transactionDAO.buscarPorPeriodo(startPicker.getValue(), endPicker.getValue());
                    resultArea.setText("Relatório de " + startPicker.getValue() + " até " + endPicker.getValue() + "\n\n");
                }
                
                double totalIncome = 0, totalExpense = 0;
                StringBuilder sb = new StringBuilder(resultArea.getText());
                sb.append(String.format("%-15s %-30s %-15s\n", "DATA", "DESCRIÇÃO", "VALOR (R$)"));
                sb.append("-------------------------------------------------------------\n");
                
                for (Transaction t : transactions) {
                    if ("INCOME".equals(t.getType())) {
                        totalIncome += t.getAmount();
                        sb.append(String.format("%s %-30s %15.2f\n", 
                            t.getTimestamp().toLocalDate(), t.getDescription(), t.getAmount()));
                    } else {
                        totalExpense += t.getAmount();
                        sb.append(String.format("%s %-30s %15.2f\n", 
                            t.getTimestamp().toLocalDate(), "📉 " + t.getDescription(), -t.getAmount()));
                    }
                }
                
                sb.append("-------------------------------------------------------------\n");
                sb.append(String.format("\n💰 Total de RECEITAS: R$ %.2f\n", totalIncome));
                sb.append(String.format("📉 Total de DESPESAS: R$ %.2f\n", totalExpense));
                sb.append(String.format("📊 SALDO FINAL: R$ %.2f\n", totalIncome - totalExpense));
                
                if ((totalIncome - totalExpense) > 0) {
                    sb.append("🎉 Situação: POSITIVO!\n");
                } else if ((totalIncome - totalExpense) < 0) {
                    sb.append("⚠️ Situação: NEGATIVO!\n");
                } else {
                    sb.append("⚖️ Situação: NEUTRO\n");
                }
                
                resultArea.setText(sb.toString());
                
                // Atualizar gráfico
                barChart.getData().clear();
                XYChart.Series<String, Number> seriesIncome = new XYChart.Series<>();
                seriesIncome.setName("Receitas");
                XYChart.Series<String, Number> seriesExpense = new XYChart.Series<>();
                seriesExpense.setName("Despesas");
                
                seriesIncome.getData().add(new XYChart.Data<>("Total", totalIncome));
                seriesExpense.getData().add(new XYChart.Data<>("Total", totalExpense));
                
                barChart.getData().addAll(seriesIncome, seriesExpense);
                
            } catch (SQLException ex) {
                mostrarErro("Erro ao gerar relatório", ex.getMessage());
            }
        });
        
        root.getChildren().addAll(periodBox, monthBox, yearBox, periodBoxFields, generateBtn, barChart, resultArea);
        
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        tab.setContent(scrollPane);
        
        return tab;
    }
    
    private void mostrarErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    private void mostrarInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
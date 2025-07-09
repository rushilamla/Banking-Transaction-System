import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class BankingTransactionSystem {
    private static final String URL = "jdbc:mysql://localhost:3306/banking_db";
    private static final String USER = "root";
    private static final String PASSWORD = "Rushil@2005";

    private JFrame frame;
    private JTextArea dashboardArea;
    private Connection conn;

    
    private static final Color PRIMARY_COLOR = new Color(33, 150, 243); // Blue
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);  // Green
    private static final Color DANGER_COLOR = new Color(244, 67, 54);   // Red
    private static final Color TEXT_COLOR = new Color(33, 33, 33);      // Dark Gray
    private static final Color GRADIENT_START = new Color(187, 222, 251); // Light Blue
    private static final Color GRADIENT_END = new Color(255, 255, 255);   // White

    public BankingTransactionSystem() {
        
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        
        showLoginWindow();
    }

    private void showLoginWindow() {
        JFrame loginFrame = new JFrame("Login to Banking System");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(450, 350);
        loginFrame.setLayout(new BorderLayout());

        
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new GridBagLayout());
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        
        JLabel titleLabel = new JLabel("Banking System Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gradientPanel.add(titleLabel, gbc);

        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gradientPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gradientPanel.add(usernameField, gbc);

        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gradientPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gradientPanel.add(passwordField, gbc);

        
        JButton loginButton = createStyledButton("Login", PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gradientPanel.add(loginButton, gbc);

        
        JButton createAccountButton = createStyledButton("Create Account", SUCCESS_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gradientPanel.add(createAccountButton, gbc);

        
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(loginFrame, "Username and Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authenticateUser(username, password)) {
                loginFrame.dispose(); 
                showMainWindow(); 
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid Username or Password!", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText(""); 
            }
        });

        
        createAccountButton.addActionListener(e -> {
            String result = createCustomer(loginFrame);
            if (result.startsWith("Customer added successfully")) {
                JOptionPane.showMessageDialog(loginFrame, "Account created successfully! Please log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                usernameField.setText("");
                passwordField.setText("");
            } else {
                JOptionPane.showMessageDialog(loginFrame, result, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginFrame.add(gradientPanel, BorderLayout.CENTER);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        try {
            String sql = "SELECT password FROM Customer WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return password.equals(storedPassword);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error authenticating user: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void showMainWindow() {
        
        frame = new JFrame("Banking Transaction System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout(10, 10));

        
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        
        dashboardArea = new JTextArea();
        dashboardArea.setEditable(false);
        dashboardArea.setFont(new Font("Arial", Font.PLAIN, 14));
        dashboardArea.setBackground(new Color(245, 245, 245)); // Very light gray
        dashboardArea.setForeground(TEXT_COLOR);
        dashboardArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1), "Dashboard",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 16), PRIMARY_COLOR),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        dashboardArea.setLineWrap(true);
        dashboardArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(dashboardArea);
        scrollPane.setPreferredSize(new Dimension(400, 0));
        mainPanel.add(scrollPane, BorderLayout.EAST);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); 
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
        JButton addAccountBtn = createStyledButton("Add Account", PRIMARY_COLOR);
        JButton addTransactionBtn = createStyledButton("Add Transaction", PRIMARY_COLOR);
        JButton displayAccountsBtn = createStyledButton("Display Accounts", PRIMARY_COLOR);
        JButton displayTransactionsBtn = createStyledButton("Display Transaction History", PRIMARY_COLOR);
        JButton viewBalanceBtn = createStyledButton("View Account Balance", PRIMARY_COLOR);
        JButton deleteCustomerBtn = createStyledButton("Delete Customer", DANGER_COLOR);
        JButton exitBtn = createStyledButton("Exit", DANGER_COLOR);

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        buttonPanel.add(addAccountBtn, gbc);
        gbc.gridy++;
        buttonPanel.add(addTransactionBtn, gbc);
        gbc.gridy++;
        buttonPanel.add(displayAccountsBtn, gbc);
        gbc.gridy++;
        buttonPanel.add(displayTransactionsBtn, gbc);
        gbc.gridy++;
        buttonPanel.add(viewBalanceBtn, gbc);
        gbc.gridy++;
        buttonPanel.add(deleteCustomerBtn, gbc);
        gbc.gridy++;
        buttonPanel.add(exitBtn, gbc);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);

       
        addAccountBtn.addActionListener(e -> addAccount());
        addTransactionBtn.addActionListener(e -> addTransaction());
        displayAccountsBtn.addActionListener(e -> displayAccounts());
        displayTransactionsBtn.addActionListener(e -> displayTransactionHistory());
        viewBalanceBtn.addActionListener(e -> viewAccountBalance());
        deleteCustomerBtn.addActionListener(e -> deleteCustomer());
        exitBtn.addActionListener(e -> {
            try {
                conn.close();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Error closing database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            frame.dispose();
        });

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setPreferredSize(new Dimension(250, 45));
        button.setOpaque(true);
        button.setBorderPainted(false);

        Color hoverColor = bgColor.brighter();
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private void displayOutput(String text) {
        dashboardArea.setText(""); 
        dashboardArea.append(text);
    }

    private String createCustomer(Component parent) {
        JTextField usernameField = new JTextField(15);
        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField passwordField = new JTextField(15);

        JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
        panel.setBackground(new Color(255, 255, 255, 230)); 
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel);
        panel.add(usernameField);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setForeground(TEXT_COLOR);
        panel.add(firstNameLabel);
        panel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setForeground(TEXT_COLOR);
        panel.add(lastNameLabel);
        panel.add(lastNameField);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(TEXT_COLOR);
        panel.add(addressLabel);
        panel.add(addressField);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setForeground(TEXT_COLOR);
        panel.add(phoneLabel);
        panel.add(phoneField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(TEXT_COLOR);
        panel.add(passwordLabel);
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Create Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String address = addressField.getText().trim();
                String phone = phoneField.getText().trim();
                String password = passwordField.getText().trim();

                if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    return "All fields are required!";
                }

                String sql = "INSERT INTO Customer (username, first_name, last_name, address, phone, password) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, firstName);
                    stmt.setString(3, lastName);
                    stmt.setString(4, address);
                    stmt.setString(5, phone);
                    stmt.setString(6, password);
                    stmt.executeUpdate();
                    return "Customer added successfully!";
                }
            } catch (SQLException ex) {
                return "Error adding customer: " + ex.getMessage();
            }
        }
        return "Customer creation cancelled.";
    }

    private void addAccount() {
        JTextField accountNumberField = new JTextField(5);
        JTextField usernameField = new JTextField(15);
        String[] accountTypes = {"Savings", "Checking"};
        JComboBox<String> accountTypeCombo = new JComboBox<>(accountTypes);
        JTextField balanceField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setForeground(TEXT_COLOR);
        panel.add(accountNumberLabel);
        panel.add(accountNumberField);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel);
        panel.add(usernameField);

        JLabel accountTypeLabel = new JLabel("Account Type:");
        accountTypeLabel.setForeground(TEXT_COLOR);
        panel.add(accountTypeLabel);
        panel.add(accountTypeCombo);

        JLabel balanceLabel = new JLabel("Initial Balance:");
        balanceLabel.setForeground(TEXT_COLOR);
        panel.add(balanceLabel);
        panel.add(balanceField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Account", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                String username = usernameField.getText().trim();
                String accountType = (String) accountTypeCombo.getSelectedItem();
                double balance = Double.parseDouble(balanceField.getText());

                if (username.isEmpty()) {
                    displayOutput("Username cannot be empty!\n");
                    JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!userExists(username)) {
                    displayOutput("Username " + username + " does not exist!\n");
                    JOptionPane.showMessageDialog(frame, "Username " + username + " does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "INSERT INTO Account (account_number, username, branch_id, account_type, balance) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountNumber);
                    stmt.setString(2, username);
                    stmt.setInt(3, 1); 
                    stmt.setString(4, accountType);
                    stmt.setDouble(5, balance);
                    stmt.executeUpdate();
                    displayOutput("Account added successfully!\n");
                }
            } catch (SQLException | NumberFormatException ex) {
                displayOutput("Error adding account: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(frame, "Error adding account: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean userExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM Customer WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private void addTransaction() {
        JTextField accountNumberField = new JTextField(5);
        String[] transactionTypes = {"Deposit", "Withdrawal"};
        JComboBox<String> transactionTypeCombo = new JComboBox<>(transactionTypes);
        JTextField amountField = new JTextField(10);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setForeground(TEXT_COLOR);
        panel.add(accountNumberLabel);
        panel.add(accountNumberField);

        JLabel transactionTypeLabel = new JLabel("Transaction Type:");
        transactionTypeLabel.setForeground(TEXT_COLOR);
        panel.add(transactionTypeLabel);
        panel.add(transactionTypeCombo);

        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(TEXT_COLOR);
        panel.add(amountLabel);
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Transaction", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                String transactionType = (String) transactionTypeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                if (amount <= 0) {
                    displayOutput("Amount must be positive!\n");
                    JOptionPane.showMessageDialog(frame, "Amount must be positive!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (transactionType.equals("Deposit")) {
                    updateBalance(accountNumber, amount, true);
                    insertTransaction(accountNumber, transactionType, amount);
                    displayOutput("Transaction added successfully! Deposited " + amount + " to Account " + accountNumber + "\n");
                } else if (transactionType.equals("Withdrawal")) {
                    double currentBalance = getBalance(accountNumber);
                    if (currentBalance >= amount) {
                        updateBalance(accountNumber, amount, false);
                        insertTransaction(accountNumber, transactionType, amount);
                        displayOutput("Transaction added successfully! Withdrew " + amount + " from Account " + accountNumber + "\n");
                    } else {
                        displayOutput("Insufficient balance for withdrawal!\n");
                        JOptionPane.showMessageDialog(frame, "Insufficient balance for withdrawal!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException | NumberFormatException ex) {
                displayOutput("Error adding transaction: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(frame, "Error adding transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private double getBalance(int accountNumber) throws SQLException {
        String sql = "SELECT balance FROM Account WHERE account_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            } else {
                throw new SQLException("Account not found!");
            }
        }
    }

    @SuppressWarnings("unused")
    private boolean accountExists(int accountNumber) throws SQLException {
        String sql = "SELECT 1 FROM Account WHERE account_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private void updateBalance(int accountNumber, double amount, boolean isDeposit) throws SQLException {
        String sql = "UPDATE Account SET balance = balance " + (isDeposit ? "+" : "-") + " ? WHERE account_number = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, amount);
            stmt.setInt(2, accountNumber);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Account not found!");
            }
        }
    }

    private void insertTransaction(int accountNumber, String transactionType, double amount) throws SQLException {
        String sql = "INSERT INTO Transaction (account_number, transaction_type, amount, transaction_date) VALUES (?, ?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountNumber);
            stmt.setString(2, transactionType);
            stmt.setDouble(3, amount);
            stmt.executeUpdate();
        }
    }

    private void displayAccounts() {
        JTextField usernameField = new JTextField(15);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Enter Username:");
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel);
        panel.add(usernameField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Display Accounts", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    displayOutput("Username cannot be empty!\n");
                    JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sql = "SELECT * FROM Account WHERE username = ?";
                StringBuilder output = new StringBuilder();
                output.append("Accounts for Username ").append(username).append(":\n");
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        output.append(String.format("Account Number: %d, Type: %s, Balance: %.2f%n",
                                rs.getInt("account_number"), rs.getString("account_type"),
                                rs.getDouble("balance")));
                    }
                }
                if (output.toString().equals("Accounts for Username " + username + ":\n")) {
                    output.append("No accounts found for this user.\n");
                }
                displayOutput(output.toString());
            } catch (SQLException ex) {
                displayOutput("Error displaying accounts: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(frame, "Error displaying accounts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayTransactionHistory() {
        JTextField accountNumberField = new JTextField(5);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel accountNumberLabel = new JLabel("Enter Account Number:");
        accountNumberLabel.setForeground(TEXT_COLOR);
        panel.add(accountNumberLabel);
        panel.add(accountNumberField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Display Transaction History", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                String sql = "SELECT * FROM Transaction WHERE account_number = ? AND transaction_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
                StringBuilder output = new StringBuilder();
                output.append("Transaction History for Account Number ").append(accountNumber).append(":\n");
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountNumber);
                    ResultSet rs = stmt.executeQuery();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    while (rs.next()) {
                        output.append(String.format("Transaction ID: %d, Type: %s, Amount: %.2f, Date: %s%n",
                                rs.getInt("transaction_id"), rs.getString("transaction_type"),
                                rs.getDouble("amount"), sdf.format(rs.getTimestamp("transaction_date"))));
                    }
                }
                if (output.toString().equals("Transaction History for Account Number " + accountNumber + ":\n")) {
                    output.append("No transactions found in the last 7 days.\n");
                }
                displayOutput(output.toString());
            } catch (SQLException | NumberFormatException ex) {
                displayOutput("Error displaying transactions: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(frame, "Error displaying transactions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAccountBalance() {
        JTextField accountNumberField = new JTextField(5);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel accountNumberLabel = new JLabel("Enter Account Number:");
        accountNumberLabel.setForeground(TEXT_COLOR);
        panel.add(accountNumberLabel);
        panel.add(accountNumberField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "View Account Balance", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int accountNumber = Integer.parseInt(accountNumberField.getText());
                String sql = "SELECT balance FROM Account WHERE account_number = ?";
                StringBuilder output = new StringBuilder();
                output.append("Account Balance for Account Number ").append(accountNumber).append(":\n");
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, accountNumber);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        double balance = rs.getDouble("balance");
                        output.append(String.format("Balance: %.2f%n", balance));
                    } else {
                        output = new StringBuilder();
                        output.append("Account Number ").append(accountNumber).append(" not found.\n");
                    }
                }
                displayOutput(output.toString());
            } catch (SQLException | NumberFormatException ex) {
                displayOutput("Error viewing balance: " + ex.getMessage() + "\n");
                JOptionPane.showMessageDialog(frame, "Error viewing balance: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteCustomer() {
        JTextField usernameField = new JTextField(15);
        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255, 230));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Enter Username to Delete:");
        usernameLabel.setForeground(TEXT_COLOR);
        panel.add(usernameLabel);
        panel.add(usernameField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Delete Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                displayOutput("Username cannot be empty!\n");
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String sql = "DELETE FROM Customer WHERE username = ?";
                int rowsAffected;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    rowsAffected = stmt.executeUpdate();
                }
                if (rowsAffected > 0) {
                    displayOutput("Username " + username + " deleted successfully!\n");
                } else {
                    displayOutput("Username " + username + " not found.\n");
                }
            } catch (SQLException ex) {
                String errorMessage = "Error deleting customer: " + ex.getMessage() + "\n";
                if (ex.getMessage().contains("foreign key constraint fails")) {
                    errorMessage += "Ensure that foreign key constraints are set to ON DELETE CASCADE in the Account and Transaction tables.\n";
                }
                displayOutput(errorMessage);
                JOptionPane.showMessageDialog(frame, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BankingTransactionSystem());
    }
}
package com.raj.banking;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class BankingAppUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color PRIMARY_COLOR = new Color(25, 118, 210);
    private static final Color SECONDARY_COLOR = new Color(66, 133, 244);
    private static final Color ACCENT_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(229, 57, 53);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(97, 97, 97);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 25);


    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font CARD_FONT = new Font("Segoe UI", Font.BOLD, 18);

    private final Bank bank = new Bank();
    private JPanel mainPanel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public BankingAppUI() {
        initializeFrame();
        setupUI();
        applyModernStyling();
    }

    private void initializeFrame() {
        setTitle("🏦 NeoBank - Modern Banking");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true); 
        setMinimumSize(new Dimension(500, 700));
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createModernHeader(), BorderLayout.NORTH);
        
        mainPanel = createMainPanel();
        add(new JScrollPane(mainPanel), BorderLayout.CENTER); 
        
        add(createModernFooter(), BorderLayout.SOUTH);
    }

    private JPanel createModernHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 
                    getWidth(), getHeight(), SECONDARY_COLOR
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(getWidth(), 180));
        header.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel bankIcon = new JLabel("🏦");
        bankIcon.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        bankIcon.setForeground(Color.WHITE);
        bankIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("NeoBank");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Digital • Secure • Modern");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(bankIcon);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(subtitleLabel);

        JPanel statsPanel = createHeaderStatsPanel();
        
        header.add(infoPanel, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createHeaderStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setOpaque(false);
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel securityLabel = new JLabel("🔒 Bank-Grade Security");
        securityLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        securityLabel.setForeground(Color.WHITE);
        securityLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel availabilityLabel = new JLabel("⏰ 24/7 Available");
        availabilityLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        availabilityLabel.setForeground(Color.WHITE);
        availabilityLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        statsPanel.add(securityLabel);
        statsPanel.add(Box.createVerticalStrut(5));
        statsPanel.add(availabilityLabel);

        return statsPanel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createWelcomeCard());
        panel.add(Box.createVerticalStrut(20));

        panel.add(createServicesGrid());
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    private JPanel createWelcomeCard() {
        JPanel card = new ModernCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel welcomeLabel = new JLabel("Welcome to Digital Banking");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        JLabel descriptionLabel = new JLabel("<html>Manage your finances securely with our modern banking platform. Fast, reliable, and always available.</html>");
        descriptionLabel.setFont(SUBTITLE_FONT);
        descriptionLabel.setForeground(TEXT_SECONDARY);

        card.add(welcomeLabel, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(10));
        card.add(descriptionLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createServicesGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 15, 15)); // Changed to 0 rows for flexibility
        gridPanel.setBackground(BACKGROUND_COLOR);
        gridPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        String[] services = {
            "CREATE_ACCOUNT", "VIEW_DETAILS", 
            "DEPOSIT", "WITHDRAW", 
            "UPDATE_ACCOUNT", "DELETE_ACCOUNT",
            "TRANSACTION_HISTORY", "ACCOUNT_SETTINGS"
        };

        String[] icons = {"📝", "👤", "💰", "💳", "⚙️", "🗑️", "📊", "🔧"};
        String[] titles = {"Open Account", "Account Details", "Deposit Funds", "Withdraw Cash", "Manage Account", "Close Account", "Transactions", "Settings"};
        String[] descriptions = {
            "Create new banking account",
            "View your account information", 
            "Add money to your account",
            "Withdraw from your balance",
            "Update personal details",
            "Permanently close account",
            "View transaction history",
            "Account preferences"
        };

        for (int i = 0; i < services.length; i++) {
            ServiceCard card = new ServiceCard(icons[i], titles[i], descriptions[i], services[i]);
            gridPanel.add(card);
        }

        return gridPanel;
    }

    private JPanel createModernFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(BACKGROUND_COLOR);
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            new EmptyBorder(15, 25, 15, 25)
        ));

        JLabel copyrightLabel = new JLabel("© 2025 NeoBank. Secure Banking");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        copyrightLabel.setForeground(TEXT_SECONDARY);

        JLabel adminLabel = new JLabel("👨‍💼 Admin Access");
        adminLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        adminLabel.setForeground(PRIMARY_COLOR);
        adminLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        adminLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                handleAdminLogin();
            }
            public void mouseEntered(MouseEvent evt) {
                adminLabel.setForeground(SECONDARY_COLOR);
            }
            public void mouseExited(MouseEvent evt) {
                adminLabel.setForeground(PRIMARY_COLOR);
            }
        });

        footer.add(copyrightLabel, BorderLayout.WEST);
        footer.add(adminLabel, BorderLayout.EAST);

        return footer;
    }

    class ModernCard extends JPanel {
        public ModernCard() {
            setOpaque(false);
            setBackground(CARD_COLOR);
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.setColor(SHADOW_COLOR);
            g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
            
            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth()-2, getHeight()-2, 18, 18);
            
            super.paintComponent(g);
        }
    }

    class ServiceCard extends ModernCard {
        public ServiceCard(String icon, String title, String description, String action) {
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            topPanel.setOpaque(false);
            
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(CARD_FONT);
            titleLabel.setForeground(TEXT_PRIMARY);
            
            topPanel.add(iconLabel);
            topPanel.add(Box.createHorizontalStrut(10));
            topPanel.add(titleLabel);
            
            JLabel descLabel = new JLabel("<html><div style='width: 120px;'>" + description + "</div></html>");
            descLabel.setFont(SUBTITLE_FONT);
            descLabel.setForeground(TEXT_SECONDARY);
            
            add(topPanel, BorderLayout.NORTH);
            add(Box.createVerticalStrut(10));
            add(descLabel, BorderLayout.CENTER);
            
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(245, 245, 245));
                }
                public void mouseExited(MouseEvent e) {
                    setBackground(CARD_COLOR);
                }
                public void mouseClicked(MouseEvent e) {
                    handleServiceAction(action);
                }
            });
        }
    }

    private void handleServiceAction(String action) {
        switch (action) {
            case "CREATE_ACCOUNT":
                handleCreateAccount();
                break;
            case "VIEW_DETAILS":
                handleViewDetails();
                break;
            case "DEPOSIT":
                handleDeposit();
                break;
            case "WITHDRAW":
                handleWithdraw();
                break;
            case "UPDATE_ACCOUNT":
                handleUpdateAccount();
                break;
            case "DELETE_ACCOUNT":
                handleDeleteAccount();
                break;
            case "TRANSACTION_HISTORY":
                handleTransactionHistory();
                break;
            case "ACCOUNT_SETTINGS":
                handleAccountSettings();
                break;
        }
    }

    private void handleCreateAccount() {
        JDialog createAccountDialog = new JDialog(this, "Create New Account", true);
        createAccountDialog.setLayout(new BorderLayout());
        createAccountDialog.setSize(500, 650);
        createAccountDialog.setLocationRelativeTo(this);
        createAccountDialog.setResizable(true);
        createAccountDialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("📝 Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Fill in your details to get started");
        subtitleLabel.setFont(SUBTITLE_FONT);
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(subtitleLabel);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JTextField nameField = createModernTextField();
        JTextField dobField = createModernTextField();
        JTextField emailField = createModernTextField();
        JTextField phoneField = createModernTextField();
        
        JPanel bankPanel = new JPanel(new BorderLayout());
        bankPanel.setBackground(CARD_COLOR);
        JLabel bankLabel = new JLabel("Select Bank:");
        bankLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bankLabel.setForeground(TEXT_PRIMARY);
        
        JComboBox<String> bankComboBox = new JComboBox<>(bank.getAvailableBanks());
        bankComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bankComboBox.setBackground(Color.WHITE);
        bankComboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        bankPanel.add(bankLabel, BorderLayout.NORTH);
        bankPanel.add(Box.createVerticalStrut(5));
        bankPanel.add(bankComboBox, BorderLayout.CENTER);

        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBackground(CARD_COLOR);
        JLabel typeLabel = new JLabel("Account Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        typeLabel.setForeground(TEXT_PRIMARY);
        
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Savings", "Salary", "Business"});
        typeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeComboBox.setBackground(Color.WHITE);
        typeComboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(Box.createVerticalStrut(5));
        typePanel.add(typeComboBox, BorderLayout.CENTER);

        JTextField depositField = createModernTextField();
        
        JPasswordField pinField = new JPasswordField();
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pinField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));

        formPanel.add(createFormField("Full Name:", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormField("Date of Birth (YYYY-MM-DD):", dobField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormField("Email Address:", emailField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormField("Phone Number:", phoneField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(bankPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(typePanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormField("Initial Deposit (₹):", depositField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFormField("4-Digit PIN:", pinField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton cancelButton = new ModernButton("Cancel", TEXT_SECONDARY);
        JButton createButton = new ModernButton("Create Account", ACCENT_COLOR);

        cancelButton.addActionListener(e -> createAccountDialog.dispose());
        createButton.addActionListener(e -> {
            if (validateAccountCreation(nameField, dobField, emailField, phoneField, depositField, pinField)) {
                try {
                    LocalDate dateOfBirth = LocalDate.parse(dobField.getText().trim(), dateFormatter);
                    String selectedBank = (String) bankComboBox.getSelectedItem();
                    String accountType = (String) typeComboBox.getSelectedItem();
                    double initialDeposit = Double.parseDouble(depositField.getText().trim());
                    int pin = Integer.parseInt(new String(pinField.getPassword()));

                    String accountNumber = bank.createAccount(
                        nameField.getText().trim(), pin, emailField.getText().trim(), 
                        phoneField.getText().trim(), dateOfBirth, selectedBank, 
                        initialDeposit, accountType
                    );

                    if (accountNumber != null) {
                        showModernSuccess("Account created successfully!\n\n" +
                            "Account Number: " + accountNumber + "\n" +
                            "Bank: " + selectedBank + "\n" +
                            "Initial Deposit: ₹" + initialDeposit + "\n" +
                            "Status: PENDING APPROVAL\n\n" +
                            "A welcome email has been sent to your registered email address.");
                        createAccountDialog.dispose();
                    } else {
                        showModernError("Failed to create account. Please try again.");
                    }
                } catch (DateTimeParseException ex) {
                    showModernError("Invalid date format. Please use YYYY-MM-DD.");
                } catch (NumberFormatException ex) {
                    showModernError("Invalid amount or PIN format.");
                } catch (IllegalArgumentException ex) {
                    showModernError(ex.getMessage());
                }
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(createButton);

        createAccountDialog.add(headerPanel, BorderLayout.NORTH);
        createAccountDialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        createAccountDialog.add(buttonPanel, BorderLayout.SOUTH);
        createAccountDialog.setVisible(true);
    }

    private boolean validateAccountCreation(JTextField name, JTextField dob, JTextField email, 
                                          JTextField phone, JTextField deposit, JPasswordField pin) {
        if (name.getText().trim().isEmpty()) {
            showModernError("Please enter your full name.");
            return false;
        }
        if (dob.getText().trim().isEmpty()) {
            showModernError("Please enter your date of birth.");
            return false;
        }
        if (email.getText().trim().isEmpty()) {
            showModernError("Please enter your email address.");
            return false;
        }
        if (phone.getText().trim().isEmpty()) {
            showModernError("Please enter your phone number.");
            return false;
        }
        if (deposit.getText().trim().isEmpty()) {
            showModernError("Please enter initial deposit amount.");
            return false;
        }
        if (new String(pin.getPassword()).length() != 4) {
            showModernError("PIN must be exactly 4 digits.");
            return false;
        }
        
        try {
            double depositAmount = Double.parseDouble(deposit.getText().trim());
            if (depositAmount < 1000) {
                showModernError("Initial deposit must be at least ₹1000.");
                return false;
            }
        } catch (NumberFormatException e) {
            showModernError("Please enter a valid deposit amount.");
            return false;
        }
        
        return true;
    }

    private void handleViewDetails() {
        Account account = authenticateUser("View Account Details");
        if (account != null) {
            showAccountDetails(account);
        }
    }

    private void handleDeposit() {
        Account account = authenticateUser("Deposit Funds");
        if (account != null) {
            String amountStr = JOptionPane.showInputDialog(this, 
                "Enter amount to deposit:", "Deposit Funds", JOptionPane.QUESTION_MESSAGE);
            
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr.trim());
                    if (amount <= 0) {
                        showModernError("Please enter a positive amount.");
                        return;
                    }
                    
                    account.deposit(amount);
                    bank.updateAccountBalance(account, "Deposit", amount);
                    showModernSuccess(String.format("Successfully deposited ₹%.2f\nNew Balance: ₹%.2f", 
                        amount, account.getBalance()));
                        
                } catch (NumberFormatException e) {
                    showModernError("Please enter a valid amount.");
                }
            }
        }
    }

    private void handleWithdraw() {
        Account account = authenticateUser("Withdraw Funds");
        if (account != null) {
            String amountStr = JOptionPane.showInputDialog(this, 
                "Enter amount to withdraw:", "Withdraw Funds", JOptionPane.QUESTION_MESSAGE);
            
            if (amountStr != null && !amountStr.trim().isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr.trim());
                    if (amount <= 0) {
                        showModernError("Please enter a positive amount.");
                        return;
                    }
                    
                    if (account.withdraw(amount)) {
                        bank.updateAccountBalance(account, "Withdrawal", amount);
                        showModernSuccess(String.format("Successfully withdrew ₹%.2f\nNew Balance: ₹%.2f", 
                            amount, account.getBalance()));
                    } else {
                        showModernError("Withdrawal failed. Check your balance.");
                    }
                        
                } catch (NumberFormatException e) {
                    showModernError("Please enter a valid amount.");
                }
            }
        }
    }

    private void handleUpdateAccount() {
        Account account = authenticateUser("Update Account");
        if (account != null) {
            showUpdateOptions(account);
        }
    }

    private void handleDeleteAccount() {
        Account account = authenticateUser("Delete Account");
        if (account != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this account?\n\n" +
                "Account: " + account.getAccountHolderName() + "\n" +
                "Number: " + account.getAccountNumber() + "\n" +
                "Bank: " + account.getBankName() + "\n" +
                "Status: " + account.getStatus() + "\n\n" +
                "This action cannot be undone!",
                "Confirm Account Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (bank.deleteAccount(account.getAccountNumber())) {
                    showModernSuccess("Account deleted successfully.");
                } else {
                    showModernError("Failed to delete account.");
                }
            }
        }
    }

    private void handleTransactionHistory() {
        Account account = authenticateUser("Transaction History");
        if (account != null) {
            List<String> transactions = bank.getTransactionHistory(account.getAccountNumber());
            
            if (transactions.isEmpty()) {
                showModernInfo("Transaction History", "No transactions found for this account.");
                return;
            }

            JDialog historyDialog = new JDialog(this, "Transaction History", true);
            historyDialog.setSize(700, 500);
            historyDialog.setLocationRelativeTo(this);
            historyDialog.setResizable(true);
            historyDialog.getContentPane().setBackground(BACKGROUND_COLOR);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            textArea.setBackground(new Color(30, 30, 30));
            textArea.setForeground(Color.GREEN);
            
            StringBuilder history = new StringBuilder();
            history.append("Transaction History for: ").append(account.getAccountNumber()).append("\n");
            history.append("Account Holder: ").append(account.getAccountHolderName()).append("\n");
            history.append("Bank: ").append(account.getBankName()).append("\n\n");
            
            for (String transaction : transactions) {
                history.append(transaction).append("\n");
            }
            
            textArea.setText(history.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);
            
            historyDialog.add(scrollPane);
            historyDialog.setVisible(true);
        }
    }

    private void handleAccountSettings() {
        showModernInfo("Account Settings", "This feature allows you to manage your account preferences and security settings.");
    }

    private void showUpdateOptions(Account account) {
        String[] options = {"Name", "Email", "Phone Number", "PIN"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "What would you like to update?",
            "Update Account Details",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);

        if (choice != null) {
            handleFieldUpdate(account, choice);
        }
    }

    private void handleFieldUpdate(Account account, String field) {
        String currentValue = "";
        String fieldName = "";
        
        switch (field) {
            case "Name":
                currentValue = account.getAccountHolderName();
                fieldName = "name";
                break;
            case "Email":
                currentValue = account.getEmail();
                fieldName = "email";
                break;
            case "Phone Number":
                currentValue = account.getPhoneNumber();
                fieldName = "phone_number";
                break;
            case "PIN":
                currentValue = "****";
                fieldName = "pin";
                break;
        }

        String newValue = JOptionPane.showInputDialog(this, 
            "Current " + field + ": " + currentValue + "\nEnter new " + field.toLowerCase() + ":",
            "Update " + field, JOptionPane.QUESTION_MESSAGE);
        
        if (newValue != null && !newValue.trim().isEmpty()) {
            String otp = bank.generateAndStoreOTP(account.getAccountNumber());
            if (otp != null) {
                String enteredOTP = JOptionPane.showInputDialog(this,
                    "For security, please enter the OTP sent to your phone:\n(OTP: " + otp + ")",
                    "OTP Verification", JOptionPane.QUESTION_MESSAGE);
                
                if (enteredOTP != null && bank.updateAccountDetailsWithOTP(account, fieldName, newValue.trim(), enteredOTP)) {
                    showModernSuccess(field + " updated successfully!");
                } else {
                    showModernError("OTP verification failed. Update cancelled.");
                }
            } else {
                showModernError("Failed to generate OTP. Please try again.");
            }
        }
    }

    private Account authenticateUser(String operation) {
        JPanel authPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        authPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        authPanel.setBackground(CARD_COLOR);

        JTextField accNumField = createModernTextField();
        JPasswordField pinField = new JPasswordField();
        pinField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pinField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));

        authPanel.add(new JLabel("Account Number:"));
        authPanel.add(accNumField);
        authPanel.add(new JLabel("PIN:"));
        authPanel.add(pinField);

        int result = JOptionPane.showConfirmDialog(this, authPanel, operation + " - Authentication Required",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String accNum = accNumField.getText().trim();
                int pin = Integer.parseInt(new String(pinField.getPassword()).trim());
                
                Account account = bank.login(accNum, pin);
                if (account != null) {
                    return account;
                } else {
                    showModernError("Authentication failed. Invalid account number, PIN, or account not active.");
                }
            } catch (NumberFormatException e) {
                showModernError("Invalid PIN format. Please enter numbers only.");
            }
        }
        return null;
    }

    private void showAccountDetails(Account account) {
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 10, 5));
        detailsPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        detailsPanel.setBackground(CARD_COLOR);
        
        addDetailRow(detailsPanel, "👤 Account Holder:", account.getAccountHolderName());
        addDetailRow(detailsPanel, "🔢 Account Number:", account.getAccountNumber());
        addDetailRow(detailsPanel, "🏦 Bank:", account.getBankName());
        addDetailRow(detailsPanel, "💰 Balance:", String.format("₹%.2f", account.getBalance()));
        addDetailRow(detailsPanel, "📧 Email:", account.getEmail());
        addDetailRow(detailsPanel, "📞 Phone:", account.getPhoneNumber());
        addDetailRow(detailsPanel, "🎂 Date of Birth:", account.getDateOfBirth().toString());
        addDetailRow(detailsPanel, "💵 Initial Deposit:", String.format("₹%.2f", account.getInitialDeposit()));
        addDetailRow(detailsPanel, "📊 Status:", getStatusWithColor(account.getStatus()));
        addDetailRow(detailsPanel, "📅 Account Created:", account.getAccountCreationDate().toString());
        
        JOptionPane.showMessageDialog(this, detailsPanel, 
            "Account Details - " + account.getAccountNumber(), 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private String getStatusWithColor(String status) {
        if ("ACTIVE".equals(status)) {
            return "🟢 ACTIVE";
        } else {
            return "🟡 PENDING";
        }
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(CARD_COLOR);
        
        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        keyLabel.setForeground(TEXT_SECONDARY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(TEXT_PRIMARY);
        
        rowPanel.add(keyLabel, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(rowPanel);
    }

    private JTextField createModernTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return textField;
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        fieldLabel.setForeground(TEXT_PRIMARY);
        
        panel.add(fieldLabel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private void handleAdminLogin() {
        String password = JOptionPane.showInputDialog(this, 
            "Enter Admin Password:", "Admin Access", JOptionPane.PLAIN_MESSAGE);
            
        if ("admin123".equals(password)) {
            SwingUtilities.invokeLater(() -> {
                AdminPanelUI adminUI = new AdminPanelUI(bank);
                adminUI.setVisible(true);
            });
        } else if (password != null) {
            showModernError("Incorrect admin password.");
        }
    }

    private void showModernSuccess(String message) {
        showModernDialog("✅ Success", message, ACCENT_COLOR);
    }

    private void showModernError(String message) {
        showModernDialog("❌ Error", message, WARNING_COLOR);
    }

    private void showModernInfo(String title, String message) {
        showModernDialog("💡 " + title, message, PRIMARY_COLOR);
    }

    private void showModernDialog(String title, String message, Color color) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);

        JLabel messageLabel = new JLabel("<html><div style='width: 300px; text-align: center;'>" + message + "</div></html>");
        messageLabel.setFont(SUBTITLE_FONT);
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "NeoBank", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    class ModernButton extends JButton {
        public ModernButton(String text, Color color) {
            super(text);
            setFont(BUTTON_FONT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setBackground(color);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 25, 12, 25));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    setBackground(color.brighter());
                }
                public void mouseExited(MouseEvent evt) {
                    setBackground(color);
                }
            });
        }
    }

    private void applyModernStyling() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("OptionPane.background", BACKGROUND_COLOR);
            UIManager.put("Panel.background", BACKGROUND_COLOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new BankingAppUI().setVisible(true);
        });
    }
}
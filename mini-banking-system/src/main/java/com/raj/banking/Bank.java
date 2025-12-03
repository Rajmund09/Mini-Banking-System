package com.raj.banking;

import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.time.Period;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bank {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String USER = "root";
    private static final String PASS = "Raj@77725";

    private static final String EMAIL_USERNAME = "prabhushankarmund@gmail.com";
    private static final String EMAIL_PASSWORD = "hbcs lpih hzez pgum";
    private static final String EMAIL_HOST = "smtp.gmail.com";
    private static final String EMAIL_PORT = "587";

    private static final String[] AVAILABLE_BANKS = {
        "State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank",
        "Punjab National Bank", "Bank of Baroda", "Canara Bank", "Union Bank"
    };

    private final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    public Bank() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("FATAL ERROR: MySQL JDBC Driver not found.");
            System.exit(1);
        }
        initializeDatabase();
    }

    private void initializeDatabase() {
        String accountsSQL = "CREATE TABLE IF NOT EXISTS accounts ("
                + "    account_number VARCHAR(10) PRIMARY KEY,"
                + "    name VARCHAR(255) NOT NULL,"
                + "    pin INT NOT NULL,"
                + "    balance DECIMAL(15, 2) NOT NULL,"
                + "    email VARCHAR(255),"
                + "    phone_number VARCHAR(20),"
                + "    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',"
                + "    date_of_birth DATE NOT NULL,"
                + "    bank_name VARCHAR(100) NOT NULL,"
                + "    account_type VARCHAR(50) NOT NULL,"
                + "    initial_deposit DECIMAL(15, 2) NOT NULL,"
                + "    otp VARCHAR(6),"
                + "    account_creation_date DATE NOT NULL"
                + ");";

        String transactionsSQL = "CREATE TABLE IF NOT EXISTS transactions ("
                + "    transaction_id INT AUTO_INCREMENT PRIMARY KEY,"
                + "    account_number VARCHAR(10) NOT NULL,"
                + "    transaction_type VARCHAR(50) NOT NULL,"
                + "    amount DECIMAL(15, 2) NOT NULL,"
                + "    balance_after DECIMAL(15, 2) NOT NULL,"
                + "    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "    FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE"
                + ");";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(accountsSQL);
            stmt.execute(transactionsSQL);
            System.out.println("Database initialized successfully!");
        } catch (SQLException e) {
            System.err.println("Database Error: Could not initialize tables. " + e.getMessage());
        }
    }

    public String[] getAvailableBanks() {
        return AVAILABLE_BANKS;
    }

    public String createAccount(String name, int pin, String email, String phoneNumber,
                               LocalDate dateOfBirth, String bankName, double initialDeposit, String accountType)
                               throws IllegalArgumentException {

        if (initialDeposit < 1000) {
            throw new IllegalArgumentException("Initial deposit must be at least ₹1000");
        }
        
        if (Period.between(dateOfBirth, LocalDate.now()).getYears() < 18) {
            throw new IllegalArgumentException("You must be 18 years or older to create an account");
        }
        
        if (String.valueOf(pin).length() != 4) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }

        String accountNumber = String.valueOf(10000000 + new Random().nextInt(90000000));
        
        String sql = "INSERT INTO accounts(account_number, name, pin, balance, email, phone_number, " +
                     "status, date_of_birth, bank_name, account_type, initial_deposit, account_creation_date) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, name);
            pstmt.setInt(3, pin);
            pstmt.setDouble(4, initialDeposit);
            pstmt.setString(5, email);
            pstmt.setString(6, phoneNumber);
            pstmt.setString(7, "PENDING");
            pstmt.setDate(8, Date.valueOf(dateOfBirth));
            pstmt.setString(9, bankName);
            pstmt.setString(10, accountType);
            pstmt.setDouble(11, initialDeposit);
            pstmt.setDate(12, Date.valueOf(LocalDate.now()));
            
            pstmt.executeUpdate();

            String subject = "Welcome to " + bankName + " - Account Pending Approval";
            String body = buildPendingEmailBody(name, accountNumber, email, phoneNumber, bankName, initialDeposit, accountType);
            sendEmailAsync(email, subject, body);

            return accountNumber;
        } catch (SQLException e) {
            System.err.println("Database Error: Could not create account. " + e.getMessage());
            return null;
        }
    }

    public boolean transferMoney(String senderAccountNumber, String recipientAccountNumber, double amount, int senderPin) {
        Account senderAccount = login(senderAccountNumber, senderPin);
        if (senderAccount == null) {
            System.err.println("Transfer Error: Sender authentication failed.");
            return false;
        }

        if (senderAccountNumber.equals(recipientAccountNumber)) {
            System.err.println("Transfer Error: Cannot transfer money to the same account.");
            return false;
        }

        if (senderAccount.getBalance() < amount || amount <= 0) {
            System.err.println("Transfer Error: Insufficient funds or invalid amount.");
            return false;
        }

        Account recipientAccount = getAccount(recipientAccountNumber);
        if (recipientAccount == null || !"ACTIVE".equals(recipientAccount.getStatus())) {
            System.err.println("Transfer Error: Recipient account not found or not active.");
            return false;
        }

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            String withdrawSQL = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            try (PreparedStatement withdrawStmt = conn.prepareStatement(withdrawSQL)) {
                withdrawStmt.setDouble(1, amount);
                withdrawStmt.setString(2, senderAccountNumber);
                int rowsAffected = withdrawStmt.executeUpdate();
                if (rowsAffected == 0) throw new SQLException("Withdrawal failed");
            }

            String depositSQL = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            try (PreparedStatement depositStmt = conn.prepareStatement(depositSQL)) {
                depositStmt.setDouble(1, amount);
                depositStmt.setString(2, recipientAccountNumber);
                int rowsAffected = depositStmt.executeUpdate();
                if (rowsAffected == 0) throw new SQLException("Deposit failed");
            }

            double senderBalanceAfter = senderAccount.getBalance() - amount;
            double recipientBalanceAfter = recipientAccount.getBalance() + amount;
            addTransaction(senderAccountNumber, "Transfer Out to " + recipientAccountNumber, amount, senderBalanceAfter, conn);
            addTransaction(recipientAccountNumber, "Transfer In from " + senderAccountNumber, amount, recipientBalanceAfter, conn);

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Database Error during transfer: " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { } }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { } }
        }
    }

    public Account login(String accountNumber, int pin) {
        Account account = getAccount(accountNumber);
        if (account != null && account.validatePin(pin) && "ACTIVE".equals(account.getStatus())) {
            return account;
        }
        return null;
    }

    public boolean approveAccount(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account == null || !"PENDING".equals(account.getStatus())) {
            System.err.println("Approval failed: Account not found or not pending.");
            return false;
        }

        String sql = "UPDATE accounts SET status = 'ACTIVE' WHERE account_number = ? AND status = 'PENDING'";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, accountNumber);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    addTransaction(accountNumber, "Initial Deposit", account.getInitialDeposit(), account.getInitialDeposit(), conn);
                    conn.commit();
                    
                    String subject = "Your " + account.getBankName() + " Account is Now Active!";
                    String body = buildActivationEmailBody(account);
                    sendEmailAsync(account.getEmail(), subject, body);
                    
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Database Error during approval statement: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Database Error: Could not connect/approve account. " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { } }
            return false;
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { } }
        }
    }

    public String[][] getPendingAccounts() {
        ArrayList<String[]> pendingList = new ArrayList<>();
        String sql = "SELECT account_number, name, email FROM accounts WHERE status = 'PENDING'";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                pendingList.add(new String[]{
                    rs.getString("account_number"),
                    rs.getString("name"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            System.err.println("Database Error: Could not fetch pending accounts. " + e.getMessage());
        }
        return pendingList.toArray(new String[0][]);
    }

    public Account getAccount(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return mapRowToAccount(rs);
            }
        } catch (SQLException e) {
            System.err.println("Database Error: Could not fetch account. " + e.getMessage());
        }
        return null;
    }

    public void updateAccountBalance(Account account, String transactionType, double transactionAmount) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, account.getBalance());
                pstmt.setString(2, account.getAccountNumber());
                pstmt.executeUpdate();
            }
            addTransaction(account.getAccountNumber(), transactionType, transactionAmount, account.getBalance(), conn);
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Database Error: Could not update balance/log transaction. " + e.getMessage());
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { } }
        } finally {
            if (conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { } }
        }
    }

    public List<String> getTransactionHistory(String accountNumber) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                history.add(String.format("%s | %-15s | Amount: ₹%10.2f | New Balance: ₹%.2f",
                    rs.getTimestamp("transaction_date").toLocalDateTime().toString().replace("T", " "),
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getDouble("balance_after")));
            }
        } catch (SQLException e) {
            System.err.println("Database Error: Could not fetch transaction history.");
        }
        return history;
    }

    public boolean updateAccountDetailsWithOTP(Account account, String field, String newValue, String otp) {
        if (!verifyStoredOTP(account.getAccountNumber(), otp)) {
            System.err.println("OTP Verification Failed for account: " + account.getAccountNumber());
            return false;
        }

        String columnName;
        switch (field.toLowerCase()) {
            case "name": columnName = "name"; break;
            case "pin": columnName = "pin"; break;
            case "email": columnName = "email"; break;
            case "phone_number": columnName = "phone_number"; break;
            default: System.err.println("Invalid field to update: " + field); return false;
        }

        String sql = "UPDATE accounts SET " + columnName + " = ?, otp = NULL WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if ("pin".equals(columnName)) {
                pstmt.setInt(1, Integer.parseInt(newValue));
                account.setPin(Integer.parseInt(newValue));
            } else {
                pstmt.setString(1, newValue);
                if ("name".equals(columnName)) account.setAccountHolderName(newValue);
                else if ("email".equals(columnName)) account.setEmail(newValue);
                else if ("phone_number".equals(columnName)) account.setPhoneNumber(newValue);
            }

            pstmt.setString(2, account.getAccountNumber());
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }
        } catch (SQLException | NumberFormatException e) {
            System.err.println("Database/Update Error: Could not update account details (" + field + "). " + e.getMessage());
        }
        return false;
    }

    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Database Error: Could not delete account. " + e.getMessage());
            return false;
        }
    }

    // OTP Management
    public String generateAndStoreOTP(String accountNumber) {
        Account account = getAccount(accountNumber);
        if (account != null && "ACTIVE".equals(account.getStatus())) {
            String otp = String.valueOf(100000 + new Random().nextInt(900000));
            String sql = "UPDATE accounts SET otp = ? WHERE account_number = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, otp);
                pstmt.setString(2, accountNumber);
                if (pstmt.executeUpdate() > 0) {
                    String subject = "Your One-Time Password (OTP) for Account Update";
                    String body = "Dear " + account.getAccountHolderName() + ",\n\n"
                                + "Your One-Time Password (OTP) for updating your account details is: " + otp + "\n\n"
                                + "This OTP is valid for a short time. Please do not share it with anyone.\n\n"
                                + "If you did not request this change, please contact customer support immediately.\n\n"
                                + "Sincerely,\nThe " + account.getBankName() + " Team";
                    sendEmailAsync(account.getEmail(), subject, body);
                    System.out.println("OTP generated and stored for account " + accountNumber);
                    return otp;
                }
            } catch (SQLException e) {
                System.err.println("Database Error: Could not store OTP. " + e.getMessage());
            }
        } else {
            System.err.println("Cannot generate OTP: Account not found or not active.");
        }
        return null;
    }

    private boolean verifyStoredOTP(String accountNumber, String enteredOtp) {
        String sql = "SELECT otp FROM accounts WHERE account_number = ?";
        String storedOtp = null;
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                storedOtp = rs.getString("otp");
            }
        } catch (SQLException e) {
            System.err.println("Database Error: Could not verify OTP. " + e.getMessage());
            return false;
        }

        boolean isValid = storedOtp != null && !storedOtp.isEmpty() && storedOtp.equals(enteredOtp);
        clearStoredOTP(accountNumber);
        return isValid;
    }

    private void clearStoredOTP(String accountNumber) {
        String sql = "UPDATE accounts SET otp = NULL WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error: Could not clear OTP. " + e.getMessage());
        }
    }

    // Helper Methods
    private void addTransaction(String accountNumber, String type, double amount, double balanceAfter, Connection conn) throws SQLException {
        String sql = "INSERT INTO transactions(account_number, transaction_type, amount, balance_after) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, balanceAfter);
            pstmt.executeUpdate();
        }
    }

    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        Date dobSql = rs.getDate("date_of_birth");
        LocalDate dob = (dobSql != null) ? dobSql.toLocalDate() : null;

        return new Account(
            rs.getString("account_number"),
            rs.getString("name"),
            rs.getInt("pin"),
            rs.getDouble("balance"),
            rs.getString("email"),
            rs.getString("phone_number"),
            rs.getString("status"),
            dob,
            rs.getString("bank_name"),
            rs.getDouble("initial_deposit"),
            rs.getString("account_type")
        );
    }

    private String buildPendingEmailBody(String name, String accNum, String email, String phone, String bankName, double deposit, String accountType) {
        return "Dear " + name + ",\n\n" +
               "Welcome to " + bankName + "! Your account application has been received.\n\n" +
               "--- Your Account Details ---\n" +
               "Account Holder: " + name + "\n" +
               "Account Number: " + accNum + "\n" +
               "Account Type: " + accountType + "\n" +
               "Bank: " + bankName + "\n" +
               "Initial Deposit: ₹" + String.format("%.2f", deposit) + "\n" +
               "Registered Email: " + email + "\n" +
               "Registered Phone: " + phone + "\n\n" +
               "--- Account Status ---\n" +
               "Status: PENDING APPROVAL\n\n" +
               "An administrator needs to approve your account before you can log in. " +
               "Your initial deposit will be available upon approval.\n\n" +
               "You will receive another email once your account is activated.\n\n" +
               "Thank you for choosing " + bankName + ".\n\n" +
               "Sincerely,\nThe " + bankName + " Team";
    }

    private String buildActivationEmailBody(Account account) {
        return "Dear " + account.getAccountHolderName() + ",\n\n" +
               "Great news! Your bank account at " + account.getBankName() + " has been activated successfully.\n\n" +
               "--- Your Active Account Details ---\n" +
               "Account Holder: " + account.getAccountHolderName() + "\n" +
               "Account Number: " + account.getAccountNumber() + "\n" +
               "Bank: " + account.getBankName() + "\n" +
               "Current Balance: ₹" + String.format("%.2f", account.getBalance()) + " (Initial Deposit Added)\n" +
               "Status: ACTIVE\n\n" +
               "You can now log in to your account and start using all banking services.\n\n" +
               "--- Security Tips ---\n" +
               "• Never share your PIN with anyone\n" +
               "• Keep your login credentials secure\n" +
               "• Report any suspicious activity immediately\n\n" +
               "Welcome aboard!\n\n" +
               "Sincerely,\nThe " + account.getBankName() + " Team";
    }

private void sendEmailAsync(String toEmail, String subject, String body) {
    if (toEmail == null || toEmail.trim().isEmpty()) {
        System.err.println("Cannot send email: Recipient address is empty.");
        return;
    }
    
    emailExecutor.submit(() -> {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", EMAIL_HOST);
        properties.put("mail.smtp.port", EMAIL_PORT);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.put("mail.smtp.connectiontimeout", "30000"); 
        properties.put("mail.smtp.timeout", "30000");
        properties.put("mail.smtp.writetimeout", "30000");
        
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.starttls.required", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });
        
        session.setDebug(true); 
        
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            
            System.out.println("Attempting to send email to: " + toEmail);
            Transport.send(message);
            System.out.println("✅ Email sent successfully to " + toEmail);
            
        } catch (AuthenticationFailedException authEx) {
            System.err.println("❌ Email Authentication Failed for " + EMAIL_USERNAME);
            System.err.println("   Please verify: ");
            System.err.println("   1. 2-Factor Authentication is enabled");
            System.err.println("   2. You're using an App Password (not your regular password)");
            System.err.println("   3. App Password is generated for 'Mail'");
            System.err.println("   Error: " + authEx.getMessage());
            
        } catch (MessagingException e) {
            System.err.println("❌ Email sending failed to " + toEmail);
            System.err.println("   Error: " + e.getMessage());
            e.printStackTrace();
            
        } catch (Exception e) {
            System.err.println("❌ Unexpected error during email sending: " + e.getMessage());
            e.printStackTrace();
        }
    });
}

    public void updateAccountDetails(Account account) {
        String sql = "UPDATE accounts SET name = ?, email = ?, phone_number = ?, pin = ? WHERE account_number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountHolderName());
            pstmt.setString(2, account.getEmail());
            pstmt.setString(3, account.getPhoneNumber());
            pstmt.setInt(4, account.getPin());
            pstmt.setString(5, account.getAccountNumber());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error: Could not update account details. " + e.getMessage());
        }
    }

    public void shutdownEmailService() {
        emailExecutor.shutdown();
        System.out.println("Email service shut down requested.");
    }
}
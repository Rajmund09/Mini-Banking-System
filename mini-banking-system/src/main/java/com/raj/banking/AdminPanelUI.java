// AdminPanelUI.java (Modern Professional Admin Panel)

package com.raj.banking;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminPanelUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final Color ADMIN_PRIMARY = new Color(103, 58, 183);
    private static final Color ADMIN_SECONDARY = new Color(156, 39, 176);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color SUCCESS_COLOR = new Color(56, 142, 60);
    private static final Color WARNING_COLOR = new Color(229, 57, 53);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(97, 97, 97);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 25);

    private final Bank bank;
    private DefaultListModel<String> pendingAccountsModel;
    private JList<String> pendingAccountsList;
    private JLabel statsLabel;

    public AdminPanelUI(Bank bank) {
        this.bank = bank;
        this.pendingAccountsModel = new DefaultListModel<>();
        initializeFrame();
        setupUI();
        loadPendingAccounts();
    }

    private void initializeFrame() {
        setTitle("👨‍💼 Admin Panel - NeoBank System");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createModernHeader(), BorderLayout.NORTH);
        add(createTabbedPane(), BorderLayout.CENTER);

        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createModernHeader() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, ADMIN_PRIMARY,
                        getWidth(), getHeight(), ADMIN_SECONDARY);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(getWidth(), 120));
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Left side - Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("👨‍💼");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        iconLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Admin Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Banking System Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(textPanel);

        JPanel statsPanel = createQuickStatsPanel();

        header.add(titlePanel, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createQuickStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        panel.setOpaque(false);

        String[][] pendingAccounts = bank.getPendingAccounts();
        int pendingCount = pendingAccounts != null ? pendingAccounts.length : 0;

        statsLabel = new JLabel(String.format("📊 %d Pending Accounts", pendingCount));
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setBorder(new EmptyBorder(8, 15, 8, 15));
        statsLabel.setBackground(new Color(255, 255, 255, 30));
        statsLabel.setOpaque(true);
        statsLabel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(255, 255, 255, 80)),
                new EmptyBorder(8, 15, 8, 15)));

        panel.add(statsLabel);
        return panel;
    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(BACKGROUND_COLOR);

        tabbedPane.addTab("📋 Account Approvals", createApprovalsPanel());

        tabbedPane.addTab("👥 Account Management", createManagementPanel());

        tabbedPane.addTab("📊 System Overview", createOverviewPanel());

        return tabbedPane;
    }

    private JPanel createApprovalsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new ModernCard();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Pending Account Approvals");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JButton refreshBtn = new AdminButton("🔄 Refresh List", ADMIN_SECONDARY);
        refreshBtn.addActionListener(e -> loadPendingAccounts());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        JPanel listPanel = new ModernCard();
        listPanel.setLayout(new BorderLayout());
        listPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        pendingAccountsList = new JList<>(pendingAccountsModel);
        pendingAccountsList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        pendingAccountsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingAccountsList.setCellRenderer(new PendingAccountRenderer());

        JScrollPane scrollPane = new JScrollPane(pendingAccountsList);
        scrollPane.setBorder(new LineBorder(new Color(220, 220, 220)));
        scrollPane.setPreferredSize(new Dimension(300, 350));

        JPanel controlsPanel = createControlsPanel();

        listPanel.add(scrollPane, BorderLayout.CENTER);
        listPanel.add(controlsPanel, BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(15));
        panel.add(listPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.setBackground(CARD_COLOR);

        JTextField accNumField = new JTextField(15);
        accNumField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        accNumField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 12, 10, 12)));

        // Auto-fill from selection
        pendingAccountsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && pendingAccountsList.getSelectedValue() != null) {
                String selected = pendingAccountsList.getSelectedValue();
                if (!selected.startsWith("No pending")) {
                    String accNum = extractAccountNumber(selected);
                    accNumField.setText(accNum);
                }
            }
        });

        JButton approveSelectedBtn = new AdminButton("✅ Approve Selected", SUCCESS_COLOR);
        JButton approveManualBtn = new AdminButton("✅ Approve by Number", SUCCESS_COLOR);
        JButton viewDetailsBtn = new AdminButton("👁️ View Details", ADMIN_PRIMARY);

        approveSelectedBtn.addActionListener(e -> approveSelectedAccount(accNumField));
        approveManualBtn.addActionListener(e -> approveManualAccount(accNumField));
        viewDetailsBtn.addActionListener(e -> viewSelectedAccountDetails());

        inputPanel.add(new JLabel("Account Number:"));
        inputPanel.add(accNumField);
        inputPanel.add(approveSelectedBtn);
        inputPanel.add(approveManualBtn);
        inputPanel.add(viewDetailsBtn);

        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new ModernCard();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Account Management Tools");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel toolsPanel = new ModernCard();
        toolsPanel.setLayout(new GridLayout(2, 2, 20, 20));
        toolsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel searchTool = createToolCard("🔍 Account Search", "Find and view any account details", "SEARCH");
        toolsPanel.add(searchTool);

        JPanel bulkTool = createToolCard("🚀 Bulk Operations", "Manage multiple accounts at once", "BULK");
        toolsPanel.add(bulkTool);

        JPanel transactionTool = createToolCard("📊 Transaction Logs", "View all transaction history", "TRANSACTIONS");
        toolsPanel.add(transactionTool);

        JPanel reportsTool = createToolCard("📈 System Reports", "Generate banking system reports", "REPORTS");
        toolsPanel.add(reportsTool);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(15));
        panel.add(toolsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createToolCard(String title, String description, String action) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 245, 245));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(20, 20, 20, 20)));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel descLabel = new JLabel("<html><div style='width: 180px;'>" + description + "</div></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_SECONDARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(Box.createVerticalStrut(10));
        card.add(descLabel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(235, 235, 235));
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
            }

            public void mouseClicked(MouseEvent e) {
                handleToolAction(action);
            }
        });

        return card;
    }

    private JPanel createOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new ModernCard();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("System Overview & Analytics");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JButton refreshBtn = new AdminButton("🔄 Refresh Stats", ADMIN_SECONDARY);
        refreshBtn.addActionListener(e -> refreshStatistics());

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);

        // Statistics cards
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Sample statistics (in real implementation, these would come from the
        // database)
        statsPanel.add(createStatCard("📈 Total Accounts", "1,247", ADMIN_PRIMARY, "Active banking accounts"));
        statsPanel.add(createStatCard("⏳ Pending Approval", String.valueOf(getPendingCount()), WARNING_COLOR,
                "Awaiting activation"));
        statsPanel.add(createStatCard("✅ Active Today", "23", SUCCESS_COLOR, "Accounts active today"));
        statsPanel.add(createStatCard("💰 Total Balance", "₹2.4M", new Color(255, 152, 0), "System total balance"));
        statsPanel.add(createStatCard("📊 Transactions", "15,482", new Color(33, 150, 243), "Total transactions"));
        statsPanel.add(createStatCard("👥 New Users", "47", new Color(156, 39, 176), "This month"));

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String description) {
        JPanel card = new ModernCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(TEXT_SECONDARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(descLabel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                new EmptyBorder(8, 20, 8, 20)));

        JLabel statusLabel = new JLabel("Ready - Admin Panel Active");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_SECONDARY);

        JLabel timestampLabel = new JLabel("NeoBank Admin v2.0");
        timestampLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timestampLabel.setForeground(TEXT_SECONDARY);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timestampLabel, BorderLayout.EAST);

        return statusBar;
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
            g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 15, 15);

            g2d.setColor(getBackground());
            g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);

            super.paintComponent(g);
        }
    }

    private class PendingAccountRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);

            label.setBorder(new EmptyBorder(12, 20, 12, 20));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (isSelected) {
                label.setBackground(ADMIN_PRIMARY);
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(index % 2 == 0 ? new Color(248, 248, 248) : Color.WHITE);
                label.setForeground(TEXT_PRIMARY);
            }

            return label;
        }
    }

    private void loadPendingAccounts() {
        pendingAccountsModel.clear();
        String[][] pendingAccounts = bank.getPendingAccounts();

        if (pendingAccounts != null && pendingAccounts.length > 0) {
            for (String[] account : pendingAccounts) {
                if (account != null && account.length >= 3) {
                    String displayText = String.format("%s - %s (%s)",
                            account[0], account[1], account[2]);
                    pendingAccountsModel.addElement(displayText);
                }
            }
        }

        if (pendingAccountsModel.isEmpty()) {
            pendingAccountsModel.addElement("No pending accounts for approval");
        }

        updateStats();
    }

    private void updateStats() {
        String[][] pendingAccounts = bank.getPendingAccounts();
        int pendingCount = pendingAccounts != null ? pendingAccounts.length : 0;
        statsLabel.setText(String.format("📊 %d Pending Accounts", pendingCount));
    }

    private int getPendingCount() {
        String[][] pendingAccounts = bank.getPendingAccounts();
        return pendingAccounts != null ? pendingAccounts.length : 0;
    }

    private void approveSelectedAccount(JTextField accNumField) {
        String selected = pendingAccountsList.getSelectedValue();
        if (selected != null && !selected.startsWith("No pending")) {
            String accNum = extractAccountNumber(selected);
            approveAccount(accNum);
            accNumField.setText("");
        } else {
            showError("Please select an account from the list.");
        }
    }

    private void approveManualAccount(JTextField accNumField) {
        String accNum = accNumField.getText().trim();
        if (!accNum.isEmpty()) {
            approveAccount(accNum);
            accNumField.setText("");
        } else {
            showError("Please enter an account number.");
        }
    }

    private void viewSelectedAccountDetails() {
        String selected = pendingAccountsList.getSelectedValue();
        if (selected != null && !selected.startsWith("No pending")) {
            String accNum = extractAccountNumber(selected);
            searchAccount(accNum);
        } else {
            showError("Please select an account from the list.");
        }
    }

    private void handleToolAction(String action) {
        switch (action) {
            case "SEARCH":
                showSearchDialog();
                break;
            case "BULK":
                showBulkOperationsDialog();
                break;
            case "TRANSACTIONS":
                showTransactionViewer();
                break;
            case "REPORTS":
                showReportsDialog();
                break;
        }
    }

    private void showSearchDialog() {
        String accountNumber = JOptionPane.showInputDialog(this,
                "Enter Account Number to search:", "Account Search", JOptionPane.QUESTION_MESSAGE);

        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            searchAccount(accountNumber.trim());
        }
    }

    private void showBulkOperationsDialog() {
        int result = JOptionPane.showConfirmDialog(this,
                "This will approve ALL pending accounts.\n\n" +
                        "Are you sure you want to continue?",
                "Bulk Account Approval",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            showInfo("Bulk Approval",
                    "Bulk approval feature would approve all pending accounts.\nThis feature is under development.");
        }
    }

    private void showTransactionViewer() {
        String accountNumber = JOptionPane.showInputDialog(this,
                "Enter Account Number to view transactions:", "Transaction Viewer", JOptionPane.QUESTION_MESSAGE);

        if (accountNumber != null && !accountNumber.trim().isEmpty()) {
            viewAccountTransactions(accountNumber.trim());
        }
    }

    private void showReportsDialog() {
        showInfo("System Reports",
                "This feature would generate comprehensive banking system reports.\nAvailable reports:\n• Account Activity Summary\n• Transaction Analytics\n• User Growth Reports\n• Financial Overview");
    }

    private void approveAccount(String accountNumber) {
        if (bank.approveAccount(accountNumber)) {
            showSuccess("✅ Account " + accountNumber + " approved successfully!\n\n" +
                    "• Account status changed to ACTIVE\n" +
                    "• Initial balance activated\n" +
                    "• User notified via email\n" +
                    "• Account ready for transactions");
            loadPendingAccounts();
        } else {
            showError("❌ Failed to approve account " + accountNumber +
                    "\n\nPossible reasons:\n" +
                    "• Account not found\n" +
                    "• Account already approved\n" +
                    "• Database error\n" +
                    "• Network connection issue");
        }
    }

    private void searchAccount(String accountNumber) {
        Account account = bank.getAccount(accountNumber);
        if (account != null) {
            showAccountDetails(account);
        } else {
            showError("Account not found: " + accountNumber);
        }
    }

    private void viewAccountTransactions(String accountNumber) {
        Account account = bank.getAccount(accountNumber);
        if (account != null) {
            List<String> transactions = bank.getTransactionHistory(accountNumber);

            if (transactions.isEmpty()) {
                showInfo("No Transactions", "No transactions found for account: " + accountNumber);
                return;
            }

            JDialog transactionDialog = new JDialog(this, "Transaction History - " + accountNumber, true);
            transactionDialog.setSize(700, 500);
            transactionDialog.setLocationRelativeTo(this);
            transactionDialog.getContentPane().setBackground(BACKGROUND_COLOR);

            JTextArea textArea = new JTextArea();
            textArea.setEditable(false);
            textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
            textArea.setBackground(new Color(30, 30, 30));
            textArea.setForeground(Color.GREEN);

            StringBuilder history = new StringBuilder();
            history.append("Transaction History for: ").append(accountNumber).append("\n");
            history.append("Account Holder: ").append(account.getAccountHolderName()).append("\n");
            history.append("Bank: ").append(account.getBankName()).append("\n");
            history.append("Current Balance: ₹").append(String.format("%.2f", account.getBalance())).append("\n\n");

            for (String transaction : transactions) {
                history.append(transaction).append("\n");
            }

            textArea.setText(history.toString());
            JScrollPane scrollPane = new JScrollPane(textArea);

            transactionDialog.add(scrollPane);
            transactionDialog.setVisible(true);
        } else {
            showError("Account not found: " + accountNumber);
        }
    }

    private void refreshStatistics() {
        updateStats();
        showSuccess("Statistics refreshed successfully!");
    }

    private String extractAccountNumber(String displayText) {
        if (displayText.contains(" - ")) {
            return displayText.split(" - ")[0];
        }
        return displayText;
    }

    private void showAccountDetails(Account account) {
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 8, 8));
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
        } else if ("PENDING".equals(status)) {
            return "🟡 PENDING";
        } else {
            return "⚪ " + status;
        }
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(CARD_COLOR);

        JLabel keyLabel = new JLabel(label);
        keyLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        keyLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        valueLabel.setForeground(TEXT_PRIMARY);

        rowPanel.add(keyLabel, BorderLayout.WEST);
        rowPanel.add(valueLabel, BorderLayout.CENTER);
        panel.add(rowPanel);
    }

    private void showSuccess(String message) {
        showDialog("✅ Success", message, SUCCESS_COLOR);
    }

    private void showError(String message) {
        showDialog("❌ Error", message, WARNING_COLOR);
    }

    private void showInfo(String title, String message) {
        showDialog("💡 " + title, message, ADMIN_PRIMARY);
    }

    private void showDialog(String title, String message, Color color) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));
        panel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(color);

        JLabel messageLabel = new JLabel("<html><div style='width: 400px;'>" + message + "</div></html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(TEXT_SECONDARY);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Admin Panel",
                JOptionPane.INFORMATION_MESSAGE);
    }

    class AdminButton extends JButton {
        public AdminButton(String text, Color color) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(true);
            setBackground(color);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 20, 12, 20));
            setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(color.darker(), 1),
                    new EmptyBorder(10, 18, 10, 18)));

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

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            Bank testBank = new Bank();
            new AdminPanelUI(testBank).setVisible(true);
        });
    }
}
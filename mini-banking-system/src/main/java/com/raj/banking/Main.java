package com.raj.banking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Bank bank = new Bank();
    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🚀 Welcome to the Enhanced Banking System!");
        System.out.println("========================================");

        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create New Account");
            System.out.println("2. View Account Details");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Transfer Money");
            System.out.println("6. Update Account Details");
            System.out.println("7. Delete Account");
            System.out.println("8. View Transaction History");
            System.out.println("9. Exit");
            System.out.print("Please select an option: ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1: handleCreateAccount(); break;
                    case 2: handleViewDetails(); break;
                    case 3: handleDeposit(); break;
                    case 4: handleWithdraw(); break;
                    case 5: handleTransferMoney(); break;
                    case 6: handleUpdateAccount(); break;
                    case 7: handleDeleteAccount(); break;
                    case 8: handleTransactionHistory(); break;
                    case 9:
                        System.out.println("\nThank you for banking with us. Goodbye! 👋");
                        bank.shutdownEmailService();
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("❌ Invalid option. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void handleCreateAccount() {
        System.out.println("\n--- Create New Account ---");
        try {
            System.out.print("Enter full name: ");
            String name = scanner.nextLine();

            System.out.print("Enter date of birth (YYYY-MM-DD): ");
            LocalDate dateOfBirth = null;
            try {
                dateOfBirth = LocalDate.parse(scanner.nextLine(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format. Please use YYYY-MM-DD.");
                return;
            }

            System.out.print("Enter email address: ");
            String email = scanner.nextLine();

            System.out.print("Enter phone number: ");
            String phone = scanner.nextLine();

            System.out.println("\nAvailable Banks:");
            String[] banks = bank.getAvailableBanks();
            for (int i = 0; i < banks.length; i++) {
                System.out.println((i + 1) + ". " + banks[i]);
            }
            System.out.print("Select bank (1-" + banks.length + "): ");
            int bankChoice = scanner.nextInt();
            scanner.nextLine();
            if (bankChoice < 1 || bankChoice > banks.length) {
                System.out.println("❌ Invalid bank selection.");
                return;
            }
            String selectedBank = banks[bankChoice - 1];

            System.out.println("Select Account Type:");
            System.out.println("1. Savings\n2. Salary\n3. Business");
            System.out.print("Enter choice (1-3): ");
            int typeChoice = scanner.nextInt();
            scanner.nextLine();
            String accountType;
            switch (typeChoice) {
                case 1: accountType = "Savings"; break;
                case 2: accountType = "Salary"; break;
                case 3: accountType = "Business"; break;
                default: 
                    System.out.println("Invalid type selected. Defaulting to Savings."); 
                    accountType = "Savings"; 
                    break;
            }

            System.out.print("Enter initial deposit (minimum ₹1000): ₹");
            double initialDeposit = scanner.nextDouble();
            scanner.nextLine();

            System.out.print("Create a 4-digit PIN: ");
            int pin = scanner.nextInt();
            scanner.nextLine();

            if (String.valueOf(pin).length() != 4) {
                System.out.println("❌ PIN must be exactly 4 digits.");
                return;
            }

            String accNum = bank.createAccount(name, pin, email, phone, dateOfBirth, selectedBank, initialDeposit, accountType);

            if (accNum != null) {
                System.out.println("\n✅ Account submitted for approval!");
                System.out.println("Your new Account Number is: " + accNum);
                System.out.println("Status: PENDING (Waiting for admin approval)");
                System.out.println("A welcome email has been sent to: " + email);
            } else {
                System.out.println("\n❌ Failed to create account.");
            }
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid input. Please enter correct data types.");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Account Creation Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void handleViewDetails() {
        System.out.println("\n--- View Account Details ---");
        Account account = authenticateUser("view details");
        if (account != null) {
            System.out.println("\n--- Account Information ---");
            System.out.println("Account Holder: " + account.getAccountHolderName());
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Account Type: " + account.getAccountType());
            System.out.printf("Balance: ₹%.2f\n", account.getBalance());
            System.out.println("Email: " + account.getEmail());
            System.out.println("Phone Number: " + account.getPhoneNumber());
            System.out.println("Bank: " + account.getBankName());
            System.out.println("Status: " + account.getStatus());
        } else {
            System.out.println("❌ Authentication failed or account not found/active.");
        }
    }

    private static void handleTransactionHistory() {
        System.out.println("\n--- Transaction History ---");
        Account account = authenticateUser("view history");
        if (account != null) {
            List<String> transactions = bank.getTransactionHistory(account.getAccountNumber());
            if (transactions.isEmpty()) {
                System.out.println("No transactions found for this account.");
            } else {
                System.out.println("\n--- Transaction History for " + account.getAccountNumber() + " ---");
                for (String transaction : transactions) {
                    System.out.println(transaction);
                }
            }
        } else {
            System.out.println("❌ Authentication failed or account not active.");
        }
    }

    private static Account authenticateUser(String action) {
        System.out.println("\n--- Authentication required to " + action + " ---");
        System.out.print("Enter account number: ");
        String accNum = scanner.nextLine();
        System.out.print("Enter PIN: ");
        int pin = -1;
        try {
            pin = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("❌ Invalid PIN format. Please enter numbers only.");
            scanner.nextLine();
            return null;
        }
        scanner.nextLine();

        Account account = bank.login(accNum, pin);
        if (account != null) {
            return account;
        }
        System.out.println("❌ Authentication failed. Check account number, PIN, or account status.");
        return null;
    }

    private static void handleDeposit() {
        System.out.println("\n--- Deposit Funds ---");
        Account account = authenticateUser("deposit funds");
        if (account != null) {
            System.out.print("Enter amount to deposit: ₹");
            double amount = -1;
            try {
                amount = scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid amount format.");
                scanner.nextLine();
                return;
            }
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("❌ Deposit amount must be positive.");
                return;
            }

            account.deposit(amount);
            bank.updateAccountBalance(account, "Deposit", amount);
            System.out.printf("✅ Deposit successful. New balance: ₹%.2f\n", account.getBalance());
        }
    }

    private static void handleWithdraw() {
        System.out.println("\n--- Withdraw Funds ---");
        Account account = authenticateUser("withdraw funds");
        if (account != null) {
            System.out.print("Enter amount to withdraw: ₹");
            double amount = -1;
            try {
                amount = scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid amount format.");
                scanner.nextLine();
                return;
            }
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("❌ Withdrawal amount must be positive.");
                return;
            }

            if (account.withdraw(amount)) {
                bank.updateAccountBalance(account, "Withdrawal", amount);
                System.out.printf("✅ Withdrawal successful. New balance: ₹%.2f\n", account.getBalance());
            } else {
                System.out.println("❌ Withdrawal failed. Insufficient balance.");
            }
        }
    }

    private static void handleTransferMoney() {
        System.out.println("\n--- Transfer Money ---");
        Account senderAccount = authenticateUser("initiate transfer");
        if (senderAccount != null) {
            System.out.print("Enter recipient's account number: ");
            String recipientAccNum = scanner.nextLine();

            System.out.print("Enter amount to transfer: ₹");
            double amount = -1;
            try {
                amount = scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid amount format.");
                scanner.nextLine();
                return;
            }
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("❌ Transfer amount must be positive.");
                return;
            }

            if (bank.transferMoney(senderAccount.getAccountNumber(), recipientAccNum, amount, senderAccount.getPin())) {
                System.out.printf("✅ Transfer successful. Your new balance: ₹%.2f\n", senderAccount.getBalance());
            } else {
                System.out.println("❌ Transfer failed. Check recipient account or your balance.");
            }
        }
    }

    private static void handleUpdateAccount() {
        System.out.println("\n--- Update Account Details ---");
        Account account = authenticateUser("update details");
        if (account != null) {
            System.out.println("What would you like to update?");
            System.out.println("1. Name\n2. PIN\n3. Email\n4. Phone Number");
            System.out.print("Select option: ");
            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("❌ Invalid choice format.");
                scanner.nextLine();
                return;
            }
            scanner.nextLine();

            String fieldToUpdate = null;
            String fieldDisplayName = null;

            switch (choice) {
                case 1: fieldToUpdate = "name"; fieldDisplayName = "Name"; break;
                case 2: fieldToUpdate = "pin"; fieldDisplayName = "PIN"; break;
                case 3: fieldToUpdate = "email"; fieldDisplayName = "Email"; break;
                case 4: fieldToUpdate = "phone_number"; fieldDisplayName = "Phone Number"; break;
                default: System.out.println("❌ Invalid choice."); return;
            }

            System.out.print("Enter new " + fieldDisplayName.toLowerCase() + ": ");
            String newValue = scanner.nextLine();

            boolean success = false;
            try {
                switch (fieldToUpdate) {
                    case "name": account.setAccountHolderName(newValue); success = true; break;
                    case "email": account.setEmail(newValue); success = true; break;
                    case "phone_number": account.setPhoneNumber(newValue); success = true; break;
                    case "pin":
                        int newPin = Integer.parseInt(newValue);
                        if (newValue.length() == 4) {
                            account.setPin(newPin);
                            success = true;
                        } else {
                            System.out.println("❌ PIN must be 4 digits.");
                        }
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid format for PIN.");
            }

            if (success) {
                String otp = bank.generateAndStoreOTP(account.getAccountNumber());
                if (otp != null) {
                    System.out.print("Enter OTP sent to your email/phone: ");
                    String enteredOTP = scanner.nextLine();
                    
                    if (bank.updateAccountDetailsWithOTP(account, fieldToUpdate, newValue, enteredOTP)) {
                        System.out.println("✅ " + fieldDisplayName + " updated successfully!");
                    } else {
                        System.out.println("❌ OTP verification failed. Update cancelled.");
                    }
                } else {
                    System.out.println("❌ Failed to generate OTP. Please try again.");
                }
            }
        }
    }

    private static void handleDeleteAccount() {
        System.out.println("\n--- Delete Account ---");
        Account account = authenticateUser("delete account");
        if (account != null) {
            System.out.print("This will permanently delete account [" + account.getAccountNumber() + "]. Are you sure? (Y/N): ");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("Y")) {
                if (bank.deleteAccount(account.getAccountNumber())) {
                    System.out.println("✅ Account deleted successfully.");
                } else {
                    System.out.println("❌ Failed to delete account.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        }
    }
}
package com.raj.banking;

import java.time.LocalDate;
import java.time.Period;
import java.util.Random;

public class Account {
    private String accountNumber;
    private String accountHolderName;
    private int pin;
    private double balance;
    private String email;
    private String phoneNumber;
    private String status;
    private LocalDate dateOfBirth;
    private String bankName;
    private String accountType;
    private double initialDeposit;
    private String otp;
    private LocalDate accountCreationDate;

    public Account(String accountNumber, String accountHolderName, int pin, double initialBalance,
            String email, String phoneNumber, String status, LocalDate dateOfBirth, String bankName,
            double initialDeposit, String accountType) {
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.pin = pin;
        this.balance = initialBalance;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.dateOfBirth = dateOfBirth;
        this.bankName = bankName;
        this.initialDeposit = initialDeposit;
        this.accountType = accountType;
        this.accountCreationDate = LocalDate.now();
        this.otp = null;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public int getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getBankName() {
        return bankName;
    }

    public double getInitialDeposit() {
        return initialDeposit;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getOtp() {
        return otp;
    }

    public LocalDate getAccountCreationDate() {
        return accountCreationDate;
    }

    public int getAccountHolderAge() {
        if (dateOfBirth == null)
            return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean validatePin(int enteredPin) {
        return this.pin == enteredPin;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > this.balance) {
            return false;
        }
        this.balance -= amount;
        return true;
    }

    public String generateOTP() {
        int otpValue = 100000 + new Random().nextInt(900000);
        this.otp = String.valueOf(otpValue);
        System.out.println("Generated OTP for account " + accountNumber + ": " + this.otp + " (For testing only)"); // For
                                                                                                                    // testing
        return this.otp;
    }

    public boolean verifyOTP(String inputOtp) {
        boolean isValid = this.otp != null && this.otp.equals(inputOtp);
        if (isValid) {
            clearOTP();
        }
        return isValid;
    }

    public void clearOTP() {
        this.otp = null;
    }
}
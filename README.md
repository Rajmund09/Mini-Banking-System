# Mini Banking System (Java + MySQL)

A console-based Banking Management System built using **Java**, **Swing/AWT**, and **MySQL** with **JDBC integration**.  
This project simulates a real-world banking workflow including account creation, authentication, deposits, withdrawals, transfers, transaction history, and admin approval.

---

## 🚀 Features

### 🧑‍💼 User Features
- Create a new bank account (requires admin approval)
- Secure login with password verification
- Deposit money
- Withdraw money
- Balance inquiry
- Transfer money to another user
- Update user details after account creation
- View full transaction history
- Automated email notifications for each action

### 🔐 Admin Features
- Approve or reject new account requests  
- View all users  
- Modify or delete user accounts  
- Monitor transactions  
- Overall system control  

---

## 🛠️ Tech Stack

### **Languages**
- Java (Core + OOP)
- SQL (MySQL)

### **Frameworks & Libraries**
- Swing / AWT — for front-end GUI  
- JDBC — database connectivity  
- JavaMail API — automated email notifications  

### **Tools & Software**
- MySQL Workbench  
- IntelliJ IDEA / VS Code / Eclipse  
- Git & GitHub  

---

## 📂 Project Architecture

/src
├── ui/ # Swing UI components
├── database/ # JDBC connection + SQL operations
├── models/ # User, Account, Transaction classes
├── services/ # Business logic (deposit, withdraw, transfer)
├── admin/ # Admin approval, management panel
└── utils/ # Email service, validation, helpers


---

## 🔧 Setup Instructions

### 1️⃣ Clone the Repository
```bash
git clone https://github.com/Rajmund09/mini-banking-system.git
cd mini-banking-system

2️⃣Configure MySQL Database

Create a database:

CREATE DATABASE bank_system;


Import the provided .sql file (tables + sample data).

3️⃣ Update Database Credentials

Inside DatabaseConnection.java, update:

String url = "jdbc:mysql://localhost:3306/bank_system";
String username = "root";
String password = "YOUR_PASSWORD";

4️⃣ Run the Project

Compile & run:

javac Main.java
java Main

### 📧 Email Integration

The project uses JavaMail API to send:

Account approval mails

Transaction alerts

Password change notifications

Setup your email & app password inside EmailService.java.

### 🔒 Security Features

Password hashing

Data validation

Admin-only operations

Protected database access

Email verification for actions

### 🎯 Project Objectives

Implement core OOP concepts

Build a real-world banking workflow

Integrate Java with MySQL

Understand full-stack application architecture

Use secure coding practices



# 🏦 NeoBank - Modern Banking Management System

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Swing](https://img.shields.io/badge/UI-Swing%2FAWT-red.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

A professional, feature-rich Banking Management System built with **Java Swing**, **JDBC**, and **MySQL**. This system features a dual-interface architecture for both customers and bank administrators, providing a complete banking workflow from account application to transaction management.

---

## 🚀 Key Features

### 👤 Customer Features
- **Modern Dashboard**: Intuitive, glassmorphism-inspired UI with live clock.
- **Account Creation**: Interactive application form (requires admin activation).
- **Secure Banking**: Deposit, Withdraw, and **Peer-to-Peer Transfers**.
- **Real-time Notifications**: Automated email alerts for all account activities.
- **OTP Verification**: Secure 2FA for updating sensitive account details.
- **Transaction History**: Comprehensive log of all financial activities.

### 👨‍💼 Admin Features
- **Smart Approvals**: Manage pending account requests with a professional JTable interface.
- **Bulk Operations**: One-click approval for all pending applications.
- **Live Analytics**: Real-time system stats (Total users, System balance, Transaction volume).
- **Account Management**: Search, view, and reject/delete accounts.
- **Transaction Monitoring**: Global view of all system transactions.

---

## 🏗️ Project Architecture

```text
src/main/java/com/raj/banking/
├── Bank.java           # Core Service Layer (JDBC, Email, Business Logic)
├── Account.java        # Domain Model (Account Entity)
├── BankingAppUI.java   # Customer Graphical Interface
├── AdminPanelUI.java   # Administrator Management Panel
└── Main.java           # Console-based Interface
```

---

## 🔧 Setup & Installation

### 1️⃣ Prerequisites
- **Java JDK 17** or higher.
- **MySQL Server 8.0**.
- **Maven** (for dependency management).

### 2️⃣ Database Configuration
Create a new database named `bank_db` in your MySQL instance:
```sql
CREATE DATABASE bank_db;
```
*Note: The system automatically generates all necessary tables (`accounts`, `transactions`) on the first run.*

### 3️⃣ Configure Credentials
1. Navigate to `src/main/resources/`.
2. Create or edit `config.properties`.
3. Add your credentials:
```properties
db.url=jdbc:mysql://localhost:3306/bank_db
db.user=your_mysql_user
db.password=your_mysql_password
email.username=your_gmail@gmail.com
email.password=your_gmail_app_password
```
> [!IMPORTANT]
> To send emails, use a **Gmail App Password**. Regular passwords will not work due to Google's security policies.

### 4️⃣ Run the Application
You can run the application using Maven or your favorite IDE:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.raj.banking.BankingAppUI"
```

---

## 🛡️ Security Features
- **PIN Authentication**: All customer actions require a 4-digit PIN.
- **Externalized Config**: Sensitive credentials are kept in a git-ignored properties file.
- **Atomic Transactions**: Multi-step operations (like transfers) use SQL transactions with rollback support.
- **OTP Validation**: 6-digit one-time passwords for profile updates.

---

## 👨‍💻 Developed By
**Rajmund09** - *Initial Work & Architecture*

---
*Developed for educational purposes to demonstrate Java OOP, JDBC integration, and Desktop UI design.*

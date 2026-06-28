# 📦 Product Expiry Tracker

A Java Swing desktop application for managing inventory and tracking product expiry dates using MySQL. The system allows users to add, update, delete, search, and monitor products with automatic expiry status detection.

---

## ✨ Features

- Add Products
- Update Products
- Delete Products
- Search Products
- View Expiring Soon Products
- Live Dashboard Statistics
- Automatic Expiry Status
- Color-coded Status Indicators
- MySQL Database Integration
- Modern Java Swing Interface

---
## 🛠️ Tech Stack

- Java
- Java Swing
- JDBC
- MySQL
- MySQL Connector/J
- Object-Oriented Programming (OOP)

---
## 📁 Project Structure

```text
ProductExpiryTracker/
│
├── ProductExpiryTracker.java
├── lib/
│   └── mysql-connector-j-9.7.0.jar
├── .gitignore
└── README.md
```

---
## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Shahanas-P-A/ProductExpiryTracker.git
```

### 2. Open the Project

```bash
cd ProductExpiryTracker
```

### 3. Create the Database

```sql
CREATE DATABASE expiry_tracker;
```

The application will automatically create the required `products` table on first launch.

### 4. Compile

```bash
javac -cp "lib/*" ProductExpiryTracker.java
```

### 5. Run

**Windows**

```bash
java -cp ".;lib/*" ProductExpiryTracker
```

**Linux / macOS**

```bash
java -cp ".:lib/*" ProductExpiryTracker
```

---
## 📋 Application Features

### Product Management
- Add new products
- Update existing products
- Delete products
- View all products

### Expiry Monitoring
- Automatically calculates days until expiry
- Highlights expired products
- Shows products expiring within 30 days
- Color-coded expiry status

### Search & Filtering
- Instant product search
- Expiring Soon filter
- Sortable product table

### Dashboard
- Total Products
- Expired Products
- Critical Products (≤7 days)
- Warning Products (≤30 days)
- Safe Products (>30 days)

---
## 🚀 Future Enhancements

- 📧 Email notifications before product expiry
- 📄 Export reports to PDF or Excel
- 📷 Product image support
- 📊 Charts and analytics dashboard
- 📱 Barcode/QR code scanning
- 👤 User authentication
- 🌙 Dark mode
- ☁️ Cloud database integration

---
## 📚 Learning Outcomes

This project demonstrates:

- Java Swing GUI Development
- JDBC Database Connectivity
- MySQL Integration
- CRUD Operations
- Object-Oriented Programming (OOP)
- Event Handling
- Data Validation
- Desktop Application Development

---
## 👨‍💻 Author

SHAHANAS P A
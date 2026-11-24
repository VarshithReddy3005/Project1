# NEXUS LUXE - E-Commerce & Inventory Management System

Java Swing + MySQL desktop application for luxury e-commerce with admin and customer portals.

## 🎯 Project Status: Week 2 Complete

| Week | Focus | Status |
|------|-------|--------|
| 1 | Requirements, Database Design, Auth Foundation | ✅ Complete |
| 2 | Catalog, Customer Accounts, Shopping Cart | ✅ Complete |
| 3 | Orders, Inventory, Invoices, Alerts | 📅 Pending |
| 4 | Dashboard, Reports, Testing, Packaging | 📅 Pending |

## ✨ Features Implemented

### Authentication & Roles
- **Admin Login** → Opens Admin Dashboard
- **Customer Registration/Login** → Opens Customer Dashboard
- **BCrypt Password Hashing** (jBCrypt)
- **Role-Based Access Control** (ADMIN / CUSTOMER)

### Admin Dashboard
- Live database metrics (customers, products, users)
- **Product Management**: Add/Edit/Delete products (persisted to MySQL)
- **Category Management**: Full CRUD for categories
- **Inventory Table**: Live data from database with stock status
- Low stock alerts (configurable threshold)
- Customer account audit

### Customer Dashboard
- **Product Catalog**: Loaded from database with search & category filters
- **Shopping Cart**: Add items, view cart, checkout simulation
- **Wishlist**: Save/remove favorite items
- **Order History**: Mock order tracking
- **Profile**: Customer info display

### Technical Stack
- **Java 17** (Maven)
- **Java Swing** (Custom animated UI with glassmorphism, parallax, particles)
- **MySQL 8.0** (JDBC with prepared statements)
- **jBCrypt** (Password hashing)
- **DAO/Service/UI Layer Separation**

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+ running locally

### 1. Configure Database
```bash
# Copy and edit database config
cp src/main/resources/db.properties.example src/main/resources/db.properties

# Edit db.properties with your MySQL password
# db.password=your_actual_mysql_password
```

### 2. Create Database Schema
```bash
# Option A: Run via MySQL client
mysql -u root -p < src/main/resources/schema.sql

# Option B: Run each statement in MySQL Workbench
```

### 3. Create First Admin Account
```bash
# Build first
mvn clean compile

# Run admin setup utility
mvn exec:java -Dexec.mainClass="com.ecommerce.util.AdminSetup" -Dexec.args="admin_bhanu SuperSecretPassword123!"
```

### 4. Run Application
```bash
mvn exec:java -Dexec.mainClass="com.ecommerce.ui.LoginFrame"
```

### 5. Test Credentials
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin_bhanu` | `SuperSecretPassword123!` |
| Customer | Register new account via "Create Account" | Your choice |

## 🏃 Run Tests
```bash
# Backend integration tests
mvn exec:java -Dexec.mainClass="com.ecommerce.MainTest"
```

## 📁 Project Structure
```
src/main/java/com/ecommerce/
├── model/          # User, Product, Category
├── dao/            # UserDAO, ProductDAO, CategoryDAO
├── service/        # AuthService, CatalogService
├── ui/             # LoginFrame, RegisterFrame, AdminDashboard, CustomerDashboard
├── util/           # DatabaseConnection, PasswordUtil, AdminSetup
└── MainTest.java   # Integration test suite
```

## 🔧 Configuration

### Database (db.properties)
```properties
db.url=jdbc:mysql://localhost:3306/ecommerce_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
db.user=root
db.password=your_password_here
```

### Schema (schema.sql)
Creates tables: `users`, `categories`, `products` with seed data.

## 🎨 UI Highlights
- **60 FPS Animation Loop** with particle systems
- **Mouse Parallax** on background elements
- **Glassmorphism Cards** with specular sheen
- **3D Vector Illustrations** (shopping bag, gift box, orbital rings)
- **Responsive Layout** with smooth card positioning
- **Custom Controls**: Animated text fields, glow buttons, status banners

## 📋 Week 2 Deliverables Met
- ✅ Customer registration/login flow
- ✅ Admin product/category management (full CRUD)
- ✅ Product search & category filters
- ✅ Product details with stock display
- ✅ Shopping cart operations
- ✅ Validation (required fields, quantities, prices, duplicates)
- ✅ JDBC Prepared Statements
- ✅ DAO/Service separation

## 🔮 Next Steps (Week 3)
- Order placement & order history
- Admin order management/status updates
- Transactional stock changes
- Cancellation with stock restoration
- PDF invoice generation (OpenPDF)
- Low stock alerts

## 📝 License
Educational project for Java Development Internship.
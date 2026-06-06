# DBS26CE4thAF011
AMIeats - Online Restaurant Management System

# 🍽️ AMIeats - Online Restaurant Management System

A full-stack web-based restaurant ordering and management system built with Java Spring Boot and MySQL.

## Tech Stack

Component | Technology 

Frontend | HTML5, CSS3, Bootstrap 5, Thymeleaf 
Backend | Java 17, Spring Boot 
Database | MySQL 8.0 
IDE | IntelliJ IDEA 
DB Tool | MySQL Workbench 

## Features

### Customer Side
- Browse food categories with images
- Add items to cart with quantity control
- Place orders with delivery details
- Multiple payment options (Cash, JazzCash, EasyPaisa, Card)
- Order confirmation with unique order number

### Admin Panel
- Secure admin login (Staff only)
- Dashboard with live stats
- Order management with status updates
- Customer management
- Menu item management (Add/Edit/Delete)
- 10 Business Reports (3 parameter-based)

##  Database Summary

Feature | Count 
Tables | 15 
Views | 4
Stored Procedures | 4 
Triggers | 3 
Transactions | 3 
Constraints | 10+ 

## How to Run

1. Clone the repository
2. Open in IntelliJ IDEA
3. Create MySQL database:
```sql
CREATE DATABASE restaurant_db;
```
4. Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```
5. Run SQL scripts for views, procedures, triggers, constraints
6. Run `RestaurantManagementApplication.java`
7. Open browser: `http://localhost:8080`

##  Admin Login

Field | Value 
URL | http://localhost:8080/login 
Email | admin@amieats.com 
Password | admin123 

## Business Reports

# | Report | Type 

1 | All Orders | Standard 
2 | Customers Report | Standard 
3 | Menu Items Report | Standard 
4 | Pending Orders | Standard 
5 | Delivered Orders | Standard 
6 | Revenue by Category | Standard 
7 | Daily Revenue | Parameter-Based 
8 | Customer Order History | Parameter-Based 
9 | Top Selling Items | Parameter-Based 
10 | Inventory Report | Standard 

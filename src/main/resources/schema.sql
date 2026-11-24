
-- ============================================
-- NOVAMART - CATALOG DATABASE
-- Week 2: Catalog Persistence
-- ============================================

USE ecommerce_db;

-- 1. CATEGORIES TABLE
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);

-- 2. PRODUCTS TABLE
CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    stock_qty INT DEFAULT 0,
    low_stock_threshold INT DEFAULT 5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);

-- 3. INSERT CATEGORIES
INSERT INTO categories (name, description)
VALUES
('TIMEPIECES', 'Swiss Automatics & Tourbillons'),
('APPAREL', 'Milanese Silks & Tailored Velvets'),
('JEWELRY', 'Ethical Pavé Diamonds & Gemstones'),
('TECH', 'Aerospace Titanium Smart Wearables')
ON DUPLICATE KEY UPDATE
    description = VALUES(description);

-- 4. INSERT PRODUCTS
-- Category IDs are looked up by name, not hardcoded.

INSERT INTO products
    (sku, name, description, category_id, price, stock_qty)
VALUES
(
    'NX-101',
    'Aura Chronometer Royal',
    'Swiss Automatic Calibre 4130, 18k White Gold, Sapphire Crystal.',
    (SELECT id FROM categories WHERE name = 'TIMEPIECES'),
    12500.00,
    3
),
(
    'NX-102',
    'Midnight Silk Velvet Trench',
    'Hand-tailored Milanese velvet coat with silk lining and gold hardware.',
    (SELECT id FROM categories WHERE name = 'APPAREL'),
    3200.00,
    8
),
(
    'NX-103',
    'Solstice Pavé Diamond Ring',
    '3.5 Carat VVS1 ethically sourced emerald-cut solitaire diamond.',
    (SELECT id FROM categories WHERE name = 'JEWELRY'),
    8950.00,
    2
),
(
    'NX-104',
    'Obsidian Precision Smart Ring',
    'Aerospace Grade 5 Titanium casing with biometric sleep and vitality sensor.',
    (SELECT id FROM categories WHERE name = 'TECH'),
    12500.00,
    15
),
(
    'NX-105',
    'Nocturne Atelier Cashmere Scarf',
    '100% Mongolian combed baby cashmere with fringed edge.',
    (SELECT id FROM categories WHERE name = 'APPAREL'),
    750.00,
    24
),
(
    'NX-106',
    'Vanguard Tourbillon Chrono',
    'Flying tourbillon with 72-hour power reserve and carbon fiber bezel.',
    (SELECT id FROM categories WHERE name = 'TIMEPIECES'),
    18400.00,
    1
)
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    description = VALUES(description),
    category_id = VALUES(category_id),
    price = VALUES(price),
    stock_qty = VALUES(stock_qty);

-- 5. USERS TABLE (for authentication)
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'CUSTOMER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. VERIFY DATABASE
SELECT * FROM categories;

SELECT
    p.id,
    p.sku,
    p.name,
    c.name AS category,
    p.price,
    p.stock_qty,
    p.low_stock_threshold
FROM products p
JOIN categories c ON p.category_id = c.id
ORDER BY p.id;

SELECT user_id, username, role FROM users;

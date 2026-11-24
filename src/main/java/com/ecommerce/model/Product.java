
package com.ecommerce.model;

import java.math.BigDecimal;

public class Product {

    private int id;
    private String sku;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName;
    private BigDecimal price;
    private int stockQty;
    private int lowStockThreshold;

    // Default constructor
    public Product() {
    }

    // Parameterized constructor
    public Product(int id, String sku, String name,
                   String description, int categoryId,
                   String categoryName, BigDecimal price,
                   int stockQty, int lowStockThreshold) {

        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.stockQty = stockQty;
        this.lowStockThreshold = lowStockThreshold;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStockQty() {
        return stockQty;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setStockQty(int stockQty) {
        this.stockQty = stockQty;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    @Override
    public String toString() {
        return name + " (" + sku + ")";
    }
}

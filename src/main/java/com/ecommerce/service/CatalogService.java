
package com.ecommerce.service;

import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.Category;
import com.ecommerce.model.Product;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class CatalogService {

    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;

    public CatalogService() {
        this.productDAO = new ProductDAO();
        this.categoryDAO = new CategoryDAO();
    }

    // Get products using search text and category name
    public List<Product> getCatalog(String search, String category) {

        int categoryId = 0;

        if (category != null &&
            !category.trim().isEmpty() &&
            !category.equalsIgnoreCase("All") &&
            !category.equalsIgnoreCase("All Categories")) {

            List<Category> categories = categoryDAO.getAllCategories();

            for (Category c : categories) {
                if (c.getName().equalsIgnoreCase(category.trim())) {
                    categoryId = c.getId();
                    break;
                }
            }

            // Requested category does not exist
            if (categoryId == 0) {
                return Collections.emptyList();
            }
        }

        return productDAO.searchProducts(search, categoryId);
    }

    // Get all categories
    public List<Category> getCategories() {
        return categoryDAO.getAllCategories();
    }

    // Add product with validation
    public boolean addProduct(Product product) {

        if (!isValidProduct(product)) {
            System.err.println("Invalid product details.");
            return false;
        }

        return productDAO.addProduct(product);
    }

    // Update product with validation
    public boolean updateProduct(Product product) {

        if (product == null ||
            product.getId() <= 0 ||
            !isValidProduct(product)) {

            System.err.println("Invalid product details.");
            return false;
        }

        return productDAO.updateProduct(product);
    }

    // Validate product details
    private boolean isValidProduct(Product product) {

        if (product == null) {
            return false;
        }

        if (product.getSku() == null ||
            product.getSku().trim().isEmpty()) {
            return false;
        }

        if (product.getName() == null ||
            product.getName().trim().isEmpty()) {
            return false;
        }

        if (product.getPrice() == null ||
            product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (product.getCategoryId() <= 0) {
            return false;
        }

        if (product.getStockQty() < 0 ||
            product.getLowStockThreshold() < 0) {
            return false;
        }

        return true;
    }
}

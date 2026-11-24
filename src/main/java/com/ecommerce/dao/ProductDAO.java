
package com.ecommerce.dao;

import com.ecommerce.model.Product;
import com.ecommerce.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Get all products with their category names
    public List<Product> getAllProducts() {
        return searchProducts("", 0);
    }

    // Get products by category
    public List<Product> getProductsByCategory(int categoryId) {
        return searchProducts("", categoryId);
    }

    // Search products, optionally filtered by category
    // categoryId = 0 means all categories
    public List<Product> searchProducts(String query, int categoryId) {

        List<Product> products = new ArrayList<>();

        String sql = "SELECT p.*, c.name AS category_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE (? = '' OR p.name LIKE ? OR p.sku LIKE ?) " +
                     "AND (? = 0 OR p.category_id = ?) " +
                     "ORDER BY p.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String search = query == null ? "" : query.trim();

            stmt.setString(1, search);
            stmt.setString(2, "%" + search + "%");
            stmt.setString(3, "%" + search + "%");
            stmt.setInt(4, categoryId);
            stmt.setInt(5, categoryId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapProduct(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error searching products:");
            e.printStackTrace();
        }

        return products;
    }

    // Get product by ID
    public Product getById(int id) {

        String sql = "SELECT p.*, c.name AS category_name " +
                     "FROM products p " +
                     "JOIN categories c ON p.category_id = c.id " +
                     "WHERE p.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapProduct(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Add product
    public boolean addProduct(Product product) {

        String sql = "INSERT INTO products " +
                     "(sku, name, description, category_id, price, stock_qty, low_stock_threshold) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCategoryId());
            stmt.setBigDecimal(5, product.getPrice());
            stmt.setInt(6, product.getStockQty());
            stmt.setInt(7, product.getLowStockThreshold());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        product.setId(keys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error adding product:");
            e.printStackTrace();
        }

        return false;
    }

    // Update product
    public boolean updateProduct(Product product) {

        String sql = "UPDATE products SET sku = ?, name = ?, " +
                     "description = ?, category_id = ?, price = ?, " +
                     "stock_qty = ?, low_stock_threshold = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getSku());
            stmt.setString(2, product.getName());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCategoryId());
            stmt.setBigDecimal(5, product.getPrice());
            stmt.setInt(6, product.getStockQty());
            stmt.setInt(7, product.getLowStockThreshold());
            stmt.setInt(8, product.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Delete product
    public boolean deleteProduct(int id) {

        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting product:");
            e.printStackTrace();
        }

        return false;
    }

    // Update stock quantity
    // qtyChange can be positive (add stock) or negative (reduce stock)
    public boolean updateStock(int productId, int qtyChange) {

        String sql = "UPDATE products " +
                     "SET stock_qty = stock_qty + ? " +
                     "WHERE id = ? AND stock_qty + ? >= 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, qtyChange);
            stmt.setInt(2, productId);
            stmt.setInt(3, qtyChange);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Convert database row into Product object
    private Product mapProduct(ResultSet rs) throws SQLException {

        return new Product(
            rs.getInt("id"),
            rs.getString("sku"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getInt("category_id"),
            rs.getString("category_name"),
            rs.getBigDecimal("price"),
            rs.getInt("stock_qty"),
            rs.getInt("low_stock_threshold")
        );
    }
}

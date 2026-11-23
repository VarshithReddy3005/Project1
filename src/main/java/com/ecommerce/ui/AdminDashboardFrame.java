package com.ecommerce.ui;

import com.ecommerce.model.User;

/**
 * Compatibility alias forwarding to AdminDashboard.
 */
public class AdminDashboardFrame extends AdminDashboard {
    public AdminDashboardFrame(User adminUser) {
        super(adminUser);
    }
}

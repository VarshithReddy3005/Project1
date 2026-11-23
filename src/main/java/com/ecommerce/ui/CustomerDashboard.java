package com.ecommerce.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.ecommerce.model.User;

/**
 * NEXUS LUXE • Luxury Customer Shopping Experience
 * Features:
 * - Curated luxury catalog with live category filtering
 * - Interactive shopping cart & live cart item counter
 * - Customer wishlist & quick add
 * - My Orders tracking & order history modal
 * - My Profile management
 * - Pure customer shopping experience without administrative internals
 */
public class CustomerDashboard extends JFrame {

    private final User user;
    private final List<ProductItem> allProducts = new ArrayList<>();
    private final List<ProductItem> filteredProducts = new ArrayList<>();
    private final List<ProductItem> cartItems = new ArrayList<>();
    private final List<ProductItem> wishlistItems = new ArrayList<>();

    private JLabel cartBadgeLabel;
    private JPanel productGridPanel;
    private JTextField searchField;
    private String selectedCategory = "ALL";

    // Ambient Animation Engine
    private Timer animationTimer;
    private final long startTime;
    private float animTime = 0f;
    private float smoothParallaxX = 0f;
    private float smoothParallaxY = 0f;
    private float targetParallaxX = 0f;
    private float targetParallaxY = 0f;

    private final List<Particle> particles = new ArrayList<>();
    private final Random rand = new Random(333);

    // Color Palette
    private static final Color BG_DARK_TOP = new Color(8, 10, 24);
    private static final Color BG_DARK_BOTTOM = new Color(18, 14, 46);
    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_VIOLET = new Color(168, 85, 247);
    private static final Color ACCENT_ROSE = new Color(244, 63, 94);
    private static final Color TEXT_WHITE = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);

    public CustomerDashboard(User user) {
        this.user = user;

        setTitle("NEXUS LUXE • Luxury Customer Portal (" + user.getUsername() + ")");
        setSize(1160, 780);
        setMinimumSize(new Dimension(960, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initCatalog();
        initParticles(45);

        MainCanvas canvas = new MainCanvas();
        canvas.setLayout(new BorderLayout());
        setContentPane(canvas);

        setupShoppingInterface(canvas);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                if (cx > 0 && cy > 0) {
                    targetParallaxX = (float) (e.getX() - cx) / cx;
                    targetParallaxY = (float) (e.getY() - cy) / cy;
                }
            }
        });

        startTime = System.currentTimeMillis();
        animationTimer = new Timer(16, e -> {
            long now = System.currentTimeMillis();
            animTime = (now - startTime) / 1000.0f;

            smoothParallaxX += (targetParallaxX - smoothParallaxX) * 0.05f;
            smoothParallaxY += (targetParallaxY - smoothParallaxY) * 0.05f;

            updateParticles();
            canvas.repaint();
        });
        animationTimer.start();
    }

    private void initCatalog() {
        allProducts.add(new ProductItem("Aura Chronometer Royal", "TIMEPIECES", 12500, "Swiss Automatic Calibre 4130, 18k White Gold, Sapphire Crystal."));
        allProducts.add(new ProductItem("Midnight Silk Velvet Trench", "APPAREL", 3200, "Hand-tailored Milanese velvet coat with silk lining and gold hardware."));
        allProducts.add(new ProductItem("Solstice Pavé Diamond Ring", "JEWELRY", 8950, "3.5 Carat VVS1 ethically sourced emerald-cut solitaire diamond."));
        allProducts.add(new ProductItem("Obsidian Precision Smart Ring", "TECH", 12500, "Aerospace Grade 5 Titanium casing with biometric sleep and vitality sensor."));
        allProducts.add(new ProductItem("Nocturne Atelier Cashmere Scarf", "APPAREL", 750, "100% Mongolian combed baby cashmere with fringed edge."));
        allProducts.add(new ProductItem("Vanguard Tourbillon Chrono", "TIMEPIECES", 18400, "Flying tourbillon with 72-hour power reserve and carbon fiber bezel."));

        // Default item in cart for realistic preview
        cartItems.add(allProducts.get(0));
        filteredProducts.addAll(allProducts);
    }

    private void initParticles(int count) {
        particles.clear();
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(
                rand.nextFloat() * 1200,
                rand.nextFloat() * 800,
                (rand.nextFloat() - 0.5f) * 0.4f,
                -0.2f - rand.nextFloat() * 0.3f,
                1.0f + rand.nextFloat() * 2.5f,
                0.2f + rand.nextFloat() * 0.5f,
                0.5f + rand.nextFloat() * 2.0f,
                rand.nextFloat() * 6.28f
            ));
        }
    }

    private void updateParticles() {
        int w = Math.max(getWidth(), 100);
        int h = Math.max(getHeight(), 100);
        for (Particle p : particles) {
            p.x += p.vx + smoothParallaxX * 0.2f;
            p.y += p.vy + smoothParallaxY * 0.2f;

            if (p.x < -20) p.x = w + 20;
            if (p.x > w + 20) p.x = -20;
            if (p.y < -20) p.y = h + 20;
            if (p.y > h + 20) p.y = -20;
        }
    }

    private void setupShoppingInterface(MainCanvas canvas) {
        JPanel mainWrapper = new JPanel(new BorderLayout());
        mainWrapper.setOpaque(false);
        mainWrapper.setBorder(new EmptyBorder(20, 30, 25, 30));

        // 1. Top Luxury Navigation Bar
        JPanel topBar = createTopBar();
        mainWrapper.add(topBar, BorderLayout.NORTH);

        // 2. Central Area: Category Pills + Hero Drop + Product Grid in JScrollPane
        JPanel centerContainer = new JPanel(new BorderLayout(0, 16));
        centerContainer.setOpaque(false);

        // Header Controls: Category Pills & Hero Banner
        JPanel headerSection = new JPanel(new BorderLayout(0, 12));
        headerSection.setOpaque(false);

        JPanel categoryPanel = createCategoryBar();
        headerSection.add(categoryPanel, BorderLayout.NORTH);

        JPanel heroCard = createHeroBanner();
        headerSection.add(heroCard, BorderLayout.CENTER);

        centerContainer.add(headerSection, BorderLayout.NORTH);

        // Product Catalog Grid
        productGridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        productGridPanel.setOpaque(false);
        renderProductCards();

        JScrollPane scrollPane = new JScrollPane(productGridPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        centerContainer.add(scrollPane, BorderLayout.CENTER);
        mainWrapper.add(centerContainer, BorderLayout.CENTER);

        canvas.add(mainWrapper, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel nav = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                g2.setColor(new Color(15, 18, 42, 230));
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                g2.setColor(new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setPreferredSize(new Dimension(1000, 68));
        nav.setBorder(new EmptyBorder(12, 24, 12, 24));

        // Brand Logo
        JPanel leftBrand = new JPanel(new BorderLayout(8, 0));
        leftBrand.setOpaque(false);
        JLabel logo = new JLabel("NEXUS LUXE");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(TEXT_WHITE);
        leftBrand.add(logo, BorderLayout.WEST);

        // Search Field
        searchField = new JTextField("Search luxury collection...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setForeground(TEXT_MUTED);
        searchField.setBackground(new Color(24, 28, 62, 220));
        searchField.setCaretColor(ACCENT_CYAN);
        searchField.setBorder(new EmptyBorder(6, 14, 6, 14));
        searchField.setPreferredSize(new Dimension(280, 36));

        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (searchField.getText().equals("Search luxury collection...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_WHITE);
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search luxury collection...");
                    searchField.setForeground(TEXT_MUTED);
                }
            }
        });

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterCatalog(searchField.getText().trim());
            }
        });

        JPanel searchWrapper = new JPanel();
        searchWrapper.setOpaque(false);
        searchWrapper.add(searchField);

        nav.add(leftBrand, BorderLayout.WEST);
        nav.add(searchWrapper, BorderLayout.CENTER);

        // Right Action Controls
        JPanel rightActions = new JPanel();
        rightActions.setOpaque(false);

        // Wishlist Button
        JButton wishlistBtn = createPillButton("♡ Wishlist", new Color(168, 85, 247));
        wishlistBtn.addActionListener(e -> showWishlistDialog());
        rightActions.add(wishlistBtn);

        // Cart Button with Counter
        cartBadgeLabel = new JLabel("🛒 Bag (" + cartItems.size() + ")");
        JButton cartBtn = createPillButton(cartBadgeLabel.getText(), ACCENT_CYAN);
        cartBtn.addActionListener(e -> showCartDialog());
        rightActions.add(cartBtn);

        // Orders Button
        JButton ordersBtn = createPillButton("📦 My Orders", new Color(99, 102, 241));
        ordersBtn.addActionListener(e -> showOrdersDialog());
        rightActions.add(ordersBtn);

        // Profile Button
        JButton profileBtn = createPillButton("👤 " + user.getUsername(), new Color(52, 211, 153));
        profileBtn.addActionListener(e -> showProfileDialog());
        rightActions.add(profileBtn);

        // Logout
        JButton logoutBtn = createPillButton("Sign Out", new Color(239, 68, 68));
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        rightActions.add(logoutBtn);

        nav.add(rightActions, BorderLayout.EAST);
        return nav;
    }

    private JPanel createCategoryBar() {
        JPanel bar = new JPanel();
        bar.setOpaque(false);

        String[] categories = {"ALL", "TIMEPIECES", "APPAREL", "JEWELRY", "TECH"};
        for (String cat : categories) {
            JButton catBtn = new JButton(cat);
            catBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            catBtn.setForeground(cat.equals(selectedCategory) ? TEXT_WHITE : TEXT_MUTED);
            catBtn.setBackground(cat.equals(selectedCategory) ? new Color(99, 102, 241, 180) : new Color(24, 28, 62, 160));
            catBtn.setFocusPainted(false);
            catBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            catBtn.setBorder(new EmptyBorder(6, 16, 6, 16));

            catBtn.addActionListener(e -> {
                selectedCategory = cat;
                filterCatalog(searchField.getText());
                // Refresh category buttons UI
                for (java.awt.Component c : bar.getComponents()) {
                    if (c instanceof JButton) {
                        JButton b = (JButton) c;
                        boolean sel = b.getText().equals(selectedCategory);
                        b.setForeground(sel ? TEXT_WHITE : TEXT_MUTED);
                        b.setBackground(sel ? new Color(99, 102, 241, 180) : new Color(24, 28, 62, 160));
                    }
                }
            });
            bar.add(catBtn);
        }
        return bar;
    }

    private JPanel createHeroBanner() {
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();

                LinearGradientPaint grad = new LinearGradientPaint(
                    0, 0, w, h,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(30, 27, 75, 230), new Color(17, 24, 39, 240), new Color(15, 23, 42, 245)}
                );
                g2.setPaint(grad);
                g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));

                g2.setColor(new Color(168, 85, 247, 80));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));
                g2.dispose();
            }
        };
        hero.setOpaque(false);
        hero.setLayout(null);
        hero.setPreferredSize(new Dimension(1000, 90));

        JLabel title = new JLabel("✨  EXCLUSIVE WINTER HAUTE ÉCOMMERCE DROP");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(ACCENT_CYAN);
        title.setBounds(28, 18, 650, 24);
        hero.add(title);

        JLabel desc = new JLabel("Handcrafted Swiss automatic timepieces, fine Milanese velvet coats, and ethically certified pavé diamonds.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(TEXT_MUTED);
        desc.setBounds(28, 44, 750, 22);
        hero.add(desc);

        return hero;
    }

    private void filterCatalog(String query) {
        filteredProducts.clear();
        String q = (query != null && !query.equals("Search luxury collection...")) ? query.trim().toLowerCase() : "";

        for (ProductItem p : allProducts) {
            boolean matchesCat = selectedCategory.equals("ALL") || p.category.equalsIgnoreCase(selectedCategory);
            boolean matchesSearch = q.isEmpty() || p.name.toLowerCase().contains(q) || p.description.toLowerCase().contains(q);

            if (matchesCat && matchesSearch) {
                filteredProducts.add(p);
            }
        }
        renderProductCards();
    }

    private void renderProductCards() {
        productGridPanel.removeAll();

        for (ProductItem item : filteredProducts) {
            JPanel card = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth();
                    int h = getHeight();

                    g2.setColor(new Color(20, 24, 58, 225));
                    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 18, 18));

                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.draw(new RoundRectangle2D.Float(0, 0, w, h, 18, 18));
                    g2.dispose();
                }
            };
            card.setOpaque(false);
            card.setLayout(null);
            card.setPreferredSize(new Dimension(320, 200));

            // Category tag
            JLabel catTag = new JLabel("● " + item.category);
            catTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
            catTag.setForeground(ACCENT_CYAN);
            catTag.setBounds(20, 16, 200, 16);
            card.add(catTag);

            // Title
            JLabel nameLabel = new JLabel(item.name);
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            nameLabel.setForeground(TEXT_WHITE);
            nameLabel.setBounds(20, 36, 280, 24);
            card.add(nameLabel);

            // Description
            JLabel descLabel = new JLabel("<html><body style='width: 240px; color: #94A3B8; font-size: 10px; font-family: Segoe UI;'>" + item.description + "</body></html>");
            descLabel.setBounds(20, 62, 280, 42);
            card.add(descLabel);

            // Price
            JLabel priceLabel = new JLabel("$" + String.format("%,d", item.price) + " USD");
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            priceLabel.setForeground(new Color(52, 211, 153));
            priceLabel.setBounds(20, 138, 140, 24);
            card.add(priceLabel);

            // Wishlist Toggle
            JButton wishBtn = new JButton("♡");
            wishBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            wishBtn.setForeground(ACCENT_ROSE);
            wishBtn.setBackground(new Color(30, 36, 75));
            wishBtn.setFocusPainted(false);
            wishBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            wishBtn.setBounds(165, 134, 38, 32);
            wishBtn.addActionListener(e -> {
                if (!wishlistItems.contains(item)) {
                    wishlistItems.add(item);
                    JOptionPane.showMessageDialog(this, "Added to Wishlist:\n" + item.name, "Wishlist", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    wishlistItems.remove(item);
                    JOptionPane.showMessageDialog(this, "Removed from Wishlist:\n" + item.name, "Wishlist", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            card.add(wishBtn);

            // Add to Bag Button
            JButton addBtn = new JButton("Add to Bag");
            addBtn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            addBtn.setForeground(Color.WHITE);
            addBtn.setBackground(new Color(99, 102, 241));
            addBtn.setFocusPainted(false);
            addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            addBtn.setBounds(210, 134, 96, 32);

            addBtn.addActionListener(e -> {
                cartItems.add(item);
                cartBadgeLabel.setText("🛒 Bag (" + cartItems.size() + ")");
                JOptionPane.showMessageDialog(this, "Item added to your shopping bag:\n" + item.name + " ($" + item.price + ")", "Shopping Bag", JOptionPane.INFORMATION_MESSAGE);
            });
            card.add(addBtn);

            productGridPanel.add(card);
        }

        productGridPanel.revalidate();
        productGridPanel.repaint();
    }

    private void showCartDialog() {
        int subtotal = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("Your Shopping Bag:\n\n");
        if (cartItems.isEmpty()) {
            sb.append("Your bag is currently empty.\nBrowse the catalog to add luxury pieces.");
        } else {
            for (int i = 0; i < cartItems.size(); i++) {
                ProductItem p = cartItems.get(i);
                sb.append(i + 1).append(". ").append(p.name).append(" — $").append(String.format("%,d", p.price)).append("\n");
                subtotal += p.price;
            }
            sb.append("\n-------------------------------------\n");
            sb.append("Estimated Subtotal: $").append(String.format("%,d", subtotal)).append(" USD\n");
            sb.append("White Glove Shipping: COMPLIMENTARY\n");
            sb.append("Total: $").append(String.format("%,d", subtotal)).append(" USD\n\n");
            sb.append("Proceed to secure checkout?");
        }

        int choice = JOptionPane.showConfirmDialog(this, sb.toString(), "Shopping Bag Checkout", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION && !cartItems.isEmpty()) {
            cartItems.clear();
            cartBadgeLabel.setText("🛒 Bag (0)");
            JOptionPane.showMessageDialog(this, "Order Confirmed! Your tracking order number is #NX-9482.\nThank you for choosing NEXUS LUXE.", "Checkout Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showWishlistDialog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Your Saved Wishlist:\n\n");
        if (wishlistItems.isEmpty()) {
            sb.append("No saved items. Click '♡' on any piece to save it for later.");
        } else {
            for (ProductItem p : wishlistItems) {
                sb.append("• ").append(p.name).append(" ($").append(p.price).append(")\n");
            }
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "My Wishlist", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showOrdersDialog() {
        String msg = "Order History & Tracking:\n\n" +
            "Order #NX-8921\n" +
            "• Aura Chronometer Royal ($12,500)\n" +
            "• Status: IN TRANSIT (Courier: Ferrari Luxury Express)\n" +
            "• Estimated Delivery: Tuesday by 2:00 PM\n" +
            "• Destination: Verified Customer Address\n\n" +
            "Order #NX-7419\n" +
            "• Cashmere Atelier Scarf ($750)\n" +
            "• Status: DELIVERED\n";
        JOptionPane.showMessageDialog(this, msg, "My Orders & Tracking", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showProfileDialog() {
        String msg = "Customer Profile:\n\n" +
            "Customer Username: " + user.getUsername() + "\n" +
            "Account Role: " + user.getRole() + "\n" +
            "Customer ID: #" + user.getUserId() + "\n" +
            "Security Status: Authenticated via BCrypt Salted Hash\n" +
            "Loyalty Tier: NEXUS Privilege Member\n";
        JOptionPane.showMessageDialog(this, msg, "My Customer Profile", JOptionPane.INFORMATION_MESSAGE);
    }

    private JButton createPillButton(String text, Color accent) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(TEXT_WHITE);
        btn.setBackground(new Color(24, 28, 62, 220));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120), 1),
            new EmptyBorder(6, 12, 6, 12)
        ));
        return btn;
    }

    private class MainCanvas extends JPanel {
        public MainCanvas() {
            setDoubleBuffered(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            if (width <= 0 || height <= 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            GradientPaint bgGradient = new GradientPaint(
                0, 0, BG_DARK_TOP,
                width * 0.5f, height, BG_DARK_BOTTOM
            );
            g2.setPaint(bgGradient);
            g2.fillRect(0, 0, width, height);

            float orb1X = width * 0.2f + (float) Math.sin(animTime * 0.5f) * 50;
            float orb1Y = height * 0.3f + (float) Math.cos(animTime * 0.4f) * 40;
            drawOrb(g2, orb1X, orb1Y, 320, ACCENT_CYAN, 0.16f);

            float orb2X = width * 0.8f + (float) Math.cos(animTime * 0.4f) * 60;
            float orb2Y = height * 0.7f + (float) Math.sin(animTime * 0.5f) * 50;
            drawOrb(g2, orb2X, orb2Y, 360, ACCENT_VIOLET, 0.18f);

            for (Particle p : particles) {
                float twinkle = (float) (Math.sin(animTime * p.pulseSpeed + p.pulsePhase) * 0.5 + 0.5);
                float curAlpha = p.baseAlpha * (0.4f + twinkle * 0.6f);
                g2.setColor(new Color(255, 255, 255, (int) (curAlpha * 255)));
                g2.fill(new Ellipse2D.Float(p.x, p.y, p.size, p.size));
            }

            g2.dispose();
        }

        private void drawOrb(Graphics2D g2, float cx, float cy, float radius, Color color, float maxAlpha) {
            float rad = Math.max(10.0f, radius);
            float[] dist = {0.0f, 0.55f, 1.0f};
            Color[] colors = {
                new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (maxAlpha * 255)),
                new Color(color.getRed(), color.getGreen(), color.getBlue(), (int) (maxAlpha * 0.35f * 255)),
                new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)
            };
            RadialGradientPaint p = new RadialGradientPaint(new Point2D.Float(cx, cy), rad, dist, colors);
            g2.setPaint(p);
            g2.fill(new Ellipse2D.Float(cx - rad, cy - rad, rad * 2, rad * 2));
        }
    }

    private static class ProductItem {
        final String name;
        final String category;
        final int price;
        final String description;

        ProductItem(String name, String category, int price, String description) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.description = description;
        }
    }

    private static class Particle {
        float x, y, vx, vy, size, baseAlpha, pulseSpeed, pulsePhase;

        public Particle(float x, float y, float vx, float vy, float size, float baseAlpha, float pulseSpeed, float pulsePhase) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.baseAlpha = baseAlpha;
            this.pulseSpeed = pulseSpeed;
            this.pulsePhase = pulsePhase;
        }
    }
}

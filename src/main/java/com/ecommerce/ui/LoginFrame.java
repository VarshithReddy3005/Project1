package com.ecommerce.ui;

import java.awt.AlphaComposite;
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
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.ecommerce.model.User;
import com.ecommerce.service.AuthService;

/**
 * Ultra-Premium NEXUS E-Commerce Login Experience
 * Features:
 * - Pure Java2D animated cosmic background with glowing orbs and particles
 * - Interactive 3D mouse parallax and floating shopping bag illustration
 * - Glassmorphism login card with specular sheen
 * - Clean input validation (no premature errors on window open)
 * - Show/Hide password toggle control
 * - Integrated with existing AuthService and MySQL database
 * - Bidirectional navigation with RegisterFrame and CustomerDashboardFrame
 */
public class LoginFrame extends JFrame {

    // --- Core Backend Service & State ---
    private final AuthService authService;

    // --- UI Controls ---
    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    private GlowButton loginButton;
    private ModernCheckBox rememberMeCheckBox;
    private StatusBanner statusBanner;
    private JLabel registerLink;
    private JPanel cardPanel;
    private MainCanvas canvas;

    // --- Animation & Parallax Engine ---
    private Timer animationTimer;
    private final long startTime;
    private float animTime = 0f;
    private float targetParallaxX = 0f;
    private float targetParallaxY = 0f;
    private float smoothParallaxX = 0f;
    private float smoothParallaxY = 0f;

    // Card vibration effect on validation failure
    private int shakeFrame = 0;
    private static final int MAX_SHAKE_FRAMES = 18;
    private float shakeOffset = 0f;

    // Window readiness guard: prevents initial Enter key or focus triggers from showing errors prematurely
    private boolean windowReady = false;

    // Particle field
    private final List<Particle> particles = new ArrayList<>();
    private final Random rand = new Random(42);

    // --- Color Palette Constants ---
    private static final Color BG_DARK_TOP = new Color(8, 10, 24);
    private static final Color BG_DARK_BOTTOM = new Color(20, 14, 48);

    private static final Color ACCENT_CYAN = new Color(0, 229, 255);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_VIOLET = new Color(168, 85, 247);
    private static final Color ACCENT_MAGENTA = new Color(236, 72, 153);
    private static final Color ACCENT_INDIGO = new Color(99, 102, 241);

    private static final Color TEXT_WHITE = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(148, 163, 184);
    private static final Color TEXT_DIM = new Color(100, 116, 139);

    // --- Fonts ---
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FONT_INPUT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 10);

    public LoginFrame() {
        // 1. Initialize existing authentication service
        authService = new AuthService();

        // 2. Window Configuration
        setTitle("NEXUS LUXE • Next-Gen E-Commerce");
        setSize(1080, 720);
        setMinimumSize(new Dimension(880, 620));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 3. Pre-generate background particles
        initParticles(55);

        // 4. Main animated canvas container
        canvas = new MainCanvas();
        canvas.setLayout(null);
        setContentPane(canvas);

        // 5. Setup the Glassmorphic Login Card & Form Components
        setupLoginCard();

        // 6. Responsive card positioning listener
        canvas.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateCardPosition();
            }
        });

        // 7. Interactive Mouse Parallax Listener
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

        // 8. 60 FPS Swing Animation Loop
        startTime = System.currentTimeMillis();
        animationTimer = new Timer(16, e -> {
            long now = System.currentTimeMillis();
            animTime = (now - startTime) / 1000.0f;

            smoothParallaxX += (targetParallaxX - smoothParallaxX) * 0.05f;
            smoothParallaxY += (targetParallaxY - smoothParallaxY) * 0.05f;

            updateParticles();

            if (shakeFrame > 0) {
                shakeFrame--;
                float progress = 1.0f - ((float) shakeFrame / MAX_SHAKE_FRAMES);
                shakeOffset = (float) (Math.sin(progress * Math.PI * 5) * 12.0f * (1.0f - progress));
            } else {
                shakeOffset = 0f;
            }

            updateCardPosition();
            canvas.repaint();
        });
        animationTimer.start();

        setVisible(true);

        // 9. Enable form validation only after window is fully rendered (prevents startup false-positive errors)
        Timer warmupTimer = new Timer(350, e -> windowReady = true);
        warmupTimer.setRepeats(false);
        warmupTimer.start();
    }

    // --- Particle Physics Engine ---
    private void initParticles(int count) {
        particles.clear();
        for (int i = 0; i < count; i++) {
            particles.add(new Particle(
                rand.nextFloat() * 1200,
                rand.nextFloat() * 800,
                (rand.nextFloat() - 0.5f) * 0.5f,
                -0.2f - rand.nextFloat() * 0.4f,
                1.0f + rand.nextFloat() * 2.5f,
                0.2f + rand.nextFloat() * 0.6f,
                0.5f + rand.nextFloat() * 2.0f,
                rand.nextFloat() * 6.28f
            ));
        }
    }

    private void updateParticles() {
        int w = Math.max(canvas.getWidth(), 100);
        int h = Math.max(canvas.getHeight(), 100);
        for (Particle p : particles) {
            p.x += p.vx + smoothParallaxX * 0.3f;
            p.y += p.vy + smoothParallaxY * 0.2f;

            if (p.x < -20) p.x = w + 20;
            if (p.x > w + 20) p.x = -20;
            if (p.y < -20) p.y = h + 20;
            if (p.y > h + 20) p.y = -20;
        }
    }

    // --- Responsive Positioning ---
    private void updateCardPosition() {
        if (cardPanel != null && canvas != null) {
            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cardW = 880;
            int cardH = 500;
            int cardX = (w - cardW) / 2 + (int) shakeOffset + (int) (smoothParallaxX * 7);
            int cardY = (h - cardH) / 2 + (int) (smoothParallaxY * 6);
            cardPanel.setBounds(cardX, cardY, cardW, cardH);
        }
    }

    // --- Public Utility for Pre-filling Username after Registration ---
    public void setUsername(String username) {
        if (usernameField != null && username != null) {
            usernameField.setText(username);
            if (passwordField != null) {
                passwordField.requestFocusInWindow();
            }
        }
    }

    public void showRegistrationSuccess(String message) {
        if (statusBanner != null) {
            statusBanner.showSuccess(message);
        }
    }

    // --- Login Card & Form Setup ---
    private void setupLoginCard() {
        cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

                int w = getWidth();
                int h = getHeight();

                // 1. Render Glassmorphism Surface & Specular Sheen
                drawGlassCardSurface(g2, w, h);

                // 2. Render Left 3D Showcase Stage (Bag, Gift Box, Orbitals)
                draw3DShowcaseStage(g2);

                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setLayout(null);
        canvas.add(cardPanel);

        // Right side: The Login Form
        int formX = 420;
        int formWidth = 425;

        // Welcome Header
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(FONT_TITLE);
        welcomeLabel.setForeground(TEXT_WHITE);
        welcomeLabel.setBounds(formX, 40, formWidth, 34);
        cardPanel.add(welcomeLabel);

        JLabel subtitleLabel = new JLabel("Sign in to access your curated luxury store & orders");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setBounds(formX, 76, formWidth, 20);
        cardPanel.add(subtitleLabel);

        // In-Card Animated Status / Error Banner (Hidden by default, starts completely clean)
        statusBanner = new StatusBanner();
        statusBanner.setBounds(formX, 104, formWidth, 34);
        statusBanner.clear();
        cardPanel.add(statusBanner);

        // Username Field
        JLabel usernameLabel = new JLabel("USERNAME OR EMAIL");
        usernameLabel.setFont(FONT_LABEL);
        usernameLabel.setForeground(TEXT_MUTED);
        usernameLabel.setBounds(formX, 146, formWidth, 18);
        cardPanel.add(usernameLabel);

        usernameField = new ModernTextField("Enter your username or email", ModernTextField.IconType.USER);
        usernameField.setBounds(formX, 168, formWidth, 46);
        cardPanel.add(usernameField);

        // Password Field
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(FONT_LABEL);
        passwordLabel.setForeground(TEXT_MUTED);
        passwordLabel.setBounds(formX, 226, formWidth, 18);
        cardPanel.add(passwordLabel);

        passwordField = new ModernPasswordField("Enter your password");
        passwordField.setBounds(formX, 248, formWidth, 46);
        cardPanel.add(passwordField);

        // Remember Me Row (Removed fake 256-BIT ENCRYPTED label)
        rememberMeCheckBox = new ModernCheckBox("Remember this device");
        rememberMeCheckBox.setBounds(formX, 304, 250, 24);
        cardPanel.add(rememberMeCheckBox);

        // Primary Action Login Button
        loginButton = new GlowButton("SIGN IN TO ACCOUNT  →");
        loginButton.setBounds(formX, 344, formWidth, 50);
        cardPanel.add(loginButton);

        // Sign Up Footer Link
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setBounds(formX, 412, formWidth, 30);

        JLabel noAccountLabel = new JLabel("Don't have an account? ");
        noAccountLabel.setFont(FONT_SMALL);
        noAccountLabel.setForeground(TEXT_MUTED);

        registerLink = new JLabel("Create Account");
        registerLink.setFont(new Font("Segoe UI", Font.BOLD, 12));
        registerLink.setForeground(ACCENT_CYAN);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setForeground(ACCENT_VIOLET);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setForeground(ACCENT_CYAN);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                openRegisterFrame();
            }
        });

        footerPanel.add(noAccountLabel);
        footerPanel.add(registerLink);
        cardPanel.add(footerPanel);

        // Action Listeners on Text Fields (Only fires when user presses Enter while actively focused)
        usernameField.addActionListener(e -> loginUser());
        passwordField.addActionListener(e -> loginUser());
        loginButton.addActionListener(e -> loginUser());

        updateCardPosition();
    }

    // --- Authentication Handler (Preserving Existing Logic & Database Connection) ---
    private void loginUser() {
        // Prevent premature triggering on initial frame display
        if (!windowReady) return;

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate only when user explicitly attempts to login
        if (username.isEmpty() || password.isEmpty()) {
            triggerShake();
            statusBanner.showError("Please enter both username and password.");
            return;
        }

        // Animated Loading Feedback on Button
        loginButton.setLoading(true);

        // Execute authentication via existing AuthService
        Timer delayTimer = new Timer(300, evt -> {
            loginButton.setLoading(false);

            User loggedIn = authService.login(username, password);

            if (loggedIn != null) {
                String rawRole = loggedIn.getRole();
                String role = (rawRole != null) ? rawRole.trim().toUpperCase() : "";

                // Role-based navigation: ADMIN -> AdminDashboard, CUSTOMER -> CustomerDashboard, Unknown -> Deny
                if ("ADMIN".equals(role)) {
                    statusBanner.showSuccess("Welcome, " + loggedIn.getUsername() + " [ADMIN]!");
                    Timer openDashboardTimer = new Timer(400, dEvt -> {
                        new AdminDashboard(loggedIn).setVisible(true);
                        dispose();
                    });
                    openDashboardTimer.setRepeats(false);
                    openDashboardTimer.start();
                } else if ("CUSTOMER".equals(role)) {
                    statusBanner.showSuccess("Welcome, " + loggedIn.getUsername() + "!");
                    Timer openDashboardTimer = new Timer(400, dEvt -> {
                        new CustomerDashboard(loggedIn).setVisible(true);
                        dispose();
                    });
                    openDashboardTimer.setRepeats(false);
                    openDashboardTimer.start();
                } else {
                    triggerShake();
                    statusBanner.showError("Access denied: Role '" + rawRole + "' is not authorized.");
                    JOptionPane.showMessageDialog(
                        this,
                        "Access Denied: The role '" + rawRole + "' is not authorized for platform access.",
                        "Authorization Failed",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                triggerShake();
                statusBanner.showError("Invalid username or password. Please try again.");
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    private void triggerShake() {
        shakeFrame = MAX_SHAKE_FRAMES;
    }

    // --- Register Frame Navigation (Opens functional RegisterFrame) ---
    private void openRegisterFrame() {
        new RegisterFrame().setVisible(true);
        dispose();
    }

    // =========================================================================
    // GLASS CARD RENDERING: Surface, Specular Sheen, Dividers, 3D Elements
    // =========================================================================
    private void drawGlassCardSurface(Graphics2D g2, int w, int h) {
        RoundRectangle2D cardShape = new RoundRectangle2D.Float(0, 0, w, h, 28, 28);

        // 1. Semi-Transparent Glassmorphism Fill
        LinearGradientPaint cardFill = new LinearGradientPaint(
            0, 0, w * 0.4f, h,
            new float[]{0.0f, 0.45f, 1.0f},
            new Color[]{
                new Color(25, 30, 68, 225),
                new Color(18, 20, 52, 235),
                new Color(12, 14, 38, 245)
            }
        );
        g2.setPaint(cardFill);
        g2.fill(cardShape);

        // 2. Specular Surface Reflection Sheen (Top-Left Angle)
        LinearGradientPaint sheen = new LinearGradientPaint(
            0, 0, w * 0.35f, h * 0.45f,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 45),
                new Color(255, 255, 255, 12),
                new Color(255, 255, 255, 0)
            }
        );
        g2.setPaint(sheen);
        g2.fill(cardShape);

        // 3. Subtle Vertical Divider separating 3D Showcase and Form
        int dividerX = 395;
        LinearGradientPaint dividerGrad = new LinearGradientPaint(
            dividerX, 30, dividerX, h - 30,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 0),
                new Color(99, 102, 241, 65),
                new Color(255, 255, 255, 0)
            }
        );
        g2.setPaint(dividerGrad);
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawLine(dividerX, 35, dividerX, h - 35);

        // 4. Glowing Translucent Border
        LinearGradientPaint borderGrad = new LinearGradientPaint(
            0, 0, w, h,
            new float[]{0.0f, 0.35f, 0.7f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 120),
                new Color(0, 229, 255, 160),
                new Color(168, 85, 247, 130),
                new Color(255, 255, 255, 40)
            }
        );
        g2.setPaint(borderGrad);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(cardShape);
    }

    // --- 3D Hero Showcase Stage: Bag, Gift Box, Orbitals ---
    private void draw3DShowcaseStage(Graphics2D g2) {
        int stageCenterX = 195 + (int) (smoothParallaxX * 10);
        int stageCenterY = 245 + (int) (smoothParallaxY * 8);

        // A. Brand Header & Tagline Pill
        int pillX = 35;
        int pillY = 36;
        g2.setColor(new Color(0, 229, 255, 25));
        g2.fill(new RoundRectangle2D.Float(pillX, pillY, 180, 24, 12, 12));
        g2.setColor(new Color(0, 229, 255, 140));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(pillX, pillY, 180, 24, 12, 12));

        g2.setFont(FONT_BADGE);
        g2.setColor(ACCENT_CYAN);
        g2.drawString("✨  NEXT-GEN SHOPPING", pillX + 16, pillY + 16);

        // B. 3D Faceted Brand Logo
        drawBrandLogo(g2, 35, 74);

        // C. Dynamic 3D Floating Shopping Bag
        float bagFloatY = (float) Math.sin(animTime * 1.8f) * 11.0f;
        draw3DShoppingBag(g2, stageCenterX - 15, stageCenterY + 10 + bagFloatY);

        // D. 3D Floating Product Gift Box with Ribbon
        float boxFloatY = (float) Math.cos(animTime * 1.5f) * 9.0f;
        draw3DGiftBox(g2, stageCenterX + 95, stageCenterY - 65 + boxFloatY);

        // E. Rotating 3D Gyroscope Orbital Rings
        draw3DOrbitalRings(g2, stageCenterX - 15, stageCenterY + 20 + bagFloatY);

        // Subtle bottom decorative glow line (Replaced fake marketing claims with clean aesthetic)
        g2.setPaint(new LinearGradientPaint(
            35, 455, 340, 455,
            new float[]{0f, 0.5f, 1f},
            new Color[]{new Color(0, 229, 255, 0), new Color(99, 102, 241, 100), new Color(168, 85, 247, 0)}
        ));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawLine(35, 455, 340, 455);
    }

    // --- Vector 3D Faceted Logo & Brand Typography ---
    private void drawBrandLogo(Graphics2D g2, int x, int y) {
        int iconX = x + 16;
        int iconY = y + 16;

        Path2D top = new Path2D.Double();
        top.moveTo(iconX, iconY - 14);
        top.lineTo(iconX + 13, iconY - 6);
        top.lineTo(iconX, iconY + 2);
        top.lineTo(iconX - 13, iconY - 6);
        top.closePath();
        g2.setPaint(new LinearGradientPaint(iconX, iconY - 14, iconX, iconY + 2,
            new float[]{0f, 1f}, new Color[]{new Color(0, 240, 255), new Color(99, 102, 241)}));
        g2.fill(top);

        Path2D left = new Path2D.Double();
        left.moveTo(iconX - 13, iconY - 6);
        left.lineTo(iconX, iconY + 2);
        left.lineTo(iconX, iconY + 16);
        left.lineTo(iconX - 13, iconY + 8);
        left.closePath();
        g2.setPaint(new LinearGradientPaint(iconX - 13, iconY, iconX, iconY + 16,
            new float[]{0f, 1f}, new Color[]{new Color(79, 70, 229), new Color(49, 46, 129)}));
        g2.fill(left);

        Path2D right = new Path2D.Double();
        right.moveTo(iconX, iconY + 2);
        right.lineTo(iconX + 13, iconY - 6);
        right.lineTo(iconX + 13, iconY + 8);
        right.lineTo(iconX, iconY + 16);
        right.closePath();
        g2.setPaint(new LinearGradientPaint(iconX, iconY, iconX + 13, iconY + 16,
            new float[]{0f, 1f}, new Color[]{new Color(168, 85, 247), new Color(107, 33, 168)}));
        g2.fill(right);

        g2.setColor(new Color(255, 255, 255, 180));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(top);
        g2.draw(left);
        g2.draw(right);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        g2.setColor(TEXT_WHITE);
        g2.drawString("NEXUS", x + 42, y + 16);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(ACCENT_CYAN);
        g2.drawString("LUXURY COMMERCE", x + 44, y + 30);
    }

    // --- Custom 3D Luxury Shopping Bag ---
    private void draw3DShoppingBag(Graphics2D g2, float cx, float cy) {
        float shadowScale = 1.0f - (Math.abs(cy) % 20) / 100.0f;
        float shadowRadius = Math.max(10.0f, 65 * shadowScale);
        g2.setPaint(new RadialGradientPaint(
            new Point2D.Float(cx, cy + 75), shadowRadius,
            new float[]{0f, 0.7f, 1f},
            new Color[]{new Color(0, 0, 0, 95), new Color(0, 0, 0, 40), new Color(0, 0, 0, 0)}
        ));
        g2.fill(new Ellipse2D.Float(cx - 60 * shadowScale, cy + 62, 120 * shadowScale, 26 * shadowScale));

        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        CubicCurve2D handleBack = new CubicCurve2D.Float(cx - 24, cy - 35, cx - 22, cy - 78, cx + 8, cy - 78, cx + 12, cy - 35);
        g2.setPaint(new LinearGradientPaint(cx - 20, cy - 75, cx + 10, cy - 35,
            new float[]{0f, 1f}, new Color[]{new Color(253, 224, 71), new Color(180, 130, 20)}));
        g2.draw(handleBack);

        Path2D bagSide = new Path2D.Double();
        bagSide.moveTo(cx + 34, cy - 35);
        bagSide.lineTo(cx + 52, cy - 46);
        bagSide.lineTo(cx + 56, cy + 34);
        bagSide.lineTo(cx + 38, cy + 48);
        bagSide.closePath();
        g2.setPaint(new LinearGradientPaint(cx + 34, cy - 40, cx + 56, cy + 40,
            new float[]{0f, 1f}, new Color[]{new Color(24, 30, 75), new Color(10, 12, 38)}));
        g2.fill(bagSide);

        Path2D bagTop = new Path2D.Double();
        bagTop.moveTo(cx - 42, cy - 35);
        bagTop.lineTo(cx - 24, cy - 46);
        bagTop.lineTo(cx + 52, cy - 46);
        bagTop.lineTo(cx + 34, cy - 35);
        bagTop.closePath();
        g2.setColor(new Color(15, 18, 48));
        g2.fill(bagTop);

        Path2D bagFront = new Path2D.Double();
        bagFront.moveTo(cx - 42, cy - 35);
        bagFront.lineTo(cx + 34, cy - 35);
        bagFront.lineTo(cx + 38, cy + 48);
        bagFront.lineTo(cx - 38, cy + 48);
        bagFront.closePath();
        LinearGradientPaint frontGrad = new LinearGradientPaint(
            cx - 40, cy - 35, cx + 38, cy + 48,
            new float[]{0f, 0.45f, 1f},
            new Color[]{new Color(59, 130, 246), new Color(30, 58, 138), new Color(15, 23, 62)}
        );
        g2.setPaint(frontGrad);
        g2.fill(bagFront);

        CubicCurve2D handleFront = new CubicCurve2D.Float(cx - 16, cy - 35, cx - 14, cy - 74, cx + 16, cy - 74, cx + 20, cy - 35);
        g2.setPaint(new LinearGradientPaint(cx - 15, cy - 72, cx + 18, cy - 35,
            new float[]{0f, 1f}, new Color[]{new Color(254, 240, 138), new Color(202, 138, 4)}));
        g2.draw(handleFront);

        g2.setColor(new Color(250, 204, 21, 220));
        g2.setStroke(new BasicStroke(1.5f));
        Path2D crest = new Path2D.Double();
        crest.moveTo(cx - 2, cy - 6);
        crest.lineTo(cx + 6, cy);
        crest.lineTo(cx - 2, cy + 6);
        crest.lineTo(cx - 10, cy);
        crest.closePath();
        g2.draw(crest);

        g2.setColor(new Color(147, 197, 253, 140));
        g2.setStroke(new BasicStroke(1.2f));
        g2.draw(bagFront);
        g2.draw(bagSide);
    }

    // --- Custom 3D Floating Gift / Product Box ---
    private void draw3DGiftBox(Graphics2D g2, float bx, float by) {
        float size = 32f;

        Path2D topFace = new Path2D.Double();
        topFace.moveTo(bx, by - size * 0.58f);
        topFace.lineTo(bx + size * 0.86f, by - size * 0.15f);
        topFace.lineTo(bx, by + size * 0.28f);
        topFace.lineTo(bx - size * 0.86f, by - size * 0.15f);
        topFace.closePath();
        g2.setPaint(new LinearGradientPaint(bx, by - size, bx, by + size * 0.3f,
            new float[]{0f, 1f}, new Color[]{new Color(192, 132, 252), new Color(147, 51, 234)}));
        g2.fill(topFace);

        Path2D leftFace = new Path2D.Double();
        leftFace.moveTo(bx - size * 0.86f, by - size * 0.15f);
        leftFace.lineTo(bx, by + size * 0.28f);
        leftFace.lineTo(bx, by + size * 1.15f);
        leftFace.lineTo(bx - size * 0.86f, by + size * 0.72f);
        leftFace.closePath();
        g2.setPaint(new LinearGradientPaint(bx - size, by, bx, by + size,
            new float[]{0f, 1f}, new Color[]{new Color(126, 34, 206), new Color(88, 28, 135)}));
        g2.fill(leftFace);

        Path2D rightFace = new Path2D.Double();
        rightFace.moveTo(bx, by + size * 0.28f);
        rightFace.lineTo(bx + size * 0.86f, by - size * 0.15f);
        rightFace.lineTo(bx + size * 0.86f, by + size * 0.72f);
        rightFace.lineTo(bx, by + size * 1.15f);
        rightFace.closePath();
        g2.setPaint(new LinearGradientPaint(bx, by, bx + size, by + size,
            new float[]{0f, 1f}, new Color[]{new Color(88, 28, 135), new Color(59, 7, 100)}));
        g2.fill(rightFace);

        g2.setColor(new Color(0, 240, 255, 230));
        g2.setStroke(new BasicStroke(3.2f));
        g2.drawLine((int) bx, (int) (by + size * 0.28f), (int) bx, (int) (by + size * 1.15f));
        g2.drawOval((int) (bx - 7), (int) (by - size * 0.58f - 7), 7, 7);
        g2.drawOval((int) bx, (int) (by - size * 0.58f - 7), 7, 7);

        g2.setColor(new Color(255, 255, 255, 160));
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(topFace);
        g2.draw(leftFace);
        g2.draw(rightFace);
    }

    // --- 3D Orbital Gyroscope Rings with Revolving Particles ---
    private void draw3DOrbitalRings(Graphics2D g2, float rx, float ry) {
        AffineTransform oldTx = g2.getTransform();

        AffineTransform tx1 = new AffineTransform(oldTx);
        tx1.translate(rx, ry);
        tx1.rotate(Math.toRadians(25));
        tx1.scale(1.0, 0.36);
        g2.setTransform(tx1);

        g2.setPaint(new LinearGradientPaint(-100, 0, 100, 0,
            new float[]{0f, 0.5f, 1f},
            new Color[]{new Color(0, 229, 255, 180), new Color(168, 85, 247, 90), new Color(236, 72, 153, 180)}));
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(-95, -95, 190, 190);

        double orbAngle = animTime * 2.2;
        int beadX = (int) (Math.cos(orbAngle) * 95);
        int beadY = (int) (Math.sin(orbAngle) * 95);
        g2.setColor(Color.WHITE);
        g2.fillOval(beadX - 4, beadY - 4, 8, 8);
        g2.setColor(new Color(0, 229, 255, 180));
        g2.fillOval(beadX - 7, beadY - 7, 14, 14);

        AffineTransform tx2 = new AffineTransform(oldTx);
        tx2.translate(rx, ry);
        tx2.rotate(Math.toRadians(-35));
        tx2.scale(1.0, 0.32);
        g2.setTransform(tx2);

        g2.setColor(new Color(99, 102, 241, 140));
        g2.setStroke(new BasicStroke(1.4f));
        g2.drawOval(-70, -70, 140, 140);

        double orbAngle2 = -animTime * 1.8;
        int bead2X = (int) (Math.cos(orbAngle2) * 70);
        int bead2Y = (int) (Math.sin(orbAngle2) * 70);
        g2.setColor(new Color(236, 72, 153));
        g2.fillOval(bead2X - 3, bead2Y - 3, 6, 6);

        g2.setTransform(oldTx);
    }

    // =========================================================================
    // MAIN CANVAS: Animated Cosmic Background, Orbs, Grid, Particles, Shadows
    // =========================================================================
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            // 1. Deep Space Cosmic Background Gradient
            GradientPaint bgGradient = new GradientPaint(
                0, 0, BG_DARK_TOP,
                width * 0.5f, height, BG_DARK_BOTTOM
            );
            g2.setPaint(bgGradient);
            g2.fillRect(0, 0, width, height);

            // 2. Futuristic Cyber Perspective Grid (Floor)
            drawCyberGrid(g2, width, height);

            // 3. Dynamic Moving Glowing Orbs
            drawGlowingOrbs(g2, width, height);

            // 4. Particle Constellation Field
            drawParticles(g2);

            // 5. Layered Ambient Drop Shadow and Outer Glow behind Login Card
            if (cardPanel != null) {
                int cx = cardPanel.getX();
                int cy = cardPanel.getY();
                int cw = cardPanel.getWidth();
                int ch = cardPanel.getHeight();

                g2.setColor(new Color(0, 0, 0, 80));
                g2.fill(new RoundRectangle2D.Float(cx - 12, cy + 18, cw + 24, ch + 24, 38, 38));

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fill(new RoundRectangle2D.Float(cx - 4, cy + 8, cw + 8, ch + 16, 32, 32));

                g2.setColor(new Color(99, 102, 241, 35));
                g2.setStroke(new BasicStroke(6.0f));
                g2.draw(new RoundRectangle2D.Float(cx - 2, cy - 2, cw + 4, ch + 4, 30, 30));
            }

            g2.dispose();
        }

        private void drawCyberGrid(Graphics2D g2, int w, int h) {
            int horizonY = (int) (h * 0.72f);
            g2.setStroke(new BasicStroke(1.0f));

            for (int y = horizonY; y < h; y += 22) {
                float alpha = (float) (y - horizonY) / (h - horizonY) * 0.16f;
                g2.setColor(new Color(ACCENT_INDIGO.getRed(), ACCENT_INDIGO.getGreen(), ACCENT_INDIGO.getBlue(), (int) (alpha * 255)));
                g2.drawLine(0, y, w, y);
            }

            int vanishingX = w / 2 + (int) (smoothParallaxX * 40);
            for (int x = -w / 2; x <= w * 1.5; x += 65) {
                g2.setColor(new Color(0, 229, 255, 18));
                g2.drawLine(vanishingX, horizonY, x, h);
            }
        }

        private void drawGlowingOrbs(Graphics2D g2, int w, int h) {
            float orb1X = w * 0.22f + (float) Math.sin(animTime * 0.6f) * 60 + smoothParallaxX * 30;
            float orb1Y = h * 0.28f + (float) Math.cos(animTime * 0.5f) * 50 + smoothParallaxY * 20;
            drawOrb(g2, orb1X, orb1Y, 320, ACCENT_CYAN, 0.25f);

            float orb2X = w * 0.78f + (float) Math.cos(animTime * 0.45f) * 70 - smoothParallaxX * 35;
            float orb2Y = h * 0.70f + (float) Math.sin(animTime * 0.55f) * 60 - smoothParallaxY * 25;
            drawOrb(g2, orb2X, orb2Y, 380, ACCENT_VIOLET, 0.28f);

            float orb3X = w * 0.85f + (float) Math.sin(animTime * 0.7f) * 45;
            float orb3Y = h * 0.18f + (float) Math.cos(animTime * 0.6f) * 40;
            drawOrb(g2, orb3X, orb3Y, 260, ACCENT_MAGENTA, 0.20f);

            float orb4X = w * 0.12f;
            float orb4Y = h * 0.85f;
            drawOrb(g2, orb4X, orb4Y, 300, ACCENT_INDIGO, 0.22f);
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

        private void drawParticles(Graphics2D g2) {
            int pCount = particles.size();

            for (int i = 0; i < pCount; i++) {
                Particle p1 = particles.get(i);
                for (int j = i + 1; j < pCount; j++) {
                    Particle p2 = particles.get(j);
                    float dx = p1.x - p2.x;
                    float dy = p1.y - p2.y;
                    float distSq = dx * dx + dy * dy;

                    if (distSq < 4900) {
                        float dist = (float) Math.sqrt(distSq);
                        float lineAlpha = (1.0f - dist / 70.0f) * 0.22f;
                        g2.setColor(new Color(0, 229, 255, (int) (lineAlpha * 255)));
                        g2.setStroke(new BasicStroke(0.8f));
                        g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
                    }
                }
            }

            for (Particle p : particles) {
                float twinkle = (float) (Math.sin(animTime * p.pulseSpeed + p.pulsePhase) * 0.5 + 0.5);
                float curAlpha = p.baseAlpha * (0.4f + twinkle * 0.6f);
                g2.setColor(new Color(255, 255, 255, (int) (curAlpha * 255)));
                g2.fill(new Ellipse2D.Float(p.x, p.y, p.size, p.size));

                if (p.size > 2.2f) {
                    g2.setColor(new Color(0, 229, 255, (int) (curAlpha * 0.4f * 255)));
                    g2.fill(new Ellipse2D.Float(p.x - 2, p.y - 2, p.size + 4, p.size + 4));
                }
            }
        }
    }

    // =========================================================================
    // CUSTOM UI CONTROLS: ModernTextField, ModernPasswordField, GlowButton
    // =========================================================================

    private static class ModernTextField extends JTextField {
        enum IconType { USER, LOCK }
        private final IconType iconType;
        private final String placeholder;
        private boolean isFocused = false;
        private float focusProgress = 0f;
        private Timer glowTimer;

        public ModernTextField(String placeholder, IconType iconType) {
            this.placeholder = placeholder;
            this.iconType = iconType;
            setFont(FONT_INPUT);
            setForeground(TEXT_WHITE);
            setCaretColor(ACCENT_CYAN);
            setOpaque(false);
            setBorder(new EmptyBorder(10, 44, 10, 16));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    animateFocus(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    animateFocus(false);
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                }
            });
        }

        private void animateFocus(boolean target) {
            if (glowTimer != null && glowTimer.isRunning()) glowTimer.stop();
            glowTimer = new Timer(15, e -> {
                if (target) {
                    focusProgress = Math.min(1.0f, focusProgress + 0.15f);
                    if (focusProgress >= 1.0f) glowTimer.stop();
                } else {
                    focusProgress = Math.max(0.0f, focusProgress - 0.15f);
                    if (focusProgress <= 0.0f) glowTimer.stop();
                }
                repaint();
            });
            glowTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(15, 18, 42, 210));
            g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 14, 14));

            if (focusProgress > 0.01f) {
                g2.setColor(new Color(0, 229, 255, (int) (focusProgress * 50)));
                g2.setStroke(new BasicStroke(4.0f));
                g2.draw(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 14, 14));
            }

            Color baseBorder = new Color(51, 65, 85, 180);
            Color activeBorder = ACCENT_CYAN;
            int r = (int) (baseBorder.getRed() + (activeBorder.getRed() - baseBorder.getRed()) * focusProgress);
            int gr = (int) (baseBorder.getGreen() + (activeBorder.getGreen() - baseBorder.getGreen()) * focusProgress);
            int b = (int) (baseBorder.getBlue() + (activeBorder.getBlue() - baseBorder.getBlue()) * focusProgress);
            g2.setColor(new Color(r, gr, b, 230));
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 14, 14));

            drawFieldIcon(g2, 16, h / 2);

            g2.dispose();
            super.paintComponent(g);

            if (getText().isEmpty() && !isFocusOwner()) {
                Graphics2D gp = (Graphics2D) g.create();
                gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                gp.setFont(FONT_INPUT);
                gp.setColor(TEXT_DIM);
                Insets in = getInsets();
                FontMetrics fm = gp.getFontMetrics();
                gp.drawString(placeholder, in.left, (h - fm.getHeight()) / 2 + fm.getAscent());
                gp.dispose();
            }
        }

        private void drawFieldIcon(Graphics2D g2, int ix, int iy) {
            g2.setColor(isFocused ? ACCENT_CYAN : TEXT_DIM);
            g2.setStroke(new BasicStroke(1.5f));

            if (iconType == IconType.USER) {
                g2.drawOval(ix - 5, iy - 10, 10, 10);
                Path2D shoulders = new Path2D.Double();
                shoulders.moveTo(ix - 8, iy + 7);
                shoulders.curveTo(ix - 8, iy + 2, ix + 8, iy + 2, ix + 8, iy + 7);
                g2.draw(shoulders);
            } else {
                g2.drawRoundRect(ix - 6, iy - 2, 12, 10, 3, 3);
                g2.drawArc(ix - 4, iy - 8, 8, 8, 0, 180);
            }
        }
    }

    private static class ModernPasswordField extends JPasswordField {
        private final String placeholder;
        private boolean isFocused = false;
        private boolean isPasswordVisible = false;
        private float focusProgress = 0f;
        private Timer glowTimer;
        private final int eyeWidth = 32;

        public ModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setFont(FONT_INPUT);
            setForeground(TEXT_WHITE);
            setCaretColor(ACCENT_CYAN);
            setEchoChar('•');
            setOpaque(false);
            setBorder(new EmptyBorder(10, 44, 10, 44));

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    isFocused = true;
                    animateFocus(true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    isFocused = false;
                    animateFocus(false);
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    requestFocusInWindow();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getX() >= getWidth() - eyeWidth - 10) {
                        isPasswordVisible = !isPasswordVisible;
                        setEchoChar(isPasswordVisible ? (char) 0 : '•');
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (e.getX() >= getWidth() - eyeWidth - 10) {
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        setCursor(new Cursor(Cursor.TEXT_CURSOR));
                    }
                }
            });
        }

        private void animateFocus(boolean target) {
            if (glowTimer != null && glowTimer.isRunning()) glowTimer.stop();
            glowTimer = new Timer(15, e -> {
                if (target) {
                    focusProgress = Math.min(1.0f, focusProgress + 0.15f);
                    if (focusProgress >= 1.0f) glowTimer.stop();
                } else {
                    focusProgress = Math.max(0.0f, focusProgress - 0.15f);
                    if (focusProgress <= 0.0f) glowTimer.stop();
                }
                repaint();
            });
            glowTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(15, 18, 42, 210));
            g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 14, 14));

            if (focusProgress > 0.01f) {
                g2.setColor(new Color(0, 229, 255, (int) (focusProgress * 50)));
                g2.setStroke(new BasicStroke(4.0f));
                g2.draw(new RoundRectangle2D.Float(2, 2, w - 4, h - 4, 14, 14));
            }

            Color baseBorder = new Color(51, 65, 85, 180);
            Color activeBorder = ACCENT_CYAN;
            int r = (int) (baseBorder.getRed() + (activeBorder.getRed() - baseBorder.getRed()) * focusProgress);
            int gr = (int) (baseBorder.getGreen() + (activeBorder.getGreen() - baseBorder.getGreen()) * focusProgress);
            int b = (int) (baseBorder.getBlue() + (activeBorder.getBlue() - baseBorder.getBlue()) * focusProgress);
            g2.setColor(new Color(r, gr, b, 230));
            g2.setStroke(new BasicStroke(1.4f));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 14, 14));

            g2.setColor(isFocused ? ACCENT_CYAN : TEXT_DIM);
            g2.setStroke(new BasicStroke(1.5f));
            int ix = 16;
            int iy = h / 2;
            g2.drawRoundRect(ix - 6, iy - 2, 12, 10, 3, 3);
            g2.drawArc(ix - 4, iy - 8, 8, 8, 0, 180);

            int eyeX = w - 28;
            int eyeY = h / 2;
            g2.setColor(isPasswordVisible ? ACCENT_CYAN : TEXT_DIM);

            Path2D eye = new Path2D.Double();
            eye.moveTo(eyeX - 8, eyeY);
            eye.quadTo(eyeX, eyeY - 6, eyeX + 8, eyeY);
            eye.quadTo(eyeX, eyeY + 6, eyeX - 8, eyeY);
            g2.draw(eye);
            g2.fillOval(eyeX - 3, eyeY - 3, 6, 6);

            if (!isPasswordVisible) {
                g2.drawLine(eyeX - 9, eyeY - 7, eyeX + 9, eyeY + 7);
            }

            g2.dispose();
            super.paintComponent(g);

            if (getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D gp = (Graphics2D) g.create();
                gp.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                gp.setFont(FONT_INPUT);
                gp.setColor(TEXT_DIM);
                Insets in = getInsets();
                FontMetrics fm = gp.getFontMetrics();
                gp.drawString(placeholder, in.left, (h - fm.getHeight()) / 2 + fm.getAscent());
                gp.dispose();
            }
        }
    }

    private static class ModernCheckBox extends JCheckBox {
        public ModernCheckBox(String text) {
            super(text);
            setFont(FONT_SMALL);
            setForeground(TEXT_MUTED);
            setOpaque(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int h = getHeight();
            int boxSize = 16;
            int boxY = (h - boxSize) / 2;

            if (isSelected()) {
                g2.setPaint(new LinearGradientPaint(0, boxY, boxSize, boxY + boxSize,
                    new float[]{0f, 1f}, new Color[]{ACCENT_CYAN, ACCENT_INDIGO}));
                g2.fill(new RoundRectangle2D.Float(1, boxY, boxSize, boxSize, 5, 5));

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D check = new Path2D.Double();
                check.moveTo(4, boxY + 8);
                check.lineTo(7, boxY + 12);
                check.lineTo(13, boxY + 4);
                g2.draw(check);
            } else {
                g2.setColor(new Color(20, 24, 52, 220));
                g2.fill(new RoundRectangle2D.Float(1, boxY, boxSize, boxSize, 5, 5));
                g2.setColor(new Color(71, 85, 105));
                g2.setStroke(new BasicStroke(1.2f));
                g2.draw(new RoundRectangle2D.Float(1, boxY, boxSize, boxSize, 5, 5));
            }

            g2.setFont(getFont());
            g2.setColor(isSelected() ? TEXT_WHITE : TEXT_MUTED);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), boxSize + 10, (h - fm.getHeight()) / 2 + fm.getAscent());

            g2.dispose();
        }
    }

    private static class GlowButton extends JButton {
        private float hoverProgress = 0f;
        private boolean isHovered = false;
        private boolean isPressed = false;
        private boolean isLoading = false;
        private Timer hoverTimer;
        private float sheenOffset = -1.0f;

        public GlowButton(String text) {
            super(text);
            setFont(FONT_BUTTON);
            setForeground(Color.WHITE);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    animateHover(true);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false;
                    animateHover(false);
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    isPressed = true;
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    isPressed = false;
                    repaint();
                }
            });
        }

        public void setLoading(boolean loading) {
            this.isLoading = loading;
            setEnabled(!loading);
            repaint();
        }

        private void animateHover(boolean target) {
            if (hoverTimer != null && hoverTimer.isRunning()) hoverTimer.stop();
            hoverTimer = new Timer(15, e -> {
                if (target) {
                    hoverProgress = Math.min(1.0f, hoverProgress + 0.12f);
                    sheenOffset += 0.08f;
                    if (hoverProgress >= 1.0f && sheenOffset > 2.0f) hoverTimer.stop();
                } else {
                    hoverProgress = Math.max(0.0f, hoverProgress - 0.12f);
                    sheenOffset = -1.0f;
                    if (hoverProgress <= 0.0f) hoverTimer.stop();
                }
                repaint();
            });
            hoverTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int pressOffset = isPressed ? 2 : 0;

            if (hoverProgress > 0.01f) {
                g2.setColor(new Color(0, 229, 255, (int) (hoverProgress * 55)));
                g2.fill(new RoundRectangle2D.Float(2, 4 + pressOffset, w - 4, h - 4, 20, 20));

                g2.setColor(new Color(168, 85, 247, (int) (hoverProgress * 65)));
                g2.setStroke(new BasicStroke(3.0f));
                g2.draw(new RoundRectangle2D.Float(1, 1 + pressOffset, w - 2, h - 2, 16, 16));
            }

            LinearGradientPaint btnGrad = new LinearGradientPaint(
                0, 0, w, h,
                new float[]{0.0f, 0.5f, 1.0f},
                new Color[]{
                    new Color(0, 210, 255),
                    new Color(99, 102, 241),
                    new Color(168, 85, 247)
                }
            );
            g2.setPaint(btnGrad);
            g2.fill(new RoundRectangle2D.Float(2, 2 + pressOffset, w - 4, h - 4, 14, 14));

            if (hoverProgress > 0.01f && sheenOffset > -0.5f && sheenOffset < 2.5f) {
                int sheenX = (int) (w * (sheenOffset - 0.5f));
                LinearGradientPaint sheenGrad = new LinearGradientPaint(
                    sheenX, 0, sheenX + 80, 0,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 0), new Color(255, 255, 255, 90), new Color(255, 255, 255, 0)}
                );
                g2.setPaint(sheenGrad);
                g2.fill(new RoundRectangle2D.Float(2, 2 + pressOffset, w - 4, h - 4, 14, 14));
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();

            if (isLoading) {
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2.5f));
                int spinX = w / 2 - 10;
                int spinY = h / 2 - 10 + pressOffset;
                g2.drawArc(spinX, spinY, 20, 20, (int) (System.currentTimeMillis() / 3 % 360), 270);
            } else {
                String txt = getText();
                int textX = (w - fm.stringWidth(txt)) / 2;
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent() + pressOffset;

                g2.setColor(new Color(0, 0, 0, 70));
                g2.drawString(txt, textX, textY + 1);

                g2.setColor(Color.WHITE);
                g2.drawString(txt, textX, textY);
            }

            g2.dispose();
        }
    }

    private static class StatusBanner extends JPanel {
        private String message = "";
        private boolean isSuccess = false;
        private float alpha = 0f;
        private Timer fadeTimer;

        public StatusBanner() {
            setOpaque(false);
            setVisible(false);
        }

        public void clear() {
            if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
            this.message = "";
            this.alpha = 0f;
            setVisible(false);
            repaint();
        }

        public void showError(String msg) {
            this.message = msg;
            this.isSuccess = false;
            setVisible(true);
            fadeIn();
        }

        public void showSuccess(String msg) {
            this.message = msg;
            this.isSuccess = true;
            setVisible(true);
            fadeIn();
        }

        private void fadeIn() {
            if (fadeTimer != null && fadeTimer.isRunning()) fadeTimer.stop();
            alpha = 0f;
            fadeTimer = new Timer(20, e -> {
                alpha = Math.min(1.0f, alpha + 0.12f);
                repaint();
                if (alpha >= 1.0f) fadeTimer.stop();
            });
            fadeTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (alpha <= 0.01f || message.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                g2.dispose();
                return;
            }

            Color bgColor = isSuccess ? new Color(16, 185, 129, 45) : new Color(239, 68, 68, 45);
            Color borderColor = isSuccess ? new Color(52, 211, 153, 190) : new Color(248, 113, 113, 190);
            Color textColor = isSuccess ? new Color(167, 243, 208) : new Color(254, 202, 202);

            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 10, 10));

            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 10, 10));

            g2.setFont(FONT_SMALL);
            g2.setColor(textColor);
            FontMetrics fm = g2.getFontMetrics();

            String fullText = (isSuccess ? "✓  " : "⚠️  ") + message;
            int tx = 14;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(fullText, tx, ty);

            g2.dispose();
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

    // =========================================================================
    // Application Entry Point
    // =========================================================================
    public static void main(String[] args) {
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(LoginFrame::new);
    }
}

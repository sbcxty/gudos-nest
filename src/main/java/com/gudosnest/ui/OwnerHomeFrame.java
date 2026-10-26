package com.gudosnest.ui;
 
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
 
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
 
import com.gudosnest.db.Seed;
import com.gudosnest.model.User;
import com.gudosnest.ui.OwnerHomePanels.BookingsPanel;
import com.gudosnest.ui.OwnerHomePanels.DashboardPanel;
import com.gudosnest.ui.OwnerHomePanels.IncomePanel;
import com.gudosnest.ui.OwnerHomePanels.InventoryPanel;
import com.gudosnest.ui.OwnerHomePanels.MaintenancePanel;
import com.gudosnest.ui.OwnerHomePanels.PropertiesPanel;
 
/**
 * OWNER HOME PAGE - shell only. Styled to match the renter home page:
 * soft lavender page, big white rounded card, burgundy-to-purple sidebar
 * with pill navigation, and a blurred-blob header banner.
 * Each screen lives in its own file under ui/OwnerHomePanels/.
 */
public class OwnerHomeFrame extends JFrame {
 
    // ---- Palette (same as Renterpage / AuthFrame) ------------------------
    private static final Color BURGUNDY = Color.decode("#36151E");
    private static final Color PURPLE = Color.decode("#593F62");
    private static final Color VIOLET = Color.decode("#7B6D8D");
    private static final Color BLUE = Color.decode("#8499B1");
    private static final Color LIGHT_BLUE = Color.decode("#A5C4D4");
 
    private static final Color PAGE_BG = new Color(234, 231, 238);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(115, 108, 124);
    private static final Color FIELD_BORDER = new Color(227, 222, 232);
    private static final int B = Font.BOLD, P = Font.PLAIN;
 
    private final User user;
    private final JPanel cardsPanel = new JPanel(new CardLayout());
    private final CardLayout cards = (CardLayout) cardsPanel.getLayout();
    private final JLabel headerTitle = new JLabel("Dashboard");
    private final JLabel headerSubtitle = new JLabel(
            "Welcome back - here is the current state of your properties.");
    private final Map<String, SideButton> navButtons = new LinkedHashMap<>();
 
    /** Demo constructor: shows the seeded owner account. */
    public OwnerHomeFrame() {
        this(new User(1, "Alex Riviera", "owner@gudosnest.com", "", true, false, "active"));
    }
 
    /** Constructor used after a real login so the identity shown is the actual user. */
    public OwnerHomeFrame(User user) {
        this.user = user;
        setTitle("Gudo's Nest - Owner Portal");
        setSize(1150, 700);
        setMinimumSize(new Dimension(980, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
 
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.setBorder(new EmptyBorder(22, 26, 22, 26));
 
        RoundedPanel card = new RoundedPanel(32, WHITE, FIELD_BORDER);
        card.setLayout(new BorderLayout());
        card.add(buildSidebar(), BorderLayout.WEST);
        card.add(buildMain(), BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
 
        Seed.run();
    }
 
    // ------------------------------------------------------------------
    // Sidebar: rounded burgundy -> purple panel with soft glow blobs
    // ------------------------------------------------------------------
    private JComponent buildSidebar() {
        JPanel sidebar = new Painted((g, w, h) -> {
            Shape shape = new RoundRectangle2D.Float(0, 0, w, h, 26, 26);
            g.setPaint(new GradientPaint(0, 0, BURGUNDY, w, h, PURPLE));
            g.fill(shape);
            blob(g, shape, w * 0.95f, h * 0.05f, w * 0.9f, BLUE, 110);
            blob(g, shape, w * 0.05f, h * 0.55f, w * 0.8f, VIOLET, 90);
            blob(g, shape, w * 0.90f, h * 1.00f, w * 1.0f, LIGHT_BLUE, 120);
        });
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(26, 16, 16, 16));
        sidebar.setPreferredSize(new Dimension(236, 0));
 
        sidebar.add(label("Gudo's Nest", B, 22, WHITE, Component.CENTER_ALIGNMENT));
        sidebar.add(label("Y O U R   S P A C E .   Y O U R   P L A C E .", B, 8, LIGHT_BLUE,
                Component.CENTER_ALIGNMENT));
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(divider());
        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(label("MENU", B, 10, alpha(WHITE, 150), Component.LEFT_ALIGNMENT));
        sidebar.add(Box.createVerticalStrut(8));
 
        String[][] nav = {
            {"Dashboard", "dashboard"}, {"Properties", "properties"}, {"Bookings", "bookings"},
            {"Payments & Income", "income"}, {"Inventory", "inventory"}, {"Maintenance", "maintenance"}
        };
        for (String[] n : nav) {
            SideButton b = new SideButton(n[0], false);
            b.addActionListener(e -> showSection(n[1], n[0]));
            b.setActive(n[1].equals("dashboard"));
            navButtons.put(n[1], b);
            sidebar.add(b);
            sidebar.add(Box.createVerticalStrut(6));
        }
 
        sidebar.add(Box.createVerticalGlue());
 
        SideButton logout = new SideButton("Log out", true);
        logout.addActionListener(e -> {
            new AuthFrame().setVisible(true);
            dispose();
        });
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(userCard());
 
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(14, 14, 14, 0));
        wrap.add(sidebar, BorderLayout.CENTER);
        return wrap;
    }
 
    private JComponent divider() {
        JPanel line = new JPanel();
        line.setBackground(alpha(WHITE, 45));
        line.setPreferredSize(new Dimension(0, 1));
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return line;
    }
 
    /** Avatar chip + account identity on a frosted rounded card. */
    private JComponent userCard() {
        final String initials = initialsOf(user.getFullName());
        JPanel card = new Painted((g, w, h) -> {
            RoundRectangle2D shape = new RoundRectangle2D.Float(0.5f, 0.5f, w - 1, h - 1, 20, 20);
            g.setColor(alpha(WHITE, 30));
            g.fill(shape);
            g.setColor(alpha(WHITE, 60));
            g.draw(shape);
        });
        card.setLayout(new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
 
        JComponent avatar = new JComponent() {
            { setPreferredSize(new Dimension(40, 40)); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = smooth(g0);
                int s = Math.min(getWidth(), getHeight());
                int x = (getWidth() - s) / 2, y = (getHeight() - s) / 2;
                g.setPaint(new GradientPaint(x, y, WHITE, x + s, y + s, LIGHT_BLUE));
                g.fillOval(x, y, s, s);
                g.setFont(font(B, 15));
                g.setColor(BURGUNDY);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(initials, x + (s - fm.stringWidth(initials)) / 2,
                        y + (s + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
        };
 
        String roleText = user.isOwner() && user.isRenter() ? "Owner & renter"
                : user.isOwner() ? "Property owner" : "Renter";
        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        labels.add(label(user.getFullName(), B, 13, WHITE, Component.LEFT_ALIGNMENT));
        labels.add(label(roleText, P, 11, LIGHT_BLUE, Component.LEFT_ALIGNMENT));
 
        card.add(avatar, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, card.getPreferredSize().height));
        return card;
    }
 
    /** "Juan Dela Cruz" -> "JD" (single word -> first two letters). */
    private static String initialsOf(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
    }
 
    // ------------------------------------------------------------------
    // Main area: blob banner header + screen cards
    // ------------------------------------------------------------------
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 12));
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(14, 18, 14, 18));
 
        JPanel banner = new Painted(OwnerHomeFrame::paintBrand);
        banner.setLayout(new BorderLayout());
        banner.setBorder(new EmptyBorder(18, 26, 18, 26));
        banner.setPreferredSize(new Dimension(0, 116));
 
        headerTitle.setFont(font(B, 28));
        headerTitle.setForeground(BURGUNDY);
        headerSubtitle.setFont(font(P, 13));
        headerSubtitle.setForeground(BURGUNDY);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(label("O W N E R   P O R T A L", B, 10, PURPLE, Component.LEFT_ALIGNMENT));
        titles.add(Box.createVerticalStrut(4));
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titles.add(headerTitle);
        titles.add(Box.createVerticalStrut(4));
        titles.add(headerSubtitle);
        banner.add(titles, BorderLayout.WEST);
 
        JLabel date = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMM d")));
        date.setFont(font(B, 12));
        date.setForeground(PURPLE);
        JPanel chip = new RoundedPanel(18, alpha(WHITE, 200), FIELD_BORDER);
        chip.setLayout(new FlowLayout(FlowLayout.CENTER, 14, 8));
        chip.add(date);
        JPanel chipHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipHolder.setOpaque(false);
        chipHolder.add(chip);
        banner.add(chipHolder, BorderLayout.EAST);
        main.add(banner, BorderLayout.NORTH);
 
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(4, 0, 0, 0));
        cardsPanel.add(new DashboardPanel(), "dashboard");
        cardsPanel.add(new PropertiesPanel(), "properties");
        cardsPanel.add(new BookingsPanel(), "bookings");
        cardsPanel.add(new IncomePanel(), "income");
        cardsPanel.add(new InventoryPanel(), "inventory");
        cardsPanel.add(new MaintenancePanel(), "maintenance");
        main.add(cardsPanel, BorderLayout.CENTER);
        return main;
    }
 
    /** Switches the visible screen, header text and active nav pill. */
    void showSection(String key, String title) {
        headerTitle.setText(title);
        switch (key) {
            case "properties"   -> headerSubtitle.setText("Manage your properties and rental units.");
            case "bookings"     -> headerSubtitle.setText("Review incoming requests from renters.");
            case "income"       -> headerSubtitle.setText("Rental income, expenses and investment recovery.");
            case "inventory"    -> headerSubtitle.setText("Consumables and long-term items per property.");
            case "maintenance"  -> headerSubtitle.setText("Repairs and upkeep records.");
            default             -> headerSubtitle.setText("Welcome back - here is the current state of your properties.");
        }
        navButtons.forEach((k, b) -> b.setActive(k.equals(key)));
        cards.show(cardsPanel, key);
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OwnerHomeFrame().setVisible(true));
    }
 
    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------
    private static Font font(int style, int size) {
        return new Font("SansSerif", style, size);
    }
 
    private static JLabel label(String text, int style, int size, Color color, float alignX) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        l.setAlignmentX(alignX);
        return l;
    }
 
    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
 
    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }
 
    private static void blob(Graphics2D g2, Shape shape, float cx, float cy, float r, Color c, int a) {
        g2.setPaint(new RadialGradientPaint(new Point2D.Float(cx, cy), r,
                new float[] { 0f, 1f }, new Color[] { alpha(c, a), alpha(c, 0) }));
        g2.fill(shape);
    }
 
    /** Light banner with soft palette blobs (same as the renter hero). */
    private static void paintBrand(Graphics2D g2, int w, int h) {
        Shape shape = new RoundRectangle2D.Float(0, 0, w, h, 26, 26);
        g2.setPaint(new GradientPaint(0, 0, new Color(249, 246, 251), 0, h, new Color(214, 228, 236)));
        g2.fill(shape);
        float r = Math.max(w * 0.30f, h * 1.2f);
        blob(g2, shape, w * 0.90f, h * 0.20f, r, BLUE, 110);
        blob(g2, shape, w * 0.55f, h * 0.90f, r, PURPLE, 120);
        blob(g2, shape, w * 0.75f, h * 0.80f, r, VIOLET, 150);
        blob(g2, shape, w * 0.98f, h * 1.00f, r * 0.9f, LIGHT_BLUE, 200);
    }
 
    // ------------------------------------------------------------------
    // Custom components
    // ------------------------------------------------------------------
    private interface Painter {
        void paint(Graphics2D g2, int w, int h);
    }
 
    /** Transparent panel that paints its own background with the given painter. */
    private static class Painted extends JPanel {
        private final Painter painter;
 
        Painted(Painter painter) {
            this.painter = painter;
            setOpaque(false);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            painter.paint(g2, getWidth(), getHeight());
            g2.dispose();
        }
    }
 
    private static class RoundedPanel extends Painted {
        RoundedPanel(int arc, Color fill, Color border) {
            super((g, w, h) -> {
                RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, arc, arc);
                g.setColor(fill);
                g.fill(shape);
                g.setColor(border);
                g.draw(shape);
            });
        }
    }
 
    /** Sidebar pill: dot + label, white when active, frosted on hover; ghost = outlined. */
    private static class SideButton extends JButton {
        private final boolean ghost;
        private boolean active;
 
        SideButton(String text, boolean ghost) {
            super(text);
            this.ghost = ghost;
            setFont(font(B, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setPreferredSize(new Dimension(200, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        }
 
        void setActive(boolean active) {
            this.active = active;
            repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth(), h = getHeight();
            boolean hover = getModel().isRollover();
            if (active) {
                g2.setColor(WHITE);
                g2.fillRoundRect(0, 0, w, h, 18, 18);
            } else if (hover) {
                g2.setColor(alpha(WHITE, ghost ? 30 : 28));
                g2.fillRoundRect(0, 0, w, h, 18, 18);
            }
            if (ghost && !active) {
                g2.setColor(alpha(WHITE, 110));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);
            }
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(active ? BURGUNDY : WHITE);
            if (ghost) {
                g2.drawString(getText(), (w - fm.stringWidth(getText())) / 2, ty);
            } else {
                g2.setColor(active ? PURPLE : alpha(WHITE, 120));
                g2.fillOval(16, h / 2 - 3, 6, 6);
                g2.setColor(active ? BURGUNDY : WHITE);
                g2.drawString(getText(), 34, ty);
            }
            g2.dispose();
        }
    }
}
 
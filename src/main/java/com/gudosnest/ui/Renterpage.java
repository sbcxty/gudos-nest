package com.gudosnest.ui;

import java.awt.BasicStroke;
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
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
 
/** Renter home page, styled to match AuthFrame (login / sign up). */
public class Renterpage extends JFrame {
 
    // ---- Palette (same as AuthFrame) -----------------------------------
    private static final Color BURGUNDY = Color.decode("#36151E");
    private static final Color PURPLE = Color.decode("#593F62");
    private static final Color VIOLET = Color.decode("#7B6D8D");
    private static final Color BLUE = Color.decode("#8499B1");
    private static final Color LIGHT_BLUE = Color.decode("#A5C4D4");
 
    private static final Color PAGE_BG = new Color(234, 231, 238);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(115, 108, 124);
    private static final Color HINT = new Color(168, 161, 177);
    private static final Color FIELD_BG = new Color(250, 249, 252);
    private static final Color FIELD_BORDER = new Color(227, 222, 232);
    private static final Color HOVER_BG = new Color(244, 241, 248);
 
    private static final String APP_NAME = "Gudo's Nest";
    private static final Color[] CARD_COLORS = { BURGUNDY, PURPLE, VIOLET, BLUE, LIGHT_BLUE };
 
    private final CardLayout pages = new CardLayout();
    private final JPanel pageHolder = new JPanel(pages);
    private final Map<String, RoundedButton> navButtons = new HashMap<>();
    private int cardColorIndex = 0;
 
    private ToastPanel toast;
    private javax.swing.Timer toastTimer;
 
    public Renterpage() {
        super(APP_NAME + " - Renter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 760);
        setMinimumSize(new Dimension(900, 640));
        setLocationRelativeTo(null);
 
        buildUI();
        initToast();
        showPage("HOME");
    }
 
    // ---- UI construction -----------------------------------------------
 
    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
 
        RoundedPanel card = new RoundedPanel(32, WHITE, FIELD_BORDER);
        card.setLayout(new BorderLayout());
 
        pageHolder.setOpaque(false);
        pageHolder.add(buildHomePage(), "HOME");
        pageHolder.add(buildPlaceholderPage("Saved properties"), "SAVED");
        pageHolder.add(buildPlaceholderPage("My bookings and inquiries"), "BOOKINGS");
        pageHolder.add(buildPlaceholderPage("Messages"), "MESSAGES");
        pageHolder.add(buildPlaceholderPage("Profile and settings"), "PROFILE");
 
        card.add(buildNavBar(), BorderLayout.NORTH);
        card.add(pageHolder, BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);
 
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
    }
 
    // ---- Navigation -----------------------------------------------------
 
    private JPanel buildNavBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(18, 28, 14, 28));
 
        JPanel logoBox = new JPanel();
        logoBox.setOpaque(false);
        logoBox.setLayout(new BoxLayout(logoBox, BoxLayout.Y_AXIS));
        JLabel logo = left(new JLabel(APP_NAME));
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(BURGUNDY);
        JLabel caption = left(new JLabel("YOUR SPACE. YOUR PLACE."));
        caption.setFont(new Font("SansSerif", Font.BOLD, 10));
        caption.setForeground(PURPLE);
        logoBox.add(logo);
        logoBox.add(Box.createVerticalStrut(4));
        logoBox.add(caption);
        bar.add(logoBox, BorderLayout.WEST);
 
        JPanel links = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        links.setOpaque(false);
        links.add(navButton("Home", "HOME"));
        links.add(navButton("Saved", "SAVED"));
        links.add(navButton("Bookings", "BOOKINGS"));
        links.add(navButton("Messages", "MESSAGES"));
        bar.add(links, BorderLayout.CENTER);
 
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        right.setOpaque(false);
        right.add(navButton("Profile", "PROFILE"));
        bar.add(right, BorderLayout.EAST);
 
        // hairline under the nav
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(bar, BorderLayout.CENTER);
        JPanel line = new JPanel();
        line.setBackground(FIELD_BORDER);
        line.setPreferredSize(new Dimension(0, 1));
        wrap.add(line, BorderLayout.SOUTH);
        return wrap;
    }
 
    private RoundedButton navButton(String text, String key) {
        RoundedButton b = new RoundedButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(text.length() > 6 ? 104 : 88, 38));
        b.addActionListener(e -> showPage(key));
        navButtons.put(key, b);
        return b;
    }
 
    private void showPage(String key) {
        pages.show(pageHolder, key);
        for (Map.Entry<String, RoundedButton> en : navButtons.entrySet()) {
            if (en.getKey().equals(key)) {
                en.getValue().setColors(PURPLE, PURPLE, WHITE, PURPLE);
            } else {
                en.getValue().setColors(WHITE, HOVER_BG, PURPLE, null);
            }
        }
    }
 
    // ---- Home page ------------------------------------------------------
 
    private JPanel buildHomePage() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        page.setBorder(new EmptyBorder(18, 28, 18, 28));
 
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.add(buildHero());
        top.add(Box.createVerticalStrut(14));
        top.add(buildSearchBar());
 
        page.add(top, BorderLayout.NORTH);
        page.add(buildListingGrid(), BorderLayout.CENTER);
        page.add(buildSidePanel(), BorderLayout.EAST);
        return page;
    }
 
    private JComponent buildHero() {
        BrandPanel hero = new BrandPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBorder(new EmptyBorder(20, 28, 20, 28));
        hero.setPreferredSize(new Dimension(0, 130));
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        hero.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel eyebrow = left(new JLabel("Welcome back, renter."));
        eyebrow.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eyebrow.setForeground(BURGUNDY);
 
        JLabel headline = left(new JLabel("Find a place to call your own."));
        headline.setFont(new Font("SansSerif", Font.BOLD, 28));
        headline.setForeground(BURGUNDY);
 
        JLabel sub = left(new JLabel("Browse listings, save favourites and book a viewing."));
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(BURGUNDY);
 
        hero.add(Box.createVerticalGlue());
        hero.add(eyebrow);
        hero.add(Box.createVerticalStrut(6));
        hero.add(headline);
        hero.add(Box.createVerticalStrut(8));
        hero.add(sub);
        hero.add(Box.createVerticalGlue());
        return hero;
    }
 
    private JComponent buildSearchBar() {
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        search.setOpaque(false);
        search.setAlignmentX(Component.LEFT_ALIGNMENT);
        search.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
 
        JTextField location = new HintTextField("Location (e.g. Davao City)");
        InputBox locationBox = new InputBox(location, null);
        locationBox.setPreferredSize(new Dimension(280, 48));
 
        JComboBox<String> type = new JComboBox<>(
                new String[] { "Any type", "Apartment", "Room", "House", "Dorm" });
        JComboBox<String> price = new JComboBox<>(
                new String[] { "Any price", "Under 5,000", "5,000 - 10,000", "10,000+" });
 
        RoundedButton go = new RoundedButton("Search");
        go.setFont(new Font("SansSerif", Font.BOLD, 14));
        go.setColors(BURGUNDY, PURPLE, WHITE, null);
        go.setPreferredSize(new Dimension(120, 48));
        go.addActionListener(e -> showToast(
                "Search results will appear in the grid below (planned).", true));
 
        search.add(locationBox);
        search.add(new SelectBox(type, 150));
        search.add(new SelectBox(price, 160));
        search.add(go);
        return search;
    }
 
    private JComponent buildListingGrid() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
 
        JLabel title = new JLabel("Available properties");
        title.setForeground(BURGUNDY);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 10, 0));
        wrapper.add(title, BorderLayout.NORTH);
 
        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setOpaque(false);
 
        String[][] sample = {
            { "Studio near campus", "Davao City", "6,500" },
            { "2-bedroom apartment", "Lanang", "14,000" },
            { "Shared room (female)", "Matina", "2,800" },
            { "Family house", "Toril", "18,500" },
            { "Dorm bed space", "Ecoland", "2,200" },
            { "1-bedroom condo", "Downtown", "11,000" },
        };
        for (String[] p : sample) {
            grid.add(new PropertyCard(p[0], p[1], p[2]));
        }
 
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(grid, BorderLayout.NORTH);
 
        JScrollPane scroll = new JScrollPane(top);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }
 
    private JPanel buildSidePanel() {
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(250, 0));
        side.setBorder(new EmptyBorder(0, 16, 0, 0));
 
        side.add(sideSection("Saved properties",
                "Studio near campus", "Dorm bed space", "1-bedroom condo"));
        side.add(Box.createVerticalStrut(16));
        side.add(sideSection("My bookings",
                "Viewing: Fri 2:00 PM", "Inquiry sent: Matina room"));
        side.add(Box.createVerticalGlue());
        return side;
    }
 
    private JComponent sideSection(String heading, String... rows) {
        RoundedPanel panel = new RoundedPanel(20, FIELD_BG, FIELD_BORDER);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
 
        JLabel h = left(new JLabel(heading));
        h.setFont(new Font("SansSerif", Font.BOLD, 14));
        h.setForeground(PURPLE);
        panel.add(h);
 
        for (String row : rows) {
            panel.add(Box.createVerticalStrut(8));
            JLabel l = left(new JLabel(row));
            l.setFont(new Font("SansSerif", Font.PLAIN, 13));
            l.setForeground(BURGUNDY);
            panel.add(l);
        }
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));
        return panel;
    }
 
    // ---- Other pages / footer ------------------------------------------
 
    private JPanel buildPlaceholderPage(String name) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel(name + " (placeholder)");
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(PURPLE);
        panel.add(label);
        return panel;
    }
 
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 14));
        footer.setOpaque(false);
        JLabel l = new JLabel(APP_NAME + "  |  About  |  Help  |  Contact  |  Terms");
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        l.setForeground(MUTED);
        footer.add(l);
        return footer;
    }
 
    // ---- Property card --------------------------------------------------
 
    private class PropertyCard extends RoundedPanel {
        PropertyCard(String title, String location, String monthlyPrice) {
            super(22, WHITE, FIELD_BORDER);
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(8, 8, 8, 8));
 
            Color accent = CARD_COLORS[cardColorIndex % CARD_COLORS.length];
            cardColorIndex++;
            PhotoPanel photo = new PhotoPanel(accent);
            photo.setPreferredSize(new Dimension(0, 110));
            add(photo, BorderLayout.NORTH);
 
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setOpaque(false);
            info.setBorder(new EmptyBorder(12, 8, 4, 8));
 
            JLabel t = left(new JLabel(title));
            t.setFont(new Font("SansSerif", Font.BOLD, 14));
            t.setForeground(BURGUNDY);
            JLabel loc = left(new JLabel(location));
            loc.setFont(new Font("SansSerif", Font.PLAIN, 12));
            loc.setForeground(MUTED);
            JLabel price = left(new JLabel("PHP " + monthlyPrice + " / month"));
            price.setFont(new Font("SansSerif", Font.BOLD, 13));
            price.setForeground(PURPLE);
 
            info.add(t);
            info.add(Box.createVerticalStrut(3));
            info.add(loc);
            info.add(Box.createVerticalStrut(6));
            info.add(price);
            add(info, BorderLayout.CENTER);
 
            JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
            actions.setOpaque(false);
            actions.setBorder(new EmptyBorder(8, 8, 6, 8));
 
            RoundedButton save = new RoundedButton("Save");
            save.setFont(new Font("SansSerif", Font.BOLD, 12));
            save.setColors(WHITE, HOVER_BG, PURPLE, BLUE);
            save.setPreferredSize(new Dimension(0, 36));
            save.addActionListener(e -> showToast(title + " saved.", true));
 
            RoundedButton view = new RoundedButton("View");
            view.setFont(new Font("SansSerif", Font.BOLD, 12));
            view.setColors(BURGUNDY, PURPLE, WHITE, null);
            view.addActionListener(e -> showToast(
                    "Individual property page opens here (built by another teammate).",
                    true));
 
            actions.add(save);
            actions.add(view);
            add(actions, BorderLayout.SOUTH);
        }
    }
 
    // ---- Helpers --------------------------------------------------------
 
    private static <T extends JComponent> T left(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }
 
    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }
 
    private static void paintHint(JTextComponent c, Graphics g, String hint) {
        if (c.getDocument().getLength() != 0) {
            return;
        }
        Graphics2D g2 = smooth(g);
        g2.setColor(HINT);
        g2.setFont(c.getFont());
        FontMetrics fm = g2.getFontMetrics();
        Insets in = c.getInsets();
        g2.drawString(hint, in.left,
                (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }
 
    // ---- Toast (replaces pop-up dialogs) --------------------------------
 
    private void initToast() {
        toast = new ToastPanel();
        toast.setVisible(false);
        toast.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toast.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideToast();
            }
        });
        getLayeredPane().add(toast, JLayeredPane.POPUP_LAYER);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionToast();
            }
        });
    }
 
    private void showToast(String message, boolean success) {
        toast.setMessage(message, success);
        positionToast();
        toast.setVisible(true);
        if (toastTimer != null) {
            toastTimer.stop();
        }
        toastTimer = new javax.swing.Timer(4500, e -> hideToast());
        toastTimer.setRepeats(false);
        toastTimer.start();
    }
 
    private void hideToast() {
        if (toastTimer != null) {
            toastTimer.stop();
        }
        toast.setVisible(false);
    }
 
    private void positionToast() {
        Dimension size = toast.getPreferredSize();
        int x = (getLayeredPane().getWidth() - size.width) / 2;
        toast.setBounds(x, 14, size.width, size.height);
        toast.validate();
        toast.repaint();
    }
 
    // ---- Custom components ----------------------------------------------
 
    private static class ToastPanel extends JPanel {
        private static final Color ROSE = new Color(196, 70, 96);
        private static final int TEXT_WIDTH = 340;
        private final JLabel label = new JLabel();
        private boolean success;
 
        ToastPanel() {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(8 + 14, 8 + 56, 8 + 14, 8 + 22));
            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            label.setForeground(BURGUNDY);
            add(label, BorderLayout.CENTER);
        }
 
        void setMessage(String message, boolean success) {
            this.success = success;
            label.setText(message);
            if (label.getPreferredSize().width > TEXT_WIDTH) {
                label.setText(wrap(message));
            }
            repaint();
        }
 
        private int textWidth(String s) {
            label.setText(s);
            return label.getPreferredSize().width;
        }
 
        private String wrap(String message) {
            StringBuilder html = new StringBuilder("<html>");
            String line = "";
            for (String word : message.split(" ")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (!line.isEmpty() && textWidth(candidate) > TEXT_WIDTH) {
                    html.append(escape(line)).append("<br>");
                    line = word;
                } else {
                    line = candidate;
                }
            }
            return html.append(escape(line)).append("</html>").toString();
        }
 
        private static String escape(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int bx = 8, by = 8, bw = getWidth() - 16, bh = getHeight() - 16;
 
            for (int i = 0; i < 7; i++) {
                g2.setColor(new Color(54, 21, 30, 9));
                g2.fillRoundRect(bx - i, by - i + 4, bw + 2 * i, bh + 2 * i,
                        18 + 2 * i, 18 + 2 * i);
            }
            g2.setColor(WHITE);
            g2.fillRoundRect(bx, by, bw, bh, 18, 18);
            g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(bx, by, bw - 1, bh - 1, 18, 18);
 
            int d = 26;
            int ix = bx + 16;
            int iy = by + (bh - d) / 2;
            float cx = ix + d / 2f;
            float cy = iy + d / 2f;
 
            g2.setColor(success ? PURPLE : ROSE);
            g2.fillOval(ix, iy, d, d);
            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (success) {
                Path2D check = new Path2D.Float();
                check.moveTo(cx - 5.5f, cy + 0.5f);
                check.lineTo(cx - 1.5f, cy + 4.5f);
                check.lineTo(cx + 6f, cy - 4f);
                g2.draw(check);
            } else {
                g2.draw(new Line2D.Float(cx, cy - 6f, cx, cy + 1.5f));
                g2.fill(new Ellipse2D.Float(cx - 1.4f, cy + 4.2f, 2.8f, 2.8f));
            }
            g2.dispose();
        }
    }
 
    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final Color border;
 
        RoundedPanel(int arc, Color fill, Color border) {
            this.arc = arc;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            RoundRectangle2D shape = new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(fill);
            g2.fill(shape);
            g2.setColor(border);
            g2.draw(shape);
            g2.dispose();
        }
    }
 
    /** Soft blurred colour blobs built from the palette (hero banner). */
    private static class BrandPanel extends JPanel {
        BrandPanel() {
            setOpaque(false);
        }
 
        private static void blob(Graphics2D g2, Shape shape, float cx, float cy,
                float radius, Color c, int alpha) {
            Color[] colors = {
                new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha),
                new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)
            };
            g2.setPaint(new RadialGradientPaint(new Point2D.Float(cx, cy), radius,
                    new float[] { 0f, 1f }, colors));
            g2.fill(shape);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, 26, 26);
 
            g2.setPaint(new GradientPaint(0, 0, new Color(249, 246, 251),
                    0, h, new Color(214, 228, 236)));
            g2.fill(shape);
 
            float r = Math.max(w * 0.30f, h * 1.2f);
            blob(g2, shape, w * 0.90f, h * 0.20f, r, BLUE, 110);
            blob(g2, shape, w * 0.55f, h * 0.90f, r, PURPLE, 120);
            blob(g2, shape, w * 0.75f, h * 0.80f, r, VIOLET, 150);
            blob(g2, shape, w * 0.98f, h * 1.00f, r * 0.9f, LIGHT_BLUE, 200);
            g2.dispose();
        }
    }
 
    /** Placeholder photo: accent colour with soft blobs, rounded. */
    private static class PhotoPanel extends JPanel {
        private final Color accent;
 
        PhotoPanel(Color accent) {
            this.accent = accent;
            setOpaque(false);
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, 16, 16);
            g2.setColor(accent);
            g2.fill(shape);
 
            Color[] glow = {
                new Color(255, 255, 255, 90), new Color(255, 255, 255, 0)
            };
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.8f, h * 0.2f), w * 0.6f,
                    new float[] { 0f, 1f }, glow));
            g2.fill(shape);
 
            g2.setColor(WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            FontMetrics fm = g2.getFontMetrics();
            String s = "Photo";
            g2.drawString(s, (w - fm.stringWidth(s)) / 2,
                    (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
 
    /** Rounded input container wrapping a text field. */
    private static class InputBox extends JPanel {
        private boolean focused;
 
        InputBox(JTextComponent field, JComponent trailing) {
            super(new BorderLayout(8, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(0, 14, 0, 10));
 
            field.setOpaque(false);
            field.setBorder(null);
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setForeground(BURGUNDY);
            field.setCaretColor(BURGUNDY);
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }
 
                @Override
                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
 
            add(field, BorderLayout.CENTER);
            if (trailing != null) {
                add(trailing, BorderLayout.EAST);
            }
            setPreferredSize(new Dimension(300, 48));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            RoundRectangle2D shape = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(FIELD_BG);
            g2.fill(shape);
            g2.setStroke(new BasicStroke(focused ? 1.6f : 1f));
            g2.setColor(focused ? VIOLET : FIELD_BORDER);
            g2.draw(shape);
            g2.dispose();
        }
    }
 
    /** Rounded container around a combo box so it matches the input fields. */
    private static class SelectBox extends JPanel {
        SelectBox(JComboBox<String> combo, int width) {
            super(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 10, 0, 6));
            combo.setBorder(null);
            combo.setBackground(FIELD_BG);
            combo.setForeground(BURGUNDY);
            combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
            combo.setFocusable(false);
            combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            add(combo, BorderLayout.CENTER);
            setPreferredSize(new Dimension(width, 48));
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            RoundRectangle2D shape = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(FIELD_BG);
            g2.fill(shape);
            g2.setColor(FIELD_BORDER);
            g2.draw(shape);
            g2.dispose();
        }
    }
 
    private static class HintTextField extends JTextField {
        private final String hint;
 
        HintTextField(String hint) {
            this.hint = hint;
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintHint(this, g, hint);
        }
    }
 
    /** Self-painted rounded button so colours match on every look-and-feel. */
    private static class RoundedButton extends JButton {
        private Color fill = WHITE;
        private Color hoverFill = null;
        private Color textColor = BURGUNDY;
        private Color borderColor = null;
        private final int arc = 16;
 
        RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
 
        void setColors(Color fill, Color hoverFill, Color textColor, Color borderColor) {
            this.fill = fill;
            this.hoverFill = hoverFill;
            this.textColor = textColor;
            this.borderColor = borderColor;
            repaint();
        }
 
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
 
            Color f = (getModel().isRollover() && hoverFill != null) ? hoverFill : fill;
            g2.setColor(f);
            g2.fillRoundRect(0, 0, w, h, arc, arc);
 
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
            }
 
            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            g2.drawString(text, (w - fm.stringWidth(text)) / 2,
                    (h - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use the default Swing appearance.
            }
            new Renterpage().setVisible(true);
        });
    }
}
 
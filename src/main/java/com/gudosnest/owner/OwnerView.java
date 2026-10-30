package com.gudosnest.owner;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.GridBagLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.gudosnest.model.PropertyCard;
import com.gudosnest.model.User;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.Toast;
import com.gudosnest.ui.UIHelpers;
import com.gudosnest.ui.UIHelpers.NavPill;

/**
 * OWNER PORTAL - a panel shown inside AuthFrame (not its own window).
 * Gradient sidebar + nav pills on the left, top bar + card content on the right.
 *
 * Focus is on Properties and Bookings; the other nav sections are simple
 * placeholders (PlaceholderPanel).
 */
public class OwnerView extends JPanel {

    private final User user;
    private final Runnable onSignOut;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel headerTitle = new JLabel("Dashboard");
    private final JLabel headerSubtitle = new JLabel(
            "Welcome back - here is the current state of your properties.");
    private final JLabel crumbCurrent = new JLabel("Dashboard");
    private JPanel pageHeader;

    private final ArrayList<NavPill> navPills = new ArrayList<>();

    // Panels (created once, refreshed as needed).
    private final PlaceholderPanel dashboard = new PlaceholderPanel(
            "Dashboard", "A quick overview of your properties is coming soon.");
    private final PropertiesPanel properties;
    private final UnitsPanel units;
    private final BookingsPanel bookings;
    private final PlaceholderPanel payments = new PlaceholderPanel(
            "Payments", "Payment tracking is coming soon.");
    private final PlaceholderPanel finances = new PlaceholderPanel(
            "Finances", "Income and expense reports are coming soon.");
    private final PlaceholderPanel messages = new PlaceholderPanel(
            "Messages", "Conversations with renters are coming soon.");

    public OwnerView(User user, Runnable onSignOut) {
        this.user = user;
        this.onSignOut = onSignOut;
        setLayout(new BorderLayout());
        setBackground(Theme.PAGE_BG);

        properties = new PropertiesPanel(this);
        units = new UnitsPanel(this);
        bookings = new BookingsPanel(this);

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(), BorderLayout.CENTER);

        content.add(dashboard, "dashboard");
        content.add(properties, "properties");
        content.add(units, "units");      // sub-view of properties
        content.add(bookings, "bookings");
        content.add(finances, "ledger");
        content.add(messages, "messages");
        content.add(payments, "payments");

        showSection("dashboard");
    }

    // ------------------------------------------------------------------
    // Sidebar (burgundy -> purple gradient)
    // ------------------------------------------------------------------
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setPaint(new GradientPaint(0, 0, Theme.BURGUNDY,
                        0, getHeight(), Theme.PURPLE));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(262, 0));
        sidebar.setBorder(new EmptyBorder(24, 16, 16, 16));

        sidebar.add(left(brandRow()));
        sidebar.add(Box.createVerticalStrut(22));
        sidebar.add(left(workspaceCard()));
        sidebar.add(Box.createVerticalStrut(22));

        JLabel section = new JLabel("WORKSPACE");
        section.setFont(Theme.SECONDARY_BOLD);
        section.setForeground(Theme.LIGHT_BLUE);
        section.setBorder(new EmptyBorder(0, 6, 0, 0));
        sidebar.add(left(section));
        sidebar.add(Box.createVerticalStrut(8));

        String[][] nav = {
            {"Dashboard", "dashboard"},
            {"Properties", "properties"},
            {"Bookings", "bookings"},
            {"Payments", "payments"},
            {"Finances", "ledger"},
            {"Messages", "messages"}
        };
        for (String[] n : nav) {
            NavPill pill = UIHelpers.navButton(n[0], n[1]);
            pill.setActionCommand(n[1]);
            pill.addActionListener(e -> showSection(n[1]));
            navPills.add(pill);
            sidebar.add(left(pill));
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(left(divider()));
        sidebar.add(left(userRow()));
        return sidebar;
    }

    // ---- small helpers for the sidebar ----

    /** Left-aligns a component so BoxLayout lines everything up. */
    private static <T extends JComponent> T left(T c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    /** Stops a component from stretching taller than it needs to. */
    private static <T extends JComponent> T capHeight(T c) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
        return c;
    }

    /** Logo icon (three bars) + "GudosNest". */
    private JComponent brandRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JComponent icon = new JComponent() {
            { setPreferredSize(new Dimension(34, 34)); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.WHITE);
                g.fillRoundRect(2, 14, 7, 18, 4, 4);
                g.fillRoundRect(13, 4, 7, 28, 4, 4);
                g.fillRoundRect(24, 10, 7, 22, 4, 4);
                g.dispose();
            }
        };

        JLabel name = new JLabel("GudosNest");
        name.setFont(Theme.LOGO);
        name.setForeground(Theme.WHITE);
        row.add(icon);
        row.add(name);
        return capHeight(row);
    }

    /** Rounded translucent card at the top: avatar, name, "Owner workspace". */
    private JComponent workspaceCard() {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.withAlpha(Theme.WHITE, 25));
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g.setColor(Theme.withAlpha(Theme.WHITE, 45));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));
        card.add(avatar(initialsOf(user.getFullName()), false), BorderLayout.WEST);
        card.add(identity("Owner workspace"), BorderLayout.CENTER);
        card.add(chevron(), BorderLayout.EAST);
        return capHeight(card);
    }

    /** Bottom row: avatar, name, role, and a log out icon button. */
    private JComponent userRow() {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(14, 4, 0, 4));
        row.add(avatar(initialsOf(user.getFullName()), true), BorderLayout.WEST);
        row.add(identity("Property owner"), BorderLayout.CENTER);
        row.add(logoutButton(), BorderLayout.EAST);
        return capHeight(row);
    }

    /** Name (white) over a smaller subtitle (light blue). */
    private JComponent identity(String subtitle) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(user.getFullName());
        name.setFont(Theme.BUTTON);
        name.setForeground(Theme.WHITE);
        JLabel sub = new JLabel(subtitle);
        sub.setFont(Theme.SECONDARY);
        sub.setForeground(Theme.LIGHT_BLUE);
        p.add(Box.createVerticalGlue());
        p.add(name);
        p.add(sub);
        p.add(Box.createVerticalGlue());
        return p;
    }

    /** Gradient avatar with initials. round = circle, otherwise rounded square. */
    private JComponent avatar(String initials, boolean round) {
        return new JComponent() {
            { setPreferredSize(new Dimension(40, 40)); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.LIGHT_BLUE,
                        getWidth(), getHeight(), Theme.VIOLET));
                if (round) g.fillOval(0, 0, 40, 40);
                else       g.fillRoundRect(0, 0, 40, 40, 12, 12);
                g.setFont(Theme.BUTTON);
                g.setColor(Theme.WHITE);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(initials,
                        (40 - fm.stringWidth(initials)) / 2,
                        (40 + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
        };
    }

    /** Small ">" arrow on the workspace card. */
    private JComponent chevron() {
        return new JComponent() {
            { setPreferredSize(new Dimension(14, 40)); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.LIGHT_BLUE);
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(4, 14, 10, 20);
                g.drawLine(10, 20, 4, 26);
                g.dispose();
            }
        };
    }

    /** Thin line above the user row. */
    private JComponent divider() {
        JComponent line = new JComponent() {
            { setPreferredSize(new Dimension(1, 1)); }
            @Override protected void paintComponent(Graphics g0) {
                g0.setColor(Theme.withAlpha(Theme.WHITE, 40));
                g0.fillRect(0, 0, getWidth(), 1);
            }
        };
        return capHeight(line);
    }

    /** Square icon button (arrow leaving a door) that logs out. */
    private JComponent logoutButton() {
        return new JComponent() {
            private boolean hover;
            {
                setPreferredSize(new Dimension(40, 40));
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                setToolTipText("Log out");
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { onSignOut.run(); }
                });
            }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                if (hover) {
                    g.setColor(Theme.withAlpha(Theme.WHITE, 25));
                    g.fillRoundRect(0, 0, 39, 39, 12, 12);
                }
                g.setColor(Theme.withAlpha(Theme.WHITE, 70));
                g.drawRoundRect(0, 0, 39, 39, 12, 12);
                g.setColor(Theme.WHITE);
                g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(12, 20, 24, 20);     // arrow shaft
                g.drawLine(20, 15, 25, 20);     // arrow head
                g.drawLine(25, 20, 20, 25);
                g.drawLine(28, 12, 28, 28);     // door edge
                g.dispose();
            }
        };
    }

    /** "Juan Dela Cruz" -> "JD". */
    private static String initialsOf(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
    }

    // ------------------------------------------------------------------
    // Main area: top bar + page header + card content
    // ------------------------------------------------------------------
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.PAGE_BG);

        headerTitle.setFont(Theme.PAGE_TITLE);
        headerTitle.setForeground(Theme.BURGUNDY);
        headerSubtitle.setFont(Theme.BODY);
        headerSubtitle.setForeground(Theme.MUTED);
        pageHeader = new JPanel();
        pageHeader.setOpaque(false);
        pageHeader.setLayout(new BoxLayout(pageHeader, BoxLayout.Y_AXIS));
        pageHeader.setBorder(new EmptyBorder(24, 26, 8, 26));
        headerTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pageHeader.add(headerTitle);
        pageHeader.add(headerSubtitle);

        JPanel north = new JPanel(new BorderLayout());
        north.setOpaque(false);
        north.add(buildTopBar(), BorderLayout.NORTH);
        north.add(pageHeader, BorderLayout.CENTER);
        main.add(north, BorderLayout.NORTH);

        content.setOpaque(false);
        content.setBorder(new EmptyBorder(8, 26, 18, 26));
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    /** White bar: breadcrumb on the left, search + bell + avatar on the right. */
    private JComponent buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                g0.setColor(Theme.WHITE);
                g0.fillRect(0, 0, getWidth(), getHeight());
                g0.setColor(Theme.HAIRLINE);
                g0.fillRect(0, getHeight() - 1, getWidth(), 1);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(0, 26, 0, 26));
        bar.setPreferredSize(new Dimension(0, 64));

        // Breadcrumb: GudosNest > Properties
        JLabel root = new JLabel("GudosNest");
        root.setFont(Theme.BODY);
        root.setForeground(Theme.MUTED);
        JLabel sep = new JLabel(">");
        sep.setFont(Theme.BODY);
        sep.setForeground(Theme.HINT);
        crumbCurrent.setFont(Theme.BUTTON);
        crumbCurrent.setForeground(Theme.BURGUNDY);
        JPanel crumbs = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        crumbs.setOpaque(false);
        crumbs.add(root);
        crumbs.add(sep);
        crumbs.add(crumbCurrent);
        JPanel crumbWrap = new JPanel(new GridBagLayout());
        crumbWrap.setOpaque(false);
        crumbWrap.add(crumbs);

        // Search box with a placeholder
        JTextField search = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(Theme.HINT);
                    g.setFont(Theme.INPUT);
                    FontMetrics fm = g.getFontMetrics();
                    g.drawString("Search anything...", 10,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        UIHelpers.field(search);
        search.setPreferredSize(new Dimension(260, 38));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(search);
        right.add(bellButton());
        right.add(avatar(initialsOf(user.getFullName()), true));
        JPanel rightWrap = new JPanel(new GridBagLayout());
        rightWrap.setOpaque(false);
        rightWrap.add(right);

        bar.add(crumbWrap, BorderLayout.WEST);
        bar.add(rightWrap, BorderLayout.EAST);
        return bar;
    }

    /** Square bordered bell icon (not wired to anything yet). */
    private JComponent bellButton() {
        return new JComponent() {
            { setPreferredSize(new Dimension(38, 38)); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.HAIRLINE);
                g.drawRoundRect(0, 0, 37, 37, 12, 12);
                g.setColor(Theme.BURGUNDY);
                g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawArc(12, 10, 14, 14, 0, 180);
                g.drawLine(12, 17, 10, 25);
                g.drawLine(26, 17, 28, 25);
                g.drawLine(10, 25, 28, 25);
                g.drawLine(17, 28, 21, 28);
                g.dispose();
            }
        };
    }

    // ------------------------------------------------------------------
    // Navigation + drill-down context
    // ------------------------------------------------------------------

    /** Shows a top-level nav section and updates the header. */
    void showSection(String key) {
        String title;
        String subtitle;
        switch (key) {
            case "properties" -> { title = "Properties"; subtitle = "Your portfolio at a glance. Select a property to manage its units."; }
            case "ledger"     -> { title = "Finances"; subtitle = "Income and expense reports are coming soon."; }
            case "bookings"   -> { title = "Bookings"; subtitle = "Review reservations, requests, and guest details."; }
            case "messages"   -> { title = "Messages"; subtitle = "Conversations with renters are coming soon."; }
            case "payments"   -> { title = "Payments"; subtitle = "Payment tracking is coming soon."; }
            default           -> { title = "Dashboard"; subtitle = "Welcome back - here is the current state of your properties."; }
        }
        setHeaders(title, subtitle);
        crumbCurrent.setText(title);
        pageHeader.setVisible(!"properties".equals(key));
        setActiveNav(key);
        if ("properties".equals(key)) properties.refresh();
        if ("bookings".equals(key))   bookings.refresh();
        cards.show(content, key);
    }

    /** Opens the units cards for one property (drill-down from properties). */
    void openUnits(PropertyCard card) {
        units.load(card);
        setHeaders("Units - " + card.getPropertyName(),
                "Manage the units in this property.");
        crumbCurrent.setText("Units");
        pageHeader.setVisible(false);
        setActiveNav("properties");
        cards.show(content, "units");
    }

    /** Returns from a drill-down to the properties grid. */
    void backToProperties() {
        showSection("properties");
    }

    private void setHeaders(String title, String subtitle) {
        headerTitle.setText(title);
        headerSubtitle.setText(subtitle);
    }

    private void setActiveNav(String key) {
        for (NavPill pill : navPills) {
            pill.setActive(key.equals(pill.getActionCommand()));
        }
    }

    /** Shows a toast anchored to this view. */
    void toast(String message) {
        Toast.show(this, message);
    }

    void toastError(String message) {
        Toast.showError(this, message);
    }
}
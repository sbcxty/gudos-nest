package com.gudosnest.ui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.gudosnest.db.Seed;
import com.gudosnest.model.User;
import com.gudosnest.ui.OwnerHomePanels.*;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * OWNER HOME PAGE - the deliverable focused on by this member.
 * Shell only: gradient hero sidebar (AuthFrame concept), nav pills with
 * active state, user card, and card content area. Each screen lives in
 * its own file under ui/OwnerHomePanels/.
 */
public class OwnerHomeFrame extends JFrame {

    private final User user;
    private final JPanel cardsPanel = new JPanel(new CardLayout());
    private final CardLayout cards = (CardLayout) cardsPanel.getLayout();
    private final JLabel eyebrowLabel = eyebrow("O W N E R   P O R T A L");
    private final JLabel headerTitle = new JLabel("Dashboard");
    private final JLabel headerSubtitle = new JLabel(
            "Welcome back - here is the current state of your properties.");
    private final ArrayList<JButton> navButtons = new ArrayList<>();

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
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMain(), BorderLayout.CENTER);
        Seed.run();
    }

    // ------------------------------------------------------------------
    // Sidebar (gradient hero panel, mirrors the AuthFrame concept)
    // ------------------------------------------------------------------
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.LAVENDER, getWidth(), getHeight(), Theme.ROSE));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(238, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER),
                new EmptyBorder(26, 14, 18, 14)));

        JLabel logo = new JLabel("Gudo's Nest");
        logo.setFont(Theme.font("head-bold", 22));
        logo.setForeground(Theme.PRIMARY);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        JLabel tagline = new JLabel("Y O U R   S P A C E .   Y O U R   P L A C E .");
        tagline.setFont(Theme.font("body-bold", 9));
        tagline.setForeground(Theme.TEXT_MUT);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(tagline);
        sidebar.add(Box.createVerticalStrut(26));

        String[][] nav = {
            {"Dashboard", "dashboard"},
            {"Properties", "properties"},
            {"Bookings", "bookings"},
            {"Payments & Income", "income"},
            {"Inventory", "inventory"},
            {"Maintenance", "maintenance"}
        };
        for (String[] n : nav) {
            JButton b = navButton(n[0]);
            b.setActionCommand(n[1]);
            b.addActionListener(e -> {
                for (JButton other : navButtons) {
                    if (other instanceof NavPill) ((NavPill) other).setActive(false);
                }
                if (b instanceof NavPill) ((NavPill) b).setActive(true);
                showSection(n[1], n[0]);
            });
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            if (b instanceof NavPill) ((NavPill) b).setActive(n[1].equals("dashboard"));
            navButtons.add(b);
            sidebar.add(b);
            sidebar.add(Box.createVerticalStrut(6));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logout = ghostButton("Log out");
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> {
            new AuthFrame().setVisible(true);
            dispose();
        });
        sidebar.add(logout);

        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(userCard());
        return sidebar;
    }

    /** Avatar chip + account identity, styled like the auth concept's card. */
    private JComponent userCard() {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override public Dimension getMaximumSize() { return getPreferredSize(); }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 10, 4, 10));
        final String initials = initialsOf(user.getFullName());
        JComponent avatar = new JComponent() {
            {
                setPreferredSize(new Dimension(40, 40));
                setMaximumSize(new Dimension(40, 40));
            }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight());
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g.setColor(Theme.PRIMARY);
                g.fillOval(x, y, s, s);
                g.setFont(Theme.font("body-bold", 16));
                g.setColor(java.awt.Color.WHITE);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(initials,
                    x + (s - fm.stringWidth(initials)) / 2,
                    y + (s + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
        };

        JPanel labels = new JPanel();
        labels.setOpaque(false);
        labels.setLayout(new BoxLayout(labels, BoxLayout.Y_AXIS));
        JLabel name = new JLabel(user.getFullName());
        name.setFont(Theme.font("body-bold", 13));
        name.setForeground(Theme.PRIMARY);
        String roleText;
        if (user.isOwner() && user.isRenter()) roleText = "Owner & renter";
        else if (user.isOwner()) roleText = "Property owner";
        else roleText = "Renter";
        JLabel role = new JLabel(roleText);
        role.setFont(Theme.font("body", 11));
        role.setForeground(Theme.TEXT_MUT);
        labels.add(name);
        labels.add(role);

        card.add(avatar, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
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
    // Main area: eyebrow + serif header + card content
    // ------------------------------------------------------------------
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(Theme.BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 26, 10, 26));
        headerTitle.setFont(Theme.font("head-bold", 26));
        headerTitle.setForeground(Theme.TEXT);
        headerSubtitle.setFont(Theme.font("body", 13));
        headerSubtitle.setForeground(Theme.TEXT_MUT);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(eyebrowLabel);
        titles.add(headerTitle);
        titles.add(headerSubtitle);
        header.add(titles, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(new EmptyBorder(8, 26, 18, 26));

        cardsPanel.add(new DashboardPanel(), "dashboard");
        cardsPanel.add(new PropertiesPanel(), "properties");
        cardsPanel.add(new BookingsPanel(), "bookings");
        cardsPanel.add(new IncomePanel(), "income");
        cardsPanel.add(new InventoryPanel(), "inventory");
        cardsPanel.add(new MaintenancePanel(), "maintenance");

        main.add(cardsPanel, BorderLayout.CENTER);
        return main;
    }

    /** Switches the visible screen and updates header text. */
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
        cards.show(cardsPanel, key);
    }
}

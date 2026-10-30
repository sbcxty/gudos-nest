package com.gudosnest.renter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.gudosnest.dao.BookingDAO;
import com.gudosnest.dao.UnitDAO;
import com.gudosnest.model.Booking;
import com.gudosnest.model.UnitListing;
import com.gudosnest.model.User;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.Toast;
import com.gudosnest.ui.UIHelpers;
import com.gudosnest.ui.UIHelpers.NavPill;
import com.gudosnest.ui.WrapLayout;

import static com.gudosnest.ui.UIHelpers.*;

/** RENTER PORTAL - a panel shown inside AuthFrame (not its own window). */
public class RenterView extends JPanel {

    private final User user;
    private final Runnable onSignOut;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel headerTitle = new JLabel("Listings");
    private final JLabel headerSubtitle = new JLabel(
            "Available units you can book.");
    private final ArrayList<NavPill> navPills = new ArrayList<>();

    private final UnitDAO unitDAO = new UnitDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final JPanel listingGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 16, 16));

    private static final String[] BOOK_COLS = {
            "#", "Property", "Unit", "Check-in", "Check-out", "Guests", "Status", "Total" };
    private final DefaultTableModel bookingModel = new DefaultTableModel(BOOK_COLS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable bookingTable = makeTable(bookingModel);

    public RenterView(User user, Runnable onSignOut) {
        this.user = user;
        this.onSignOut = onSignOut;
        setLayout(new BorderLayout());
        setBackground(Theme.PAGE_BG);

        add(buildSidebar(), BorderLayout.WEST);

        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.PAGE_BG);
        headerTitle.setFont(Theme.PAGE_TITLE);
        headerTitle.setForeground(Theme.BURGUNDY);
        headerSubtitle.setFont(Theme.BODY);
        headerSubtitle.setForeground(Theme.MUTED);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(headerTitle);
        titles.add(headerSubtitle);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(24, 26, 8, 26));
        header.add(titles, BorderLayout.WEST);
        main.add(header, BorderLayout.NORTH);

        content.setOpaque(false);
        content.setBorder(new EmptyBorder(8, 26, 18, 26));
        content.add(buildListings(), "listings");
        content.add(buildMyBookings(), "bookings");
        main.add(content, BorderLayout.CENTER);
        add(main, BorderLayout.CENTER);

        showSection("listings");
    }

    // ---- Sidebar --------------------------------------------------------
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.VIOLET,
                        0, getHeight(), Theme.PURPLE));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(238, 0));
        sidebar.setBorder(new EmptyBorder(26, 14, 18, 14));

        JLabel logo = new JLabel("Gudo's Nest");
        logo.setFont(Theme.LOGO);
        logo.setForeground(Theme.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);

        JLabel tagline = new JLabel("F I N D   Y O U R   N E S T.");
        tagline.setFont(Theme.SECONDARY_BOLD);
        tagline.setForeground(Theme.LIGHT_BLUE);
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(tagline);
        sidebar.add(Box.createVerticalStrut(26));

        String[][] nav = {
            {"Listings", "listings"},
            {"My bookings", "bookings"}
        };
        for (String[] n : nav) {
            NavPill pill = UIHelpers.navButton(n[0]);
            pill.setActionCommand(n[1]);
            pill.addActionListener(e -> showSection(n[1]));
            pill.setAlignmentX(Component.CENTER_ALIGNMENT);
            navPills.add(pill);
            sidebar.add(pill);
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton logout = UIHelpers.ghostButton("Log out");
        logout.setForeground(Theme.LIGHT_BLUE);
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> onSignOut.run());
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(userCard());
        return sidebar;
    }

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
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.LIGHT_BLUE,
                        getWidth(), getHeight(), Theme.BLUE));
                int s = Math.min(getWidth(), getHeight());
                int x = (getWidth() - s) / 2;
                int y = (getHeight() - s) / 2;
                g.fillOval(x, y, s, s);
                g.setFont(Theme.BUTTON);
                g.setColor(Theme.WHITE);
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
        name.setFont(Theme.BUTTON);
        name.setForeground(Theme.WHITE);
        JLabel role = new JLabel("Renter");
        role.setFont(Theme.SECONDARY);
        role.setForeground(Theme.LIGHT_BLUE);
        labels.add(name);
        labels.add(role);
        card.add(avatar, BorderLayout.WEST);
        card.add(labels, BorderLayout.CENTER);
        return card;
    }

    private static String initialsOf(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
    }

    // ---- Listings -------------------------------------------------------
    private java.awt.Component buildListings() {
        listingGrid.setOpaque(false);
        JScrollPane scroll = new JScrollPane(listingGrid);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private void refreshListings() {
        listingGrid.removeAll();
        ArrayList<UnitListing> listings = unitDAO.getAvailableListings();
        if (listings.isEmpty()) {
            listingGrid.add(new JLabel("No units available right now."));
        } else {
            for (UnitListing listing : listings) {
                listingGrid.add(new ListingCardView(listing));
            }
        }
        listingGrid.revalidate();
        listingGrid.repaint();
    }

    private class ListingCardView extends JPanel {
        private final UnitListing listing;
        private boolean hover;

        ListingCardView(UnitListing listing) {
            this.listing = listing;
            setOpaque(false);
            setPreferredSize(new Dimension(280, 176));
            setMaximumSize(new Dimension(280, 176));
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(18, 20, 16, 20));

            JButton book = primaryButton("Book");
            book.setPreferredSize(new Dimension(90, 34));
            book.setMaximumSize(new Dimension(90, 34));
            book.addActionListener(e ->
                    new RenterBookingDialog(RenterView.this, user.getUserId(),
                            listing, RenterView.this::refreshMyBookings).setVisible(true));

            JPanel foot = new JPanel(new BorderLayout());
            foot.setOpaque(false);
            JLabel rate = label(formatMoney(listing.getDailyRate()) + " / night",
                    Theme.PURPLE, 13);
            foot.add(rate, BorderLayout.WEST);
            foot.add(book, BorderLayout.EAST);
            add(foot, BorderLayout.SOUTH);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            if (hover) {
                for (int i = 0; i < 6; i++) {
                    g.setColor(Theme.withAlpha(Theme.BURGUNDY, 8));
                    g.fillRoundRect(3 - i, 5 - i, w - 6 + 2 * i, h - 6 + 2 * i,
                            20 + 2 * i, 20 + 2 * i);
                }
            }
            g.setColor(Theme.WHITE);
            g.fillRoundRect(0, 0, w, h - 6, 20, 20);
            g.setColor(hover ? Theme.LIGHT_BLUE : Theme.HAIRLINE);
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(0, 0, w - 1, h - 7, 20, 20);

            g.setColor(Theme.VIOLET);
            g.fillRoundRect(20, 20, 34, 6, 6, 6);
            g.setColor(Theme.BURGUNDY);
            g.setFont(Theme.CARD_HEADING);
            g.drawString(listing.getUnitName(), 20, 48);
            g.setColor(Theme.MUTED);
            g.setFont(Theme.SECONDARY);
            g.drawString(listing.getPropertyName(), 20, 70);
            g.drawString(listing.getCapacity() + " guests", 20, 90);
            g.dispose();
        }
    }

    // ---- My bookings ----------------------------------------------------
    private java.awt.Component buildMyBookings() {
        bookingTable.getColumnModel().getColumn(6).setCellRenderer(chipRenderer());
        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.setBorder(null);
        return scroll;
    }

    private void refreshMyBookings() {
        ArrayList<Booking> bookings = bookingDAO.getForRenter(user.getUserId());
        bookingModel.setRowCount(0);
        for (Booking b : bookings) {
            bookingModel.addRow(new String[]{
                String.valueOf(b.getBookingId()),
                b.getPropertyName(),
                b.getUnitName(),
                b.getCheckIn(),
                b.getCheckOut(),
                String.valueOf(b.getNumberOfRenters()),
                b.getBookingStatus().toUpperCase(),
                formatMoney(b.getTotalAmount())
            });
        }
        // Also refresh listings in case a unit freed up.
        refreshListings();
    }

    // ---- Navigation -----------------------------------------------------
    private void showSection(String key) {
        if ("listings".equals(key)) {
            headerTitle.setText("Listings");
            headerSubtitle.setText("Available units you can book.");
            refreshListings();
        } else {
            headerTitle.setText("My bookings");
            headerSubtitle.setText("Track your booking requests and their status.");
            refreshMyBookings();
        }
        for (NavPill pill : navPills) {
            pill.setActive(key.equals(pill.getActionCommand()));
        }
        cards.show(content, key);
    }
}

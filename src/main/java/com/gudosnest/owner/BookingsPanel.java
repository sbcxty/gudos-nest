package com.gudosnest.owner;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.gudosnest.dao.BookingDAO;
import com.gudosnest.model.Booking;
import com.gudosnest.ui.HintTextField;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Bookings list styled like the design: status filter tabs, a search box,
 * and a table of guest (with avatar), booking ref, property, dates, amount,
 * and a status pill. Double-click a row (or its chevron) to open details.
 */
public class BookingsPanel extends JPanel {

    private static final String[] COLS = {
            "GUEST", "BOOKING", "PROPERTY", "DATES", "AMOUNT", "STATUS", "" };

    private final OwnerView owner;
    private final BookingDAO bookingDAO = new BookingDAO();
    private final DefaultTableModel model = new DefaultTableModel(COLS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = makeTable(model);

    private final JTextField search = new HintTextField("Search bookings...");
    private ArrayList<Booking> all = new ArrayList<>();
    private ArrayList<Booking> visible = new ArrayList<>();

    private final FilterTab[] tabs = {
        new FilterTab("all", "All"),
        new FilterTab("pending", "Pending"),
        new FilterTab("upcoming", "Upcoming"),
        new FilterTab("completed", "Completed"),
        new FilterTab("cancelled", "Cancelled"),
    };
    private String activeTab = "all";
    private boolean newestFirst = true;

    public BookingsPanel(OwnerView owner) {
        this.owner = owner;
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));
        setBackground(Theme.PAGE_BG);

        styleTable();
        wireTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBackground(Theme.WHITE);
        scroll.getViewport().setBackground(Theme.WHITE);

        // Inner content is inset so the card's rounded corners stay clean.
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(6, 16, 14, 16));
        content.add(buildToolbar(), BorderLayout.NORTH);
        content.add(scroll, BorderLayout.CENTER);

        JPanel card = cardPanel(new BorderLayout(), 0, 18);
        card.add(content, BorderLayout.CENTER);

        add(buildTabs(), BorderLayout.NORTH);
        add(card, BorderLayout.CENTER);
        refresh();
    }

    // ---- Toolbar: search on the left, Date / Filters on the right -------
    private JPanel buildToolbar() {
        search.setFont(Theme.INPUT);
        search.setPreferredSize(new Dimension(280, 40));
        search.setMaximumSize(new Dimension(280, 40));
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { applyFilter(); }
            @Override public void removeUpdate(DocumentEvent e)  { applyFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applyFilter(); }
        });

        JButton date = outlinedButton("Date");
        date.addActionListener(e -> { newestFirst = !newestFirst; applyFilter(); });
        JButton filters = outlinedButton("Filters");
        filters.addActionListener(e -> {
            search.setText("");
            activeTab = "all";
            updateTabs();
            applyFilter();
        });

        JPanel bar = new JPanel(new BorderLayout(12, 0));
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(8, 0, 14, 0));
        bar.add(search, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(date);
        right.add(filters);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ---- Filter tabs ----------------------------------------------------
    private JPanel buildTabs() {
        JPanel strip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        strip.setOpaque(false);
        strip.setBorder(new EmptyBorder(0, 4, 0, 4));
        for (FilterTab t : tabs) {
            t.addActionListener(e -> {
                activeTab = t.key;
                updateTabs();
                applyFilter();
            });
            strip.add(t);
        }
        return strip;
    }

    private void updateTabs() {
        for (FilterTab t : tabs) {
            t.update(countFor(t.key), t.key.equals(activeTab));
        }
    }

    private int countFor(String key) {
        int n = 0;
        for (Booking b : all) if (matchesTab(b, key)) n++;
        return n;
    }

    private static boolean matchesTab(Booking b, String key) {
        return switch (key) {
            case "pending"   -> "pending".equals(b.getBookingStatus());
            case "upcoming"  -> "approved".equals(b.getBookingStatus());
            case "completed" -> "completed".equals(b.getBookingStatus());
            case "cancelled" -> "cancelled".equals(b.getBookingStatus());
            default          -> true; // "all"
        };
    }

    // ---- Table styling (white rows separated by hairlines) --------------
    private void styleTable() {
        table.setRowHeight(54);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setBackground(Theme.HAIRLINE);
        table.setFillsViewportHeight(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowSelectionAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);

        // Plain cells: white, burgundy text (replaces the shared zebra look).
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component x = super.getTableCellRendererComponent(jt, v, sel, foc, r, c);
                x.setFont(Theme.BODY);
                x.setForeground(Theme.BURGUNDY);
                ((JComponent) x).setOpaque(true);
                x.setBackground(sel ? Theme.HOVER_BG : Theme.WHITE);
                return x;
            }
        });
        table.getColumnModel().getColumn(0).setCellRenderer(guestRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(dotChipRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(chevronRenderer());

        int[] widths = { 210, 100, 190, 120, 110, 140, 36 };
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
        }
    }

    private DefaultTableCellRenderer chevronRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(jt, "›", sel, foc, r, c);
                l.setOpaque(true);
                l.setBackground(sel ? Theme.HOVER_BG : Theme.WHITE);
                l.setForeground(Theme.HINT);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setFont(Theme.f(java.awt.Font.PLAIN, 20));
                return l;
            }
        };
    }

    private void wireTable() {
        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row < 0) return;
                boolean chevron = table.columnAtPoint(e.getPoint()) == 6;
                if (e.getClickCount() == 2 || chevron) {
                    table.setRowSelectionInterval(row, row);
                    openSelected();
                }
            }
        });
    }

    // ---- Data -----------------------------------------------------------
    public void refresh() {
        all = bookingDAO.getAll();
        updateTabs();
        applyFilter();
    }

    private void applyFilter() {
        String q = search.getText().trim().toLowerCase();
        visible = new ArrayList<>();
        for (Booking b : all) {
            if (!matchesTab(b, activeTab)) continue;
            if (!q.isEmpty() && !matchesSearch(b, q)) continue;
            visible.add(b);
        }
        visible.sort((a, b) -> newestFirst
                ? Integer.compare(b.getBookingId(), a.getBookingId())
                : Integer.compare(a.getBookingId(), b.getBookingId()));

        model.setRowCount(0);
        for (Booking b : visible) {
            String property = b.getUnitName() == null || b.getUnitName().isBlank()
                    ? b.getPropertyName()
                    : b.getPropertyName() + " · " + b.getUnitName();
            model.addRow(new String[]{
                b.getRenterName(),
                bookingRef(b.getBookingId()),
                property,
                compactRange(b.getCheckIn(), b.getCheckOut()),
                formatMoney(b.getTotalAmount()),
                titleCase(b.getBookingStatus()),
                "›"
            });
        }
    }

    private static boolean matchesSearch(Booking b, String q) {
        return b.getRenterName().toLowerCase().contains(q)
                || b.getPropertyName().toLowerCase().contains(q)
                || (b.getUnitName() != null && b.getUnitName().toLowerCase().contains(q))
                || bookingRef(b.getBookingId()).toLowerCase().contains(q);
    }

    private void openSelected() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visible.size()) return;
        new BookingDialog(owner, visible.get(row), this::refresh).setVisible(true);
    }

    // ---- One filter tab (label + count badge + active underline) --------
    private static class FilterTab extends JButton {
        private final String key;
        private final String label;
        private int count;
        private boolean active;

        FilterTab(String key, String label) {
            this.key = key;
            this.label = label;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void update(int count, boolean active) {
            this.count = count;
            this.active = active;
            FontMetrics fm = getFontMetrics(active ? Theme.BUTTON : Theme.BODY);
            int labelW = fm.stringWidth(label);
            String ct = String.valueOf(count);
            int badgeW = fm.stringWidth(ct) + 16;
            setPreferredSize(new Dimension(labelW + 8 + badgeW + 20, 40));
            revalidate();
            repaint();
        }

        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setFont(active ? Theme.BUTTON : Theme.BODY);
            FontMetrics fm = g.getFontMetrics();
            String ct = String.valueOf(count);
            int badgeW = fm.stringWidth(ct) + 16;
            int x = 10;
            int cy = getHeight() / 2;

            g.setColor(active ? Theme.BURGUNDY : Theme.MUTED);
            g.drawString(label, x, cy + fm.getAscent() / 2 - 1);

            int bx = x + fm.stringWidth(label) + 8;
            int bh = 22, by = cy - bh / 2;
            g.setColor(active ? Theme.withAlpha(Theme.PURPLE, 45) : Theme.HOVER_BG);
            g.fillRoundRect(bx, by, badgeW, bh, bh, bh);
            g.setColor(active ? Theme.PURPLE : Theme.MUTED);
            g.drawString(ct, bx + (badgeW - fm.stringWidth(ct)) / 2,
                    by + (bh + fm.getAscent() - fm.getDescent()) / 2);

            if (active) {
                g.setColor(Theme.BURGUNDY);
                g.fillRoundRect(x, getHeight() - 3, fm.stringWidth(label) + 8 + badgeW, 3, 3, 3);
            }
            g.dispose();
        }
    }
}

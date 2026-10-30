package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Shared Swing styling helpers for the whole app (buttons, cards, tables,
 * chips). Everything pulls its colours and fonts from Theme.
 */
public final class UIHelpers {

    /** Rounded white card panel with padding (default 24px corners). */
    public static JPanel cardPanel(LayoutManager lm, int pad) {
        return cardPanel(lm, pad, 24);
    }

    /** Rounded card panel with a customizable corner radius. */
    public static JPanel cardPanel(LayoutManager lm, int pad, int radius) {
        JPanel p = new JPanel(lm) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.WHITE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g.setColor(Theme.HAIRLINE);
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(pad, pad, pad, pad));
        return p;
    }

    /** Body text label. */
    public static JLabel label(String text, Color c, int size) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.f(java.awt.Font.PLAIN, size));
        l.setForeground(c);
        return l;
    }

    /** Bold heading label (e.g. card / panel titles). */
    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.CARD_HEADING);
        l.setForeground(Theme.BURGUNDY);
        return l;
    }

    /** Small-caps eyebrow label in purple. */
    public static JLabel eyebrow(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.SECONDARY_BOLD);
        l.setForeground(Theme.PURPLE);
        return l;
    }

    /** Filled burgundy button (primary action) with hover state. */
    public static JButton primaryButton(String text) {
        JButton b = new RoundedButton(text);
        ((RoundedButton) b).setColors(Theme.BURGUNDY, Theme.BURGUNDY_HOVER,
                Theme.WHITE, null);
        ((RoundedButton) b).setShadow(true);
        return b;
    }

    /** Ghost / text-only button (secondary actions) in purple. */
    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.SECONDARY_BOLD);
        b.setForeground(Theme.PURPLE);
        b.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Filled purple button (primary confirm, e.g. Approve). */
    public static JButton accentButton(String text) {
        JButton b = new RoundedButton(text);
        ((RoundedButton) b).setColors(Theme.PURPLE, Theme.VIOLET, Theme.WHITE, null);
        ((RoundedButton) b).setShadow(true);
        return b;
    }

    /** Filled red button (destructive, e.g. Reject). */
    public static JButton dangerButton(String text) {
        JButton b = new RoundedButton(text);
        ((RoundedButton) b).setColors(Theme.RED, Theme.BURGUNDY, Theme.WHITE, null);
        return b;
    }

    /** Outlined button on white (secondary, e.g. Mark as completed). */
    public static JButton outlinedButton(String text) {
        JButton b = new RoundedButton(text);
        ((RoundedButton) b).setColors(Theme.WHITE, Theme.HOVER_BG, Theme.PURPLE, Theme.HAIRLINE);
        return b;
    }

    /** Rounded sidebar navigation pill with active + hover states. */
    public static NavPill navButton(String text) {
        return new NavPill(text, null);
    }

    /** Same, with a small line icon (key = section name, e.g. "properties"). */
    public static NavPill navButton(String text, String icon) {
        return new NavPill(text, icon);
    }

    /** Sidebar nav pill: white text, solid white when active, soft wash on hover. */
    public static class NavPill extends JButton {
        private boolean hover = false;
        private boolean active = false;
        private final String icon;

        public NavPill(String text, String icon) {
            super(text, null);
            this.icon = icon;
            setOpaque(false);
            setFont(Theme.BODY);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(10, icon != null ? 46 : 14, 10, 14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }

        public void setActive(boolean on) { active = on; repaint(); }

        @Override protected void paintComponent(Graphics g0) {
            setForeground(active ? Theme.BURGUNDY : Theme.WHITE);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g.setColor(Theme.WHITE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            } else if (hover) {
                g.setColor(Theme.withAlpha(Theme.WHITE, 60));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            if (icon != null) {
                g.setColor(getForeground());
                g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.translate(16, (getHeight() - 18) / 2);
                drawIcon(g, icon);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /** Draws a simple 18x18 line icon at the current origin. */
    private static void drawIcon(Graphics2D g, String key) {
        switch (key) {
            case "dashboard" -> {
                g.drawRoundRect(1, 1, 6, 6, 2, 2);
                g.drawRoundRect(11, 1, 6, 6, 2, 2);
                g.drawRoundRect(1, 11, 6, 6, 2, 2);
                g.drawRoundRect(11, 11, 6, 6, 2, 2);
            }
            case "properties" -> {
                g.drawLine(1, 8, 9, 1);
                g.drawLine(9, 1, 17, 8);
                g.drawLine(3, 7, 3, 17);
                g.drawLine(15, 7, 15, 17);
                g.drawLine(3, 17, 15, 17);
                g.drawRect(7, 11, 4, 6);
            }
            case "bookings" -> {
                g.drawRoundRect(1, 3, 16, 14, 3, 3);
                g.drawLine(1, 8, 17, 8);
                g.drawLine(5, 1, 5, 5);
                g.drawLine(13, 1, 13, 5);
            }
            case "payments" -> {
                g.drawRoundRect(1, 3, 16, 13, 3, 3);
                g.drawLine(1, 8, 17, 8);
                g.drawLine(4, 12, 7, 12);
            }
            case "ledger" -> {
                g.drawLine(1, 17, 17, 17);
                g.drawLine(4, 17, 4, 10);
                g.drawLine(9, 17, 9, 3);
                g.drawLine(14, 17, 14, 7);
            }
            case "messages" -> {
                g.drawRoundRect(1, 2, 16, 11, 4, 4);
                g.drawLine(5, 13, 4, 17);
                g.drawLine(4, 17, 9, 13);
            }
            default -> { }
        }
    }

    /** Styled single-line text input. */
    public static JTextField field(JTextField f) {
        f.setFont(Theme.INPUT);
        f.setBackground(Theme.FIELD_BG);
        f.setForeground(Theme.BURGUNDY);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.HAIRLINE, 1, true),
            new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    /** Colored pill status chip, e.g. PENDING / APPROVED. */
    public static JComponent chip(String text, Color c) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.withAlpha(c, 38));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g.dispose();
                super.paintComponent(g0);
            }
        };
        l.setFont(Theme.SECONDARY_BOLD);
        l.setForeground(c);
        l.setBorder(new EmptyBorder(5, 12, 5, 12));
        l.setOpaque(false);
        return l;
    }

    /** Pill status chip with a leading color dot (matches the design). */
    public static JComponent dotChip(String text) {
        Color c = statusColor(text);
        return new JComponent() {
            {
                setFont(Theme.SECONDARY_BOLD);
                FontMetrics fm = getFontMetrics(Theme.SECONDARY_BOLD);
                setPreferredSize(new Dimension(10 + 6 + 5 + fm.stringWidth(text) + 12, 26));
            }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int pillH = getHeight();
                int dotD = 6;
                g.setFont(Theme.SECONDARY_BOLD);
                FontMetrics fm = g.getFontMetrics();
                int pillW = 10 + dotD + 5 + fm.stringWidth(text) + 12;
                g.setColor(Theme.withAlpha(c, 40));
                g.fillRoundRect(0, 0, pillW, pillH, pillH, pillH);
                g.setColor(c);
                g.fillOval(10, (pillH - dotD) / 2, dotD, dotD);
                g.drawString(text, 10 + dotD + 5,
                        (pillH - fm.getHeight()) / 2 + fm.getAscent());
                g.dispose();
            }
        };
    }

    /** Maps a status word to its chip color. */
    public static Color statusColor(String status) {
        if (status == null) return Theme.MUTED;
        switch (status.toLowerCase()) {
            case "approved":
            case "paid":
            case "good":
            case "completed":
            case "recovered":
            case "unoccupied":
            case "processed":
                return Theme.GREEN;
            case "pending":
            case "requested":
            case "maintenance":
            case "in_progress":
            case "low":
                return Theme.AMBER;
            case "rejected":
            case "damaged":
            case "missing":
            case "suspended":
                return Theme.RED;
            case "unread":
                return Theme.AMBER;
            case "read":
                return Theme.GREEN;
            case "occupied":
            case "rental_payment":
            case "damage_fee":
            case "equipment":
            case "consumable":
                return Theme.BLUE;
            default:
                return Theme.MUTED;
        }
    }

    /** Table cell renderer that draws a colored status pill for a column. */
    public static DefaultTableCellRenderer chipRenderer() {
        return new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(
                        jt, v, sel, foc, r, c);
                String text = v == null ? "" : v.toString();
                l.setText(text);
                l.setOpaque(false);
                l.setFont(Theme.SECONDARY_BOLD);
                l.setForeground(statusColor(text));
                l.setBorder(new EmptyBorder(2, 8, 2, 8));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                return l;
            }
        };
    }

    /** Small dashboard stat card: title, big number, colored underline. */
    public static JPanel statCard(String title, int value, Color accent) {
        JPanel p = cardPanel(new java.awt.BorderLayout(0, 8), 18);
        JLabel t = label(title, Theme.MUTED, 12);
        JLabel v = new JLabel(String.valueOf(value));
        v.setFont(Theme.PAGE_TITLE);
        v.setForeground(accent);
        JComponent bar = new JComponent() {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(accent);
                g.fillRoundRect(0, 0, 44, 6, 6, 6);
                g.dispose();
            }
        };
        p.add(t, java.awt.BorderLayout.NORTH);
        p.add(v, java.awt.BorderLayout.CENTER);
        p.add(bar, java.awt.BorderLayout.SOUTH);
        p.setPreferredSize(new Dimension(100, 112));
        return p;
    }

    /** Card with a bold title and muted body text (financial summaries). */
    public static JPanel summaryCard(String title, String body, Color accent) {
        JPanel p = cardPanel(new java.awt.BorderLayout(0, 6), 18);
        JLabel t = new JLabel(title);
        t.setFont(Theme.CARD_HEADING);
        t.setForeground(accent);
        JLabel b = new JLabel(body);
        b.setFont(Theme.SECONDARY);
        b.setForeground(Theme.MUTED);
        p.add(t, java.awt.BorderLayout.NORTH);
        p.add(b, java.awt.BorderLayout.CENTER);
        return p;
    }

    /** "PHP 1,500.00" -> 1500 (long) for calculations. */
    public static long parseMoney(String moneyText) {
        String digits = moneyText.replaceAll("[^0-9-]", "");
        if (digits.isEmpty()) return 0;
        try { return Long.parseLong(digits); } catch (NumberFormatException e) { return 0; }
    }

    /** BigDecimal 1500 -> "PHP 1,500.00" (correctly keeps decimals). */
    public static String formatMoney(java.math.BigDecimal amount) {
        if (amount == null) return "PHP 0.00";
        return "PHP " + new java.text.DecimalFormat("#,##0.00").format(amount);
    }

    /** The window that owns a component (used as a dialog's parent). */
    public static java.awt.Window windowOf(java.awt.Component c) {
        return javax.swing.SwingUtilities.getWindowAncestor(c);
    }

    /** Styled read-only table from query rows. */
    public static JTable makeTable(ArrayList<String[]> rows, String[] cols) {
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] r : rows) m.addRow(r);
        return styleTable(m);
    }

    /** Styled read-only table from an existing model (used for refreshable tables). */
    public static JTable makeTable(DefaultTableModel model) {
        return styleTable(model);
    }

    /** Applies shared visual styling: height, fonts, header colors, zebra rows. */
    public static JTable styleTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(30);
        t.setFont(Theme.BODY);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 2));
        t.setFillsViewportHeight(true);
        t.setRowSelectionAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel x = (JLabel) super.getTableCellRendererComponent(jt, v, sel, foc, r, c);
                x.setFont(Theme.SECONDARY_BOLD);
                x.setForeground(Theme.MUTED);
                x.setBackground(Theme.WHITE);
                x.setBorder(new EmptyBorder(0, 8, 8, 8));
                return x;
            }
        });
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component x = super.getTableCellRendererComponent(jt, v, sel, foc, r, c);
                x.setFont(Theme.BODY);
                x.setForeground(Theme.BURGUNDY);
                x.setBackground(r % 2 == 0 ? Theme.WHITE : Theme.ZEBRA);
                return x;
            }
        });
        return t;
    }

    // ------------------------------------------------------------------
    // Custom cell renderers (avatar + status pill)
    // ------------------------------------------------------------------

    /** Table cell that draws a circular initials avatar beside a guest name. */
    public static DefaultTableCellRenderer guestRenderer() {
        return new DefaultTableCellRenderer() {
            private String name = "";
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                name = v == null ? "" : v.toString();
                setOpaque(true);
                setText("");
                setBackground(Theme.WHITE);
                setBorder(new EmptyBorder(2, 8, 2, 8));
                return this;
            }
            @Override protected void paintComponent(Graphics g0) {
                super.paintComponent(g0);
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                int d = 30;
                int ax = 8;
                int ay = (getHeight() - d) / 2;
                g.setColor(Theme.HOVER_BG);
                g.fillOval(ax, ay, d, d);
                g.setColor(Theme.withAlpha(Theme.VIOLET, 70));
                g.setStroke(new BasicStroke(1f));
                g.drawOval(ax, ay, d, d);

                String ini = initials(name);
                g.setFont(Theme.SECONDARY_BOLD);
                g.setColor(Theme.PURPLE);
                FontMetrics fm = g.getFontMetrics();
                g.drawString(ini, ax + (d - fm.stringWidth(ini)) / 2,
                        ay + (d + fm.getAscent() - fm.getDescent()) / 2);

                g.setFont(Theme.BODY);
                g.setColor(Theme.BURGUNDY);
                FontMetrics fn = g.getFontMetrics();
                g.drawString(name, ax + d + 10,
                        (getHeight() + fn.getAscent() - fn.getDescent()) / 2);
                g.dispose();
            }
        };
    }

    /** Table cell that draws a rounded status pill with a leading color dot. */
    public static DefaultTableCellRenderer dotChipRenderer() {
        return new DefaultTableCellRenderer() {
            private String text = "";
            private Color color = Theme.MUTED;
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                text = v == null ? "" : v.toString();
                color = statusColor(text);
                setOpaque(true);
                setText("");
                setBackground(Theme.WHITE);
                setBorder(new EmptyBorder(2, 8, 2, 8));
                return this;
            }
            @Override protected void paintComponent(Graphics g0) {
                super.paintComponent(g0);
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setFont(Theme.SECONDARY_BOLD);
                FontMetrics fm = g.getFontMetrics();
                int dotD = 6;
                int textW = fm.stringWidth(text);
                int pillH = Math.min(getHeight() - 8, 24);
                int pillY = (getHeight() - pillH) / 2;
                int pillW = 10 + dotD + 5 + textW + 12;
                int pillX = 6;
                g.setColor(Theme.withAlpha(color, 40));
                g.fillRoundRect(pillX, pillY, pillW, pillH, pillH, pillH);
                g.setColor(color);
                int dotX = pillX + 10;
                g.fillOval(dotX, pillY + (pillH - dotD) / 2, dotD, dotD);
                g.drawString(text, dotX + dotD + 5,
                        pillY + (pillH - fm.getHeight()) / 2 + fm.getAscent());
                g.dispose();
            }
        };
    }

    // ------------------------------------------------------------------
    // Small text / formatting utilities
    // ------------------------------------------------------------------

    /** "Pending" / "in_progress" -> "Pending" / "In progress". */
    public static String titleCase(String s) {
        if (s == null || s.isEmpty()) return "";
        String sp = s.replace('_', ' ');
        return Character.toUpperCase(sp.charAt(0)) + sp.substring(1).toLowerCase();
    }

    /** "Maya Chen" -> "MC". */
    public static String initials(String fullName) {
        if (fullName == null || fullName.isBlank()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
        }
        return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
    }

    /** Booking id 1 -> "BK-0001". */
    public static String bookingRef(int id) {
        return "BK-" + String.format("%04d", id);
    }

    /** "Aug 12, 3:00 PM" + "Aug 16, 11:00 AM" -> "12–16 Aug". */
    public static String compactRange(String from, String to) {
        if (from == null || to == null) return "";
        String[] a = from.split(" ");
        String[] b = to.split(" ");
        if (a.length < 2 || b.length < 2) return from + " – " + to;
        String monA = a[0], dayA = a[1].replace(",", "");
        String monB = b[0], dayB = b[1].replace(",", "");
        if (monA.equalsIgnoreCase(monB)) return dayA + "–" + dayB + " " + monA;
        return dayA + " " + monA + " – " + dayB + " " + monB;
    }

    /** "Aug 12, 3:00 PM" -> "3:00 PM". */
    public static String timePart(String dateTime) {
        if (dateTime == null) return "";
        int i = dateTime.indexOf(',');
        return i >= 0 ? dateTime.substring(i + 1).trim() : dateTime;
    }

    /** First 4-digit year found in a string, or "" if none. */
    public static String yearOf(String s) {
        if (s == null) return "";
        java.util.regex.Matcher m =
                java.util.regex.Pattern.compile("\\b(\\d{4})\\b").matcher(s);
        return m.find() ? m.group(1) : "";
    }

    private UIHelpers() {}
}
package com.gudosnest.ui;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Shared Swing styling helpers for the whole app.
 * v3: AuthFrame-caliber components - pill status chips, hover buttons,
 * zebra-striped tables, deeper card rounding.
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
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.SURFACE);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g.setColor(Theme.BORDER);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(pad, pad, pad, pad));
        return p;
    }

    /** Medium-weight text label in a chosen color and size. */
    public static JLabel label(String text, Color c, int size) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font("body", size));
        l.setForeground(c);
        return l;
    }

    /** Small-caps eyebrow label (like 'O W N E R  P O R T A L' in the auth concept). */
    public static JLabel eyebrow(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.font("body-bold", 10));
        l.setForeground(Theme.ACCENT);
        return l;
    }

    /** Filled rounded plum button (primary action) with hover state. */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text) {
            private boolean hover = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(hover ? Theme.PRIMARY_D : Theme.PRIMARY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        b.setFont(Theme.font("body-bold", 14));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 18, 10, 18));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setContentAreaFilled(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Text-only link-style button (secondary actions). */
    public static JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.font("body", 12));
        b.setForeground(Theme.ACCENT);
        b.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        b.setFocusPainted(false);
        b.setContentAreaFilled(false);
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Rounded toolbar button used for sidebar navigation with active + hover pills. */
    public static NavPill navButton(String text) {
        return new NavPill(text);
    }

    /** Sidebar nav pill: transparent by default, soft white on hover, plum when active. */
    public static class NavPill extends JButton {
        private boolean hover = false;
        private boolean active = false;

        public NavPill(String text) {
            super(text, null);
            setOpaque(false);
            setFont(Theme.font("body", 14));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(9, 12, 9, 12));
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hover = false; repaint(); }
            });
        }

        public void setActive(boolean on) { active = on; repaint(); }

        @Override protected void paintComponent(Graphics g0) {
            setForeground(active ? Color.WHITE : Theme.TEXT);
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active) {
                g.setColor(Theme.PRIMARY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            } else if (hover) {
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g.dispose();
            super.paintComponent(g0);
        }
    }

    /** Styled single-line text input (AuthFrame field look). */
    public static JTextField field(JTextField f) {
        f.setFont(Theme.font("body", 13));
        f.setBackground(new Color(0xFA, 0xF9, 0xFC));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER, 1, true),
            new EmptyBorder(6, 8, 6, 8)));
        return f;
    }

    /** Colored pill status chip, e.g. PENDING / APPROVED. */
    public static JComponent chip(String text, Color c) {
        JLabel l = new JLabel(text) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 38));
                g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                g.dispose();
                super.paintComponent(g0);
            }
        };
        l.setFont(Theme.font("body-bold", 11));
        l.setForeground(c);
        l.setBorder(new EmptyBorder(5, 12, 5, 12));
        l.setOpaque(false);
        return l;
    }

    /** Small dashboard stat card: title, big serif number, colored underline. */
    public static JPanel statCard(String title, int value, Color accent) {
        JPanel p = cardPanel(new BorderLayout(0, 8), 18);
        JLabel t = label(title, Theme.TEXT_MUT, 12);
        JLabel v = new JLabel(String.valueOf(value));
        v.setFont(Theme.font("head-bold", 30));
        v.setForeground(accent);
        JComponent bar = new JComponent() {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(accent);
                g.fillRoundRect(0, 0, 44, 6, 6, 6);
                g.dispose();
            }
        };
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        p.add(bar, BorderLayout.SOUTH);
        p.setPreferredSize(new Dimension(100, 108));
        return p;
    }

    /** Card with a bold title and muted body text (financial summaries). */
    public static JPanel summaryCard(String title, String body, Color accent) {
        JPanel p = cardPanel(new BorderLayout(0, 6), 18);
        JLabel t = new JLabel(title);
        t.setFont(Theme.font("body-bold", 14));
        t.setForeground(accent);
        JLabel b = new JLabel(body);
        b.setFont(Theme.font("body", 12));
        b.setForeground(Theme.TEXT_MUT);
        p.add(t, BorderLayout.NORTH);
        p.add(b, BorderLayout.CENTER);
        return p;
    }

    /** "PHP 1,500.00" -> 1500 (long) for calculations. */
    public static long parseMoney(String moneyText) {
        String digits = moneyText.replaceAll("[^0-9-]", "");
        if (digits.isEmpty()) return 0;
        try { return Long.parseLong(digits); } catch (NumberFormatException e) { return 0; }
    }

    /** Styled read-only table from query rows (zebra rows + hover-friendly header). */
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
        t.setRowHeight(28);
        t.setFont(Theme.font("body", 13));
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 2));
        t.setFillsViewportHeight(true);
        t.setRowSelectionAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                JLabel x = (JLabel) super.getTableCellRendererComponent(jt, v, sel, foc, r, c);
                x.setFont(Theme.font("body-bold", 12));
                x.setForeground(Theme.TEXT_MUT);
                x.setBackground(Theme.SURFACE);
                x.setBorder(new EmptyBorder(0, 8, 8, 8));
                return x;
            }
        });
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable jt, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component x = super.getTableCellRendererComponent(jt, v, sel, foc, r, c);
                x.setFont(Theme.font("body", 13));
                x.setForeground(Theme.TEXT);
                x.setBackground(r % 2 == 0 ? Theme.SURFACE : new Color(0xF8, 0xF6, 0xFB));
                return x;
            }
        });
        return t;
    }

    private UIHelpers() {}
}

package com.gudosnest.owner;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.gudosnest.dao.UnitDAO;
import com.gudosnest.model.PropertyCard;
import com.gudosnest.model.Unit;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

public class UnitsPanel extends JPanel {

    private final OwnerView owner;
    private final UnitDAO unitDAO = new UnitDAO();

    // Three equal columns; the grid tracks the viewport width but not its height.
    private final ScrollableGrid grid =
            new ScrollableGrid(new GridLayout(0, 3, 16, 16));

    private final JLabel title = heading("Units");

    private int propertyId;
    private String propertyName;

    public UnitsPanel(OwnerView owner) {
        this.owner = owner;

        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBackground(Theme.PAGE_BG);

        JButton back = UIHelpers.ghostButton("← Back to properties");
        back.addActionListener(e -> owner.backToProperties());

        JButton add = UIHelpers.accentButton("+ Add unit");
        add.addActionListener(e ->
                new AddUnitDialog(windowOf(this), propertyId, this::refresh)
                        .setVisible(true));

        JPanel right = new JPanel(new BorderLayout(12, 0));
        right.setOpaque(false);
        right.add(title, BorderLayout.CENTER);
        right.add(add, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(back, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        // Header stays outside the scroll pane.
        add(top, BorderLayout.NORTH);

        grid.setOpaque(false);

        JScrollPane scroll = new JScrollPane(
                grid,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Theme.PAGE_BG);

        add(scroll, BorderLayout.CENTER);
    }

    /**
     * Loads the units for a chosen property and shows them.
     */
    public void load(PropertyCard card) {
        this.propertyId = card.getPropertyId();
        this.propertyName = card.getPropertyName();

        title.setText("Units - " + propertyName);

        refresh();
    }

    public void refresh() {
        grid.removeAll();

        ArrayList<Unit> units = unitDAO.getForProperty(propertyId);

        if (units.isEmpty()) {
            grid.setLayout(new BorderLayout());
            JLabel empty = new JLabel("No units for this property.", SwingConstants.CENTER);
            empty.setForeground(Theme.MUTED);
            grid.add(empty, BorderLayout.CENTER);
        } else {
            grid.setLayout(new GridLayout(0, 3, 16, 16));

            for (Unit unit : units) {
                grid.add(new UnitCardView(unit));
            }
        }

        grid.revalidate();
        grid.repaint();
    }

    // ------------------------------------------------------------------
    // Grid that expands to the viewport width and scrolls vertically.
    // ------------------------------------------------------------------

    private static class ScrollableGrid extends JPanel implements Scrollable {

        ScrollableGrid(GridLayout layout) {
            super(layout);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
                java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 24;
        }

        @Override
        public int getScrollableBlockIncrement(
                java.awt.Rectangle visibleRect, int orientation, int direction) {
            return Math.max(visibleRect.height - 24, 24);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // ------------------------------------------------------------------
    // One clickable unit card
    // ------------------------------------------------------------------

    private class UnitCardView extends JPanel {

        private final Unit unit;
        private boolean hover;

        UnitCardView(Unit unit) {
            this.unit = unit;

            setOpaque(false);

            // Preferred height lets the scroll pane calculate the full content height.
            setPreferredSize(new Dimension(260, 150));
            setMinimumSize(new Dimension(0, 150));

            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(18, 20, 16, 20));

            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    owner.toast("Unit details are coming soon.");
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {

            Graphics2D g = (Graphics2D) g0.create();

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            if (hover) {
                for (int i = 0; i < 6; i++) {
                    g.setColor(Theme.withAlpha(Theme.BURGUNDY, 8));

                    g.fillRoundRect(
                            3 - i,
                            5 - i,
                            w - 6 + 2 * i,
                            h - 6 + 2 * i,
                            20 + 2 * i,
                            20 + 2 * i
                    );
                }
            }

            // Card background.
            g.setColor(Theme.WHITE);

            g.fillRoundRect(
                    0, 0,
                    w, h - 6,
                    20, 20
            );

            // Card border.
            g.setColor(hover ? Theme.LIGHT_BLUE : Theme.HAIRLINE);
            g.setStroke(new BasicStroke(1.2f));

            g.drawRoundRect(
                    0, 0,
                    w - 1, h - 7,
                    20, 20
            );

            // Top decorative accent.
            g.setColor(Theme.BLUE);

            g.fillRoundRect(
                    20, 20,
                    34, 6,
                    6, 6
            );

            // Unit name.
            g.setColor(Theme.BURGUNDY);
            g.setFont(Theme.CARD_HEADING);

            g.drawString(
                    unit.getUnitName(),
                    20, 48
            );

            // Capacity.
            g.setColor(Theme.MUTED);
            g.setFont(Theme.SECONDARY);

            g.drawString(
                    unit.getCapacity() + " guests",
                    20, 70
            );

            // Daily rate.
            g.setColor(Theme.PURPLE);
            g.setFont(Theme.SECONDARY_BOLD);

            g.drawString(
                    formatMoney(unit.getDailyRate()) + " / night",
                    20, h - 24
            );

            // Status chip, top-right.
            String status = unit.getUnitStatus().toUpperCase();

            Color sc = statusColor(unit.getUnitStatus());

            g.setFont(Theme.SECONDARY);

            FontMetrics fm = g.getFontMetrics();

            int cw = fm.stringWidth(status) + 20;
            int cx = w - 20 - cw;

            g.setColor(Theme.withAlpha(sc, 38));

            g.fillRoundRect(
                    cx, 20,
                    cw, 22,
                    22, 22
            );

            g.setColor(sc);

            g.drawString(
                    status,
                    cx + 10, 35
            );

            g.dispose();
        }
    }
}

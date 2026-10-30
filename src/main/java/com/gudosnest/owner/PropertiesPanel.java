package com.gudosnest.owner;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import com.gudosnest.dao.PropertyDAO;
import com.gudosnest.model.PropertyCard;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

/**
 * Properties: title + "Add property" button, search and filter row,
 * and a responsive three-column grid of equally sized property cards.
 * The header stays pinned while the card grid scrolls independently.
 */
public class PropertiesPanel extends JPanel {
    private static final int CARD_H = 240;
    private static final int COVER_H = 120;
    private static final int GAP = 18;
    /** Solid color in place of a photo. Swap for an image later. */
    private static final Color COVER = Theme.BLUE;
    private final OwnerView owner;
    private final PropertyDAO propertyDAO = new PropertyDAO();
    private final JPanel grid = new JPanel(new GridLayout(0, 3, GAP, GAP));
    private final JTextField search = placeholderField("Search properties...");
    private final JComboBox<String> filter = new JComboBox<>(
            new String[] { "All properties", "Whole properties", "Buildings" });
    private final JLabel count = UIHelpers.label(" ", Theme.MUTED, 13);
    private ArrayList<PropertyCard> allCards = new ArrayList<>();

    public PropertiesPanel(OwnerView owner) {
        this.owner = owner;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        add(buildTop(), BorderLayout.NORTH);
        grid.setOpaque(false);
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);

        // Keep the grid at the top and stretch it across the available width.
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.weightx = 1.0;
        gridConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridConstraints.anchor = GridBagConstraints.NORTH;
        holder.add(grid, gridConstraints);
        JScrollPane scroll = new JScrollPane(holder);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
        // Live search + filter.
        search.getDocument().addDocumentListener(new DocumentListener() {
    
        @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }
    
        @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }
    
        @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
        filter.addActionListener(e -> applyFilter());
        refresh();
    }

    // ------------------------------------------------------------------
    // Top area: title, Add button, search/filter row

    // ------------------------------------------------------------------

    private JPanel buildTop() {
        JLabel title = new JLabel("Properties");
        title.setFont(Theme.PAGE_TITLE);
        title.setForeground(Theme.BURGUNDY);
        JLabel sub = UIHelpers.label(
                "Manage your properties, units, inventory, and maintenance.", Theme.MUTED, 14);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(title);
        titles.add(sub);
        JButton add = UIHelpers.primaryButton("+  Add property");
        add.addActionListener(e -> new AddPropertyDialog(UIHelpers.windowOf(this), () -> {
            refresh();
            owner.toast("Property added.");
        }).setVisible(true));
        JPanel addWrap = new JPanel(new GridBagLayout()); // keeps the button at its own size
        addWrap.setOpaque(false);
        addWrap.add(add);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titles, BorderLayout.WEST);
        header.add(addWrap, BorderLayout.EAST);
        search.setPreferredSize(new Dimension(260, 38));
        filter.setFont(Theme.INPUT);
        filter.setPreferredSize(new Dimension(170, 38));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        controls.setOpaque(false);
        controls.add(search);
        controls.add(Box.createHorizontalStrut(10));
        controls.add(filter);
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(controls, BorderLayout.WEST);
        toolbar.add(count, BorderLayout.EAST);
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(header);
        top.add(Box.createVerticalStrut(16));
        top.add(toolbar);
        return top;
    }
    /** Text field that shows grey hint text while empty. */

    private static JTextField placeholderField(String hint) {
        JTextField f = new JTextField() {
    
        @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    g.setColor(Theme.HINT);
                    g.setFont(Theme.INPUT);
                    FontMetrics fm = g.getFontMetrics();
                    Insets in = getInsets();
                    g.drawString(hint, in.left,
                            (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        return UIHelpers.field(f);
    }

    // ------------------------------------------------------------------
    // Data + layout

    // ------------------------------------------------------------------
    /** Reloads the property cards from the database. */

    public void refresh() {
        allCards = propertyDAO.getCards(null);
        applyFilter();
    }
    /** Rebuilds the grid from allCards using the search text and filter. */

    private void applyFilter() {
        String q = search.getText().trim().toLowerCase();
        int mode = filter.getSelectedIndex(); // 0 = all, 1 = whole, 2 = buildings
        grid.removeAll();
        int shown = 0;
        int units = 0;
        for (PropertyCard c : allCards) {
            boolean single = c.isSingleUnit();
            if (mode == 1 && !single)
                continue;
            if (mode == 2 && single)
                continue;
            if (!q.isEmpty()
                    && !c.getPropertyName().toLowerCase().contains(q)
                    && !c.getLocation().toLowerCase().contains(q))
                continue;
            grid.add(new PropertyCardView(c));
            shown++;
            units += c.getUnitCount();
        }
        if (shown == 0) {
            grid.add(UIHelpers.label(allCards.isEmpty()
                    ? "No properties yet."
                    : "No properties match your search.", Theme.MUTED, 14));
        }
        count.setText(shown + (shown == 1 ? " property" : " properties")
                + " · " + units + " total units");
        grid.revalidate();
        grid.repaint();
    }

    // ------------------------------------------------------------------
    // One clickable property card

    // ------------------------------------------------------------------

    private class PropertyCardView extends JPanel {
        private final PropertyCard card;
        private boolean hover;

        PropertyCardView(PropertyCard card) {
            this.card = card;
            setOpaque(false);
            setPreferredSize(new Dimension(390, CARD_H));
            setMinimumSize(new Dimension(0, CARD_H));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
                    open();
                }
            });
        }

        private void open() {
            owner.openUnits(card);
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight() - 6; // bottom 6px is room for the hover shadow
            if (hover) {
                for (int i = 0; i < 6; i++) {
                    g.setColor(Theme.withAlpha(Theme.BURGUNDY, 8));
                    g.fillRoundRect(3 - i, 5 - i, w - 6 + 2 * i, h + 2 * i - 2,
                            20 + 2 * i, 20 + 2 * i);
                }
            }
            // Card body, cover block (clipped to the rounded corners), border.
            Shape shape = new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 20, 20);
            g.setColor(Theme.WHITE);
            g.fill(shape);
            g.setClip(shape);
            g.setColor(COVER);
            g.fillRect(0, 0, w, COVER_H);
            g.setClip(null);
            g.setColor(hover ? Theme.LIGHT_BLUE : Theme.HAIRLINE);
            g.setStroke(new BasicStroke(1.2f));
            g.draw(shape);
            // Eyebrow (type) + unit count on the right.
            int x = 22;
            int innerW = w - 2 * x;
            g.setFont(Theme.SECONDARY_BOLD);
            g.setColor(Theme.MUTED);
            g.drawString(card.isSingleUnit() ? "WHOLE PROPERTY" : "BUILDING", x, COVER_H + 30);
            if (!card.isSingleUnit()) {
                String units = card.getUnitCount() + " units";
                g.drawString(units, w - x - g.getFontMetrics().stringWidth(units), COVER_H + 30);
            }
            // Property name.
            g.setFont(Theme.CARD_HEADING);
            g.setColor(Theme.BURGUNDY);
            g.drawString(fit(g, card.getPropertyName(), innerW), x, COVER_H + 58);
            // Divider, then location (left) and starting rate (right).
            g.setColor(Theme.HAIRLINE);
            g.drawLine(x, h - 44, w - x, h - 44);
            int rateW = 0;
            if (card.getMinDailyRate() != null) {
                String rate = "from " + UIHelpers.formatMoney(card.getMinDailyRate()) + " / night";
                g.setFont(Theme.SECONDARY_BOLD);
                rateW = g.getFontMetrics().stringWidth(rate);
                g.setColor(Theme.PURPLE);
                g.drawString(rate, w - x - rateW, h - 16);
            }
            g.setFont(Theme.SECONDARY);
            g.setColor(Theme.MUTED);
            g.drawString(fit(g, card.getLocation(), innerW - rateW - 12), x, h - 16);
            g.dispose();
        }
        /** Shortens text with "..." so it fits in maxW pixels. */
        private String fit(Graphics2D g, String text, int maxW) {
            if (text == null)
                return "";
            FontMetrics fm = g.getFontMetrics();
            if (fm.stringWidth(text) <= maxW)
                return text;
            String t = text;
            while (t.length() > 1 && fm.stringWidth(t + "...") > maxW) {
                t = t.substring(0, t.length() - 1);
            }
            return t + "...";
        }
    }
}

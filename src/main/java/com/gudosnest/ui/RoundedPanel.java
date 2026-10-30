package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/** Rounded rectangle panel with a fill and optional hairline border. */
public class RoundedPanel extends JPanel {
    private final int arc;
    private final Color fill;
    private final Color border;

    public RoundedPanel(int arc, Color fill, Color border) {
        this.arc = arc;
        this.fill = fill;
        this.border = border;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D shape = new RoundRectangle2D.Float(
                0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
        g2.setColor(fill);
        g2.fill(shape);
        if (border != null) {
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1f));
            g2.draw(shape);
        }
        g2.dispose();
    }
}

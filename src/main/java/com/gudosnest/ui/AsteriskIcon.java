package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import javax.swing.JComponent;

/** Small asterisk mark shown above the auth heading. */
public class AsteriskIcon extends JComponent {
    public AsteriskIcon() {
        setPreferredSize(new Dimension(30, 30));
        setMaximumSize(new Dimension(30, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float r = Math.min(getWidth(), getHeight()) / 2f - 2f;

        g2.setColor(Theme.PURPLE);
        g2.setStroke(new BasicStroke(3.6f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 3; i++) {
            double a = Math.toRadians(90 + i * 60);
            float dx = (float) (Math.cos(a) * r);
            float dy = (float) (Math.sin(a) * r);
            g2.draw(new Line2D.Float(cx - dx, cy - dy, cx + dx, cy + dy));
        }
        g2.dispose();
    }
}

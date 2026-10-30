package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.JButton;

/** Eye icon that toggles password visibility. */
public class EyeButton extends JButton {
    private boolean revealed;

    public EyeButton() {
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(28, 28));
        setToolTipText("Show or hide password");
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;

        g2.setColor(Theme.VIOLET);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        Path2D eye = new Path2D.Float();
        eye.moveTo(cx - 9, cy);
        eye.quadTo(cx, cy - 10, cx + 9, cy);
        eye.quadTo(cx, cy + 10, cx - 9, cy);
        g2.draw(eye);
        g2.drawOval(Math.round(cx - 3), Math.round(cy - 3), 6, 6);

        if (!revealed) {
            g2.draw(new Line2D.Float(cx - 8, cy + 8, cx + 8, cy - 8));
        }
        g2.dispose();
    }
}

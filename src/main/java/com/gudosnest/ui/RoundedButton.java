package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * Fully self-painted rounded button so colours look the same on every
 * look-and-feel (system look-and-feels often ignore setBackground).
 */
public class RoundedButton extends JButton {
    private Color fill = Theme.WHITE;
    private Color hoverFill = null;
    private Color textColor = Theme.BURGUNDY;
    private Color borderColor = null;
    private boolean shadow = false;
    private final int arc = 16;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFont(Theme.BUTTON);
    }

    public void setColors(Color fill, Color hoverFill,
                          Color textColor, Color borderColor) {
        this.fill = fill;
        this.hoverFill = hoverFill;
        this.textColor = textColor;
        this.borderColor = borderColor;
        repaint();
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        int bodyH = shadow ? h - 10 : h;

        if (shadow) {
            for (int i = 0; i < 6; i++) {
                g2.setColor(Theme.withAlpha(Theme.BURGUNDY, 10));
                g2.fillRoundRect(6 - i, 8 - i, w - 12 + 2 * i,
                        bodyH + 2 * i, arc + 2 * i, arc + 2 * i);
            }
        }

        Color f = (getModel().isRollover() && hoverFill != null) ? hoverFill : fill;
        g2.setColor(f);
        g2.fillRoundRect(0, 0, w, bodyH, arc, arc);

        if (borderColor != null) {
            g2.setColor(borderColor);
            g2.drawRoundRect(0, 0, w - 1, bodyH - 1, arc, arc);
        }

        g2.setColor(textColor);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        String text = getText();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (bodyH - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, tx, ty);
        g2.dispose();
    }
}

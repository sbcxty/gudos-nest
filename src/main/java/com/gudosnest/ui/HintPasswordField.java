package com.gudosnest.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.JPasswordField;

/** Password field that paints grey hint text while empty. */
public class HintPasswordField extends JPasswordField {
    private final String hint;

    public HintPasswordField(String hint) {
        this.hint = hint;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getDocument().getLength() == 0) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(Theme.HINT);
            g2.setFont(getFont());
            java.awt.FontMetrics fm = g2.getFontMetrics();
            Insets in = getInsets();
            g2.drawString(hint, in.left,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
            g2.dispose();
        }
    }
}

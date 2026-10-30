package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

/** Rounded input container that wraps a text field (and optional trailing icon). */
public class InputBox extends JPanel {
    private boolean focused;

    public InputBox(JTextComponent field, JComponent trailing) {
        super(new BorderLayout(8, 0));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 14, 0, 10));

        field.setOpaque(false);
        field.setBorder(null);
        field.setFont(Theme.INPUT);
        field.setForeground(Theme.BURGUNDY);
        field.setCaretColor(Theme.BURGUNDY);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
            @Override public void focusLost(FocusEvent e)   { focused = false; repaint(); }
        });

        add(field, BorderLayout.CENTER);
        if (trailing != null) {
            add(trailing, BorderLayout.EAST);
        }

        setPreferredSize(new Dimension(300, 48));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        RoundRectangle2D shape = new RoundRectangle2D.Float(
                0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 14, 14);
        g2.setColor(Theme.FIELD_BG);
        g2.fill(shape);
        g2.setStroke(new BasicStroke(focused ? 1.6f : 1f));
        g2.setColor(focused ? Theme.VIOLET : Theme.HAIRLINE);
        g2.draw(shape);
        g2.dispose();
    }
}

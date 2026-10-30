package com.gudosnest.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

/** Styled dropdown that matches the rounded input look. */
public class SelectBox<T> extends JComboBox<T> {
    public SelectBox() {
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean foc) {
                JLabel l = (JLabel) super.getListCellRendererComponent(
                        list, value, index, sel, foc);
                l.setFont(Theme.INPUT);
                l.setForeground(sel ? Theme.WHITE : Theme.BURGUNDY);
                l.setBackground(sel ? Theme.PURPLE : Theme.WHITE);
                l.setOpaque(true);
                l.setBorder(new EmptyBorder(6, 10, 6, 10));
                return l;
            }
        });
        setFont(Theme.INPUT);
        setPreferredSize(new Dimension(200, 40));
        setBackground(Theme.WHITE);
        setForeground(Theme.BURGUNDY);
    }
}

package com.gudosnest.owner;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

/**
 * A simple "coming soon" card used for the portal sections that are not
 * part of the current focus (bookings + properties). Keeps the nav intact
 * without pulling in the old full-featured panels.
 */
public class PlaceholderPanel extends JPanel {

    public PlaceholderPanel(String title, String message) {
        setOpaque(false);
        setLayout(new GridBagLayout());

        JPanel card = UIHelpers.cardPanel(new BorderLayout(0, 10), 32, 26);

        JLabel eyebrow = new JLabel("COMING SOON");
        eyebrow.setFont(Theme.SECONDARY_BOLD);
        eyebrow.setForeground(Theme.VIOLET);

        JLabel heading = new JLabel(title);
        heading.setFont(Theme.f(Font.BOLD, 22));
        heading.setForeground(Theme.BURGUNDY);

        JLabel body = new JLabel(message);
        body.setFont(Theme.BODY);
        body.setForeground(Theme.MUTED);
        body.setBorder(new EmptyBorder(4, 0, 0, 0));

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        for (JComponent c : new JComponent[] {eyebrow, heading, body}) {
            c.setAlignmentX(LEFT_ALIGNMENT);
            titles.add(c);
        }

        card.add(titles, BorderLayout.CENTER);
        add(card);
    }
}

package com.gudosnest.ui.OwnerHomePanels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.*;

import com.gudosnest.db.DataStore;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Bookings: list of renter requests with a status legend.
 * Skeleton note: approve/reject buttons, refund rules, damage fees next.
 */
public class BookingsPanel extends JPanel {
    public BookingsPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Object[][] keyed = {{"PENDING", Theme.AMBER}, {"APPROVED", Theme.GREEN},
                {"REJECTED", Theme.RED}, {"CANCELLED", Theme.TEXT_MUT}, {"COMPLETED", Theme.BLUE}};
        JPanel legendRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        legendRow.setOpaque(false);
        legendRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        legendRow.add(label("Status legend:", Theme.TEXT_MUT, 12));
        for (Object[] k : keyed) legendRow.add(chip((String) k[0], (Color) k[1]));

        JPanel book = cardPanel(new BorderLayout(0, 10), 16);
        book.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(label("Bookings (auto-updates from renter requests)", Theme.TEXT, 14), BorderLayout.WEST);
        top.add(legendRow, BorderLayout.EAST);
        book.add(top, BorderLayout.NORTH);
        book.add(makeTable(DataStore.getBookings(),
            new String[]{"#", "Renter", "Property", "Unit", "Check-in", "Check-out", "Guests", "Status", "Total"}), BorderLayout.CENTER);
        book.setPreferredSize(new Dimension(100, 380));
        add(book);
        add(Box.createVerticalStrut(12));
        JLabel note = label(
            "Skeleton note: Approve/Reject buttons per row, cancel-refund rules and"
            + " damage-fee requests are part of the next milestone.",
            Theme.TEXT_MUT, 12);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(note);
    }
}

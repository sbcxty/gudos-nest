package com.gudosnest.ui.OwnerHomePanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.gudosnest.db.DataStore;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Dashboard: quick stats + latest booking activity.
 * Skeleton note: action buttons (approve/reject, add units, edit inventory)
 * are next milestones.
 */
public class DashboardPanel extends JPanel {
    public DashboardPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.add(statCard("Properties", DataStore.count("SELECT COUNT(*) FROM properties"), Theme.PRIMARY));
        stats.add(statCard("Occupied units", DataStore.count("SELECT COUNT(*) FROM units WHERE unit_status='occupied'"), Theme.BLUE));
        stats.add(statCard("Pending requests", DataStore.count("SELECT COUNT(*) FROM bookings WHERE booking_status='pending'"), Theme.AMBER));
        stats.add(statCard("Inventory items", DataStore.count("SELECT COUNT(*) FROM inventory"), Theme.GREEN));

        JPanel latest = cardPanel(new BorderLayout(0, 10), 16);
        latest.setAlignmentX(Component.LEFT_ALIGNMENT);
        latest.add(label("Latest booking activity", Theme.TEXT, 14), BorderLayout.NORTH);
        latest.add(makeTable(DataStore.getBookings(),
            new String[]{"#", "Renter", "Property", "Unit", "Check-in", "Check-out", "Guests", "Status", "Total"}), BorderLayout.CENTER);
        latest.setPreferredSize(new Dimension(100, 260));

        add(stats);
        add(Box.createVerticalStrut(14));
        add(latest);
        add(Box.createVerticalStrut(14));
        JLabel hint = label(
            "Skeleton note: action buttons (approve/reject booking, add units, edit inventory)"
            + " are next milestones. Current demo focus: persistent data + owner home navigation.",
            Theme.TEXT_MUT, 12);
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(hint);
    }
}

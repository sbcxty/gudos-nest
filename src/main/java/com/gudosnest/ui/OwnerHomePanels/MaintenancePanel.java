package com.gudosnest.ui.OwnerHomePanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.*;

import com.gudosnest.db.DataStore;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Maintenance: repair log per property/unit/item.
 * Skeleton note: record/complete actions + automatic expense logging next.
 */
public class MaintenancePanel extends JPanel {
    public MaintenancePanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel m = cardPanel(new BorderLayout(0, 10), 16);
        m.setAlignmentX(Component.LEFT_ALIGNMENT);
        m.add(label("Maintenance log", Theme.TEXT, 14), BorderLayout.NORTH);
        m.add(makeTable(DataStore.getMaintenance(),
            new String[]{"Description", "Property", "Cost", "Status", "Started"}), BorderLayout.CENTER);
        m.setPreferredSize(new Dimension(100, 330));
        add(m);
        add(Box.createVerticalStrut(12));
        JLabel note = label(
            "Skeleton note: record/complete maintenance actions and automatic"
            + " expense logging come next.",
            Theme.TEXT_MUT, 12);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(note);
    }
}

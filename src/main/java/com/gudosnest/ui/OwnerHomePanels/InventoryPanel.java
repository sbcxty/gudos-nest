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
 * Inventory: consumables + long-term equipment per property.
 * Skeleton note: add/restock forms, condition updates, low-stock alerts next.
 */
public class InventoryPanel extends JPanel {
    public InventoryPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JPanel inv = cardPanel(new BorderLayout(0, 10), 16);
        inv.setAlignmentX(Component.LEFT_ALIGNMENT);
        inv.add(label("Property supplies (consumables + long-term equipment)", Theme.TEXT, 14), BorderLayout.NORTH);
        inv.add(makeTable(DataStore.getInventory(),
            new String[]{"Item", "Property", "Type", "Quantity", "Unit price", "Condition"}), BorderLayout.CENTER);
        inv.setPreferredSize(new Dimension(100, 330));
        add(inv);
        add(Box.createVerticalStrut(12));
        JLabel note = label(
            "Skeleton note: add/restock item forms, condition updates and"
            + " low-stock alerts come next (e.g., 'Shampoo sachets' is below its restock threshold).",
            Theme.TEXT_MUT, 12);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(note);
    }
}

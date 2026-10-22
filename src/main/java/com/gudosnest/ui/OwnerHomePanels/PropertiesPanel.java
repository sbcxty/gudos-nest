package com.gudosnest.ui.OwnerHomePanels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.gudosnest.db.DataStore;
import com.gudosnest.ui.Toast;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Properties: live demo of SAVE + RETRIEVE + RETAIN against MySQL.
 */
public class PropertiesPanel extends JPanel {

    private final JTextField name = new JTextField(16);
    private final JTextField location = new JTextField(10);
    private final JTextField rate = new JTextField(5);
    private final JTextField capacity = new JTextField(3);
    private final JCheckBox wholeHouse = new JCheckBox("Rent as whole house");
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"Property", "Location", "Setup", "Capacity", "Daily rate", "Unit status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = makeTable(model);

    public PropertiesPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        refresh();

        JPanel list = cardPanel(new BorderLayout(0, 10), 16);
        list.setAlignmentX(Component.LEFT_ALIGNMENT);
        list.add(label("Your properties", Theme.TEXT, 14), BorderLayout.NORTH);
        list.add(new JScrollPane(table), BorderLayout.CENTER);
        list.setPreferredSize(new Dimension(100, 300));
        add(list);
        add(Box.createVerticalStrut(12));

        JPanel addCard = cardPanel(new GridBagLayout(), 16);
        addCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(2, 6, 2, 6);
        g.gridy = 0; g.gridx = 0; addCard.add(label("Add sample property (saves to MySQL)", Theme.TEXT, 13), g);
        g.gridx = 1; addCard.add(field(name), g);
        g.gridx = 2; addCard.add(label("Location", Theme.TEXT_MUT, 12), g);
        g.gridx = 3; addCard.add(field(location), g);
        g.gridy = 1; g.gridx = 0; addCard.add(label("Daily rate", Theme.TEXT_MUT, 12), g);
        g.gridx = 1; addCard.add(field(rate), g);
        g.gridx = 2; addCard.add(label("Capacity", Theme.TEXT_MUT, 12), g);
        g.gridx = 3; addCard.add(field(capacity), g);
        g.gridy = 2; g.gridx = 1; wholeHouse.setOpaque(false); addCard.add(wholeHouse, g);
        g.gridx = 2;
        JButton save = primaryButton("Save property");
        save.addActionListener(e -> doSave());
        addCard.add(save, g);
        g.gridx = 3;
        JButton reload = ghostButton("Reload from DB");
        reload.addActionListener(e -> refresh());
        addCard.add(reload, g);
        add(addCard);
    }

    /** Clears the table and re-reads all properties from MySQL. */
    public void refresh() {
        model.setRowCount(0);
        for (String[] row : DataStore.getProperties()) model.addRow(row);
    }

    /** Validates the form, saves one property row, then refreshes. */
    private void doSave() {
        if (name.getText().isBlank() || !rate.getText().matches("\\d+") || !capacity.getText().matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                "Fill in a property name and numeric daily rate / capacity.");
            return;
        }
        DataStore.insertSampleProperty(
            name.getText().trim(),
            location.getText().isBlank() ? "Unspecified" : location.getText().trim(),
            rate.getText().trim(),
            Integer.parseInt(capacity.getText().trim()),
            wholeHouse.isSelected());
        Toast.show(this,
            "Property saved to MySQL - it will survive a restart (persistence).");
        refresh();
        name.setText(""); location.setText(""); rate.setText(""); capacity.setText("");
    }
}

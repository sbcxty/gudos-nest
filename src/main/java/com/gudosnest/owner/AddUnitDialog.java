package com.gudosnest.owner;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import com.gudosnest.dao.UnitDAO;
import com.gudosnest.model.Unit;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

/**
 * Form for adding one or more units to a property.
 *
 * PROTOTYPE: the "Start from existing unit" dropdown clones a chosen unit
 * via Unit.clone() to pre-fill the fields, and the "Number of copies" spinner
 * lets the owner bulk-create many identical units in one save.
 */
class AddUnitDialog extends JDialog {

    private final int propertyId;
    private final UnitDAO dao = new UnitDAO();
    private final Runnable onSaved;

    private final ArrayList<Unit> existing = new ArrayList<>();
    private final JComboBox<String> startFrom = new JComboBox<>();
    private final JTextField name = UIHelpers.field(new JTextField());
    private final JTextField description = UIHelpers.field(new JTextField());
    private final JTextField capacity = UIHelpers.field(new JTextField("2"));
    private final JTextField rate = UIHelpers.field(new JTextField());
    private final JComboBox<String> status = new JComboBox<>(
            new String[] {"unoccupied", "occupied", "maintenance"});
    private final JSpinner copies = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final JLabel error = UIHelpers.label(" ", Theme.RED, 12);

    AddUnitDialog(Window parent, int propertyId, Runnable onSaved) {
        super(parent, "Add unit", Dialog.ModalityType.APPLICATION_MODAL);
        this.propertyId = propertyId;
        this.onSaved = onSaved;

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.WHITE);
        body.setBorder(new EmptyBorder(22, 24, 18, 24));

        JLabel title = UIHelpers.heading("Add unit");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title);
        body.add(Box.createVerticalStrut(14));

        // PROTOTYPE: pick an existing unit to clone, or start blank.
        startFrom.addItem("— Blank unit —");
        for (Unit u : dao.getForProperty(propertyId)) {
            existing.add(u);
            startFrom.addItem(u.getUnitName());
        }
        startFrom.setFont(Theme.INPUT);
        startFrom.addActionListener(e -> applyPrototype());
        addRow(body, "Start from existing unit - optional", startFrom);

        addRow(body, "Unit name", name);
        addRow(body, "Description - optional", description);
        addRow(body, "Capacity (guests)", capacity);
        addRow(body, "Daily rate (PHP)", rate);
        addRow(body, "Status", status);
        addRow(body, "Number of copies", copies);

        error.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(error);
        body.add(Box.createVerticalStrut(10));

        JButton cancel = UIHelpers.ghostButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton save = UIHelpers.primaryButton("Save unit(s)");
        save.addActionListener(e -> save());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(save);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(buttons);

        setContentPane(body);
        setResizable(false);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addRow(JPanel body, String labelText, JComponent field) {
        JLabel l = UIHelpers.label(labelText, Theme.MUTED, 12);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(380, 38));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        body.add(l);
        body.add(Box.createVerticalStrut(4));
        body.add(field);
        body.add(Box.createVerticalStrut(12));
    }

    /** Copies the chosen unit's fields into the form (PROTOTYPE clone). */
    private void applyPrototype() {
        int index = startFrom.getSelectedIndex() - 1; // 0 = blank
        if (index < 0 || index >= existing.size()) return;
        Unit prototype = existing.get(index).clone();
        name.setText(prototype.getUnitName());
        description.setText(prototype.getUnitDescription() == null
                ? "" : prototype.getUnitDescription());
        capacity.setText(String.valueOf(prototype.getCapacity()));
        rate.setText(prototype.getDailyRate().toPlainString());
        status.setSelectedItem(prototype.getUnitStatus());
    }

    private void save() {
        String unitName = name.getText().trim();
        if (unitName.isEmpty()) {
            error.setText("Unit name is required.");
            return;
        }
        int cap;
        BigDecimal dailyRate;
        try {
            cap = Integer.parseInt(capacity.getText().trim());
            dailyRate = new BigDecimal(rate.getText().trim());
        } catch (NumberFormatException e) {
            error.setText("Capacity must be a whole number and rate a number, e.g. 1500.");
            return;
        }

        int count = (int) copies.getValue();
        for (int i = 1; i <= count; i++) {
            String label = copyLabel(unitName, i, count);
            Unit unit = new Unit(0, propertyId, label,
                    description.getText().trim(), cap, dailyRate,
                    (String) status.getSelectedItem());
            if (dao.insert(unit) < 0) {
                error.setText("Could not save unit \"" + label + "\".");
                return;
            }
        }
        onSaved.run();
        dispose();
    }

    /**
     * Names each copy. A purely numeric base ("1") becomes plain sequence
     * numbers ("1", "2", "3"); a named base ("Loft Type") keeps the name and
     * appends the number ("Loft Type 1", "Loft Type 2").
     */
    private static String copyLabel(String base, int index, int count) {
        if (count == 1) return base;
        return isNumeric(base) ? String.valueOf(index) : base + " " + index;
    }

    private static boolean isNumeric(String s) {
        if (s == null || s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) return false;
        }
        return true;
    }
}

package com.gudosnest.owner;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.gudosnest.core.Session;
import com.gudosnest.dao.PropertyDAO;
import com.gudosnest.model.Property;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.UIHelpers;

/** Small form for adding a property. Uses Property.Builder to assemble it. */
class AddPropertyDialog extends JDialog {

    private final PropertyDAO dao = new PropertyDAO();
    private final Runnable onSaved;

    private final JTextField name     = UIHelpers.field(new JTextField());
    private final JTextField location = UIHelpers.field(new JTextField());
    private final JTextField cost     = UIHelpers.field(new JTextField());
    private final JTextField date     = UIHelpers.field(new JTextField());
    private final JLabel error = UIHelpers.label(" ", Theme.RED, 12);

    AddPropertyDialog(Window parent, Runnable onSaved) {
        super(parent, "Add property", Dialog.ModalityType.APPLICATION_MODAL);
        this.onSaved = onSaved;

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Theme.WHITE);
        body.setBorder(new EmptyBorder(22, 24, 18, 24));

        JLabel title = UIHelpers.heading("Add property");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(title);
        body.add(Box.createVerticalStrut(14));

        addRow(body, "Property name", name);
        addRow(body, "Location", location);
        addRow(body, "Acquisition cost (PHP)", cost);
        addRow(body, "Acquisition date (YYYY-MM-DD) - optional", date);

        error.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(error);
        body.add(Box.createVerticalStrut(10));

        JButton cancel = UIHelpers.ghostButton("Cancel");
        cancel.addActionListener(e -> dispose());
        JButton save = UIHelpers.primaryButton("Save property");
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

    private void addRow(JPanel body, String labelText, JTextField f) {
        JLabel l = UIHelpers.label(labelText, Theme.MUTED, 12);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.setPreferredSize(new Dimension(380, 38));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        body.add(l);
        body.add(Box.createVerticalStrut(4));
        body.add(f);
        body.add(Box.createVerticalStrut(12));
    }

    private void save() {
        try {
            Property.Builder b = new Property.Builder(
                    Session.getInstance().getUser().getUserId(),
                    name.getText().trim(),
                    location.getText().trim(),
                    new BigDecimal(cost.getText().trim()));
            if (!date.getText().isBlank()) {
                b.acquisitionDate(LocalDate.parse(date.getText().trim()));
            }
            Property p = b.build();

            if (dao.insert(p)) {
                onSaved.run();
                dispose();
            } else {
                error.setText("Could not save. Please try again.");
            }
        } catch (NumberFormatException e) {
            error.setText("Cost must be a number, e.g. 2500000.");
        } catch (DateTimeParseException e) {
            error.setText("Date must look like 2024-08-12.");
        } catch (IllegalArgumentException e) {
            error.setText(e.getMessage());
        }
    }
}
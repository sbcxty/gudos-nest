package com.gudosnest.renter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.gudosnest.model.Booking;
import com.gudosnest.model.UnitListing;
import com.gudosnest.service.BookingService;
import com.gudosnest.ui.HintTextField;
import com.gudosnest.ui.InputBox;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.Toast;

import static com.gudosnest.ui.UIHelpers.*;

/** Modal dialog for a renter to book an available unit. */
public class RenterBookingDialog extends JDialog {

    private final int renterId;
    private final UnitListing listing;
    private final Runnable onBooked;
    private final BookingService bookingService = new BookingService();

    private final JTextField checkIn = new HintTextField(LocalDate.now().toString());
    private final JTextField checkOut = new HintTextField(LocalDate.now().plusDays(1).toString());
    private final JSpinner guests = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
    private final JTextField requests = new HintTextField("Anything the host should know?");
    private final JLabel totalLabel = label("Total: PHP 0.00", Theme.PURPLE, 14);

    public RenterBookingDialog(Component owner, int renterId, UnitListing listing,
                               Runnable onBooked) {
        super(windowOf(owner), "Book " + listing.getUnitName(), Dialog.ModalityType.APPLICATION_MODAL);
        this.renterId = renterId;
        this.listing = listing;
        this.onBooked = onBooked;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(460, 430);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(Theme.PAGE_BG);
        root.setBorder(new EmptyBorder(22, 24, 20, 24));

        JLabel sub = heading(listing.getPropertyName() + " - " + listing.getUnitName());
        JLabel rate = label(formatMoney(listing.getDailyRate()) + " / night, "
                + listing.getCapacity() + " guests", Theme.MUTED, 13);
        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.add(sub);
        head.add(rate);
        root.add(head, BorderLayout.NORTH);

        guests.setFont(Theme.INPUT);
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        form.add(label("Check-in", Theme.BURGUNDY, 13), g);
        g.gridx = 1; g.weightx = 1;
        form.add(new InputBox(checkIn, null), g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(label("Check-out", Theme.BURGUNDY, 13), g);
        g.gridx = 1; g.weightx = 1;
        form.add(new InputBox(checkOut, null), g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0;
        form.add(label("Guests", Theme.BURGUNDY, 13), g);
        g.gridx = 1; g.weightx = 1;
        form.add(guests, g);

        g.gridx = 0; g.gridy = 3; g.weightx = 0;
        form.add(label("Requests", Theme.BURGUNDY, 13), g);
        g.gridx = 1; g.weightx = 1;
        form.add(new InputBox(requests, null), g);

        g.gridx = 1; g.gridy = 4;
        form.add(totalLabel, g);

        // Keep the running total in sync as the dates change.
        DocumentListener recalc = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateTotal(); }
            @Override public void removeUpdate(DocumentEvent e) { updateTotal(); }
            @Override public void changedUpdate(DocumentEvent e) { updateTotal(); }
        };
        checkIn.getDocument().addDocumentListener(recalc);
        checkOut.getDocument().addDocumentListener(recalc);
        updateTotal();

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        root.add(scroll, BorderLayout.CENTER);

        JButton book = primaryButton("Request booking");
        JButton cancel = ghostButton("Cancel");
        cancel.addActionListener(e -> dispose());
        book.addActionListener(e -> submit());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(cancel);
        buttons.add(book);
        root.add(buttons, BorderLayout.SOUTH);
        setContentPane(root);
    }

    /** Recomputes and shows nights * nightly rate. */
    private void updateTotal() {
        Long nights = nightsOrNull();
        if (nights == null || nights <= 0) {
            totalLabel.setText("Total: —");
            return;
        }
        BigDecimal total = listing.getDailyRate().multiply(BigDecimal.valueOf(nights));
        totalLabel.setText("Total: " + formatMoney(total) + "  (" + nights + " nights)");
    }

    /** Nights between the two date fields, or null if they are invalid. */
    private Long nightsOrNull() {
        try {
            LocalDate in = LocalDate.parse(checkIn.getText().trim());
            LocalDate out = LocalDate.parse(checkOut.getText().trim());
            return java.time.temporal.ChronoUnit.DAYS.between(in, out);
        } catch (java.time.format.DateTimeParseException ex) {
            return null;
        }
    }

    private void submit() {
        Long nights = nightsOrNull();
        if (nights == null) {
            Toast.showError(this, "Use dates like " + LocalDate.now() + ".");
            return;
        }
        if (nights <= 0) {
            Toast.showError(this, "Check-out must be after check-in.");
            return;
        }
        int guestCount = (int) guests.getValue();
        if (guestCount > listing.getCapacity()) {
            Toast.showError(this, "Guests exceed the unit capacity (" + listing.getCapacity() + ").");
            return;
        }

        LocalDate in = LocalDate.parse(checkIn.getText().trim());
        LocalDate out = LocalDate.parse(checkOut.getText().trim());
        BigDecimal total = listing.getDailyRate().multiply(BigDecimal.valueOf(nights));

        // BUILDER + FACADE: assemble the booking, then book it in one call.
        Booking draft = new Booking.Builder(
                renterId, listing.getUnitId(), in, out, guestCount)
                .specialRequests(requests.getText().trim())
                .totalAmount(total)
                .build();
        String error = bookingService.book(draft);
        if (error == null) {
            Toast.show(this, "Booking requested! Awaiting approval.");
            if (onBooked != null) onBooked.run();
            dispose();
        } else {
            Toast.showError(this, error);
        }
    }
}

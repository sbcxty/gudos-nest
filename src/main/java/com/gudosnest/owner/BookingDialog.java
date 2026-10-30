package com.gudosnest.owner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.gudosnest.dao.BookingDAO;
import com.gudosnest.model.Booking;
import com.gudosnest.model.Payment;
import com.gudosnest.service.BookingService;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.Toast;

import static com.gudosnest.ui.UIHelpers.*;

/**
 * Booking details modal, styled like the design: an eyebrow ref, a guest
 * header card, a grid of key/value details, a special-requests block, and
 * the status-appropriate action buttons.
 */
public class BookingDialog extends JDialog {

    private final BookingService bookingService = new BookingService();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final Runnable onClosed;

    private final int bookingId;
    private Booking booking;

    private final JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

    public BookingDialog(OwnerView owner, Booking booking, Runnable onClosed) {
        super(windowOf(owner), java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        this.onClosed = onClosed;
        this.bookingId = booking.getBookingId();
        this.booking = booking;

        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(Theme.PAGE_BG);

        // Close button lives in the footer for the life of the dialog.
        actions.setOpaque(false);
        JButton close = ghostButton("Close");
        close.setForeground(Theme.MUTED);
        close.addActionListener(e -> dispose());
        actions.add(close);

        JPanel card = buildCard();
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.PAGE_BG);
        root.setBorder(new EmptyBorder(22, 22, 22, 22));
        root.add(card, BorderLayout.CENTER);
        setContentPane(root);

        setSize(700, 600);
        setLocationRelativeTo(owner);
        refreshActions();
    }

    // ---- White card -----------------------------------------------------
    private JPanel buildCard() {
        JPanel card = cardPanel(new BorderLayout(0, 18), 24, 20);
        card.add(buildHeader(), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(buildGuestCard());
        body.add(Box.createVerticalStrut(14));
        body.add(buildDetailsGrid());
        body.add(Box.createVerticalStrut(14));
        body.add(buildSpecialRequests());
        body.add(Box.createVerticalGlue());

        JScrollPane scroll = new JScrollPane(body);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);
        card.add(buildFooter(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildHeader() {
        JLabel eyebrow = eyebrow("BOOKING " + bookingRef(bookingId));
        JLabel title = new JLabel("Booking details");
        title.setFont(Theme.f(Font.BOLD, 24));
        title.setForeground(Theme.BURGUNDY);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        eyebrow.setAlignmentX(LEFT_ALIGNMENT);
        title.setAlignmentX(LEFT_ALIGNMENT);
        titles.add(eyebrow);
        titles.add(Box.createVerticalStrut(4));
        titles.add(title);

        JButton close = closeButton();

        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setOpaque(false);
        header.add(titles, BorderLayout.WEST);
        header.add(close, BorderLayout.EAST);
        return header;
    }

    private JPanel buildGuestCard() {
        JPanel guest = new JPanel(new BorderLayout(14, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Theme.FIELD_BG);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g.setColor(Theme.HAIRLINE);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g.dispose();
            }
        };
        guest.setOpaque(false);
        guest.setBorder(new EmptyBorder(14, 16, 14, 16));
        guest.add(avatar(initials(booking.getRenterName()), 44), BorderLayout.WEST);

        JLabel name = new JLabel(booking.getRenterName());
        name.setFont(Theme.f(Font.BOLD, 16));
        name.setForeground(Theme.BURGUNDY);
        String sub = booking.getRenterEmail() == null ? "Guest"
                : "Guest · " + booking.getRenterEmail();
        JLabel subtitle = label(sub, Theme.MUTED, 12);
        JPanel nameBlock = new JPanel();
        nameBlock.setOpaque(false);
        nameBlock.setLayout(new BoxLayout(nameBlock, BoxLayout.Y_AXIS));
        name.setAlignmentX(LEFT_ALIGNMENT);
        subtitle.setAlignmentX(LEFT_ALIGNMENT);
        nameBlock.add(name);
        nameBlock.add(Box.createVerticalStrut(2));
        nameBlock.add(subtitle);
        guest.add(nameBlock, BorderLayout.CENTER);

        JButton message = outlinedButton("Message guest");
        message.addActionListener(e ->
                Toast.show(this, "Messaging is not wired up yet."));
        guest.add(message, BorderLayout.EAST);
        return guest;
    }

    private JPanel buildDetailsGrid() {
        String dates = compactRange(booking.getCheckIn(), booking.getCheckOut());
        String year = yearOf(booking.getCreatedAt());
        if (!year.isEmpty()) dates = dates + ", " + year;

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);
        grid.add(detailCell("PROPERTY", valueLabel(booking.getPropertyName())));
        grid.add(detailCell("DATES", valueLabel(dates)));
        grid.add(detailCell("CHECK-IN", valueLabel(timePart(booking.getCheckIn()))));
        grid.add(detailCell("CHECK-OUT", valueLabel(timePart(booking.getCheckOut()))));
        grid.add(detailCell("PAYMENT", valueLabel(paymentSummary())));
        grid.add(detailCell("STATUS", dotChip(titleCase(booking.getBookingStatus()))));
        return grid;
    }

    private JPanel buildSpecialRequests() {
        String text = booking.getSpecialRequests() == null || booking.getSpecialRequests().isBlank()
                ? "—" : booking.getSpecialRequests();
        JPanel card = cardPanel(new BorderLayout(0, 8), 14, 12);
        JLabel key = new JLabel("SPECIAL REQUESTS");
        key.setFont(Theme.SECONDARY_BOLD);
        key.setForeground(Theme.MUTED);
        card.add(key, BorderLayout.NORTH);
        card.add(label(text, Theme.BURGUNDY, 14), BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFooter() {
        return actions;
    }

    // ---- Small builders -------------------------------------------------
    private JPanel detailCell(String key, JComponent value) {
        JPanel cell = cardPanel(new BorderLayout(0, 8), 14, 12);
        JLabel k = new JLabel(key);
        k.setFont(Theme.SECONDARY_BOLD);
        k.setForeground(Theme.MUTED);
        cell.add(k, BorderLayout.NORTH);
        cell.add(value, BorderLayout.CENTER);
        return cell;
    }

    private JLabel valueLabel(String text) {
        JLabel v = new JLabel(text == null || text.isBlank() ? "—" : text);
        v.setFont(Theme.f(Font.BOLD, 14));
        v.setForeground(Theme.BURGUNDY);
        return v;
    }

    private JButton closeButton() {
        JButton b = new JButton("✕") {
            private boolean hover;
            {
                setPreferredSize(new Dimension(36, 36));
                setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                    @Override public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(hover ? Theme.HOVER_BG : Theme.WHITE);
                g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g.setColor(Theme.HAIRLINE);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g.setColor(Theme.MUTED);
                g.setFont(Theme.f(Font.PLAIN, 15));
                String x = "✕";
                java.awt.FontMetrics fm = g.getFontMetrics();
                g.drawString(x, (getWidth() - fm.stringWidth(x)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
        };
        b.addActionListener(e -> dispose());
        return b;
    }

    /** Gradient circle with initials. */
    private JComponent avatar(String initials, int size) {
        return new JComponent() {
            { setPreferredSize(new Dimension(size, size)); }
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0, 0, Theme.LIGHT_BLUE,
                        getWidth(), getHeight(), Theme.VIOLET));
                g.fillOval(0, 0, getWidth(), getHeight());
                g.setColor(Theme.WHITE);
                g.setFont(Theme.f(Font.BOLD, size / 2 - 2));
                java.awt.FontMetrics fm = g.getFontMetrics();
                g.drawString(initials,
                        (getWidth() - fm.stringWidth(initials)) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g.dispose();
            }
        };
    }

    /** "PHP 840.00 · Paid" for the PAYMENT cell. */
    private String paymentSummary() {
        List<Payment> payments = bookingDAO.getPaymentsForBooking(bookingId);
        String status = payments.isEmpty() ? "Unpaid" : titleCase(payments.get(0).getPaymentStatus());
        return formatMoney(booking.getTotalAmount()) + " · " + status;
    }

    // ---- Actions --------------------------------------------------------
    /** Rebuilds the action buttons for the booking's current status. */
    private void refreshActions() {
        for (java.awt.Component comp : actions.getComponents()) {
            if (comp instanceof JButton b && !"Close".equals(b.getText())) {
                actions.remove(b);
            }
        }
        List<String> allowed = bookingService.allowedActions(booking.getBookingStatus());
        // Order so the primary (approve) ends up right-most.
        allowed.stream()
                .sorted((a, b) -> Integer.compare(actionRank(a), actionRank(b)))
                .forEach(action -> {
                    JButton b = actionButton(action);
                    b.addActionListener(e -> runAction(action));
                    actions.add(b);
                });
        actions.revalidate();
        actions.repaint();
    }

    private static int actionRank(String action) {
        return switch (action) {
            case "cancel"   -> 0;
            case "complete" -> 1;
            case "reject"   -> 2;
            case "approve"  -> 3;
            default         -> 4;
        };
    }

    private JButton actionButton(String action) {
        return switch (action) {
            case "approve"  -> accentButton("✓  Approve");
            case "reject"   -> dangerButton("Reject");
            case "complete" -> outlinedButton("Mark as completed");
            default         -> ghostButton("Cancel booking");
        };
    }

    private void runAction(String action) {
        String error = bookingService.changeStatus(bookingId, action);
        if (error != null) {
            Toast.showError(this, error);
            return;
        }
        booking = bookingDAO.getDetails(bookingId);
        Toast.show(this, "Booking " + pastTense(action) + ".");
        // Refresh the details shown in the open modal.
        rebuildDetails();
        refreshActions();
        if (onClosed != null) onClosed.run();
    }

    /** Rebuilds the body after a status change (keeps the modal in sync). */
    private void rebuildDetails() {
        getContentPane().removeAll();
        getContentPane().add(buildCard());
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private static String pastTense(String action) {
        return switch (action) {
            case "approve"  -> "approved";
            case "reject"   -> "rejected";
            case "cancel"   -> "cancelled";
            case "complete" -> "marked completed";
            default         -> action;
        };
    }
}

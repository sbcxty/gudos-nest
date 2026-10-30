package com.gudosnest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.gudosnest.controller.BookingController;
import com.gudosnest.core.Session;
import com.gudosnest.dao.BookingDAO;
import com.gudosnest.dao.MessagesDAO;
import com.gudosnest.dao.PaymentDAO;
import com.gudosnest.dao.UnitDAO;
import com.gudosnest.model.Booking;
import com.gudosnest.model.Payment;
import com.gudosnest.model.User;

/**
 * FACADE: one simple entry point for everything that changes a booking.
 *
 * The UI never talks to BookingDAO / PaymentDAO / MessagesDAO directly for
 * booking flows - it calls a single method here, and this class orchestrates
 * the ordered steps behind the scenes:
 *
 *   book()    - check availability -> save booking -> record pending payment
 *               -> notify the property owner.
 *   cancel()  - mark cancelled (sets cancelled_at) -> request a refund ->
 *               notify the counter-party.
 *   approve / reject / complete - simple status flips.
 */
public class BookingService {

    private final BookingDAO bookingDAO = new BookingDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final MessagesDAO messagesDAO = new MessagesDAO();
    private final UnitDAO unitDAO = new UnitDAO();
    private final BookingController rules = new BookingController();

    /** Which actions a booking in this status may take (delegated to the rules). */
    public List<String> allowedActions(String status) {
        return rules.allowedActions(status);
    }

    /**
     * Books a unit for a renter in one call. The draft is expected to come
     * from Booking.Builder. Returns null on success, or an error message.
     */
    public String book(Booking draft) {
        // 1. Check the unit is free for those dates.
        if (bookingDAO.hasOverlap(draft.getUnitId(), draft.getCheckIn(), draft.getCheckOut())) {
            return "That unit is already booked for those dates.";
        }

        // Work out the total (the caller may pass one; otherwise compute it).
        BigDecimal total = draft.getTotalAmount();
        if (total == null) {
            BigDecimal rate = unitDAO.getDailyRate(draft.getUnitId());
            total = rate.multiply(BigDecimal.valueOf(nights(draft)));
        }

        // 2. Save the booking.
        int bookingId = bookingDAO.insert(
                draft.getRenterId(), draft.getUnitId(),
                draft.getCheckIn(), draft.getCheckOut(),
                draft.getNumberOfRenters(), draft.getSpecialRequests(),
                total.toPlainString());
        if (bookingId < 0) {
            return "Could not save the booking.";
        }

        // 3. Record a pending payment for it.
        paymentDAO.insertPending(bookingId, total);

        // 4. Notify the property owner.
        int ownerId = unitDAO.ownerIdForUnit(draft.getUnitId());
        if (ownerId > 0 && ownerId != draft.getRenterId()) {
            messagesDAO.insert(draft.getRenterId(), ownerId, bookingId,
                    "New booking request for unit " + draft.getUnitId() + ".");
        }
        return null;
    }

    /**
     * Applies an action (approve / reject / cancel / complete).
     * Cancel runs the full cancel flow (status + refund + notify).
     * Returns null on success, or an error message.
     */
    public String changeStatus(int bookingId, String action) {
        Booking booking = bookingDAO.getDetails(bookingId);
        if (booking == null) {
            return "Booking not found.";
        }
        if (!rules.allowedActions(booking.getBookingStatus()).contains(action)) {
            return "That action is not available for this booking.";
        }
        if ("cancel".equals(action)) {
            return cancel(booking);
        }

        String newStatus = switch (action) {
            case "approve"  -> "approved";
            case "reject"   -> "rejected";
            case "complete" -> "completed";
            default         -> action;
        };
        return bookingDAO.updateStatus(bookingId, newStatus)
                ? null : "Could not update the booking.";
    }

    // ---- cancel: status -> refund -> notify -------------------------------
    private String cancel(Booking booking) {
        // 1. Mark cancelled (this also stamps cancelled_at, as the DB requires).
        if (!bookingDAO.updateStatus(booking.getBookingId(), "cancelled")) {
            return "Could not cancel the booking.";
        }

        // 2. Request a refund for any payment already on the booking.
        List<Payment> payments = bookingDAO.getPaymentsForBooking(booking.getBookingId());
        if (!payments.isEmpty()) {
            bookingDAO.insertRefund(
                    payments.get(0).getPaymentId(),
                    BigDecimal.ZERO,
                    "Booking cancelled.");
        }

        // 3. Notify the counter-party (renter <-> owner).
        User me = Session.getInstance().getUser();
        int myId = me != null ? me.getUserId() : booking.getRenterId();
        int ownerId = unitDAO.ownerIdForUnit(booking.getUnitId());
        int receiver = (myId == booking.getRenterId()) ? ownerId : booking.getRenterId();
        if (receiver > 0 && receiver != myId) {
            messagesDAO.insert(myId, receiver, booking.getBookingId(),
                    "Booking " + booking.getBookingId() + " was cancelled.");
        }
        return null;
    }

    // ---- helpers ---------------------------------------------------------
    /** Nights between the stored "yyyy-MM-dd HH:mm:ss" check-in/check-out. */
    private static long nights(Booking booking) {
        LocalDate in  = LocalDate.parse(booking.getCheckIn().substring(0, 10));
        LocalDate out = LocalDate.parse(booking.getCheckOut().substring(0, 10));
        return ChronoUnit.DAYS.between(in, out);
    }
}

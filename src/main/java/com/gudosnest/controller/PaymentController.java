package com.gudosnest.controller;

import com.gudosnest.dao.PaymentDAO;

/**
 * Payment rules: validating a guest payment entry before it is saved.
 */
public class PaymentController {

    private final PaymentDAO paymentDAO = new PaymentDAO();

    /**
     * Records a paid guest payment (and its matching income transaction).
     * Returns null on success, or an error message the UI can show.
     */
    public String addPayment(int bookingId, int propertyId, String amount,
                             String paymentType, String receiptUrl) {
        if (receiptUrl == null || receiptUrl.isBlank()) {
            return "A receipt link is required for a paid payment.";
        }
        if (!"rental_payment".equals(paymentType) && !"damage_fee".equals(paymentType)) {
            return "Payment type must be rental payment or damage fee.";
        }
        if (!isPositive(amount)) {
            return "Amount must be a positive number.";
        }

        boolean ok = paymentDAO.insertPaid(bookingId, propertyId, amount, paymentType, receiptUrl.trim());
        return ok ? null : "Could not record the payment.";
    }

    /** True when the text is a number greater than zero. */
    static boolean isPositive(String amount) {
        try {
            return new java.math.BigDecimal(amount).signum() > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

package com.gudosnest.model;

import java.math.BigDecimal;

/** One row from the Payments table. */
public class Payment {
    private final int paymentId;
    private final int bookingId;
    private final BigDecimal amount;
    private final String receiptUrl; // may be null
    private final String paymentType;   // rental_payment | damage_fee
    private final String paymentStatus; // pending | paid
    private final String paymentDate;   // display string, may be null

    public Payment(int paymentId, int bookingId, BigDecimal amount,
                   String receiptUrl, String paymentType,
                   String paymentStatus, String paymentDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.receiptUrl = receiptUrl;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId()      { return paymentId; }
    public int getBookingId()      { return bookingId; }
    public BigDecimal getAmount()  { return amount; }
    public String getReceiptUrl()  { return receiptUrl; }
    public String getPaymentType() { return paymentType; }
    public String getPaymentStatus(){ return paymentStatus; }
    public String getPaymentDate() { return paymentDate; }
}

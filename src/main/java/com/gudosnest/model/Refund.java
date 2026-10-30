package com.gudosnest.model;

import java.math.BigDecimal;

/** One row from the Refunds table. */
public class Refund {
    private final int refundId;
    private final int paymentId;
    private final BigDecimal cancellationFee;
    private final String cancellationReason; // may be null
    private final String refundStatus; // requested|approved|rejected|processed
    private final String requestedAt;  // display string
    private final String processedAt;  // display string, may be null

    public Refund(int refundId, int paymentId, BigDecimal cancellationFee,
                  String cancellationReason, String refundStatus,
                  String requestedAt, String processedAt) {
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.cancellationFee = cancellationFee;
        this.cancellationReason = cancellationReason;
        this.refundStatus = refundStatus;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
    }

    public int getRefundId()            { return refundId; }
    public int getPaymentId()           { return paymentId; }
    public BigDecimal getCancellationFee(){ return cancellationFee; }
    public String getCancellationReason(){ return cancellationReason; }
    public String getRefundStatus()     { return refundStatus; }
    public String getRequestedAt()      { return requestedAt; }
    public String getProcessedAt()      { return processedAt; }
}

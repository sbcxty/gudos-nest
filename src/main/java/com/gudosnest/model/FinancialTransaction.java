package com.gudosnest.model;

import java.math.BigDecimal;

/**
 * One row from the FinancialTransactions table, plus the joined
 * property name that list queries pull in for the UI.
 */
public class FinancialTransaction {
    private final int transactionId;
    private final int propertyId;
    private final Integer paymentId; // may be null
    private final String transactionType; // rental_income|damage_fee_income|refund|maintenance_expense|supply_expense
    private final BigDecimal amount;
    private final String transactionDate; // display string

    // Joined display name.
    private final String propertyName;

    public FinancialTransaction(int transactionId, int propertyId,
                                Integer paymentId, String transactionType,
                                BigDecimal amount, String transactionDate,
                                String propertyName) {
        this.transactionId = transactionId;
        this.propertyId = propertyId;
        this.paymentId = paymentId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.propertyName = propertyName;
    }

    public int getTransactionId()   { return transactionId; }
    public int getPropertyId()      { return propertyId; }
    public Integer getPaymentId()   { return paymentId; }
    public String getTransactionType(){ return transactionType; }
    public BigDecimal getAmount()   { return amount; }
    public String getTransactionDate(){ return transactionDate; }
    public String getPropertyName() { return propertyName; }
}

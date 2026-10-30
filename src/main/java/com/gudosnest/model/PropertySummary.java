package com.gudosnest.model;

import java.math.BigDecimal;

/** Per-property financial summary used by the Income & Expenses screen. */
public class PropertySummary {
    private final int propertyId;
    private final String propertyName;
    private final BigDecimal acquisitionCost;
    private final BigDecimal totalIncome;  // rental + damage-fee income
    private final BigDecimal totalExpenses; // refund + maintenance + supply

    public PropertySummary(int propertyId, String propertyName,
                           BigDecimal acquisitionCost,
                           BigDecimal totalIncome, BigDecimal totalExpenses) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.acquisitionCost = acquisitionCost;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
    }

    public int getPropertyId()      { return propertyId; }
    public String getPropertyName() { return propertyName; }
    public BigDecimal getAcquisitionCost(){ return acquisitionCost; }
    public BigDecimal getTotalIncome(){ return totalIncome; }
    public BigDecimal getTotalExpenses(){ return totalExpenses; }

    /** Income minus expenses. */
    public BigDecimal getNet() { return totalIncome.subtract(totalExpenses); }

    /** True when net income has covered the acquisition cost. */
    public boolean isRecovered() { return getNet().compareTo(acquisitionCost) >= 0; }
}

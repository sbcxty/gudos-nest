package com.gudosnest.model;

import java.math.BigDecimal;

/** A bookable unit shown to renters (unit joined with its property name). */
public class UnitListing {
    private final int unitId;
    private final int propertyId;
    private final String propertyName;
    private final String unitName;
    private final int capacity;
    private final BigDecimal dailyRate;
    private final String unitStatus;

    public UnitListing(int unitId, int propertyId, String propertyName,
                       String unitName, int capacity, BigDecimal dailyRate,
                       String unitStatus) {
        this.unitId = unitId;
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.unitName = unitName;
        this.capacity = capacity;
        this.dailyRate = dailyRate;
        this.unitStatus = unitStatus;
    }

    public int getUnitId()       { return unitId; }
    public int getPropertyId()   { return propertyId; }
    public String getPropertyName(){ return propertyName; }
    public String getUnitName()  { return unitName; }
    public int getCapacity()     { return capacity; }
    public BigDecimal getDailyRate(){ return dailyRate; }
    public String getUnitStatus() { return unitStatus; }
}

package com.gudosnest.model;

import java.math.BigDecimal;

/**
 * A property prepared for the owner's "property cards" screen.
 * Adds the display info the cards need (unit count, cheapest rate,
 * and the single unit id when the property has exactly one unit).
 */
public class PropertyCard {
    private final int propertyId;
    private final String propertyName;
    private final String location;
    private final int unitCount;
    private final BigDecimal minDailyRate; // cheapest unit rate, may be null
    private final int singleUnitId;        // only when unitCount == 1, else -1

    public PropertyCard(int propertyId, String propertyName, String location,
                        int unitCount, BigDecimal minDailyRate, int singleUnitId) {
        this.propertyId = propertyId;
        this.propertyName = propertyName;
        this.location = location;
        this.unitCount = unitCount;
        this.minDailyRate = minDailyRate;
        this.singleUnitId = singleUnitId;
    }

    public int getPropertyId()      { return propertyId; }
    public String getPropertyName() { return propertyName; }
    public String getLocation()     { return location; }
    public int getUnitCount()       { return unitCount; }
    public BigDecimal getMinDailyRate() { return minDailyRate; }

    /** True when the property is a single unit ("whole property" rental). */
    public boolean isSingleUnit()   { return unitCount == 1 && singleUnitId > 0; }

    /** The lone unit id for a single-unit property, or -1 if not single. */
    public int getSingleUnitId()    { return singleUnitId; }
}

package com.gudosnest.model;

import java.math.BigDecimal;

/**
 * One row from the Units table.
 *
 * PROTOTYPE: a Unit can be cloned into a fresh, not-yet-saved copy
 * (unitId reset to 0) that the owner can rename and re-insert. This lets
 * the UI duplicate an existing unit quickly instead of re-typing it.
 */
public class Unit implements Cloneable {
    private final int unitId;
    private final int propertyId;
    private final String unitName;
    private final String unitDescription; // may be null
    private final int capacity;
    private final BigDecimal dailyRate;
    private final String unitStatus; // unoccupied | occupied | maintenance

    public Unit(int unitId, int propertyId, String unitName,
                String unitDescription, int capacity,
                BigDecimal dailyRate, String unitStatus) {
        this.unitId = unitId;
        this.propertyId = propertyId;
        this.unitName = unitName;
        this.unitDescription = unitDescription;
        this.capacity = capacity;
        this.dailyRate = dailyRate;
        this.unitStatus = unitStatus;
    }

    public int getUnitId()           { return unitId; }
    public int getPropertyId()       { return propertyId; }
    public String getUnitName()      { return unitName; }
    public String getUnitDescription(){ return unitDescription; }
    public int getCapacity()         { return capacity; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public String getUnitStatus()    { return unitStatus; }

    // ---- PROTOTYPE ------------------------------------------------------
    /**
     * Returns a detached copy of this unit with its id reset to 0 so it can
     * be saved as a brand-new unit. Same description, capacity, rate, and
     * status are carried over.
     */
    @Override
    public Unit clone() {
        return new Unit(0, this.propertyId, this.unitName,
                this.unitDescription, this.capacity, this.dailyRate,
                this.unitStatus);
    }

    /**
     * Convenience for the "duplicate unit" flow: a clone renamed to a name
     * the caller supplies (e.g. "Studio A101" -> "Studio A102").
     */
    public Unit copyWithNewName(String newName) {
        Unit copy = this.clone();
        return new Unit(0, copy.propertyId, newName, copy.unitDescription,
                copy.capacity, copy.dailyRate, copy.unitStatus);
    }
}

package com.gudosnest.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Property {

    private final int propertyId;          // 0 until the database assigns it
    private final int ownerId;
    private final String propertyName;
    private final String location;
    private final BigDecimal acquisitionCost;
    private final LocalDate acquisitionDate;   // optional, may be null

    /** Used by PropertyDAO when reading rows from the database. */
    public Property(int propertyId, int ownerId, String propertyName, String location,
                    BigDecimal acquisitionCost, LocalDate acquisitionDate) {
        this.propertyId = propertyId;
        this.ownerId = ownerId;
        this.propertyName = propertyName;
        this.location = location;
        this.acquisitionCost = acquisitionCost;
        this.acquisitionDate = acquisitionDate;
    }

    private Property(Builder b) {
        this(0, b.ownerId, b.propertyName, b.location, b.acquisitionCost, b.acquisitionDate);
    }

    public int getPropertyId()             { return propertyId; }
    public int getOwnerId()                { return ownerId; }
    public String getPropertyName()        { return propertyName; }
    public String getLocation()            { return location; }
    public BigDecimal getAcquisitionCost() { return acquisitionCost; }
    public LocalDate getAcquisitionDate()  { return acquisitionDate; }

    /** BUILDER: required parts in the constructor, optional parts chained. */
    public static class Builder {
        private final int ownerId;
        private final String propertyName;
        private final String location;
        private final BigDecimal acquisitionCost;
        private LocalDate acquisitionDate;

        public Builder(int ownerId, String propertyName, String location,
                       BigDecimal acquisitionCost) {
            this.ownerId = ownerId;
            this.propertyName = propertyName;
            this.location = location;
            this.acquisitionCost = acquisitionCost;
        }

        public Builder acquisitionDate(LocalDate d) {
            this.acquisitionDate = d;
            return this;
        }

        public Property build() {
            if (propertyName == null || propertyName.isBlank())
                throw new IllegalArgumentException("Property name is required.");
            if (location == null || location.isBlank())
                throw new IllegalArgumentException("Location is required.");
            if (acquisitionCost == null || acquisitionCost.signum() < 0)
                throw new IllegalArgumentException("Cost cannot be negative.");
            return new Property(this);
        }
    }
}
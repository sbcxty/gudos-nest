package com.gudosnest.model;

import java.math.BigDecimal;

/**
 * One row from the Maintenance table, plus joined display names
 * (property / unit) that list queries pull in for the UI.
 */
public class Maintenance {
    private final int maintenanceId;
    private final int propertyId;
    private final Integer unitId; // may be null
    private final Integer itemId; // may be null
    private final String maintenanceDescription;
    private final BigDecimal maintenanceCost;
    private final String maintenanceStatus; // pending|in_progress|completed
    private final String startedAt;  // display string
    private final String completedAt; // display string, may be null

    // Joined display names.
    private final String propertyName;
    private final String unitName; // "Whole property" when unitId is null

    public Maintenance(int maintenanceId, int propertyId, Integer unitId,
                       Integer itemId, String maintenanceDescription,
                       BigDecimal maintenanceCost, String maintenanceStatus,
                       String startedAt, String completedAt,
                       String propertyName, String unitName) {
        this.maintenanceId = maintenanceId;
        this.propertyId = propertyId;
        this.unitId = unitId;
        this.itemId = itemId;
        this.maintenanceDescription = maintenanceDescription;
        this.maintenanceCost = maintenanceCost;
        this.maintenanceStatus = maintenanceStatus;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.propertyName = propertyName;
        this.unitName = unitName;
    }

    public int getMaintenanceId()   { return maintenanceId; }
    public int getPropertyId()      { return propertyId; }
    public Integer getUnitId()      { return unitId; }
    public Integer getItemId()      { return itemId; }
    public String getMaintenanceDescription(){ return maintenanceDescription; }
    public BigDecimal getMaintenanceCost(){ return maintenanceCost; }
    public String getMaintenanceStatus(){ return maintenanceStatus; }
    public String getStartedAt()    { return startedAt; }
    public String getCompletedAt()  { return completedAt; }
    public String getPropertyName() { return propertyName; }
    public String getUnitName()     { return unitName; }
}

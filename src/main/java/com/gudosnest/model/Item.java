package com.gudosnest.model;

import java.math.BigDecimal;

/**
 * One row from the Items table (inventory), plus the joined display
 * names (property / unit) that list queries pull in for the UI.
 */
public class Item {
    private final int itemId;
    private final int propertyId;
    private final Integer unitId; // null = shared / whole property
    private final String itemName;
    private final String itemType;    // consumable | equipment
    private final int quantity;
    private final BigDecimal unitPrice;
    private final String itemCondition; // good|damaged|maintenance|missing

    // Joined display names.
    private final String propertyName;
    private final String unitName; // "Whole property" when unitId is null

    public Item(int itemId, int propertyId, Integer unitId, String itemName,
                String itemType, int quantity, BigDecimal unitPrice,
                String itemCondition, String propertyName, String unitName) {
        this.itemId = itemId;
        this.propertyId = propertyId;
        this.unitId = unitId;
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.itemCondition = itemCondition;
        this.propertyName = propertyName;
        this.unitName = unitName;
    }

    public int getItemId()       { return itemId; }
    public int getPropertyId()   { return propertyId; }
    public Integer getUnitId()   { return unitId; }
    public String getItemName()  { return itemName; }
    public String getItemType()  { return itemType; }
    public int getQuantity()     { return quantity; }
    public BigDecimal getUnitPrice(){ return unitPrice; }
    public String getItemCondition(){ return itemCondition; }
    public String getPropertyName(){ return propertyName; }
    public String getUnitName()  { return unitName; }
}

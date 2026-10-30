package com.gudosnest.controller;

import java.util.Set;

import com.gudosnest.dao.ItemsDAO;

/** Inventory rules: validating an item update before it is saved. */
public class InventoryController {

    private static final Set<String> CONDITIONS =
            Set.of("good", "damaged", "maintenance", "missing");

    private final ItemsDAO itemsDAO = new ItemsDAO();

    /**
     * Updates an item's quantity and condition.
     * Returns null on success, or an error message the UI can show.
     */
    public String updateItem(int itemId, int quantity, String condition) {
        if (quantity < 0) {
            return "Quantity cannot be negative.";
        }
        if (!CONDITIONS.contains(condition)) {
            return "Unknown condition: " + condition;
        }

        boolean ok = itemsDAO.update(itemId, quantity, condition);
        return ok ? null : "Could not update the item.";
    }
}

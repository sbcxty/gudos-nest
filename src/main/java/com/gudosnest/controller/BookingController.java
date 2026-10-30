package com.gudosnest.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Booking rules: which status changes are allowed for a given status.
 * This class holds no state and touches no database - it is pure logic
 * that the BookingService Facade reuses.
 */
public class BookingController {

    /** Returns the actions a booking in the given status can take. */
    public List<String> allowedActions(String status) {
        List<String> actions = new ArrayList<>();
        switch (status == null ? "" : status) {
            case "pending"  -> { actions.add("approve"); actions.add("reject"); actions.add("cancel"); }
            case "approved" -> { actions.add("cancel"); actions.add("complete"); }
            default -> { /* rejected / cancelled / completed: no further actions */ }
        }
        return actions;
    }
}

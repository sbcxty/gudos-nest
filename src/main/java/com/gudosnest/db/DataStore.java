package com.gudosnest.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * All reads (and the demo "save a property" write) on the gudos_nest schema.
 * Panels call these methods to fill tables and summary cards.
 */
public final class DataStore {

    // ------------------------------------------------------------------
    // Reads used by the Owner portal UI (RETRIEVE half of the demo)
    // ------------------------------------------------------------------
    public static java.util.ArrayList<String[]> getProperties() {
        return rows("SELECT p.property_name, p.location, IF(p.has_units,'Multiple units','Whole house'),"
                + " p.capacity, CONCAT('PHP ', FORMAT(p.daily_rate,2)),"
                + " COALESCE((SELECT GROUP_CONCAT(DISTINCT u.unit_status) FROM units u WHERE u.property_id=p.property_id),'whole-house')"
                + " FROM properties p ORDER BY p.property_id");
    }

    public static java.util.ArrayList<String[]> getBookings() {
        return rows("SELECT b.booking_id, r.full_name, p.property_name, IFNULL(u.unit_name,'Whole property'),"
                + " DATE_FORMAT(b.check_in,'%b %e %l:%i %p'), DATE_FORMAT(b.check_out,'%b %e %l:%i %p'),"
                + " b.number_of_renters, UPPER(b.booking_status), CONCAT('PHP ', FORMAT(b.total_amount,2))"
                + " FROM bookings b JOIN users r ON r.user_id=b.renter_id"
                + " JOIN properties p ON p.property_id=b.property_id LEFT JOIN units u ON u.unit_id=b.unit_id"
                + " ORDER BY b.booking_id");
    }

    public static java.util.ArrayList<String[]> getInventory() {
        return rows("SELECT i.item_name, p.property_name, i.item_type, i.quantity,"
                + " CONCAT('PHP ', FORMAT(i.unit_price,2)), UPPER(i.item_condition)"
                + " FROM inventory i JOIN properties p ON p.property_id=i.property_id"
                + " ORDER BY i.item_id");
    }

    public static java.util.ArrayList<String[]> getMaintenance() {
        return rows("SELECT m.maintenance_description, p.property_name, CONCAT('PHP ', FORMAT(m.maintenance_cost,2)),"
                + " UPPER(m.maintenance_status), DATE_FORMAT(m.started_at,'%b %e, %Y')"
                + " FROM maintenance m JOIN properties p ON p.property_id=m.property_id"
                + " ORDER BY m.maintenance_id");
    }

    public static java.util.ArrayList<String[]> rowsPayments() {
        return rows("SELECT CAST(b.booking_id AS CHAR), CONCAT('PHP ', FORMAT(p2.amount,2)),"
                + " UPPER(p2.payment_type), UPPER(p2.payment_status),"
                + " IFNULL(DATE_FORMAT(p2.payment_date,'%b %e, %Y'), 'not paid yet')"
                + " FROM payments p2 JOIN bookings b ON b.booking_id=p2.booking_id"
                + " ORDER BY p2.payment_id");
    }

    public static java.util.ArrayList<String[]> getPropertySummaries() {
        return rows("SELECT p.property_name, CONCAT('PHP ', FORMAT(p.acquisition_cost,2)),"
                + " SUM(CASE WHEN f.transaction_type IN ('rental_income','other_income') THEN f.amount ELSE 0 END),"
                + " SUM(CASE WHEN f.transaction_type IN ('refund','maintenance_expense','supply_expense') THEN f.amount ELSE 0 END)"
                + " FROM properties p LEFT JOIN financialtransactions f ON f.property_id=p.property_id"
                + " GROUP BY p.property_id, p.property_name, p.acquisition_cost ORDER BY p.property_id");
    }

    public static int count(String sql) {
        try (var c = Database.connect(); var s = c.createStatement()) {
            var rs = s.executeQuery(sql);
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ------------------------------------------------------------------
    // SAVE half of the demo: inserts a new property row from the UI.
    // ------------------------------------------------------------------
    public static boolean insertSampleProperty(String name, String location, String rate, int capacity, boolean hasUnits) {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(
                 "INSERT INTO properties (owner_id, property_name, location, acquisition_cost,"
                 + " acquisition_date, has_units, daily_rate, capacity) VALUES (?,?,?,?,?,?,?,?)")) {
            ps.setInt(1, 1);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setBigDecimal(4, new java.math.BigDecimal("1000000"));
            ps.setString(5, "2026-01-01");
            ps.setBoolean(6, hasUnits);
            ps.setBigDecimal(7, new java.math.BigDecimal(rate));
            ps.setInt(8, capacity);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ------------------------------------------------------------------
    // Result-set -> String[] helper shared by every read
    // ------------------------------------------------------------------
    private static java.util.ArrayList<String[]> rows(String sql) {
        java.util.ArrayList<String[]> out = new java.util.ArrayList<>();
        try (Connection c = Database.connect(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            int n = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                String[] row = new String[n];
                for (int i = 0; i < n; i++) row[i] = rs.getString(i + 1);
                out.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    private DataStore() {}
}

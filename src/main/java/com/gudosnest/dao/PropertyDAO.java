package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.Property;
import com.gudosnest.model.PropertyCard;

/**
 * Data Access Object for the Properties table.
 * Every method that touches the Properties table lives here.
 */
public class PropertyDAO {

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /**
     * Returns every property as a display card (with unit count and
     * cheapest rate). Pass ownerId to limit to one owner, or null for all.
     */
    public ArrayList<PropertyCard> getCards(Integer ownerId) {
        String sql =
            "SELECT p.property_id, "
          + "       p.property_name, "
          + "       p.location, "
          + "       COUNT(u.unit_id) AS unit_count, "
          + "       MIN(u.daily_rate) AS min_rate, "
          + "       MIN(u.unit_id) AS first_unit_id "
          + "FROM Properties p "
          + "LEFT JOIN Units u ON u.property_id = p.property_id "
          + (ownerId == null ? "" : "WHERE p.owner_id = ? ")
          + "GROUP BY p.property_id, p.property_name, p.location "
          + "ORDER BY p.property_id";

        Object[] params = ownerId == null ? new Object[0] : new Object[]{ ownerId };

        return QueryHelper.query(sql, rs -> {
            int unitCount = rs.getInt("unit_count");
            int firstUnitId = rs.getInt("first_unit_id");
            BigDecimal minRate = rs.getBigDecimal("min_rate");
            return new PropertyCard(
                rs.getInt("property_id"),
                rs.getString("property_name"),
                rs.getString("location"),
                unitCount,
                minRate,
                unitCount == 1 ? firstUnitId : -1);
        }, params);
    }

    /** Returns every property as a plain entity. */
    public ArrayList<Property> getAll() {
        return QueryHelper.query(
            "SELECT property_id, owner_id, property_name, location, "
          + "       acquisition_cost, acquisition_date "
          + "FROM Properties "
          + "ORDER BY property_id",
            PropertyDAO::mapProperty);
    }

    /** Returns one property by its id, or null if not found. */
    public Property getById(int propertyId) {
        ArrayList<Property> list = QueryHelper.query(
            "SELECT property_id, owner_id, property_name, location, "
          + "       acquisition_cost, acquisition_date "
          + "FROM Properties WHERE property_id = ?",
            PropertyDAO::mapProperty, propertyId);
        return list.isEmpty() ? null : list.get(0);
    }

    /** Returns the total number of properties. */
    public int countAll() {
        return QueryHelper.count("SELECT COUNT(*) FROM Properties");
    }

    /** Returns the number of properties owned by a specific owner. */
    public int countForOwner(int ownerId) {
        return QueryHelper.count(
            "SELECT COUNT(*) FROM Properties WHERE owner_id = ?", ownerId);
    }

    // ---------------------------------------------------------------
    // WRITE
    // ---------------------------------------------------------------

    /** Inserts a property built with Property.Builder. Returns true on success. */
    public boolean insert(Property p) {
        String sql = "INSERT INTO Properties "
                   + "(owner_id, property_name, location, acquisition_cost, acquisition_date) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, p.getOwnerId());
            ps.setString(2, p.getPropertyName());
            ps.setString(3, p.getLocation());
            ps.setBigDecimal(4, p.getAcquisitionCost());
            if (p.getAcquisitionDate() == null) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(5, java.sql.Date.valueOf(p.getAcquisitionDate()));
            }
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Older version that takes plain text values.
     * Kept in case other code (like sample data) still calls it.
     */
    public boolean insert(int ownerId, String name, String location,
                          String acquisitionCost, String acquisitionDate) {
        String sql = "INSERT INTO Properties "
                   + "(owner_id, property_name, location, acquisition_cost, acquisition_date) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, ownerId);
            ps.setString(2, name);
            ps.setString(3, location);
            ps.setBigDecimal(4, new BigDecimal(acquisitionCost));
            ps.setString(5, acquisitionDate);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------------------------------------------------------
    // DELETE
    // ---------------------------------------------------------------

    /**
     * Deletes a property together with everything that depends on it, in
     * child-first order inside one transaction so the foreign keys hold:
     *
     *   Refunds -> FinancialTransactions -> Messages -> Payments -> Bookings
     *   -> Maintenance -> Items -> Units -> Properties.
     *
     * Returns true on success; rolls back and returns false on failure.
     */
    public boolean delete(int propertyId) {
        // Subqueries that resolve this property's rows while they still exist.
        final String units    = "SELECT unit_id FROM Units WHERE property_id = ?";
        final String bookings = "SELECT booking_id FROM Bookings WHERE unit_id IN (" + units + ")";
        final String payments = "SELECT payment_id FROM Payments WHERE booking_id IN (" + bookings + ")";

        try (Connection c = DBConnection.getConnection()) {
            try {
                c.setAutoCommit(false);

                exec(c, "DELETE FROM Refunds WHERE payment_id IN (" + payments + ")",
                        propertyId);
                exec(c, "DELETE FROM FinancialTransactions WHERE property_id = ? "
                        + "OR payment_id IN (" + payments + ")",
                        propertyId, propertyId);
                exec(c, "DELETE FROM Messages WHERE booking_id IN (" + bookings + ")",
                        propertyId);
                exec(c, "DELETE FROM Payments WHERE booking_id IN (" + bookings + ")",
                        propertyId);
                exec(c, "DELETE FROM Bookings WHERE unit_id IN (" + units + ")",
                        propertyId);
                exec(c, "DELETE FROM Maintenance WHERE property_id = ?", propertyId);
                exec(c, "DELETE FROM Items WHERE property_id = ?", propertyId);
                exec(c, "DELETE FROM Units WHERE property_id = ?", propertyId);
                exec(c, "DELETE FROM Properties WHERE property_id = ?", propertyId);

                c.commit();
                return true;

            } catch (SQLException e) {
                rollbackQuietly(c);
                e.printStackTrace();
                return false;
            } finally {
                restoreAutoCommit(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void exec(Connection c, String sql, Integer... params) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setInt(i + 1, params[i]);
            }
            ps.executeUpdate();
        }
    }

    private static void rollbackQuietly(Connection c) {
        try { c.rollback(); } catch (SQLException ignored) { }
    }

    private static void restoreAutoCommit(Connection c) {
        try { c.setAutoCommit(true); } catch (SQLException ignored) { }
    }

    // Maps one ResultSet row to a Property entity.
    private static Property mapProperty(ResultSet rs) throws SQLException {
        java.sql.Date d = rs.getDate("acquisition_date");
        return new Property(
            rs.getInt("property_id"),
            rs.getInt("owner_id"),
            rs.getString("property_name"),
            rs.getString("location"),
            rs.getBigDecimal("acquisition_cost"),
            d == null ? null : d.toLocalDate());
    }
}
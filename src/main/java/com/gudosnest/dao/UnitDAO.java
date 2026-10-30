package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.Unit;

/**
 * Data Access Object for the Units table.
 * Every method that touches the Units table lives here.
 */
public class UnitDAO {

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /** Returns every unit belonging to a specific property. */
    public ArrayList<Unit> getForProperty(int propertyId) {
        return QueryHelper.query(
            "SELECT unit_id, property_id, unit_name, unit_description, "
          + "       capacity, daily_rate, unit_status "
          + "FROM Units "
          + "WHERE property_id = ? "
          + "ORDER BY unit_id",
            UnitDAO::mapUnit, propertyId);
    }

    /** Returns every unit across all properties. */
    public ArrayList<Unit> getAll() {
        return QueryHelper.query(
            "SELECT unit_id, property_id, unit_name, unit_description, "
          + "       capacity, daily_rate, unit_status "
          + "FROM Units "
          + "ORDER BY unit_id",
            UnitDAO::mapUnit);
    }

    /** Returns the number of units with a given status (e.g. "occupied"). */
    public int countByStatus(String status) {
        return QueryHelper.count(
            "SELECT COUNT(*) FROM Units WHERE unit_status = ?", status);
    }

    /** Returns the unoccupied units (available to book) with property names. */
    public ArrayList<com.gudosnest.model.UnitListing> getAvailableListings() {
        return QueryHelper.query(
            "SELECT u.unit_id, u.property_id, p.property_name, u.unit_name, "
          + "       u.capacity, u.daily_rate, u.unit_status "
          + "FROM Units u "
          + "JOIN Properties p ON p.property_id = u.property_id "
          + "WHERE u.unit_status = 'unoccupied' "
          + "ORDER BY p.property_name, u.unit_name",
            rs -> new com.gudosnest.model.UnitListing(
                rs.getInt("unit_id"),
                rs.getInt("property_id"),
                rs.getString("property_name"),
                rs.getString("unit_name"),
                rs.getInt("capacity"),
                rs.getBigDecimal("daily_rate"),
                rs.getString("unit_status")));
    }

    // Maps one ResultSet row to a Unit entity.
    private static Unit mapUnit(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new Unit(
            rs.getInt("unit_id"),
            rs.getInt("property_id"),
            rs.getString("unit_name"),
            rs.getString("unit_description"),
            rs.getInt("capacity"),
            rs.getBigDecimal("daily_rate"),
            rs.getString("unit_status"));
    }

    // ---------------------------------------------------------------
    // WRITE + LOOKUPS (used by the Booking Facade / Prototype clone)
    // ---------------------------------------------------------------

    /** Saves a brand-new unit and returns its generated unit_id, or -1. */
    public int insert(Unit unit) {
        String sql = "INSERT INTO Units "
                   + "(property_id, unit_name, unit_description, capacity, daily_rate, unit_status) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, unit.getPropertyId());
            ps.setString(2, unit.getUnitName());
            ps.setString(3, unit.getUnitDescription());
            ps.setInt(4, unit.getCapacity());
            ps.setBigDecimal(5, unit.getDailyRate());
            ps.setString(6, unit.getUnitStatus());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            return keys.next() ? keys.getInt(1) : -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /** Returns the owner who owns the given unit, or -1 if not found. */
    public int ownerIdForUnit(int unitId) {
        ArrayList<Integer> ids = QueryHelper.query(
            "SELECT p.owner_id "
          + "FROM Units u "
          + "JOIN Properties p ON p.property_id = u.property_id "
          + "WHERE u.unit_id = ?",
            rs -> rs.getInt(1), unitId);
        return ids.isEmpty() ? -1 : ids.get(0);
    }

    /** Returns the nightly rate of a unit, or zero if not found. */
    public BigDecimal getDailyRate(int unitId) {
        ArrayList<BigDecimal> rates = QueryHelper.query(
            "SELECT daily_rate FROM Units WHERE unit_id = ?",
            rs -> rs.getBigDecimal(1), unitId);
        return rates.isEmpty() ? BigDecimal.ZERO : rates.get(0);
    }
}

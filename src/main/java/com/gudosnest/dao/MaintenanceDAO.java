package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.Maintenance;

/**
 * Data Access Object for the Maintenance table.
 * Every method that touches this table lives here.
 */
public class MaintenanceDAO {

    // Shared SELECT with joined property / unit display names.
    private static final String MAINT_SELECT =
        "SELECT m.maintenance_id, m.property_id, m.unit_id, m.item_id, "
      + "       m.maintenance_description, m.maintenance_cost, m.maintenance_status, "
      + "       DATE_FORMAT(m.started_at, '%b %e, %Y') AS started_at, "
      + "       IFNULL(DATE_FORMAT(m.completed_at, '%b %e, %Y'), '—') AS completed_at, "
      + "       p.property_name, IFNULL(u.unit_name, 'Whole property') AS unit_name "
      + "FROM Maintenance m "
      + "JOIN Properties p ON p.property_id = m.property_id "
      + "LEFT JOIN Units u ON u.unit_id = m.unit_id ";

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /** Returns every maintenance record. */
    public ArrayList<Maintenance> getAll() {
        return QueryHelper.query(MAINT_SELECT + "ORDER BY m.maintenance_id",
                                 MaintenanceDAO::mapMaintenance);
    }

    /** Returns maintenance records whose status matches (e.g. "pending"). */
    public ArrayList<Maintenance> getByStatus(String status) {
        return QueryHelper.query(
            MAINT_SELECT + "WHERE m.maintenance_status = ? ORDER BY m.maintenance_id",
            MaintenanceDAO::mapMaintenance, status);
    }

    /** Returns maintenance records for a specific property. */
    public ArrayList<Maintenance> getByProperty(int propertyId) {
        return QueryHelper.query(
            MAINT_SELECT + "WHERE m.property_id = ? ORDER BY m.maintenance_id",
            MaintenanceDAO::mapMaintenance, propertyId);
    }

    /** Returns the total number of maintenance records. */
    public int countAll() {
        return QueryHelper.count("SELECT COUNT(*) FROM Maintenance");
    }

    /** Returns the number of maintenance records with a given status. */
    public int countByStatus(String status) {
        return QueryHelper.count(
            "SELECT COUNT(*) FROM Maintenance WHERE maintenance_status = ?", status);
    }

    // ---------------------------------------------------------------
    // WRITE
    // ---------------------------------------------------------------

    /**
     * Inserts a new maintenance record and returns the generated id,
     * or -1 on failure.
     */
    public int insert(int propertyId, Integer unitId, Integer itemId,
                      String description, String cost, String status,
                      String startedAt) {
        String sql = "INSERT INTO Maintenance "
                   + "(property_id, unit_id, item_id, maintenance_description, "
                   + " maintenance_cost, maintenance_status, started_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, propertyId);
            if (unitId == null) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, unitId);
            if (itemId == null) ps.setNull(3, java.sql.Types.INTEGER);
            else ps.setInt(3, itemId);
            ps.setString(4, description);
            ps.setBigDecimal(5, new BigDecimal(cost));
            ps.setString(6, status);
            ps.setString(7, startedAt);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
            return -1;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Updates the status of a maintenance record.
     * When completed, completed_at is set to NOW(). Returns true on success.
     */
    public boolean updateStatus(int maintenanceId, String newStatus) {
        boolean completing = "completed".equals(newStatus);
        String sql = completing
            ? "UPDATE Maintenance SET maintenance_status = ?, completed_at = NOW() WHERE maintenance_id = ?"
            : "UPDATE Maintenance SET maintenance_status = ? WHERE maintenance_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, maintenanceId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Maps one ResultSet row to a Maintenance entity.
    private static Maintenance mapMaintenance(ResultSet rs) throws SQLException {
        int unitId = rs.getInt("unit_id");
        boolean unitNull = rs.wasNull();
        int itemId = rs.getInt("item_id");
        boolean itemNull = rs.wasNull();
        return new Maintenance(
            rs.getInt("maintenance_id"),
            rs.getInt("property_id"),
            unitNull ? null : unitId,
            itemNull ? null : itemId,
            rs.getString("maintenance_description"),
            rs.getBigDecimal("maintenance_cost"),
            rs.getString("maintenance_status"),
            rs.getString("started_at"),
            rs.getString("completed_at"),
            rs.getString("property_name"),
            rs.getString("unit_name"));
    }
}

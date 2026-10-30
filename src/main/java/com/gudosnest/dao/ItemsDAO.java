package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.Item;

/**
 * Data Access Object for the Items table (inventory).
 * Every method that touches this table lives here.
 */
public class ItemsDAO {

    // Shared SELECT with joined property / unit display names.
    private static final String ITEM_SELECT =
        "SELECT i.item_id, i.property_id, i.unit_id, i.item_name, "
      + "       i.item_type, i.quantity, i.unit_price, i.item_condition, "
      + "       p.property_name, IFNULL(u.unit_name, 'Whole property') AS unit_name "
      + "FROM Items i "
      + "JOIN Properties p ON p.property_id = i.property_id "
      + "LEFT JOIN Units u ON u.unit_id = i.unit_id ";

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /** Returns every inventory item. */
    public ArrayList<Item> getAll() {
        return QueryHelper.query(ITEM_SELECT + "ORDER BY i.item_id",
                                 ItemsDAO::mapItem);
    }

    /** Returns items that belong to a specific property. */
    public ArrayList<Item> getByProperty(int propertyId) {
        return QueryHelper.query(ITEM_SELECT + "WHERE i.property_id = ? ORDER BY i.item_id",
                                 ItemsDAO::mapItem, propertyId);
    }

    /**
     * Returns items for one property, optionally scoped to a single unit.
     * Pass unitId = null to see the whole property (all its items).
     */
    public ArrayList<Item> getForScope(int propertyId, Integer unitId) {
        if (unitId == null) {
            return getByProperty(propertyId);
        }
        return QueryHelper.query(
            ITEM_SELECT + "WHERE i.property_id = ? AND i.unit_id = ? ORDER BY i.item_id",
            ItemsDAO::mapItem, propertyId, unitId);
    }

    /** Returns the total number of inventory items. */
    public int countAll() {
        return QueryHelper.count("SELECT COUNT(*) FROM Items");
    }

    // ---------------------------------------------------------------
    // WRITE
    // ---------------------------------------------------------------

    /**
     * Inserts a new inventory item and returns true on success.
     *
     * @param unitId nullable — the specific unit (null = shared / whole property)
     */
    public boolean insert(int propertyId, Integer unitId, String itemName,
                          String itemType, int quantity, String unitPrice,
                          String condition) {
        String sql = "INSERT INTO Items "
                   + "(property_id, unit_id, item_name, item_type, quantity, unit_price, item_condition) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, propertyId);
            if (unitId == null) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, unitId);
            ps.setString(3, itemName);
            ps.setString(4, itemType);
            ps.setInt(5, quantity);
            ps.setBigDecimal(6, new BigDecimal(unitPrice));
            ps.setString(7, condition);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Updates an item's quantity and condition in one go. */
    public boolean update(int itemId, int quantity, String condition) {
        String sql = "UPDATE Items SET quantity = ?, item_condition = ? WHERE item_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, condition);
            ps.setInt(3, itemId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Maps one ResultSet row to an Item entity.
    private static Item mapItem(ResultSet rs) throws SQLException {
        int unitId = rs.getInt("unit_id");
        return new Item(
            rs.getInt("item_id"),
            rs.getInt("property_id"),
            rs.wasNull() ? null : unitId,
            rs.getString("item_name"),
            rs.getString("item_type"),
            rs.getInt("quantity"),
            rs.getBigDecimal("unit_price"),
            rs.getString("item_condition"),
            rs.getString("property_name"),
            rs.getString("unit_name"));
    }
}

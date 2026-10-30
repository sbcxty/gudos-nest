package com.gudosnest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;

/**
 * Shared low-level helpers that every DAO can reuse.
 *
 * The main method is query(): it runs a SELECT with optional parameters
 * and turns each row into a model object using a RowMapper you provide.
 */
public final class QueryHelper {

    /** Turns one ResultSet row into a model object. */
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }

    /**
     * Runs the given SELECT (with ? placeholders) and returns every row
     * mapped by your RowMapper. Example:
     *
     *   QueryHelper.query("SELECT * FROM Users WHERE email = ?",
     *       rs -> new User(...), email);
     */
    public static <T> ArrayList<T> query(String sql, RowMapper<T> mapper, Object... params) {
        ArrayList<T> out = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapper.map(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Runs a SELECT that returns a single number (e.g. COUNT(*))
     * and returns that number. Returns -1 if the query fails.
     */
    public static int count(String sql, Object... params) {
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Private constructor: this class only holds static helpers.
    private QueryHelper() {}
}

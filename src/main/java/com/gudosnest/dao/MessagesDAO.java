package com.gudosnest.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.Message;

/**
 * Data Access Object for the Messages table.
 * Every method that touches this table lives here.
 */
public class MessagesDAO {

    /** Returns every message sent to or from a user, newest first. */
    public ArrayList<Message> getForUser(int userId) {
        return QueryHelper.query(
            "SELECT m.message_id, m.sender_id, m.receiver_id, m.booking_id, "
          + "       m.message_text, m.is_read, "
          + "       DATE_FORMAT(m.sent_at, '%b %e, %Y %l:%i %p') AS sent_at, "
          + "       s.full_name AS sender_name, "
          + "       r.full_name AS receiver_name "
          + "FROM Messages m "
          + "JOIN Users s ON s.user_id = m.sender_id "
          + "JOIN Users r ON r.user_id = m.receiver_id "
          + "WHERE m.receiver_id = ? OR m.sender_id = ? "
          + "ORDER BY m.sent_at DESC",
            rs -> {
                int bookingId = rs.getInt("booking_id");
                boolean bookingNull = rs.wasNull();
                return new Message(
                    rs.getInt("message_id"),
                    rs.getInt("sender_id"),
                    rs.getInt("receiver_id"),
                    bookingNull ? null : bookingId,
                    rs.getString("message_text"),
                    rs.getString("sent_at"),
                    rs.getBoolean("is_read"),
                    rs.getString("sender_name"),
                    rs.getString("receiver_name"));
            },
            userId, userId);
    }

    /**
     * Saves a new message (bookingId may be null for a general message).
     * Returns true on success.
     */
    public boolean insert(int senderId, int receiverId, Integer bookingId, String text) {
        String sql = "INSERT INTO Messages "
                   + "(sender_id, receiver_id, booking_id, message_text) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            if (bookingId == null) ps.setNull(3, Types.INTEGER);
            else                    ps.setInt(3, bookingId);
            ps.setString(4, text);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

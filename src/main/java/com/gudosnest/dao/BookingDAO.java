package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.Booking;
import com.gudosnest.model.Payment;
import com.gudosnest.model.Refund;

/**
 * Data Access Object for the Bookings, Payments, and Refunds tables.
 * Every method that touches these tables lives here.
 */
public class BookingDAO {

    // Shared SELECT for a booking with its joined display names.
    private static final String BOOKING_SELECT =
        "SELECT b.booking_id, b.renter_id, b.unit_id, "
      + "       DATE_FORMAT(b.created_at, '%b %e, %Y %l:%i %p') AS created_at, "
      + "       DATE_FORMAT(b.check_in, '%b %e, %l:%i %p') AS check_in, "
      + "       DATE_FORMAT(b.check_out, '%b %e, %l:%i %p') AS check_out, "
      + "       b.number_of_renters, b.special_requests, b.booking_status, "
      + "       b.total_amount, "
      + "       IFNULL(DATE_FORMAT(b.cancelled_at, '%b %e, %Y %l:%i %p'), '—') AS cancelled_at, "
      + "       r.full_name AS renter_name, r.email AS renter_email, "
      + "       p.property_name, u.unit_name "
      + "FROM Bookings b "
      + "JOIN Users r ON r.user_id = b.renter_id "
      + "JOIN Units u ON u.unit_id = b.unit_id "
      + "JOIN Properties p ON p.property_id = u.property_id ";

    // ---------------------------------------------------------------
    // BOOKINGS - READ
    // ---------------------------------------------------------------

    /** Returns every booking with renter, property, and unit names. */
    public ArrayList<Booking> getAll() {
        return QueryHelper.query(
            BOOKING_SELECT + "ORDER BY b.booking_id",
            BookingDAO::mapBooking);
    }

    /** Returns bookings whose status matches (e.g. "pending"). */
    public ArrayList<Booking> getByStatus(String status) {
        return QueryHelper.query(
            BOOKING_SELECT + "WHERE b.booking_status = ? ORDER BY b.booking_id",
            BookingDAO::mapBooking, status);
    }

    /** Returns the bookings belonging to one renter. */
    public ArrayList<Booking> getForRenter(int renterId) {
        return QueryHelper.query(
            BOOKING_SELECT + "WHERE b.renter_id = ? ORDER BY b.booking_id",
            BookingDAO::mapBooking, renterId);
    }

    /** Returns one booking by id (with renter email), or null if not found. */
    public Booking getDetails(int bookingId) {
        ArrayList<Booking> list = QueryHelper.query(
            BOOKING_SELECT + "WHERE b.booking_id = ?",
            BookingDAO::mapBooking, bookingId);
        return list.isEmpty() ? null : list.get(0);
    }

    /** Returns the total number of bookings. */
    public int countAll() {
        return QueryHelper.count("SELECT COUNT(*) FROM Bookings");
    }

    /** Returns the number of bookings with a given status (e.g. "pending"). */
    public int countByStatus(String status) {
        return QueryHelper.count(
            "SELECT COUNT(*) FROM Bookings WHERE booking_status = ?", status);
    }

    /**
     * True if the unit already has a pending or approved booking that
     * overlaps the given "yyyy-MM-dd HH:mm:ss" window.
     */
    public boolean hasOverlap(int unitId, String checkIn, String checkOut) {
        return QueryHelper.count(
            "SELECT COUNT(*) FROM Bookings "
          + "WHERE unit_id = ? "
          + "  AND booking_status IN ('pending','approved') "
          + "  AND check_in  < ? "
          + "  AND check_out > ?",
            unitId, checkOut, checkIn) > 0;
    }

    // ---------------------------------------------------------------
    // PAYMENTS - READ
    // ---------------------------------------------------------------

    /** Returns every payment. */
    public ArrayList<Payment> getPayments() {
        return QueryHelper.query(
            "SELECT payment_id, booking_id, amount, receipt_url, "
          + "       payment_type, payment_status, "
          + "       IFNULL(DATE_FORMAT(payment_date, '%b %e, %Y'), 'not paid yet') AS payment_date "
          + "FROM Payments "
          + "ORDER BY payment_id",
            BookingDAO::mapPayment);
    }

    /** Returns only the payments that have not been paid yet. */
    public ArrayList<Payment> getPendingPayments() {
        return QueryHelper.query(
            "SELECT payment_id, booking_id, amount, receipt_url, "
          + "       payment_type, payment_status, "
          + "       IFNULL(DATE_FORMAT(payment_date, '%b %e, %Y'), 'not paid yet') AS payment_date "
          + "FROM Payments "
          + "WHERE payment_status = 'pending' "
          + "ORDER BY payment_id",
            BookingDAO::mapPayment);
    }

    /** Returns the payments attached to one booking. */
    public ArrayList<Payment> getPaymentsForBooking(int bookingId) {
        return QueryHelper.query(
            "SELECT payment_id, booking_id, amount, receipt_url, "
          + "       payment_type, payment_status, "
          + "       IFNULL(DATE_FORMAT(payment_date, '%b %e, %Y'), 'not paid yet') AS payment_date "
          + "FROM Payments "
          + "WHERE booking_id = ? "
          + "ORDER BY payment_id",
            BookingDAO::mapPayment, bookingId);
    }

    /** Returns the total number of payments. */
    public int countPayments() {
        return QueryHelper.count("SELECT COUNT(*) FROM Payments");
    }

    // ---------------------------------------------------------------
    // REFUNDS - READ
    // ---------------------------------------------------------------

    /** Returns every refund request. */
    public ArrayList<Refund> getRefunds() {
        return QueryHelper.query(
            "SELECT refund_id, payment_id, cancellation_fee, cancellation_reason, "
          + "       refund_status, "
          + "       DATE_FORMAT(requested_at, '%b %e, %Y') AS requested_at, "
          + "       IFNULL(DATE_FORMAT(processed_at, '%b %e, %Y'), 'not yet') AS processed_at "
          + "FROM Refunds "
          + "ORDER BY refund_id",
            BookingDAO::mapRefund);
    }

    // ---------------------------------------------------------------
    // BOOKINGS - WRITE
    // ---------------------------------------------------------------

    /**
     * Inserts a new booking and returns the generated booking_id,
     * or -1 on failure.
     */
    public int insert(int renterId, int unitId,
                      String checkIn, String checkOut,
                      int numberOfRenters, String specialRequests,
                      String totalAmount) {
        String sql = "INSERT INTO Bookings "
                   + "(renter_id, unit_id, check_in, check_out, "
                   + " number_of_renters, special_requests, total_amount) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, renterId);
            ps.setInt(2, unitId);
            ps.setString(3, checkIn);
            ps.setString(4, checkOut);
            ps.setInt(5, numberOfRenters);
            ps.setString(6, specialRequests);
            ps.setBigDecimal(7, new BigDecimal(totalAmount));
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
     * Updates the status of an existing booking.
     * Cancelling also records cancelled_at (required by a DB CHECK).
     * Returns true on success.
     */
    public boolean updateStatus(int bookingId, String newStatus) {
        boolean cancelling = "cancelled".equals(newStatus);
        String sql = cancelling
            ? "UPDATE Bookings SET booking_status = ?, cancelled_at = NOW() WHERE booking_id = ?"
            : "UPDATE Bookings SET booking_status = ? WHERE booking_id = ?";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Records a refund request against a payment. Returns true on success. */
    public boolean insertRefund(int paymentId, BigDecimal cancellationFee, String reason) {
        String sql = "INSERT INTO Refunds "
                   + "(payment_id, cancellation_fee, cancellation_reason, refund_status) "
                   + "VALUES (?, ?, ?, 'requested')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, paymentId);
            ps.setBigDecimal(2, cancellationFee);
            ps.setString(3, reason);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---- Row mappers ----

    private static Booking mapBooking(ResultSet rs) throws SQLException {
        return new Booking(
            rs.getInt("booking_id"),
            rs.getInt("renter_id"),
            rs.getInt("unit_id"),
            rs.getString("created_at"),
            rs.getString("check_in"),
            rs.getString("check_out"),
            rs.getInt("number_of_renters"),
            rs.getString("special_requests"),
            rs.getString("booking_status"),
            rs.getBigDecimal("total_amount"),
            rs.getString("cancelled_at"),
            rs.getString("renter_name"),
            rs.getString("renter_email"),
            rs.getString("property_name"),
            rs.getString("unit_name"));
    }

    private static Payment mapPayment(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt("payment_id"),
            rs.getInt("booking_id"),
            rs.getBigDecimal("amount"),
            rs.getString("receipt_url"),
            rs.getString("payment_type"),
            rs.getString("payment_status"),
            rs.getString("payment_date"));
    }

    private static Refund mapRefund(ResultSet rs) throws SQLException {
        return new Refund(
            rs.getInt("refund_id"),
            rs.getInt("payment_id"),
            rs.getBigDecimal("cancellation_fee"),
            rs.getString("cancellation_reason"),
            rs.getString("refund_status"),
            rs.getString("requested_at"),
            rs.getString("processed_at"));
    }
}

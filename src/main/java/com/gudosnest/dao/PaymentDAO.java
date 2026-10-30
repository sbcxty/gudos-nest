package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gudosnest.db.DBConnection;

/**
 * Data Access Object for writing Payments together with their matching
 * income record in FinancialTransactions (done atomically in one transaction
 * so the two tables never fall out of sync).
 */
public class PaymentDAO {

    /**
     * Records a paid payment and its matching income transaction in a
     * single transaction. Returns true on success, false on failure.
     *
     * @param bookingId  the booking this payment settles
     * @param propertyId the property the income belongs to
     * @param amount     e.g. "25000.00"
     * @param paymentType "rental_payment" or "damage_fee"
     * @param receiptUrl receipt proof (required for paid payments)
     */
    public boolean insertPaid(int bookingId, int propertyId, String amount,
                              String paymentType, String receiptUrl) {
        // A damage-fee payment becomes damage-fee income; a rental payment
        // becomes rental income. (CHECK constraint requires a payment_id
        // on income rows, so we insert them together.)
        String incomeType = "damage_fee".equals(paymentType)
                ? "damage_fee_income" : "rental_income";

        String paySql =
            "INSERT INTO Payments "
          + "(booking_id, amount, receipt_url, payment_type, payment_status, payment_date) "
          + "VALUES (?, ?, ?, ?, 'paid', NOW())";

        String txSql =
            "INSERT INTO FinancialTransactions "
          + "(property_id, payment_id, transaction_type, amount, transaction_date) "
          + "VALUES (?, ?, ?, ?, NOW())";

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false); // start transaction

            int paymentId;
            try (PreparedStatement ps = c.prepareStatement(paySql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, bookingId);
                ps.setBigDecimal(2, new BigDecimal(amount));
                ps.setString(3, receiptUrl);
                ps.setString(4, paymentType);
                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (!keys.next()) { c.rollback(); return false; }
                paymentId = keys.getInt(1);
            }

            try (PreparedStatement ps = c.prepareStatement(txSql)) {
                ps.setInt(1, propertyId);
                ps.setInt(2, paymentId);
                ps.setString(3, incomeType);
                ps.setBigDecimal(4, new BigDecimal(amount));
                ps.executeUpdate();
            }

            c.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Records a pending rental payment for a booking (no receipt yet).
     * Used by the Booking Facade when a booking is first requested.
     */
    public boolean insertPending(int bookingId, BigDecimal amount) {
        String sql = "INSERT INTO Payments "
                   + "(booking_id, amount, payment_type, payment_status) "
                   + "VALUES (?, ?, 'rental_payment', 'pending')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, bookingId);
            ps.setBigDecimal(2, amount);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

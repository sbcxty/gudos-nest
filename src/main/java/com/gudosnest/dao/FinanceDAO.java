package com.gudosnest.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.gudosnest.db.DBConnection;
import com.gudosnest.model.FinancialTransaction;
import com.gudosnest.model.PropertySummary;

/**
 * Data Access Object for the FinancialTransactions table.
 * Every method that touches this table lives here.
 */
public class FinanceDAO {

    // ---------------------------------------------------------------
    // READ
    // ---------------------------------------------------------------

    /**
     * Returns financial transactions, optionally filtered by property
     * and/or transaction type. Pass null to skip a filter.
     */
    public ArrayList<FinancialTransaction> getTransactions(Integer propertyId, String type) {
        StringBuilder sql = new StringBuilder(
            "SELECT f.transaction_id, f.property_id, f.payment_id, "
          + "       f.transaction_type, f.amount, "
          + "       DATE_FORMAT(f.transaction_date, '%b %e, %Y %l:%i %p') AS transaction_date, "
          + "       p.property_name "
          + "FROM FinancialTransactions f "
          + "JOIN Properties p ON p.property_id = f.property_id ");

        ArrayList<Object> params = new ArrayList<>();
        if (propertyId != null) {
            sql.append("WHERE f.property_id = ? ");
            params.add(propertyId);
        }
        if (type != null) {
            sql.append(params.isEmpty() ? "WHERE " : "AND ");
            sql.append("f.transaction_type = ? ");
            params.add(type);
        }
        sql.append("ORDER BY f.transaction_date DESC");

        return QueryHelper.query(sql.toString(), FinanceDAO::mapTransaction,
                                 params.toArray());
    }

    /** Returns a per-property financial summary (income, expenses, net). */
    public ArrayList<PropertySummary> getPropertySummaries() {
        return QueryHelper.query(
            "SELECT p.property_id, p.property_name, p.acquisition_cost, "
          + "       COALESCE(SUM(CASE WHEN f.transaction_type IN "
          + "           ('rental_income', 'damage_fee_income') "
          + "           THEN f.amount ELSE 0 END), 0) AS total_income, "
          + "       COALESCE(SUM(CASE WHEN f.transaction_type IN "
          + "           ('refund', 'maintenance_expense', 'supply_expense') "
          + "           THEN f.amount ELSE 0 END), 0) AS total_expenses "
          + "FROM Properties p "
          + "LEFT JOIN FinancialTransactions f ON f.property_id = p.property_id "
          + "GROUP BY p.property_id, p.property_name, p.acquisition_cost "
          + "ORDER BY p.property_id",
            rs -> new PropertySummary(
                rs.getInt("property_id"),
                rs.getString("property_name"),
                rs.getBigDecimal("acquisition_cost"),
                rs.getBigDecimal("total_income"),
                rs.getBigDecimal("total_expenses")));
    }

    /** Returns the summed amount of a given type for a property (0 if none). */
    public BigDecimal sumAmount(int propertyId, String type) {
        ArrayList<BigDecimal> list = QueryHelper.query(
            "SELECT COALESCE(SUM(amount), 0) "
          + "FROM FinancialTransactions "
          + "WHERE property_id = ? AND transaction_type = ?",
            rs -> rs.getBigDecimal(1), propertyId, type);
        return list.isEmpty() ? BigDecimal.ZERO : list.get(0);
    }

    /** Returns the total number of financial transactions. */
    public int countTransactions() {
        return QueryHelper.count("SELECT COUNT(*) FROM FinancialTransactions");
    }

    // ---------------------------------------------------------------
    // WRITE
    // ---------------------------------------------------------------

    /**
     * Inserts a manual financial transaction (expense / income) and
     * returns true on success. Use PaymentDAO for paid-payment income.
     */
    public boolean insert(int propertyId, String type, String amount, String date) {
        String sql = "INSERT INTO FinancialTransactions "
                   + "(property_id, transaction_type, amount, transaction_date) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, propertyId);
            ps.setString(2, type);
            ps.setBigDecimal(3, new BigDecimal(amount));
            ps.setString(4, date);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Maps one ResultSet row to a FinancialTransaction entity.
    private static FinancialTransaction mapTransaction(java.sql.ResultSet rs)
            throws SQLException {
        int paymentId = rs.getInt("payment_id");
        boolean paymentNull = rs.wasNull();
        return new FinancialTransaction(
            rs.getInt("transaction_id"),
            rs.getInt("property_id"),
            paymentNull ? null : paymentId,
            rs.getString("transaction_type"),
            rs.getBigDecimal("amount"),
            rs.getString("transaction_date"),
            rs.getString("property_name"));
    }
}

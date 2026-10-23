package com.gudosnest.db;

import java.math.BigDecimal;
import java.sql.*;

import org.mindrot.jbcrypt.BCrypt;

/**
 * One-time sample data seeding. Runs on app start whenever the users table
 * is empty, so a fresh database is demo-ready.
 *
 * Demo accounts created here (hashed with jBCrypt, never stored in plain text):
 *   owner  : owner@gudosnest.com  / gudos123
 *   renter : renter@gudosnest.com / gudos123
 */
public final class Seed {

    public static void run() {
        try (Connection c = Database.connect(); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM users");
            rs.next();
            if (rs.getInt(1) > 0) return; // already seeded

            String ownerHash = BCrypt.hashpw("gudos123", BCrypt.gensalt());
            String renterHash = BCrypt.hashpw("gudos123", BCrypt.gensalt());

            PreparedStatement u = c.prepareStatement(
                "INSERT INTO users (full_name, email, password_hash, is_owner, is_renter)"
                + " VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            int ownerId = insertUser(u, "Alex Riviera", "owner@gudosnest.com", ownerHash, true, false);
            int renterId = insertUser(u, "Mika Tanaka", "renter@gudosnest.com", renterHash, false, true);

            PreparedStatement pu = c.prepareStatement(
                "INSERT INTO properties (owner_id, property_name, location, acquisition_cost,"
                + " acquisition_date, has_units, daily_rate, capacity) VALUES (?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            int shell = insertProperty(pu, ownerId, "Shell Harbor Cabin", "Bataan, Bataan Coast", 2500000, "2022-03-15", true, 1800, 4);
            int olive = insertProperty(pu, ownerId, "Olive Garden Villa", "Tagaytay, Cavite", 4600000, "2021-11-02", false, 6500, 10);

            PreparedStatement un = c.prepareStatement(
                "INSERT INTO units (property_id, unit_name, unit_description, capacity, daily_rate, unit_status)"
                + " VALUES (?,?,?,?,?,?)");
            un.setInt(1, shell); un.setString(2, "Unit A - Sea View"); un.setString(3, "Studio with balcony, wifi, aircon");
            un.setInt(4, 3); un.setBigDecimal(5, new BigDecimal("1800")); un.setString(6, "occupied"); un.executeUpdate();
            un.setInt(1, shell); un.setString(2, "Unit B - Garden View"); un.setString(3, "Studio with garden access, wifi");
            un.setInt(4, 2); un.setBigDecimal(5, new BigDecimal("1600")); un.setString(6, "unoccupied"); un.executeUpdate();
            un.setInt(1, shell); un.setString(2, "Unit C - Family Loft"); un.setString(3, "Two-bedroom loft w/ kitchenette");
            un.setInt(4, 6); un.setBigDecimal(5, new BigDecimal("2600")); un.setString(6, "unoccupied"); un.executeUpdate();

            PreparedStatement bk = c.prepareStatement(
                "INSERT INTO bookings (renter_id, property_id, unit_id, check_in, check_out,"
                + " number_of_renters, booking_status, total_amount) VALUES (?,?,?,?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            int b1 = insertBooking(bk, renterId, shell, 1L, "2026-10-25 14:00:00", "2026-10-27 11:00:00", 2, "approved", "3600");
            insertBooking(bk, renterId, olive, null, "2026-11-01 14:00:00", "2026-11-03 11:00:00", 8, "pending", "13000");
            insertBooking(bk, renterId, shell, 2L, "2026-10-10 14:00:00", "2026-10-12 11:00:00", 2, "completed", "3200");

            PreparedStatement pay = c.prepareStatement(
                "INSERT INTO payments (booking_id, amount, payment_type, payment_status, payment_date)"
                + " VALUES (?,?,?,?,NOW())");
            pay.setInt(1, b1); pay.setBigDecimal(2, new BigDecimal("3600"));
            pay.setString(3, "rental"); pay.setString(4, "paid");
            pay.executeUpdate();

            PreparedStatement ft = c.prepareStatement(
                "INSERT INTO financialtransactions (property_id, payment_id, transaction_type, amount, transaction_date)"
                + " VALUES (?,?,?,?,NOW())");
            ft.setInt(1, shell); ft.setNull(2, Types.INTEGER); ft.setString(3, "rental_income");
            ft.setBigDecimal(4, new BigDecimal("3600")); ft.executeUpdate();
            ft.setInt(1, shell); ft.setNull(2, Types.INTEGER); ft.setString(3, "rental_income");
            ft.setBigDecimal(4, new BigDecimal("3200")); ft.executeUpdate();
            ft.setInt(1, shell); ft.setNull(2, Types.INTEGER); ft.setString(3, "supply_expense");
            ft.setBigDecimal(4, new BigDecimal("450")); ft.executeUpdate();
            ft.setInt(1, olive); ft.setNull(2, Types.INTEGER); ft.setString(3, "maintenance_expense");
            ft.setBigDecimal(4, new BigDecimal("1200")); ft.executeUpdate();

            PreparedStatement inv = c.prepareStatement(
                "INSERT INTO inventory (property_id, unit_id, item_name, item_type, quantity,"
                + " restock_threshold, unit_price, item_condition) VALUES (?,?,?,?,?,?,?,?)");
            insertItem(inv, shell, null, "Bath towel set", "consumable", 8, 4, "150", "good");
            insertItem(inv, shell, null, "Shampoo sachets", "consumable", 2, 6, "12", "good");
            insertItem(inv, shell, null, "Microwave", "long_term", 1, null, "2800", "good");
            insertItem(inv, shell, null, "Hair dryer", "long_term", 1, null, "950", "damaged");

            PreparedStatement mnt = c.prepareStatement(
                "INSERT INTO maintenance (property_id, unit_id, item_id, maintenance_description,"
                + " maintenance_cost, maintenance_status, started_at, completed_at)"
                + " VALUES (?,?,?,?,?,?,?,?)");
            mnt.setInt(1, shell); mnt.setNull(2, Types.INTEGER); mnt.setNull(3, Types.INTEGER);
            mnt.setString(4, "Water heater replacement in Unit A");
            mnt.setBigDecimal(5, new BigDecimal("1200"));
            mnt.setString(6, "completed"); mnt.setString(7, "2026-10-05 09:00:00");
            mnt.setString(8, "2026-10-06 15:00:00"); mnt.executeUpdate();

            System.out.println("[db] Seeded sample data.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int insertUser(PreparedStatement ps, String name, String email, String hash,
                                  boolean isOwner, boolean isRenter) throws SQLException {
        ps.setString(1, name); ps.setString(2, email); ps.setString(3, hash);
        ps.setBoolean(4, isOwner); ps.setBoolean(5, isRenter);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys(); rs.next(); return rs.getInt(1);
    }

    private static int insertProperty(PreparedStatement ps, int owner, String name, String loc,
                                      int cost, String date, boolean hasUnits, int rate, int cap) throws SQLException {
        ps.setInt(1, owner); ps.setString(2, name); ps.setString(3, loc);
        ps.setBigDecimal(4, new BigDecimal(cost)); ps.setString(5, date);
        ps.setBoolean(6, hasUnits); ps.setBigDecimal(7, new BigDecimal(rate)); ps.setInt(8, cap);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys(); rs.next(); return rs.getInt(1);
    }

    private static int insertBooking(PreparedStatement ps, int renter, int prop, Long unit,
                                     String in, String out, int guests, String status, String total) throws SQLException {
        ps.setInt(1, renter); ps.setInt(2, prop);
        if (unit == null) ps.setNull(3, Types.INTEGER); else ps.setLong(3, unit);
        ps.setString(4, in); ps.setString(5, out); ps.setInt(6, guests);
        ps.setString(7, status); ps.setString(8, total);
        ps.executeUpdate();
        ResultSet rs = ps.getGeneratedKeys(); rs.next(); return rs.getInt(1);
    }

    private static void insertItem(PreparedStatement ps, int prop, Integer unit, String name,
                                   String type, int qty, Integer threshold, String price, String cond) throws SQLException {
        ps.setInt(1, prop);
        if (unit == null) ps.setNull(2, Types.INTEGER); else ps.setInt(2, unit);
        ps.setString(3, name); ps.setString(4, type); ps.setInt(5, qty);
        if (threshold == null) ps.setNull(6, Types.INTEGER); else ps.setInt(6, threshold);
        ps.setString(7, price); ps.setString(8, cond);
        ps.executeUpdate();
    }

    private Seed() {}
}

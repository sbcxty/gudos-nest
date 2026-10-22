package com.gudosnest.dao;

import org.mindrot.jbcrypt.BCrypt;

import com.gudosnest.model.User;

import java.sql.*;

public class UserDAO {

    // Register a user with one or both roles.
    public boolean register(String fullName, String email,
            String password, boolean owner, boolean renter) {

        if (!owner && !renter) {
            return false;
        }

        String sql = "INSERT INTO Users "
                + "(full_name, email, password_hash, is_owner, is_renter) "
                + "VALUES (?, ?, ?, ?, ?)";

        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, hash);
            ps.setBoolean(4, owner);
            ps.setBoolean(5, renter);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Register failed: " + e.getMessage());
            return false;
        }
    }

    // Log in and return the user if the credentials are correct
    // and the account is active.
    public User login(String email, String password, String role) {
        String sql = "SELECT * FROM Users WHERE email = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String hash = rs.getString("password_hash");

                    if (!BCrypt.checkpw(password, hash)) {
                        return null;
                    }

                    String status = rs.getString("account_status");

                    if (!"active".equals(status)) {
                        return null;
                    }

                    boolean isOwner = rs.getBoolean("is_owner");
                    boolean isRenter = rs.getBoolean("is_renter");

                    if ("owner".equals(role) && !isOwner) {
                        return null;
                    }

                    if ("renter".equals(role) && !isRenter) {
                        return null;
                    }

                    if (!"owner".equals(role) && !"renter".equals(role)) {
                        return null;
                    }

                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getString("email"),
                            hash,
                            isOwner,
                            isRenter,
                            status);
                }
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
        }

        return null;
    }
}
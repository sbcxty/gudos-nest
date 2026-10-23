package com.gudosnest.db;

import java.sql.SQLException;

import com.gudosnest.dao.DBConnection;
import com.gudosnest.dao.UserDAO;
import com.gudosnest.model.User;

/**
 * Entry point of the data layer.
 *
 * In this DRAFT build, this class only exposes the shared connection plus a
 * headless smoke test. The responsibilities live in sibling classes:
 *   Seed     - one-time sample data (SAVE/seed half)
 *   DataStore- reads + demo save (RETRIEVE half feeding the UI)
 *   Auth     - login validation with jBCrypt
 *
 * Smoke test: java -cp ... com.gudosnest.db.Database
 */
public final class Database {

    public static java.sql.Connection connect() throws SQLException {
        return DBConnection.getConnection();
    }

    public static void main(String[] args) {
        Seed.run();
        System.out.println("users        : " + DataStore.count("SELECT COUNT(*) FROM users"));
        System.out.println("properties   : " + DataStore.count("SELECT COUNT(*) FROM properties"));
        System.out.println("units        : " + DataStore.count("SELECT COUNT(*) FROM units"));
        System.out.println("bookings     : " + DataStore.count("SELECT COUNT(*) FROM bookings"));
        System.out.println("payments     : " + DataStore.count("SELECT COUNT(*) FROM payments"));
        System.out.println("fintrans     : " + DataStore.count("SELECT COUNT(*) FROM financialtransactions"));
        System.out.println("inventory    : " + DataStore.count("SELECT COUNT(*) FROM inventory"));
        System.out.println("maintenance  : " + DataStore.count("SELECT COUNT(*) FROM maintenance"));
        System.out.println("messages     : " + DataStore.count("SELECT COUNT(*) FROM messages"));
        UserDAO dao = new UserDAO();
        User owner = dao.login("owner@gudosnest.com", "gudos123", "owner");
        System.out.println("owner login via DB: " + (owner != null ? "OK" : "FAILED"));
        System.out.println("bad password rejected: "
                + (dao.login("owner@gudosnest.com", "wrong", "owner") == null));
    }

    private Database() {}
}

package com.gudosnest;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    public static Connection getConnection() throws SQLException {
        try (InputStream in = DBConnection.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new SQLException("db.properties not found");
            }
            Properties props = new Properties();
            props.load(in);
            return DriverManager.getConnection(
                props.getProperty("db.url"),
                props.getProperty("db.user"),
                props.getProperty("db.password"));
        } catch (java.io.IOException e) {
            throw new SQLException("Could not read db.properties", e);
        }
    }
}
package com.gudosnest.db;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * SINGLETON: the whole app shares one database connection.
 * Callers still write DBConnection.getConnection() like before.
 */
public final class DBConnection {

    private static final DBConnection INSTANCE = new DBConnection();

    private Connection real;    
    private Connection shared;   

    private DBConnection() {}   

    public static DBConnection getInstance() { return INSTANCE; }

    public static Connection getConnection() throws SQLException {
        return INSTANCE.connection();
    }

    public static void shutdown() {
        INSTANCE.closeReal();
    }

    private synchronized Connection connection() throws SQLException {
        if (real == null || real.isClosed() || !real.isValid(2)) {
            real = open();
            shared = (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[]{ Connection.class },
                (proxy, method, args) -> {
                    if (method.getName().equals("close")) return null;   // ignore close()
                    try {
                        return method.invoke(real, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
        }
        return shared;
    }

    private synchronized void closeReal() {
        try { if (real != null) real.close(); } catch (SQLException ignored) { }
        real = null;
        shared = null;
    }

    private static Connection open() throws SQLException {
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
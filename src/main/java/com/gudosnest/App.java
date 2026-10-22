package com.gudosnest;

import com.gudosnest.dao.UserDAO;
import com.gudosnest.model.User;

public class App {
    public static void main(String[] args) {
        UserDAO dao = new UserDAO();

        // Register as an owner
        boolean registered = dao.register(
            "Test Owner",
            "owner@test.com",
            "pass123",
            true,
            false
        );

        System.out.println("Registration successful: " + registered);

        // Login as an owner
        User owner = dao.login("owner@test.com", "pass123", "owner");

        System.out.println(
            owner != null ? "Owner login OK" : "Owner login failed"
        );

        // Try logging in as a renter with the same account
        User renter = dao.login("owner@test.com", "pass123", "renter");

        System.out.println(
            renter == null
                ? "Renter login correctly rejected"
                : "Renter login OK"
        );
    }
}
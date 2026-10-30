package com.gudosnest.controller;

import com.gudosnest.dao.UserDAO;
import com.gudosnest.model.User;

/**
 * Holds all the login / registration rules so the UI stays thin.
 * The UI calls these methods and just shows the returned message.
 */
public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    /** Attempts to log a user in and explains why it failed if it did. */
    public AuthResult login(String email, String password, String role) {
        if (email.isBlank() || password.isBlank()) {
            return new AuthResult(false, "Please fill in all required fields.", null);
        }
        if (!isValidEmail(email)) {
            return new AuthResult(false, "Please enter a valid email address.", null);
        }

        User user = userDAO.login(email.trim(), password, role);
        if (user == null) {
            return new AuthResult(false,
                    "Login failed. Check your details, selected role, or account status.",
                    null);
        }
        return new AuthResult(true, "Welcome back!", user);
    }

    /** Registers a new account after validating the inputs. */
    public AuthResult register(String fullName, String email,
                               String password, boolean owner, boolean renter) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            return new AuthResult(false, "Please fill in all required fields.", null);
        }
        if (!isValidEmail(email)) {
            return new AuthResult(false, "Please enter a valid email address.", null);
        }
        if (!owner && !renter) {
            return new AuthResult(false, "Choose at least one role.", null);
        }

        String problem = passwordProblem(password);
        if (problem != null) {
            return new AuthResult(false, problem, null);
        }

        boolean ok = userDAO.register(fullName.trim(), email.trim(), password, owner, renter);
        if (!ok) {
            return new AuthResult(false,
                    "Registration failed. The email may already exist.", null);
        }
        return new AuthResult(true, "Account created! You can now log in.", null);
    }

    /**
     * Returns a description of what is wrong with the password,
     * or null when it meets the policy.
     */
    public static String passwordProblem(String password) {
        if (password.length() < 8 || password.length() > 64) {
            return "Password must be 8-64 characters long.";
        }
        if (password.chars().anyMatch(Character::isWhitespace)) {
            return "Password cannot contain spaces.";
        }
        boolean upper = password.chars().anyMatch(Character::isUpperCase);
        boolean lower = password.chars().anyMatch(Character::isLowerCase);
        boolean digit = password.chars().anyMatch(Character::isDigit);
        boolean symbol = password.chars().anyMatch(c -> !Character.isLetterOrDigit(c));
        if (!upper || !lower || !digit || !symbol) {
            return "Password needs an uppercase letter, a lowercase letter, a number, and a symbol.";
        }
        return null;
    }

    /** Basic email shape check (something @ something . something). */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }
}

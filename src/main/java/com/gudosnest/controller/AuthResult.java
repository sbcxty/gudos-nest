package com.gudosnest.controller;

import com.gudosnest.model.User;

/**
 * Result of an auth attempt: success flag, a user-facing message,
 * and (on success) the logged-in user.
 */
public record AuthResult(boolean success, String message, User user) {}

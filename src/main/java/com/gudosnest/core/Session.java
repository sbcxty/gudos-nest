package com.gudosnest.core;

import com.gudosnest.model.User;

/** SINGLETON: remembers who is logged in. */
public final class Session {

    private static final Session INSTANCE = new Session();
    private User user;

    private Session() {}

    public static Session getInstance() { return INSTANCE; }

    public void setUser(User u) { user = u; }
    public User getUser()       { return user; }
    public void clear()         { user = null; }
}
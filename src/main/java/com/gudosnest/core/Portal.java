package com.gudosnest.core;

import javax.swing.JPanel;

import com.gudosnest.model.User;

public abstract class Portal {

    protected final User user;
    protected final Runnable onSignOut;

    protected Portal(User user, Runnable onSignOut) {
        this.user = user;
        this.onSignOut = onSignOut;
    }

    public abstract JPanel getPanel();

    public abstract String role();
}

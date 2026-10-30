package com.gudosnest.core;

import com.gudosnest.model.User;

public final class PortalFactory {

    private PortalFactory() {}

    public static Portal create(User user, String role, Runnable onSignOut) {
        return "owner".equalsIgnoreCase(role)
                ? new OwnerPortal(user, onSignOut)
                : new RenterPortal(user, onSignOut);
    }
}

package com.gudosnest.core;

import javax.swing.JPanel;

import com.gudosnest.model.User;
import com.gudosnest.owner.OwnerView;

/** FACTORY METHOD: the owner portal product. */
public class OwnerPortal extends Portal {

    private final OwnerView view;

    public OwnerPortal(User user, Runnable onSignOut) {
        super(user, onSignOut);
        this.view = new OwnerView(user, onSignOut);
    }

    @Override
    public JPanel getPanel() {
        return view;
    }

    @Override
    public String role() {
        return "owner";
    }
}

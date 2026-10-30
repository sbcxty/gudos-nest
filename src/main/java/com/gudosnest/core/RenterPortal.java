package com.gudosnest.core;

import javax.swing.JPanel;

import com.gudosnest.model.User;
import com.gudosnest.renter.RenterView;

/** FACTORY METHOD: the renter portal product. */
public class RenterPortal extends Portal {

    private final RenterView view;

    public RenterPortal(User user, Runnable onSignOut) {
        super(user, onSignOut);
        this.view = new RenterView(user, onSignOut);
    }

    @Override
    public JPanel getPanel() {
        return view;
    }

    @Override
    public String role() {
        return "renter";
    }
}

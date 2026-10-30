package com.gudosnest.auth;

import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.gudosnest.core.Portal;
import com.gudosnest.core.PortalFactory;
import com.gudosnest.core.Session;
import com.gudosnest.model.User;

public class AuthFrame extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private Portal currentPortal;

    public AuthFrame() {
        setTitle("Gudo's Nest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 760);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        root.add(new AuthPanel(this::onLoggedIn), "auth");
        setContentPane(root);
        cards.show(root, "auth");
    }

    /** Called by AuthPanel after a successful login. */
    private void onLoggedIn(User user, String role) {
        // SINGLETON: remember who is logged in.
        Session.getInstance().setUser(user);

        // Drop any previous main view so a fresh one is built next time.
        if (currentPortal != null) {
            root.remove(currentPortal.getPanel());
        }
        // FACTORY METHOD: let PortalFactory pick the concrete portal.
        currentPortal = PortalFactory.create(user, role, this::showAuth);
        root.add(currentPortal.getPanel(), "main");
        cards.show(root, "main");
        revalidate();
        repaint();
    }

    /** Returns to the login card (used on logout). */
    private void showAuth() {
        // SINGLETON: forget the logged-in user.
        Session.getInstance().clear();
        if (currentPortal != null) {
            root.remove(currentPortal.getPanel());
            currentPortal = null;
        }
        cards.show(root, "auth");
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use the default Swing appearance.
            }
            new AuthFrame().setVisible(true);
        });
    }
}

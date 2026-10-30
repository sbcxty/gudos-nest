
package com.gudosnest.auth;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.function.BiConsumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

import com.gudosnest.controller.AuthController;
import com.gudosnest.controller.AuthResult;
import com.gudosnest.model.User;
import com.gudosnest.ui.AsteriskIcon;
import com.gudosnest.ui.BrandPanel;
import com.gudosnest.ui.EyeButton;
import com.gudosnest.ui.HintPasswordField;
import com.gudosnest.ui.HintTextField;
import com.gudosnest.ui.InputBox;
import com.gudosnest.ui.RoundedButton;
import com.gudosnest.ui.RoundedPanel;
import com.gudosnest.ui.Theme;
import com.gudosnest.ui.Toast;

public class AuthPanel extends JPanel {

    private final BiConsumer<User, String> onLogin;
    private final AuthController controller = new AuthController();

    private static final int FORM_WIDTH = 340;

    private String selectedRole = "renter";
    private boolean signupMode = false;

    private final JTextField nameField =
            new HintTextField("Your full name");

    private final JTextField emailField =
            new HintTextField("you@example.com");

    private final javax.swing.JPasswordField passwordField =
            new HintPasswordField("Enter your password");

    private final JLabel titleLabel = center(new JLabel());
    private final JLabel subtitleLabel = center(new JLabel());
    private final JLabel nameLabel = makeLabel("Full name");
    private final JLabel switchLabel = new JLabel();

    private final InputBox nameBox;

    private final RoundedButton renterButton =
            new RoundedButton("Renter");

    private final RoundedButton ownerButton =
            new RoundedButton("Owner");

    private final RoundedButton submitButton =
            new RoundedButton("");

    private final JButton switchButton = new JButton();

    private final JPanel formFields = new JPanel();

    public AuthPanel(BiConsumer<User, String> onLogin) {
        this.onLogin = onLogin;

        setLayout(new BorderLayout());
        setBackground(Theme.PAGE_BG);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        // One big rounded white card holds both halves.
        RoundedPanel card =
                new RoundedPanel(32, Theme.WHITE, Theme.HAIRLINE);

        card.setLayout(new BorderLayout());

        JPanel brandWrap = new JPanel(new BorderLayout());
        brandWrap.setOpaque(false);
        brandWrap.setBorder(new EmptyBorder(10, 10, 10, 10));
        brandWrap.add(buildBrandPanel(), BorderLayout.CENTER);
        brandWrap.setPreferredSize(new Dimension(420, 0));

        card.add(brandWrap, BorderLayout.WEST);
        card.add(buildFormScroll(), BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);

        nameBox = new InputBox(nameField, null);
        InputBox emailBox = new InputBox(emailField, null);

        final char hiddenEcho = passwordField.getEchoChar();

        EyeButton eye = new EyeButton();

        eye.addActionListener(e -> {
            boolean reveal = passwordField.getEchoChar() != 0;

            passwordField.setEchoChar(
                    reveal ? (char) 0 : hiddenEcho
            );

            eye.setRevealed(reveal);
        });

        InputBox passwordBox = new InputBox(passwordField, eye);

        // Keep every control on the same fixed-width column.
        cap(nameBox);
        cap(emailBox);
        cap(passwordBox);

        addField(formFields, nameLabel, nameBox);
        addField(formFields, makeLabel("Email address"), emailBox);
        addField(formFields, makeLabel("Password"), passwordBox);

        wireRoleButtons();

        addField(
                formFields,
                makeLabel("I want to continue as"),
                buildRolePanel()
        );

        cap(submitButton);
        formFields.add(submitButton);
        formFields.add(Box.createVerticalStrut(18));
        formFields.add(buildSwitchPanel());
        formFields.add(Box.createVerticalGlue());

        submitButton.addActionListener(e -> submitForm());

        updateForm();
    }

    // ---- Brand (left) panel ---------------------------------------------

    private JPanel buildBrandPanel() {
        JPanel brand = new BrandPanel();

        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(30, 30, 32, 30));

        JLabel logo = left(new JLabel("Gudo's Nest"));
        logo.setFont(Theme.LOGO);
        logo.setForeground(Theme.BURGUNDY);

        JLabel logoCaption =
                left(new JLabel("YOUR SPACE. YOUR PLACE."));

        logoCaption.setFont(Theme.SECONDARY_BOLD);
        logoCaption.setForeground(Theme.PURPLE);

        JLabel eyebrow =
                left(new JLabel("A better place to begin."));

        eyebrow.setFont(Theme.BODY);
        eyebrow.setForeground(Theme.BURGUNDY);

        JLabel welcome = left(new JLabel(
                "<html>Find a place<br>to call your own.</html>"
        ));

        welcome.setFont(Theme.f(java.awt.Font.BOLD, 32));
        welcome.setForeground(Theme.BURGUNDY);

        JLabel description = left(new JLabel(
                "<html>Whether you're looking for a home<br>"
                + "or managing a property, start here.</html>"
        ));

        description.setFont(Theme.BODY);
        description.setForeground(Theme.BURGUNDY);

        brand.add(logo);
        brand.add(Box.createVerticalStrut(4));
        brand.add(logoCaption);
        brand.add(Box.createVerticalGlue());
        brand.add(eyebrow);
        brand.add(Box.createVerticalStrut(10));
        brand.add(welcome);
        brand.add(Box.createVerticalStrut(14));
        brand.add(description);

        return brand;
    }

    // ---- Form (right) panel ---------------------------------------------

    private JScrollPane buildFormScroll() {
        formFields.setLayout(
                new BoxLayout(formFields, BoxLayout.Y_AXIS)
        );

        formFields.setOpaque(false);

        // Balanced padding around the centered form column.
        formFields.setBorder(
                new EmptyBorder(24, 40, 24, 40)
        );

        titleLabel.setFont(Theme.PAGE_TITLE);
        titleLabel.setForeground(Theme.BURGUNDY);

        subtitleLabel.setFont(Theme.BODY);
        subtitleLabel.setForeground(Theme.MUTED);

        formFields.add(Box.createVerticalGlue());
        formFields.add(center(new AsteriskIcon()));
        formFields.add(Box.createVerticalStrut(16));
        formFields.add(titleLabel);
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(subtitleLabel);
        formFields.add(Box.createVerticalStrut(24));

        JScrollPane scroll = new JScrollPane(formFields);

        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        scroll.getViewport().setScrollMode(
                JViewport.SIMPLE_SCROLL_MODE
        );

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scroll.getVerticalScrollBar().setUnitIncrement(16);

        return scroll;
    }

    private void wireRoleButtons() {
        renterButton.addActionListener(e -> {
            selectedRole = "renter";
            updateRoleButtons();
        });

        ownerButton.addActionListener(e -> {
            selectedRole = "owner";
            updateRoleButtons();
        });
    }

    /**
     * Pins a control to the form's fixed width and centers it.
     */
    private static <T extends JComponent> T cap(T component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);

        component.setPreferredSize(
                new Dimension(FORM_WIDTH, 48)
        );

        component.setMaximumSize(
                new Dimension(FORM_WIDTH, 48)
        );

        return component;
    }

    private JPanel buildRolePanel() {
        JPanel rolePanel =
                new JPanel(new GridLayout(1, 2, 10, 0));

        rolePanel.setOpaque(false);

        cap(rolePanel);

        rolePanel.add(renterButton);
        rolePanel.add(ownerButton);

        return rolePanel;
    }

    private JPanel buildSwitchPanel() {
        JPanel switchPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));

        switchPanel.setOpaque(false);
        switchPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        switchPanel.setMaximumSize(
                new Dimension(FORM_WIDTH, 24)
        );

        switchLabel.setFont(Theme.SECONDARY);
        switchLabel.setForeground(Theme.MUTED);

        switchButton.setFont(Theme.SECONDARY_BOLD);
        switchButton.setForeground(Theme.PURPLE);

        switchButton.setOpaque(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setBorderPainted(false);
        switchButton.setFocusPainted(false);

        switchButton.setBorder(
                new EmptyBorder(0, 0, 0, 0)
        );

        switchButton.setMargin(
                new Insets(0, 0, 0, 0)
        );

        switchButton.setCursor(
                java.awt.Cursor.getPredefinedCursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        switchButton.addActionListener(e -> {
            signupMode = !signupMode;
            updateForm();
        });

        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);

        return switchPanel;
    }

    // ---- Small helpers ---------------------------------------------------

    private static <T extends JComponent> T left(T component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    /**
     * Centers a component horizontally inside its BoxLayout container.
     */
    private static <T extends JComponent> T center(T component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        return component;
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);

        label.setFont(Theme.SECONDARY_BOLD);
        label.setForeground(Theme.BURGUNDY);

        // Align label text with the left edge of its input.
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        label.setPreferredSize(
                new Dimension(FORM_WIDTH, 22)
        );

        label.setMaximumSize(
                new Dimension(FORM_WIDTH, 22)
        );

        return label;
    }

    private void addField(
            JPanel panel,
            JLabel label,
            JComponent field
    ) {
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));

        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(field);

        panel.add(Box.createVerticalStrut(18));
    }

    private void styleRoleButton(
            RoundedButton button,
            boolean selected
    ) {
        if (selected) {
            button.setColors(
                    Theme.PURPLE,
                    Theme.PURPLE,
                    Theme.WHITE,
                    Theme.PURPLE
            );
        } else {
            button.setColors(
                    Theme.WHITE,
                    Theme.HOVER_BG,
                    Theme.PURPLE,
                    Theme.BLUE
            );
        }
    }

    private void updateRoleButtons() {
        boolean renter = selectedRole.equals("renter");

        styleRoleButton(renterButton, renter);
        styleRoleButton(ownerButton, !renter);
    }

    private void updateForm() {
        titleLabel.setText(
                signupMode ? "Create an account" : "Welcome back"
        );

        subtitleLabel.setText(
                signupMode
                        ? "Enter your details to get started."
                        : "Log in to continue to your account."
        );

        nameLabel.setVisible(signupMode);
        nameBox.setVisible(signupMode);

        submitButton.setText(
                signupMode ? "Create account" : "Log in"
        );

        switchLabel.setText(
                signupMode
                        ? "Already have an account? "
                        : "Don't have an account? "
        );

        switchButton.setText(
                signupMode ? "Log in" : "Sign up"
        );

        updateRoleButtons();

        formFields.revalidate();
        formFields.repaint();
    }

    // ---- Form logic ------------------------------------------------------

    private void submitForm() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = nameField.getText().trim();

        if (signupMode) {
            AuthResult result = controller.register(
                    fullName,
                    email,
                    password,
                    selectedRole.equals("owner"),
                    selectedRole.equals("renter")
            );

            Toast.show(
                    this,
                    result.message(),
                    result.success()
            );

            if (result.success()) {
                signupMode = false;
                passwordField.setText("");
                updateForm();
            }

        } else {
            AuthResult result = controller.login(
                    email,
                    password,
                    selectedRole
            );

            if (result.success()) {
                onLogin.accept(result.user(), selectedRole);
            } else {
                Toast.showError(this, result.message());
            }
        }
    }
}

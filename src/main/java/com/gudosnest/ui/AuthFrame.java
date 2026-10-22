package com.gudosnest.ui;

import com.gudosnest.dao.UserDAO;
import com.gudosnest.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

public class AuthFrame extends JFrame {

    // ---- Palette -------------------------------------------------------
    private static final Color BURGUNDY = Color.decode("#36151E");
    private static final Color PURPLE = Color.decode("#593F62");
    private static final Color VIOLET = Color.decode("#7B6D8D");
    private static final Color BLUE = Color.decode("#8499B1");
    private static final Color LIGHT_BLUE = Color.decode("#A5C4D4");

    // Neutrals derived from the palette
    private static final Color PAGE_BG = new Color(234, 231, 238);
    private static final Color WHITE = Color.WHITE;
    private static final Color MUTED = new Color(115, 108, 124);
    private static final Color HINT = new Color(168, 161, 177);
    private static final Color FIELD_BG = new Color(250, 249, 252);
    private static final Color FIELD_BORDER = new Color(227, 222, 232);

    private final UserDAO dao = new UserDAO();

    private String selectedRole = "renter";
    private boolean signupMode = false;

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;

    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel nameLabel;
    private JLabel switchLabel;

    private InputBox nameBox;

    private RoundedButton renterButton;
    private RoundedButton ownerButton;
    private RoundedButton submitButton;
    private JButton switchButton;

    private JPanel formFields;

    private ToastPanel toast;
    private javax.swing.Timer toastTimer;

    public AuthFrame() {
        setTitle("Gudo's Nest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 740);
        setMinimumSize(new Dimension(880, 680));
        setLocationRelativeTo(null);

        buildUI();
        initToast();
    }

    // ---- UI construction -----------------------------------------------

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        // One big rounded white card holds both halves.
        RoundedPanel card = new RoundedPanel(32, WHITE, FIELD_BORDER);
        card.setLayout(new BorderLayout());

        // The gradient panel sits inset inside the card, like the reference.
        JPanel brandWrap = new JPanel(new BorderLayout());
        brandWrap.setOpaque(false);
        brandWrap.setBorder(new EmptyBorder(10, 10, 10, 10));
        brandWrap.add(buildBrandPanel(), BorderLayout.CENTER);
        brandWrap.setPreferredSize(new Dimension(420, 0));

        card.add(brandWrap, BorderLayout.WEST);
        card.add(buildFormScroll(), BorderLayout.CENTER);

        root.add(card, BorderLayout.CENTER);
        setContentPane(root);
        updateForm();
    }

    private JPanel buildBrandPanel() {
        BrandPanel brand = new BrandPanel();
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(30, 30, 32, 30));

        JLabel logo = left(new JLabel("Gudo's Nest"));
        logo.setFont(new Font("SansSerif", Font.BOLD, 20));
        logo.setForeground(BURGUNDY);

        JLabel logoCaption = left(new JLabel("YOUR SPACE. YOUR PLACE."));
        logoCaption.setFont(new Font("SansSerif", Font.BOLD, 10));
        logoCaption.setForeground(PURPLE);

        JLabel eyebrow = left(new JLabel("A better place to begin."));
        eyebrow.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eyebrow.setForeground(BURGUNDY);

        JLabel welcome = left(new JLabel(
                "<html>Find a place<br>to call your own.</html>"));
        welcome.setFont(new Font("SansSerif", Font.BOLD, 32));
        welcome.setForeground(BURGUNDY);

        JLabel description = left(new JLabel(
                "<html>Whether you're looking for a home<br>"
                        + "or managing a property, start here.</html>"));
        description.setFont(new Font("SansSerif", Font.PLAIN, 13));
        description.setForeground(BURGUNDY);

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

    private JScrollPane buildFormScroll() {
        formFields = new JPanel();
        formFields.setLayout(new BoxLayout(formFields, BoxLayout.Y_AXIS));
        formFields.setOpaque(false);
        formFields.setBorder(new EmptyBorder(14, 52, 14, 52));

        titleLabel = left(new JLabel());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setForeground(BURGUNDY);

        subtitleLabel = left(new JLabel());
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(MUTED);

        // Inputs
        nameField = new HintTextField("Your full name");
        emailField = new HintTextField("you@example.com");
        passwordField = new HintPasswordField("Enter your password");

        final char hiddenEcho = passwordField.getEchoChar();
        EyeButton eye = new EyeButton();
        eye.addActionListener(e -> {
            boolean reveal = passwordField.getEchoChar() != 0;
            passwordField.setEchoChar(reveal ? (char) 0 : hiddenEcho);
            eye.setRevealed(reveal);
        });

        nameBox = new InputBox(nameField, null);
        InputBox emailBox = new InputBox(emailField, null);
        InputBox passwordBox = new InputBox(passwordField, eye);

        nameLabel = makeLabel("Full name");

        // Role toggle
        renterButton = new RoundedButton("Renter");
        ownerButton = new RoundedButton("Owner");
        renterButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        ownerButton.setFont(new Font("SansSerif", Font.BOLD, 13));

        renterButton.addActionListener(e -> {
            selectedRole = "renter";
            updateRoleButtons();
        });
        ownerButton.addActionListener(e -> {
            selectedRole = "owner";
            updateRoleButtons();
        });

        JPanel rolePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        rolePanel.setOpaque(false);
        rolePanel.setPreferredSize(new Dimension(300, 46));
        rolePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        rolePanel.add(renterButton);
        rolePanel.add(ownerButton);
        left(rolePanel);

        // Primary action
        submitButton = new RoundedButton("");
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.setColors(BURGUNDY, PURPLE, WHITE, null);
        submitButton.setShadow(true);
        submitButton.setPreferredSize(new Dimension(300, 62));
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        submitButton.addActionListener(e -> submitForm());
        left(submitButton);

        // Switch login / sign up
        JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        switchPanel.setOpaque(false);
        switchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        left(switchPanel);

        switchLabel = new JLabel();
        switchLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        switchLabel.setForeground(MUTED);

        switchButton = new JButton();
        switchButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        switchButton.setForeground(PURPLE);
        switchButton.setOpaque(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setBorderPainted(false);
        switchButton.setFocusPainted(false);
        switchButton.setBorder(new EmptyBorder(0, 0, 0, 0));
        switchButton.setMargin(new Insets(0, 0, 0, 0));
        switchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchButton.addActionListener(e -> {
            signupMode = !signupMode;
            updateForm();
        });

        switchPanel.add(switchLabel);
        switchPanel.add(switchButton);

        // Assemble (glue at both ends keeps the form vertically centered)
        formFields.add(Box.createVerticalGlue());
        formFields.add(left(new AsteriskIcon()));
        formFields.add(Box.createVerticalStrut(16));
        formFields.add(titleLabel);
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(subtitleLabel);
        formFields.add(Box.createVerticalStrut(24));

        addField(formFields, nameLabel, nameBox);
        addField(formFields, makeLabel("Email address"), emailBox);
        addField(formFields, makeLabel("Password"), passwordBox);

        formFields.add(makeLabel("I want to continue as"));
        formFields.add(Box.createVerticalStrut(8));
        formFields.add(rolePanel);
        formFields.add(Box.createVerticalStrut(22));

        formFields.add(submitButton);
        formFields.add(Box.createVerticalStrut(18));
        formFields.add(switchPanel);
        formFields.add(Box.createVerticalGlue());

        // Scrolling only kicks in if the window is too short for the form.
        JScrollPane scroll = new JScrollPane(formFields);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // ---- Small helpers --------------------------------------------------

    private static <T extends JComponent> T left(T component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    private JLabel makeLabel(String text) {
        JLabel label = left(new JLabel(text));
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(BURGUNDY);
        return label;
    }

    private void addField(JPanel panel, JLabel label, JComponent field) {
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        panel.add(Box.createVerticalStrut(18));
    }

    private void styleRoleButton(RoundedButton button, boolean selected) {
        if (selected) {
            button.setColors(PURPLE, PURPLE, WHITE, PURPLE);
        } else {
            button.setColors(WHITE, new Color(244, 241, 248), PURPLE, BLUE);
        }
    }

    private void updateRoleButtons() {
        boolean renter = selectedRole.equals("renter");
        styleRoleButton(renterButton, renter);
        styleRoleButton(ownerButton, !renter);
    }

    private void updateForm() {
        titleLabel.setText(signupMode ? "Create an account" : "Welcome back");
        subtitleLabel.setText(signupMode
                ? "Enter your details to get started."
                : "Log in to continue to your account.");

        nameLabel.setVisible(signupMode);
        nameBox.setVisible(signupMode);

        submitButton.setText(signupMode ? "Create account" : "Log in");
        switchLabel.setText(signupMode
                ? "Already have an account? "
                : "Don't have an account? ");
        switchButton.setText(signupMode ? "Log in" : "Sign up");

        updateRoleButtons();

        formFields.revalidate();
        formFields.repaint();
        revalidate();
        repaint();
    }

    // ---- Form logic (unchanged) -----------------------------------------

    private void submitForm() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = nameField.getText().trim();

        if (email.isEmpty() || password.isEmpty()
                || (signupMode && fullName.isEmpty())) {
            showToast("Please fill in all required fields.", false);
            return;
        }

        if (signupMode) {
            boolean success = dao.register(
                    fullName,
                    email,
                    password,
                    selectedRole.equals("owner"),
                    selectedRole.equals("renter"));

            if (success) {
                showToast("Account created! You can now log in.", true);
                signupMode = false;
                passwordField.setText("");
                updateForm();
            } else {
                showToast("Registration failed. The email may already exist.",
                        false);
            }

        } else {
            User user = dao.login(email, password, selectedRole);

            if (user == null) {
                showToast("Login failed. Check your details, selected role, "
                        + "or account status.", false);
                return;
            }

            showToast("Welcome, " + user.getFullName() + "!", true);
        }
    }

    // ---- On-screen messages (replace pop-up dialogs) --------------------

    private void initToast() {
        toast = new ToastPanel();
        toast.setVisible(false);
        toast.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toast.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hideToast();
            }
        });
        getLayeredPane().add(toast, JLayeredPane.POPUP_LAYER);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                positionToast();
            }
        });
    }

    /** Shows a message on the frame itself. Click it to dismiss early. */
    private void showToast(String message, boolean success) {
        toast.setMessage(message, success);
        positionToast();
        toast.setVisible(true);

        if (toastTimer != null) {
            toastTimer.stop();
        }
        toastTimer = new javax.swing.Timer(4500, e -> hideToast());
        toastTimer.setRepeats(false);
        toastTimer.start();
    }

    private void hideToast() {
        if (toastTimer != null) {
            toastTimer.stop();
        }
        toast.setVisible(false);
    }

    private void positionToast() {
        Dimension size = toast.getPreferredSize();

        // Center over the form side: page margin (28) + brand panel (420).
        int left = 28 + 420;
        int right = getLayeredPane().getWidth() - 28;
        int x = (left + right) / 2 - size.width / 2;

        toast.setBounds(x, 14, size.width, size.height);
        toast.validate();
        toast.repaint();
    }

    // ---- Custom components ----------------------------------------------

    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    private static void paintHint(JTextComponent c, Graphics g, String hint) {
        if (c.getDocument().getLength() != 0) {
            return;
        }
        Graphics2D g2 = smooth(g);
        g2.setColor(HINT);
        g2.setFont(c.getFont());
        FontMetrics fm = g2.getFontMetrics();
        Insets in = c.getInsets();
        g2.drawString(hint, in.left,
                (c.getHeight() - fm.getHeight()) / 2 + fm.getAscent());
        g2.dispose();
    }

    /** Floating message card (icon + text) that sits on top of the window. */
    private static class ToastPanel extends JPanel {
        private static final Color ROSE = new Color(196, 70, 96);
        private static final int TEXT_WIDTH = 340;

        private final JLabel label = new JLabel();
        private boolean success;

        ToastPanel() {
            super(new BorderLayout());
            setOpaque(false);
            // 8px of the border is room for the drop shadow.
            setBorder(new EmptyBorder(8 + 14, 8 + 56, 8 + 14, 8 + 22));

            label.setFont(new Font("SansSerif", Font.BOLD, 13));
            label.setForeground(BURGUNDY);
            add(label, BorderLayout.CENTER);
        }

        void setMessage(String message, boolean success) {
            this.success = success;
            // Short messages stay on one line; long ones wrap at TEXT_WIDTH.
            label.setText(message);
            if (label.getPreferredSize().width > TEXT_WIDTH) {
                label.setText(wrap(message));
            }
            repaint();
        }

        private int textWidth(String s) {
            label.setText(s);
            return label.getPreferredSize().width;
        }

        /** Greedy word wrap, so we don't depend on HTML width support. */
        private String wrap(String message) {
            StringBuilder html = new StringBuilder("<html>");
            String line = "";
            for (String word : message.split(" ")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (!line.isEmpty() && textWidth(candidate) > TEXT_WIDTH) {
                    html.append(escape(line)).append("<br>");
                    line = word;
                } else {
                    line = candidate;
                }
            }
            return html.append(escape(line)).append("</html>").toString();
        }

        private static String escape(String s) {
            return s.replace("&", "&amp;").replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int bx = 8;
            int by = 8;
            int bw = getWidth() - 16;
            int bh = getHeight() - 16;

            for (int i = 0; i < 7; i++) {
                g2.setColor(new Color(54, 21, 30, 9));
                g2.fillRoundRect(bx - i, by - i + 4, bw + 2 * i, bh + 2 * i,
                        18 + 2 * i, 18 + 2 * i);
            }

            g2.setColor(WHITE);
            g2.fillRoundRect(bx, by, bw, bh, 18, 18);
            g2.setColor(FIELD_BORDER);
            g2.drawRoundRect(bx, by, bw - 1, bh - 1, 18, 18);

            int d = 26;
            int ix = bx + 16;
            int iy = by + (bh - d) / 2;
            float cx = ix + d / 2f;
            float cy = iy + d / 2f;

            g2.setColor(success ? PURPLE : ROSE);
            g2.fillOval(ix, iy, d, d);

            g2.setColor(WHITE);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            if (success) {
                Path2D check = new Path2D.Float();
                check.moveTo(cx - 5.5f, cy + 0.5f);
                check.lineTo(cx - 1.5f, cy + 4.5f);
                check.lineTo(cx + 6f, cy - 4f);
                g2.draw(check);
            } else {
                g2.draw(new Line2D.Float(cx, cy - 6f, cx, cy + 1.5f));
                g2.fill(new Ellipse2D.Float(cx - 1.4f, cy + 4.2f, 2.8f, 2.8f));
            }
            g2.dispose();
        }
    }

    /** Rounded white card with a hairline border. */
    private static class RoundedPanel extends JPanel {
        private final int arc;
        private final Color fill;
        private final Color border;

        RoundedPanel(int arc, Color fill, Color border) {
            this.arc = arc;
            this.fill = fill;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            RoundRectangle2D shape = new RoundRectangle2D.Float(
                    0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g2.setColor(fill);
            g2.fill(shape);
            g2.setColor(border);
            g2.draw(shape);
            g2.dispose();
        }
    }

    /** Left panel: soft, blurred colour blobs built from the palette. */
    private static class BrandPanel extends JPanel {
        BrandPanel() {
            setOpaque(false);
        }

        private static void blob(Graphics2D g2, Shape shape,
                float cx, float cy, float radius,
                Color c, int alpha) {
            Color[] colors = {
                    new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha),
                    new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)
            };
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx, cy), radius,
                    new float[] { 0f, 1f }, colors));
            g2.fill(shape);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, 26, 26);

            g2.setPaint(new GradientPaint(0, 0, new Color(249, 246, 251),
                    0, h, new Color(214, 228, 236)));
            g2.fill(shape);

            blob(g2, shape, w * 0.85f, h * 0.30f, w * 0.75f, BLUE, 110);
            blob(g2, shape, w * 0.20f, h * 0.58f, w * 0.65f, PURPLE, 150);
            blob(g2, shape, w * 0.55f, h * 0.72f, w * 0.70f, VIOLET, 170);
            blob(g2, shape, w * 0.88f, h * 0.92f, w * 0.50f, LIGHT_BLUE, 200);

            g2.dispose();
        }
    }

    /** Rounded input container that wraps a text field (and optional icon). */
    private static class InputBox extends JPanel {
        private boolean focused;

        InputBox(JTextComponent field, JComponent trailing) {
            super(new BorderLayout(8, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(0, 14, 0, 10));

            field.setOpaque(false);
            field.setBorder(null);
            field.setFont(new Font("SansSerif", Font.PLAIN, 14));
            field.setForeground(BURGUNDY);
            field.setCaretColor(BURGUNDY);
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });

            add(field, BorderLayout.CENTER);
            if (trailing != null) {
                add(trailing, BorderLayout.EAST);
            }

            setPreferredSize(new Dimension(300, 48));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
            setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            RoundRectangle2D shape = new RoundRectangle2D.Float(
                    0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(FIELD_BG);
            g2.fill(shape);
            g2.setStroke(new BasicStroke(focused ? 1.6f : 1f));
            g2.setColor(focused ? VIOLET : FIELD_BORDER);
            g2.draw(shape);
            g2.dispose();
        }
    }

    private static class HintTextField extends JTextField {
        private final String hint;

        HintTextField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintHint(this, g, hint);
        }
    }

    private static class HintPasswordField extends JPasswordField {
        private final String hint;

        HintPasswordField(String hint) {
            this.hint = hint;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintHint(this, g, hint);
        }
    }

    /** Eye icon that toggles password visibility. */
    private static class EyeButton extends JButton {
        private boolean revealed;

        EyeButton() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setFocusable(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(28, 28));
            setToolTipText("Show or hide password");
        }

        void setRevealed(boolean revealed) {
            this.revealed = revealed;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;

            g2.setColor(VIOLET);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));

            Path2D eye = new Path2D.Float();
            eye.moveTo(cx - 9, cy);
            eye.quadTo(cx, cy - 10, cx + 9, cy);
            eye.quadTo(cx, cy + 10, cx - 9, cy);
            g2.draw(eye);
            g2.drawOval(Math.round(cx - 3), Math.round(cy - 3), 6, 6);

            if (!revealed) {
                g2.draw(new Line2D.Float(cx - 8, cy + 8, cx + 8, cy - 8));
            }
            g2.dispose();
        }
    }

    /** Small asterisk mark above the heading. */
    private static class AsteriskIcon extends JComponent {
        AsteriskIcon() {
            setPreferredSize(new Dimension(30, 30));
            setMaximumSize(new Dimension(30, 30));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            float r = Math.min(getWidth(), getHeight()) / 2f - 2f;

            g2.setColor(PURPLE);
            g2.setStroke(new BasicStroke(3.6f, BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND));
            for (int i = 0; i < 3; i++) {
                double a = Math.toRadians(90 + i * 60);
                float dx = (float) (Math.cos(a) * r);
                float dy = (float) (Math.sin(a) * r);
                g2.draw(new Line2D.Float(cx - dx, cy - dy, cx + dx, cy + dy));
            }
            g2.dispose();
        }
    }

    /**
     * Fully self-painted rounded button, so colours look the same on every
     * look-and-feel (system look-and-feels often ignore setBackground).
     */
    private static class RoundedButton extends JButton {
        private Color fill = WHITE;
        private Color hoverFill = null;
        private Color textColor = BURGUNDY;
        private Color borderColor = null;
        private boolean shadow = false;
        private final int arc = 16;

        RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setRolloverEnabled(true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        void setColors(Color fill, Color hoverFill,
                Color textColor, Color borderColor) {
            this.fill = fill;
            this.hoverFill = hoverFill;
            this.textColor = textColor;
            this.borderColor = borderColor;
            repaint();
        }

        void setShadow(boolean shadow) {
            this.shadow = shadow;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = smooth(g);
            int w = getWidth();
            int h = getHeight();
            int bodyH = shadow ? h - 10 : h;

            if (shadow) {
                for (int i = 0; i < 6; i++) {
                    g2.setColor(new Color(54, 21, 30, 10));
                    g2.fillRoundRect(6 - i, 8 - i, w - 12 + 2 * i,
                            bodyH + 2 * i, arc + 2 * i, arc + 2 * i);
                }
            }

            Color f = (getModel().isRollover() && hoverFill != null)
                    ? hoverFill
                    : fill;
            g2.setColor(f);
            g2.fillRoundRect(0, 0, w, bodyH, arc, arc);

            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.drawRoundRect(0, 0, w - 1, bodyH - 1, arc, arc);
            }

            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (bodyH - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(text, tx, ty);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use the default Swing appearance.
            }

            new AuthFrame().setVisible(true);
        });
    }
}
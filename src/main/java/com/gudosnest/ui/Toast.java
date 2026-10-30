
package com.gudosnest.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * Transient white notification card shown at the top-centre of a window.
 * Click to dismiss early; auto-dismisses after ~4.5s. One toast per window.
 *
 * Toast.show(anchor, "Saved.");                 // success
 * Toast.showError(anchor, "Something broke.");  // error
 */
public final class Toast {

    // One card per window so switching screens doesn't stack toasts.
    private static final Map<Window, Card> CARDS = new HashMap<>();

    public static void show(Component anchor, String message) {
        show(anchor, message, true);
    }

    public static void showError(Component anchor, String message) {
        show(anchor, message, false);
    }

    /** Shows a success (true) or error (false) toast near the anchor. */
    public static void show(
            Component anchor,
            String message,
            boolean success
    ) {
        Window w = (anchor instanceof Window win)
                ? win
                : SwingUtilities.getWindowAncestor(anchor);

        JLayeredPane lp = layeredPaneOf(w);
        if (lp == null) return;

        Card card = CARDS.computeIfAbsent(w, win -> {
            Card c = new Card();
            lp.add(c, JLayeredPane.POPUP_LAYER);
            return c;
        });

        card.show(message, success, lp);
    }

    private static JLayeredPane layeredPaneOf(Window w) {
        if (w instanceof JFrame f) return f.getLayeredPane();
        if (w instanceof JDialog d) return d.getLayeredPane();
        return null;
    }

    /** The white card + its fade timer. */
    private static final class Card extends JPanel {

        private static final int TEXT_WIDTH = 340;
        private static final int ICON_SIZE = 24;
        private static final int ICON_GAP = 12;

        private final JLabel label = new JLabel();
        private Timer timer;
        private boolean success;

        Card() {
            super(new BorderLayout());

            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Reserve space for the icon on the LEFT.
            int leftInset = 8 + 14 + ICON_SIZE + ICON_GAP;
            int rightInset = 8 + 18;

            setBorder(new EmptyBorder(
                    8 + 12,
                    leftInset,
                    8 + 12,
                    rightInset
            ));

            label.setFont(Theme.NOTIFICATION);
            label.setForeground(Theme.BURGUNDY);

            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dismiss();
                }
            });
        }

        void show(
                String message,
                boolean success,
                JLayeredPane lp
        ) {
            this.success = success;

            label.setText(
                    textWidth(message) > TEXT_WIDTH
                            ? wrap(message)
                            : message
            );

            setVisible(true);
            position(lp);

            if (timer != null) timer.stop();

            timer = new Timer(4500, e -> dismiss());
            timer.setRepeats(false);
            timer.start();
        }

        private void dismiss() {
            if (timer != null) timer.stop();
            setVisible(false);
        }

        private void position(JLayeredPane lp) {
            Dimension size = getPreferredSize();

            int x = (lp.getWidth() - size.width) / 2;

            setBounds(x, 14, size.width, size.height);

            validate();
            repaint();
        }

        private int textWidth(String s) {
            label.setText(s);
            return label.getPreferredSize().width;
        }

        private String wrap(String message) {
            StringBuilder html = new StringBuilder("<html>");
            String line = "";

            for (String word : message.split(" ")) {
                String candidate = line.isEmpty()
                        ? word
                        : line + " " + word;

                if (!line.isEmpty()
                        && textWidth(candidate) > TEXT_WIDTH) {

                    html.append(escape(line)).append("<br>");
                    line = word;

                } else {
                    line = candidate;
                }
            }

            return html.append(escape(line))
                    .append("</html>")
                    .toString();
        }

        private static String escape(String s) {
            return s.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int bx = 8;
            int by = 8;
            int bw = getWidth() - 16;
            int bh = getHeight() - 16;

            // Shadow
            for (int i = 0; i < 7; i++) {
                g2.setColor(
                        Theme.withAlpha(Theme.BURGUNDY, 9)
                );

                g2.fillRoundRect(
                        bx - i,
                        by - i + 4,
                        bw + 2 * i,
                        bh + 2 * i,
                        18 + 2 * i,
                        18 + 2 * i
                );
            }

            // Card background
            g2.setColor(Theme.WHITE);
            g2.fillRoundRect(bx, by, bw, bh, 18, 18);

            g2.setColor(Theme.HAIRLINE);
            g2.drawRoundRect(
                    bx, by, bw - 1, bh - 1, 18, 18
            );

            // Icon
            int d = ICON_SIZE;
            int ix = bx + 14;
            int iy = by + (bh - d) / 2;

            float cx = ix + d / 2f;
            float cy = iy + d / 2f;

            g2.setColor(success ? Theme.PURPLE : Theme.RED);
            g2.fillOval(ix, iy, d, d);

            g2.setColor(Theme.WHITE);

            g2.setStroke(new BasicStroke(
                    2.2f,
                    BasicStroke.CAP_ROUND,
                    BasicStroke.JOIN_ROUND
            ));

            if (success) {
                Path2D check = new Path2D.Float();

                check.moveTo(cx - 5.5f, cy + 0.5f);
                check.lineTo(cx - 1.5f, cy + 4.5f);
                check.lineTo(cx + 6f, cy - 4f);

                g2.draw(check);

            } else {
                g2.draw(new Line2D.Float(
                        cx, cy - 6f,
                        cx, cy + 1.5f
                ));

                g2.fill(new Ellipse2D.Float(
                        cx - 1.4f,
                        cy + 4.2f,
                        2.8f,
                        2.8f
                ));
            }

            g2.dispose();
        }
    }

    private Toast() {}
}

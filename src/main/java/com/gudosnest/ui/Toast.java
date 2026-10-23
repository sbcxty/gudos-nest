package com.gudosnest.ui;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Transient notification bubble (matches the AuthFrame toast look):
 * dark plum rounded pill fades in bottom-right of the window and removes
 * itself. Use instead of a JOptionPane for non-blocking confirmations.
 */
public final class Toast {

    /** Shows a self-removing toast anchored to the frame containing 'anchor'. */
    public static void show(JComponent anchor, String message) {
        Window w = SwingUtilities.getWindowAncestor(anchor);
        if (w instanceof JFrame) {
            show((JFrame) w, message);
        }
    }

    public static void show(JFrame frame, String message) {
        if (frame == null) return;
        JLayeredPane lp = frame.getLayeredPane();
        if (lp == null) return;

        final float[] alpha = {0f};
        JPanel toast = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setComposite(java.awt.AlphaComposite.SrcOver.derive(alpha[0]));
                g.setColor(Theme.PRIMARY);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g.dispose();
            }
        };
        toast.setOpaque(false);
        JLabel text = new JLabel(message);
        text.setFont(Theme.font("body-bold", 13));
        text.setForeground(java.awt.Color.WHITE);
        text.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        toast.add(text);

        Runnable place = () -> {
            toast.setSize(toast.getPreferredSize());
            toast.setLocation(
                lp.getWidth() - toast.getWidth() - 26,
                lp.getHeight() - toast.getHeight() - 26);
            toast.revalidate();
            toast.repaint();
        };
        place.run();
        frame.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { place.run(); }
        });
        lp.add(toast, JLayeredPane.MODAL_LAYER);

        // fade in, hold, fade out, remove
        Timer fin = new Timer(30, null);
        fin.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                alpha[0] += 0.12f;
                toast.repaint();
                if (alpha[0] >= 1f) {
                    alpha[0] = 1f;
                    toast.repaint();
                    fin.stop();
                }
            }
        });
        fin.start();

        Timer fout = new Timer(30, null);
        fout.addActionListener(new ActionListener() {
            int hold = 0;
            @Override public void actionPerformed(ActionEvent e) {
                if (hold++ < 70) return;                 // ~2.1s visible hold
                alpha[0] -= 0.08f;
                toast.repaint();
                if (alpha[0] <= 0f) {
                    fout.stop();
                    lp.remove(toast);
                    lp.revalidate();
                    lp.repaint();
                }
            }
        });
        fout.start();
    }

    private Toast() {}
}

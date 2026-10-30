package com.gudosnest.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/** Left auth panel: a soft gradient built from the palette colour blobs. */
public class BrandPanel extends JPanel {
    public BrandPanel() {
        setOpaque(false);
    }

    private static void blob(Graphics2D g2, Shape shape,
            float cx, float cy, float radius, Color c, int alpha) {
        Color[] colors = {
                Theme.withAlpha(c, alpha),
                Theme.withAlpha(c, 0)
        };
        g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(cx, cy), radius, new float[] { 0f, 1f }, colors));
        g2.fill(shape);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        RoundRectangle2D shape = new RoundRectangle2D.Float(0, 0, w, h, 26, 26);

        g2.setPaint(new GradientPaint(0, 0, Color.decode("#F9F6FB"),
                0, h, Color.decode("#D6E4EC")));
        g2.fill(shape);

        blob(g2, shape, w * 0.85f, h * 0.30f, w * 0.75f, Theme.BLUE, 110);
        blob(g2, shape, w * 0.20f, h * 0.58f, w * 0.65f, Theme.PURPLE, 150);
        blob(g2, shape, w * 0.55f, h * 0.72f, w * 0.70f, Theme.VIOLET, 170);
        blob(g2, shape, w * 0.88f, h * 0.92f, w * 0.50f, Theme.LIGHT_BLUE, 200);

        g2.dispose();
    }
}

package com.gudosnest.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * A FlowLayout that wraps onto the next row when the container is too narrow,
 * so fixed-size components form a real grid that always fits the available
 * width (and never overflows horizontally).
 *
 * Plain FlowLayout inside a JScrollPane sizes the view to the sum of all
 * children, which produces one endless row; this layout instead measures
 * against the container's actual (viewport) width and wraps.
 */
public class WrapLayout extends FlowLayout {

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension min = layoutSize(target, false);
        min.width -= (getHgap() + 1);
        return min;
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            // Find the closest ancestor with a known width (the viewport).
            Container container = target;
            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }
            int targetWidth = container.getSize().width;
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            int x = 0;
            int y = vgap;
            int rowHeight = 0;
            int minWidth = 0;

            for (Component c : target.getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();
                if (x == 0 || (x + d.width + hgap) <= maxWidth) {
                    x += (x > 0) ? hgap : 0;
                    x += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                } else {
                    x = d.width;
                    y += rowHeight + vgap;
                    rowHeight = d.height;
                }
                minWidth = Math.max(minWidth, x);
            }

            y += rowHeight + vgap;
            return new Dimension(minWidth + horizontalInsetsAndGap, y + insets.bottom);
        }
    }
}

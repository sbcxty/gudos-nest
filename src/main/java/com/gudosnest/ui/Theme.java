package com.gudosnest.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Single source of truth for the app's colors and fonts.
 * Palette and typography come straight from the design reference.
 * One name per color - no aliases - so it stays easy to read.
 */
public final class Theme {

    // ------------------------------------------------------------------
    // Core palette (from the design reference)
    // ------------------------------------------------------------------
    /** Sidebar base, primary branding, text accents. */
    public static final Color BURGUNDY  = Color.decode("#36151E");
    /** Sidebar gradient, navigation accents, headings. */
    public static final Color PURPLE    = Color.decode("#593F62");
    /** Decorative gradient effects and background glow. */
    public static final Color VIOLET    = Color.decode("#7B6D8D");
    /** Decorative sidebar and banner gradients. */
    public static final Color BLUE      = Color.decode("#8499B1");
    /** Secondary accents, avatar gradient, sidebar subtitles. */
    public static final Color LIGHT_BLUE= Color.decode("#A5C4D4");
    /** Main page background (light lavender). */
    public static final Color PAGE_BG   = Color.decode("#EAE7EE");
    /** Content cards, active nav buttons, text on the dark sidebar. */
    public static final Color WHITE     = Color.WHITE;
    /** Secondary text and subdued labels (muted gray-purple). */
    public static final Color MUTED     = Color.decode("#736C7C");
    /** Borders around cards and input fields (light gray-lavender). */
    public static final Color HAIRLINE  = Color.decode("#E3DEE8");

    // ------------------------------------------------------------------
    // Derived helpers (kept small and purpose-specific)
    // ------------------------------------------------------------------
    /** Slightly lifted burgundy for hover states. */
    public static final Color BURGUNDY_HOVER = Color.decode("#4A2130");
    /** Input / field background. */
    public static final Color FIELD_BG  = Color.decode("#FAF9FC");
    /** Soft hover wash on light surfaces. */
    public static final Color HOVER_BG  = Color.decode("#F4F1F8");
    /** Placeholder / hint text. */
    public static final Color HINT      = Color.decode("#A8A1B1");
    /** Zebra stripe on tables. */
    public static final Color ZEBRA     = Color.decode("#F8F6FB");

    // Status colors (not in the reference, kept for chips).
    public static final Color GREEN = Color.decode("#2F9E62");
    public static final Color AMBER = Color.decode("#C88A1C");
    public static final Color RED   = Color.decode("#C03939");

    // ------------------------------------------------------------------
    // Typography - SansSerif family, sizes per the reference
    // ------------------------------------------------------------------
    /** Page title - Bold 30. */
    public static final Font PAGE_TITLE      = f(Font.BOLD, 30);
    /** App name / logo - Bold 22. */
    public static final Font LOGO            = f(Font.BOLD, 22);
    /** Section headings - Bold 18. */
    public static final Font SECTION         = f(Font.BOLD, 18);
    /** Card and panel headings - Bold 16. */
    public static final Font CARD_HEADING    = f(Font.BOLD, 16);
    /** Greeting line - Regular 16. */
    public static final Font GREETING        = f(Font.PLAIN, 16);
    /** Body text - Regular 14. */
    public static final Font BODY            = f(Font.PLAIN, 14);
    /** Buttons - Bold 14. */
    public static final Font BUTTON          = f(Font.BOLD, 14);
    /** Notifications - Bold 14. */
    public static final Font NOTIFICATION    = f(Font.BOLD, 14);
    /** Input fields - Regular 14. */
    public static final Font INPUT           = f(Font.PLAIN, 14);
    /** Secondary text - Regular 12. */
    public static final Font SECONDARY       = f(Font.PLAIN, 12);
    /** Secondary text, bold (eyebrows / labels) - Bold 12. */
    public static final Font SECONDARY_BOLD  = f(Font.BOLD, 12);

    /** Builds a SansSerif font (the whole app uses one family). */
    public static Font f(int style, int size) {
        return new Font("SansSerif", style, size);
    }

    /** Returns the same color with a custom alpha (0-255). */
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    private Theme() {}
}

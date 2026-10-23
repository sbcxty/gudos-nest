package com.gudosnest.ui;

import java.awt.*;

/**
 * Central place for colors, fonts, and small factories used to build
 * the visual language of the app.
 *
 * PALETTE v2 - aligned with the AuthFrame concept (Shekainah):
 * gradient lavender hero panel, serif display headings, dark-plum primary
 * buttons on soft neutral surfaces.
 */
public final class Theme {
    // ---- Palette ----
    public static final Color BG        = new Color(0xF2, 0xEE, 0xF6); // app background (soft lavender-gray)
    public static final Color SURFACE   = new Color(0xFF, 0xFF, 0xFF); // cards / table containers
    public static final Color BORDER    = new Color(0xE6, 0xE0, 0xEB); // hairline borders

    public static final Color LAVENDER  = new Color(0xC9, 0xB8, 0xE4); // gradient start (hero/sidebar)
    public static final Color ROSE      = new Color(0xEE, 0xD3, 0xDC); // gradient end (hero/sidebar)
    public static final Color CREAM     = new Color(0xF5, 0xEF, 0xE6); // warm neutral (window bands)

    public static final Color PRIMARY   = new Color(0x3B, 0x1F, 0x32); // dark plum - primary buttons/active nav
    public static final Color PRIMARY_D = new Color(0x2A, 0x16, 0x24); // deeper plum - hover/footer band
    public static final Color ACCENT    = new Color(0x8A, 0x6C, 0xA8); // violet accent - badges, highlights

    public static final Color TEXT      = new Color(0x24, 0x1B, 0x22); // main text (soft black)
    public static final Color TEXT_MUT  = new Color(0x7A, 0x71, 0x80); // secondary text

    public static final Color GREEN     = new Color(0x2F, 0x9E, 0x62); // status: good / paid / approved
    public static final Color AMBER     = new Color(0xC8, 0x8A, 0x1C); // status: pending / low stock
    public static final Color RED       = new Color(0xC0, 0x39, 0x39); // status: damaged / overdue
    public static final Color BLUE      = new Color(0x5B, 0x5B, 0xB8); // status: info / in progress

    // ---- Typography (designed for Poppins/Inter, falls back to Segoe UI/Georgia) ----
    public static Font font(String style, int size) {
        String family = style.startsWith("head") ? headingFamily() : bodyFamily();
        return new Font(family, deriveFlags(style), size);
    }

    private static String headingFamily() {
        return anyInstalled(new String[]{"Poppins", "Georgia", "Dialog"});
    }

    private static String anyInstalled(String[] candidates) {
        for (String c : candidates) {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (String installed : ge.getAvailableFontFamilyNames()) {
                if (installed.equalsIgnoreCase(c)) return installed;
            }
        }
        return "Segoe UI";
    }

    private static String bodyFamily() {
        return anyInstalled(new String[]{"Inter", "Segoe UI", "Dialog"});
    }

    private static String familyFor(String style) {
        return anyInstalled(new String[]{
                style.startsWith("head") ? "Poppins" : "Inter", "Segoe UI", "Dialog"});
    }

    private static int deriveFlags(String style) {
        switch (style) {
            case "head-bold": return Font.BOLD;
            case "head":      return Font.BOLD;
            case "body":      return Font.PLAIN;
            case "body-bold": return Font.BOLD;
            default:          return Font.PLAIN;
        }
    }

    private Theme() {}
}

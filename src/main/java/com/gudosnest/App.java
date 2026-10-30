package com.gudosnest;

import javax.swing.SwingUtilities;

import com.gudosnest.auth.AuthFrame;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
            () -> new AuthFrame().setVisible(true)
        );
    }
}
package com.github.loafabreadly.franchisetracker;

import com.github.loafabreadly.franchisetracker.scene.MainMenu;

/**
 * The entry point for the Franchise Tracker application.
 */
public class Main {
    /**
     * Main method to launch the Franchise Tracker TUI.
     * @param args Command-line arguments
     * @throws Exception if the application fails to start
     */
    public static void main(String[] args) throws Exception {
        MainMenu.createMenu();
    }
}

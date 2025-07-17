package com.github.loafabreadly.franchisetracker;

import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        FranchiseTracker tracker = new FranchiseTracker();
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        BasicWindow window = new BasicWindow("Franchise Tracker");

        Panel panel = new Panel();
        panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("Welcome to Franchise Tracker!"));
        panel.addComponent(new Button("Create New Save", () -> {
            // TODO: Implement create new save logic
            window.setTitle("Create New Save");
        }));
        panel.addComponent(new Button("Edit Roster", () -> {
            // TODO: Implement edit roster logic
            window.setTitle("Edit Roster");
        }));
        panel.addComponent(new Button("Set Draft Picks", () -> {
            // TODO: Implement set draft picks logic
            window.setTitle("Set Draft Picks");
        }));
        panel.addComponent(new Button("View/Edit Lines", () -> {
            // TODO: Implement view/edit lines logic
            window.setTitle("View/Edit Lines");
        }));
        panel.addComponent(new Button("Enter End of Season Stats", () -> {
            // TODO: Implement enter stats logic
            window.setTitle("Enter End of Season Stats");
        }));
        panel.addComponent(new Button("Save Franchise", () -> {
            // TODO: Implement save logic
            window.setTitle("Save Franchise");
        }));
        panel.addComponent(new Button("Load Franchise", () -> {
            // TODO: Implement load logic
            window.setTitle("Load Franchise");
        }));
        panel.addComponent(new Button("Exit", window::close));

        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
        screen.stopScreen();
    }
}

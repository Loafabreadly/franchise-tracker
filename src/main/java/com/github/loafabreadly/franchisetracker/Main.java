package com.github.loafabreadly.franchisetracker;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.TerminalSize;
import java.util.*;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws Exception {
        FranchiseTracker tracker = new FranchiseTracker();
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        Screen screen = terminalFactory.createScreen();
        screen.startScreen();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);
        BasicWindow window = new BasicWindow("Franchise Tracker");

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        mainPanel.addComponent(new Label("Welcome to Franchise Tracker!"));
        mainPanel.addComponent(new Button("Create New Save", () -> {
            window.setTitle("Create New Save");
            Panel createPanel = new Panel();
            createPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            TextBox nhlTeamBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
            TextBox ahlTeamBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
            createPanel.addComponent(new Label("NHL Team Name:"));
            createPanel.addComponent(nhlTeamBox);
            createPanel.addComponent(new Label("AHL Affiliate Name:"));
            createPanel.addComponent(ahlTeamBox);
            // You can add more fields for initial roster, draft picks, etc. here
            Button createButton = new Button("Create", () -> {
                String nhlTeam = nhlTeamBox.getText();
                String ahlTeam = ahlTeamBox.getText();
                // For now, create with empty roster and draft picks
                tracker.createNewSave(nhlTeam, ahlTeam, new ArrayList<>(), new ArrayList<>());
                window.setTitle("Franchise Tracker");
                window.setComponent(mainPanel);
            });
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker");
                window.setComponent(mainPanel);
            });
            createPanel.addComponent(createButton);
            createPanel.addComponent(backButton);
            window.setComponent(createPanel);
        }));
        mainPanel.addComponent(new Button("Edit Roster", () -> {
            // TODO: Implement edit roster logic
            window.setTitle("Edit Roster");
        }));
        mainPanel.addComponent(new Button("Set Draft Picks", () -> {
            // TODO: Implement set draft picks logic
            window.setTitle("Set Draft Picks");
        }));
        mainPanel.addComponent(new Button("View/Edit Lines", () -> {
            // TODO: Implement view/edit lines logic
            window.setTitle("View/Edit Lines");
        }));
        mainPanel.addComponent(new Button("Enter End of Season Stats", () -> {
            // TODO: Implement enter stats logic
            window.setTitle("Enter End of Season Stats");
        }));
        mainPanel.addComponent(new Button("Save Franchise", () -> {
            // TODO: Implement save logic
            window.setTitle("Save Franchise");
        }));
        mainPanel.addComponent(new Button("Load Franchise", () -> {
            // TODO: Implement load logic
            window.setTitle("Load Franchise");
        }));
        mainPanel.addComponent(new Button("Exit", window::close));

        window.setComponent(mainPanel);
        textGUI.addWindowAndWait(window);
        screen.stopScreen();
    }
}

package com.github.loafabreadly.franchisetracker;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.github.loafabreadly.franchisetracker.scene.Game;
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
            TextBox gmName = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
            createPanel.addComponent(new Label("NHL Team Name:"));
            createPanel.addComponent(nhlTeamBox);
            createPanel.addComponent(new Label("AHL Affiliate Name:"));
            createPanel.addComponent(ahlTeamBox);
            createPanel.addComponent(new Label("General Manager Name:"));
            createPanel.addComponent(gmName); 
            Button createButton = new Button("Create", () -> {
                String nhlTeam = nhlTeamBox.getText();
                String ahlTeam = ahlTeamBox.getText();
                tracker.createNewSave(nhlTeam, ahlTeam, null, null, gmName.getText());
                // Prompt for filename to save
                Panel savePanel = new Panel();
                savePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
                TextBox fileNameBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
                savePanel.addComponent(new Label("Enter filename to save (e.g., save.json):"));
                savePanel.addComponent(fileNameBox);
                Button saveButton = new Button("Save", () -> {
                    String fileName = fileNameBox.getText();
                    try {
                        tracker.saveToFile(fileName);
                        window.setTitle("Franchise Tracker");
                        window.setComponent(mainPanel);
                    } catch (Exception e) {
                        savePanel.addComponent(new Label("Error saving: " + e.getMessage()));
                    }
                });
                Button backButton2 = new Button("Back", () -> {
                    window.setTitle("Franchise Tracker");
                    window.setComponent(mainPanel);
                });
                savePanel.addComponent(saveButton);
                savePanel.addComponent(backButton2);
                window.setTitle("Save Franchise");
                window.setComponent(savePanel);
            });
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker");
                window.setComponent(mainPanel);
            });
            createPanel.addComponent(createButton);
            createPanel.addComponent(backButton);
            window.setComponent(createPanel);
        }));
        mainPanel.addComponent(new Button("Load Franchise", () -> {
            window.setTitle("Load Franchise");
            Panel loadPanel = new Panel();
            loadPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            TextBox fileNameBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
            loadPanel.addComponent(new Label("Enter filename to load (e.g., save.json):"));
            loadPanel.addComponent(fileNameBox);
            Button loadButton = new Button("Load", () -> {
                String fileName = fileNameBox.getText();
                try {
                    tracker.loadFromFile(fileName);
                    window.setTitle("Franchise Tracker - " + tracker.getSelectedNHLTeam().getName());
                    Panel gamePanel = new Game();
                    window.setComponent(gamePanel);
                } catch (Exception e) {
                    loadPanel.addComponent(new Label("Error loading: " + e.getMessage()));
                }
            });
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker");
                window.setComponent(mainPanel);
            });
            loadPanel.addComponent(loadButton);
            loadPanel.addComponent(backButton);
            window.setComponent(loadPanel);
        }));
        mainPanel.addComponent(new Button("Exit", window::close));

        window.setComponent(mainPanel);
        textGUI.addWindowAndWait(window);
        screen.stopScreen();
    }

    private String validateSaveName(String fileName) {
        if (fileName.endsWith(".json")) {
            return fileName;
        }
        else {
            return fileName + ".json"; // Append .json if not present
        }
    }
}

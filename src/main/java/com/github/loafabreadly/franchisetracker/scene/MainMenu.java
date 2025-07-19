package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.Utils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;


public class MainMenu extends Panel {

    private static final Logger logger = LogManager.getLogger(MainMenu.class);
    static FranchiseTracker tracker = new FranchiseTracker();

    public static Panel createMenu() {
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        try {
            Screen screen = terminalFactory.createScreen();
            screen.startScreen();
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
            TextBox seasonBox = new TextBox().setValidationPattern(Pattern.compile("\\d{4}")).setPreferredSize(new TerminalSize(6, 1));
            createPanel.addComponent(new Label("NHL Team Name:"));
            createPanel.addComponent(nhlTeamBox);
            createPanel.addComponent(new Label("AHL Affiliate Name:"));
            createPanel.addComponent(ahlTeamBox);
            createPanel.addComponent(new Label("General Manager Name:"));
            createPanel.addComponent(gmName);
            createPanel.addComponent(new Label("Season to start on (Defaults to 2025):"));
            createPanel.addComponent(seasonBox);
            Button createButton = new Button("Create", () -> {
                String nhlTeam = nhlTeamBox.getText();
                String ahlTeam = ahlTeamBox.getText();
                int season = seasonBox.getText().isEmpty() ? 2025 : Integer.parseInt(seasonBox.getText());
                Panel savePanel = new Panel();
                savePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
                TextBox fileNameBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
                savePanel.addComponent(new Label("Enter filename to save (e.g., my-save):"));
                savePanel.addComponent(fileNameBox);
                Button saveButton = new Button("Save", () -> {
                    tracker = new FranchiseTracker(nhlTeam, ahlTeam, new ArrayList<>(), new ArrayList<>(), gmName.getText(), season);
                    String fileName = Utils.validateSaveName(fileNameBox.getText());
                    try {
                        tracker.saveFranchise(fileName);
                        window.setTitle("Franchise Tracker");
                        window.setComponent(new Game(tracker, screen, window, logger));
                    } catch (Exception e) {
                        logger.error("Error saving: ", e);
                        savePanel.addComponent(new Label("Error saving: " + e.getMessage()));
                    }
                });
                Button backButton = new Button("Back", () -> {
                    window.setTitle("Franchise Tracker");
                    window.setComponent(mainPanel);
                });
                savePanel.addComponent(saveButton);
                savePanel.addComponent(backButton);
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
            java.io.File dir = new java.io.File(System.getProperty("user.dir"));
            java.io.File[] nhlFiles = dir.listFiles((d, name) -> name.endsWith(".nhl"));
            if (nhlFiles != null && nhlFiles.length > 0) {
                for (java.io.File file : nhlFiles) {
                    loadPanel.addComponent(new Button(file.getName(), () -> {
                        try {
                            tracker = tracker.loadFranchise(file.getName());
                            window.setTitle("Franchise Tracker - " + tracker.getSelectedNHLTeam().getName());
                            Panel gamePanel = new Game(tracker, screen, window, logger);
                            window.setComponent(gamePanel);
                        } catch (Exception e) {
                            logger.error("Error loading: ", e);
                            loadPanel.addComponent(new Label("Error loading: " + e.getMessage()));
                        }
                    }));
                }
            } else {
                loadPanel.addComponent(new Label("No .nhl save files found in this directory."));
            }
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker");
                window.setComponent(mainPanel);
            });
            loadPanel.addComponent(backButton);
            window.setComponent(loadPanel);
        }));
        mainPanel.addComponent(new Button("Exit", () -> {
            try {
                screen.stopScreen();
            } catch (Exception e) {
                logger.error("Error stopping screen: ", e);
            } finally {
                System.exit(0);
            }
        }));
        MultiWindowTextGUI textGUI = new MultiWindowTextGUI(screen);
        window.setComponent(mainPanel);
        textGUI.addWindowAndWait(window);
        return mainPanel;
        } catch (Exception e) {
            logger.error("Exception during screen creation: ", e);
            return null;
        }
    }
}

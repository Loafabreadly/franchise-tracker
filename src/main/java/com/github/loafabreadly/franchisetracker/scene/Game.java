package com.github.loafabreadly.franchisetracker.scene;

import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.Utils;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.screen.Screen;

/**
 * Game is the main gameplay panel that provides access to all franchise management features.
 */
public class Game extends Panel {
    
    /**
     * Creates the main game panel with all management options.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     */
    public Game(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        
        addComponent(new Label("=== " + tracker.getSelectedNHLTeam().getName() + " ==="));
        addComponent(new Label("GM: " + tracker.getGeneralManagerName()));
        addComponent(new Label("Season: " + tracker.getCurrentSeason()));
        addComponent(new EmptySpace());
               
        addComponent(new Button("Season Stats & Progression", () -> {
            window.setComponent(new SeasonStatsEditor(tracker, screen, window, logger, this));
        }));

        addComponent(new Button("View/Edit Lineup", () -> {
            window.setComponent(new LineupEditor(tracker, screen, window, logger, this));
        }));

        addComponent(new Button("Edit Players", () -> {
            window.setComponent(new PlayerEditor(tracker, screen, window, logger, this));
        }));

        addComponent(new Button("View Roster Summary", () -> {
            showRosterSummary(tracker, screen, window, logger);
        }));

        addComponent(new EmptySpace());

        addComponent(new Button("Save Franchise", () -> {
            window.setTitle("Save Franchise");
            Panel savePanel = new Panel();
            savePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            TextBox fileNameBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
            savePanel.addComponent(new Label("Enter filename to save (e.g., save):"));
            savePanel.addComponent(fileNameBox);
            Button saveButton = new Button("Save", () -> {
                String fileName = Utils.validateSaveName(fileNameBox.getText());
                try {
                    tracker.saveFranchise(fileName);
                    window.setTitle("Franchise Tracker - " + tracker.getSelectedNHLTeam().getName());
                    window.setComponent(this);
                } catch (Exception e) {
                    logger.error("Error saving: ", e);
                    savePanel.addComponent(new Label("Error saving: " + e.getMessage()));
                }
            });
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker - " + tracker.getSelectedNHLTeam().getName());
                window.setComponent(this);
            });
            savePanel.addComponent(saveButton);
            savePanel.addComponent(backButton);
            window.setComponent(savePanel);
        }));

        addComponent(new Button("Return to Main Menu", () -> {
            MainMenu.createMenu();
        }));

        addComponent(new Button("Exit", () -> {
            try {
                screen.stopScreen();
            } catch (Exception e) {
                logger.error("Error stopping screen: ", e);
            } finally {
                System.exit(0);
            }
        }));
    }

    private void showRosterSummary(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger) {
        Panel summaryPanel = new Panel();
        summaryPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        summaryPanel.addComponent(new Label("=== Roster Summary ==="));
        summaryPanel.addComponent(new EmptySpace());

        // NHL Roster
        summaryPanel.addComponent(new Label("--- NHL: " + tracker.getSelectedNHLTeam().getName() + " ---"));
        if (tracker.getSelectedNHLTeam().getRoster() != null && !tracker.getSelectedNHLTeam().getRoster().isEmpty()) {
            summaryPanel.addComponent(new Label(String.format("%-20s %-5s %-3s", "Name", "Pos", "OVR")));
            for (var player : tracker.getSelectedNHLTeam().getRoster()) {
                summaryPanel.addComponent(new Label(String.format("%-20s %-5s %-3d",
                    player.getFirstName() + " " + player.getLastName(),
                    player.getPosition() != null ? player.getPosition().name().substring(0, Math.min(5, player.getPosition().name().length())) : "N/A",
                    player.getOverall())));
            }
            summaryPanel.addComponent(new Label("Total: " + tracker.getSelectedNHLTeam().getRoster().size() + " players"));
        } else {
            summaryPanel.addComponent(new Label("  (Empty roster)"));
        }

        summaryPanel.addComponent(new EmptySpace());

        // AHL Roster
        summaryPanel.addComponent(new Label("--- AHL: " + tracker.getSelectedAHLTeam().getName() + " ---"));
        if (tracker.getSelectedAHLTeam().getRoster() != null && !tracker.getSelectedAHLTeam().getRoster().isEmpty()) {
            summaryPanel.addComponent(new Label(String.format("%-20s %-5s %-3s", "Name", "Pos", "OVR")));
            for (var player : tracker.getSelectedAHLTeam().getRoster()) {
                summaryPanel.addComponent(new Label(String.format("%-20s %-5s %-3d",
                    player.getFirstName() + " " + player.getLastName(),
                    player.getPosition() != null ? player.getPosition().name().substring(0, Math.min(5, player.getPosition().name().length())) : "N/A",
                    player.getOverall())));
            }
            summaryPanel.addComponent(new Label("Total: " + tracker.getSelectedAHLTeam().getRoster().size() + " players"));
        } else {
            summaryPanel.addComponent(new Label("  (Empty roster)"));
        }

        summaryPanel.addComponent(new EmptySpace());
        summaryPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(summaryPanel);
    }
}

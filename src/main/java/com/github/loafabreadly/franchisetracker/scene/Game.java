package com.github.loafabreadly.franchisetracker.scene;

import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.screen.Screen;

public class Game extends Panel {
    
    public Game(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        addComponent(new Label("Game Scene"));
               
        addComponent(new Button("Enter end of season stats", () -> {
            // Logic to enter end of season stats
            System.out.println("Entering end of season stats...");
        }));
        addComponent(new Button("View Lineup", () -> {
            // Logic to view lineup
            System.out.println("Viewing lineup...");
        }));
        addComponent(new Button("Team Career Stats", () -> {
            // Logic to view stats
            System.out.println("Viewing stats...");
        }));

        addComponent(new Button("Edit Players", () -> {
            // Logic to edit players
            System.out.println("Editing players...");
        }));

        addComponent(new Button("Edit Draft Picks", () -> {
            // Logic to edit draft picks
            System.out.println("Editing draft picks...");
        }));
        
        addComponent(new Button("Edit Contacts", () -> {
            // Logic to edit contracts
            System.out.println("Editing contracts...");
        }));
        addComponent(new Button("Edit Trades", () -> {
            // Logic to view trades
            System.out.println("Viewing trades...");
        }));
        addComponent(new Button("Save Franchise", () -> {
            window.setTitle("Save Franchise");
            Panel savePanel = new Panel();
            savePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
            TextBox fileNameBox = new TextBox().setValidationPattern(Pattern.compile(".*")).setPreferredSize(new TerminalSize(30, 1));
            savePanel.addComponent(new Label("Enter filename to save (e.g., save.json):"));
            savePanel.addComponent(fileNameBox);
             Button saveButton = new Button("Save", () -> {
                String fileName = fileNameBox.getText();
                try {
                    tracker.saveFranchise(fileName);
                    window.setTitle("Franchise Tracker");
                    window.setComponent(this);
                } catch (Exception e) {
                    savePanel.addComponent(new Label("Error saving: " + e.getMessage()));
                }
            });
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker");
                window.setComponent(MainMenu.createMenu());
            });
            savePanel.addComponent(saveButton);
            savePanel.addComponent(backButton);
            window.setComponent(savePanel);
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
}

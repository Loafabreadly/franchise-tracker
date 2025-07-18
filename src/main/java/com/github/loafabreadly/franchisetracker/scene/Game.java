package com.github.loafabreadly.franchisetracker.scene;

import java.util.regex.Pattern;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;

public class Game extends Panel {
    
    public Game() {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        addComponent(new Label("Game Scene"));
        
        // Example components
        addComponent(new Button("Start Game", () -> {
            // Logic to start the game
            System.out.println("Game started!");
        }));
        
        addComponent(new Button("View Stats", () -> {
            // Logic to view stats
            System.out.println("Viewing stats...");
        }));
        
        addComponent(new Button("Exit Game", () -> {
            // Logic to exit the game
            System.out.println("Exiting game...");
        }));
    }
    /* 
    mainPanel.addComponent(new Button("Save Franchise", () -> {
            window.setTitle("Save Franchise");
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
            Button backButton = new Button("Back", () -> {
                window.setTitle("Franchise Tracker");
                window.setComponent(mainPanel);
            });
            savePanel.addComponent(saveButton);
            savePanel.addComponent(backButton);
            window.setComponent(savePanel);
        }));
        */
}

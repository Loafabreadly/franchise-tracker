package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * PlayerEditor provides a TUI for adding, editing, and removing players from the roster.
 */
public class PlayerEditor extends Panel {

    /**
     * Creates the player editor panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public PlayerEditor(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        addComponent(new Label("=== Player Editor ==="));
        addComponent(new EmptySpace());

        // Show current roster
        refreshRosterView(tracker, screen, window, logger, parentPanel);
    }

    private void refreshRosterView(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        addComponent(new Label("=== Player Editor ==="));
        addComponent(new EmptySpace());

        // NHL Roster section
        addComponent(new Label("--- NHL Roster (" + tracker.getSelectedNHLTeam().getName() + ") ---"));
        if (tracker.getSelectedNHLTeam().getRoster() != null && !tracker.getSelectedNHLTeam().getRoster().isEmpty()) {
            for (Player player : tracker.getSelectedNHLTeam().getRoster()) {
                String playerInfo = String.format("%s %s | %s | OVR: %d", 
                    player.getFirstName(), player.getLastName(), 
                    player.getPosition() != null ? player.getPosition().name() : "N/A",
                    player.getOverall());
                addComponent(new Button(playerInfo, () -> {
                    showPlayerActions(player, tracker, screen, window, logger, parentPanel, false);
                }));
            }
        } else {
            addComponent(new Label("  (No players on roster)"));
        }

        addComponent(new EmptySpace());

        // AHL Roster section
        addComponent(new Label("--- AHL Roster (" + tracker.getSelectedAHLTeam().getName() + ") ---"));
        if (tracker.getSelectedAHLTeam().getRoster() != null && !tracker.getSelectedAHLTeam().getRoster().isEmpty()) {
            for (Player player : tracker.getSelectedAHLTeam().getRoster()) {
                String playerInfo = String.format("%s %s | %s | OVR: %d", 
                    player.getFirstName(), player.getLastName(), 
                    player.getPosition() != null ? player.getPosition().name() : "N/A",
                    player.getOverall());
                addComponent(new Button(playerInfo, () -> {
                    showPlayerActions(player, tracker, screen, window, logger, parentPanel, true);
                }));
            }
        } else {
            addComponent(new Label("  (No players on roster)"));
        }

        addComponent(new EmptySpace());
        addComponent(new Button("Add New Player", () -> {
            showAddPlayerForm(tracker, screen, window, logger, parentPanel);
        }));
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showPlayerActions(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel, boolean isAHL) {
        Panel actionPanel = new Panel();
        actionPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        actionPanel.addComponent(new Label("=== " + player.getFirstName() + " " + player.getLastName() + " ==="));
        actionPanel.addComponent(new Label("Position: " + (player.getPosition() != null ? player.getPosition().name() : "N/A")));
        actionPanel.addComponent(new Label("Overall: " + player.getOverall()));
        if (player.getContract() != null) {
            actionPanel.addComponent(new Label("Contract: $" + player.getContract().getAav() + "M x " + player.getContract().getTermInYears() + " years"));
        }
        actionPanel.addComponent(new EmptySpace());

        if (isAHL) {
            actionPanel.addComponent(new Button("Call Up to NHL", () -> {
                tracker.callUpPlayerFromAHL(player);
                refreshRosterView(tracker, screen, window, logger, parentPanel);
                window.setComponent(this);
            }));
        } else {
            actionPanel.addComponent(new Button("Send Down to AHL", () -> {
                tracker.sendPlayerToAHL(player);
                refreshRosterView(tracker, screen, window, logger, parentPanel);
                window.setComponent(this);
            }));
        }

        actionPanel.addComponent(new Button("Edit Player", () -> {
            showEditPlayerForm(player, tracker, screen, window, logger, parentPanel);
        }));

        actionPanel.addComponent(new Button("Remove from Roster", () -> {
            if (isAHL) {
                tracker.removePlayerFromAHL(player);
            } else {
                tracker.removePlayerFromNHL(player);
            }
            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        actionPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(actionPanel);
    }

    private void showAddPlayerForm(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Add New Player ==="));

        TextBox firstNameBox = new TextBox().setPreferredSize(new TerminalSize(20, 1));
        TextBox lastNameBox = new TextBox().setPreferredSize(new TerminalSize(20, 1));
        TextBox overallBox = new TextBox().setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));

        ComboBox<PLAYER_POS> positionCombo = new ComboBox<>();
        for (PLAYER_POS pos : PLAYER_POS.values()) {
            positionCombo.addItem(pos);
        }

        ComboBox<String> teamCombo = new ComboBox<>();
        teamCombo.addItem("NHL - " + tracker.getSelectedNHLTeam().getName());
        teamCombo.addItem("AHL - " + tracker.getSelectedAHLTeam().getName());

        formPanel.addComponent(new Label("First Name:"));
        formPanel.addComponent(firstNameBox);
        formPanel.addComponent(new Label("Last Name:"));
        formPanel.addComponent(lastNameBox);
        formPanel.addComponent(new Label("Overall (1-99):"));
        formPanel.addComponent(overallBox);
        formPanel.addComponent(new Label("Position:"));
        formPanel.addComponent(positionCombo);
        formPanel.addComponent(new Label("Team:"));
        formPanel.addComponent(teamCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Add Player", () -> {
            Player newPlayer = new Player();
            newPlayer.setFirstName(firstNameBox.getText());
            newPlayer.setLastName(lastNameBox.getText());
            newPlayer.setOverall(overallBox.getText().isEmpty() ? 70 : Integer.parseInt(overallBox.getText()));
            newPlayer.setPosition(positionCombo.getSelectedItem());
            newPlayer.setCareerStats(new ArrayList<>());

            if (teamCombo.getSelectedIndex() == 0) {
                tracker.addPlayerToNHL(newPlayer);
            } else {
                tracker.addPlayerToAHL(newPlayer);
            }

            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showEditPlayerForm(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Edit Player ==="));

        TextBox firstNameBox = new TextBox(player.getFirstName()).setPreferredSize(new TerminalSize(20, 1));
        TextBox lastNameBox = new TextBox(player.getLastName()).setPreferredSize(new TerminalSize(20, 1));
        TextBox overallBox = new TextBox(String.valueOf(player.getOverall())).setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));

        ComboBox<PLAYER_POS> positionCombo = new ComboBox<>();
        for (PLAYER_POS pos : PLAYER_POS.values()) {
            positionCombo.addItem(pos);
            if (pos == player.getPosition()) {
                positionCombo.setSelectedIndex(Arrays.asList(PLAYER_POS.values()).indexOf(pos));
            }
        }

        formPanel.addComponent(new Label("First Name:"));
        formPanel.addComponent(firstNameBox);
        formPanel.addComponent(new Label("Last Name:"));
        formPanel.addComponent(lastNameBox);
        formPanel.addComponent(new Label("Overall (1-99):"));
        formPanel.addComponent(overallBox);
        formPanel.addComponent(new Label("Position:"));
        formPanel.addComponent(positionCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Changes", () -> {
            player.setFirstName(firstNameBox.getText());
            player.setLastName(lastNameBox.getText());
            player.setOverall(overallBox.getText().isEmpty() ? player.getOverall() : Integer.parseInt(overallBox.getText()));
            player.setPosition(positionCombo.getSelectedItem());

            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }
}

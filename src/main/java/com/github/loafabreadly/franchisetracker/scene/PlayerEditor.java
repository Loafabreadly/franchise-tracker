package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                String potStr = player.getPotential() != null ? " [" + player.getPotential().getDisplayName() + "]" : "";
                String playerInfo = String.format("%s %s | %s | OVR: %d%s", 
                    player.getFirstName(), player.getLastName(), 
                    player.getPosition() != null ? player.getPosition().name() : "N/A",
                    player.getOverall(),
                    potStr);
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
                String potStr = player.getPotential() != null ? " [" + player.getPotential().getDisplayName() + "]" : "";
                String playerInfo = String.format("%s %s | %s | OVR: %d%s", 
                    player.getFirstName(), player.getLastName(), 
                    player.getPosition() != null ? player.getPosition().name() : "N/A",
                    player.getOverall(),
                    potStr);
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
        actionPanel.addComponent(new Label("Age: " + player.getAge()));
        actionPanel.addComponent(new Label("Overall: " + player.getOverall()));
        if (player.getPotential() != null) {
            String accStr = player.getPotentialAccuracy() != null ? " (" + player.getPotentialAccuracy().name() + " accuracy)" : "";
            actionPanel.addComponent(new Label("Potential: " + player.getPotential().getDisplayName() + accStr));
        }
        if (player.getStyle() != null) {
            actionPanel.addComponent(new Label("Play Style: " + player.getStyle().name()));
        }
        if (player.getXFactors() != null && !player.getXFactors().isEmpty()) {
            actionPanel.addComponent(new Label("X-Factors: " + player.getXFactors().size()));
        }
        if (player.getContract() != null) {
            Contract c = player.getContract();
            String twoWay = c.isTwoWay() ? " (2-way)" : "";
            actionPanel.addComponent(new Label("Contract: $" + c.getAav() + "M x " + c.getTermInYears() + " yrs" + twoWay));
        } else {
            actionPanel.addComponent(new Label("Contract: None"));
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

        actionPanel.addComponent(new Button("Quick Contract Edit", () -> {
            showQuickContractEdit(player, tracker, screen, window, logger, parentPanel);
        }));

        actionPanel.addComponent(new Button("Edit X-Factors", () -> {
            showXFactorEditor(player, tracker, screen, window, logger, parentPanel);
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
        TextBox ageBox = new TextBox("20").setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));

        ComboBox<PLAYER_POS> positionCombo = new ComboBox<>();
        for (PLAYER_POS pos : PLAYER_POS.values()) {
            positionCombo.addItem(pos);
        }

        // Use String combo with "(None)" option for nullable fields
        ComboBox<String> potentialCombo = new ComboBox<>();
        potentialCombo.addItem("(None)");
        for (PLAYER_POTENTIAL pot : PLAYER_POTENTIAL.values()) {
            potentialCombo.addItem(pot.getDisplayName());
        }

        ComboBox<POTENTIAL_ACCURACY> accuracyCombo = new ComboBox<>();
        for (POTENTIAL_ACCURACY acc : POTENTIAL_ACCURACY.values()) {
            accuracyCombo.addItem(acc);
        }

        ComboBox<String> styleCombo = new ComboBox<>();
        styleCombo.addItem("(None)");
        for (PLAYER_STYLE style : PLAYER_STYLE.values()) {
            styleCombo.addItem(style.name());
        }

        ComboBox<String> teamCombo = new ComboBox<>();
        teamCombo.addItem("NHL - " + tracker.getSelectedNHLTeam().getName());
        teamCombo.addItem("AHL - " + tracker.getSelectedAHLTeam().getName());

        formPanel.addComponent(new Label("First Name:"));
        formPanel.addComponent(firstNameBox);
        formPanel.addComponent(new Label("Last Name:"));
        formPanel.addComponent(lastNameBox);
        formPanel.addComponent(new Label("Age:"));
        formPanel.addComponent(ageBox);
        formPanel.addComponent(new Label("Overall (1-99):"));
        formPanel.addComponent(overallBox);
        formPanel.addComponent(new Label("Position:"));
        formPanel.addComponent(positionCombo);
        formPanel.addComponent(new Label("Potential:"));
        formPanel.addComponent(potentialCombo);
        formPanel.addComponent(new Label("Potential Accuracy:"));
        formPanel.addComponent(accuracyCombo);
        formPanel.addComponent(new Label("Play Style:"));
        formPanel.addComponent(styleCombo);
        formPanel.addComponent(new Label("Team:"));
        formPanel.addComponent(teamCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Add Player", () -> {
            Player newPlayer = new Player();
            newPlayer.setFirstName(firstNameBox.getText());
            newPlayer.setLastName(lastNameBox.getText());
            newPlayer.setAge(ageBox.getText().isEmpty() ? 20 : Integer.parseInt(ageBox.getText()));
            newPlayer.setOverall(overallBox.getText().isEmpty() ? 70 : Integer.parseInt(overallBox.getText()));
            newPlayer.setPosition(positionCombo.getSelectedItem());
            
            // Convert string selection back to enum (index 0 is "(None)")
            int potIdx = potentialCombo.getSelectedIndex();
            newPlayer.setPotential(potIdx == 0 ? null : PLAYER_POTENTIAL.values()[potIdx - 1]);
            
            newPlayer.setPotentialAccuracy(accuracyCombo.getSelectedItem());
            
            int styleIdx = styleCombo.getSelectedIndex();
            newPlayer.setStyle(styleIdx == 0 ? null : PLAYER_STYLE.values()[styleIdx - 1]);
            
            newPlayer.setCareerStats(new ArrayList<>());
            newPlayer.setXFactors(new ArrayList<>());

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
        TextBox ageBox = new TextBox(String.valueOf(player.getAge())).setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));

        ComboBox<PLAYER_POS> positionCombo = new ComboBox<>();
        for (PLAYER_POS pos : PLAYER_POS.values()) {
            positionCombo.addItem(pos);
            if (pos == player.getPosition()) {
                positionCombo.setSelectedIndex(Arrays.asList(PLAYER_POS.values()).indexOf(pos));
            }
        }

        // Use String combo with "(None)" option for nullable fields
        ComboBox<String> potentialCombo = new ComboBox<>();
        potentialCombo.addItem("(None)");
        int potIdx = 0;
        for (int i = 0; i < PLAYER_POTENTIAL.values().length; i++) {
            PLAYER_POTENTIAL pot = PLAYER_POTENTIAL.values()[i];
            potentialCombo.addItem(pot.getDisplayName());
            if (pot == player.getPotential()) {
                potIdx = i + 1; // +1 because index 0 is "(None)"
            }
        }
        potentialCombo.setSelectedIndex(potIdx);

        ComboBox<POTENTIAL_ACCURACY> accuracyCombo = new ComboBox<>();
        int accIdx = 0;
        for (POTENTIAL_ACCURACY acc : POTENTIAL_ACCURACY.values()) {
            accuracyCombo.addItem(acc);
            if (acc == player.getPotentialAccuracy()) {
                accIdx = Arrays.asList(POTENTIAL_ACCURACY.values()).indexOf(acc);
            }
        }
        accuracyCombo.setSelectedIndex(accIdx);

        ComboBox<String> styleCombo = new ComboBox<>();
        styleCombo.addItem("(None)");
        int styleIdx = 0;
        for (int i = 0; i < PLAYER_STYLE.values().length; i++) {
            PLAYER_STYLE style = PLAYER_STYLE.values()[i];
            styleCombo.addItem(style.name());
            if (style == player.getStyle()) {
                styleIdx = i + 1; // +1 because index 0 is "(None)"
            }
        }
        styleCombo.setSelectedIndex(styleIdx);

        formPanel.addComponent(new Label("First Name:"));
        formPanel.addComponent(firstNameBox);
        formPanel.addComponent(new Label("Last Name:"));
        formPanel.addComponent(lastNameBox);
        formPanel.addComponent(new Label("Age:"));
        formPanel.addComponent(ageBox);
        formPanel.addComponent(new Label("Overall (1-99):"));
        formPanel.addComponent(overallBox);
        formPanel.addComponent(new Label("Position:"));
        formPanel.addComponent(positionCombo);
        formPanel.addComponent(new Label("Potential:"));
        formPanel.addComponent(potentialCombo);
        formPanel.addComponent(new Label("Potential Accuracy:"));
        formPanel.addComponent(accuracyCombo);
        formPanel.addComponent(new Label("Play Style:"));
        formPanel.addComponent(styleCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Changes", () -> {
            player.setFirstName(firstNameBox.getText());
            player.setLastName(lastNameBox.getText());
            player.setAge(ageBox.getText().isEmpty() ? player.getAge() : Integer.parseInt(ageBox.getText()));
            player.setOverall(overallBox.getText().isEmpty() ? player.getOverall() : Integer.parseInt(overallBox.getText()));
            player.setPosition(positionCombo.getSelectedItem());
            
            // Convert string selection back to enum (index 0 is "(None)")
            int selectedPotIdx = potentialCombo.getSelectedIndex();
            player.setPotential(selectedPotIdx == 0 ? null : PLAYER_POTENTIAL.values()[selectedPotIdx - 1]);
            
            player.setPotentialAccuracy(accuracyCombo.getSelectedItem());
            
            int selectedStyleIdx = styleCombo.getSelectedIndex();
            player.setStyle(selectedStyleIdx == 0 ? null : PLAYER_STYLE.values()[selectedStyleIdx - 1]);

            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showQuickContractEdit(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Quick Contract Edit ==="));
        formPanel.addComponent(new Label("Player: " + player.getFullName()));
        formPanel.addComponent(new EmptySpace());

        Contract existing = player.getContract();
        TextBox aavBox = new TextBox(existing != null ? String.valueOf(existing.getAav()) : "1.0").setPreferredSize(new TerminalSize(10, 1));
        TextBox termBox = new TextBox(existing != null ? String.valueOf(existing.getTermInYears()) : "2").setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));

        CheckBox twoWayCheck = new CheckBox("Two-Way Contract");
        if (existing != null) {
            twoWayCheck.setChecked(existing.isTwoWay());
        }

        formPanel.addComponent(new Label("AAV (millions):"));
        formPanel.addComponent(aavBox);
        formPanel.addComponent(new Label("Term (years):"));
        formPanel.addComponent(termBox);
        formPanel.addComponent(twoWayCheck);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Contract", () -> {
            double aav;
            try {
                aav = Double.parseDouble(aavBox.getText());
            } catch (NumberFormatException e) {
                aav = 1.0;
            }
            int term = termBox.getText().isEmpty() ? 2 : Integer.parseInt(termBox.getText());

            Contract contract = existing != null ? existing : new Contract();
            contract.setAav(aav);
            contract.setTermInYears(term);
            contract.setTwoWay(twoWayCheck.isChecked());
            if (existing == null) {
                contract.setSigningYear(tracker.getCurrentSeason());
            }
            player.setContract(contract);

            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Remove Contract", () -> {
            player.setContract(null);
            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showXFactorEditor(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== X-Factor Editor ==="));
        formPanel.addComponent(new Label("Player: " + player.getFullName()));
        formPanel.addComponent(new EmptySpace());

        List<PLAYER_XFACTORS> currentXFactors = player.getXFactors() != null ? new ArrayList<>(player.getXFactors()) : new ArrayList<>();

        // Create checkboxes for each X-Factor
        List<CheckBox> xfactorCheckboxes = new ArrayList<>();
        for (PLAYER_XFACTORS xf : PLAYER_XFACTORS.values()) {
            CheckBox cb = new CheckBox(xf.name());
            cb.setChecked(currentXFactors.contains(xf));
            xfactorCheckboxes.add(cb);
            formPanel.addComponent(cb);
        }

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save X-Factors", () -> {
            List<PLAYER_XFACTORS> selected = new ArrayList<>();
            PLAYER_XFACTORS[] values = PLAYER_XFACTORS.values();
            for (int i = 0; i < xfactorCheckboxes.size(); i++) {
                if (xfactorCheckboxes.get(i).isChecked()) {
                    selected.add(values[i]);
                }
            }
            player.setXFactors(selected);

            refreshRosterView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }
}

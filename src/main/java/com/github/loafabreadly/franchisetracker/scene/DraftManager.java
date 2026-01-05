package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * DraftManager provides a TUI for managing draft picks and recording drafted players.
 */
public class DraftManager extends Panel {

    /**
     * Creates the draft manager panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public DraftManager(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showMainMenu(tracker, screen, window, logger, parentPanel);
    }

    private void showMainMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Draft Manager - Season " + tracker.getCurrentSeason() + " ==="));
        addComponent(new EmptySpace());

        addComponent(new Button("View Draft Pick Inventory", () -> {
            showPickInventory(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Record Draft Selection", () -> {
            showRecordDraftPick(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Draft History", () -> {
            showDraftHistory(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Unsigned Draftees", () -> {
            showUnsignedDraftees(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showPickInventory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel inventoryPanel = new Panel();
        inventoryPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        inventoryPanel.addComponent(new Label("=== Draft Pick Inventory ==="));
        inventoryPanel.addComponent(new EmptySpace());

        // Show picks for next 5 years
        for (int year = tracker.getCurrentSeason(); year <= tracker.getCurrentSeason() + 5; year++) {
            List<DraftPick> yearPicks = tracker.getDraftPicksForYear(year);
            inventoryPanel.addComponent(new Label("--- " + year + " (" + yearPicks.size() + " picks) ---"));
            
            if (yearPicks.isEmpty()) {
                inventoryPanel.addComponent(new Label("  No picks"));
            } else {
                for (DraftPick pick : yearPicks) {
                    inventoryPanel.addComponent(new Label("  " + pick.getDisplayString()));
                }
            }
        }

        inventoryPanel.addComponent(new EmptySpace());
        inventoryPanel.addComponent(new Button("Add Acquired Pick", () -> {
            showAddPickForm(tracker, screen, window, logger, parentPanel);
        }));
        inventoryPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(inventoryPanel);
    }

    private void showAddPickForm(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Add Acquired Draft Pick ==="));
        formPanel.addComponent(new EmptySpace());

        TextBox yearBox = new TextBox(String.valueOf(tracker.getCurrentSeason() + 1))
            .setValidationPattern(Pattern.compile("\\d{4}"))
            .setPreferredSize(new TerminalSize(6, 1));

        ComboBox<String> roundCombo = new ComboBox<>();
        for (int i = 1; i <= 7; i++) {
            roundCombo.addItem("Round " + i);
        }

        TextBox fromTeamBox = new TextBox().setPreferredSize(new TerminalSize(20, 1));

        formPanel.addComponent(new Label("Draft Year:"));
        formPanel.addComponent(yearBox);
        formPanel.addComponent(new Label("Round:"));
        formPanel.addComponent(roundCombo);
        formPanel.addComponent(new Label("Acquired From (team name):"));
        formPanel.addComponent(fromTeamBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Add Pick", () -> {
            DraftPick pick = new DraftPick();
            pick.setYear(Integer.parseInt(yearBox.getText()));
            pick.setRound(roundCombo.getSelectedIndex() + 1);
            pick.setOriginalTeam(fromTeamBox.getText());
            pick.setCurrentOwner(tracker.getSelectedNHLTeam().getName());
            pick.setWasTraded(true);
            pick.setTradedFrom(fromTeamBox.getText());
            
            tracker.getFutureDraftPicks().add(pick);
            showPickInventory(tracker, screen, window, logger, parentPanel);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            showPickInventory(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(formPanel);
    }

    private void showRecordDraftPick(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Record Draft Selection ==="));
        formPanel.addComponent(new EmptySpace());

        // Available picks for current season
        List<DraftPick> availablePicks = tracker.getDraftPicksForYear(tracker.getCurrentSeason());
        if (availablePicks.isEmpty()) {
            formPanel.addComponent(new Label("No draft picks available for " + tracker.getCurrentSeason()));
            formPanel.addComponent(new Button("Back", () -> {
                window.setComponent(this);
            }));
            window.setComponent(formPanel);
            return;
        }

        ComboBox<String> pickCombo = new ComboBox<>();
        for (DraftPick pick : availablePicks) {
            pickCombo.addItem(pick.getDisplayString());
        }

        TextBox firstNameBox = new TextBox().setPreferredSize(new TerminalSize(20, 1));
        TextBox lastNameBox = new TextBox().setPreferredSize(new TerminalSize(20, 1));
        TextBox overallBox = new TextBox("65").setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));
        TextBox ageBox = new TextBox("18").setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(5, 1));
        TextBox pickOverallBox = new TextBox("1").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(5, 1));

        ComboBox<PLAYER_POS> positionCombo = new ComboBox<>();
        for (PLAYER_POS pos : PLAYER_POS.values()) {
            positionCombo.addItem(pos);
        }

        ComboBox<PLAYER_POTENTIAL> potentialCombo = new ComboBox<>();
        for (PLAYER_POTENTIAL pot : PLAYER_POTENTIAL.skaterPotentials()) {
            potentialCombo.addItem(pot);
        }

        formPanel.addComponent(new Label("Select Pick:"));
        formPanel.addComponent(pickCombo);
        formPanel.addComponent(new Label("Overall Pick # (1-224):"));
        formPanel.addComponent(pickOverallBox);
        formPanel.addComponent(new Label("First Name:"));
        formPanel.addComponent(firstNameBox);
        formPanel.addComponent(new Label("Last Name:"));
        formPanel.addComponent(lastNameBox);
        formPanel.addComponent(new Label("Overall Rating:"));
        formPanel.addComponent(overallBox);
        formPanel.addComponent(new Label("Age:"));
        formPanel.addComponent(ageBox);
        formPanel.addComponent(new Label("Position:"));
        formPanel.addComponent(positionCombo);
        formPanel.addComponent(new Label("Potential:"));
        formPanel.addComponent(potentialCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Record Draft Pick", () -> {
            // Create the player
            Player player = new Player();
            player.setFirstName(firstNameBox.getText());
            player.setLastName(lastNameBox.getText());
            player.setOverall(Integer.parseInt(overallBox.getText().isEmpty() ? "65" : overallBox.getText()));
            player.setAge(Integer.parseInt(ageBox.getText().isEmpty() ? "18" : ageBox.getText()));
            player.setPosition(positionCombo.getSelectedItem());
            player.setPotential(potentialCombo.getSelectedItem());
            player.setDraftYear(tracker.getCurrentSeason());
            player.setDraftRound(availablePicks.get(pickCombo.getSelectedIndex()).getRound());
            player.setDraftOverallPick(Integer.parseInt(pickOverallBox.getText().isEmpty() ? "0" : pickOverallBox.getText()));
            player.setCareerStats(new ArrayList<>());

            // Create drafted player record
            DraftedPlayer draftedPlayer = new DraftedPlayer();
            draftedPlayer.setYear(tracker.getCurrentSeason());
            draftedPlayer.setRound(availablePicks.get(pickCombo.getSelectedIndex()).getRound());
            draftedPlayer.setPickOverall(Integer.parseInt(pickOverallBox.getText().isEmpty() ? "0" : pickOverallBox.getText()));
            draftedPlayer.setPlayer(player);
            draftedPlayer.setOverallAtDraft(player.getOverall());
            draftedPlayer.setPosition(player.getPosition());
            draftedPlayer.setPotentialAtDraft(player.getPotential());

            // Record the pick and add player to AHL
            tracker.recordDraftPick(draftedPlayer);
            tracker.addPlayerToAHL(player);

            showMainMenu(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showDraftHistory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel historyPanel = new Panel();
        historyPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        historyPanel.addComponent(new Label("=== Draft History ==="));
        historyPanel.addComponent(new EmptySpace());

        List<DraftedPlayer> allDrafts = tracker.getDraftPicks();
        if (allDrafts == null || allDrafts.isEmpty()) {
            historyPanel.addComponent(new Label("No draft history yet."));
        } else {
            // Group by year
            allDrafts.stream()
                .sorted(Comparator.comparingInt(DraftedPlayer::getYear).reversed()
                    .thenComparingInt(DraftedPlayer::getRound))
                .forEach(dp -> {
                    String nhlStatus = dp.isMadeNHL() ? " [NHL]" : "";
                    String signed = dp.isSignedToContract() ? " ✓" : " ✗";
                    historyPanel.addComponent(new Label(dp.getSummary() + signed + nhlStatus));
                });
        }

        historyPanel.addComponent(new EmptySpace());
        historyPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(historyPanel);
    }

    private void showUnsignedDraftees(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel unsignedPanel = new Panel();
        unsignedPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        unsignedPanel.addComponent(new Label("=== Unsigned Draft Picks ==="));
        unsignedPanel.addComponent(new EmptySpace());

        List<DraftedPlayer> unsigned = tracker.getUnsignedDraftees();
        if (unsigned.isEmpty()) {
            unsignedPanel.addComponent(new Label("All draft picks have been signed."));
        } else {
            for (DraftedPlayer dp : unsigned) {
                unsignedPanel.addComponent(new Button(dp.getSummary(), () -> {
                    // Option to sign the player
                    showSignDrafteeForm(dp, tracker, screen, window, logger, parentPanel);
                }));
            }
        }

        unsignedPanel.addComponent(new EmptySpace());
        unsignedPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(unsignedPanel);
    }

    private void showSignDrafteeForm(DraftedPlayer draftee, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Sign " + draftee.getDisplayName() + " ==="));
        formPanel.addComponent(new EmptySpace());

        TextBox aavBox = new TextBox("0.925").setPreferredSize(new TerminalSize(10, 1));
        TextBox termBox = new TextBox("3").setValidationPattern(Pattern.compile("\\d")).setPreferredSize(new TerminalSize(5, 1));

        formPanel.addComponent(new Label("AAV (millions):"));
        formPanel.addComponent(aavBox);
        formPanel.addComponent(new Label("Term (years, typically 3 for ELC):"));
        formPanel.addComponent(termBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Sign Contract", () -> {
            Contract contract = Contract.createContract(
                Double.parseDouble(aavBox.getText().isEmpty() ? "0.925" : aavBox.getText()),
                Integer.parseInt(termBox.getText().isEmpty() ? "3" : termBox.getText()),
                tracker.getCurrentSeason(),
                CONTRACT_STATUS.RFA
            );
            
            if (draftee.getPlayer() != null) {
                draftee.getPlayer().setContract(contract);
            }
            draftee.setSignedToContract(true);
            draftee.setSignedYear(tracker.getCurrentSeason());

            showUnsignedDraftees(tracker, screen, window, logger, parentPanel);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            showUnsignedDraftees(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(formPanel);
    }
}

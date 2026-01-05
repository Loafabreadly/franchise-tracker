package com.github.loafabreadly.franchisetracker.scene;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * AwardManager provides a TUI for recording and viewing player/team awards.
 */
public class AwardManager extends Panel {

    /**
     * Creates the award manager panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public AwardManager(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showMainMenu(tracker, screen, window, logger, parentPanel);
    }

    private void showMainMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Award Manager - Season " + tracker.getCurrentSeason() + " ==="));
        addComponent(new EmptySpace());

        addComponent(new Button("Record Player Award", () -> {
            showRecordPlayerAward(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Record Team Award", () -> {
            showRecordTeamAward(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Award History", () -> {
            showAwardHistory(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Award Summary", () -> {
            showAwardSummary(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showRecordPlayerAward(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Record Player Award ==="));
        formPanel.addComponent(new EmptySpace());

        // Player selection
        ComboBox<String> playerCombo = new ComboBox<>();
        List<Player> allPlayers = tracker.getAllPlayers();
        for (Player p : allPlayers) {
            playerCombo.addItem(p.getFullName() + " (" + (p.getPosition() != null ? p.getPosition().name() : "N/A") + ")");
        }

        // Award selection - player awards only
        ComboBox<Award.AwardType> awardCombo = new ComboBox<>();
        Award.AwardType[] playerAwards = {
            Award.AwardType.HART_TROPHY,
            Award.AwardType.NORRIS_TROPHY,
            Award.AwardType.VEZINA_TROPHY,
            Award.AwardType.CALDER_TROPHY,
            Award.AwardType.CONN_SMYPHE_TROPHY,
            Award.AwardType.LADY_BYNG_TROPHY,
            Award.AwardType.SELKE_TROPHY,
            Award.AwardType.TED_LINDSEY_AWARD,
            Award.AwardType.BILL_MASTERSON_TROPHY,
            Award.AwardType.KING_CLANCY_TROPHY
        };
        for (Award.AwardType type : playerAwards) {
            awardCombo.addItem(type);
        }

        formPanel.addComponent(new Label("Select Player:"));
        formPanel.addComponent(playerCombo);
        formPanel.addComponent(new Label("Select Award:"));
        formPanel.addComponent(awardCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Record Award", () -> {
            if (!allPlayers.isEmpty()) {
                Award award = new Award();
                award.setYear(tracker.getCurrentSeason());
                award.setRecipientType(Award.AwardCategory.PLAYER);
                award.setPlayer(allPlayers.get(playerCombo.getSelectedIndex()));
                award.setAward(awardCombo.getSelectedItem());
                
                tracker.recordAward(award);
            }
            showMainMenu(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showRecordTeamAward(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Record Team Award ==="));
        formPanel.addComponent(new Label("Team: " + tracker.getSelectedNHLTeam().getName()));
        formPanel.addComponent(new EmptySpace());

        // Award selection - team awards only
        ComboBox<Award.AwardType> awardCombo = new ComboBox<>();
        awardCombo.addItem(Award.AwardType.STANLEY_CUP);
        awardCombo.addItem(Award.AwardType.PRESIDENTS_TROPHY);

        formPanel.addComponent(new Label("Select Award:"));
        formPanel.addComponent(awardCombo);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Record Award", () -> {
            Award award = new Award();
            award.setYear(tracker.getCurrentSeason());
            award.setRecipientType(Award.AwardCategory.TEAM);
            award.setTeam(tracker.getSelectedNHLTeam());
            award.setAward(awardCombo.getSelectedItem());
            
            tracker.recordAward(award);
            showMainMenu(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showAwardHistory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel historyPanel = new Panel();
        historyPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        historyPanel.addComponent(new Label("=== Award History ==="));
        historyPanel.addComponent(new EmptySpace());

        List<Award> awards = tracker.getLeagueAwards();
        if (awards == null || awards.isEmpty()) {
            historyPanel.addComponent(new Label("No awards recorded."));
        } else {
            // Group by season
            Map<Integer, List<Award>> byYear = awards.stream()
                .collect(Collectors.groupingBy(Award::getYear));
            
            byYear.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .forEach(year -> {
                    historyPanel.addComponent(new Label("═══ Season " + year + " ═══"));
                    for (Award award : byYear.get(year)) {
                        String recipient = award.getRecipientType() == Award.AwardCategory.PLAYER
                            ? (award.getPlayer() != null ? award.getPlayer().getFullName() : "Unknown")
                            : (award.getTeam() != null ? award.getTeam().getName() : "Unknown");
                        historyPanel.addComponent(new Label("  " + award.getAward().name() + " - " + recipient));
                    }
                    historyPanel.addComponent(new EmptySpace());
                });
        }

        historyPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(historyPanel);
    }

    private void showAwardSummary(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel summaryPanel = new Panel();
        summaryPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        summaryPanel.addComponent(new Label("=== Award Summary ==="));
        summaryPanel.addComponent(new EmptySpace());

        List<Award> awards = tracker.getLeagueAwards();
        if (awards == null || awards.isEmpty()) {
            summaryPanel.addComponent(new Label("No awards recorded."));
        } else {
            // Count by award type
            Map<Award.AwardType, Long> countByType = awards.stream()
                .collect(Collectors.groupingBy(Award::getAward, Collectors.counting()));

            summaryPanel.addComponent(new Label("--- Team Awards ---"));
            long cups = countByType.getOrDefault(Award.AwardType.STANLEY_CUP, 0L);
            long presidents = countByType.getOrDefault(Award.AwardType.PRESIDENTS_TROPHY, 0L);
            summaryPanel.addComponent(new Label("  Stanley Cups: " + cups));
            summaryPanel.addComponent(new Label("  Presidents' Trophies: " + presidents));
            
            summaryPanel.addComponent(new EmptySpace());
            summaryPanel.addComponent(new Label("--- Player Awards ---"));
            for (Award.AwardType type : Award.AwardType.values()) {
                if (type != Award.AwardType.STANLEY_CUP && type != Award.AwardType.PRESIDENTS_TROPHY) {
                    long count = countByType.getOrDefault(type, 0L);
                    if (count > 0) {
                        summaryPanel.addComponent(new Label("  " + type.name() + ": " + count));
                    }
                }
            }
        }

        summaryPanel.addComponent(new EmptySpace());
        summaryPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(summaryPanel);
    }
}

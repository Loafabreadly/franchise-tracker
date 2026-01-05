package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * LineupEditor provides a TUI for setting forward lines, defense pairs, and goalies.
 */
public class LineupEditor extends Panel {

    /**
     * Creates the lineup editor panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public LineupEditor(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        refreshLineupView(tracker, screen, window, logger, parentPanel);
    }

    private void refreshLineupView(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Lineup Editor - " + tracker.getSelectedNHLTeam().getName() + " ==="));
        addComponent(new EmptySpace());

        Lineup lineup = tracker.getSelectedNHLTeam().getLineup();
        if (lineup == null) {
            lineup = new Lineup();
            tracker.getSelectedNHLTeam().setLineup(lineup);
        }

        // Initialize lists if null
        if (lineup.getForwards() == null) lineup.setForwards(new ArrayList<>());
        if (lineup.getDefensemen() == null) lineup.setDefensemen(new ArrayList<>());
        if (lineup.getGoalies() == null) lineup.setGoalies(new ArrayList<>());

        // Forwards section
        addComponent(new Label("--- Forwards ---"));
        List<Player> forwards = lineup.getForwards();
        for (int i = 0; i < Math.max(12, forwards.size()); i++) {
            int lineNum = (i / 3) + 1;
            String pos = i % 3 == 0 ? "LW" : (i % 3 == 1 ? "C" : "RW");
            String slotLabel = "Line " + lineNum + " " + pos + ": ";
            if (i < forwards.size() && forwards.get(i) != null) {
                Player p = forwards.get(i);
                addComponent(new Label(slotLabel + p.getFirstName() + " " + p.getLastName() + " (" + p.getOverall() + ")"));
            } else {
                addComponent(new Label(slotLabel + "(Empty)"));
            }
        }

        addComponent(new Button("Edit Forwards", () -> {
            showPositionEditor(tracker, screen, window, logger, parentPanel, "FORWARD");
        }));

        addComponent(new EmptySpace());

        // Defense section
        addComponent(new Label("--- Defensemen ---"));
        List<Player> defensemen = lineup.getDefensemen();
        for (int i = 0; i < Math.max(6, defensemen.size()); i++) {
            int pairNum = (i / 2) + 1;
            String side = i % 2 == 0 ? "LD" : "RD";
            String slotLabel = "Pair " + pairNum + " " + side + ": ";
            if (i < defensemen.size() && defensemen.get(i) != null) {
                Player p = defensemen.get(i);
                addComponent(new Label(slotLabel + p.getFirstName() + " " + p.getLastName() + " (" + p.getOverall() + ")"));
            } else {
                addComponent(new Label(slotLabel + "(Empty)"));
            }
        }

        addComponent(new Button("Edit Defensemen", () -> {
            showPositionEditor(tracker, screen, window, logger, parentPanel, "DEFENSE");
        }));

        addComponent(new EmptySpace());

        // Goalies section
        addComponent(new Label("--- Goalies ---"));
        List<Player> goalies = lineup.getGoalies();
        String[] goalieLabels = {"Starter", "Backup"};
        for (int i = 0; i < 2; i++) {
            String slotLabel = goalieLabels[i] + ": ";
            if (i < goalies.size() && goalies.get(i) != null) {
                Player p = goalies.get(i);
                addComponent(new Label(slotLabel + p.getFirstName() + " " + p.getLastName() + " (" + p.getOverall() + ")"));
            } else {
                addComponent(new Label(slotLabel + "(Empty)"));
            }
        }

        addComponent(new Button("Edit Goalies", () -> {
            showPositionEditor(tracker, screen, window, logger, parentPanel, "GOALIE");
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showPositionEditor(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel, String positionType) {
        Panel editorPanel = new Panel();
        editorPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        editorPanel.addComponent(new Label("=== Select " + positionType + " ==="));
        editorPanel.addComponent(new EmptySpace());

        Lineup lineup = tracker.getSelectedNHLTeam().getLineup();
        List<Player> roster = tracker.getSelectedNHLTeam().getRoster();
        if (roster == null) roster = new ArrayList<>();

        // Filter players by position type
        List<Player> eligiblePlayers;
        List<Player> targetList;
        int maxSlots;

        switch (positionType) {
            case "FORWARD":
                eligiblePlayers = roster.stream()
                    .filter(p -> p.getPosition() == PLAYER_POS.CENTER || 
                                 p.getPosition() == PLAYER_POS.LEFT_WING || 
                                 p.getPosition() == PLAYER_POS.RIGHT_WING)
                    .collect(Collectors.toList());
                targetList = lineup.getForwards();
                maxSlots = 12;
                break;
            case "DEFENSE":
                eligiblePlayers = roster.stream()
                    .filter(p -> p.getPosition() == PLAYER_POS.LEFT_DEFENSE || 
                                 p.getPosition() == PLAYER_POS.RIGHT_DEFENSE)
                    .collect(Collectors.toList());
                targetList = lineup.getDefensemen();
                maxSlots = 6;
                break;
            case "GOALIE":
                eligiblePlayers = roster.stream()
                    .filter(p -> p.getPosition() == PLAYER_POS.GOALIE)
                    .collect(Collectors.toList());
                targetList = lineup.getGoalies();
                maxSlots = 2;
                break;
            default:
                eligiblePlayers = new ArrayList<>();
                targetList = new ArrayList<>();
                maxSlots = 0;
        }

        editorPanel.addComponent(new Label("Available players (click to add to lineup):"));
        for (Player player : eligiblePlayers) {
            boolean inLineup = targetList.contains(player);
            String prefix = inLineup ? "[IN] " : "     ";
            editorPanel.addComponent(new Button(prefix + player.getFirstName() + " " + player.getLastName() + " (" + player.getOverall() + ")", () -> {
                if (targetList.contains(player)) {
                    targetList.remove(player);
                } else if (targetList.size() < maxSlots) {
                    targetList.add(player);
                }
                showPositionEditor(tracker, screen, window, logger, parentPanel, positionType);
            }));
        }

        if (eligiblePlayers.isEmpty()) {
            editorPanel.addComponent(new Label("  (No eligible players on roster)"));
        }

        editorPanel.addComponent(new EmptySpace());
        editorPanel.addComponent(new Button("Clear All", () -> {
            targetList.clear();
            showPositionEditor(tracker, screen, window, logger, parentPanel, positionType);
        }));

        editorPanel.addComponent(new Button("Done", () -> {
            refreshLineupView(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        window.setComponent(editorPanel);
    }
}

package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * SeasonStatsEditor provides a TUI for entering end-of-season player and team stats.
 */
public class SeasonStatsEditor extends Panel {

    /**
     * Creates the season stats editor panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public SeasonStatsEditor(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showMainMenu(tracker, screen, window, logger, parentPanel);
    }

    private void showMainMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== End of Season Stats - Season " + tracker.getCurrentSeason() + " ==="));
        addComponent(new EmptySpace());

        addComponent(new Button("Enter Player Stats", () -> {
            showPlayerStatsMenu(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Enter Team Stats", () -> {
            showTeamStatsForm(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Career Stats", () -> {
            showCareerStats(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Advance to Next Season", () -> {
            showAdvanceSeasonConfirm(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showPlayerStatsMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel menuPanel = new Panel();
        menuPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        menuPanel.addComponent(new Label("=== Enter Player Stats - Season " + tracker.getCurrentSeason() + " ==="));
        menuPanel.addComponent(new EmptySpace());
        menuPanel.addComponent(new Label("Select a player:"));

        if (tracker.getSelectedNHLTeam().getRoster() != null) {
            for (Player player : tracker.getSelectedNHLTeam().getRoster()) {
                menuPanel.addComponent(new Button(player.getFirstName() + " " + player.getLastName() + " (" + player.getPosition() + ")", () -> {
                    if (player.getPosition() == PLAYER_POS.GOALIE) {
                        showGoalieStatsForm(player, tracker, screen, window, logger, parentPanel);
                    } else {
                        showSkaterStatsForm(player, tracker, screen, window, logger, parentPanel);
                    }
                }));
            }
        }

        menuPanel.addComponent(new EmptySpace());
        menuPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(menuPanel);
    }

    private void showSkaterStatsForm(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Stats for " + player.getFirstName() + " " + player.getLastName() + " ==="));
        formPanel.addComponent(new Label("Season: " + tracker.getCurrentSeason()));
        formPanel.addComponent(new EmptySpace());

        TextBox gamesBox = new TextBox("82").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox goalsBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox assistsBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox plusMinusBox = new TextBox("0").setValidationPattern(Pattern.compile("-?\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox pimBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,4}")).setPreferredSize(new TerminalSize(6, 1));

        formPanel.addComponent(new Label("Games Played:"));
        formPanel.addComponent(gamesBox);
        formPanel.addComponent(new Label("Goals:"));
        formPanel.addComponent(goalsBox);
        formPanel.addComponent(new Label("Assists:"));
        formPanel.addComponent(assistsBox);
        formPanel.addComponent(new Label("+/-:"));
        formPanel.addComponent(plusMinusBox);
        formPanel.addComponent(new Label("PIM:"));
        formPanel.addComponent(pimBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Stats", () -> {
            PlayerSeasonStats stats = new PlayerSeasonStats();
            stats.setYear(tracker.getCurrentSeason());
            stats.setGamesPlayed(Integer.parseInt(gamesBox.getText()));
            stats.setGoals(Integer.parseInt(goalsBox.getText()));
            stats.setAssists(Integer.parseInt(assistsBox.getText()));
            stats.setPoints(stats.getGoals() + stats.getAssists());
            stats.setPlusMinus(Integer.parseInt(plusMinusBox.getText()));
            stats.setPenaltyMinutes(Integer.parseInt(pimBox.getText()));

            if (player.getCareerStats() == null) {
                player.setCareerStats(new ArrayList<>());
            }
            // Remove existing stats for this season if any
            player.getCareerStats().removeIf(s -> s.getYear() == tracker.getCurrentSeason());
            player.getCareerStats().add(stats);

            showPlayerStatsMenu(tracker, screen, window, logger, parentPanel);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            showPlayerStatsMenu(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(formPanel);
    }

    private void showGoalieStatsForm(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Stats for " + player.getFirstName() + " " + player.getLastName() + " ==="));
        formPanel.addComponent(new Label("Season: " + tracker.getCurrentSeason()));
        formPanel.addComponent(new EmptySpace());

        TextBox gamesBox = new TextBox("40").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox winsBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox lossesBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox otlBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox shutoutsBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,2}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox savePctBox = new TextBox("920").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox gaaBox = new TextBox("250").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));

        formPanel.addComponent(new Label("Games Played:"));
        formPanel.addComponent(gamesBox);
        formPanel.addComponent(new Label("Wins:"));
        formPanel.addComponent(winsBox);
        formPanel.addComponent(new Label("Losses:"));
        formPanel.addComponent(lossesBox);
        formPanel.addComponent(new Label("OT Losses:"));
        formPanel.addComponent(otlBox);
        formPanel.addComponent(new Label("Shutouts:"));
        formPanel.addComponent(shutoutsBox);
        formPanel.addComponent(new Label("Save % (e.g., 920 for .920):"));
        formPanel.addComponent(savePctBox);
        formPanel.addComponent(new Label("GAA (e.g., 250 for 2.50):"));
        formPanel.addComponent(gaaBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Stats", () -> {
            PlayerSeasonStats stats = new PlayerSeasonStats();
            stats.setYear(tracker.getCurrentSeason());
            stats.setGamesPlayed(Integer.parseInt(gamesBox.getText()));
            stats.setWins(Integer.parseInt(winsBox.getText()));
            stats.setLosses(Integer.parseInt(lossesBox.getText()));
            stats.setOvertimeLosses(Integer.parseInt(otlBox.getText()));
            stats.setShutouts(Integer.parseInt(shutoutsBox.getText()));
            stats.setSave_percentage(Integer.parseInt(savePctBox.getText()));
            stats.setGoalsAgainstAverage(Integer.parseInt(gaaBox.getText()));

            if (player.getCareerStats() == null) {
                player.setCareerStats(new ArrayList<>());
            }
            // Remove existing stats for this season if any
            player.getCareerStats().removeIf(s -> s.getYear() == tracker.getCurrentSeason());
            player.getCareerStats().add(stats);

            showPlayerStatsMenu(tracker, screen, window, logger, parentPanel);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            showPlayerStatsMenu(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(formPanel);
    }

    private void showTeamStatsForm(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Team Stats - " + tracker.getSelectedNHLTeam().getName() + " ==="));
        formPanel.addComponent(new Label("Season: " + tracker.getCurrentSeason()));
        formPanel.addComponent(new EmptySpace());

        TextBox gamesBox = new TextBox("82").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox winsBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox lossesBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox otlBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,3}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox goalsForBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,4}")).setPreferredSize(new TerminalSize(6, 1));
        TextBox goalsAgainstBox = new TextBox("0").setValidationPattern(Pattern.compile("\\d{0,4}")).setPreferredSize(new TerminalSize(6, 1));

        formPanel.addComponent(new Label("Games Played:"));
        formPanel.addComponent(gamesBox);
        formPanel.addComponent(new Label("Wins:"));
        formPanel.addComponent(winsBox);
        formPanel.addComponent(new Label("Losses:"));
        formPanel.addComponent(lossesBox);
        formPanel.addComponent(new Label("OT Losses:"));
        formPanel.addComponent(otlBox);
        formPanel.addComponent(new Label("Goals For:"));
        formPanel.addComponent(goalsForBox);
        formPanel.addComponent(new Label("Goals Against:"));
        formPanel.addComponent(goalsAgainstBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Stats", () -> {
            TeamSeasonStats stats = new TeamSeasonStats();
            stats.setYear(tracker.getCurrentSeason());
            stats.setGamesPlayed(Integer.parseInt(gamesBox.getText()));
            stats.setWins(Integer.parseInt(winsBox.getText()));
            stats.setLosses(Integer.parseInt(lossesBox.getText()));
            stats.setOvertimeLosses(Integer.parseInt(otlBox.getText()));
            stats.setGoalsFor(Integer.parseInt(goalsForBox.getText()));
            stats.setGoalsAgainst(Integer.parseInt(goalsAgainstBox.getText()));
            stats.setPoints((stats.getWins() * 2) + stats.getOvertimeLosses());

            if (tracker.getSelectedNHLTeam().getCareerStats() == null) {
                tracker.getSelectedNHLTeam().setCareerStats(new ArrayList<>());
            }
            // Remove existing stats for this season if any
            tracker.getSelectedNHLTeam().getCareerStats().removeIf(s -> s.getYear() == tracker.getCurrentSeason());
            tracker.getSelectedNHLTeam().getCareerStats().add(stats);

            showMainMenu(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showCareerStats(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel statsPanel = new Panel();
        statsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        statsPanel.addComponent(new Label("=== Career Stats ==="));
        statsPanel.addComponent(new EmptySpace());

        // Team career stats
        statsPanel.addComponent(new Label("--- " + tracker.getSelectedNHLTeam().getName() + " Season History ---"));
        if (tracker.getSelectedNHLTeam().getCareerStats() != null && !tracker.getSelectedNHLTeam().getCareerStats().isEmpty()) {
            for (TeamSeasonStats ts : tracker.getSelectedNHLTeam().getCareerStats()) {
                statsPanel.addComponent(new Label(String.format("%d: %d-%d-%d (%d pts) GF:%d GA:%d",
                    ts.getYear(), ts.getWins(), ts.getLosses(), ts.getOvertimeLosses(),
                    ts.getPoints(), ts.getGoalsFor(), ts.getGoalsAgainst())));
            }
        } else {
            statsPanel.addComponent(new Label("  (No team stats recorded)"));
        }

        statsPanel.addComponent(new EmptySpace());
        statsPanel.addComponent(new Label("--- Player Career Stats ---"));

        if (tracker.getSelectedNHLTeam().getRoster() != null) {
            for (Player player : tracker.getSelectedNHLTeam().getRoster()) {
                if (player.getCareerStats() != null && !player.getCareerStats().isEmpty()) {
                    statsPanel.addComponent(new Label(player.getFirstName() + " " + player.getLastName() + ":"));
                    for (PlayerSeasonStats ps : player.getCareerStats()) {
                        if (player.getPosition() == PLAYER_POS.GOALIE) {
                            statsPanel.addComponent(new Label(String.format("  %d: %dGP %dW-%dL-%dOTL %.3fSV%% %.2fGAA",
                                ps.getYear(), ps.getGamesPlayed(), ps.getWins(), ps.getLosses(), ps.getOvertimeLosses(),
                                ps.getSave_percentage() / 1000.0, ps.getGoalsAgainstAverage() / 100.0)));
                        } else {
                            statsPanel.addComponent(new Label(String.format("  %d: %dGP %dG %dA %dP %+d %dPIM",
                                ps.getYear(), ps.getGamesPlayed(), ps.getGoals(), ps.getAssists(),
                                ps.getPoints(), ps.getPlusMinus(), ps.getPenaltyMinutes())));
                        }
                    }
                }
            }
        }

        statsPanel.addComponent(new EmptySpace());
        statsPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(statsPanel);
    }

    private void showAdvanceSeasonConfirm(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel confirmPanel = new Panel();
        confirmPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        confirmPanel.addComponent(new Label("=== Advance Season ==="));
        confirmPanel.addComponent(new EmptySpace());
        confirmPanel.addComponent(new Label("Current Season: " + tracker.getCurrentSeason()));
        confirmPanel.addComponent(new Label("Next Season: " + (tracker.getCurrentSeason() + 1)));
        confirmPanel.addComponent(new EmptySpace());
        confirmPanel.addComponent(new Label("Are you sure you want to advance to the next season?"));
        confirmPanel.addComponent(new Label("Make sure you've entered all stats first!"));
        confirmPanel.addComponent(new EmptySpace());

        confirmPanel.addComponent(new Button("Yes, Advance Season", () -> {
            tracker.advanceSeason();
            showMainMenu(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        confirmPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(confirmPanel);
    }
}

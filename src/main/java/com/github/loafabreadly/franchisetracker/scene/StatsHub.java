package com.github.loafabreadly.franchisetracker.scene;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.ChartComponents;
import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * StatsHub provides visualizations for player and team statistics.
 */
public class StatsHub extends Panel {

    /**
     * Creates the stats hub panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public StatsHub(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showMainMenu(tracker, screen, window, logger, parentPanel);
    }

    private void showMainMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Statistics Hub ==="));
        addComponent(new EmptySpace());

        addComponent(new Button("Player Overall Progression", () -> {
            showPlayerProgressionSelect(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Team Points History", () -> {
            showTeamPointsChart(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Roster Age Distribution", () -> {
            showAgeDistribution(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Cap Trajectory", () -> {
            showCapTrajectory(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Player Stat Comparison", () -> {
            showPlayerStatComparison(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showPlayerProgressionSelect(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel selectPanel = new Panel();
        selectPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        selectPanel.addComponent(new Label("=== Select Player for Progression ==="));
        selectPanel.addComponent(new EmptySpace());

        List<Player> allPlayers = tracker.getAllPlayers();
        for (Player player : allPlayers) {
            if (player.getCareerStats() != null && player.getCareerStats().size() > 1) {
                selectPanel.addComponent(new Button(player.getFullName() + " (" + player.getCareerStats().size() + " seasons)", () -> {
                    showPlayerProgression(player, tracker, screen, window, logger, parentPanel);
                }));
            }
        }

        if (allPlayers.stream().noneMatch(p -> p.getCareerStats() != null && p.getCareerStats().size() > 1)) {
            selectPanel.addComponent(new Label("No players with multiple seasons of data."));
        }

        selectPanel.addComponent(new EmptySpace());
        selectPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(selectPanel);
    }

    private void showPlayerProgression(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel chartPanel = new Panel();
        chartPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        chartPanel.addComponent(new Label("=== " + player.getFullName() + " Progression ==="));
        chartPanel.addComponent(new EmptySpace());

        List<PlayerSeasonStats> stats = player.getCareerStats().stream()
            .sorted(Comparator.comparingInt(PlayerSeasonStats::getYear))
            .toList();

        // Extract overall progression if available
        List<Integer> overalls = new ArrayList<>();
        List<Integer> points = new ArrayList<>();
        
        for (PlayerSeasonStats s : stats) {
            if (s.getOverallAtEndOfSeason() > 0) {
                overalls.add(s.getOverallAtEndOfSeason());
            }
            points.add(s.getPoints());
        }

        // Points sparkline
        chartPanel.addComponent(new Label("Points by Season:"));
        String pointSparkline = ChartComponents.createSparkline(points);
        chartPanel.addComponent(new Label("  " + pointSparkline));
        chartPanel.addComponent(new EmptySpace());

        // Detailed stats table
        chartPanel.addComponent(new Label("Season | GP  |  G  |  A  | Pts | +/-"));
        chartPanel.addComponent(new Label("─".repeat(40)));
        for (PlayerSeasonStats s : stats) {
            chartPanel.addComponent(new Label(String.format(" %4d  | %3d | %3d | %3d | %3d | %+3d",
                s.getYear(), s.getGamesPlayed(), s.getGoals(), s.getAssists(), s.getPoints(), s.getPlusMinus())));
        }

        chartPanel.addComponent(new EmptySpace());
        chartPanel.addComponent(new Button("Back", () -> {
            showPlayerProgressionSelect(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(chartPanel);
    }

    private void showTeamPointsChart(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel chartPanel = new Panel();
        chartPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        chartPanel.addComponent(new Label("=== Team Points History ==="));
        chartPanel.addComponent(new EmptySpace());

        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        if (history == null || history.isEmpty()) {
            chartPanel.addComponent(new Label("No season history available."));
            chartPanel.addComponent(new Label("Complete seasons to see data here."));
        } else {
            List<Integer> points = history.stream()
                .sorted(Comparator.comparingInt(SeasonSnapshot::getSeason))
                .map(SeasonSnapshot::getPoints)
                .collect(Collectors.toList());

            // Sparkline
            chartPanel.addComponent(new Label("Points Trend: " + ChartComponents.createSparkline(points)));
            chartPanel.addComponent(new EmptySpace());

            // Detailed table
            chartPanel.addComponent(new Label("Season | Pts | W  | L  | OTL | Cup?"));
            chartPanel.addComponent(new Label("─".repeat(40)));
            for (SeasonSnapshot s : history.stream().sorted(Comparator.comparingInt(SeasonSnapshot::getSeason)).toList()) {
                TeamSeasonStats ts = s.getTeamStats();
                String cupIcon = s.wonStanleyCup() ? " 🏆" : "";
                if (ts != null) {
                    chartPanel.addComponent(new Label(String.format(" %4d  | %3d | %2d | %2d | %3d%s",
                        s.getSeason(), ts.getPoints(), ts.getWins(), ts.getLosses(), ts.getOvertimeLosses(), cupIcon)));
                } else {
                    chartPanel.addComponent(new Label(String.format(" %4d  | N/A | -- | -- | ---%s", s.getSeason(), cupIcon)));
                }
            }
        }

        chartPanel.addComponent(new EmptySpace());
        chartPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(chartPanel);
    }

    private void showAgeDistribution(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel chartPanel = new Panel();
        chartPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        chartPanel.addComponent(new Label("=== Roster Age Distribution ==="));
        chartPanel.addComponent(new EmptySpace());

        List<Player> nhlRoster = tracker.getSelectedNHLTeam().getRoster();
        if (nhlRoster == null || nhlRoster.isEmpty()) {
            chartPanel.addComponent(new Label("No players on NHL roster."));
        } else {
            int[] ages = nhlRoster.stream().mapToInt(Player::getAge).toArray();
            String histogram = ChartComponents.createAgeHistogram(ages, 3);
            
            chartPanel.addComponent(new Label("Age Range Distribution:"));
            for (String line : histogram.split("\n")) {
                chartPanel.addComponent(new Label(line));
            }

            chartPanel.addComponent(new EmptySpace());
            
            // Summary stats
            double avgAge = nhlRoster.stream().mapToInt(Player::getAge).average().orElse(0);
            int minAge = nhlRoster.stream().mapToInt(Player::getAge).min().orElse(0);
            int maxAge = nhlRoster.stream().mapToInt(Player::getAge).max().orElse(0);
            
            chartPanel.addComponent(new Label(String.format("Average Age: %.1f | Range: %d - %d", avgAge, minAge, maxAge)));
        }

        chartPanel.addComponent(new EmptySpace());
        chartPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(chartPanel);
    }

    private void showCapTrajectory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel chartPanel = new Panel();
        chartPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        chartPanel.addComponent(new Label("=== Cap Trajectory ==="));
        chartPanel.addComponent(new EmptySpace());

        // Current cap situation
        chartPanel.addComponent(new Label("Current Season: " + tracker.getCurrentSeason()));
        chartPanel.addComponent(new Label(String.format("Cap Ceiling: $%.1fM", tracker.getCapCeiling())));
        chartPanel.addComponent(new Label(String.format("Current Hit: $%.1fM", tracker.getTotalCapHit())));
        chartPanel.addComponent(new Label(String.format("Cap Space:   $%.1fM", tracker.getCapSpace())));
        chartPanel.addComponent(new EmptySpace());

        // Projected cap by year (based on expiring contracts)
        chartPanel.addComponent(new Label("--- Projected Cap Relief by Year ---"));
        double currentCap = tracker.getTotalCapHit();
        
        for (int year = tracker.getCurrentSeason(); year <= tracker.getCurrentSeason() + 3; year++) {
            List<Player> expiring = tracker.getExpiringContracts(year);
            double relief = expiring.stream().mapToDouble(Player::getCapHit).sum();
            currentCap -= relief;
            
            chartPanel.addComponent(new Label(String.format("%d: $%.1fM expiring (%d players) | Projected: $%.1fM",
                year, relief, expiring.size(), Math.max(0, currentCap))));
        }

        chartPanel.addComponent(new EmptySpace());
        chartPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(chartPanel);
    }

    private void showPlayerStatComparison(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel selectPanel = new Panel();
        selectPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        selectPanel.addComponent(new Label("=== Player Stat Comparison ==="));
        selectPanel.addComponent(new Label("Select players to compare their career stats"));
        selectPanel.addComponent(new EmptySpace());

        // Show top scorers from current season
        List<Player> playersWithStats = tracker.getAllPlayers().stream()
            .filter(p -> p.getCareerStats() != null && !p.getCareerStats().isEmpty())
            .sorted((a, b) -> {
                int aPoints = a.getCareerStats().stream().mapToInt(PlayerSeasonStats::getPoints).sum();
                int bPoints = b.getCareerStats().stream().mapToInt(PlayerSeasonStats::getPoints).sum();
                return Integer.compare(bPoints, aPoints);
            })
            .limit(15)
            .toList();

        if (playersWithStats.isEmpty()) {
            selectPanel.addComponent(new Label("No players have recorded stats yet."));
        } else {
            selectPanel.addComponent(new Label("Career Leaders:"));
            selectPanel.addComponent(new Label(String.format("%-20s | GP  |  G  |  A  | Pts", "Player")));
            selectPanel.addComponent(new Label("─".repeat(50)));
            
            for (Player p : playersWithStats) {
                int totalGP = p.getCareerStats().stream().mapToInt(PlayerSeasonStats::getGamesPlayed).sum();
                int totalG = p.getCareerStats().stream().mapToInt(PlayerSeasonStats::getGoals).sum();
                int totalA = p.getCareerStats().stream().mapToInt(PlayerSeasonStats::getAssists).sum();
                int totalP = p.getCareerStats().stream().mapToInt(PlayerSeasonStats::getPoints).sum();
                
                selectPanel.addComponent(new Label(String.format("%-20s | %3d | %3d | %3d | %3d",
                    p.getFullName().substring(0, Math.min(20, p.getFullName().length())),
                    totalGP, totalG, totalA, totalP)));
            }
        }

        selectPanel.addComponent(new EmptySpace());
        selectPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(selectPanel);
    }
}

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
 * CapDashboard provides a comprehensive view of the team's salary cap situation.
 */
public class CapDashboard extends Panel {

    /**
     * Creates the cap dashboard panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public CapDashboard(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showDashboard(tracker, screen, window, logger, parentPanel);
    }

    private void showDashboard(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Cap Dashboard - Season " + tracker.getCurrentSeason() + " ==="));
        addComponent(new EmptySpace());

        // Cap Summary
        double capCeiling = tracker.getCapCeiling();
        double capFloor = tracker.getCapFloor();
        double totalHit = tracker.getTotalCapHit();
        double capSpace = tracker.getCapSpace();

        addComponent(new Label("╔══════════════════════════════════════════╗"));
        addComponent(new Label(String.format("║  Cap Ceiling:     $%.1fM                ║", capCeiling)));
        addComponent(new Label(String.format("║  Cap Floor:       $%.1fM                ║", capFloor)));
        addComponent(new Label(String.format("║  Total Cap Hit:   $%.1fM                ║", totalHit)));
        addComponent(new Label(String.format("║  Cap Space:       $%.1fM                ║", capSpace)));
        addComponent(new Label("╚══════════════════════════════════════════╝"));
        addComponent(new EmptySpace());

        // Cap usage bar
        int barWidth = 40;
        int usedBlocks = (int) Math.min(barWidth, (totalHit / capCeiling) * barWidth);
        String bar = "█".repeat(usedBlocks) + "░".repeat(barWidth - usedBlocks);
        addComponent(new Label("Cap Usage: [" + bar + "] " + String.format("%.1f%%", (totalHit / capCeiling) * 100)));
        addComponent(new EmptySpace());

        // Top 10 Cap Hits
        addComponent(new Label("--- Top 10 Cap Hits ---"));
        List<Player> topPlayers = tracker.getSelectedNHLTeam().getRoster().stream()
            .filter(p -> p.getContract() != null)
            .sorted(Comparator.comparingDouble(Player::getCapHit).reversed())
            .limit(10)
            .toList();

        if (topPlayers.isEmpty()) {
            addComponent(new Label("  No signed players"));
        } else {
            for (int i = 0; i < topPlayers.size(); i++) {
                Player p = topPlayers.get(i);
                addComponent(new Label(String.format("%2d. %-20s $%.2fM x %dy", 
                    i + 1, 
                    p.getFullName().substring(0, Math.min(20, p.getFullName().length())),
                    p.getCapHit(),
                    p.getContractYearsRemaining())));
            }
        }

        addComponent(new EmptySpace());
        addComponent(new Button("View Expiring Contracts", () -> {
            showExpiringContracts(tracker, screen, window, logger, parentPanel);
        }));
        addComponent(new Button("View by Position", () -> {
            showCapByPosition(tracker, screen, window, logger, parentPanel);
        }));
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showExpiringContracts(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel expiringPanel = new Panel();
        expiringPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        expiringPanel.addComponent(new Label("=== Expiring Contracts ==="));
        expiringPanel.addComponent(new EmptySpace());

        // Show next 3 years of expirations
        for (int year = tracker.getCurrentSeason(); year <= tracker.getCurrentSeason() + 3; year++) {
            final int checkYear = year;
            List<Player> expiring = tracker.getAllPlayers().stream()
                .filter(p -> p.getContract() != null && p.getContract().getExpirationYear() == checkYear)
                .sorted(Comparator.comparingDouble(Player::getCapHit).reversed())
                .toList();

            expiringPanel.addComponent(new Label("--- " + year + " (" + expiring.size() + " players, $" + 
                String.format("%.1f", expiring.stream().mapToDouble(Player::getCapHit).sum()) + "M) ---"));

            if (expiring.isEmpty()) {
                expiringPanel.addComponent(new Label("  None"));
            } else {
                for (Player p : expiring) {
                    String status = p.getContract().getStatus() != null ? p.getContract().getStatus().name() : "?";
                    expiringPanel.addComponent(new Label(String.format("  %-18s $%.2fM (%s)", 
                        p.getFullName().substring(0, Math.min(18, p.getFullName().length())),
                        p.getCapHit(),
                        status)));
                }
            }
        }

        expiringPanel.addComponent(new EmptySpace());
        expiringPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(expiringPanel);
    }

    private void showCapByPosition(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel posPanel = new Panel();
        posPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        posPanel.addComponent(new Label("=== Cap Hit by Position ==="));
        posPanel.addComponent(new EmptySpace());

        Map<PLAYER_POS, Double> capByPos = tracker.getSelectedNHLTeam().getRoster().stream()
            .filter(p -> p.getContract() != null && p.getPosition() != null)
            .collect(Collectors.groupingBy(
                Player::getPosition,
                Collectors.summingDouble(Player::getCapHit)
            ));

        Map<PLAYER_POS, Long> countByPos = tracker.getSelectedNHLTeam().getRoster().stream()
            .filter(p -> p.getContract() != null && p.getPosition() != null)
            .collect(Collectors.groupingBy(
                Player::getPosition,
                Collectors.counting()
            ));

        for (PLAYER_POS pos : PLAYER_POS.values()) {
            double cap = capByPos.getOrDefault(pos, 0.0);
            long count = countByPos.getOrDefault(pos, 0L);
            if (count > 0) {
                posPanel.addComponent(new Label(String.format("%-15s: %d players, $%.2fM total, $%.2fM avg",
                    pos.name(), count, cap, cap / count)));
            }
        }

        posPanel.addComponent(new EmptySpace());
        posPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(posPanel);
    }
}

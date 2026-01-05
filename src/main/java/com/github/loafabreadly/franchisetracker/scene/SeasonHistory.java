package com.github.loafabreadly.franchisetracker.scene;

import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * SeasonHistory provides a browser for viewing archived season snapshots.
 */
public class SeasonHistory extends Panel {

    /**
     * Creates the season history browser panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public SeasonHistory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showSeasonList(tracker, screen, window, logger, parentPanel);
    }

    private void showSeasonList(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Season History ==="));
        addComponent(new EmptySpace());

        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        if (history == null || history.isEmpty()) {
            addComponent(new Label("No archived seasons yet."));
            addComponent(new Label("Complete seasons to see history here."));
        } else {
            addComponent(new Label("Select a season to view:"));
            addComponent(new EmptySpace());

            history.stream()
                .sorted(Comparator.comparingInt(SeasonSnapshot::getSeason).reversed())
                .forEach(snapshot -> {
                    String cupIcon = snapshot.wonStanleyCup() ? " 🏆" : "";
                    String summary = String.format("Season %d - %d pts, %d players%s",
                        snapshot.getSeason(),
                        snapshot.getPoints(),
                        snapshot.getTotalPlayers(),
                        cupIcon);
                    addComponent(new Button(summary, () -> {
                        showSeasonDetails(snapshot, tracker, screen, window, logger, parentPanel);
                    }));
                });
        }

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showSeasonDetails(SeasonSnapshot snapshot, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel detailPanel = new Panel();
        detailPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        detailPanel.addComponent(new Label("=== Season " + snapshot.getSeason() + " Details ==="));
        detailPanel.addComponent(new EmptySpace());

        // Navigation
        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        int currentIndex = -1;
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).getSeason() == snapshot.getSeason()) {
                currentIndex = i;
                break;
            }
        }
        final int prevIndex = currentIndex > 0 ? currentIndex - 1 : -1;
        final int nextIndex = currentIndex < history.size() - 1 ? currentIndex + 1 : -1;

        Panel navPanel = new Panel();
        navPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        if (prevIndex >= 0) {
            navPanel.addComponent(new Button("◀ " + history.get(prevIndex).getSeason(), () -> {
                showSeasonDetails(history.get(prevIndex), tracker, screen, window, logger, parentPanel);
            }));
        }
        navPanel.addComponent(new Label("  Season " + snapshot.getSeason() + "  "));
        if (nextIndex >= 0) {
            navPanel.addComponent(new Button(history.get(nextIndex).getSeason() + " ▶", () -> {
                showSeasonDetails(history.get(nextIndex), tracker, screen, window, logger, parentPanel);
            }));
        }
        detailPanel.addComponent(navPanel);
        detailPanel.addComponent(new EmptySpace());

        // Summary
        detailPanel.addComponent(new Label("╔════════════════════════════════════╗"));
        detailPanel.addComponent(new Label(String.format("║  Cap Ceiling: $%.1fM               ║", snapshot.getCapCeiling())));
        detailPanel.addComponent(new Label(String.format("║  Cap Used:    $%.1fM               ║", snapshot.getTotalCapHit())));
        detailPanel.addComponent(new Label(String.format("║  NHL Roster:  %d players            ║", snapshot.getNhlRoster().size())));
        detailPanel.addComponent(new Label(String.format("║  AHL Roster:  %d players            ║", snapshot.getAhlRoster().size())));
        detailPanel.addComponent(new Label("╚════════════════════════════════════╝"));
        detailPanel.addComponent(new EmptySpace());

        // Team stats
        if (snapshot.getTeamStats() != null) {
            TeamSeasonStats ts = snapshot.getTeamStats();
            detailPanel.addComponent(new Label("--- Team Record ---"));
            detailPanel.addComponent(new Label(String.format("Record: %d-%d-%d (%d pts)",
                ts.getWins(), ts.getLosses(), ts.getOvertimeLosses(), ts.getPoints())));
            detailPanel.addComponent(new Label(String.format("Goals: %d GF, %d GA (Diff: %+d)",
                ts.getGoalsFor(), ts.getGoalsAgainst(), ts.getGoalsFor() - ts.getGoalsAgainst())));
        }

        detailPanel.addComponent(new EmptySpace());
        detailPanel.addComponent(new Button("View NHL Roster", () -> {
            showRosterSnapshot(snapshot.getNhlRoster(), "NHL", snapshot.getSeason(), tracker, screen, window, logger, parentPanel, snapshot);
        }));
        detailPanel.addComponent(new Button("View AHL Roster", () -> {
            showRosterSnapshot(snapshot.getAhlRoster(), "AHL", snapshot.getSeason(), tracker, screen, window, logger, parentPanel, snapshot);
        }));
        detailPanel.addComponent(new Button("View Awards", () -> {
            showAwardsSnapshot(snapshot, tracker, screen, window, logger, parentPanel);
        }));
        detailPanel.addComponent(new Button("View Trades", () -> {
            showTradesSnapshot(snapshot, tracker, screen, window, logger, parentPanel);
        }));
        detailPanel.addComponent(new Button("View Draft Picks", () -> {
            showDraftSnapshot(snapshot, tracker, screen, window, logger, parentPanel);
        }));

        detailPanel.addComponent(new EmptySpace());
        detailPanel.addComponent(new Button("Back to Season List", () -> {
            showSeasonList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        window.setComponent(detailPanel);
    }

    private void showRosterSnapshot(List<PlayerSnapshot> roster, String league, int season, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel, SeasonSnapshot snapshot) {
        Panel rosterPanel = new Panel();
        rosterPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        rosterPanel.addComponent(new Label("=== " + league + " Roster - Season " + season + " ==="));
        rosterPanel.addComponent(new EmptySpace());

        if (roster.isEmpty()) {
            rosterPanel.addComponent(new Label("No players on roster."));
        } else {
            rosterPanel.addComponent(new Label(String.format("%-20s %-5s %-3s %-4s %-8s", "Name", "Pos", "OVR", "Age", "Cap Hit")));
            rosterPanel.addComponent(new Label("─".repeat(50)));

            roster.stream()
                .sorted(Comparator.comparingInt(PlayerSnapshot::getOverall).reversed())
                .forEach(p -> {
                    String capStr = p.getAav() > 0 ? String.format("$%.2fM", p.getAav()) : "N/A";
                    rosterPanel.addComponent(new Label(String.format("%-20s %-5s %-3d %-4d %-8s",
                        p.getFullName().substring(0, Math.min(20, p.getFullName().length())),
                        p.getPosition() != null ? p.getPosition().name().substring(0, Math.min(5, p.getPosition().name().length())) : "N/A",
                        p.getOverall(),
                        p.getAge(),
                        capStr)));
                });
        }

        rosterPanel.addComponent(new EmptySpace());
        rosterPanel.addComponent(new Button("Back", () -> {
            showSeasonDetails(snapshot, tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(rosterPanel);
    }

    private void showAwardsSnapshot(SeasonSnapshot snapshot, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel awardsPanel = new Panel();
        awardsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        awardsPanel.addComponent(new Label("=== Awards - Season " + snapshot.getSeason() + " ==="));
        awardsPanel.addComponent(new EmptySpace());

        List<Award> awards = snapshot.getAwards();
        if (awards == null || awards.isEmpty()) {
            awardsPanel.addComponent(new Label("No awards this season."));
        } else {
            for (Award award : awards) {
                String recipient = award.getRecipientType() == Award.AwardCategory.PLAYER
                    ? (award.getPlayer() != null ? award.getPlayer().getFullName() : "Unknown")
                    : (award.getTeam() != null ? award.getTeam().getName() : "Unknown");
                awardsPanel.addComponent(new Label("• " + award.getAward().name() + " - " + recipient));
            }
        }

        awardsPanel.addComponent(new EmptySpace());
        awardsPanel.addComponent(new Button("Back", () -> {
            showSeasonDetails(snapshot, tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(awardsPanel);
    }

    private void showTradesSnapshot(SeasonSnapshot snapshot, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel tradesPanel = new Panel();
        tradesPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        tradesPanel.addComponent(new Label("=== Trades - Season " + snapshot.getSeason() + " ==="));
        tradesPanel.addComponent(new EmptySpace());

        List<Trade> trades = snapshot.getTrades();
        if (trades == null || trades.isEmpty()) {
            tradesPanel.addComponent(new Label("No trades this season."));
        } else {
            for (Trade trade : trades) {
                tradesPanel.addComponent(new Label("Trade with: " + trade.getTeam2Name()));
                tradesPanel.addComponent(new Label("  Received: " + trade.getReceivedSummary()));
                tradesPanel.addComponent(new Label("  Sent: " + trade.getSentSummary()));
                tradesPanel.addComponent(new EmptySpace());
            }
        }

        tradesPanel.addComponent(new Button("Back", () -> {
            showSeasonDetails(snapshot, tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(tradesPanel);
    }

    private void showDraftSnapshot(SeasonSnapshot snapshot, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel draftPanel = new Panel();
        draftPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        draftPanel.addComponent(new Label("=== Draft Picks - Season " + snapshot.getSeason() + " ==="));
        draftPanel.addComponent(new EmptySpace());

        List<DraftedPlayer> picks = snapshot.getDraftPicks();
        if (picks == null || picks.isEmpty()) {
            draftPanel.addComponent(new Label("No draft picks this season."));
        } else {
            for (DraftedPlayer dp : picks) {
                draftPanel.addComponent(new Label(dp.getSummary()));
            }
        }

        draftPanel.addComponent(new EmptySpace());
        draftPanel.addComponent(new Button("Back", () -> {
            showSeasonDetails(snapshot, tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(draftPanel);
    }
}

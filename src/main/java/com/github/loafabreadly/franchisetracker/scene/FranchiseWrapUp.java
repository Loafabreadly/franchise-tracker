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
 * FranchiseWrapUp provides a comprehensive summary of the franchise when completed.
 */
public class FranchiseWrapUp extends Panel {

    /**
     * Creates the franchise wrap-up panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public FranchiseWrapUp(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showWrapUpConfirmation(tracker, screen, window, logger, parentPanel);
    }

    private void showWrapUpConfirmation(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("═══════════════════════════════════════════"));
        addComponent(new Label("       COMPLETE FRANCHISE MODE?"));
        addComponent(new Label("═══════════════════════════════════════════"));
        addComponent(new EmptySpace());
        addComponent(new Label("This will archive the current season and"));
        addComponent(new Label("generate a final franchise summary."));
        addComponent(new EmptySpace());
        addComponent(new Label("Team: " + tracker.getSelectedNHLTeam().getName()));
        addComponent(new Label("GM: " + tracker.getGeneralManagerName()));
        addComponent(new Label("Seasons: " + (tracker.getSeasonHistory() != null ? tracker.getSeasonHistory().size() + 1 : 1)));
        addComponent(new EmptySpace());

        addComponent(new Button("Complete Franchise & View Summary", () -> {
            tracker.completeFranchise();
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Cancel", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showFranchiseSummary(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("╔═══════════════════════════════════════════════════╗"));
        addComponent(new Label("║         FRANCHISE SUMMARY                         ║"));
        addComponent(new Label("║  " + tracker.getSelectedNHLTeam().getName() + "                                      ║"));
        addComponent(new Label("╚═══════════════════════════════════════════════════╝"));
        addComponent(new EmptySpace());

        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        int totalSeasons = history != null ? history.size() : 0;
        int cups = tracker.getStanleyCupCount();

        // Overview
        addComponent(new Label("══════ OVERVIEW ══════"));
        addComponent(new Label("General Manager: " + tracker.getGeneralManagerName()));
        addComponent(new Label("Total Seasons: " + totalSeasons));
        addComponent(new Label("Stanley Cups: " + cups + " " + "🏆".repeat(Math.min(cups, 5))));
        addComponent(new EmptySpace());

        // Calculate franchise grade
        String grade = calculateFranchiseGrade(tracker);
        addComponent(new Label("FRANCHISE GRADE: " + grade));
        addComponent(new EmptySpace());

        addComponent(new Button("View Championships", () -> {
            showChampionships(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View All-Time Leaders", () -> {
            showAllTimeLeaders(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Award History", () -> {
            showFullAwardHistory(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Best Seasons", () -> {
            showBestSeasons(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Notable Trades", () -> {
            showNotableTrades(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Draft Successes", () -> {
            showDraftSuccesses(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Return to Game", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private String calculateFranchiseGrade(FranchiseTracker tracker) {
        int cups = tracker.getStanleyCupCount();
        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        int seasons = history != null ? history.size() : 0;
        
        if (seasons == 0) return "N/A";
        
        double cupRate = (double) cups / seasons;
        int totalWins = history != null ? history.stream()
            .filter(s -> s.getTeamStats() != null)
            .mapToInt(s -> s.getTeamStats().getWins())
            .sum() : 0;
        double avgWins = seasons > 0 ? (double) totalWins / seasons : 0;
        
        // Awards count
        List<Award> awards = tracker.getLeagueAwards();
        int awardCount = awards != null ? awards.size() : 0;
        
        // Calculate score
        double score = 0;
        score += cups * 20;  // 20 points per cup
        score += avgWins * 0.5;  // Points for avg wins
        score += awardCount * 2;  // Points for awards
        
        if (score >= 100) return "S+ (Dynasty)";
        if (score >= 80) return "S (Legendary)";
        if (score >= 60) return "A (Excellent)";
        if (score >= 45) return "B (Good)";
        if (score >= 30) return "C (Average)";
        if (score >= 15) return "D (Below Average)";
        return "F (Poor)";
    }

    private void showChampionships(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel cupPanel = new Panel();
        cupPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        cupPanel.addComponent(new Label("=== Stanley Cup Championships ==="));
        cupPanel.addComponent(new EmptySpace());

        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        if (history == null) {
            cupPanel.addComponent(new Label("No seasons archived."));
        } else {
            List<SeasonSnapshot> cupYears = history.stream()
                .filter(SeasonSnapshot::wonStanleyCup)
                .sorted(Comparator.comparingInt(SeasonSnapshot::getSeason))
                .toList();

            if (cupYears.isEmpty()) {
                cupPanel.addComponent(new Label("No Stanley Cups won."));
            } else {
                for (SeasonSnapshot s : cupYears) {
                    TeamSeasonStats ts = s.getTeamStats();
                    String record = ts != null ? String.format("%d-%d-%d", ts.getWins(), ts.getLosses(), ts.getOvertimeLosses()) : "N/A";
                    cupPanel.addComponent(new Label("🏆 " + s.getSeason() + " - Record: " + record));
                }
                cupPanel.addComponent(new EmptySpace());
                cupPanel.addComponent(new Label("Total: " + cupYears.size() + " Stanley Cup(s)"));
            }
        }

        cupPanel.addComponent(new EmptySpace());
        cupPanel.addComponent(new Button("Back", () -> {
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(cupPanel);
    }

    private void showAllTimeLeaders(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel leadersPanel = new Panel();
        leadersPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        leadersPanel.addComponent(new Label("=== All-Time Franchise Leaders ==="));
        leadersPanel.addComponent(new EmptySpace());

        // Collect all players who have ever been on the roster
        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        if (history == null || history.isEmpty()) {
            leadersPanel.addComponent(new Label("No historical data available."));
        } else {
            // Build career totals from snapshots
            Map<String, int[]> careerStats = new java.util.HashMap<>(); // name -> [gp, g, a, pts]
            
            for (SeasonSnapshot s : history) {
                for (PlayerSnapshot ps : s.getNhlRoster()) {
                    if (ps.getSeasonStats() != null) {
                        PlayerSeasonStats stats = ps.getSeasonStats();
                        careerStats.computeIfAbsent(ps.getFullName(), k -> new int[4]);
                        int[] totals = careerStats.get(ps.getFullName());
                        totals[0] += stats.getGamesPlayed();
                        totals[1] += stats.getGoals();
                        totals[2] += stats.getAssists();
                        totals[3] += stats.getPoints();
                    }
                }
            }

            if (careerStats.isEmpty()) {
                leadersPanel.addComponent(new Label("No player statistics recorded."));
            } else {
                // Sort by points
                List<Map.Entry<String, int[]>> sorted = careerStats.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue()[3], a.getValue()[3]))
                    .limit(10)
                    .toList();

                leadersPanel.addComponent(new Label("--- Points Leaders ---"));
                leadersPanel.addComponent(new Label(String.format("%-20s | GP  |  G  |  A  | Pts", "Player")));
                leadersPanel.addComponent(new Label("─".repeat(50)));
                
                int rank = 1;
                for (Map.Entry<String, int[]> e : sorted) {
                    int[] t = e.getValue();
                    leadersPanel.addComponent(new Label(String.format("%2d. %-17s | %3d | %3d | %3d | %3d",
                        rank++, e.getKey().substring(0, Math.min(17, e.getKey().length())),
                        t[0], t[1], t[2], t[3])));
                }

                // Goals leaders
                leadersPanel.addComponent(new EmptySpace());
                leadersPanel.addComponent(new Label("--- Goals Leaders ---"));
                List<Map.Entry<String, int[]>> goalsSorted = careerStats.entrySet().stream()
                    .sorted((a, b) -> Integer.compare(b.getValue()[1], a.getValue()[1]))
                    .limit(5)
                    .toList();
                for (Map.Entry<String, int[]> e : goalsSorted) {
                    leadersPanel.addComponent(new Label(String.format("  %-20s: %d goals",
                        e.getKey().substring(0, Math.min(20, e.getKey().length())), e.getValue()[1])));
                }
            }
        }

        leadersPanel.addComponent(new EmptySpace());
        leadersPanel.addComponent(new Button("Back", () -> {
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(leadersPanel);
    }

    private void showFullAwardHistory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel awardsPanel = new Panel();
        awardsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        awardsPanel.addComponent(new Label("=== Complete Award History ==="));
        awardsPanel.addComponent(new EmptySpace());

        List<Award> awards = tracker.getLeagueAwards();
        if (awards == null || awards.isEmpty()) {
            awardsPanel.addComponent(new Label("No awards won."));
        } else {
            // Group by award type
            Map<Award.AwardType, List<Award>> byType = awards.stream()
                .collect(Collectors.groupingBy(Award::getAward));

            for (Award.AwardType type : Award.AwardType.values()) {
                List<Award> typeAwards = byType.get(type);
                if (typeAwards != null && !typeAwards.isEmpty()) {
                    awardsPanel.addComponent(new Label("--- " + type.name() + " (" + typeAwards.size() + ") ---"));
                    for (Award a : typeAwards.stream().sorted(Comparator.comparingInt(Award::getYear)).toList()) {
                        String recipient = a.getRecipientType() == Award.AwardCategory.PLAYER
                            ? (a.getPlayer() != null ? a.getPlayer().getFullName() : "Unknown")
                            : "Team";
                        awardsPanel.addComponent(new Label("  " + a.getYear() + ": " + recipient));
                    }
                }
            }
        }

        awardsPanel.addComponent(new EmptySpace());
        awardsPanel.addComponent(new Button("Back", () -> {
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(awardsPanel);
    }

    private void showBestSeasons(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel bestPanel = new Panel();
        bestPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        bestPanel.addComponent(new Label("=== Best Seasons ==="));
        bestPanel.addComponent(new EmptySpace());

        List<SeasonSnapshot> history = tracker.getSeasonHistory();
        if (history == null || history.isEmpty()) {
            bestPanel.addComponent(new Label("No seasons archived."));
        } else {
            // Sort by points
            List<SeasonSnapshot> byPoints = history.stream()
                .filter(s -> s.getTeamStats() != null)
                .sorted((a, b) -> Integer.compare(b.getTeamStats().getPoints(), a.getTeamStats().getPoints()))
                .limit(5)
                .toList();

            bestPanel.addComponent(new Label("--- By Points ---"));
            for (SeasonSnapshot s : byPoints) {
                TeamSeasonStats ts = s.getTeamStats();
                String cup = s.wonStanleyCup() ? " 🏆" : "";
                bestPanel.addComponent(new Label(String.format("%d: %d pts (%d-%d-%d)%s",
                    s.getSeason(), ts.getPoints(), ts.getWins(), ts.getLosses(), ts.getOvertimeLosses(), cup)));
            }

            // Sort by wins
            bestPanel.addComponent(new EmptySpace());
            bestPanel.addComponent(new Label("--- By Wins ---"));
            List<SeasonSnapshot> byWins = history.stream()
                .filter(s -> s.getTeamStats() != null)
                .sorted((a, b) -> Integer.compare(b.getTeamStats().getWins(), a.getTeamStats().getWins()))
                .limit(5)
                .toList();
            for (SeasonSnapshot s : byWins) {
                TeamSeasonStats ts = s.getTeamStats();
                String cup = s.wonStanleyCup() ? " 🏆" : "";
                bestPanel.addComponent(new Label(String.format("%d: %d wins%s", s.getSeason(), ts.getWins(), cup)));
            }
        }

        bestPanel.addComponent(new EmptySpace());
        bestPanel.addComponent(new Button("Back", () -> {
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(bestPanel);
    }

    private void showNotableTrades(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel tradesPanel = new Panel();
        tradesPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        tradesPanel.addComponent(new Label("=== Notable Trades ==="));
        tradesPanel.addComponent(new EmptySpace());

        List<Trade> trades = tracker.getTrades();
        if (trades == null || trades.isEmpty()) {
            tradesPanel.addComponent(new Label("No trades recorded."));
        } else {
            tradesPanel.addComponent(new Label("Total Trades: " + trades.size()));
            tradesPanel.addComponent(new EmptySpace());

            // Show all trades grouped by season
            Map<Integer, List<Trade>> bySeason = trades.stream()
                .collect(Collectors.groupingBy(Trade::getSeason));

            bySeason.keySet().stream()
                .sorted(Comparator.reverseOrder())
                .forEach(season -> {
                    tradesPanel.addComponent(new Label("═══ Season " + season + " ═══"));
                    for (Trade t : bySeason.get(season)) {
                        tradesPanel.addComponent(new Label("  With " + t.getTeam2Name() + ":"));
                        tradesPanel.addComponent(new Label("    Got: " + t.getReceivedSummary()));
                        tradesPanel.addComponent(new Label("    Gave: " + t.getSentSummary()));
                    }
                });
        }

        tradesPanel.addComponent(new EmptySpace());
        tradesPanel.addComponent(new Button("Back", () -> {
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(tradesPanel);
    }

    private void showDraftSuccesses(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel draftPanel = new Panel();
        draftPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        draftPanel.addComponent(new Label("=== Draft Successes ==="));
        draftPanel.addComponent(new EmptySpace());

        List<DraftedPlayer> drafted = tracker.getDraftPicks();
        if (drafted == null || drafted.isEmpty()) {
            draftPanel.addComponent(new Label("No players drafted."));
        } else {
            draftPanel.addComponent(new Label("Total Players Drafted: " + drafted.size()));
            
            long signedCount = drafted.stream().filter(DraftedPlayer::isSignedToContract).count();
            long nhlCount = drafted.stream().filter(DraftedPlayer::isMadeNHL).count();
            
            draftPanel.addComponent(new Label("Signed to Contract: " + signedCount));
            draftPanel.addComponent(new Label("Made NHL Roster: " + nhlCount));
            draftPanel.addComponent(new EmptySpace());

            // List notable draft picks (first rounders, NHL players)
            draftPanel.addComponent(new Label("--- First Round Picks ---"));
            drafted.stream()
                .filter(d -> d.getRound() == 1)
                .sorted(Comparator.comparingInt(DraftedPlayer::getYear))
                .forEach(d -> {
                    String status = d.isMadeNHL() ? " [NHL]" : (d.isSignedToContract() ? " [Signed]" : "");
                    draftPanel.addComponent(new Label("  " + d.getSummary() + status));
                });

            draftPanel.addComponent(new EmptySpace());
            draftPanel.addComponent(new Label("--- Players Who Made NHL ---"));
            drafted.stream()
                .filter(DraftedPlayer::isMadeNHL)
                .sorted(Comparator.comparingInt(DraftedPlayer::getYear))
                .forEach(d -> {
                    draftPanel.addComponent(new Label("  " + d.getDisplayName() + " (" + d.getYear() + " R" + d.getRound() + ")"));
                });
        }

        draftPanel.addComponent(new EmptySpace());
        draftPanel.addComponent(new Button("Back", () -> {
            showFranchiseSummary(tracker, screen, window, logger, parentPanel);
        }));

        window.setComponent(draftPanel);
    }
}

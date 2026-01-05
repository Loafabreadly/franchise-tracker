package com.github.loafabreadly.franchisetracker.scene;

import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * ProspectTracker provides a dashboard for viewing and comparing AHL prospects.
 */
public class ProspectTracker extends Panel {

    private Player selectedForComparison = null;

    /**
     * Creates the prospect tracker panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public ProspectTracker(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showProspectList(tracker, screen, window, logger, parentPanel);
    }

    private void showProspectList(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Prospect Tracker ==="));
        addComponent(new Label("AHL: " + tracker.getSelectedAHLTeam().getName()));
        addComponent(new EmptySpace());

        if (selectedForComparison != null) {
            addComponent(new Label("Selected for comparison: " + selectedForComparison.getFullName()));
            addComponent(new Button("Clear Selection", () -> {
                selectedForComparison = null;
                showProspectList(tracker, screen, window, logger, parentPanel);
                window.setComponent(this);
            }));
            addComponent(new EmptySpace());
        }

        List<Player> prospects = tracker.getProspects();
        if (prospects.isEmpty()) {
            addComponent(new Label("No prospects in the system."));
        } else {
            addComponent(new Label(String.format("%-20s %-5s %-3s %-4s %-12s", "Name", "Pos", "OVR", "Age", "Potential")));
            addComponent(new Label("─".repeat(50)));
            
            prospects.stream()
                .sorted(Comparator.comparingInt(Player::getOverall).reversed())
                .forEach(prospect -> {
                    String potStr = prospect.getPotential() != null 
                        ? prospect.getPotential().getDisplayName() : "Unknown";
                    String info = String.format("%-20s %-5s %-3d %-4d %-12s",
                        prospect.getFullName().substring(0, Math.min(20, prospect.getFullName().length())),
                        prospect.getPosition() != null ? prospect.getPosition().name().substring(0, Math.min(5, prospect.getPosition().name().length())) : "N/A",
                        prospect.getOverall(),
                        prospect.getAge(),
                        potStr.substring(0, Math.min(12, potStr.length())));
                    
                    addComponent(new Button(info, () -> {
                        if (selectedForComparison != null && selectedForComparison != prospect) {
                            showComparison(selectedForComparison, prospect, tracker, screen, window, logger, parentPanel);
                        } else {
                            showProspectDetails(prospect, tracker, screen, window, logger, parentPanel);
                        }
                    }));
                });
        }

        addComponent(new EmptySpace());
        addComponent(new Button("View Unsigned Draft Picks", () -> {
            showUnsignedPicks(tracker, screen, window, logger, parentPanel);
        }));
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showProspectDetails(Player prospect, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel detailPanel = new Panel();
        detailPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        detailPanel.addComponent(new Label("=== " + prospect.getFullName() + " ==="));
        detailPanel.addComponent(new EmptySpace());

        detailPanel.addComponent(new Label("Position: " + (prospect.getPosition() != null ? prospect.getPosition().name() : "N/A")));
        detailPanel.addComponent(new Label("Overall: " + prospect.getOverall()));
        detailPanel.addComponent(new Label("Age: " + prospect.getAge()));
        detailPanel.addComponent(new Label("Style: " + (prospect.getStyle() != null ? prospect.getStyle().name() : "N/A")));
        detailPanel.addComponent(new EmptySpace());

        detailPanel.addComponent(new Label("--- Potential ---"));
        detailPanel.addComponent(new Label("Tier: " + (prospect.getPotential() != null ? prospect.getPotential().getDisplayName() : "Unknown")));
        detailPanel.addComponent(new Label("Accuracy: " + (prospect.getPotentialAccuracy() != null ? prospect.getPotentialAccuracy().getDisplayName() : "Unknown")));
        detailPanel.addComponent(new EmptySpace());

        if (prospect.getDraftYear() > 0) {
            detailPanel.addComponent(new Label("--- Draft Info ---"));
            detailPanel.addComponent(new Label("Drafted: " + prospect.getDraftYear() + " Round " + prospect.getDraftRound() + " (#" + prospect.getDraftOverallPick() + ")"));
            detailPanel.addComponent(new EmptySpace());
        }

        if (prospect.getContract() != null) {
            detailPanel.addComponent(new Label("--- Contract ---"));
            detailPanel.addComponent(new Label("AAV: $" + String.format("%.2f", prospect.getContract().getAav()) + "M"));
            detailPanel.addComponent(new Label("Years: " + prospect.getContract().getTermInYears()));
            detailPanel.addComponent(new EmptySpace());
        }

        // X-Factors
        if (prospect.getXFactors() != null && !prospect.getXFactors().isEmpty()) {
            detailPanel.addComponent(new Label("--- X-Factors ---"));
            for (PLAYER_XFACTORS xf : prospect.getXFactors()) {
                detailPanel.addComponent(new Label("  • " + xf.name()));
            }
            detailPanel.addComponent(new EmptySpace());
        }

        detailPanel.addComponent(new Button("Select for Comparison", () -> {
            selectedForComparison = prospect;
            showProspectList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        detailPanel.addComponent(new Button("Call Up to NHL", () -> {
            tracker.callUpPlayerFromAHL(prospect);
            showProspectList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        detailPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(detailPanel);
    }

    private void showComparison(Player player1, Player player2, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel compPanel = new Panel();
        compPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        compPanel.addComponent(new Label("=== Prospect Comparison ==="));
        compPanel.addComponent(new EmptySpace());

        // Side by side header
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s", player1.getFullName(), player2.getFullName())));
        compPanel.addComponent(new Label("─".repeat(25) + "┼" + "─".repeat(25)));

        // Attributes comparison
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Overall: " + player1.getOverall(),
            "Overall: " + player2.getOverall())));
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Age: " + player1.getAge(),
            "Age: " + player2.getAge())));
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Pos: " + (player1.getPosition() != null ? player1.getPosition().name() : "N/A"),
            "Pos: " + (player2.getPosition() != null ? player2.getPosition().name() : "N/A"))));
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Style: " + (player1.getStyle() != null ? player1.getStyle().name() : "N/A"),
            "Style: " + (player2.getStyle() != null ? player2.getStyle().name() : "N/A"))));

        compPanel.addComponent(new EmptySpace());
        compPanel.addComponent(new Label("─".repeat(25) + "┼" + "─".repeat(25)));

        // Potential comparison
        String pot1 = player1.getPotential() != null ? player1.getPotential().getDisplayName() : "Unknown";
        String pot2 = player2.getPotential() != null ? player2.getPotential().getDisplayName() : "Unknown";
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Potential: " + pot1.substring(0, Math.min(15, pot1.length())),
            "Potential: " + pot2.substring(0, Math.min(15, pot2.length())))));

        String acc1 = player1.getPotentialAccuracy() != null ? player1.getPotentialAccuracy().getDisplayName() : "?";
        String acc2 = player2.getPotentialAccuracy() != null ? player2.getPotentialAccuracy().getDisplayName() : "?";
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Accuracy: " + acc1,
            "Accuracy: " + acc2)));

        compPanel.addComponent(new EmptySpace());
        compPanel.addComponent(new Label("─".repeat(25) + "┼" + "─".repeat(25)));

        // Contract comparison
        String contract1 = player1.getContract() != null 
            ? "$" + String.format("%.2f", player1.getContract().getAav()) + "M x " + player1.getContract().getTermInYears() + "y"
            : "No contract";
        String contract2 = player2.getContract() != null 
            ? "$" + String.format("%.2f", player2.getContract().getAav()) + "M x " + player2.getContract().getTermInYears() + "y"
            : "No contract";
        compPanel.addComponent(new Label(String.format("%-25s │ %-25s",
            "Contract: " + contract1,
            "Contract: " + contract2)));

        compPanel.addComponent(new EmptySpace());
        compPanel.addComponent(new Button("Clear Selection & Back", () -> {
            selectedForComparison = null;
            showProspectList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        window.setComponent(compPanel);
    }

    private void showUnsignedPicks(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel unsignedPanel = new Panel();
        unsignedPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        unsignedPanel.addComponent(new Label("=== Unsigned Draft Picks ==="));
        unsignedPanel.addComponent(new EmptySpace());

        List<DraftedPlayer> unsigned = tracker.getUnsignedDraftees();
        if (unsigned.isEmpty()) {
            unsignedPanel.addComponent(new Label("All draft picks have been signed."));
        } else {
            for (DraftedPlayer dp : unsigned) {
                String potStr = dp.getPotentialAtDraft() != null ? dp.getPotentialAtDraft().getDisplayName() : "?";
                unsignedPanel.addComponent(new Label(dp.getSummary() + " | Pot: " + potStr));
            }
        }

        unsignedPanel.addComponent(new EmptySpace());
        unsignedPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(unsignedPanel);
    }
}

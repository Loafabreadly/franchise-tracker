package com.github.loafabreadly.franchisetracker.scene;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * TradeLog provides a TUI for recording and viewing trades.
 */
public class TradeLog extends Panel {

    /**
     * Creates the trade log panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public TradeLog(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showMainMenu(tracker, screen, window, logger, parentPanel);
    }

    private void showMainMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Trade Log - Season " + tracker.getCurrentSeason() + " ==="));
        addComponent(new EmptySpace());

        addComponent(new Button("Record New Trade", () -> {
            showRecordTradeForm(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("View Trade History", () -> {
            showTradeHistory(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showRecordTradeForm(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Record New Trade ==="));
        formPanel.addComponent(new Label(tracker.getSelectedNHLTeam().getName() + " trades with:"));
        formPanel.addComponent(new EmptySpace());

        TextBox otherTeamBox = new TextBox().setPreferredSize(new TerminalSize(25, 1));
        TextBox receivedBox = new TextBox().setPreferredSize(new TerminalSize(40, 1));
        TextBox sentBox = new TextBox().setPreferredSize(new TerminalSize(40, 1));
        TextBox notesBox = new TextBox().setPreferredSize(new TerminalSize(40, 1));

        formPanel.addComponent(new Label("Trade Partner (team name):"));
        formPanel.addComponent(otherTeamBox);
        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Label("Received (comma-separated):"));
        formPanel.addComponent(new Label("  e.g., John Smith, 2025 1st, 2026 3rd"));
        formPanel.addComponent(receivedBox);
        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Label("Sent (comma-separated):"));
        formPanel.addComponent(new Label("  e.g., Mike Jones, 2025 2nd"));
        formPanel.addComponent(sentBox);
        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Label("Notes (optional):"));
        formPanel.addComponent(notesBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Record Trade", () -> {
            Trade trade = new Trade(
                tracker.getSelectedNHLTeam().getName(),
                otherTeamBox.getText(),
                tracker.getCurrentSeason(),
                new Date()
            );
            
            // Parse received assets
            if (!receivedBox.getText().isEmpty()) {
                for (String asset : receivedBox.getText().split(",")) {
                    trade.getTeam1Receives().add(asset.trim());
                }
            }
            
            // Parse sent assets
            if (!sentBox.getText().isEmpty()) {
                for (String asset : sentBox.getText().split(",")) {
                    trade.getTeam2Receives().add(asset.trim());
                }
            }
            
            trade.setNotes(notesBox.getText());
            tracker.recordTrade(trade);

            showMainMenu(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showTradeHistory(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel historyPanel = new Panel();
        historyPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        historyPanel.addComponent(new Label("=== Trade History ==="));
        historyPanel.addComponent(new EmptySpace());

        List<Trade> trades = tracker.getTrades();
        if (trades == null || trades.isEmpty()) {
            historyPanel.addComponent(new Label("No trades recorded."));
        } else {
            // Group by season, most recent first
            trades.stream()
                .sorted(Comparator.comparingInt(Trade::getSeason).reversed())
                .forEach(trade -> {
                    historyPanel.addComponent(new Label("═══ Season " + trade.getSeason() + " ═══"));
                    historyPanel.addComponent(new Label("With: " + trade.getTeam2Name()));
                    historyPanel.addComponent(new Label("  Received: " + trade.getReceivedSummary()));
                    historyPanel.addComponent(new Label("  Sent:     " + trade.getSentSummary()));
                    if (trade.getNotes() != null && !trade.getNotes().isEmpty()) {
                        historyPanel.addComponent(new Label("  Notes: " + trade.getNotes()));
                    }
                    historyPanel.addComponent(new EmptySpace());
                });
        }

        historyPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(historyPanel);
    }
}

package com.github.loafabreadly.franchisetracker.scene;

import java.util.Arrays;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Logger;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * ContractEditor provides a TUI for editing player contracts.
 */
public class ContractEditor extends Panel {

    /**
     * Creates the contract editor panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public ContractEditor(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showPlayerList(tracker, screen, window, logger, parentPanel);
    }

    private void showPlayerList(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Contract Editor ==="));
        addComponent(new Label("Cap Ceiling: $" + String.format("%.1f", tracker.getCapCeiling()) + "M | " +
                              "Cap Hit: $" + String.format("%.1f", tracker.getTotalCapHit()) + "M | " +
                              "Space: $" + String.format("%.1f", tracker.getCapSpace()) + "M"));
        addComponent(new EmptySpace());

        // NHL Roster Contracts
        addComponent(new Label("--- NHL Roster (" + tracker.getSelectedNHLTeam().getName() + ") ---"));
        if (tracker.getSelectedNHLTeam().getRoster() != null && !tracker.getSelectedNHLTeam().getRoster().isEmpty()) {
            for (Player player : tracker.getSelectedNHLTeam().getRoster()) {
                String contractInfo = formatContractInfo(player);
                addComponent(new Button(contractInfo, () -> {
                    showContractForm(player, tracker, screen, window, logger, parentPanel);
                }));
            }
        } else {
            addComponent(new Label("  (No players)"));
        }

        addComponent(new EmptySpace());

        // AHL Roster Contracts
        addComponent(new Label("--- AHL Roster (" + tracker.getSelectedAHLTeam().getName() + ") ---"));
        if (tracker.getSelectedAHLTeam().getRoster() != null && !tracker.getSelectedAHLTeam().getRoster().isEmpty()) {
            for (Player player : tracker.getSelectedAHLTeam().getRoster()) {
                String contractInfo = formatContractInfo(player);
                addComponent(new Button(contractInfo, () -> {
                    showContractForm(player, tracker, screen, window, logger, parentPanel);
                }));
            }
        } else {
            addComponent(new Label("  (No players)"));
        }

        addComponent(new EmptySpace());
        addComponent(new Button("Adjust Cap Ceiling", () -> {
            showCapCeilingForm(tracker, screen, window, logger, parentPanel);
        }));
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private String formatContractInfo(Player player) {
        if (player.getContract() != null) {
            Contract c = player.getContract();
            String status = c.getStatus() != null ? c.getStatus().name() : "N/A";
            return String.format("%-18s $%.1fM x %dy (%s)", 
                player.getFullName().substring(0, Math.min(18, player.getFullName().length())),
                c.getAav(), c.getTermInYears(), status);
        } else {
            return String.format("%-18s [No Contract]", 
                player.getFullName().substring(0, Math.min(18, player.getFullName().length())));
        }
    }

    private void showContractForm(Player player, FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Contract: " + player.getFullName() + " ==="));
        formPanel.addComponent(new Label("Position: " + (player.getPosition() != null ? player.getPosition().name() : "N/A") +
                                        " | Overall: " + player.getOverall()));
        formPanel.addComponent(new EmptySpace());

        Contract existing = player.getContract();
        
        TextBox aavBox = new TextBox(existing != null ? String.format("%.2f", existing.getAav()) : "1.00")
            .setValidationPattern(Pattern.compile("\\d{0,2}\\.?\\d{0,2}"))
            .setPreferredSize(new TerminalSize(10, 1));
        TextBox termBox = new TextBox(existing != null ? String.valueOf(existing.getTermInYears()) : "2")
            .setValidationPattern(Pattern.compile("\\d{0,2}"))
            .setPreferredSize(new TerminalSize(5, 1));
        TextBox signingYearBox = new TextBox(existing != null ? String.valueOf(existing.getSigningYear()) : String.valueOf(tracker.getCurrentSeason()))
            .setValidationPattern(Pattern.compile("\\d{4}"))
            .setPreferredSize(new TerminalSize(6, 1));

        ComboBox<CONTRACT_STATUS> statusCombo = new ComboBox<>();
        for (CONTRACT_STATUS status : CONTRACT_STATUS.values()) {
            statusCombo.addItem(status);
            if (existing != null && status == existing.getStatus()) {
                statusCombo.setSelectedIndex(Arrays.asList(CONTRACT_STATUS.values()).indexOf(status));
            }
        }

        ComboBox<String> clauseCombo = new ComboBox<>();
        clauseCombo.addItem("None");
        for (CONTRACT_CLAUSE clause : CONTRACT_CLAUSE.values()) {
            clauseCombo.addItem(clause.name());
            if (existing != null && existing.getClauses() == clause) {
                clauseCombo.setSelectedIndex(Arrays.asList(CONTRACT_CLAUSE.values()).indexOf(clause) + 1);
            }
        }

        CheckBox twoWayCheck = new CheckBox("Two-Way Contract");
        if (existing != null) {
            twoWayCheck.setChecked(existing.isTwoWay());
        }

        formPanel.addComponent(new Label("AAV (in millions, e.g., 5.75):"));
        formPanel.addComponent(aavBox);
        formPanel.addComponent(new Label("Term (years):"));
        formPanel.addComponent(termBox);
        formPanel.addComponent(new Label("Signing Year:"));
        formPanel.addComponent(signingYearBox);
        formPanel.addComponent(new Label("Status (RFA/UFA after expiry):"));
        formPanel.addComponent(statusCombo);
        formPanel.addComponent(new Label("Clause:"));
        formPanel.addComponent(clauseCombo);
        formPanel.addComponent(twoWayCheck);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save Contract", () -> {
            Contract contract = new Contract();
            contract.setAav(Double.parseDouble(aavBox.getText().isEmpty() ? "0" : aavBox.getText()));
            contract.setTermInYears(Integer.parseInt(termBox.getText().isEmpty() ? "0" : termBox.getText()));
            contract.setSigningYear(Integer.parseInt(signingYearBox.getText().isEmpty() ? 
                String.valueOf(tracker.getCurrentSeason()) : signingYearBox.getText()));
            contract.setStatus(statusCombo.getSelectedItem());
            if (clauseCombo.getSelectedIndex() > 0) {
                contract.setClauses(CONTRACT_CLAUSE.values()[clauseCombo.getSelectedIndex() - 1]);
            }
            contract.setTwoWay(twoWayCheck.isChecked());
            
            player.setContract(contract);
            showPlayerList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Remove Contract", () -> {
            player.setContract(null);
            showPlayerList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }

    private void showCapCeilingForm(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel formPanel = new Panel();
        formPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        formPanel.addComponent(new Label("=== Adjust Salary Cap ==="));
        formPanel.addComponent(new EmptySpace());

        TextBox ceilingBox = new TextBox(String.format("%.1f", tracker.getCapCeiling()))
            .setValidationPattern(Pattern.compile("\\d{0,3}\\.?\\d?"))
            .setPreferredSize(new TerminalSize(10, 1));
        TextBox floorBox = new TextBox(String.format("%.1f", tracker.getCapFloor()))
            .setValidationPattern(Pattern.compile("\\d{0,3}\\.?\\d?"))
            .setPreferredSize(new TerminalSize(10, 1));

        formPanel.addComponent(new Label("Cap Ceiling (millions):"));
        formPanel.addComponent(ceilingBox);
        formPanel.addComponent(new Label("Cap Floor (millions):"));
        formPanel.addComponent(floorBox);

        formPanel.addComponent(new EmptySpace());
        formPanel.addComponent(new Button("Save", () -> {
            tracker.setCapCeiling(Double.parseDouble(ceilingBox.getText().isEmpty() ? "88.0" : ceilingBox.getText()));
            tracker.setCapFloor(Double.parseDouble(floorBox.getText().isEmpty() ? "65.0" : floorBox.getText()));
            showPlayerList(tracker, screen, window, logger, parentPanel);
            window.setComponent(this);
        }));

        formPanel.addComponent(new Button("Cancel", () -> {
            window.setComponent(this);
        }));

        window.setComponent(formPanel);
    }
}

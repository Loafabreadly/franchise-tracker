package com.github.loafabreadly.franchisetracker.scene;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.github.loafabreadly.franchisetracker.model.*;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

/**
 * ImportExport provides CSV/JSON import and export functionality for rosters.
 */
public class ImportExport extends Panel {

    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Creates the import/export panel.
     * @param tracker The franchise tracker instance
     * @param screen The Lanterna screen
     * @param window The main window
     * @param logger The logger instance
     * @param parentPanel The panel to return to
     */
    public ImportExport(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        setLayoutManager(new LinearLayout(Direction.VERTICAL));
        showMainMenu(tracker, screen, window, logger, parentPanel);
    }

    private void showMainMenu(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        removeAllComponents();
        addComponent(new Label("=== Import / Export ==="));
        addComponent(new EmptySpace());

        addComponent(new Button("Export Roster to CSV", () -> {
            showExportCSV(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Export Roster to JSON", () -> {
            showExportJSON(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Import Roster from CSV", () -> {
            showImportCSV(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new Button("Import Roster from JSON", () -> {
            showImportJSON(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("View CSV Template", () -> {
            showCSVTemplate(tracker, screen, window, logger, parentPanel);
        }));

        addComponent(new EmptySpace());
        addComponent(new Button("Back", () -> {
            window.setComponent(parentPanel);
        }));
    }

    private void showExportCSV(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel exportPanel = new Panel();
        exportPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        exportPanel.addComponent(new Label("=== Export Roster to CSV ==="));
        exportPanel.addComponent(new EmptySpace());

        TextBox fileNameBox = new TextBox("roster_export.csv").setPreferredSize(new TerminalSize(30, 1));
        exportPanel.addComponent(new Label("Filename:"));
        exportPanel.addComponent(fileNameBox);

        exportPanel.addComponent(new EmptySpace());
        exportPanel.addComponent(new Button("Export", () -> {
            try {
                String fileName = fileNameBox.getText();
                exportRosterToCSV(tracker, fileName);
                exportPanel.addComponent(new Label("✓ Exported to " + fileName));
            } catch (Exception e) {
                logger.error("Export failed", e);
                exportPanel.addComponent(new Label("✗ Error: " + e.getMessage()));
            }
        }));

        exportPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(exportPanel);
    }

    private void exportRosterToCSV(FranchiseTracker tracker, String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("League,FirstName,LastName,Position,Overall,Age,Potential,AAV,ContractYears,Status\n");

        // NHL roster
        if (tracker.getSelectedNHLTeam().getRoster() != null) {
            for (Player p : tracker.getSelectedNHLTeam().getRoster()) {
                sb.append(formatPlayerCSV("NHL", p));
            }
        }

        // AHL roster
        if (tracker.getSelectedAHLTeam().getRoster() != null) {
            for (Player p : tracker.getSelectedAHLTeam().getRoster()) {
                sb.append(formatPlayerCSV("AHL", p));
            }
        }

        Files.writeString(Path.of(fileName), sb.toString());
    }

    private String formatPlayerCSV(String league, Player p) {
        String potential = p.getPotential() != null ? p.getPotential().name() : "";
        double aav = p.getContract() != null ? p.getContract().getAav() : 0;
        int years = p.getContract() != null ? p.getContract().getTermInYears() : 0;
        String status = p.getContract() != null && p.getContract().getStatus() != null 
            ? p.getContract().getStatus().name() : "";
        
        return String.format("%s,%s,%s,%s,%d,%d,%s,%.2f,%d,%s\n",
            league,
            p.getFirstName(),
            p.getLastName(),
            p.getPosition() != null ? p.getPosition().name() : "",
            p.getOverall(),
            p.getAge(),
            potential,
            aav,
            years,
            status);
    }

    private void showExportJSON(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel exportPanel = new Panel();
        exportPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        exportPanel.addComponent(new Label("=== Export Roster to JSON ==="));
        exportPanel.addComponent(new EmptySpace());

        TextBox fileNameBox = new TextBox("roster_export.json").setPreferredSize(new TerminalSize(30, 1));
        exportPanel.addComponent(new Label("Filename:"));
        exportPanel.addComponent(fileNameBox);

        exportPanel.addComponent(new EmptySpace());
        exportPanel.addComponent(new Button("Export", () -> {
            try {
                String fileName = fileNameBox.getText();
                exportRosterToJSON(tracker, fileName);
                exportPanel.addComponent(new Label("✓ Exported to " + fileName));
            } catch (Exception e) {
                logger.error("Export failed", e);
                exportPanel.addComponent(new Label("✗ Error: " + e.getMessage()));
            }
        }));

        exportPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(exportPanel);
    }

    private void exportRosterToJSON(FranchiseTracker tracker, String fileName) throws IOException {
        RosterExport export = new RosterExport();
        export.nhlTeam = tracker.getSelectedNHLTeam().getName();
        export.ahlTeam = tracker.getSelectedAHLTeam().getName();
        export.nhlRoster = tracker.getSelectedNHLTeam().getRoster();
        export.ahlRoster = tracker.getSelectedAHLTeam().getRoster();
        
        mapper.writeValue(new File(fileName), export);
    }

    private void showImportCSV(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel importPanel = new Panel();
        importPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        importPanel.addComponent(new Label("=== Import Roster from CSV ==="));
        importPanel.addComponent(new EmptySpace());

        // Show available CSV files
        File dir = new File(System.getProperty("user.dir"));
        File[] csvFiles = dir.listFiles((d, name) -> name.endsWith(".csv"));

        if (csvFiles != null && csvFiles.length > 0) {
            importPanel.addComponent(new Label("Select file to import:"));
            for (File file : csvFiles) {
                importPanel.addComponent(new Button(file.getName(), () -> {
                    try {
                        int imported = importRosterFromCSV(tracker, file.getName());
                        importPanel.addComponent(new Label("✓ Imported " + imported + " players"));
                    } catch (Exception e) {
                        logger.error("Import failed", e);
                        importPanel.addComponent(new Label("✗ Error: " + e.getMessage()));
                    }
                }));
            }
        } else {
            importPanel.addComponent(new Label("No CSV files found in current directory."));
        }

        importPanel.addComponent(new EmptySpace());
        importPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(importPanel);
    }

    private int importRosterFromCSV(FranchiseTracker tracker, String fileName) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(fileName));
        int imported = 0;

        // Skip header
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length < 6) continue;

            Player player = new Player();
            String league = parts[0].trim();
            player.setFirstName(parts[1].trim());
            player.setLastName(parts[2].trim());
            
            if (!parts[3].trim().isEmpty()) {
                try {
                    player.setPosition(PLAYER_POS.valueOf(parts[3].trim()));
                } catch (IllegalArgumentException ignored) {}
            }
            
            player.setOverall(Integer.parseInt(parts[4].trim()));
            player.setAge(Integer.parseInt(parts[5].trim()));
            
            if (parts.length > 6 && !parts[6].trim().isEmpty()) {
                try {
                    player.setPotential(PLAYER_POTENTIAL.valueOf(parts[6].trim()));
                } catch (IllegalArgumentException ignored) {}
            }
            
            if (parts.length > 8) {
                double aav = Double.parseDouble(parts[7].trim());
                int years = Integer.parseInt(parts[8].trim());
                if (aav > 0 || years > 0) {
                    Contract contract = new Contract();
                    contract.setAav(aav);
                    contract.setTermInYears(years);
                    if (parts.length > 9 && !parts[9].trim().isEmpty()) {
                        try {
                            contract.setStatus(CONTRACT_STATUS.valueOf(parts[9].trim()));
                        } catch (IllegalArgumentException ignored) {}
                    }
                    player.setContract(contract);
                }
            }
            
            player.setCareerStats(new ArrayList<>());

            if ("AHL".equalsIgnoreCase(league)) {
                tracker.addPlayerToAHL(player);
            } else {
                tracker.addPlayerToNHL(player);
            }
            imported++;
        }

        return imported;
    }

    private void showImportJSON(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel importPanel = new Panel();
        importPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        importPanel.addComponent(new Label("=== Import Roster from JSON ==="));
        importPanel.addComponent(new EmptySpace());

        // Show available JSON files
        File dir = new File(System.getProperty("user.dir"));
        File[] jsonFiles = dir.listFiles((d, name) -> name.endsWith(".json") && !name.endsWith(".nhl"));

        if (jsonFiles != null && jsonFiles.length > 0) {
            importPanel.addComponent(new Label("Select file to import:"));
            for (File file : jsonFiles) {
                importPanel.addComponent(new Button(file.getName(), () -> {
                    try {
                        int imported = importRosterFromJSON(tracker, file.getName());
                        importPanel.addComponent(new Label("✓ Imported " + imported + " players"));
                    } catch (Exception e) {
                        logger.error("Import failed", e);
                        importPanel.addComponent(new Label("✗ Error: " + e.getMessage()));
                    }
                }));
            }
        } else {
            importPanel.addComponent(new Label("No JSON files found in current directory."));
        }

        importPanel.addComponent(new EmptySpace());
        importPanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(importPanel);
    }

    private int importRosterFromJSON(FranchiseTracker tracker, String fileName) throws IOException {
        RosterExport importData = mapper.readValue(new File(fileName), RosterExport.class);
        int imported = 0;

        if (importData.nhlRoster != null) {
            for (Player p : importData.nhlRoster) {
                if (p.getCareerStats() == null) p.setCareerStats(new ArrayList<>());
                tracker.addPlayerToNHL(p);
                imported++;
            }
        }

        if (importData.ahlRoster != null) {
            for (Player p : importData.ahlRoster) {
                if (p.getCareerStats() == null) p.setCareerStats(new ArrayList<>());
                tracker.addPlayerToAHL(p);
                imported++;
            }
        }

        return imported;
    }

    private void showCSVTemplate(FranchiseTracker tracker, Screen screen, BasicWindow window, Logger logger, Panel parentPanel) {
        Panel templatePanel = new Panel();
        templatePanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        templatePanel.addComponent(new Label("=== CSV Import Template ==="));
        templatePanel.addComponent(new EmptySpace());

        templatePanel.addComponent(new Label("Header row:"));
        templatePanel.addComponent(new Label("League,FirstName,LastName,Position,Overall,Age,Potential,AAV,ContractYears,Status"));
        templatePanel.addComponent(new EmptySpace());

        templatePanel.addComponent(new Label("Example rows:"));
        templatePanel.addComponent(new Label("NHL,Connor,McDavid,CENTER,97,28,FRANCHISE,12.50,5,UFA"));
        templatePanel.addComponent(new Label("NHL,Leon,Draisaitl,CENTER,94,29,ELITE,8.50,3,UFA"));
        templatePanel.addComponent(new Label("AHL,Dylan,Holloway,LEFT_WING,78,22,TOP_6F,0.92,2,RFA"));
        templatePanel.addComponent(new EmptySpace());

        templatePanel.addComponent(new Label("Valid Positions:"));
        templatePanel.addComponent(new Label("  CENTER, LEFT_WING, RIGHT_WING,"));
        templatePanel.addComponent(new Label("  LEFT_DEFENSE, RIGHT_DEFENSE, GOALIE"));
        templatePanel.addComponent(new EmptySpace());

        templatePanel.addComponent(new Label("Valid Potentials:"));
        templatePanel.addComponent(new Label("  FRANCHISE, ELITE, TOP_6F, TOP_4D,"));
        templatePanel.addComponent(new Label("  TOP_9F, TOP_6D, BOTTOM_6F, BOTTOM_PAIR_D, AHL"));
        templatePanel.addComponent(new EmptySpace());

        templatePanel.addComponent(new Button("Back", () -> {
            window.setComponent(this);
        }));

        window.setComponent(templatePanel);
    }

    // Helper class for JSON export
    private static class RosterExport {
        public String nhlTeam;
        public String ahlTeam;
        public List<Player> nhlRoster;
        public List<Player> ahlRoster;
    }
}

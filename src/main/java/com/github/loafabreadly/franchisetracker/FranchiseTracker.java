package com.github.loafabreadly.franchisetracker;

import com.github.loafabreadly.franchisetracker.model.*;
import com.github.loafabreadly.franchisetracker.service.FranchiseDataService;
import java.util.*;
import java.io.IOException;

public class FranchiseTracker {
    private List<Team> teams = new ArrayList<>();
    private Team selectedNHLTeam;
    private Team selectedAHLTeam;
    private int currentSeason = 2025;

    public void createNewSave(String nhlTeamName, String ahlTeamName, List<Player> initialRoster, List<DraftPick> draftPicks) {
        selectedNHLTeam = new Team();
        selectedNHLTeam.setName(nhlTeamName);
        selectedNHLTeam.setRoster(initialRoster);
        selectedNHLTeam.setAwards(new ArrayList<>());
        selectedNHLTeam.setSeasonStats(new ArrayList<>());
        selectedNHLTeam.setCareerStats(new CareerStats());
        selectedNHLTeam.setLineup(new Lineup());
        selectedNHLTeam.setIsAHL(false);

        selectedAHLTeam = new Team();
        selectedAHLTeam.setName(ahlTeamName);
        selectedAHLTeam.setRoster(new ArrayList<>());
        selectedAHLTeam.setAwards(new ArrayList<>());
        selectedAHLTeam.setSeasonStats(new ArrayList<>());
        selectedAHLTeam.setCareerStats(new CareerStats());
        selectedAHLTeam.setLineup(new Lineup());
        selectedAHLTeam.setIsAHL(true);

        teams.clear();
        teams.add(selectedNHLTeam);
        teams.add(selectedAHLTeam);
    }

    public void setDraftPicks(List<DraftPick> draftPicks) {
        // Attach draft picks to NHL team for the current season
        // You may want to add a draftPicks field to Team or FranchiseTracker
    }

    public void displayLines(String preset) {
        // Display lines based on preset (highest overall, youngest, etc.)
    }

    public void editLines(Lineup newLineup) {
        selectedNHLTeam.setLineup(newLineup);
    }

    public void enterEndOfSeasonStats(List<SeasonStats> playerStats, SeasonStats teamStats, List<Award> leagueAwards) {
        // Update player, team, and league stats for the season
    }

    public void saveFranchise(String filePath) throws IOException {
        FranchiseDataService.saveTeams(teams, filePath);
    }

    public void loadFranchise(String filePath) throws IOException {
        teams = FranchiseDataService.loadTeams(filePath);
        // Re-assign selectedNHLTeam and selectedAHLTeam based on loaded data
    }

    // Add more methods as needed for TUI interaction
}

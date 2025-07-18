package com.github.loafabreadly.franchisetracker;

import com.github.loafabreadly.franchisetracker.model.*;
import com.github.loafabreadly.franchisetracker.service.FranchiseDataService;

import lombok.Data;

import java.util.*;
import java.io.IOException;

@Data
public class FranchiseTracker {
    private List<Team> teams = new ArrayList<>();
    private Team selectedNHLTeam;
    private Team selectedAHLTeam;
    private int currentSeason = 2025;
    private String generalManagerName;
    private List<DraftPick> draftPicks = new ArrayList<>();
    private List<Award> leagueAwards = new ArrayList<>();
    private List<SeasonStats> playerStats = new ArrayList<>();
    private SeasonStats teamStats;
    private List<Trade> trades = new ArrayList<>();

    public void createNewSave(String nhlTeamName, String ahlTeamName, List<Player> initialRoster, List<DraftPick> draftPicks, String generalManagerName) {
        selectedNHLTeam = new Team();
        selectedNHLTeam.setName(nhlTeamName);
        selectedNHLTeam.setRoster(initialRoster);
        selectedNHLTeam.setAwards(new ArrayList<>());
        selectedNHLTeam.setSeasonStats(new ArrayList<>());
        selectedNHLTeam.setCareerStats(new CareerStats());
        selectedNHLTeam.setLineup(new Lineup());
        selectedNHLTeam.setIsAHL(false);
        this.generalManagerName = generalManagerName;
        this.draftPicks = draftPicks;
        this.currentSeason = 2025; // Reset to default season

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

    public void createNewSave(String nhlTeamName, String ahlTeamName, List<Player> initialRoster, List<DraftPick> draftPicks, String generalManagerName, int currentSeason) {
        selectedNHLTeam = new Team();
        selectedNHLTeam.setName(nhlTeamName);
        selectedNHLTeam.setRoster(initialRoster);
        selectedNHLTeam.setAwards(new ArrayList<>());
        selectedNHLTeam.setSeasonStats(new ArrayList<>());
        selectedNHLTeam.setCareerStats(new CareerStats());
        selectedNHLTeam.setLineup(new Lineup());
        selectedNHLTeam.setIsAHL(false);
        this.generalManagerName = generalManagerName;
        this.draftPicks = draftPicks;
        this.currentSeason = currentSeason; // Reset to default season

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

    public void saveFranchise( String filePath) throws IOException {
        FranchiseDataService.saveTeams(this, filePath);
    }

    public FranchiseTracker loadFranchise(String filePath) throws IOException {
        FranchiseTracker loadedTracker = FranchiseDataService.loadTracker(filePath);
        return loadedTracker;
    }

    public void saveToFile(String filePath) throws IOException {
        FranchiseDataService.saveTeams(this, filePath);
    }

    public void loadFromFile(String filePath) throws IOException {
        FranchiseTracker loaded = FranchiseDataService.loadTracker(filePath);
        this.teams = loaded.teams;
        this.selectedNHLTeam = loaded.selectedNHLTeam;
        this.selectedAHLTeam = loaded.selectedAHLTeam;
        this.currentSeason = loaded.currentSeason;
        this.generalManagerName = loaded.generalManagerName;
        this.draftPicks = loaded.draftPicks;
        this.leagueAwards = loaded.leagueAwards;
        this.playerStats = loaded.playerStats;
        this.teamStats = loaded.teamStats;
        this.trades = loaded.trades;
    }

    // Add more methods as needed for TUI interaction
}

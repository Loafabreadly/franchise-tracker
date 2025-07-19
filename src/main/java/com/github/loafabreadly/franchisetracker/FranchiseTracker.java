package com.github.loafabreadly.franchisetracker;

import com.github.loafabreadly.franchisetracker.model.*;
import com.github.loafabreadly.franchisetracker.service.FranchiseDataService;

import lombok.Data;

import java.util.*;
import java.io.IOException;

@Data
public class FranchiseTracker {
    private List<Team> teams = new ArrayList<>();
    private transient Team selectedNHLTeam;
    private transient Team selectedAHLTeam;
    private int currentSeason;
    private String generalManagerName;
    private List<DraftedPlayer> draftPicks = new ArrayList<>();
    private List<Award> leagueAwards = new ArrayList<>();
    private List<TeamSeasonStats> playerStats = new ArrayList<>();
    private TeamSeasonStats teamStats;
    private List<Trade> trades = new ArrayList<>();

    public FranchiseTracker() {

    }

    public FranchiseTracker (String nhlTeamName, String ahlTeamName, List<Player> initialRoster, List<DraftedPlayer> draftPicks, String generalManagerName, int currentSeason) {
        selectedNHLTeam = new Team();
        selectedNHLTeam.setName(nhlTeamName);
        selectedNHLTeam.setRoster(initialRoster);
        selectedNHLTeam.setAwards(new ArrayList<>());
        selectedNHLTeam.setCareerStats(new ArrayList<>());
        selectedNHLTeam.setLineup(new Lineup());
        selectedNHLTeam.setIsAHL(false);
        selectedNHLTeam.setDraftPicks(new ArrayList<>());
        this.generalManagerName = generalManagerName;
        this.currentSeason = currentSeason;

        selectedAHLTeam = new Team();
        selectedAHLTeam.setName(ahlTeamName);
        selectedAHLTeam.setRoster(new ArrayList<>());
        selectedAHLTeam.setAwards(new ArrayList<>());
        selectedAHLTeam.setCareerStats(new ArrayList<>());
        selectedAHLTeam.setLineup(new Lineup());
        selectedAHLTeam.setIsAHL(true);
        selectedAHLTeam.setDraftPicks(new ArrayList<>());

        teams.clear();
        teams.add(selectedNHLTeam);
        teams.add(selectedAHLTeam);
    }

    public void enterEndOfSeasonStats(List<TeamSeasonStats> playerStats, TeamSeasonStats teamStats, List<Award> leagueAwards) {
        // Update player, team, and league stats for the season
    }

    public void saveFranchise(String filePath) throws IOException {
        FranchiseDataService.saveTeams(this, filePath);
    }

    public FranchiseTracker loadFranchise(String filePath) throws IOException {
        FranchiseTracker loadedTracker = FranchiseDataService.loadTracker(filePath);
        return loadedTracker;
    }
}

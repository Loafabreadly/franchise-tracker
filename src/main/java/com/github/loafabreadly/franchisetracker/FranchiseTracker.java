package com.github.loafabreadly.franchisetracker;

import com.github.loafabreadly.franchisetracker.model.*;
import com.github.loafabreadly.franchisetracker.service.FranchiseDataService;

import lombok.Data;

import java.util.*;
import java.io.IOException;

/**
 * FranchiseTracker manages the state of a hockey franchise, including teams, draft picks, awards, stats, and trades.
 * Provides methods for saving and loading franchise data.
 */
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

    /**
     * Default constructor for FranchiseTracker.
     */
    public FranchiseTracker() {

    }

    /**
     * Constructs a FranchiseTracker with the given team and franchise details.
     * @param nhlTeamName Name of the NHL team
     * @param ahlTeamName Name of the AHL affiliate
     * @param initialRoster Initial player roster for the NHL team
     * @param draftPicks List of drafted players
     * @param generalManagerName Name of the general manager
     * @param currentSeason Starting season year
     */
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

    /**
     * Updates player, team, and league stats for the end of a season.
     * @param playerStats List of player stats for the season
     * @param teamStats Team stats for the season
     * @param leagueAwards List of league awards for the season
     */
    public void enterEndOfSeasonStats(List<TeamSeasonStats> playerStats, TeamSeasonStats teamStats, List<Award> leagueAwards) {
        // Update player, team, and league stats for the season
    }

    /**
     * Saves the current franchise state to a file.
     * @param filePath Path to the file to save to
     * @throws IOException if saving fails
     */
    public void saveFranchise(String filePath) throws IOException {
        FranchiseDataService.saveTeams(this, filePath);
    }

    /**
     * Loads a franchise state from a file.
     * @param filePath Path to the file to load from
     * @return Loaded FranchiseTracker instance
     * @throws IOException if loading fails
     */
    public FranchiseTracker loadFranchise(String filePath) throws IOException {
        FranchiseTracker loadedTracker = FranchiseDataService.loadTracker(filePath);
        return loadedTracker;
    }
}

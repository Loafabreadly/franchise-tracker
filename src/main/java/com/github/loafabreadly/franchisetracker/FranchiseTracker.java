package com.github.loafabreadly.franchisetracker;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
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
    
    // Season archiving
    private List<SeasonSnapshot> seasonHistory = new ArrayList<>();
    
    // Cap management
    private double capCeiling = 88.0; // Default NHL cap ceiling in millions
    private double capFloor = 65.0;
    
    // Future draft picks inventory
    private List<DraftPick> futureDraftPicks = new ArrayList<>();
    
    // Franchise completion status
    private boolean franchiseCompleted = false;
    private int franchiseCompletedSeason;

    /**
     * Default constructor for FranchiseTracker.
     */
    public FranchiseTracker() {
        initializeFutureDraftPicks();
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
        
        initializeFutureDraftPicks();
    }
    
    /**
     * Initializes future draft picks for the next 5 years.
     */
    private void initializeFutureDraftPicks() {
        if (futureDraftPicks == null) {
            futureDraftPicks = new ArrayList<>();
        }
        if (futureDraftPicks.isEmpty()) {
            for (int year = currentSeason; year <= currentSeason + 5; year++) {
                for (int round = 1; round <= 7; round++) {
                    DraftPick pick = new DraftPick(year, round);
                    pick.setCurrentOwner(selectedNHLTeam != null ? selectedNHLTeam.getName() : "Own");
                    pick.setOriginalTeam(selectedNHLTeam != null ? selectedNHLTeam.getName() : "Own");
                    futureDraftPicks.add(pick);
                }
            }
        }
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
     * Loads a franchise state from a file and restores transient team references.
     * @param filePath Path to the file to load from
     * @return Loaded FranchiseTracker instance
     * @throws IOException if loading fails
     */
    public FranchiseTracker loadFranchise(String filePath) throws IOException {
        FranchiseTracker loadedTracker = FranchiseDataService.loadTracker(filePath);
        loadedTracker.restoreTeamReferences();
        return loadedTracker;
    }

    /**
     * Restores transient team references from the teams list after deserialization.
     */
    private void restoreTeamReferences() {
        for (Team team : teams) {
            if (team.isAHL()) {
                selectedAHLTeam = team;
            } else {
                selectedNHLTeam = team;
            }
        }
    }

    /**
     * Advances the franchise to the next season.
     */
    public void advanceSeason() {
        // Archive current season before advancing
        archiveSeason();
        
        // Decrement contract years and age players
        decrementContractYears();
        agePlayersOneYear();
        
        // Add new draft picks for the future
        addFutureDraftPicks(currentSeason + 6);
        
        currentSeason++;
    }
    
    /**
     * Archives the current season state to history.
     */
    public void archiveSeason() {
        List<Player> nhlRoster = selectedNHLTeam != null && selectedNHLTeam.getRoster() != null 
            ? new ArrayList<>(selectedNHLTeam.getRoster()) : new ArrayList<>();
        List<Player> ahlRoster = selectedAHLTeam != null && selectedAHLTeam.getRoster() != null 
            ? new ArrayList<>(selectedAHLTeam.getRoster()) : new ArrayList<>();
        
        List<Award> seasonAwards = leagueAwards != null 
            ? leagueAwards.stream().filter(a -> a.getYear() == currentSeason).toList() 
            : new ArrayList<>();
        
        List<Trade> seasonTrades = trades != null 
            ? trades.stream().filter(t -> t.getSeason() == currentSeason).toList() 
            : new ArrayList<>();
        
        List<DraftedPlayer> seasonDrafts = draftPicks != null 
            ? draftPicks.stream().filter(d -> d.getYear() == currentSeason).toList() 
            : new ArrayList<>();
        
        SeasonSnapshot snapshot = SeasonSnapshot.createSnapshot(
            currentSeason,
            nhlRoster,
            ahlRoster,
            teamStats,
            seasonAwards,
            seasonTrades,
            seasonDrafts,
            futureDraftPicks != null ? new ArrayList<>(futureDraftPicks) : new ArrayList<>(),
            capCeiling
        );
        
        if (seasonHistory == null) {
            seasonHistory = new ArrayList<>();
        }
        seasonHistory.add(snapshot);
    }
    
    /**
     * Decrements contract years for all players.
     */
    private void decrementContractYears() {
        for (Player player : getAllPlayers()) {
            if (player.getContract() != null && player.getContract().getTermInYears() > 0) {
                player.getContract().setTermInYears(player.getContract().getTermInYears() - 1);
            }
        }
    }
    
    /**
     * Ages all players by one year.
     */
    private void agePlayersOneYear() {
        for (Player player : getAllPlayers()) {
            player.setAge(player.getAge() + 1);
        }
    }
    
    /**
     * Adds draft picks for a future year.
     */
    private void addFutureDraftPicks(int year) {
        if (futureDraftPicks == null) {
            futureDraftPicks = new ArrayList<>();
        }
        for (int round = 1; round <= 7; round++) {
            DraftPick pick = new DraftPick(year, round);
            pick.setCurrentOwner(selectedNHLTeam != null ? selectedNHLTeam.getName() : "Own");
            pick.setOriginalTeam(selectedNHLTeam != null ? selectedNHLTeam.getName() : "Own");
            futureDraftPicks.add(pick);
        }
    }
    
    /**
     * Calculates total cap hit for the NHL roster.
     */
    public double getTotalCapHit() {
        if (selectedNHLTeam == null || selectedNHLTeam.getRoster() == null) {
            return 0.0;
        }
        return selectedNHLTeam.getRoster().stream()
            .mapToDouble(Player::getCapHit)
            .sum();
    }
    
    /**
     * Gets remaining cap space.
     */
    public double getCapSpace() {
        return capCeiling - getTotalCapHit();
    }
    
    /**
     * Gets players with expiring contracts for a given year.
     */
    public List<Player> getExpiringContracts(int year) {
        return getAllPlayers().stream()
            .filter(p -> p.getContract() != null && 
                        p.getContract().getExpirationYear() == year)
            .toList();
    }
    
    /**
     * Gets draft picks for a specific year.
     */
    public List<DraftPick> getDraftPicksForYear(int year) {
        if (futureDraftPicks == null) return new ArrayList<>();
        return futureDraftPicks.stream()
            .filter(p -> p.getYear() == year)
            .sorted((a, b) -> a.getRound() - b.getRound())
            .toList();
    }
    
    /**
     * Records a trade.
     */
    public void recordTrade(Trade trade) {
        if (trades == null) {
            trades = new ArrayList<>();
        }
        trade.setSeason(currentSeason);
        trades.add(trade);
    }
    
    /**
     * Records an award.
     */
    public void recordAward(Award award) {
        if (leagueAwards == null) {
            leagueAwards = new ArrayList<>();
        }
        award.setYear(currentSeason);
        leagueAwards.add(award);
    }
    
    /**
     * Records a drafted player.
     */
    public void recordDraftPick(DraftedPlayer draftedPlayer) {
        if (draftPicks == null) {
            draftPicks = new ArrayList<>();
        }
        draftedPlayer.setYear(currentSeason);
        draftPicks.add(draftedPlayer);
        
        // Remove the used pick from future picks
        if (futureDraftPicks != null) {
            futureDraftPicks.removeIf(p -> 
                p.getYear() == currentSeason && p.getRound() == draftedPlayer.getRound());
        }
    }
    
    /**
     * Gets all AHL/prospect players.
     */
    public List<Player> getProspects() {
        List<Player> prospects = new ArrayList<>();
        if (selectedAHLTeam != null && selectedAHLTeam.getRoster() != null) {
            prospects.addAll(selectedAHLTeam.getRoster());
        }
        return prospects;
    }
    
    /**
     * Gets unsigned draft picks.
     */
    public List<DraftedPlayer> getUnsignedDraftees() {
        if (draftPicks == null) return new ArrayList<>();
        return draftPicks.stream()
            .filter(d -> !d.isSignedToContract())
            .toList();
    }
    
    /**
     * Gets the number of Stanley Cups won.
     */
    public int getStanleyCupCount() {
        if (seasonHistory == null) return 0;
        return (int) seasonHistory.stream()
            .filter(SeasonSnapshot::wonStanleyCup)
            .count();
    }
    
    /**
     * Completes the franchise and marks it as finished.
     */
    public void completeFranchise() {
        archiveSeason();
        franchiseCompleted = true;
        franchiseCompletedSeason = currentSeason;
    }

    /**
     * Adds a player to the NHL team roster.
     * @param player The player to add
     */
    public void addPlayerToNHL(Player player) {
        if (selectedNHLTeam != null && selectedNHLTeam.getRoster() != null) {
            selectedNHLTeam.getRoster().add(player);
        }
    }

    /**
     * Adds a player to the AHL team roster.
     * @param player The player to add
     */
    public void addPlayerToAHL(Player player) {
        if (selectedAHLTeam != null && selectedAHLTeam.getRoster() != null) {
            selectedAHLTeam.getRoster().add(player);
        }
    }

    /**
     * Removes a player from the NHL team roster.
     * @param player The player to remove
     */
    public void removePlayerFromNHL(Player player) {
        if (selectedNHLTeam != null && selectedNHLTeam.getRoster() != null) {
            selectedNHLTeam.getRoster().remove(player);
        }
    }

    /**
     * Removes a player from the AHL team roster.
     * @param player The player to remove
     */
    public void removePlayerFromAHL(Player player) {
        if (selectedAHLTeam != null && selectedAHLTeam.getRoster() != null) {
            selectedAHLTeam.getRoster().remove(player);
        }
    }

    /**
     * Moves a player from the NHL roster to the AHL roster.
     * @param player The player to send down
     */
    public void sendPlayerToAHL(Player player) {
        removePlayerFromNHL(player);
        addPlayerToAHL(player);
    }

    /**
     * Moves a player from the AHL roster to the NHL roster.
     * @param player The player to call up
     */
    public void callUpPlayerFromAHL(Player player) {
        removePlayerFromAHL(player);
        addPlayerToNHL(player);
    }

    /**
     * Gets all players from both NHL and AHL rosters.
     * @return Combined list of all players
     */
    public List<Player> getAllPlayers() {
        List<Player> allPlayers = new ArrayList<>();
        if (selectedNHLTeam != null && selectedNHLTeam.getRoster() != null) {
            allPlayers.addAll(selectedNHLTeam.getRoster());
        }
        if (selectedAHLTeam != null && selectedAHLTeam.getRoster() != null) {
            allPlayers.addAll(selectedAHLTeam.getRoster());
        }
        return allPlayers;
    }
}

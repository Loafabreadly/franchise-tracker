package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * SeasonSnapshot captures the complete state of a franchise at the end of a season.
 * Used for historical tracking and the franchise wrap-up summary.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SeasonSnapshot {
    private int season;
    private double capCeiling;
    private double totalCapHit;
    
    // Deep copies of roster state at end of season
    private List<PlayerSnapshot> nhlRoster = new ArrayList<>();
    private List<PlayerSnapshot> ahlRoster = new ArrayList<>();
    
    // Team performance
    private TeamSeasonStats teamStats;
    
    // Awards won this season
    private List<Award> awards = new ArrayList<>();
    
    // Trades made this season
    private List<Trade> trades = new ArrayList<>();
    
    // Draft picks made this season
    private List<DraftedPlayer> draftPicks = new ArrayList<>();
    
    // Future draft pick inventory at end of season
    private List<DraftPick> futurePicks = new ArrayList<>();
    
    /**
     * Creates a snapshot from the current franchise state.
     */
    public static SeasonSnapshot createSnapshot(int season, 
                                                  List<Player> nhlRoster, 
                                                  List<Player> ahlRoster,
                                                  TeamSeasonStats teamStats,
                                                  List<Award> awards,
                                                  List<Trade> trades,
                                                  List<DraftedPlayer> draftPicks,
                                                  List<DraftPick> futurePicks,
                                                  double capCeiling) {
        SeasonSnapshot snapshot = new SeasonSnapshot();
        snapshot.setSeason(season);
        snapshot.setCapCeiling(capCeiling);
        snapshot.setTeamStats(teamStats);
        snapshot.setAwards(new ArrayList<>(awards));
        snapshot.setTrades(new ArrayList<>(trades));
        snapshot.setDraftPicks(new ArrayList<>(draftPicks));
        snapshot.setFuturePicks(new ArrayList<>(futurePicks));
        
        // Create player snapshots
        double totalCap = 0.0;
        for (Player p : nhlRoster) {
            snapshot.getNhlRoster().add(PlayerSnapshot.fromPlayer(p, season));
            totalCap += p.getCapHit();
        }
        for (Player p : ahlRoster) {
            snapshot.getAhlRoster().add(PlayerSnapshot.fromPlayer(p, season));
        }
        snapshot.setTotalCapHit(totalCap);
        
        return snapshot;
    }
    
    /**
     * Gets the total number of players in the snapshot.
     */
    public int getTotalPlayers() {
        return nhlRoster.size() + ahlRoster.size();
    }
    
    /**
     * Gets the team's wins for this season.
     */
    public int getWins() {
        return teamStats != null ? teamStats.getWins() : 0;
    }
    
    /**
     * Gets the team's points for this season.
     */
    public int getPoints() {
        return teamStats != null ? teamStats.getPoints() : 0;
    }
    
    /**
     * Checks if the team won the Stanley Cup this season.
     */
    public boolean wonStanleyCup() {
        return awards.stream()
            .anyMatch(a -> a.getAward() == Award.AwardType.STANLEY_CUP);
    }
}

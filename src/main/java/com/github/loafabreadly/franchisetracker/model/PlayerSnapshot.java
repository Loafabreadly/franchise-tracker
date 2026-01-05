package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * PlayerSnapshot captures a player's state at a specific point in time.
 * Used within SeasonSnapshot for historical tracking.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSnapshot {
    private String firstName;
    private String lastName;
    private int overall;
    private int age;
    private PLAYER_POS position;
    private PLAYER_STYLE style;
    private List<PLAYER_XFACTORS> xFactors;
    private PLAYER_POTENTIAL potential;
    private POTENTIAL_ACCURACY potentialAccuracy;
    
    // Contract at time of snapshot
    private double aav;
    private int contractYearsRemaining;
    private CONTRACT_STATUS contractStatus;
    
    // Stats for the snapshot season
    private PlayerSeasonStats seasonStats;
    
    /**
     * Creates a snapshot from a Player at a given season.
     */
    public static PlayerSnapshot fromPlayer(Player player, int season) {
        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.setFirstName(player.getFirstName());
        snapshot.setLastName(player.getLastName());
        snapshot.setOverall(player.getOverall());
        snapshot.setAge(player.getAge());
        snapshot.setPosition(player.getPosition());
        snapshot.setStyle(player.getStyle());
        snapshot.setXFactors(player.getXFactors() != null ? new ArrayList<>(player.getXFactors()) : new ArrayList<>());
        snapshot.setPotential(player.getPotential());
        snapshot.setPotentialAccuracy(player.getPotentialAccuracy());
        
        if (player.getContract() != null) {
            snapshot.setAav(player.getContract().getAav());
            snapshot.setContractYearsRemaining(player.getContract().getTermInYears());
            snapshot.setContractStatus(player.getContract().getStatus());
        }
        
        // Find stats for the given season
        if (player.getCareerStats() != null) {
            snapshot.setSeasonStats(
                player.getCareerStats().stream()
                    .filter(s -> s.getYear() == season)
                    .findFirst()
                    .orElse(null)
            );
        }
        
        return snapshot;
    }
    
    /**
     * Returns the player's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}

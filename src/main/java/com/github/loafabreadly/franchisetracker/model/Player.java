package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Player {
    private String firstName;
    private String lastName;
    private int overall;
    private int age;
    private PLAYER_POS position;
    private PLAYER_STYLE style;
    private List<PLAYER_XFACTORS> xFactors;
    private List<PlayerSeasonStats> careerStats;
    private Contract contract;
    
    // Potential tracking fields
    private PLAYER_POTENTIAL potential;
    private POTENTIAL_ACCURACY potentialAccuracy;
    
    // Draft information
    private int draftYear;
    private int draftRound;
    private int draftOverallPick;
    
    /**
     * Returns the player's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Checks if this player is a goalie.
     */
    public boolean isGoalie() {
        return position == PLAYER_POS.GOALIE;
    }
    
    /**
     * Gets the current contract cap hit, or 0 if no contract.
     */
    public double getCapHit() {
        return contract != null ? contract.getAav() : 0.0;
    }
    
    /**
     * Gets remaining contract years, or 0 if no contract.
     */
    public int getContractYearsRemaining() {
        return contract != null ? contract.getTermInYears() : 0;
    }
}

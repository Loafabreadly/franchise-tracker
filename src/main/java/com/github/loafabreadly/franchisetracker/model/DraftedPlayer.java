package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DraftedPlayer {
    private int year;
    private int round;
    private int pickOverall;
    private Player player;
    private int overallAtDraft;
    private PLAYER_POS position;
    private PLAYER_STYLE style;
    private List<PLAYER_XFACTORS> xFactors;
    private PLAYER_POTENTIAL potentialAtDraft;
    private POTENTIAL_ACCURACY potentialAccuracy;
    private boolean signedToContract;
    private int signedYear;
    private boolean madeNHL;
    private int nhlDebutYear;
    
    /**
     * Gets the display name of the drafted player.
     */
    public String getDisplayName() {
        if (player != null) {
            return player.getFullName();
        }
        return "Unknown";
    }
    
    /**
     * Gets a summary string for this draft pick.
     */
    public String getSummary() {
        String roundStr = switch (round) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> round + "th";
        };
        return String.format("%d %s Round (#%d): %s - %s OVR %d", 
            year, roundStr, pickOverall, getDisplayName(), 
            position != null ? position.name() : "N/A", overallAtDraft);
    }
}

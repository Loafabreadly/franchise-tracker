package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DraftPick {
    private int year;
    private int round;
    private int pickOverall;
    private String originalTeam;
    private String currentOwner;
    private boolean wasTraded;
    private String tradedFrom;
    
    /**
     * Default constructor.
     */
    public DraftPick() {
    }
    
    /**
     * Creates a draft pick with basic info.
     */
    public DraftPick(int year, int round) {
        this.year = year;
        this.round = round;
    }
    
    /**
     * Returns a display string for the pick.
     */
    public String getDisplayString() {
        String roundStr = switch (round) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> round + "th";
        };
        String source = wasTraded && tradedFrom != null ? " (from " + tradedFrom + ")" : "";
        return year + " " + roundStr + " Round Pick" + source;
    }
}

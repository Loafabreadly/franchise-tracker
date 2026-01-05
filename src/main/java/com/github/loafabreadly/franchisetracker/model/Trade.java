package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Trade {
    private String team1Name;
    private String team2Name;
    private int season;
    private Date tradeDate;
    
    // Assets going to team1 (what we received)
    private List<String> team1Receives = new ArrayList<>();
    
    // Assets going to team2 (what we sent)
    private List<String> team2Receives = new ArrayList<>();
    
    // Detailed tracking
    private List<Player> playersReceived = new ArrayList<>();
    private List<Player> playersSent = new ArrayList<>();
    private List<DraftPick> picksReceived = new ArrayList<>();
    private List<DraftPick> picksSent = new ArrayList<>();
    
    private String notes;

    /**
     * Default constructor for JSON deserialization.
     */
    public Trade() {
    }

    public Trade(String team1Name, String team2Name, int season, Date tradeDate) {
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.season = season;
        this.tradeDate = tradeDate;
    }
    
    /**
     * Gets a summary of what was received in the trade.
     */
    public String getReceivedSummary() {
        return String.join(", ", team1Receives);
    }
    
    /**
     * Gets a summary of what was sent in the trade.
     */
    public String getSentSummary() {
        return String.join(", ", team2Receives);
    }
}

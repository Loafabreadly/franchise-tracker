package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class Trade {
    private String team1;
    private String team2;
    private String playerInvolved;
    private String draftPickInvolved;
    private String tradeDate;

    public Trade(String team1, String team2, String playerInvolved, String draftPickInvolved, String tradeDate) {
        this.team1 = team1;
        this.team2 = team2;
        this.playerInvolved = playerInvolved;
        this.draftPickInvolved = draftPickInvolved;
        this.tradeDate = tradeDate;
    }    
}

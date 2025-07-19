package com.github.loafabreadly.franchisetracker.model;

import java.util.Date;

import lombok.Data;

@Data
public class Trade {
    private Team team1;
    private Team team2;
    private Player playerInvolved;
    private String draftPickInvolved;
    private Date tradeDate;

    public Trade(Team team1, Team team2, Player playerInvolved, String draftPickInvolved, Date tradeDate) {
        this.team1 = team1;
        this.team2 = team2;
        this.playerInvolved = playerInvolved;
        this.draftPickInvolved = draftPickInvolved;
        this.tradeDate = tradeDate;
    }    
}

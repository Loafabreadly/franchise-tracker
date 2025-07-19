package com.github.loafabreadly.franchisetracker.model;

import java.util.List;

import lombok.Data;

@Data
public class TeamSeasonStats {
    private int year;
    private int gamesPlayed;
    private int goalsFor;
    private int goalsAgainst;
    private int penaltyMinutes;
    private int wins;
    private int losses;
    private int overtimeLosses;
    private int points;
    private int shutouts;
    private List<Award> awards;
}

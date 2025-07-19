package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class PlayerSeasonStats {
    private int year;
    private int gamesPlayed;
    private int goals;
    private int assists;
    private int points;
    private int plusMinus;
    private int penaltyMinutes;
    private int wins;
    private int losses;
    private int overtimeLosses;
    private int shutouts;
    private int save_percentage;
    private int goalsAgainstAverage;
    // Add more as needed for skaters/goalies
}

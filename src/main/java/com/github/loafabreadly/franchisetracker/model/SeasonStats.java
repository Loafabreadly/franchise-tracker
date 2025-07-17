package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class SeasonStats {
    private int year;
    private int gamesPlayed;
    private int goals;
    private int assists;
    private int points;
    private int plusMinus;
    private int penaltyMinutes;
    private int wins;
    private int losses;
    private int shutouts;
    // Add more as needed for skaters/goalies
}

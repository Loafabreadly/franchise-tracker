package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSeasonStats {
    private int year;
    private LEAGUE_LEVEL leagueLevel = LEAGUE_LEVEL.NHL;
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
    private int overallAtEndOfSeason;
    // Add more as needed for skaters/goalies
}

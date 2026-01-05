package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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

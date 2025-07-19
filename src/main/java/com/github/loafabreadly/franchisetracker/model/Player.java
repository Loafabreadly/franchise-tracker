package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;
import java.util.List;

@Data
public class Player {
    private String firstName;
    private String lastName;
    private int overall;
    private PLAYER_POS position;
    private PLAYER_STYLE style;
    private List<PLAYER_XFACTORS> xFactors;
    private List<PlayerSeasonStats> careerStats;
    private Contract contract;
}

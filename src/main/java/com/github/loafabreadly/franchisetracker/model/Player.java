package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;
import java.util.List;

@Data
public class Player {
    private String name;
    private int overall;
    private String position;
    private String style;
    private List<String> xFactors;
    private List<PlayerSeasonStats> seasonStats;
    private CareerStats careerStats;
    private Contract contract;
}

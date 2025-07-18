package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;
import java.util.List;

@Data
public class Team {
    private String name;
    private int overall;
    private List<Player> roster;
    private List<Award> awards;
    private List<TeamSeasonStats> seasonStats;
    private CareerStats careerStats;
    private Lineup lineup;
    private boolean isAHL;

    public void setIsAHL(boolean isAHL) {
        this.isAHL = isAHL;
    }
}

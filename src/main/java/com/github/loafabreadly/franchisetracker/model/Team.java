package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;
import java.util.List;

@Data
public class Team {
    private String name;
    private int off_overall;
    private int def_overall;
    private int goal_overall;
    private List<Player> roster;
    private List<Award> awards;
    private List<DraftPick> draftPicks;
    private List<TeamSeasonStats> careerStats;
    private Lineup lineup;
    private boolean isAHL;

    public void setIsAHL(boolean isAHL) {
        this.isAHL = isAHL;
    }
}

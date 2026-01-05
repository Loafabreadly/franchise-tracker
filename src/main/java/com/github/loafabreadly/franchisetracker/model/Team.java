package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
    
    @JsonProperty("ahl")
    private boolean isAHL;

    public void setIsAHL(boolean isAHL) {
        this.isAHL = isAHL;
    }
    
    @JsonProperty("ahl")
    public boolean isAHL() {
        return isAHL;
    }
}

package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class DraftPick {
    private int year;
    private int pickOverall;
    private Player player;
    private int overallAtDraft;
    private int overallGrowth;
    private String position;
    private String style;
    private String xFactors;
}

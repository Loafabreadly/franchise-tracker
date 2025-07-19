package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class DraftedPlayer {
    private int year;
    private int pickOverall;
    private Player player;
    private int overallAtDraft;
    private PLAYER_POS position;
    private PLAYER_STYLE style;
    private PLAYER_XFACTORS xFactors;
}

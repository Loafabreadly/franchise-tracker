package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Award {
    public enum AwardType {
        STANLEY_CUP,
        PRESIDENTS_TROPHY,
        HART_TROPHY,
        NORRIS_TROPHY,
        VEZINA_TROPHY,
        CALDER_TROPHY,
        CONN_SMYPHE_TROPHY,
        LADY_BYNG_TROPHY,
        SELKE_TROPHY,
        MASTERSON_TROPHY,
        JACK_ADAMS_AWARD,
        BILL_MASTERSON_TROPHY,
        TED_LINDSEY_AWARD,
        KING_CLANCY_TROPHY
    }
    public enum AwardCategory {
        TEAM,
        PLAYER
    }
    private int year;
    private AwardCategory recipientType; // Player or Team
    private Player player;
    private Team team;
    private AwardType award;
}

package com.github.loafabreadly.franchisetracker.model;

/**
 * Represents the league level for player statistics.
 */
public enum LEAGUE_LEVEL {
    NHL("NHL", "National Hockey League"),
    AHL("AHL", "American Hockey League"),
    ECHL("ECHL", "East Coast Hockey League"),
    CHL("CHL", "Canadian Hockey League"),
    NCAA("NCAA", "NCAA College Hockey"),
    EUROPE("Europe", "European Leagues"),
    OTHER("Other", "Other Leagues");

    private final String displayName;
    private final String description;

    LEAGUE_LEVEL(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

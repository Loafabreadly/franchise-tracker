package com.github.loafabreadly.franchisetracker.model;

/**
 * Represents the potential tier for a player, indicating their projected development ceiling.
 * Separate tiers are provided for skaters and goalies.
 */
public enum PLAYER_POTENTIAL {
    // Skater Potentials
    FRANCHISE("Franchise", "Generational talent, potential for Hart Trophy contention"),
    ELITE("Elite", "All-Star caliber, top-line player"),
    TOP_6F("Top 6 Forward", "Consistent scoring forward, power play contributor"),
    TOP_4D("Top 4 Defenseman", "Reliable two-way defenseman, big minutes"),
    TOP_9F("Top 9 Forward", "Solid middle-six forward"),
    TOP_6D("Top 6 Defenseman", "Depth defenseman with upside"),
    BOTTOM_6F("Bottom 6 Forward", "Checking line / energy player"),
    BOTTOM_PAIR_D("Bottom Pair Defenseman", "Depth defenseman"),
    AHL("AHL", "Career minor leaguer"),
    
    // Goalie Potentials
    FRANCHISE_G("Franchise Goalie", "Vezina-caliber, number one for a decade"),
    ELITE_G("Elite Goalie", "Clear starter, All-Star potential"),
    STARTER("Starter", "Reliable NHL starter"),
    BACKUP("Backup", "Quality NHL backup"),
    AHL_G("AHL Goalie", "Minor league goalie");

    private final String displayName;
    private final String description;

    PLAYER_POTENTIAL(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Returns skater potential tiers only.
     */
    public static PLAYER_POTENTIAL[] skaterPotentials() {
        return new PLAYER_POTENTIAL[] {
            FRANCHISE, ELITE, TOP_6F, TOP_4D, TOP_9F, TOP_6D, BOTTOM_6F, BOTTOM_PAIR_D, AHL
        };
    }

    /**
     * Returns goalie potential tiers only.
     */
    public static PLAYER_POTENTIAL[] goaliePotentials() {
        return new PLAYER_POTENTIAL[] {
            FRANCHISE_G, ELITE_G, STARTER, BACKUP, AHL_G
        };
    }
}

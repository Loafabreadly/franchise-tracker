package com.github.loafabreadly.franchisetracker.model;

/**
 * Represents the accuracy/confidence level of a player's potential rating.
 * In NHL games, scout accuracy varies and potential can be revealed over time.
 */
public enum POTENTIAL_ACCURACY {
    EXACT("Exact", "Potential is known with certainty"),
    HIGH("High", "High confidence in potential rating"),
    MEDIUM("Med", "Moderate confidence in potential rating"),
    LOW("Low", "Low confidence, potential could vary significantly");

    private final String displayName;
    private final String description;

    POTENTIAL_ACCURACY(String displayName, String description) {
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

package com.github.loafabreadly.franchisetracker.service;

import com.github.loafabreadly.franchisetracker.FranchiseTracker;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

/**
 * FranchiseDataService provides static methods for saving and loading FranchiseTracker data to and from disk using JSON serialization.
 */
public class FranchiseDataService {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Saves the given FranchiseTracker instance to a file as JSON using the .nhl file extension.
     * @param trackerToSave The FranchiseTracker to save
     * @param filePath The file path to save to
     * @throws IOException if writing to the file fails
     */
    public static void saveTeams(FranchiseTracker trackerToSave, String filePath) throws IOException {
        mapper.writeValue(new File(filePath), trackerToSave);
    }

    /**
     * Loads a FranchiseTracker instance from a JSON file using the .nhl file extension.
     * @param filePath The file path to load from
     * @return The loaded FranchiseTracker instance
     * @throws IOException if reading from the file fails
     */
    public static FranchiseTracker loadTracker(String filePath) throws IOException {
        return mapper.readValue(new File(filePath), FranchiseTracker.class);
    }
}

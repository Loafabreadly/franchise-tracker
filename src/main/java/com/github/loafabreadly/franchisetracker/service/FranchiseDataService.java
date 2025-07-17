package com.github.loafabreadly.franchisetracker.service;

import com.github.loafabreadly.franchisetracker.model.Team;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FranchiseDataService {
    private static final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void saveTeams(List<Team> teams, String filePath) throws IOException {
        mapper.writeValue(new File(filePath), teams);
    }

    public static List<Team> loadTeams(String filePath) throws IOException {
        return mapper.readValue(new File(filePath), mapper.getTypeFactory().constructCollectionType(List.class, Team.class));
    }
}

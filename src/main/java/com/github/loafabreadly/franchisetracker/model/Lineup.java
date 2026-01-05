package com.github.loafabreadly.franchisetracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Lineup {
    private List<Player> forwards;
    private List<Player> defensemen;
    private List<Player> goalies;
}

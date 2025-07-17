package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;
import java.util.List;

@Data
public class Lineup {
    private List<Player> forwards;
    private List<Player> defensemen;
    private List<Player> goalies;
}

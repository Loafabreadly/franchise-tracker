package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class Contract {
    private double aav;
    private int term;
    private String clauses;
    private String status; // RFA/UFA
}

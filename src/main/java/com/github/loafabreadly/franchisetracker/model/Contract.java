package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class Contract {
    private double aav;
    private int termInYears;
    private CONTRACT_CLAUSE clauses;
    private CONTRACT_STATUS status; // RFA/UFA
}

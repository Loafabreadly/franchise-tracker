package com.github.loafabreadly.franchisetracker.model;

import lombok.Data;

@Data
public class Award {
    private String name;
    private int year;
    private String recipientType; // Player or Team
    private String recipientName;
}

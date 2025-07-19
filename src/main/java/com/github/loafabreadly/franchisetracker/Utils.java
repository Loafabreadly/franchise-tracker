package com.github.loafabreadly.franchisetracker;

public class Utils {

        public static String validateSaveName(String fileName) {
        if (fileName.endsWith(".nhl")) {
            return fileName;
        }
        else {
            return fileName + ".nhl"; // Append .json if not present
        }
    }
}

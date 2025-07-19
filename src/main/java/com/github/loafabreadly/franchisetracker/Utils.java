package com.github.loafabreadly.franchisetracker;

public class Utils {

        public static String validateSaveName(String fileName) {
        if (fileName.endsWith(".json")) {
            return fileName;
        }
        else {
            return fileName + ".json"; // Append .json if not present
        }
    }
}

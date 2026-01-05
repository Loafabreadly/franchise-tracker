package com.github.loafabreadly.franchisetracker;

import com.mitchtalmadge.asciidata.graph.ASCIIGraph;

import java.util.List;

/**
 * ChartComponents provides utility methods for creating ASCII charts and graphs
 * for use in Lanterna TUI displays.
 */
public class ChartComponents {

    /**
     * Creates a line graph from a series of data points.
     * @param data The data points to plot
     * @param height The height of the graph in lines
     * @return String representation of the line graph
     */
    public static String createLineGraph(double[] data, int height) {
        if (data == null || data.length == 0) {
            return "No data available";
        }
        try {
            return ASCIIGraph.fromSeries(data)
                .withNumRows(height)
                .plot();
        } catch (Exception e) {
            return "Unable to generate graph: " + e.getMessage();
        }
    }

    /**
     * Creates a line graph from a list of integers.
     * @param data The data points to plot
     * @param height The height of the graph in lines
     * @return String representation of the line graph
     */
    public static String createLineGraph(List<Integer> data, int height) {
        if (data == null || data.isEmpty()) {
            return "No data available";
        }
        double[] doubleData = data.stream().mapToDouble(Integer::doubleValue).toArray();
        return createLineGraph(doubleData, height);
    }

    /**
     * Creates a simple horizontal bar chart.
     * @param labels The labels for each bar
     * @param values The values for each bar
     * @param maxWidth The maximum width of the bars
     * @return String representation of the bar chart
     */
    public static String createHorizontalBarChart(String[] labels, double[] values, int maxWidth) {
        if (labels == null || values == null || labels.length == 0 || values.length == 0) {
            return "No data available";
        }

        StringBuilder sb = new StringBuilder();
        double maxValue = 0;
        int maxLabelLen = 0;

        for (double v : values) {
            maxValue = Math.max(maxValue, v);
        }
        for (String label : labels) {
            maxLabelLen = Math.max(maxLabelLen, label.length());
        }

        for (int i = 0; i < labels.length && i < values.length; i++) {
            int barLen = maxValue > 0 ? (int) ((values[i] / maxValue) * maxWidth) : 0;
            String bar = "█".repeat(Math.max(0, barLen));
            sb.append(String.format("%" + maxLabelLen + "s │%s %.1f\n", labels[i], bar, values[i]));
        }

        return sb.toString();
    }

    /**
     * Creates a sparkline (compact inline chart) for a data series.
     * @param data The data points
     * @return A single-line sparkline representation
     */
    public static String createSparkline(double[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        char[] sparkChars = {'▁', '▂', '▃', '▄', '▅', '▆', '▇', '█'};
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (double v : data) {
            min = Math.min(min, v);
            max = Math.max(max, v);
        }

        StringBuilder sb = new StringBuilder();
        double range = max - min;

        for (double v : data) {
            int index = range > 0 ? (int) (((v - min) / range) * 7) : 0;
            index = Math.max(0, Math.min(7, index));
            sb.append(sparkChars[index]);
        }

        return sb.toString();
    }

    /**
     * Creates a sparkline from a list of integers.
     * @param data The data points
     * @return A single-line sparkline representation
     */
    public static String createSparkline(List<Integer> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        double[] doubleData = data.stream().mapToDouble(Integer::doubleValue).toArray();
        return createSparkline(doubleData);
    }

    /**
     * Creates a simple progress bar.
     * @param current The current value
     * @param max The maximum value
     * @param width The width of the progress bar
     * @return String representation of the progress bar
     */
    public static String createProgressBar(double current, double max, int width) {
        if (max <= 0) {
            return "[" + "░".repeat(width) + "] 0%";
        }
        double ratio = Math.min(1.0, current / max);
        int filled = (int) (ratio * width);
        int empty = width - filled;
        int percent = (int) (ratio * 100);
        return "[" + "█".repeat(filled) + "░".repeat(empty) + "] " + percent + "%";
    }

    /**
     * Creates an age distribution histogram.
     * @param ages Array of ages
     * @param bucketSize Size of each age bucket
     * @return String representation of the histogram
     */
    public static String createAgeHistogram(int[] ages, int bucketSize) {
        if (ages == null || ages.length == 0) {
            return "No data available";
        }

        int minAge = Integer.MAX_VALUE;
        int maxAge = Integer.MIN_VALUE;
        for (int age : ages) {
            minAge = Math.min(minAge, age);
            maxAge = Math.max(maxAge, age);
        }

        int numBuckets = ((maxAge - minAge) / bucketSize) + 1;
        int[] buckets = new int[numBuckets];
        
        for (int age : ages) {
            int bucketIndex = (age - minAge) / bucketSize;
            buckets[bucketIndex]++;
        }

        int maxCount = 0;
        for (int count : buckets) {
            maxCount = Math.max(maxCount, count);
        }

        StringBuilder sb = new StringBuilder();
        int barWidth = 20;

        for (int i = 0; i < numBuckets; i++) {
            int rangeStart = minAge + (i * bucketSize);
            int rangeEnd = rangeStart + bucketSize - 1;
            int barLen = maxCount > 0 ? (buckets[i] * barWidth) / maxCount : 0;
            String bar = "█".repeat(barLen);
            sb.append(String.format("%2d-%2d │%s %d\n", rangeStart, rangeEnd, bar, buckets[i]));
        }

        return sb.toString();
    }
}

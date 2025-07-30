package com.uniba.mining.sdmetrics;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SdMetricsReportFormatter {

    public static class MetricSummary {
        public String name;
        public String label;
        public int n;
        public double sum, mean, stddev, min, max;

        public MetricSummary(String name, String label, int n, double sum, double mean, double stddev, double min, double max) {
            this.name = name;
            this.label = label;
            this.n = n;
            this.sum = sum;
            this.mean = mean;
            this.stddev = stddev;
            this.min = min;
            this.max = max;
        }
    }

    public static class ElementMetricRow {
        public String elementName;
        public Map<String, Double> metrics = new LinkedHashMap<>();

        public ElementMetricRow(String elementName) {
            this.elementName = elementName;
        }
    }

    public static List<MetricSummary> parseSummaryCsv(Path csvFilePath, Map<String, String> labelMap) throws IOException {
        List<MetricSummary> summaries = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
            String header = reader.readLine();
            if (header == null || !header.startsWith("Name,")) return summaries;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length >= 13) {
                    String name = tokens[0].trim();
                    int n = Integer.parseInt(tokens[2].trim());
                    double sum = Double.parseDouble(tokens[1].trim());
                    double mean = Double.parseDouble(tokens[3].trim());
                    double stddev = Double.parseDouble(tokens[4].trim());
                    double max = Double.parseDouble(tokens[5].trim());
                    double min = Double.parseDouble(tokens[12].trim());
                    String label = labelMap.getOrDefault(name, name);
                    summaries.add(new MetricSummary(name, label, n, sum, mean, stddev, min, max));
                }
            }
        }
        return summaries;
    }

    public static List<ElementMetricRow> parseElementLevelCsv(Path csvFilePath) throws IOException {
        List<ElementMetricRow> rows = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFilePath)) {
            String header = reader.readLine();
            if (header == null) return rows;
            String[] metricNames = header.split(",");
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length < 2) continue;
                String elementName = tokens[0].trim();
                ElementMetricRow row = new ElementMetricRow(elementName);
                for (int i = 1; i < tokens.length && i < metricNames.length; i++) {
                    try {
                        double val = Double.parseDouble(tokens[i].trim());
                        row.metrics.put(metricNames[i].trim(), val);
                    } catch (NumberFormatException e) {
                        row.metrics.put(metricNames[i].trim(), 0.0);
                    }
                }
                rows.add(row);
            }
        }
        return rows;
    }

    public static String formatReport(String diagramType, List<MetricSummary> summaries, List<ElementMetricRow> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("\uD83D\uDCCA ").append(diagramType).append(" Metrics Summary (").append(!rows.isEmpty() ? rows.size() : "N/A").append(" elements)\n\n");

        for (MetricSummary s : summaries) {
            if (s.max == 0 && s.mean == 0) {
                sb.append("- ").append(s.label).append(" (" + s.name + "): Always 0\n");
            } else {
                sb.append(String.format("- %s (%s): Mean = %.2f, Max = %.0f, StdDev = %.2f\n",
                        s.label, s.name, s.mean, s.max, s.stddev));
            }
        }

        if (!rows.isEmpty()) {
            sb.append("\n\uD83D\uDD0D Per-Element Breakdown\n\n");

            List<String> metricKeys = summaries.stream()
            	    .map(s -> s.name)
            	    .collect(Collectors.toList());

            sb.append(String.format("| %-30s |", "Element"));
            for (String m : metricKeys) {
                sb.append(String.format(" %-8s |", m));
            }
            sb.append("\n").append("-".repeat(34 + 11 * metricKeys.size())).append("\n");

            for (ElementMetricRow row : rows) {
                sb.append(String.format("| %-30s |", row.elementName));
                for (String m : metricKeys) {
                    sb.append(String.format(" %-8.0f |", row.metrics.getOrDefault(m, 0.0)));
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
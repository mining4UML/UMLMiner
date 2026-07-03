package com.uniba.mining.processfeedback.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.uniba.mining.processfeedback.core.ProcessViolation;

public class RumCsvViolationParser {

    public List<ProcessViolation> parse(File csvFile)
            throws IOException {

        List<ProcessViolation> violations =
                new ArrayList<>();

        try (BufferedReader reader =
                     new BufferedReader(
                             new FileReader(csvFile))) {

            String header = reader.readLine();

            if (header == null) {
                return violations;
            }

            String line;

            while ((line = reader.readLine()) != null) {

                String[] cols = splitCsv(line);

                if (cols.length < 15) {
                    continue;
                }

                ProcessViolation violation =
                        buildViolation(cols);

                violations.add(violation);
            }
        }

        return violations;
    }

    private ProcessViolation buildViolation(
            String[] cols) {

        ProcessViolation v =
                new ProcessViolation();

        // 0 = Trace (non usato per ora)

        v.setConstraint(get(cols, 1));
        v.setActivities(get(cols, 2));
        v.setResultType(get(cols, 3));
        v.setActivityName(get(cols, 4));
        v.setActivityIndex(get(cols, 5));

        // 6 = DiagramName
        // 7 = DiagramType

        v.setElementType(get(cols, 8));
        v.setElementName(get(cols, 9));

        v.setPropertyName(get(cols, 10));
        v.setPropertyValue(get(cols, 11));

        v.setRelationshipFrom(get(cols, 12));
        v.setRelationshipTo(get(cols, 13));

        v.setElementId(get(cols, 14));

        if (cols.length > 15) {
            v.setDescription(get(cols, 15));
        }

        return v;
    }

    private String get(
            String[] cols,
            int index) {

        if (index >= cols.length) {
            return "";
        }

        String value = cols[index];

        if (value == null) {
            return "";
        }

        return value
                .replace("\"", "")
                .trim();
    }

    /**
     * Gestisce correttamente le virgole
     * contenute dentro campi quotati.
     */
    private String[] splitCsv(
            String line) {

        List<String> values =
                new ArrayList<>();

        StringBuilder current =
                new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {

                insideQuotes =
                        !insideQuotes;
            }
            else if (c == ',' &&
                    !insideQuotes) {

                values.add(
                        current.toString());

                current.setLength(0);
            }
            else {

                current.append(c);
            }
        }

        values.add(current.toString());

        return values.toArray(
                new String[0]);
    }
}
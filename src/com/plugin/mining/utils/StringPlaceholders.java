package com.plugin.mining.utils;

public class StringPlaceholders {

    public static class Placeholder {
        private String name;
        private String value;

        public Placeholder(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    private StringPlaceholders() {
        // Empty
    }

    public static String setPlaceholders(String string, Placeholder... placeholders) {
        String stringWithPlaceholders = string;
        for (Placeholder placeholder : placeholders) {
            if (placeholder.getValue() != null)
                stringWithPlaceholders = stringWithPlaceholders.replace(String.format("{{%s}}", placeholder.getName()),
                        placeholder.getValue());
        }
        return stringWithPlaceholders;
    }
}

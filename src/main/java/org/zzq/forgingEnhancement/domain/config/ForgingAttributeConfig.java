package org.zzq.forgingEnhancement.domain.config;

import java.util.Map;

public class ForgingAttributeConfig {
    private String key;
    private String name;
    private boolean rare;
    private String operation;
    private Map<String, ValueRange> values;

    public ForgingAttributeConfig(String key, String name, boolean rare, String operation, Map<String, ValueRange> values) {
        this.key = key;
        this.name = name;
        this.rare = rare;
        this.operation = operation;
        this.values = values;
    }

    public static class ValueRange{
        private final String rarity;
        private final double min;
        private final double max;

        public ValueRange(String rarity, double min, double max) {
            this.rarity = rarity;
            this.min = min;
            this.max = max;
        }

        public String getRarity() {
            return rarity;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public boolean isRare() {
        return rare;
    }

    public String getOperation() {
        return operation;
    }

    public Map<String, ValueRange> getValues() {
        return values;
    }
}

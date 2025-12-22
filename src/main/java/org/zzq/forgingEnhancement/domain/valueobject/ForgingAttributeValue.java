package org.zzq.forgingEnhancement.domain.valueobject;

import org.zzq.forgingEnhancement.utils.RandomUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class ForgingAttributeValue {
    private String key;
    private String name;
    private boolean rare;
    private String operation;
    private Map<String, ValueRange> values;

    public ForgingAttributeValue(String key, String name, boolean rare, String operation, Map<String, ValueRange> values) {
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

        public double getRandomValue(){
            double randomValue = min + (RandomUtil.nextDouble() * (max - min));
            BigDecimal bigDecimal = new BigDecimal(Double.toString(randomValue));
            bigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);
            return bigDecimal.doubleValue();
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

    public ValueRange getValueRangeByLevel(int level){
        return switch (level) {
            case 0 -> values.get("BROKEN");
            case 1 -> values.get("COMMON");
            case 2 -> values.get("UNCOMMON");
            case 3 -> values.get("EPIC");
            case 4 -> values.get("LEGENDARY");
            case 5 -> values.get("MYTHIC");
            default -> values.get("COMMON");
        };
    }
}

package org.zzq.forgingEnhancement.models;

public class Attribute {
    private String name;
    private int level;
    private double value;

    public Attribute(String name, int level, double value) {
        this.name = name;
        this.level = level;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }
}

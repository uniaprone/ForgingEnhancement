package org.zzq.forgingEnhancement.models;

public class ForgingAttribute {
    private String name;
    private int level;
    private double value;
    private boolean isEngraved;

    public ForgingAttribute(String name, int level, double value) {
        this.name = name;
        this.level = level;
        this.value = value;
    }

    public ForgingAttribute(String name, int level, double value, boolean isEngraved) {
        this.name = name;
        this.level = level;
        this.value = value;
        this.isEngraved = isEngraved;
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

    public boolean isEngraved() {
        return isEngraved;
    }

    public void setEngraved(boolean engraved) {
        isEngraved = engraved;
    }
}

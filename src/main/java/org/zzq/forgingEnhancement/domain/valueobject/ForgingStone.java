package org.zzq.forgingEnhancement.domain.valueobject;

public enum ForgingStone {
    BROKEN(0, "破损", "§8"),
    COMMON(1, "普通", "§f"),
    UNCOMMON(2, "优秀", "§a"),
    EPIC(3, "史诗", "§5"),
    LEGENDARY(4, "传说", "§6"),
    MYTHIC(5, "神话", "§d");

    private final int level;
    private final String quality;
    private final String color;

    private ForgingStone(int level, String quality, String color) {
        this.level = level;
        this.quality = quality;
        this.color = color;
    }

    public int getLevel() {
        return level;
    }

    public String getQuality() {
        return quality;
    }

    public String getColor() {
        return color;
    }

    public static boolean isMatchForgingStone(String quality){
        for (ForgingStone forgingStone: ForgingStone.values()){
            if(forgingStone.name().equalsIgnoreCase(quality)){
                return true;
            }
        }
        return false;
    }
}

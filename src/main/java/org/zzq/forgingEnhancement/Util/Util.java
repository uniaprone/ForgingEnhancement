package org.zzq.forgingEnhancement.Util;

public class Util {
    public static String getEquipmentType(String name) {
        name = name.toLowerCase();
        if (name.endsWith("_helmet")) return "helmet";
        if (name.endsWith("_chestplate")) return "chestplate";
        if (name.endsWith("_leggings")) return "leggings";
        if (name.endsWith("_boots")) return "boots";
        if (name.endsWith("_sword")) return "sword";
        if (name.endsWith("_axe")) return "axe";
        if (name.endsWith("_pickaxe")) return "pickaxe";
        if (name.endsWith("_shovel")) return "shovel";
        if (name.endsWith("_hoe")) return "hoe";
        return null;
    }
}

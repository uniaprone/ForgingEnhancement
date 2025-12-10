package org.zzq.forgingEnhancement.utils;

import net.kyori.adventure.text.format.NamedTextColor;

public class ColorUtil {
    public static NamedTextColor getColorByLevel(int level){
        return switch (level) {
            case 0 -> NamedTextColor.DARK_GRAY;    // 深灰
            case 1 -> NamedTextColor.WHITE;   // 白色
            case 2 -> NamedTextColor.GREEN; // 绿色
            case 3 -> NamedTextColor.DARK_PURPLE;      // 紫色
            case 4 -> NamedTextColor.GOLD; // 金色
            case 5 -> NamedTextColor.LIGHT_PURPLE;    // 粉色
            default -> NamedTextColor.WHITE;
        };
    }
}

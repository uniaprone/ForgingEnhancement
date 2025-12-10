package org.zzq.forgingEnhancement.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {

    public static void playEngraveSound(Player player){
        if(player == null) return;
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }

    public static void playForgingSound(Player player){
        if(player == null) return;
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
    }
}

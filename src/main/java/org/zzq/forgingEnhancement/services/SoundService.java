package org.zzq.forgingEnhancement.services;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundService {

    public void playEngraveSound(Player player){
        if(player == null) return;
        player.playSound(player.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }

    public void playForgingSound(Player player){
        if(player == null) return;
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 1.0f);
    }
}

package org.zzq.forgingEnhancement.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.ForgingEnhancement;
import org.zzq.forgingEnhancement.managers.BaseAttributeManager;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.StoneManager;

import java.util.*;

public class AnvilForgingListener implements Listener {
    private final ConfigManager configManager;
    private final StoneManager stoneManager;
    
    public AnvilForgingListener(ConfigManager configManager, StoneManager stoneManager) {
        this.configManager = configManager;
        this.stoneManager = stoneManager;
    }

    // 修改onPrepareAnvil方法中的锻造石识别部分
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        if (firstItem != null && secondItem != null && stoneManager.isForgingStone(secondItem)) {
            String firstItemType = configManager.getEquipmentType(firstItem.getType());
            if (configManager.isEnhanceableEquipment(firstItemType)) {
                ItemStack result = firstItem.clone();

                event.setResult(result);
                event.getView().setRepairCost(0);
            }
        }
    }
}

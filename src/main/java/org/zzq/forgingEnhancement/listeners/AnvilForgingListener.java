package org.zzq.forgingEnhancement.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.services.ItemDisplayService;

public class AnvilForgingListener implements Listener {
    private final ConfigManager configManager;
    private final StoneManager stoneManager;
    private ItemDisplayService itemDisplayService;
    
    public AnvilForgingListener(ConfigManager configManager, StoneManager stoneManager, ItemDisplayService itemDisplayService) {
        this.configManager = configManager;
        this.stoneManager = stoneManager;
        this.itemDisplayService = itemDisplayService;
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
                ItemMeta resultItemMeta = result.getItemMeta();
                itemDisplayService.updatePrepareAnvilDisplay(resultItemMeta, firstItem.getItemMeta(), stoneManager.getStoneQualityLevel(secondItem));
                result.setItemMeta(resultItemMeta);
                event.setResult(result);
                event.getView().setRepairCost(0);
            }
        }
    }
}

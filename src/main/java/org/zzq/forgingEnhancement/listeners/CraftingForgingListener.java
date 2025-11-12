package org.zzq.forgingEnhancement.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.ForgingEnhancement;
import org.zzq.forgingEnhancement.managers.BaseAttributeManager;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.services.ItemDisplayService;

import java.util.*;
import java.util.logging.Logger;

public class CraftingForgingListener implements Listener {
    private final ConfigManager configManager;
    private ItemDisplayService itemDisplayService;
    private Logger logger;

    public CraftingForgingListener(ConfigManager configManager, StoneManager stoneManager, ItemDisplayService itemDisplayService,Logger logger) {
        this.configManager = configManager;
        this.itemDisplayService = itemDisplayService;
        this.logger = logger;
    }

    // 修改onPrepareAnvil方法中的锻造石识别部分
    @EventHandler
    public void onWorkbenchCraft(PrepareItemCraftEvent event){
        CraftingInventory craftingInventory =  event.getInventory();
        ItemStack resultItem = craftingInventory.getResult();
        if(resultItem != null){
            String firstItemType = configManager.getEquipmentType(resultItem.getType());
            if (configManager.isEnhanceableEquipment(firstItemType)) {
                ItemStack result = resultItem.clone();
                ItemMeta resultItemMeta = result.getItemMeta();
                itemDisplayService.updatePrepareAnvilDisplay(resultItemMeta, resultItem.getItemMeta(), 1);
                result.setItemMeta(resultItemMeta);
                craftingInventory.setResult(result);
            }
        }
    }
}


package org.zzq.forgingEnhancement.application;

import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftItemService;

public class AnvilPreForgingService {
    private ForgingAttributePoolConfig forgingAttributePoolConfig;
    private MinecraftItemService minecraftItemService;

    public AnvilPreForgingService(ForgingAttributePoolConfig forgingAttributePoolConfig, MinecraftItemService minecraftItemService) {
        this.forgingAttributePoolConfig = forgingAttributePoolConfig;
        this.minecraftItemService = minecraftItemService;
    }

    public ItemStack anvilPreForging(ItemStack forgingItem, ItemStack forgingStone){
        if(forgingItem == null || forgingStone == null) return null;
        if(!forgingAttributePoolConfig.isForgeableEquipment(String.valueOf(forgingItem.getType()))) return null;
        if(!minecraftItemService.isForgingStone(forgingStone)) return null;

        ItemStack resultItem = forgingItem.clone();
        int stoneLevel = minecraftItemService.getStoneLevel(forgingStone);
        minecraftItemService.addPreForgingLore(resultItem, stoneLevel);
        return resultItem;
    }
}

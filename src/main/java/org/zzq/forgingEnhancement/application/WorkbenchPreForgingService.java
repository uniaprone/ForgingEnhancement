package org.zzq.forgingEnhancement.application;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.domain.aggregateroot.PlayerSettingConfig;
import org.zzq.forgingEnhancement.domain.valueobject.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.infrastructure.ForgingLogger;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftItemService;

public class WorkbenchPreForgingService {
    private PlayerSettingConfig playerSettingConfig;
    private ForgingAttributePoolConfig forgingAttributePoolConfig;
    private MinecraftItemService minecraftItemService;
    private ForgingLogger logger;

    public WorkbenchPreForgingService(PlayerSettingConfig playerSettingConfig, ForgingAttributePoolConfig forgingAttributePoolConfig, MinecraftItemService minecraftItemService, ForgingLogger logger) {
        this.playerSettingConfig = playerSettingConfig;
        this.forgingAttributePoolConfig = forgingAttributePoolConfig;
        this.minecraftItemService = minecraftItemService;
        this.logger = logger;
    }

    public void workbenchPreForging(Player player, ItemStack itemStack){
        if(itemStack == null) return;
        if(!playerSettingConfig.isWorkbenchForgingEnable(String.valueOf(player.getUniqueId()))) return;
        if(!forgingAttributePoolConfig.isForgeableEquipment(String.valueOf(itemStack.getType()))) return;
        minecraftItemService.addPreForgingLore(itemStack, 1);
    }
}

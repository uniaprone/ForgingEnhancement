package org.zzq.forgingEnhancement.listeners;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.application.WorkbenchPreForgingService;
import org.zzq.forgingEnhancement.infrastructure.ForgingLogger;

import java.util.*;

public class WorkbenchPreForgingListener implements Listener {
    private WorkbenchPreForgingService workbenchPreForgingService;
    private ForgingLogger logger;

    public WorkbenchPreForgingListener(WorkbenchPreForgingService workbenchPreForgingService, ForgingLogger logger) {
        this.workbenchPreForgingService = workbenchPreForgingService;
        this.logger = logger;
    }

    // 修改onPrepareAnvil方法中的锻造石识别部分
    @EventHandler
    public void onWorkbenchCraft(PrepareItemCraftEvent event) {
        List<HumanEntity> viewers = event.getViewers();
        if (viewers.isEmpty()) return;
        for (HumanEntity player : viewers) {
            CraftingInventory craftingInventory = event.getInventory();
            ItemStack resultItem = craftingInventory.getResult();

            workbenchPreForgingService.workbenchPreForging((Player) player, resultItem);
            craftingInventory.setResult(resultItem);
        }
    }
}


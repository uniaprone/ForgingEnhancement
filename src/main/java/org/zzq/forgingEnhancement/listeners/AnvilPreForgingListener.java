package org.zzq.forgingEnhancement.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.application.forging.AnvilPreForgingService;

public class AnvilPreForgingListener implements Listener {
    private AnvilPreForgingService anvilPreForgingService;

    public AnvilPreForgingListener(AnvilPreForgingService anvilPreForgingService) {
        this.anvilPreForgingService = anvilPreForgingService;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        ItemStack resultItem = anvilPreForgingService.anvilPreForging(firstItem, secondItem);
        if(resultItem == null) return;
        event.setResult(resultItem);
        event.getView().setRepairCost(0);

    }
}

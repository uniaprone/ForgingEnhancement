package org.zzq.forgingEnhancement.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.application.AnvilPreForgingService;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.utils.ColorUtil;
import org.zzq.forgingEnhancement.utils.RegxUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        event.setResult(resultItem);
        event.getView().setRepairCost(0);

    }
}

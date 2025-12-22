package org.zzq.forgingEnhancement.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.application.AnvilForgingService;

import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIService;

public class AnvilForgingListener implements Listener {
    private final AnvilForgingService anvilForgingService;
    public AnvilForgingListener(AnvilForgingService anvilForgingService){
        this.anvilForgingService = anvilForgingService;
    }

    @EventHandler
    public void onAnvilForging(InventoryClickEvent event) {
        if(!(event.getInventory().getType() == InventoryType.ANVIL && event.getRawSlot() == 2)) return;
        if(!(event.getWhoClicked() instanceof Player player)) return;

        // 获取铁砧中的物品
        AnvilInventory anvil = (AnvilInventory) event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();
        ItemStack resultItem = event.getCurrentItem();
        if (firstItem == null || secondItem == null || resultItem == null) {
            return;
        }
        ItemStack resultForgingItem = anvilForgingService.forgeItem(player, resultItem, secondItem);
        if(resultForgingItem == null) return;
        event.setCancelled(true); // 取消默认的点击行为
        anvil.setFirstItem(null);
        anvil.setSecondItem(consumeItem(secondItem));
        // 手动设置光标物品
        event.getWhoClicked().setItemOnCursor(resultForgingItem);
        // 更新铁砧结果槽为空
        anvil.setResult(null);
    }
    // 消耗物品（减少数量）
    private ItemStack consumeItem(ItemStack item) {
        if (item.getAmount() > 1) {
            ItemStack consumed = item.clone();
            consumed.setAmount(item.getAmount() - 1);
            return consumed;
        }
        return null;
    }
}


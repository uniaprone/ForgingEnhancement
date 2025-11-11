package org.zzq.forgingEnhancement.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.services.*;
import org.zzq.forgingEnhancement.models.EnhancementResult;

public class AnvilClickListener implements Listener {
    private final ForgingService forgingService;
    public AnvilClickListener(ForgingService forgingService){
        this.forgingService = forgingService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }
        // 只处理结果槽的点击（槽位2）
        if (event.getRawSlot() != 2) {
            return;
        }

        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null) {
            return;
        }

        event.setCancelled(true); // 取消默认的点击行为

        // 获取铁砧中的物品
        AnvilInventory anvil = (AnvilInventory) event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        ItemStack resultForgingItem = forgingService.enhanceItem(firstItem, secondItem, resultItem);

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


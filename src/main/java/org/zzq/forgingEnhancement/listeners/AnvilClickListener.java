package org.zzq.forgingEnhancement.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.services.*;
import org.zzq.forgingEnhancement.models.EnhancementResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnvilClickListener implements Listener {
    private final ForgingService forgingService;
    public AnvilClickListener(ForgingService forgingService){
        this.forgingService = forgingService;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryType inventoryType = event.getInventory().getType();
        if (inventoryType != InventoryType.ANVIL && inventoryType != InventoryType.WORKBENCH) {
            return;
        }

        if(inventoryType == InventoryType.ANVIL && event.getRawSlot() == 2){
            // 获取铁砧中的物品
            AnvilInventory anvil = (AnvilInventory) event.getInventory();
            ItemStack firstItem = anvil.getFirstItem();
            ItemStack secondItem = anvil.getSecondItem();
            ItemStack resultItem = event.getCurrentItem();
            if (firstItem == null || secondItem == null || resultItem == null || !forgingService.isForgingStone(secondItem)) {
                return;
            }
            event.setCancelled(true); // 取消默认的点击行为

            ItemStack resultForgingItem = forgingService.enhanceItem(resultItem, secondItem);

            anvil.setFirstItem(null);
            anvil.setSecondItem(consumeItem(secondItem));
            // 手动设置光标物品
            event.getWhoClicked().setItemOnCursor(resultForgingItem);
            // 更新铁砧结果槽为空
            anvil.setResult(null);
        }

        if(inventoryType == InventoryType.WORKBENCH && event.getRawSlot() == 0){
            CraftingInventory craftingInventory =  (CraftingInventory) event.getInventory();
            ItemStack resultItem = craftingInventory.getResult();
            if(resultItem == null) return;
            ItemStack[] matrix = craftingInventory.getMatrix();
            ItemStack  firstItem = resultItem.clone();
            ItemStack secondItem = forgingService.createForgingStone("COMMON", 1);
            // 首先移除预添加的lora
            ItemMeta resultItemMeta = resultItem.getItemMeta();
            if(resultItemMeta != null && resultItemMeta.hasLore()){
                List<String> originalLore = resultItemMeta.getLore();
                if (originalLore == null) originalLore = new ArrayList<>();
                Iterator<String> iterator =  originalLore.iterator();
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    if(line.contains("品质") && line.contains("???")){
                        iterator.remove();
                    }
                }
                resultItemMeta.setLore(originalLore);
                firstItem.setItemMeta(resultItemMeta);
                resultItem.setItemMeta(resultItemMeta);
            }
            ItemStack resultForgingItem = forgingService.enhanceItem(resultItem, secondItem);

            event.setCancelled(true); // 取消默认的点击行为
            // 消耗合成网格中的物品
            consumeMatrixItems(craftingInventory, matrix);

            // 将锻造结果设置到玩家光标
            event.getWhoClicked().setItemOnCursor(resultForgingItem);

            // 清空结果槽
            craftingInventory.setResult(null);
        }
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

    /**
     * 消耗合成网格中的物品
     */
    private void consumeMatrixItems(CraftingInventory craftingInventory, ItemStack[] matrix) {
        // 创建新的矩阵副本用于更新
        ItemStack[] newMatrix = matrix.clone();
        forgingService.getLogger().info(newMatrix.length + "newMatrix.length");
        // 遍历所有槽位，找到第一个有物品的槽位并消耗
        for (int i = 0; i < newMatrix.length; i++) {
            ItemStack item = newMatrix[i];
            if (item != null && !item.getType().isAir()) {
                if (item.getAmount() > 1) {
                    // 减少数量
                    item.setAmount(item.getAmount() - 1);
                } else {
                    // 完全移除
                    newMatrix[i] = null;
                }
            }
        }

        // 更新合成网格
        craftingInventory.setMatrix(newMatrix);
    }
}


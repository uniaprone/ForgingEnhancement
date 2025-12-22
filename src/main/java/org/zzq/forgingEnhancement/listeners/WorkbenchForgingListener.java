package org.zzq.forgingEnhancement.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.application.WorkbenchForgingService;


public class WorkbenchForgingListener implements Listener {
    private WorkbenchForgingService workbenchForgingService;

    public WorkbenchForgingListener(WorkbenchForgingService workbenchForgingService) {
        this.workbenchForgingService = workbenchForgingService;
    }

    @EventHandler
    public void onWorkbenchClick(InventoryClickEvent event){
        if(event.getInventory().getType() != InventoryType.WORKBENCH) return;
        if(event.getRawSlot() != 0) return;

        Player player = (Player) event.getWhoClicked();
        CraftingInventory craftingInventory =  (CraftingInventory) event.getInventory();

        ItemStack resultItem = craftingInventory.getResult();
        if(resultItem == null) return;

        ItemStack forgingResultItem = workbenchForgingService.forgeItem(player, resultItem.clone());
        if(forgingResultItem == null) return;

        event.setCancelled(true); // 取消默认的点击行为

        // 消耗合成网格中的物品
        ItemStack[] matrix = craftingInventory.getMatrix();
        consumeMatrixItems(craftingInventory, matrix);
        // 将锻造结果设置到玩家光标
        event.getWhoClicked().setItemOnCursor(forgingResultItem);
        // 清空结果槽
        craftingInventory.setResult(null);
    }
    /**
     * 消耗合成网格中的物品
     */
    private void consumeMatrixItems(CraftingInventory craftingInventory, ItemStack[] matrix) {
        // 创建新的矩阵副本用于更新
        ItemStack[] newMatrix = matrix.clone();
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

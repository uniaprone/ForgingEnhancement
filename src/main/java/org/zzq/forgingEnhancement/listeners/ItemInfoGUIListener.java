package org.zzq.forgingEnhancement.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUIHolder;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUIService;

import java.util.logging.Logger;

public class ItemInfoGUIListener implements Listener {
    private Logger logger;
    private ItemInfoGUIService itemInfoGUIService;

    public ItemInfoGUIListener(Logger logger, ItemInfoGUIService itemInfoGUIService) {
        this.logger = logger;
        this.itemInfoGUIService = itemInfoGUIService;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查点击的库存是否是我们自定义的GUI
        if (!(event.getInventory().getHolder() instanceof ItemInfoGUIHolder)) {
            return; // 不是我们的GUI，不处理
        }

        // 取消所有在这个GUI内的点击事件
        event.setCancelled(true);

        // 可选：添加关闭按钮功能
        if (event.getSlot() == 26) { // 假设右下角是关闭按钮
            event.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // 防止在GUI内拖拽物品
        if (event.getInventory().getHolder() instanceof ItemInfoGUIHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // 清理资源（如果需要）
        if (event.getInventory().getHolder() instanceof ItemInfoGUIHolder) {
            // 可以在这里执行一些清理操作
            event.getPlayer().setItemOnCursor(null);
        }
    }
}

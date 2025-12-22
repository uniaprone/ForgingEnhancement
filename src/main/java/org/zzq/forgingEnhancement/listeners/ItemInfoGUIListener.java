package org.zzq.forgingEnhancement.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIHolder;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIService;
import org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI.AttributeBindingService;

import java.util.logging.Logger;

public class ItemInfoGUIListener implements Listener {
    private Logger logger;
    private ItemInfoGUIService itemInfoGUIService;
    private AttributeBindingService attributeBindingService;

    public ItemInfoGUIListener(Logger logger, ItemInfoGUIService itemInfoGUIService, AttributeBindingService attributeBindingService) {
        this.logger = logger;
        this.itemInfoGUIService = itemInfoGUIService;
        this.attributeBindingService = attributeBindingService;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ClickType clickType = event.getClick();
        Player player = (Player) event.getWhoClicked();
        if(clickType.isRightClick()){
            itemInfoGUIService.openItemInfoGUI(player, event.getCurrentItem());
        }

        // 检查点击的库存是否是我们自定义的GUI
        if (!(event.getInventory().getHolder() instanceof ItemInfoGUIHolder)) {
            return; // 不是我们的GUI，不处理
        }
        if(event.getCurrentItem() != null && !event.getCurrentItem().getType().name().equals("AIR")){
            int clickSlot = event.getRawSlot();
            attributeBindingService.openAttributeBindingGUI((Player) event.getWhoClicked(), clickSlot, itemInfoGUIService.getInventoryHolder().getItemStack(), itemInfoGUIService.getInventoryHolder());
        }
        // 取消所有在这个GUI内的点击事件
        event.setCancelled(true);


        // 可选：添加关闭按钮功能
        if (event.getSlot() == 49) { // 假设右下角是关闭按钮
            player.closeInventory();
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
            event.getPlayer().setItemOnCursor(null);
        }
    }
}

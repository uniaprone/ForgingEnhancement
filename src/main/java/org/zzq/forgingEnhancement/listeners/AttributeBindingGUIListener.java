package org.zzq.forgingEnhancement.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIService;
import org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI.AttributeBindingHolder;
import org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI.AttributeBindingService;

import java.util.Objects;
import java.util.logging.Logger;

public class AttributeBindingGUIListener implements Listener {
    private Logger logger;
    private AttributeBindingService attributeBindingService;
    private ItemInfoGUIService itemInfoGUIService;

    public AttributeBindingGUIListener(Logger logger, AttributeBindingService attributeBindingService, ItemInfoGUIService itemInfoGUIService) {
        this.logger = logger;
        this.attributeBindingService = attributeBindingService;
        this.itemInfoGUIService = itemInfoGUIService;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // 检查点击的库存是否是我们自定义的GUI
        if (!(event.getInventory().getHolder() instanceof AttributeBindingHolder)) {
            return; // 不是我们的GUI，不处理
        }
        int clickSlot = event.getRawSlot();
        if((attributeBindingService.getAttributeBindingHolder().getSlotType(clickSlot)  == AttributeBindingHolder.SlotType.DISPLAY_ONLY)){
            event.setCancelled(true);
        }
        if(attributeBindingService.getAttributeBindingHolder().getSlotType(clickSlot)  == AttributeBindingHolder.SlotType.FUNCTIONAL){
            event.setCancelled(true);
            if(clickSlot == 6){attributeBindingService.confirmLogic(Objects.requireNonNull(event.getClickedInventory()).getItem(4));}
            if(clickSlot == 7){attributeBindingService.cancelLogic(event.getInventory().getItem(4),(Player) event.getWhoClicked());}
            if(clickSlot == 8){
                attributeBindingService.closeLogic(event.getInventory().getItem(4),(Player) event.getWhoClicked());
                itemInfoGUIService.openItemInfoGUI(((Player) event.getWhoClicked()).getPlayer(), itemInfoGUIService.getInventoryHolder().getItemStack());
            }
        }

        if(attributeBindingService.getAttributeBindingHolder().getSlotType(clickSlot)  == AttributeBindingHolder.SlotType.INTERACTIVE){
            logger.info("交互槽位");
            attributeBindingService.interactiveSlotDetection(event.getCurrentItem());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        // 防止在GUI内拖拽物品
        logger.info("onInventoryDrag事件: " + event.getType().name());
        if (event.getInventory().getHolder() instanceof AttributeBindingHolder) {
            logger.info("事件: " + event.getType().name());
//            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // 清理资源（如果需要）
        if (event.getInventory().getHolder() instanceof AttributeBindingHolder) {
            // 可以在这里执行一些清理操作
            event.getPlayer().setItemOnCursor(null);
        }
    }
}

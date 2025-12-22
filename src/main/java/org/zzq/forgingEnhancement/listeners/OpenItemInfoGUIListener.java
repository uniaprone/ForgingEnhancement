package org.zzq.forgingEnhancement.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIService;

public class OpenItemInfoGUIListener implements Listener {
    private final ItemInfoGUIService itemInfoGUIService;

    public OpenItemInfoGUIListener(ItemInfoGUIService itemInfoGUIService) {
        this.itemInfoGUIService = itemInfoGUIService;
    }

    @EventHandler
    public void open(InventoryClickEvent event){
        ClickType clickType = event.getClick();
        Player player = (Player) event.getWhoClicked();
        if(player.getInventory().getType() != InventoryType.PLAYER) return;
        if(!clickType.isRightClick()) return;

        itemInfoGUIService.openItemInfoGUI(player, event.getCurrentItem());
    }
}

package org.zzq.forgingEnhancement.services.guiService;

import com.google.common.collect.Multimap;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.services.EnhancementService;
import org.zzq.forgingEnhancement.services.NBTService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ItemInfoGUIService {
    private Logger logger;
    private NBTService nbtService;
    private GUIDecorateService guiDecorateService;
    public ItemInfoGUIService(Logger logger, NBTService nbtService, GUIDecorateService guiDecorateService) {
        this.logger = logger;
        this.nbtService = nbtService;
        this.guiDecorateService = guiDecorateService;
    }

    public void openItemInfoGUI(Player player, ItemStack item){
        ItemMeta itemMeta = item.getItemMeta();
        logger.info("forgingAttribute为:"+ item);
        InventoryHolder inventoryHolder = new ItemInfoGUIHolder(item);
        Component titleComponent = getDisplayName(item);
        Inventory itemInfoInventory = Bukkit.createInventory(inventoryHolder, 54, titleComponent);
        Map<Integer, ItemStack> forgingItem = guiDecorateService.placeForgingItem(item.clone());
        if(nbtService.hasForgingNBT(itemMeta)){
            EnhancementResult enhancementResult = nbtService.getForgingNBT(itemMeta);
            Map<Integer, ItemStack> border = guiDecorateService.itemInfoGUIBorder(enhancementResult.getLevel());
            Map<Integer, ItemStack> forgingAttributeSlots = guiDecorateService.forgingAttributeSlots(enhancementResult.getAttributeList());
            border.forEach(itemInfoInventory::setItem);
            forgingAttributeSlots.forEach(itemInfoInventory::setItem);
        }
        forgingItem.forEach(itemInfoInventory::setItem);
        player.openInventory(itemInfoInventory);
    }

    public boolean isValidInput(Player player, ItemStack item){
        return player != null && item != null && item.hasItemMeta() && item.getItemMeta() != null && nbtService.hasForgingNBT(item.getItemMeta());
    }

    private Component getDisplayName(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.hasCustomName()){
            return itemMeta.displayName();
        }
        else{
            return Component.text("装备属性");
        }
    }
}

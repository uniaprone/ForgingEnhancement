package org.zzq.forgingEnhancement.application.guiService.ItemInfoGUI;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.infrastructure.minecraft.ForgingDataRepository;
import org.zzq.forgingEnhancement.infrastructure.minecraft.services.MinecraftItemService;
import org.zzq.forgingEnhancement.application.guiService.GUIDecorateService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ItemInfoGUIService {
    private Logger logger;
    private MinecraftItemService minecraftItemService;
    private GUIDecorateService guiDecorateService;
    private ItemInfoGUIHolder inventoryHolder;
    private ForgingDataRepository forgingDataRepository;
    public ItemInfoGUIService(Logger logger, MinecraftItemService minecraftItemService, GUIDecorateService guiDecorateService, ForgingDataRepository forgingDataRepository) {
        this.logger = logger;
        this.guiDecorateService = guiDecorateService;
        this.minecraftItemService = minecraftItemService;
        this.forgingDataRepository = forgingDataRepository;
    }

    public void openItemInfoGUI(Player player, ItemStack item){
        if(player == null) return;
        if(item == null) return;
        ItemMeta itemMeta = item.getItemMeta();
        if(!minecraftItemService.isForged(itemMeta)) return;
        Map<Integer, ItemStack> slotMap = new HashMap<>();
        Map<Integer, ItemStack> forgingItem = guiDecorateService.placeForgingItem(item);
        slotMap.putAll(forgingItem);
        EnhancementResult enhancementResult = forgingDataRepository.getForgingData(itemMeta);

        Map<Integer, ItemStack> border = guiDecorateService.itemInfoGUIBorder(enhancementResult.getLevel());
        slotMap.putAll(border);
        Map<Integer, ItemStack> forgingAttributeSlots = guiDecorateService.forgingAttributeSlots(enhancementResult.getAttributeList());
        slotMap.putAll(forgingAttributeSlots);
        inventoryHolder = new ItemInfoGUIHolder(item, slotMap);
        Component titleComponent = getDisplayName(item);
        Inventory itemInfoInventory = Bukkit.createInventory(inventoryHolder, 54, titleComponent);
        inventoryHolder.getSlotMap().forEach(itemInfoInventory::setItem);
        player.openInventory(itemInfoInventory);
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

    public ItemInfoGUIHolder getInventoryHolder() {
        return inventoryHolder;
    }
}

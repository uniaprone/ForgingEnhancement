package org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ItemInfoGUIHolder implements InventoryHolder {
    private ItemStack itemStack;
    private Map<Integer, ItemStack> slotMap;
    public ItemInfoGUIHolder(ItemStack itemStack, Map<Integer, ItemStack> slotMap) {
        this.itemStack = itemStack;
        this.slotMap = slotMap;
    }
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Map<Integer, ItemStack> getSlotMap() {
        return slotMap;
    }
}

package org.zzq.forgingEnhancement.services.guiService;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemInfoGUIHolder implements InventoryHolder {
    private ItemStack itemStack;
    public ItemInfoGUIHolder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}

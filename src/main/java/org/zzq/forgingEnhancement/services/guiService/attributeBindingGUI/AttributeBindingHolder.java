package org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AttributeBindingHolder implements InventoryHolder {
    private ItemStack forgingItem;
    private ItemStack forgingAttributeItem;
    private final Map<Integer, SlotType> slotTypes = new HashMap<>();

    public enum SlotType {
        FUNCTIONAL,    // 功能槽位（如按钮）
        INTERACTIVE,   // 可交互槽位（允许操作物品）
        DISPLAY_ONLY   // 仅展示槽位（不允许任何操作）
    }

    public AttributeBindingHolder(ItemStack forgingItem, ItemStack forgingAttributeItem) {
        this.forgingItem = forgingItem;
        this.forgingAttributeItem = forgingAttributeItem;
        initializeSlotTypes();
    }

    public ItemStack getForgingItem() {
        return forgingItem;
    }

    public ItemStack getForgingAttributeItem() {
        return forgingAttributeItem;
    }

    private void initializeSlotTypes() {
        // 其他槽位默认为DISPLAY_ONLY
        slotTypes.put(0, SlotType.DISPLAY_ONLY);
        slotTypes.put(1, SlotType.DISPLAY_ONLY);
        slotTypes.put(2, SlotType.DISPLAY_ONLY);
        slotTypes.put(3, SlotType.DISPLAY_ONLY);
        slotTypes.put(5, SlotType.DISPLAY_ONLY);

        // 设置功能槽位
        slotTypes.put(6, SlotType.FUNCTIONAL); // 属性1按钮
        slotTypes.put(7, SlotType.FUNCTIONAL); // 属性2按钮
        slotTypes.put(8, SlotType.FUNCTIONAL);

        // 设置可交互槽位
        slotTypes.put(4, SlotType.INTERACTIVE); // 材料槽位1
    }

    public void updateSlotType(){
        slotTypes.clear();
        for(int i = 0; i < 8; i++){
            slotTypes.put(i, SlotType.DISPLAY_ONLY);
        }
        slotTypes.put(8, SlotType.FUNCTIONAL);
    }

    public SlotType getSlotType(int slot) {
        return slotTypes.getOrDefault(slot, SlotType.INTERACTIVE);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}

package org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.EngraveStoneManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;
import org.zzq.forgingEnhancement.models.ForgingAttribute;
import org.zzq.forgingEnhancement.services.ItemDisplayService;
import org.zzq.forgingEnhancement.services.NBTService;
import org.zzq.forgingEnhancement.services.guiService.GUIDecorateService;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIHolder;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIService;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class AttributeBindingService {
    private Logger logger;
    private NBTService nbtService;
    private GUIDecorateService guiDecorateService;
    private AttributeBindingHolder attributeBindingHolder;
    private EngraveStoneManager engraveStoneManager;
    private ItemDisplayService itemDisplayService;
    private Inventory attributeBindingInventory;

    public AttributeBindingService(Logger logger, NBTService nbtService, GUIDecorateService guiDecorateService, EngraveStoneManager engraveStoneManager, ItemDisplayService itemDisplayService) {
        this.logger = logger;
        this.nbtService = nbtService;
        this.guiDecorateService = guiDecorateService;
        this.engraveStoneManager =engraveStoneManager;
        this.itemDisplayService = itemDisplayService;
    }

    public void openAttributeBindingGUI(Player player, int clickSlot, ItemStack forgingItem, ItemInfoGUIHolder itemInfoGUIHolder){
        ItemStack item = itemInfoGUIHolder.getSlotMap().get(clickSlot);
        if(!isValidInput(player, item)) return;
        ItemMeta itemMeta = item.getItemMeta();
        ForgingAttribute forgingAttribute = nbtService.getForgingAttributeGUINBT(itemMeta);
        Component titleComponent = getDisplayName(item);
        attributeBindingHolder = new AttributeBindingHolder(forgingItem, item);
        attributeBindingInventory = Bukkit.createInventory(attributeBindingHolder, 9, titleComponent);
        Map<Integer, ItemStack> slotMap = guiDecorateService.attributeBindingSlots(attributeBindingHolder, item, forgingAttribute.getLevel());
        //setItem会创建副本
        slotMap.forEach(attributeBindingInventory::setItem);
        player.openInventory(attributeBindingInventory);
    }

    public void interactiveSlotDetection(ItemStack item){
        //TODO
        logger.info("正在编写中...");
    }

    public void confirmLogic(ItemStack bindingSlotItem){
        if(!checkBindingSlot(bindingSlotItem)) return;
        ItemStack forgingItem = attributeBindingHolder.getForgingItem();
        ItemMeta itemMeta = forgingItem.getItemMeta();
        ItemStack attributeItem = attributeBindingHolder.getForgingAttributeItem();
        ItemMeta attributeMete = attributeItem.getItemMeta();
        ForgingAttribute forgingAttribute = nbtService.getForgingAttributeGUINBT(attributeMete);
        //检查是否已绑定
        if(forgingAttribute.isEngraved()) return;
        EnhancementResult enhancementResult = nbtService.getForgingNBT(itemMeta);
        for(int i = 0; i < enhancementResult.getAttributeList().size(); i++){
            if(enhancementResult.getAttributeList().get(i).getName().equals(forgingAttribute.getName())){
                enhancementResult.getAttributeList().get(i).setEngraved(true);
                break;
            }
        }
        forgingAttribute.setEngraved(true);
        nbtService.storeForgingNBT(itemMeta, enhancementResult);
        nbtService.storeForgingAttributeGUINBT(attributeMete, forgingAttribute);
        itemDisplayService.addEngravedLora(attributeMete);
        forgingItem.setItemMeta(itemMeta);
        attributeItem.setItemMeta(attributeMete);
        attributeBindingHolder.setForgingAttributeItem(attributeItem);
        attributeBindingInventory.setItem(1, attributeItem);
        bindingSlotItem.setAmount(bindingSlotItem.getAmount() - 1);
    }

    public void cancelLogic(ItemStack itemStack, Player player) {
        if (itemStack == null || itemStack.getType().isAir()) {
            return; // 槽位为空，无需移动
        }

        // 获取玩家背包
        Inventory playerInventory = player.getInventory();

        // 尝试将物品添加到背包（会自动处理堆叠）
        HashMap<Integer, ItemStack> leftover = playerInventory.addItem(itemStack);

        if (leftover.isEmpty()) {
            // 物品成功添加到背包，清空原槽位
            attributeBindingInventory.setItem(4, new ItemStack(Material.AIR));
        } else {
            // 背包已满，无法完全添加
            // 将剩余物品放回原槽位
            player.dropItem(itemStack);
            attributeBindingInventory.setItem(4, new ItemStack(Material.AIR));
            player.sendMessage("§c背包已满，无法放入物品！");
        }
    }

    public void closeLogic(ItemStack itemStack, Player player){
        cancelLogic(itemStack, player);
    }

    public boolean isValidInput(Player player, ItemStack item){
        return player != null && item != null && item.hasItemMeta() && item.getItemMeta() != null && nbtService.hasForgingAttributeGUINBT(item.getItemMeta());
    }

    private boolean checkBindingSlot(ItemStack itemStack){
        if(itemStack == null) return false;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) return false;
        return engraveStoneManager.isEngraveStone(itemStack);
    }

    private Component getDisplayName(ItemStack itemStack){
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta.hasCustomName()){
            return itemMeta.displayName();
        }
        else{
            return Component.text("属性");
        }
    }

    public AttributeBindingHolder getAttributeBindingHolder() {
        return attributeBindingHolder;
    }
}

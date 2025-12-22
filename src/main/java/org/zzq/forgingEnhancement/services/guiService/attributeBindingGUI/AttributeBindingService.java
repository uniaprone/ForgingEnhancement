package org.zzq.forgingEnhancement.services.guiService.attributeBindingGUI;

import com.google.gson.Gson;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.infrastructure.manager.KeyManager;
import org.zzq.forgingEnhancement.infrastructure.manager.EngraveStoneManager;
import org.zzq.forgingEnhancement.domain.entity.EnhancementResult;
import org.zzq.forgingEnhancement.domain.entity.ForgingAttribute;
import org.zzq.forgingEnhancement.infrastructure.minecraft.ForgingDataRepository;
import org.zzq.forgingEnhancement.utils.SoundUtil;
import org.zzq.forgingEnhancement.services.guiService.GUIDecorateService;
import org.zzq.forgingEnhancement.services.guiService.ItemInfoGUI.ItemInfoGUIHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AttributeBindingService {
    private Logger logger;
    private GUIDecorateService guiDecorateService;
    private AttributeBindingHolder attributeBindingHolder;
    private EngraveStoneManager engraveStoneManager;
    private Inventory attributeBindingInventory;
    private KeyManager keyManager;
    private ForgingDataRepository forgingDataRepository;
    private final Gson gson = new Gson();

    public AttributeBindingService(Logger logger, KeyManager keyManager, GUIDecorateService guiDecorateService, EngraveStoneManager engraveStoneManager, ForgingDataRepository forgingDataRepository) {
        this.logger = logger;
        this.guiDecorateService = guiDecorateService;
        this.engraveStoneManager =engraveStoneManager;
        this.keyManager = keyManager;
        this.forgingDataRepository = forgingDataRepository;
    }

    public void openAttributeBindingGUI(Player player, int clickSlot, ItemStack forgingItem, ItemInfoGUIHolder itemInfoGUIHolder){
        ItemStack item = itemInfoGUIHolder.getSlotMap().get(clickSlot);
        if(!isValidInput(player, item)) return;
        ItemMeta itemMeta = item.getItemMeta();

        ForgingAttribute forgingAttribute = forgingDataRepository.getGUIForgingAttribute(itemMeta);
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

    public void confirmLogic(Player player, ItemStack bindingSlotItem){
        if(!checkBindingSlot(bindingSlotItem)) return;
        ItemStack forgingItem = attributeBindingHolder.getForgingItem();
        ItemMeta itemMeta = forgingItem.getItemMeta();
        ItemStack attributeItem = attributeBindingHolder.getForgingAttributeItem();
        ItemMeta attributeMete = attributeItem.getItemMeta();
        String attributeString1 = attributeMete.getPersistentDataContainer().get(keyManager.getForgingAttributeGUIKey(), PersistentDataType.STRING);
        ForgingAttribute forgingAttribute = gson.fromJson(attributeString1, ForgingAttribute.class);
        //检查是否已绑定
        if(forgingAttribute.isEngraved()) return;
        String AttributesString2 = itemMeta.getPersistentDataContainer().get(keyManager.getEnhancementKey(), PersistentDataType.STRING);
        EnhancementResult enhancementResult = gson.fromJson(AttributesString2, EnhancementResult.class);
        for(int i = 0; i < enhancementResult.getAttributeList().size(); i++){
            if(enhancementResult.getAttributeList().get(i).getName().equals(forgingAttribute.getName())){
                enhancementResult.getAttributeList().get(i).setEngraved(true);
                break;
            }
        }
        forgingAttribute.setEngraved(true);

        String AttributeString3 = gson.toJson(enhancementResult);
        itemMeta.getPersistentDataContainer().set(keyManager.getEnhancementKey(),  PersistentDataType.STRING, AttributeString3);

        String AttributeString4 = gson.toJson(forgingAttribute);
        attributeMete.getPersistentDataContainer().set(keyManager.getForgingAttributeGUIKey(),  PersistentDataType.STRING, AttributeString4);

        addEngravedLora(attributeMete);
        forgingItem.setItemMeta(itemMeta);
        attributeItem.setItemMeta(attributeMete);
        attributeBindingHolder.setForgingAttributeItem(attributeItem);
        attributeBindingInventory.setItem(1, attributeItem);
        bindingSlotItem.setAmount(bindingSlotItem.getAmount() - 1);
        SoundUtil.playEngraveSound(player);
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
        return player != null && item != null && item.hasItemMeta() && item.getItemMeta() != null && item.getItemMeta().getPersistentDataContainer().get(keyManager.getForgingAttributeGUIKey(), PersistentDataType.STRING) != null;
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

    public void addEngravedLora(ItemMeta itemMeta){
        List<Component> originLore = itemMeta.lore();
        if (originLore != null) {
            originLore.add(originLore.size() ,Component.text("已铭刻").color(NamedTextColor.DARK_GRAY));
        }else{
            originLore = List.of(Component.text("已铭刻").color(NamedTextColor.DARK_GRAY));
        }
        itemMeta.lore(originLore);
    }

    public AttributeBindingHolder getAttributeBindingHolder() {
        return attributeBindingHolder;
    }
}

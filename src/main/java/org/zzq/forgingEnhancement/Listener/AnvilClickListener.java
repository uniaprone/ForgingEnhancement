package org.zzq.forgingEnhancement.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.zzq.forgingEnhancement.Service.*;
import org.zzq.forgingEnhancement.model.Attribute;
import org.zzq.forgingEnhancement.model.EnhancementResult;

import java.util.List;

public class AnvilClickListener implements Listener {
    private AttributeApplicationService attributeApplicationService;
    private EnhancementService enhancementService;
    private ItemDisplayService itemDisplayService;
    private NBTService nbtService;
    private PluginContext pluginContext;
    public AnvilClickListener(PluginContext pluginContext){
        this.pluginContext = pluginContext;
        this.attributeApplicationService = new AttributeApplicationService(pluginContext);
        this.enhancementService = new EnhancementService(pluginContext.getConfigManager());
        this.itemDisplayService = new ItemDisplayService(pluginContext);
        this.nbtService = new NBTService(pluginContext);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        pluginContext.getLogger().info("铁砧点击事件触发");
        if (event.getInventory().getType() != InventoryType.ANVIL) {
            return;
        }
        pluginContext.getLogger().info("检测到铁砧界面，槽位: " + event.getRawSlot());
        // 只处理结果槽的点击（槽位2）
        if (event.getRawSlot() != 2) {
            return;
        }

        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null) {
            return;
        }
        ItemMeta resultItemMeta = resultItem.getItemMeta();

        event.setCancelled(true); // 取消默认的点击行为

        // 获取铁砧中的物品
        AnvilInventory anvil = (AnvilInventory) event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        if (firstItem == null || secondItem == null || !pluginContext.getPlugin().isForgingStone(secondItem)) {
            return;
        }

        pluginContext.getLogger().info("开始应用属性到物品");
        if(pluginContext.getConfigManager().isEnhanceableEquipment(firstItem.getType())){
            pluginContext.getLogger().info("是可强化装备");
            String baseQuality = pluginContext.getPlugin().getStoneQuality(secondItem);
            int baseLevel = pluginContext.getConfigManager().getLevelByQuality(baseQuality);
            EnhancementResult enhancementResult  = enhancementService.enhance(firstItem, baseLevel);
            if(!nbtService.hasBaseAttributeApplied(resultItemMeta)){
                attributeApplicationService.applyBaseAttributes(resultItemMeta, resultItem.getType());
                nbtService.markBaseAttributeApplied(resultItemMeta);
            }
            attributeApplicationService.applyExtraAttributes(resultItemMeta, resultItem.getType(), enhancementResult.getAttributeList());
            nbtService.storeForgingNBT(resultItemMeta, pluginContext.getPlugin().getEnhancementKey(), enhancementResult);
            itemDisplayService.updateItemDisplay(resultItemMeta,resultItem.getItemMeta(),  baseQuality, enhancementResult.getAttributeList());
            resultItem.setItemMeta(resultItemMeta);

            anvil.setFirstItem(null);
            anvil.setSecondItem(consumeItem(secondItem));

            // 手动设置光标物品
            event.getWhoClicked().setItemOnCursor(resultItem);
            // 更新铁砧结果槽为空
            anvil.setResult(null);
        }
    }
    // 消耗物品（减少数量）
    private ItemStack consumeItem(ItemStack item) {
        if (item.getAmount() > 1) {
            ItemStack consumed = item.clone();
            consumed.setAmount(item.getAmount() - 1);
            return consumed;
        }
        return null;
    }
}


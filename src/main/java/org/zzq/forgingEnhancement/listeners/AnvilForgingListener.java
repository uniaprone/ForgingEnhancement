package org.zzq.forgingEnhancement.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.utils.ColorUtil;
import org.zzq.forgingEnhancement.utils.RegxUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnvilForgingListener implements Listener {
    private final ConfigManager configManager;
    private final StoneManager stoneManager;
    
    public AnvilForgingListener(ConfigManager configManager, StoneManager stoneManager) {
        this.configManager = configManager;
        this.stoneManager = stoneManager;
    }

    // 修改onPrepareAnvil方法中的锻造石识别部分
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        if (firstItem != null && secondItem != null && stoneManager.isForgingStone(secondItem)) {
            String firstItemType = configManager.getEquipmentType(firstItem.getType());
            if (configManager.isEnhanceableEquipment(firstItemType)) {
                ItemStack result = firstItem.clone();
                ItemMeta resultItemMeta = result.getItemMeta();
                updatePrepareAnvilDisplay(resultItemMeta, firstItem.getItemMeta(), stoneManager.getStoneQualityLevel(secondItem));
                result.setItemMeta(resultItemMeta);
                event.setResult(result);
                event.getView().setRepairCost(0);
            }
        }
    }

    public void updatePrepareAnvilDisplay(ItemMeta newMeta, ItemMeta originalMeta, int itemQualityLevel){
        // 获取原有Lore
        List<Component> originalLore = originalMeta.hasLore() ? originalMeta.lore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();
        // 移除之前由本插件添加的强化信息（如果有的话）
        removeOldForgingLore(originalLore);

        List<Component> newLore = new ArrayList<>();

        // 添加品质信息
        NamedTextColor qualityColor = ColorUtil.getColorByLevel(itemQualityLevel);
        Component unKnowComponent = Component.text("品质: " + "???").color(qualityColor);
        newLore.add(unKnowComponent);
        newLore.addAll(originalLore);
        newMeta.lore(newLore);
    }

    private void removeOldForgingLore(List<Component> Lore){
        Iterator<Component> iterator = Lore.iterator();
        while (iterator.hasNext()) {
            TextComponent line = (TextComponent) iterator.next();
            if(RegxUtil.loraDetection(line.content())){
                iterator.remove();
            }
        }
    }
}

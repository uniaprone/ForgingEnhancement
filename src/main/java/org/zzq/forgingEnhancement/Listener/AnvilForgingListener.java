package org.zzq.forgingEnhancement.Listener;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.zzq.forgingEnhancement.ForgingEnhancement;
import org.zzq.forgingEnhancement.Util.RandomUtil;
import org.zzq.forgingEnhancement.manager.BaseAttributeManager;
import org.zzq.forgingEnhancement.manager.ConfigManager;

import java.util.*;

public class AnvilForgingListener implements Listener {
    private final ForgingEnhancement plugin;
    private final ConfigManager configManager;
    private final BaseAttributeManager baseAttributeManager;
    private NamespacedKey enhancementKey;
    private NamespacedKey baseAttributeKey;

    private Random random;
    
    public AnvilForgingListener(ForgingEnhancement plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getFileManager().getConfigManager();
        this.baseAttributeManager = plugin.getFileManager().getBaseAttributeManager();
        this.random = new Random();
        this.enhancementKey = new NamespacedKey(plugin, "forging_data");
        this.baseAttributeKey = new NamespacedKey(plugin, "base_attribute_applied");
    }

    // 修改onPrepareAnvil方法中的锻造石识别部分
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        if (firstItem != null && secondItem != null && isForgingStone(secondItem)) {
            String firstItemType = configManager.getEquipmentType(firstItem.getType());
            if (configManager.isEnhanceableEquipment(firstItemType)) {
                ItemStack result = firstItem.clone();

                event.setResult(result);
                event.getView().setRepairCost(0);
            }
        }
    }

    // 修改锻造石识别方法
    private boolean isForgingStone(ItemStack item) {
        return plugin.isForgingStone(item);
    }
}

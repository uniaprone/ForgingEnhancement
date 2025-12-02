package org.zzq.forgingEnhancement.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.zzq.forgingEnhancement.ForgingEnhancement;
import org.zzq.forgingEnhancement.managers.BaseAttributeManager;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.managers.PlayerSettingManager;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.services.ItemDisplayService;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CraftingForgingListener implements Listener {
    private final ConfigManager configManager;
    private final PlayerSettingManager playerSettingManager;
    private ItemDisplayService itemDisplayService;
    private Logger logger;

    public CraftingForgingListener(ConfigManager configManager, StoneManager stoneManager, ItemDisplayService itemDisplayService,Logger logger, PlayerSettingManager playerSettingManager) {
        this.configManager = configManager;
        this.itemDisplayService = itemDisplayService;
        this.logger = logger;
        this.playerSettingManager = playerSettingManager;
    }

    // 修改onPrepareAnvil方法中的锻造石识别部分
    @EventHandler
    public void onWorkbenchCraft(PrepareItemCraftEvent event) {
        // 1. 检查关键依赖是否注入成功
        if (playerSettingManager == null || configManager == null || itemDisplayService == null) {
            logger.severe("[ForgingEnhancement] 关键依赖未正确初始化，事件处理已跳过。");
            return;
        }

        // 2. 安全获取并检查观众列表
        List<HumanEntity> viewers = event.getViewers();
        if (viewers == null || viewers.isEmpty()) {
            return;
        }

        for (HumanEntity player : viewers) {
            // 3. 检查循环中的玩家对象是否有效
            if (player == null) {
                continue; // 跳过无效的玩家引用
            }

            // 4. 安全获取玩家设置
            Boolean setting = playerSettingManager.getPlayerSetting(player.getUniqueId());
            if (setting == null || !setting) {
                // 如果获取到的设置为null或false，则不进行增强处理
                return;
            }

            CraftingInventory craftingInventory = event.getInventory();
            // 5. 检查合成库存是否存在
            if (craftingInventory == null) {
                return;
            }
            ItemStack resultItem = craftingInventory.getResult();
            // 使用 .isAir() 方法能更准确地判断物品是否有效
            if (resultItem == null || resultItem.getType().isAir()) {
                return;
            }

            // 6. 安全获取装备类型并检查
            String firstItemType = configManager.getEquipmentType(resultItem.getType());
            if (firstItemType == null || firstItemType.isEmpty()) {
                return;
            }
            if (!configManager.isEnhanceableEquipment(firstItemType)) {
                return;
            }

            // 7. 【核心修复】安全处理 ItemMeta
            ItemStack result = resultItem.clone();
            // 获取克隆后物品的元数据
            ItemMeta resultItemMeta = result.getItemMeta();
            // 获取原始物品的元数据
            ItemMeta originalItemMeta = resultItem.getItemMeta();

            // 检查两者是否都为非空
            if (resultItemMeta == null || originalItemMeta == null) {
                // 如果任何一个元数据为空，可以选择跳过增强或创建新元数据
                // 例如: resultItemMeta = Bukkit.getItemFactory().getItemMeta(result.getType());
                // 此处选择记录日志并跳过
                logger.warning("[ForgingEnhancement] 无法获取物品的元数据，跳过增强。物品类型: " + resultItem.getType());
                return;
            }

            // 8. 调用显示服务
            try {
                itemDisplayService.updatePrepareAnvilDisplay(resultItemMeta, originalItemMeta, 1);
                result.setItemMeta(resultItemMeta);
                craftingInventory.setResult(result);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "[ForgingEnhancement] 在更新物品显示时发生错误", e);
            }
        }
    }
}


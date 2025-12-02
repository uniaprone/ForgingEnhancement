package org.zzq.forgingEnhancement.services;

import org.bukkit.inventory.ItemStack;
import org.zzq.forgingEnhancement.managers.StoneManager;
import org.zzq.forgingEnhancement.models.ForgingAttribute;
import org.zzq.forgingEnhancement.managers.ConfigManager;
import org.zzq.forgingEnhancement.models.EnhancementResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Logger;

public class EnhancementService {
    private ConfigManager configManager;
    private StoneManager stoneManager;
    private Logger logger;
    private RandomService randomService;
    public EnhancementService(ConfigManager configManager, StoneManager stoneManager, Logger logger, RandomService randomService) {
        this.configManager = configManager;
        this.stoneManager = stoneManager;
        this.logger = logger;
        this.randomService = randomService;
    }

    /**
     * 执行装备强化操作
     *
     * @param item 待强化的物品堆栈
     * @param stoneLevel 强化石等级
     * @return 强化结果对象，包含最终等级和属性列表
     */
    public EnhancementResult enhance(ItemStack item, int stoneLevel){
        // 获取装备类型
        String itemType = configManager.getEquipmentType(item.getType());
        // 计算最终强化等级（基于正态分布）
        int finalItemLevel = randomService.normalDistribution(stoneLevel, stoneManager.getLevelQualityMap().size() - 1);

        // 计算并选择普通属性
        int maxCommonAttributes = configManager.getEquipmentCommonAttributes(itemType).size();
        int commonAttributeCount = calculateAttributeCount(finalItemLevel, maxCommonAttributes);
        List<String> availableAttributes = configManager.getEquipmentCommonAttributes(itemType);
        // 确保选择的属性数量不超过可用属性总数
        commonAttributeCount = Math.min(commonAttributeCount, availableAttributes.size());
        // 随机打乱属性列表并选择前N个
        Collections.shuffle(availableAttributes);
        List<String> selectedAttributes = availableAttributes.subList(0, commonAttributeCount);

        // 计算选定属性的品质分布
        List<ForgingAttribute> enhancements = calculateAttributeQualities(selectedAttributes, finalItemLevel, commonAttributeCount);

        // 处理稀有属性
        List<String> rareAttributes = configManager.getEquipmentRareAttributes(itemType);
        if (!rareAttributes.isEmpty()) {
            for (String rareAttribute : rareAttributes) {
                // 根据稀有概率决定是否添加稀有属性
                double randomValue = randomService.nextDouble();
                if (randomValue < 0.1 + (double)(finalItemLevel + 1) / 30.0){// 添加稀有属性，使用最小属性值
                    enhancements.add(new ForgingAttribute(rareAttribute, finalItemLevel,
                            configManager.getAttributeConfig(rareAttribute).values.get(
                                   stoneManager.getLevelQualityMap().get(finalItemLevel)).min));
                }
            }
        }
        // 返回强化结果
        return new EnhancementResult(finalItemLevel, enhancements);
    }

    private int calculateAttributeCount(int stoneLevel, int maxAttributes) {
        switch (stoneLevel) {
            case 0, 1:
                return Math.max((int) Math.round((maxAttributes * (0.2 + randomService.nextDouble() * (0.2)))), 1);
            case 2:
                return Math.max((int) Math.round((maxAttributes * (0.4 + randomService.nextDouble() * (0.2)))), 1);
            case 3, 4:
                return Math.max((int) Math.round((maxAttributes * (0.6 + randomService.nextDouble() * (0.2)))), 1);
            case 5:
                return Math.max((int) Math.round((maxAttributes * (0.8 + randomService.nextDouble() * (0.2)))), 1);
            default:
                return -1;
        }
    }

    private List<ForgingAttribute> calculateAttributeQualities(List<String> attributes, int itemQualityLevel, int attributeCount) {
        List<ForgingAttribute> enhancements = new ArrayList<>();

        // 计算总品质点数（每个词条基础为物品品质等级）
        int totalQualityPoints = itemQualityLevel * attributeCount;

        // 为每个词条分配基础品质（物品品质等级）
        int[] attributeLevels = new int[attributeCount];
        Arrays.fill(attributeLevels, itemQualityLevel);

        // 随机调整词条品质，但保持总和不变
        for (int i = 0; i < attributeCount * 2; i++) { // 调整次数
            int attrIndex1 = randomService.nextInt(attributeCount);
            int attrIndex2 = randomService.nextInt(attributeCount);

            if (attrIndex1 == attrIndex2) continue;

            // 尝试交换品质点数
            int diff = randomService.nextInt(3) - 1; // -1, 0, 或 1

            if (canAdjustQuality(attributeLevels[attrIndex1], -diff) &&
                    canAdjustQuality(attributeLevels[attrIndex2], diff)) {
                attributeLevels[attrIndex1] -= diff;
                attributeLevels[attrIndex2] += diff;
            }
        }

        // 创建增强属性对象
        for (int i = 0; i < attributeCount; i++) {
            String attributeKey = attributes.get(i);
            String quality = stoneManager.getLevelQualityMap().get(attributeLevels[i]);

            ConfigManager.AttributeConfig attrConfig = configManager.getAttributeConfig(attributeKey);
            if (attrConfig != null) {
                // 计算属性值
                double value = calculateAttributeValue(attrConfig, quality);

                enhancements.add(new ForgingAttribute(attributeKey, attributeLevels[i], value));
            }
        }

        return enhancements;
    }

    private boolean canAdjustQuality(int currentLevel, int adjustment) {
        int newLevel = currentLevel + adjustment;
        return newLevel >= 0 && newLevel < stoneManager.getLevelQualityMap().size();
    }

    private double calculateAttributeValue(ConfigManager.AttributeConfig config, String quality) {
        ConfigManager.ValueRange range = config.values.get(quality);
        if (range == null) {
            // 如果该品质没有定义范围，使用COMMON品质
            range = config.values.get("COMMON");
        }

        if (range != null) {
            double randomValue = range.min + (randomService.nextDouble() * (range.max - range.min));
            BigDecimal bigDecimal = new BigDecimal(Double.toString(randomValue));
            bigDecimal = bigDecimal.setScale(4, RoundingMode.HALF_UP);
            return bigDecimal.doubleValue();
        }

        // 默认值
        return 0.0;
    }
}

package org.zzq.forgingEnhancement;

import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ForgingEnhancement extends JavaPlugin implements Listener {

    private NamespacedKey enhancementKey;
    private NamespacedKey baseAttributeKey;
    private ConfigManager configManager;
    private BaseAttributeManager baseAttributeManager;
    private Random random;
    // 锻造石与品质的映射
    private final Map<Material, String> stoneToQuality = new HashMap<>();

    // 品质等级映射
    private final Map<String, Integer> qualityLevels = new HashMap<>();
    private final List<String> qualityOrder = Arrays.asList(
            "BROKEN", "COMMON", "UNCOMMON", "EPIC", "LEGENDARY", "MYTHIC"
    );

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.baseAttributeManager = new BaseAttributeManager(this);
        this.enhancementKey = new NamespacedKey(this, "enhancement_data");
        this.baseAttributeKey = new NamespacedKey(this, "base_attribute_applied");
        this.random = new Random();

        // 初始化锻造石品质映射
        initializeStoneQualities();

        // 初始化品质等级
        initializeQualityLevels();

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("forgingenhancement").setExecutor(new ForgingEnhancementCommand(this));

        getLogger().info("锻造增强插件已启用!");
    }

    private void initializeStoneQualities() {
        // 四种锻造石对应品质
        stoneToQuality.put(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE, "COMMON");     // 普通
        stoneToQuality.put(Material.NETHERITE_SCRAP, "UNCOMMON");                       // 优秀
        stoneToQuality.put(Material.AMETHYST_SHARD, "EPIC");                            // 史诗
        stoneToQuality.put(Material.DIAMOND, "LEGENDARY");                              // 传说
    }

    private void initializeQualityLevels() {
        for (int i = 0; i < qualityOrder.size(); i++) {
            qualityLevels.put(qualityOrder.get(i), i);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("锻造增强插件已禁用!");
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory anvil = event.getInventory();
        ItemStack firstItem = anvil.getFirstItem();
        ItemStack secondItem = anvil.getSecondItem();

        if (firstItem != null && secondItem != null && isForgingStone(secondItem)) {
            if (isEnhanceableEquipment(firstItem)) {
                if (hasEnhancement(firstItem)) {
                    getLogger().info("已经附魔了");
                    return;
                }

                // 获取锻造石对应的基础品质
                String baseQuality = stoneToQuality.get(secondItem.getType());
                // 对品质进行再随机
                String finalItemQuality = randomizeQuality(baseQuality);

                // 确定词条数量
                int maxAttributes = getMaxAttributesForEquipment(firstItem.getType());
                int attributeCount = random.nextInt(maxAttributes + 1);

                // 应用强化 - 这里需要确保保留所有原有属性
                ItemStack result = applyEnhancement(firstItem, finalItemQuality, attributeCount);

                if (result != null) {
                    event.setResult(result);
                    event.getView().setRepairCost(0);

                    getLogger().info("为 " + firstItem.getType() + " 添加了 " + attributeCount +
                            " 个词条，最终品质: " + finalItemQuality);
                }
            }
        }
    }

    private String randomizeQuality(String baseQuality) {
        int baseLevel = qualityLevels.get(baseQuality);

        // 品质随机规则：有概率提升或降低1级
        // 50%概率保持原品质，25%概率提升1级，25%概率降低1级
        double rand = random.nextDouble();
        int newLevel = baseLevel;

        if (rand < 0.25 && baseLevel > 0) {
            // 降低1级
            newLevel = baseLevel - 1;
        } else if (rand > 0.75 && baseLevel < qualityOrder.size() - 1) {
            // 提升1级
            newLevel = baseLevel + 1;
        }

        return qualityOrder.get(newLevel);
    }

    private int getMaxAttributesForEquipment(Material material) {
        String equipmentType = configManager.getEquipmentType(material);
        getLogger().info("装备类型"+equipmentType);
        return configManager.getEquipmentMaxAttributes(equipmentType);
    }

    private ItemStack applyEnhancement(ItemStack originalItem, String itemQuality, int attributeCount) {
        // 1. 完全克隆原物品，包括所有NBT数据
        ItemStack result = originalItem.clone();

        // 2. 获取原物品的meta并确保不为空
        ItemMeta originalMeta = originalItem.getItemMeta();
        if (originalMeta == null) {
            getLogger().warning("原物品没有ItemMeta");
            return originalItem.clone();
        }
        ItemMeta newMeta = result.getItemMeta();
        if (newMeta == null) {
            getLogger().warning("无法获取新物品的ItemMeta");
            return originalItem.clone();
        }

        // 4. 如果是0词条，只添加品质显示
        if (attributeCount == 0) {
            return applyZeroAttributeEnhancement(result, itemQuality);
        }

        // 5. 获取装备类型和可用属性
        String equipmentType = configManager.getEquipmentType(result.getType());
        if (equipmentType == null) {
            getLogger().warning("无法确定装备类型: " + result.getType());
            return originalItem.clone();
        }

        List<String> availableAttributes = new ArrayList<>(configManager.getAttributesForEquipment(equipmentType));
        if (availableAttributes.isEmpty()) {
            getLogger().warning("装备类型 " + equipmentType + " 没有可用的属性");
            return originalItem.clone();
        }

        // 6. 确保属性数量不超过可用属性
        attributeCount = Math.min(attributeCount, availableAttributes.size());

        // 7. 随机选择属性
        Collections.shuffle(availableAttributes);
        List<String> selectedAttributes = availableAttributes.subList(0, attributeCount);

        // 8. 计算词条品质分布
        List<EnhancementAttribute> enhancements = calculateAttributeQualities(selectedAttributes, itemQuality, attributeCount);

        // 9. 应用所有新词条（不修改原有属性）
        for (EnhancementAttribute enh : enhancements) {
            applySingleAttribute(newMeta, enh, equipmentType, result.getType());
        }
        result.setItemMeta(newMeta);
        getLogger().warning("基础属性判断");
        Objects.requireNonNull(result.getItemMeta().getAttributeModifiers()).forEach((attribute, modifiers) -> {
            if (attribute != null) {
                getLogger().warning("基础属性：" + attribute.getKey());
            }
        });
        getLogger().warning("基础属性结束");
        // 3. 应用基础属性（如果尚未应用）

        if (!hasBaseAttributeApplied(newMeta)) {
            applyBaseAttributes(newMeta, result.getType());
            markBaseAttributeApplied(newMeta);
        }


        // 10. 存储强化数据到NBT
        storeEnhancementData(newMeta, itemQuality, enhancements);

        // 11. 更新物品显示（保留所有原有Lore）
        updateItemDisplay(newMeta, originalMeta, itemQuality, enhancements);

        result.setItemMeta(newMeta);
        return result;
    }

    private void applyBaseAttributes(ItemMeta meta, Material material) {
        String equipmentType = configManager.getEquipmentType(material);
        if (equipmentType == null) {
            getLogger().warning("无法确定装备类型: " + material);
            return;
        }

        Map<String, Double> baseAttrs = baseAttributeManager.getBaseAttributes(material);
        if (baseAttrs.isEmpty()) {
            getLogger().warning("装备类型 " + equipmentType + " 没有基础属性配置");
            return;
        }

        getLogger().info("为 " + material + " (" + equipmentType + ") 应用 " + baseAttrs.size() + " 个基础属性");

        for (Map.Entry<String, Double> attribute : baseAttributeManager.getBaseAttributes(material).entrySet()){

            Attribute bukkitAttribute = getBukkitAttribute(attribute.getKey());
            if (bukkitAttribute == null) {
                getLogger().warning("未知的基础属性: " + attribute);
                continue;
            }
            NamespacedKey baseAttrNamespaceKey = new NamespacedKey(this, "ForgingEnhancement_base_" + attribute.getKey() + System.currentTimeMillis());
            // 基础属性命名ID
            switch (attribute.getKey()) {
                case "attack_damage" -> baseAttrNamespaceKey = NamespacedKey.minecraft("base_attack_damage");
                case "attack_speed" -> baseAttrNamespaceKey = NamespacedKey.minecraft("base_attack_speed");
                case "armor" -> {
                    if (material.name().equalsIgnoreCase("_helmet")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.helmet");
                    }
                    if (material.name().equalsIgnoreCase("_chestplate")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.chestplate");
                    }
                    if (material.name().equalsIgnoreCase("_leggings")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.leggings");
                    }
                    if (material.name().equalsIgnoreCase("_boots")) {
                        baseAttrNamespaceKey = NamespacedKey.minecraft("armor.boots");
                    }
                }
            }
            AttributeModifier baseAttrModifier = new AttributeModifier(
                    baseAttrNamespaceKey,
                    attribute.getValue(),
                    Operation.ADD_NUMBER,
                    material.getEquipmentSlot().getGroup()
            );
            meta.addAttributeModifier(bukkitAttribute, baseAttrModifier);
            getLogger().info("应用基础属性: " + attribute.getKey() + " = " + attribute.getValue() + " (" + material.getEquipmentSlot().getGroup() + ")");
        }
    }

    private boolean hasBaseAttributeApplied(ItemMeta meta) {
        return meta.getPersistentDataContainer().get(baseAttributeKey, PersistentDataType.BYTE) != null;
    }

    private void markBaseAttributeApplied(ItemMeta meta) {
        meta.getPersistentDataContainer().set(baseAttributeKey, PersistentDataType.BYTE, (byte) 1);
    }

    private void applySingleAttribute(ItemMeta meta, EnhancementAttribute enhancement, String equipmentType, Material material) {
        ConfigManager.AttributeConfig config = configManager.getAttributeConfig(enhancement.attributeKey);
        if (config == null) {
            getLogger().warning("未知的属性配置: " + enhancement.attributeKey);
            return;
        }

        Attribute bukkitAttribute = getBukkitAttribute(enhancement.attributeKey);
        if (bukkitAttribute == null) {
            getLogger().warning("未知的Bukkit属性: " + enhancement.attributeKey);
            return;
        }

        // 创建唯一标识符
        NamespacedKey modifierKey = new NamespacedKey(this,
                "enhancement_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 6));

        // 创建属性修饰符，使用原物品对应的槽位
        AttributeModifier modifier = new AttributeModifier(
                modifierKey,
                enhancement.value,
                Operation.valueOf(config.operation),
                material.getEquipmentSlot().getGroup()
        );

        // 添加属性修饰符（不会移除原有的）
        meta.addAttributeModifier(bukkitAttribute, modifier);
    }

    private ItemStack applyZeroAttributeEnhancement(ItemStack originalItem, String itemQuality) {
        ItemStack result = originalItem.clone();
        ItemMeta originalMeta = originalItem.getItemMeta();
        ItemMeta newMeta = result.getItemMeta();

        if (originalMeta == null || newMeta == null) {
            return originalItem.clone();
        }

        // 应用基础属性（如果尚未应用）
        if (!hasBaseAttributeApplied(newMeta)) {
            applyBaseAttributes(newMeta, result.getType());
            markBaseAttributeApplied(newMeta);
        }

        // 存储基础强化数据
        storeBasicEnhancementData(newMeta, itemQuality);

        // 更新显示
        updateBasicItemDisplay(newMeta, originalMeta, itemQuality);

        result.setItemMeta(newMeta);
        return result;
    }

    private void updateBasicItemDisplay(ItemMeta newMeta, ItemMeta originalMeta, String itemQuality) {
        List<String> originalLore = originalMeta.hasLore() ? originalMeta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        List<String> newLore = new ArrayList<>(originalLore);

        // 移除旧的强化信息
        Iterator<String> iterator = newLore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.contains("品质:") || line.contains("词条:") || line.contains("未获得词条")) {
                iterator.remove();
            }
        }

        // 添加分隔线
        if (!originalLore.isEmpty()) {
            newLore.add("§8§m----------------------");
        }

        // 添加品质信息
        String qualityColor = getQualityColor(itemQuality);
        newLore.add(qualityColor + "品质: " + getQualityDisplayName(itemQuality));
        newLore.add("§7未获得词条");

        newMeta.setLore(newLore);
    }

    private List<EnhancementAttribute> calculateAttributeQualities(List<String> attributes, String itemQuality, int attributeCount) {
        List<EnhancementAttribute> enhancements = new ArrayList<>();
        int itemQualityLevel = qualityLevels.get(itemQuality);

        // 计算总品质点数（每个词条基础为物品品质等级）
        int totalQualityPoints = itemQualityLevel * attributeCount;

        // 为每个词条分配基础品质（物品品质等级）
        int[] attributeLevels = new int[attributeCount];
        Arrays.fill(attributeLevels, itemQualityLevel);

        // 随机调整词条品质，但保持总和不变
        for (int i = 0; i < attributeCount * 2; i++) { // 调整次数
            int attrIndex1 = random.nextInt(attributeCount);
            int attrIndex2 = random.nextInt(attributeCount);

            if (attrIndex1 == attrIndex2) continue;

            // 尝试交换品质点数
            int diff = random.nextInt(3) - 1; // -1, 0, 或 1

            if (canAdjustQuality(attributeLevels[attrIndex1], -diff) &&
                    canAdjustQuality(attributeLevels[attrIndex2], diff)) {
                attributeLevels[attrIndex1] -= diff;
                attributeLevels[attrIndex2] += diff;
            }
        }

        // 创建增强属性对象
        for (int i = 0; i < attributeCount; i++) {
            String attributeKey = attributes.get(i);
            String quality = getQualityByLevel(attributeLevels[i]);

            ConfigManager.AttributeConfig attrConfig = configManager.getAttributeConfig(attributeKey);
            if (attrConfig != null) {
                // 计算属性值
                double value = calculateAttributeValue(attrConfig, quality);

                enhancements.add(new EnhancementAttribute(attributeKey, quality, value));
            }
        }

        return enhancements;
    }

    private boolean canAdjustQuality(int currentLevel, int adjustment) {
        int newLevel = currentLevel + adjustment;
        return newLevel >= 0 && newLevel < qualityOrder.size();
    }

    private String getQualityByLevel(int level) {
        if (level < 0) return qualityOrder.get(0);
        if (level >= qualityOrder.size()) return qualityOrder.get(qualityOrder.size() - 1);
        return qualityOrder.get(level);
    }

    private double calculateAttributeValue(ConfigManager.AttributeConfig config, String quality) {
        ConfigManager.ValueRange range = config.values.get(quality);
        if (range == null) {
            // 如果该品质没有定义范围，使用COMMON品质
            range = config.values.get("COMMON");
        }

        if (range != null) {
            return range.min + (random.nextDouble() * (range.max - range.min));
        }

        // 默认值
        return 0.0;
    }

    private void storeEnhancementData(ItemMeta meta, String itemQuality, List<EnhancementAttribute> enhancements) {
        // 构建JSON格式的强化数据
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("{\"quality\":\"").append(itemQuality).append("\",\"attributes\":[");

        for (int i = 0; i < enhancements.size(); i++) {
            EnhancementAttribute attr = enhancements.get(i);
            if (i > 0) dataBuilder.append(",");
            dataBuilder.append("{\"key\":\"").append(attr.attributeKey)
                    .append("\",\"quality\":\"").append(attr.quality)
                    .append("\",\"value\":").append(attr.value).append("}");
        }

        dataBuilder.append("]}");

        meta.getPersistentDataContainer().set(enhancementKey, PersistentDataType.STRING, dataBuilder.toString());
    }

    private void storeBasicEnhancementData(ItemMeta meta, String itemQuality) {
        String data = "{\"quality\":\"" + itemQuality + "\",\"attributes\":[]}";
        meta.getPersistentDataContainer().set(enhancementKey, PersistentDataType.STRING, data);
    }

    private void updateItemDisplay(ItemMeta newMeta, ItemMeta originalMeta, String itemQuality, List<EnhancementAttribute> enhancements) {
        // 获取原有Lore
        List<String> originalLore = originalMeta.hasLore() ? originalMeta.getLore() : new ArrayList<>();
        if (originalLore == null) originalLore = new ArrayList<>();

        // 创建新的Lore列表，先添加原有Lore
        List<String> newLore = new ArrayList<>(originalLore);

        // 移除之前由本插件添加的强化信息（如果有的话）
        Iterator<String> iterator = newLore.iterator();
        while (iterator.hasNext()) {
            String line = iterator.next();
            if (line.contains("品质:") || line.contains("词条:") || line.contains("未获得词条")) {
                iterator.remove();
            }
        }

        // 添加分隔线（如果原有Lore不为空）
        if (!originalLore.isEmpty() && !enhancements.isEmpty()) {
            newLore.add("§8§m----------------------");
        }

        // 添加品质信息
        String qualityColor = getQualityColor(itemQuality);
        newLore.add(qualityColor + "品质: " + getQualityDisplayName(itemQuality));

        // 添加词条信息
        for (EnhancementAttribute attr : enhancements) {
            ConfigManager.AttributeConfig config = configManager.getAttributeConfig(attr.attributeKey);
            if (config != null) {
                String attrColor = getQualityColor(attr.quality);
                String valueDisplay = formatAttributeValue(config, attr.value);

                newLore.add(attrColor + config.name + ": +" + valueDisplay + " (" + getQualityDisplayName(attr.quality) + ")");
            }
        }

        newMeta.setLore(newLore);
    }

    private String formatAttributeValue(ConfigManager.AttributeConfig config, double value) {
        if ("ADD_NUMBER".equals(config.operation)) {
            return String.format("%.1f", value);
        } else {
            return String.format("%.1f%%", value * 100);
        }
    }

    private String getQualityColor(String quality) {
        switch (quality) {
            case "BROKEN": return "§8";    // 深灰
            case "COMMON": return "§f";   // 白色
            case "UNCOMMON": return "§a"; // 绿色
            case "EPIC": return "§5";      // 紫色
            case "LEGENDARY": return "§6"; // 金色
            case "MYTHIC": return "§d";    // 粉色
            default: return "§f";
        }
    }

    private String getQualityDisplayName(String quality) {
        switch (quality) {
            case "BROKEN": return "破旧";
            case "COMMON": return "普通";
            case "UNCOMMON": return "优秀";
            case "EPIC": return "史诗";
            case "LEGENDARY": return "传说";
            case "MYTHIC": return "神话";
            default: return quality;
        }
    }

    // 原有的辅助方法
    private boolean isForgingStone(ItemStack item) {
        return stoneToQuality.containsKey(item.getType());
    }

    public boolean isEnhanceableEquipment(ItemStack item) {
        if (item == null) return false;
        return configManager.isEnhanceableEquipment(item.getType());
    }

    public boolean hasEnhancement(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        String enhancementData = meta.getPersistentDataContainer().get(enhancementKey, PersistentDataType.STRING);
        return enhancementData != null && !enhancementData.isEmpty();
    }

    private Attribute getBukkitAttribute(String configKey) {
        switch (configKey) {
            case "movement_speed": return Attribute.MOVEMENT_SPEED;
            case "armor": return Attribute.ARMOR;
            case "armor_toughness": return Attribute.ARMOR_TOUGHNESS;
            case "max_health": return Attribute.MAX_HEALTH;
            case "knockback_resistance": return Attribute.KNOCKBACK_RESISTANCE;
            case "attack_damage": return Attribute.ATTACK_DAMAGE;
            case "attack_knockback": return Attribute.ATTACK_KNOCKBACK;
            case "attack_speed": return Attribute.ATTACK_SPEED;
            case "gravity": return Attribute.GRAVITY;
            case "burning_time": return Attribute.BURNING_TIME;
            case "explosion_knockback_resistance": return Attribute.EXPLOSION_KNOCKBACK_RESISTANCE;
            case "oxygen_bonus": return Attribute.OXYGEN_BONUS;
            case "sneaking_speed": return  Attribute.SNEAKING_SPEED;
            case "step_height": return  Attribute.STEP_HEIGHT;
            case "fall_damage_multiplier": return  Attribute.FALL_DAMAGE_MULTIPLIER;
            case "water_movement_efficiency": return  Attribute.WATER_MOVEMENT_EFFICIENCY;
            case "movement_efficiency": return  Attribute.MOVEMENT_EFFICIENCY;
            case "jump_strength": return  Attribute.JUMP_STRENGTH;
            case "safe_fall_distance": return  Attribute.SAFE_FALL_DISTANCE;
            case "entity_interaction_range": return  Attribute.ENTITY_INTERACTION_RANGE;
            case "sweeping_damage_ratio": return Attribute.SWEEPING_DAMAGE_RATIO;
            case "mining_efficiency": return Attribute.MINING_EFFICIENCY;
            case "submerged_mining_speed": return Attribute.SUBMERGED_MINING_SPEED;
            case "block_interaction_range": return Attribute.BLOCK_INTERACTION_RANGE;
            default:
                getLogger().warning("不支持的属性类型: " + configKey);
                return null;
        }
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public BaseAttributeManager getBaseAttributeManager() {
        return baseAttributeManager;
    }

    // 内部类，用于存储强化属性信息
    private static class EnhancementAttribute {
        public final String attributeKey;
        public final String quality;
        public final double value;

        public EnhancementAttribute(String attributeKey, String quality, double value) {
            this.attributeKey = attributeKey;
            this.quality = quality;
            this.value = value;
        }
    }
}
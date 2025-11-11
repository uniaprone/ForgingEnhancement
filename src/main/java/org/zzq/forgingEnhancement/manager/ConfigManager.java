package org.zzq.forgingEnhancement.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class ConfigManager {
    private final Plugin plugin;
    private FileConfiguration config;
    private File configFile;

    // 配置字段
    private double rareChance;
    private List<Material> forgingStones;
    private List<String> enhanceableEquipmentSuffixes;
    private Map<String, String> messages;
    private Map<String, List<String>> attributePool;
    private Map<String, AttributeConfig> attributes;
    private Map<String, EquipmentSlotGroup> equipmentSlots;

    private final Map<Integer, String> levelQualityMap = Map.of(
            0, "BROKEN",
            1, "COMMON",
            2, "UNCOMMON",
            3, "EPIC",
            4, "LEGENDARY",
            5, "MYTHIC"
    );

    public Map<Integer, String> getLevelQualityMap() {
        return levelQualityMap;
    }

    // 内部类用于存储属性配置
    public static class AttributeConfig {
        public String name;
        public boolean rare;
        public String operation;
        public Map<String, ValueRange> values;

        public AttributeConfig() {
            this.values = new HashMap<>();
        }
    }

    // 内部类用于存储数值范围
    public static class ValueRange {
        public double min;
        public double max;

        public ValueRange(double min, double max) {
            this.min = min;
            this.max = max;
        }
    }

    public ConfigManager(Plugin plugin) {
        this.plugin = plugin;
        initializeEquipmentSlots();
        loadConfig();
    }

    private void initializeEquipmentSlots() {
        equipmentSlots = new HashMap<>();
        equipmentSlots.put("helmet", EquipmentSlotGroup.HEAD);
        equipmentSlots.put("chestplate", EquipmentSlotGroup.CHEST);
        equipmentSlots.put("leggings", EquipmentSlotGroup.LEGS);
        equipmentSlots.put("boots", EquipmentSlotGroup.FEET);
        equipmentSlots.put("sword", EquipmentSlotGroup.HAND);
        equipmentSlots.put("axe", EquipmentSlotGroup.HAND);
        equipmentSlots.put("pickaxe", EquipmentSlotGroup.HAND);
        equipmentSlots.put("shovel", EquipmentSlotGroup.HAND);
        equipmentSlots.put("hoe", EquipmentSlotGroup.HAND);
    }

    public void loadConfig() {
        try{
            configFile = new File(plugin.getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                plugin.saveResource("config.yml", false);
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            parseConfig();
            plugin.getLogger().info("配置文件加载完成，共加载 " + attributePool.size() + " 种装备的基础属性");
        }catch (Exception e){
            plugin.getLogger().warning("无法读取配置文件!");
        }
    }

    private void parseConfig() {
        // 读取稀有词条概率
        rareChance = config.getDouble("rare-chance", 0.2);

        // 读取锻造石材料列表
        forgingStones = new ArrayList<>();
        List<String> stoneStrings = config.getStringList("forging-stones");
        for (String stone : stoneStrings) {
            try {
                Material material = Material.valueOf(stone.toUpperCase());
                forgingStones.add(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("无效的锻造石材料: " + stone);
            }
        }

        // 读取可强化装备后缀
        enhanceableEquipmentSuffixes = config.getStringList("enhanceable-equipment-suffixes");

        // 读取消息格式
        messages = new HashMap<>();
        if (config.contains("messages")) {
            messages.put("lore-format", config.getString("messages.lore-format", "§a%s: +%.1f%%"));
            messages.put("display-format", config.getString("messages.display-format", "当前物品的%s: +%.1f%%"));
        }

        // 读取属性池
        attributePool = new HashMap<>();
        if (config.contains("attribute_pool")) {
            for (String equipmentType : config.getConfigurationSection("attribute_pool").getKeys(false)) {
                List<String> attributes = config.getStringList("attribute_pool." + equipmentType);
                attributePool.put(equipmentType, attributes);
            }
        }

        // 读取属性配置
        attributes = new HashMap<>();
        if (config.contains("attributes")) {
            for (String attributeKey : config.getConfigurationSection("attributes").getKeys(false)) {
                String path = "attributes." + attributeKey;

                AttributeConfig attributeConfig = new AttributeConfig();
                attributeConfig.name = config.getString(path + ".name", attributeKey);
                attributeConfig.rare = config.getBoolean(path + ".rare", false);
                attributeConfig.operation = config.getString(path + ".operation", "ADD_SCALAR");

                // 读取各品质的数值范围
                if (config.contains(path + ".values")) {
                    for (String quality : config.getConfigurationSection(path + ".values").getKeys(false)) {
                        double min = config.getDouble(path + ".values." + quality + ".min");
                        double max = config.getDouble(path + ".values." + quality + ".max");
                        attributeConfig.values.put(quality, new ValueRange(min, max));
                    }
                }

                attributes.put(attributeKey, attributeConfig);
            }
        }
    }

    public List<String> getEnhanceableEquipmentSuffixes() {
        return new ArrayList<>(enhanceableEquipmentSuffixes);
    }

    public List<String> getAttributesForEquipment(String equipmentType) {
        return attributePool.getOrDefault(equipmentType, new ArrayList<>());
    }

    public AttributeConfig getAttributeConfig(String attributeKey) {
        return attributes.get(attributeKey);
    }

    public Map<String, AttributeConfig> getAllAttributes() {
        return new HashMap<>(attributes);
    }

    // 工具方法：检查物品是否是可强化装备
    public boolean isEnhanceableEquipment(Material material) {
        String materialName = material.name();
        for (String suffix : enhanceableEquipmentSuffixes) {
            if (materialName.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    public boolean isEnhanceableEquipment(String materialName) {
        materialName = materialName.toUpperCase();
        for (String suffix : enhanceableEquipmentSuffixes) {
            if (suffix.endsWith(materialName)) {
                return true;
            }
        }
        return false;
    }

    // 工具方法：获取装备类型
    public String getEquipmentType(Material material) {
        String materialName = material.name();
        for (String suffix : enhanceableEquipmentSuffixes) {
            if (materialName.endsWith(suffix)) {
                // 去掉下划线并转换为小写
                return suffix.replace("_", "").toLowerCase();
            }
        }
        return null;
    }

    // 重新加载配置
    public void reloadConfig() {
        loadConfig();
    }

    public List<String> getEquipmentCommonAttributes(String type){
        List<String> equipmentCommonAttributes = new ArrayList<>(attributePool.get(type));
        Iterator<String> iterable = equipmentCommonAttributes.iterator();
        while(iterable.hasNext()){
            String currentAttribute = iterable.next();
            if(attributes.get(currentAttribute).rare){
                iterable.remove();
            }
        }
        return equipmentCommonAttributes;
    }

    public List<String> getEquipmentRareAttributes(String type){
        List<String> equipmentCommonAttributes = new ArrayList<>(attributePool.get(type));
        Iterator<String> iterable = equipmentCommonAttributes.iterator();
        while(iterable.hasNext()){
            String currentAttribute = iterable.next();
            if(!attributes.get(currentAttribute).rare){
                iterable.remove();
            }
        }
        return equipmentCommonAttributes;
    }

    public double getRareChance() {
        return rareChance;
    }

    public int getLevelByQuality(String quality){
        for (int i = 0; i < levelQualityMap.size(); i++) {
            if(levelQualityMap.get(i).equals(quality)){
                return i;
            }
        }
        return 0;
    }
}
package org.zzq.forgingEnhancement;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

/**
 * 基础属性管理器 - 专门处理装备的基础属性配置
 */
public class BaseAttributeManager {
    private final Plugin plugin;
    private FileConfiguration baseAttributeConfig;
    private File baseAttributeFile;

    // 存储基础属性配置：装备类型 -> 具体装备 -> 属性映射
    private final Map<String, Map<String, Map<String, Double>>> baseAttributes;

    public BaseAttributeManager(Plugin plugin) {
        this.plugin = plugin;
        this.baseAttributes = new HashMap<>();
        loadBaseAttributeConfig();
    }

    /**
     * 加载基础属性配置文件
     */
    public void loadBaseAttributeConfig() {
        try {
            baseAttributeFile = new File(plugin.getDataFolder(), "base_attributes.yml");

            // 如果文件不存在，从资源中保存默认配置
            if (!baseAttributeFile.exists()) {
                plugin.saveResource("base_attributes.yml", false);
                plugin.getLogger().info("基础属性配置文件已创建: " + baseAttributeFile.getAbsolutePath());
            }

            baseAttributeConfig = YamlConfiguration.loadConfiguration(baseAttributeFile);
            parseBaseAttributes();
            plugin.getLogger().info("基础属性配置加载完成，共加载 " + getTotalEquipmentCount() + " 种装备的基础属性");

        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "加载基础属性配置失败", e);
        }
    }

    /**
     * 解析基础属性配置
     */
    private void parseBaseAttributes() {
        baseAttributes.clear();

        if (!baseAttributeConfig.contains("base_attributes")) {
            plugin.getLogger().warning("基础属性配置文件中未找到 base_attributes 部分");
            return;
        }

        // 遍历所有装备类型
        for (String equipmentType : baseAttributeConfig.getConfigurationSection("base_attributes").getKeys(false)) {
            String path = "base_attributes." + equipmentType;

            if (!baseAttributeConfig.contains(path)) {
                plugin.getLogger().warning("装备类型 " + equipmentType + " 的配置为空");
                continue;
            }

            Map<String, Map<String, Double>> equipmentAttributes = new HashMap<>();

            // 遍历该类型下的所有具体装备
            for (String equipmentName : baseAttributeConfig.getConfigurationSection(path).getKeys(false)) {
                String equipmentPath = path + "." + equipmentName;
                Map<String, Double> attributes = new HashMap<>();

                // 遍历该装备的所有属性
                for (String attributeKey : baseAttributeConfig.getConfigurationSection(equipmentPath).getKeys(false)) {
                    double value = baseAttributeConfig.getDouble(equipmentPath + "." + attributeKey);
                    attributes.put(attributeKey, value);

                    plugin.getLogger().fine("加载装备基础属性: " + equipmentName + " -> " + attributeKey + ": " + value);
                }

                equipmentAttributes.put(equipmentName.toUpperCase(), attributes);
            }

            baseAttributes.put(equipmentType, equipmentAttributes);
            plugin.getLogger().info("装备类型 " + equipmentType + " 加载了 " + equipmentAttributes.size() + " 种装备的基础属性");
        }
    }

    /**
     * 获取指定装备的基础属性配置
     * @param material 装备材质
     * @return 属性名到数值的映射，如果找不到返回空映射
     */
    public Map<String, Double> getBaseAttributes(Material material) {
        String materialName = material.name().toUpperCase();

        // 确定装备类型
        String equipmentType = Util.getEquipmentType(materialName);
        if (equipmentType == null) {
            plugin.getLogger().warning("无法确定装备 " + material + " 的类型");
            return Collections.emptyMap();
        }

        // 获取该类型的所有装备配置
        Map<String, Map<String, Double>> typeAttributes = baseAttributes.get(equipmentType);
        if (typeAttributes == null) {
            plugin.getLogger().warning("装备类型 " + equipmentType + " 没有基础属性配置");
            return Collections.emptyMap();
        }

        // 查找具体装备的配置
        for (Map.Entry<String, Map<String, Double>> entry : typeAttributes.entrySet()) {
            if (materialName.contains(entry.getKey())) {
                return new HashMap<>(entry.getValue());
            }
        }

        plugin.getLogger().warning("装备 " + material + " 没有找到基础属性配置");
        return Collections.emptyMap();
    }

    /**
     * 获取所有装备类型的基础属性配置
     */
    public Map<String, Map<String, Map<String, Double>>> getAllBaseAttributes() {
        Map<String, Map<String, Map<String, Double>>> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Map<String, Double>>> entry : baseAttributes.entrySet()) {
            result.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return result;
    }

    /**
     * 获取指定装备类型的所有基础属性配置
     */
    public Map<String, Map<String, Double>> getBaseAttributesByType(String equipmentType) {
        Map<String, Map<String, Double>> typeAttributes = baseAttributes.get(equipmentType);
        return typeAttributes != null ? new HashMap<>(typeAttributes) : Collections.emptyMap();
    }

    /**
     * 检查装备是否有基础属性配置
     */
    public boolean hasBaseAttributes(Material material) {
        return !getBaseAttributes(material).isEmpty();
    }

    /**
     * 获取配置的装备总数
     */
    public int getTotalEquipmentCount() {
        int count = 0;
        for (Map<String, Map<String, Double>> typeAttributes : baseAttributes.values()) {
            count += typeAttributes.size();
        }
        return count;
    }

    /**
     * 重新加载配置
     */
    public void reloadConfig() {
        try {
            baseAttributeConfig = YamlConfiguration.loadConfiguration(baseAttributeFile);
            parseBaseAttributes();
            plugin.getLogger().info("基础属性配置已重新加载");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "重新加载基础属性配置失败", e);
        }
    }

    /**
     * 获取支持的属性类型列表
     */
    public Set<String> getSupportedAttributeTypes() {
        Set<String> attributeTypes = new HashSet<>();
        for (Map<String, Map<String, Double>> typeAttributes : baseAttributes.values()) {
            for (Map<String, Double> attributes : typeAttributes.values()) {
                attributeTypes.addAll(attributes.keySet());
            }
        }
        return attributeTypes;
    }

    /**
     * 调试方法：打印所有基础属性配置
     */
    public void debugPrintAllAttributes() {
        plugin.getLogger().info("=== 基础属性配置调试信息 ===");
        for (String equipmentType : baseAttributes.keySet()) {
            Map<String, Map<String, Double>> typeAttributes = baseAttributes.get(equipmentType);
            plugin.getLogger().info("装备类型: " + equipmentType + " (" + typeAttributes.size() + " 种装备)");

            for (String equipmentName : typeAttributes.keySet()) {
                Map<String, Double> attributes = typeAttributes.get(equipmentName);
                plugin.getLogger().info("  " + equipmentName + ": " + attributes);
            }
        }
        plugin.getLogger().info("总计: " + getTotalEquipmentCount() + " 种装备的基础属性");
    }
}
package org.zzq.forgingEnhancement.infrastructure.yaml.parser;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.valueobject.BaseAttribute;
import org.zzq.forgingEnhancement.domain.valueobject.BaseAttributeConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class YamlBaseAttributeParser {
    private Plugin plugin;
    private File file;
    private FileConfiguration fileConfiguration;

    public YamlBaseAttributeParser(Plugin plugin) {
        this.plugin = plugin;
    }

    public BaseAttributeConfig load(){
        try{
            file = new File(plugin.getDataFolder(), "base_attributes.yml");
            if(!file.exists()){
                plugin.saveResource("base_attributes.yml", false);
                file = new File(plugin.getDataFolder(), "base_attributes.yml");
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            BaseAttributeConfig forgingAttributeConfig = parse();
            plugin.getLogger().info("读取锻造属性成功!");
            return forgingAttributeConfig;
        }catch (Exception e){
            plugin.getLogger().severe("无法读取base_attributes文件! 插件将被禁用: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }
    }

    private BaseAttributeConfig parse(){
        if(!fileConfiguration.contains("base_attributes")) return null;
        Map<String, BaseAttribute> baseAttributeMap = new HashMap<>();
        for (String itemName : fileConfiguration.getConfigurationSection("base_attributes").getKeys(false)){
            String path = "base_attributes." + itemName;
            Map<String, Double> attributes = new HashMap<>();
            for (String attributeName : fileConfiguration.getConfigurationSection(path).getKeys(false)){
                String valuePath = path + "." + attributeName;
                double attributeValue = fileConfiguration.getDouble(valuePath, 0);
                attributes.put(attributeName, attributeValue);
            }
            baseAttributeMap.put(itemName, new BaseAttribute(itemName, attributes));
        }
        return new BaseAttributeConfig(baseAttributeMap);
    }
}

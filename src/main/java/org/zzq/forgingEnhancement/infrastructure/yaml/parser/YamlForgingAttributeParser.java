package org.zzq.forgingEnhancement.infrastructure.yaml.parser;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.config.ForgingAttributeConfig;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class YamlForgingAttributeParser {
    private Plugin plugin;
    private FileConfiguration fileConfiguration;
    private File file;

    public YamlForgingAttributeParser(Plugin plugin){
        this.plugin = plugin;
    }

    public Map<String, ForgingAttributeConfig> load(){
        try{
            file = new File(plugin.getDataFolder(), "forging_attribute.yml");
            if(!file.exists()){
                plugin.saveResource("forging_attribute.yml", false);
                file = new File(plugin.getDataFolder(), "forging_attribute.yml");
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            Map<String, ForgingAttributeConfig> forgingAttributeConfig = parse();
            plugin.getLogger().info("读取锻造属性成功!");
            return forgingAttributeConfig;
        }catch (Exception e){
            plugin.getLogger().severe("无法读取配置文件! 插件将被禁用: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }
    }

    public Map<String, ForgingAttributeConfig> parse() {
        Map<String, ForgingAttributeConfig> map = new HashMap<>();
        for (String key : fileConfiguration.getKeys(false)) {
            // 收集所有基础属性
            String name = fileConfiguration.getString(key + ".name", key);
            boolean rare = fileConfiguration.getBoolean(key + ".rare", false);
            String operation = fileConfiguration.getString(key + ".operation", "ADD_SCALAR");

            // 收集values
            Map<String, ForgingAttributeConfig.ValueRange> values = new HashMap<>();
            ConfigurationSection valuesSection = fileConfiguration.getConfigurationSection(key + ".values");
            if (valuesSection != null) {
                for (String quality : valuesSection.getKeys(false)) {
                    String valuePath = key + ".values." + quality;
                    double min = fileConfiguration.getDouble(valuePath + ".min");
                    double max = fileConfiguration.getDouble(valuePath + ".max");
                    values.put(quality, new ForgingAttributeConfig.ValueRange(quality, min, max));
                }
            }

            // 创建配置对象
            ForgingAttributeConfig config = new ForgingAttributeConfig(
                key, name, rare, operation, values
            );
            map.put(key, config);
        }
        return map;
    }
}

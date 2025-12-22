package org.zzq.forgingEnhancement.infrastructure.yaml.parser;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.aggregateroot.ForgingAttributePoolConfig;
import org.zzq.forgingEnhancement.domain.valueobject.EquipmentAttribute;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlForgingAttributePoolParser {
    private Plugin plugin;
    private FileConfiguration fileConfiguration;
    private File file;

    public YamlForgingAttributePoolParser(Plugin plugin){
        this.plugin = plugin;
    }

    public ForgingAttributePoolConfig load(){
        try{
            file = new File(plugin.getDataFolder(), "forging_attribute_pool.yml");
            if(!file.exists()){
                plugin.saveResource("forging_attribute_pool.yml", false);
                file = new File(plugin.getDataFolder(), "forging_attribute_pool.yml");
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            ForgingAttributePoolConfig map = parse();
            plugin.getLogger().info("读取锻造属性池成功!");
            return map;
        }catch (Exception e){
            plugin.getLogger().severe("无法读取锻造属性池! 插件将被禁用: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }
    }

    public ForgingAttributePoolConfig parse(){
        if(!fileConfiguration.contains("attribute_pool")) return null;

        List<String> forgeableEquipments = new ArrayList<>();
        Map<String, EquipmentAttribute> map = new HashMap<>();

        forgeableEquipments = fileConfiguration.getStringList("forgeable_equipment");

        for (String key : fileConfiguration.getConfigurationSection("attribute_pool").getKeys(false)){
            List<String> attributes = fileConfiguration.getStringList("attribute_pool." + key);
            map.put(key, new EquipmentAttribute(key, attributes));
        }
        return new ForgingAttributePoolConfig(forgeableEquipments, map);
    }
}

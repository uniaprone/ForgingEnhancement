package org.zzq.forgingEnhancement.infrastructure.yaml.parser;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.zzq.forgingEnhancement.domain.entity.ForgingConfig;

import java.io.File;

public class YamlConfigParser {
    private Plugin plugin;
    private File file;
    private FileConfiguration fileConfiguration;

    public YamlConfigParser(Plugin plugin){
        this.plugin = plugin;
    }

    public ForgingConfig load(){
        try{
            file = new File(plugin.getDataFolder(), "config.yml");
            if(!file.exists()){
                plugin.saveResource("config.yml", false);
                file = new File(plugin.getDataFolder(), "config.yml");
            }
            fileConfiguration = YamlConfiguration.loadConfiguration(file);
            ForgingConfig forgingConfig = parser();
            plugin.getLogger().info("读取锻造配置成功!");
            return forgingConfig;
        }catch (Exception e){
            plugin.getLogger().severe("无法读取锻造配置文件! 插件将被禁用: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return null;
        }
    }

    public ForgingConfig parser(){
        String env = "DEBUG";
        if(fileConfiguration.contains("env")){
           env = fileConfiguration.getString("env", "DEBUG");
        }
        return new ForgingConfig(env);
    }

    public ForgingConfig reload(){
        return load();
    }
}
